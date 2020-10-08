/*
 * @(#)ProductoDatos.java    Aug 6, 2009
 *
 *
 * Ra�l Guti�rrez S�nchez. (raul@cab.inta-csic.es)	
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

package svo.gtc.db.logfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogFileDb {
	private String		progId 			= null;
	private String		oblId 			= null;
	private String	 	logFilename		= null;
	private String		logPath 		= null;
	private Integer 	bpathId 		= null;
	private String 		bpathPath 		= null;
	private String		logtype			=null;
	
	public LogFileDb(){}
	
	public LogFileDb(ResultSet resset) throws SQLException{
		this.progId		= resset.getString("prog_id");
		this.oblId		= resset.getString("obl_id");
		this.logFilename = resset.getString("log_filename");
		this.logPath 	= resset.getString("log_path");
		this.logtype	= resset.getString("log_type");
		this.bpathId 	= resset.getInt("bpath_id");
		this.bpathPath 	= resset.getString("bpath_path");
	}

	
	
	
	
	public String getProgId() {
		return progId;
	}

	public void setProgId(String progId) {
		this.progId = progId;
	}

	public String getOblId() {
		return oblId;
	}

	public void setOblId(String oblId) {
		this.oblId = oblId;
	}

	public String getLogFilename() {
		return logFilename;
	}

	public void setLogFilename(String logFilename) {
		this.logFilename = logFilename;
	}

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	public Integer getBpathId() {
		return bpathId;
	}

	public void setBpathId(Integer bpathId) {
		this.bpathId = bpathId;
	}

	public String getBpathPath() {
		return bpathPath;
	}

	public void setBpathPath(String bpathPath) {
		this.bpathPath = bpathPath;
	}
	
	public String getLogtype() {
		return logtype;
	}

	public void setLogtype(String logtype) {
		this.logtype = logtype;
	}

	public String getAbsolutePath(){
		return this.getBpathPath()+this.getLogPath()+"/"+this.getLogFilename();
	}

	public File getFile(){
		return new File(getAbsolutePath());
		
	}
	
	public InputStream getInputStream() throws FileNotFoundException{
		FileInputStream fins = new FileInputStream(getAbsolutePath());
		
		return(fins);
	}

	public long writeToStream(OutputStream out) throws IOException {
		long size=0;
		InputStream ins = this.getInputStream();
		byte[] buf = new byte[1024];
		int len = -1;
		while((len=ins.read(buf))>=0){
			size+=len;
			out.write(buf, 0, len);
		}
		out.flush();
		return size;
	}

	
}
