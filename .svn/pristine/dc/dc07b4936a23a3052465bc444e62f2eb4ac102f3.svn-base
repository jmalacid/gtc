package svo.gtc.utiles;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import svo.gtc.db.DriverBD;
import svo.gtc.db.priv.DBPrivate;
import svo.gtc.db.prodat.WarningDb;
import svo.gtc.db.proderr.ErrorDb;
import svo.gtc.ingestion.ConfigException;
import svo.gtc.proddat.GtcFileException;
import svo.gtc.proddat.ProdDatos;
import svo.gtc.proddat.ProdDatosCanaricam;
import svo.gtc.proddat.ProdDatosOsiris;

public class BuscaErrores {
	
	public static void main(String args[]) throws IOException, ConfigException, GtcFileException, SQLException, FitsException{

		
		
		//Método para comprobar keywords de ficheros por un tipo de error
		DriverBD drive = new  DriverBD();
		Connection con = drive.bdConexion();

		DBPrivate union = new DBPrivate(con);

		String[] progs = union.fileError("E-OSIRIS-0061");
		
		for(int i=0; i<progs.length;i++){
			
			/*//Comprobamos el nombre
			if(progs[i].contains("object") && (progs[i].indexOf("LongSlitSpectroscopy")>0 || progs[i].indexOf("BroadBand")>0)){
				System.out.println(progs[i]);
			}else{
				//System.out.println(progs[i]);
			}*/
				
			
			try{
			File fits= new File("/pcdisk/oort6/raul/gtc/data"+progs[i]);
			
			boolean compressed = false;
			
			if(fits.getName().toUpperCase().endsWith(".GZ")){
				compressed = true;
			}
		    
			
			
		    
		    
		    
		    


			String msgProd = "";
			Integer bpathId = 9000;
			boolean autocommit = true;
				
			ProdDatos prod = new ProdDatos(fits);

			//Solo si no se tratan de datos reducidos
			//if(!prod.getFile().toString().contains("REDUCEDFROM")){
				//Creamos un proddatos según el instrumento
				if(prod.getInstrument()!=null && prod.getInstrument().equalsIgnoreCase("OSIRIS")){
					prod = new ProdDatosOsiris(prod, con);
				}else if(prod.getInstrument()!=null && prod.getInstrument().equalsIgnoreCase("CANARICAM")){
					prod = new ProdDatosCanaricam(prod, con);
				}

				try{
						
					

					// Si no est� introducido se testea, se introduce y se elimina de errores
					// si estuviese en esa tabla.
					try{
						//Pasamos los tests especificos según el instrumento								
						prod.test(con);
					}catch (GtcFileException eProd){
						/// Vemos si hay errores o son todo warnings.
						String err = eProd.getMessage();
						if(err.matches(".*E-.*[0-9]{4}:.*")){
							throw eProd;
						}
						// Si llegamos aquí es que solo hay warnings.
						msgProd = eProd.getMessage();
						msgProd=msgProd.substring(0, msgProd.lastIndexOf(";"));

					}


					

					/////////////////////////////////////////////////////
					// TEST OK:
					/////////////////////////////////////////////////////
					//boolean isExtCal = false;
					try {
						con.setAutoCommit(false);

						// Fijamos los warnings del producto de datos:
						WarningDb[] warnings = WarningDb.getWarnings(msgProd);

						//Insertamos el producto en la base de datos
						////////// INSERCION DEL PRODUCTO Y ELIMINACION DE ERRORES //////////
						prod.insertaBD(con,bpathId,warnings);
						/////////////////////////////////////////////////////////////////////

						con.commit();
						con.setAutoCommit(autocommit);
					} catch (GtcFileException e1){
						
						try {
							con.rollback();
							con.setAutoCommit(autocommit);
						} catch (SQLException e2) {
							e1.printStackTrace();
						}
						throw e1;
					} catch (Exception e) {
						e.printStackTrace();
						try {
							con.rollback();
							con.setAutoCommit(autocommit);
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
						System.out.println("\t\t"+prod.getFile().getName()+"   :   ERROR CRITICO, SALIENDO.");
						System.exit(-1);
					}
					if(msgProd.length()>0) msgProd=" : "+msgProd; 
					System.out.println("\t\t"+prod.getFile().getName()+"   :   OK"+msgProd);
				}catch(GtcFileException e){
					/////////////////////////////////////////////////////
					// TEST FAILED:
					/////////////////////////////////////////////////////
					

					msgProd = e.getMessage();
					msgProd=msgProd.substring(0, msgProd.lastIndexOf(";"));

					System.out.println("\t\t"+prod.getFile().getName()+"   :   "+msgProd);
					

					// Fijamos los errores del producto de datos:
					ErrorDb[] errors = ErrorDb.getErrors(msgProd);

					try {

						////////////  SI HAY ERRORES SE INSERTAN /////////////
						con.setAutoCommit(false);

						prod.insertaErrorBD(con, bpathId, errors);

						con.commit();
						con.setAutoCommit(autocommit);
					} catch (SQLException e1) {
						e1.printStackTrace();
						try {
							con.rollback();
							con.setAutoCommit(autocommit);
						} catch (SQLException e2) {
							e2.printStackTrace();
							
							System.exit(-1);
						}
						e1.printStackTrace();
						
						System.exit(-1);
					}


				}catch(Exception e){
					System.out.println("\t\t"+prod.getFile().getName()+"   :   "+e.getMessage());
					e.printStackTrace();
					
				}
			//} Los guardamos todos, tengan o no el "Reducedfrom"
		

	
		    
		    
				
				
		    
		    
		    
		    
		    
		    
		    
		    
			}catch (Exception e){
				
				
				
			}
		}
		
		
		
		con.close();
		
		
	


	



	}
}
