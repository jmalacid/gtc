package svo.gtc.ingestion;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import svo.gtc.db.DriverBD;
import svo.gtc.db.basepath.BasepathAccess;
import svo.gtc.db.ingest.IngestDB;
import svo.gtc.db.prodred.PredProdAccess;
import svo.gtc.db.prodred.PredProdDb;
import svo.gtc.db.prodred.ProdRedDb;
import svo.gtc.db.usuario.UserPI;
import svo.gtc.priv.objetos.Prod_red;
import svo.gtc.utiles.Mail;
import svo.varios.utiles.seguridad.Encriptado;

public class IngestionRedHorus {

	
	public static void main(String[] args) throws Exception {
		
		if(args[0]!=null){
				
		String[] info = args[0].split(";");

		//Ponemos manualmente los programas
//		String  linea = "GTC64-20A;0001,0003";
//		String[] info = linea.split(";");
				
			//String prog = info[0];//"GTC122-19B"
String prog = "GTC100-19B";			
			//Obtenemos los obs
			String[] obs= info[1].split(",");
			
			// Conexi�n con la base de datos
			DriverBD driver = new  DriverBD();
			Connection con = null;
			try {
			
			for(int w=0; w<obs.length; w++){
			
				//String obl = obs[w];//"0002"
String obl = "0002";
				//System.out.println("/export/pcdisk6/raul/HORuS/"+prog+"/OB"+obl+"/chain/");
				
				System.out.println("OB: "+obs[w]);
				
				String path = "/pcdisk/oort6/raul/HORuS/"+prog+"/OB"+obl+"/chain/"; //El path donde están los ficheros que vamos a reducir (Habrá 3)
//				String path = "/export/pcdisk6/raul/gtc/dataNew/2000X/"+prog+"/OB"+obl+"/chain/"; //El path donde están los ficheros que vamos a reducir
			
			
				con = driver.bdConexion();	
				IngestDB pr = new IngestDB(con);
				
				File fich = new File(path);
				File[] fichsRed = fich.listFiles();
				
				ArrayList<Prod_red> lista = new ArrayList<Prod_red>();
				
				for(int i=0; i<fichsRed.length; i++){
					if((fichsRed[i].getName().endsWith(".fits.gz") || fichsRed[i].getName().endsWith(".png")) && !(fichsRed[i].getName().contains("ap.fits") || fichsRed[i].getName().contains("ap1.fits"))){
					
					//System.out.println("NOMBRE: "+fichsRed[i].getName());
					
					//Comprobar si existe				
					String md5 = Encriptado.md5(fichsRed[i]);
					int countMd5 = pr.countByMd5(md5);
					
					if(countMd5==0){
						//Obtener info del raw
						System.out.println(fichsRed[i].getName());
						Prod_red raw = pr.getProd(fichsRed[i].getName().substring(1).replaceAll("png", "fits.gz"), prog, obl);
						
						Double ra = null;
						Double dec = null;
						String type = null;
						if(raw!=null){
							raw.setFilename(fichsRed[i].getName());
							//System.out.println("no es nulo");
							ra = raw.getProd_ra();
							dec = raw.getProd_dec();
							if(raw.getSubm_id().equalsIgnoreCase("SPE")){
								type = "HOR_S";
							}else{
								type = "HOR_C";
							}
						}else{
							if(!fichsRed[i].getName().contains("Spectroscopy")){
							
								raw = new Prod_red(fichsRed[i].getName(), prog, obl);
								raw.setSubm_id("CAL");
								//System.out.println("NULL");
								ra = 0.0;
								dec = 0.0;
								type = "HOR_C";
							}else{
								System.out.println(fichsRed[i].getName()+": Este no cumple");
							}
							
						}
						
						//Creamos el objeto prodRed con la información si hay raw
						
						if(raw!=null){
							ProdRedDb pred = new ProdRedDb();
							
							int predId = pr.getNewPredId();
			
							raw.setPred_id(predId);
							
							pred.setPredId(predId);
							pred.setUsrId("jmalacid");
							pred.setColId(7);
							pred.setBpathId(1);
							pred.setPredPath(path.replaceAll("/export/pcdisk6/raul/gtc/dataNew", ""));//No es el path
							pred.setPredFilenameOrig(fichsRed[i].getName());
							pred.setPredFilesize(new Long(fichsRed[i].length()));
							pred.setPredMd5sum(md5);
							pred.setPredRa(ra);
							pred.setPredDe(dec);
							pred.setPredFilename(fichsRed[i].getName());
							pred.setPredType(type);
							
							pr.insProdDatos(pred);
							//System.out.println("Prod reducido ingestado: "+predId+", fich: "+fichsRed[i]);
							
							lista.add(raw);
						}
						
					}else{
						System.out.println("Existe");
					}
					
				}
				}
					for(int j=0; j<lista.size();j++){
						System.out.println(lista.get(j).getFilename()+", "+lista.get(j).getSubm_id());
						if(lista.get(j).getFilename().startsWith("n") && lista.get(j).getSubm_id().equalsIgnoreCase("SPE") && lista.get(j).getFilename().contains(".fits")){
							//System.out.println("Es N y SPE");
							//Es de ciencia, relacionamos con el resto de ficheros reducido
							/// Insertamos en la tabla PRed_Prod
							PredProdAccess ppa = new PredProdAccess(con);			
							for(int k=0; k<lista.size();k++){
								
								if(lista.get(k).getSubm_id().equalsIgnoreCase("SPE") && lista.get(k).getFilename().substring(1).equalsIgnoreCase(lista.get(j).getFilename().substring(1))){
									System.out.println(" - SPE: "+lista.get(k).getFilename());
									PredProdDb cal = new PredProdDb(lista.get(k).getPred_id(),lista.get(j).getProg_id(),lista.get(j).getObl_id(),lista.get(j).getProd_id());
									ppa.insPredProd(cal);
								}else if(lista.get(k).getSubm_id().equalsIgnoreCase("SPE") && lista.get(k).getFilename().substring(1).replaceAll("png", ".fits.gz").equalsIgnoreCase(lista.get(j).getFilename().substring(1))){
									System.out.println(" - SPE PNG: "+lista.get(k).getFilename());
									PredProdDb cal = new PredProdDb(lista.get(k).getPred_id(),lista.get(j).getProg_id(),lista.get(j).getObl_id(),lista.get(j).getProd_id());
									ppa.insPredProd(cal);
								}else if(!lista.get(k).getSubm_id().equalsIgnoreCase("SPE")){
									System.out.println(" - CAL: "+lista.get(k).getFilename());
									PredProdDb cal = new PredProdDb(lista.get(k).getPred_id(),lista.get(j).getProg_id(),lista.get(j).getObl_id(),lista.get(j).getProd_id());
									ppa.insPredProd(cal);
								}
									
							}
							con.commit();
						}
							
					}
						
		
			}
			
			//Mandamos correo si todo ha ido bien
			BasepathAccess  bpathAccess 	= new BasepathAccess(con);;	
			UserPI pi = bpathAccess.selPI(prog);
			
			String asunto="GTC Archive: new Reduced and Private Data";
			String mail=pi.getUsr_email();
			String cuerpo = "Dear colleague,\n\nNew observing blocks ("+info[1]+")  of your program "+info[0]+" and their corresponding reduced data are now available in your private area of the GTC Archive"
			+ "(https://gtc.sdc.cab.inta-csic.es/gtc/index.jsp).Please note that there might be some delay between the date the observations were "
			+ "made and the date they are ingested into the GTC archive.\n\nYou can access them using your user_id ("+pi.getUsr_id()+") and password. "
					+ "These datasets will become public one year after being observed.Please note that the user_id/passwd to access "
					+ "the GTC archive are totally independent of those used in the Phase-II.\n\n If you have any problem, do not hesitate to contact us.\n\nThe GTC archive staff.";
				
			
//			Mail mail_user = new Mail(cuerpo,asunto, mail);
			//Mail mail_user = new Mail(cuerpo,asunto, "jmalacid@cab.inta-csic.es");
			
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
			
		}else{
			System.out.println("No has pasado los suficientes argumentos");
		}
	}
}
