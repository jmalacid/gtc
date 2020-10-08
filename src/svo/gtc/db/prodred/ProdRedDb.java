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

package svo.gtc.db.prodred;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.util.Cursor;

public class ProdRedDb {
	private String 		usrId 			= null;
	private Integer		colId	 		= null;
	private Integer 	predId	 		= null;
	private Integer	 	bpathId 		= null;
	private String	 	bpathPath 		= null;
	private String 		predFilename	= null;
	private String 		predPath 		= null;
	private String 		predFilenameOrig	= null;
	private Long		predFilesize	= null;
	private String 		predMd5sum		= null;
	private Double	 	predRa 			= null;
	private Double	 	predDe 			= null;
	private String	 	predType 			= null;

	public ProdRedDb(){}

	public ProdRedDb(ResultSet resset) throws SQLException{
		this.usrId 		= resset.getString("usr_id");
		this.colId 		= resset.getInt("col_id");
		this.predId 	= resset.getInt("pred_id");
		
		this.bpathId 	= resset.getInt("bpath_id");
		this.bpathPath 	= resset.getString("bpath_path");
		this.predFilename 	= resset.getString("pred_filename");
		this.predPath 	= resset.getString("pred_path");

		this.predFilenameOrig 	= resset.getString("pred_filenameorig");

		this.predFilesize	= new Long(resset.getLong("pred_filesize"));
		this.predMd5sum 	= resset.getString("pred_md5sum");

		this.predRa	= new Double(resset.getDouble("pred_ra"));
		this.predDe	= new Double(resset.getDouble("pred_de"));
		
		this.predType	= resset.getString("pred_type");

	}
	
	public String getAbsolutePath(){
		return this.getBpathPath()+this.getPredPath()+this.getPredFilename();
	}

	public InputStream getInputStream() throws FileNotFoundException{
		FileInputStream fins = new FileInputStream(getAbsolutePath());
		
		return(fins);
	}

	public long writeToStream(OutputStream out) throws IOException {
		InputStream ins = this.getInputStream();
		byte[] buf = new byte[1024];
		int len = -1;
		long size = 0;
		while((len=ins.read(buf))>=0){
			out.write(buf, 0, len);
			size+=len;
		}
		out.flush();
		return size;
	}
	
	public String getHeader(){
		String salida = "";
		
		Fits f;
		BasicHDU[] hdus =  new BasicHDU[0];
		try {
			//System.out.println("XXXXXXXXXXXXX "+getAbsolutePath());
			f = new Fits(getAbsolutePath());
			hdus = f.read();
		} catch (FitsException e) {
			e.printStackTrace();
			return null;
		}

		for(int i=0; i<hdus.length; i++){
			BasicHDU h = hdus[i];
			if( h != null) {
				salida += "Header listing for HDU #"+i+":\n";
				Header hdr = h.getHeader();		
				Cursor iter = hdr.iterator();
				while(iter.hasNext()) {
					HeaderCard hc = (HeaderCard) iter.next();
					salida += hc.toString()+"\n";
				}
				salida+="\n";
			}
		}
		
		
		return(salida);
	}

	public String getHeaderText() throws IOException{
		String salida="";
		
		File fichero = new File(getAbsolutePath());
		BufferedReader br = new BufferedReader(new FileReader(fichero)); 
		String linea = "";
		
		while( (linea=br.readLine())!=null){
			salida +=linea+"\n";
		}
		br.close();
		
		return salida;
	}
	
	public synchronized String getUsrId() {
		return usrId;
	}

	public synchronized void setUsrId(String usrId) {
		this.usrId = usrId;
	}

	public synchronized Integer getColId() {
		return colId;
	}

	public synchronized void setColId(Integer colId) {
		this.colId = colId;
	}

	public synchronized Integer getPredId() {
		return predId;
	}

	public synchronized void setPredId(Integer predId) {
		this.predId = predId;
	}

	public synchronized Integer getBpathId() {
		return bpathId;
	}

	public synchronized void setBpathId(Integer bpathId) {
		this.bpathId = bpathId;
	}

	public synchronized String getBpathPath() {
		return bpathPath;
	}

	public synchronized void setBpathPath(String bpathPath) {
		this.bpathPath = bpathPath;
	}

	public synchronized String getPredFilename() {
		return predFilename;
	}

	public synchronized void setPredFilename(String predFilename) {
		this.predFilename = predFilename;
	}

	public synchronized String getPredPath() {
		return predPath;
	}

	public synchronized void setPredPath(String predPath) {
		this.predPath = predPath;
	}

	public synchronized String getPredFilenameOrig() {
		return predFilenameOrig;
	}

	public synchronized void setPredFilenameOrig(String predFilenameOrig) {
		this.predFilenameOrig = predFilenameOrig;
	}

	public synchronized Long getPredFilesize() {
		return predFilesize;
	}

	public synchronized void setPredFilesize(Long predFilesize) {
		this.predFilesize = predFilesize;
	}

	public synchronized String getPredMd5sum() {
		return predMd5sum;
	}

	public synchronized void setPredMd5sum(String predMd5sum) {
		this.predMd5sum = predMd5sum;
	}

	public synchronized Double getPredRa() {
		return predRa;
	}

	public synchronized void setPredRa(Double predRa) {
		this.predRa = predRa;
	}

	public synchronized Double getPredDe() {
		return predDe;
	}

	public synchronized void setPredDe(Double predDe) {
		this.predDe = predDe;
	}

	public String getPredType() {
		return predType;
	}

	public void setPredType(String predType) {
		this.predType = predType;
	}
	
}
