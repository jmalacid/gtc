/*
 * @(#)QcFile.java    Mar 7, 2011
 *
 *
 * Ra�l Guti�rrez S�nchez. (raul@laeff.inta.es)	
 * LAEFF: 	Laboratorio de Astrof�sica Espacial y F�sica Fundamental.
 *        	INTA
 *			Villafranca del Castillo Satellite Tracking Station
 *			Villafranca del Castillo, E-28691 Villanueva de la Ca�ada
 *			Madrid - Espa�a
 *			
 * Phone:  34 91-8131260
 * Fax..:  34 91-8131160   
 * -----------------------------------------------------------------------
 * 
 *	ESTE  PROGRAMA  ES  DE  USO EXCLUSIVO.  ES  PROPIEDAD  DE  SU AUTOR  Ra�l 
 *  Guti�rrez S�nchez  Y SE PRESENTA "COMO ESTA" SIN NINGUN TIPO DE GARANTIAS
 *  DENTRO Y FUERA  DE LA FINALIDAD PARA  LA QUE EL  AUTOR LO  HIZO. EL AUTOR 
 *  PERMITE  SU USO SIEMPRE QUE  SE LE REFERENCIE. SE PERMITE LA MODIFICACION
 *  DE SU  FUENTE SIEMPRE QUE SE  HAGA REFERENCIA A SU AUTORIA Y JUNTO A ELLA
 *  SE  ADJUNTE LA IDENTIFICACION DEL  AUTOR DE LAS NUEVAS MODIFICACIONES. EL
 *  AUTOR  NUNCA SERA RESPONSABLE DEL USO DE ESTE  PROGRAMA NI DE LOS EFECTOS
 *  QUE ESTE PROGRAMA PUDIEDA CAUSAR EN DETERIOROS, ALTERACIONES Y/O PERDIDAS 
 *  DE INFORMACION.
 *	
 */

package svo.gtc.proddat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import svo.gtc.db.basepath.BasepathAccess;
import svo.gtc.db.basepath.BasepathDb;
import svo.gtc.db.logfile.LogFileAccess;
import svo.gtc.db.logfile.LogFileDb;
import svo.gtc.db.obsblock.ObsBlockAccess;
import svo.gtc.db.obsblock.ObsBlockDb;
import svo.gtc.db.programa.ProgramaAccess;
import svo.gtc.db.programa.ProgramaDb;
import svo.gtc.ingestion.IngestionException;


public class QcFile {
	private File	file		= null;
	private ObsBlock oBlock 	= null;
	
	public QcFile(ObsBlock oblock, File qcFile){
		this.file=qcFile;
		this.oBlock=oblock;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file; 
	}
	
	

	public ObsBlock getOblock() {
		return oBlock;
	}

	public void setOblock(ObsBlock oBlock) {
		this.oBlock = oBlock;
	}

	/**
	 * Lee el fichero de control de calidad para obtener todos los nombres de fichero supuestamente
	 * generados en el observing block.
	 * @return
	 * @throws IOException
	 */
	public String[] getProductFileNames(){
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(this.file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new String[0];
		}
		String linea = "";
		Vector<String> fileNames = new Vector<String>(); 
		//String regExpFile = "^([0-9]{10}-[0-9]{8}-[^-]*-[^-]*\\.fits).*$";
		String regExpFile = "^([0-9]{10}-[0-9]{8}-[^\\.]*\\.fits).*$";
		
		// Recorremos el fichero linea por línea hasta el final del fichero
		// o hasta que encontremos el comienzo del bloque de calibraciones externas.
		
		try {
			while((linea=br.readLine())!=null){
				if(linea.toUpperCase().contains("EXTERNAL CALIBRATION")){
					break;
				}
				
				if(linea.matches(regExpFile)){
					String filename = linea.replaceAll(regExpFile, "$1");
					fileNames.add(filename);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new String[0];
		}
		
		return (String[])fileNames.toArray(new String[0]);
		
	}
	
	/**
	 * Inserta un QcFile en la tabla LogFile
	 * @param con
	 * @param bpathId
	 * @throws SQLException
	 * @throws IngestionException
	 */
	public void insertaBD(Connection con, Integer bpathId) throws SQLException{

		// INSERTAMOS LA INFORMACIÓN EN LA BASE DE DATOS
		boolean autocommit = con.getAutoCommit();
		try{
			con.setAutoCommit(false);
			ProgramaAccess programaAccess = new ProgramaAccess(con);
			ObsBlockAccess oblAccess = new ObsBlockAccess(con);
			BasepathAccess basepathAccess = new BasepathAccess(con);
			LogFileAccess logFileAccess = new LogFileAccess(con);

			
			ProgramaDb 		programaDb 	= null;
			ObsBlockDb 		oblDb		= null;
			BasepathDb 		basepathDb	= null;
			
			// Comprobamos la existencia de Programa y si no existe lo añadimos
			programaDb = programaAccess.selectById(this.getOblock().getProgram().getProgId().toUpperCase().replaceAll("\\s", ""));
			
			if(programaDb==null){
				programaDb = new ProgramaDb(this.getOblock().getProgram());
				programaAccess.insert(programaDb);
			}

			// Comprobamos la existencia de Oblock y si no existe lo añadimos
			oblDb = oblAccess.selectById(this.getOblock().getProgram().getProgId().toUpperCase().replaceAll("\\s", ""),
										 this.getOblock().getOblId().toUpperCase().replaceAll("\\s", ""));
			
			if(oblDb==null){
				oblDb = new ObsBlockDb(this.getOblock());
				oblAccess.insert(oblDb);
			}
			
			// Determinamos el path del fichero
			basepathDb = basepathAccess.selectBpathById(9000);//lo dejamos en 9000 para obtener el verdadero path
			String path = this.getFile().getAbsolutePath().replaceAll(this.getFile().getName(), "").replaceAll(basepathDb.getBpathPath(), "");
			
			// Insertamos el fichero de log de datos
			LogFileDb logFileDb = new LogFileDb();
			logFileDb.setProgId(this.getOblock().getProgram().getProgId().toUpperCase().replaceAll("\\s", ""));
			logFileDb.setOblId(this.getOblock().getOblId().toUpperCase().replaceAll("\\s", ""));
			logFileDb.setLogFilename(this.file.getName());
			logFileDb.setLogPath(path);
			logFileDb.setBpathId(bpathId);
			if(this.file.getName().contains("log")){
				logFileDb.setLogtype("LOG");
			}else{
				logFileDb.setLogtype("QC");
			}

			// Vemos si el fichero no existe ya
			int count = 0;
			count = logFileAccess.countById(logFileDb.getProgId(), logFileDb.getOblId(), logFileDb.getLogFilename());

			if(count==0){
				logFileAccess.insert(logFileDb);
			}
			
		}catch(SQLException sqlE){
			con.rollback();
			con.setAutoCommit(autocommit);
			throw sqlE;
		}
		
		// Finalizamos la transacción
		
	}

	
}
