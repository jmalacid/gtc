/*
 * @(#)Config.java    19/01/2012
 *
 *
 * Raúl Gutiérrez Sánchez. (raul@laeff.inta.es)	
 * LAEFF: 	Laboratorio de Astrofísica Espacial y Física Fundamental.
 *        	INTA
 *			Villafranca del Castillo Satellite Tracking Station
 *			Villafranca del Castillo, E-28691 Villanueva de la Cañada
 *			Madrid - España
 *			
 * Phone:  34 91-8131260
 * Fax..:  34 91-8131160   
 * -----------------------------------------------------------------------
 * 
 *	ESTE  PROGRAMA  ES  DE  USO EXCLUSIVO.  ES  PROPIEDAD  DE  SU AUTOR  Raúl 
 *  Gutiérrez Sánchez  Y SE PRESENTA "COMO ESTA" SIN NINGUN TIPO DE GARANTIAS
 *  DENTRO Y FUERA  DE LA FINALIDAD PARA  LA QUE EL  AUTOR LO  HIZO. EL AUTOR 
 *  PERMITE  SU USO SIEMPRE QUE  SE LE REFERENCIE. SE PERMITE LA MODIFICACION
 *  DE SU  FUENTE SIEMPRE QUE SE  HAGA REFERENCIA A SU AUTORIA Y JUNTO A ELLA
 *  SE  ADJUNTE LA IDENTIFICACION DEL  AUTOR DE LAS NUEVAS MODIFICACIONES. EL
 *  AUTOR  NUNCA SERA RESPONSABLE DEL USO DE ESTE  PROGRAMA NI DE LOS EFECTOS
 *  QUE ESTE PROGRAMA PUDIEDA CAUSAR EN DETERIOROS, ALTERACIONES Y/O PERDIDAS 
 *  DE INFORMACION.
 *	
 */

package svo.gtc.ingestion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

public class Config {

	private File pathBase;
	private File pathData;
	private File pathLogs;
	
	 
	public Config(){}
	
	public File getPathBase() {
		return pathBase;
	}
	public void setPathBase(File pathBase) throws ConfigException {
		testPath(pathBase);
		this.pathBase = pathBase;
	}
	public File getPathData() {
		return pathData;
	}
	public void setPathData(File pathData) throws ConfigException {
		testPath(pathData);
		this.pathData = pathData;
	}
	public File getPathLogs() {
		return pathLogs;
	}
	public void setPathLogs(File pathLogs) throws ConfigException {
		testPath(pathLogs);
		this.pathLogs = pathLogs;
	}
	
	
	/**
	 * Comprobación de valided del directorio
	 * @param path
	 * @throws ConfigException
	 */
	private void testPath(File path) throws ConfigException{
		if(!path.canRead()){
			throw new ConfigException("Unable to read directory: "+path);
		}
	}
	
	/**
	 * Lee la configuración del fichero de configuración.
	 */
	public void readFromDisk(){
		File fichConf = new File(System.getProperty("user.home")+"/.svo/gtc/ingestion/config");
		
		if(fichConf.canRead()){
			try {
				BufferedReader reader = new BufferedReader(new FileReader(fichConf));
				
				String linea = "";
				HashMap<String,String> propiedades = new HashMap<String,String>();
				
				while((linea=reader.readLine())!=null){
					if(linea.trim().startsWith("#")) continue;
					
					linea = linea.trim();
					
					if(linea.matches("^([^\\s]+)\\s+([^\\s]+)$")){
						String propiedad= linea.replaceAll("^([^\\s]+)\\s+([^\\s]+)$","$1");
						String valor    = linea.replaceAll("^([^\\s]+)\\s+([^\\s]+)$","$2");
						propiedades.put(propiedad, valor);
					}
				}
				
				if(propiedades.containsKey("PathBase")) this.pathBase = new File(propiedades.get("PathBase"));
				if(propiedades.containsKey("PathData")) this.pathData = new File(propiedades.get("PathData"));
				if(propiedades.containsKey("PathLogs")) this.pathLogs = new File(propiedades.get("PathLogs"));
				
				reader.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			System.out.println(fichConf.getAbsolutePath());
		}
	}
	
	
	/**
	 * Salva la configuración en disco.
	 */
	public void saveOnDisk(){
		File confDir = new File(System.getProperty("user.home")+"/.svo/gtc/ingestion/");
		boolean dirCreado = true;
		
		if(!confDir.canRead()){
			dirCreado=confDir.mkdirs();
		}
			
		File fichConf = new File(confDir.getAbsolutePath()+"/config");
		if( dirCreado ){
			if(!fichConf.canRead())
				try {
					fichConf.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fichConf));
			
			writer.write("# CONFIGURATION FILE FOR GTC INGESTION \n");
			writer.write("# "+Calendar.getInstance().getTime().toString()+" \n\n");
			
			if(this.getPathBase()!=null) writer.write("PathBase "+this.getPathBase() +" \n");
			if(this.getPathData()!=null) writer.write("PathData "+this.getPathData() +" \n");
			if(this.getPathLogs()!=null) writer.write("PathLogs "+this.getPathLogs() +" \n");
			
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	
	
}
