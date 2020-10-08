package svo.gtc.utiles;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.Header;
import nom.tam.fits.PaddingException;
import svo.gtc.db.DriverBD;
import svo.gtc.db.prodred.PredProdAccess;
import svo.gtc.db.prodred.PredProdDb;
import svo.gtc.db.prodred.ProdRedAccess;
import svo.gtc.db.prodred.ProdRedDb;
import svo.varios.utiles.seguridad.Encriptado;

public class UploadRedMegara {

	public static void main(String[] args) throws Exception {
		
		//Habría que copiar los ficheros en el correspondiente path de cornell PROG/OB/DIR_RED
		//POR CADA CASO HABRÍA QUE HACES ESTO:
		
		//Completar esta info para cada caso
		String prog ="GTC5-18BFLO";
		String obl = "OB0003";
		String dir_red ="GTC5-18BFLO_OB0003_HR-I";
		
		String path = "/pcdisk/oort/jmalacid/data/gtc/MEGARA/Megara_reducidos/"+prog+"/"+obl+"/"+dir_red+"/"; //El path donde están los ficheros que vamos a reducir (Habrá 3)
				
		String[] fichsRed = {"GTC5-18BFLO_OB0003_HR-I_final_rss.fits","GTC5-18BFLO_OB0003_HR-I_reduced_rss.fits","GTC5-18BFLO_OB0003_HR-I_sky_rss.fits", "readme_GTC5-18BFLO_OB0003_HR-I.txt"}; 
		String[] fichsRaw = {"0001855302-20181129-MEGARA-MegaraLcbImage.fits.gz","0001855303-20181129-MEGARA-MegaraLcbImage.fits.gz","0001855304-20181129-MEGARA-MegaraLcbImage.fits.gz"};
		
		
		// Conexi�n con la base de datos
		//-->Habría que cambiarla a eric para hacer la ingestión directamente		
		DriverBD driver = new  DriverBD();
		Connection con = null;
		try {
			con = driver.bdConexion();	
			ProdRedAccess pr = new ProdRedAccess(con);
		
		for(int i=0; i<fichsRed.length; i++){
			File red= new File(path+fichsRed[i]);
			System.out.println("path: "+path+fichsRed[i]);
			
			Double ra= null;
			Double de= null;
			if(fichsRed[i].contains(".txt")){
				ra =0.0;
				de =0.0;
			}else{
				//Obtenemos Ra y Dec
				Fits fEntrada = new Fits(red, false);	
				
				try{
					fEntrada.read();	    
			    }catch(PaddingException e){
			    	fEntrada.addHDU(e.getTruncatedHDU());
			    }
			    BasicHDU hdu = fEntrada.getHDU(0);   		    
			    Header header=hdu.getHeader();
				
				ra= new Double(header.findCard("RADEG").getValue());
				de= new Double(header.findCard("DECDEG").getValue());
			}
				
		  
		  //Ingestamos en la tabla prodreducido y pred_prod
			ProdRedDb pred = new ProdRedDb();
			
			int predId = pr.getNewPredId("jmalacid", 6);

			pred.setPredId(predId);
			pred.setUsrId("jmalacid");
			pred.setColId(6);
			pred.setBpathId(2);
			pred.setPredPath("/megara/"+prog+"/"+obl+"/"+dir_red+"/");
			pred.setPredFilenameOrig(red.getName());
			pred.setPredFilesize(new Long(red.length()));
			pred.setPredMd5sum(Encriptado.md5(red));
			pred.setPredRa(ra);
			pred.setPredDe(de);
			pred.setPredFilename(red.getName());
			pred.setPredType("MEG");
			
			pr.insProdDatos(pred);
			//System.out.println("Prod reducido ingestado: "+predId+", fich: "+fichsRed[i]);
			
			for(int j=0; j<fichsRaw.length;j++){
				/// Insertamos en la tabla PRed_Prod
				PredProdAccess ppa = new PredProdAccess(con);
				PredProdDb predProdDb = ppa.selectProd(fichsRaw[j]);
				if(predProdDb.getProdId()!=null){
					predProdDb.setPredId(pred.getPredId());

					ppa.insPredProd(predProdDb);
					con.commit();
				}
			
				//System.out.println("Prod-pred ingestado: "+predId+", fich: "+fichsRaw[j]);
				
			}
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}finally{
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
