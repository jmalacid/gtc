package svo.gtc.ingestion;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.Header;
import svo.gtc.db.DriverBD;
import svo.gtc.db.prodred.PredProdAccess;
import svo.gtc.db.prodred.PredProdDb;
import svo.gtc.db.prodred.ProdRedAccess;
import svo.gtc.db.prodred.ProdRedDb;
import svo.varios.utiles.seguridad.Encriptado;

public class IngestionRedHerve {

	public static void main(String[] args) {
		
		//Obtenemos el path
		String path = "/pcdisk/oort5/jmalacid/gtc_prod/images";
		/*String path = null;
		if(args.length>0 && args[0] !=null && args[0].trim().length()>0){
			path=args[0].trim();
		}*/
		
		// Conexi�n con la base de datos
		//-->Habría que cambiarla a eric para hacer la ingestión directamente		
		DriverBD driver = new  DriverBD();
		Connection con = null;
		try {
			con = driver.bdConexion();	
			ProdRedAccess pr = new ProdRedAccess(con);
			
			//Listamos los ficheros
			File pathFil = new File(path);
			File[] fichs = pathFil.listFiles();
			
			for(int i = 0; i<fichs.length; i++){
				
				try{
					if(pr.countByMd5(Encriptado.md5(fichs[i]))>0){
						
						System.out.println(fichs[i].getName()+": ERROR - Fichero ya en la BD");
						
					}else{
						//Obtenemos Ra y Dec
						
						Fits fEntrada = new Fits(fichs[i],true);
		
						Double ra= null;
						Double de= null;
					   // BasicHDU hdu = null;
					    
					    BasicHDU[] hdus =  new BasicHDU[0];
						
						hdus = fEntrada.read();
						BasicHDU h = hdus[1];
							if( h != null) {
								Header hdr = h.getHeader();		
								try{
									ra= new Double(hdr.findCard("RADEG").getValue());
									de= new Double(hdr.findCard("DECDEG").getValue());
								}catch(Exception e){
									ra = 0.0;
									de = 0.0;
								}
								
							}
							
					  
					  //Ingestamos en la tabla prodreducido y pred_prod
						ProdRedDb pred = new ProdRedDb();
						
						int predId = pr.getNewPredId("jmalacid", 2);
		
						pred.setPredId(predId);
						pred.setUsrId("jmalacid");
						pred.setColId(2);
						pred.setBpathId(2);
						pred.setPredPath("/images/");
						pred.setPredFilenameOrig(fichs[i].getName());
						pred.setPredFilesize(new Long(fichs[i].length()));
						pred.setPredMd5sum(Encriptado.md5(fichs[i]));
						pred.setPredRa(ra);
						pred.setPredDe(de);
						pred.setPredFilename(fichs[i].getName());
						pred.setPredType("HERVE");
						
						pr.insProdDatos(pred);
						
						
						/// Insertamos en la tabla PRed_Prod
						PredProdAccess ppa = new PredProdAccess(con);
						PredProdDb predProdDb = ppa.selectProd(fichs[i].getName());
						if(predProdDb.getProdId()!=null){
							predProdDb.setPredId(pred.getPredId());
		
							ppa.insPredProd(predProdDb);
							con.commit();
							System.out.println(fichs[i].getName()+" OK");
						}else{
							System.out.println(fichs[i].getName()+" ERROR - No tiene raw relacionado");
						}
						
					}
					
				}catch(Exception e){
					System.out.println(fichs[i].getName()+" ERROR - "+e.getMessage());
					e.printStackTrace();
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
