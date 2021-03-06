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

package svo.gtc.db.prodat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.PaddingException;
import nom.tam.util.Cursor;

public class ProdDatosDb {
	private Integer		prodId 			= null;
	private String 		progId 			= null;
	private String 		oblId	 		= null;
	private String 		prodFilename 	= null;
	private Integer 	detId	 		= null;
	private String 		insId		 	= null;
	private String 		modId		 	= null;
	private String 		modShortname	= null;
	private String 		submId		 	= null;
	private Integer		confId	 		= null;
	private String		mtyId	 		= null;
	private Integer	 	bpathId 		= null;
	private String	 	bpathPath 		= null;
	private String 		prodPath 		= null;
	private Long		prodFilesize	= null;
	private Double	 	prodRa 			= null;
	private Double	 	prodDe 			= null;
	private String	 	prodObsType		= null;
	private Timestamp 	prodInitime		= null;
	private Timestamp	prodEndtime		= null;
	private Double	 	prodExposure	= null;
	private Double	 	prodAirmass		= null;
	private String	 	prodObserver	= null;
	private String		prodObject		= null;
	private Double		prodBandMin		= null;
	private Double		prodBandMax		= null;
	private String		prodPolig		= null;
	private String		prodPolig2		= null;
	private String		prodPoint		= null;
	
	private WarningDb[]	warnings		= new WarningDb[0];

	
	public ProdDatosDb(){}

	public ProdDatosDb(ProdDatosDb prod){
		this.prodId			=	prod.getProdId();
		this.progId			=	prod.getProgId();
		this.oblId			=	prod.getOblId();
		this.prodFilename	=	prod.getProdFilename();
		this.detId			=	prod.getDetId();
		this.insId			=	prod.getInsId();
		this.modId			=	prod.getModId();
		this.modShortname	=	prod.getModShortname();
		this.submId			=	prod.getSubmId();
		this.confId			=	prod.getConfId();
		this.mtyId			=	prod.getMtyId();
		this.bpathId		=	prod.getBpathId();
		this.bpathPath		=	prod.getBpathPath();
		this.prodPath		=	prod.getProdPath();
		this.prodFilesize	=	prod.getProdFilesize();
		this.prodRa			=	prod.getProdRa();
		this.prodDe			=	prod.getProdDe();
		this.prodInitime	=	prod.getProdInitime();
		this.prodEndtime	=	prod.getProdEndtime();
		this.prodExposure	=	prod.getProdExposure();
		this.prodAirmass	=	prod.getProdAirmass();
		this.prodObserver	=	prod.getProdObserver();
		this.prodObject 	=	prod.getProdObject();
		this.prodBandMin	= 	prod.getProdBandMin();
		this.prodBandMax	=	prod.getProdBandMax();
		this.prodPolig 	=	prod.getProdPolig();
		this.prodPolig2 	=	prod.getProdPolig2();
		this.prodPoint 	=	prod.getProdPoint();
		
	}
	
	public ProdDatosDb(ResultSet resset) throws SQLException{
		this.prodId 		= new Integer(resset.getInt("prod_id"));
		this.progId 		= resset.getString("prog_id").trim();
		this.oblId 		= resset.getString("obl_id").trim();
		this.prodFilename 	= resset.getString("prod_filename");
		this.detId 		= resset.getInt("det_id");
		this.insId 		= resset.getString("ins_id").trim();
		this.modId 		= resset.getString("mod_id").trim();
		this.modShortname 	= resset.getString("mod_shortname").trim();
		this.submId 	= resset.getString("subm_id").trim();
		this.confId 	= new Integer(resset.getInt("conf_id"));
		this.mtyId 		= resset.getString("mty_id").trim();
		
		this.bpathId 		= resset.getInt("bpath_id");
		this.bpathPath 	= resset.getString("bpath_path");
		this.prodPath 		= resset.getString("prod_path");

		this.prodFilesize	= new Long(resset.getLong("prod_filesize"));

		this.prodRa	= new Double(resset.getDouble("prod_ra"));
		this.prodDe	= new Double(resset.getDouble("prod_de"));

		this.prodInitime 		= resset.getTimestamp("prod_initime");
		this.prodEndtime 		= resset.getTimestamp("prod_endtime");
		if(resset.wasNull()) prodEndtime = null;
		
		this.prodExposure	= new Double(resset.getDouble("prod_exposure"));
		if(resset.wasNull()) prodExposure = null;

		this.prodAirmass	= new Double(resset.getDouble("prod_airmass"));
		if(resset.wasNull()) prodAirmass = null;
		
		this.prodObserver 	= resset.getString("prod_observer");
		
		this.prodObject	= resset.getString("prod_object");
		if(resset.wasNull()) prodObject = null;
		
		this.prodBandMin	= new Double(resset.getDouble("prod_band_min"));
		if(resset.wasNull()) prodBandMin = null;
		
		this.prodBandMax	= new Double(resset.getDouble("prod_band_max"));
		if(resset.wasNull()) prodBandMax = null;
	}
	
	public String getAbsolutePath(){
		return this.getBpathPath()+this.getProdPath()+"/"+this.getProdFilename();
	}


	public String getHeader() throws IOException {
		
		
		String salida = "";
		
		Fits f;
		
		try{

			f = new Fits(getAbsolutePath());
			 try{
			    	f.read();	    
			    }catch(PaddingException e){
			    	f.addHDU(e.getTruncatedHDU());
			    }
			
			BasicHDU[] hdus =  new BasicHDU[0];
			
			hdus = f.read();
			
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
		}catch (Exception e){
			try{
				BasicHDU hdus = null;
			
				f = new Fits(getAbsolutePath());
				hdus = f.readHDU();
				
				salida += "Header listing for HDU :\n";
				Header hdr = hdus.getHeader();		
				Cursor iter = hdr.iterator();
				while(iter.hasNext()) {
					HeaderCard hc = (HeaderCard) iter.next();
					salida += hc.toString()+"\n";
				}
				salida+="\n";
			}catch(Exception e1){
				e.printStackTrace();
				String mensaje = "Sorry, this header can't be showed.\n Please download this file to see the header";
				return mensaje;
			}
		}

		/*for(int i=0; i<hdus.length; i++){
			System.out.println(i);
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
*/		
		
		return(salida);

		
	}

	
	public String getProdObsType() {
		return prodObsType;
	}

	public void setProdObsType(String prodObsType) {
		this.prodObsType = prodObsType;
	}

	public Integer getProdId() {
		return prodId;
	}

	public void setProdId(Integer prodId) {
		this.prodId = prodId;
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

	public String getProdFilename() {
		return prodFilename;
	}

	public void setProdFilename(String prodFilename) {
		this.prodFilename = prodFilename;
	}

	public Integer getDetId() {
		return detId;
	}

	public void setDetId(Integer detId) {
		this.detId = detId;
	}

	public String getInsId() {
		return insId;
	}

	public void setInsId(String insId) {
		this.insId = insId;
	}

	public String getModId() {
		return modId;
	}

	public void setModId(String modId) {
		this.modId = modId;
	}
	
	public String getModShortname() {
		return modShortname;
	}

	public void setModShortname(String modShortname) {
		this.modShortname = modShortname;
	}

	public String getSubmId() {
		return submId;
	}

	public void setSubmId(String submId) {
		this.submId = submId;
	}

	public Integer getConfId() {
		return confId;
	}

	public void setConfId(Integer confId) {
		this.confId = confId;
	}

	
	public String getMtyId() {
		return mtyId;
	}

	public void setMtyId(String mtyId) {
		this.mtyId = mtyId;
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

	public String getProdPath() {
		return prodPath;
	}

	public void setProdPath(String prodPath) {
		this.prodPath = prodPath;
	}

	public Long getProdFilesize() {
		return prodFilesize;
	}

	public void setProdFilesize(Long prodFilesize) {
		this.prodFilesize = prodFilesize;
	}

	public Double getProdRa() {
		return prodRa;
	}

	public void setProdRa(Double prodRa) {
		this.prodRa = prodRa;
	}

	public Double getProdDe() {
		return prodDe;
	}

	public void setProdDe(Double prodDe) {
		this.prodDe = prodDe;
	}

	public Timestamp getProdInitime() {
		return prodInitime;
	}

	public void setProdInitime(Timestamp prodInitime) {
		this.prodInitime = prodInitime;
	}

	public Timestamp getProdEndtime() {
		return prodEndtime;
	}

	public void setProdEndtime(Timestamp prodEndtime) {
		this.prodEndtime = prodEndtime;
	}

	public Double getProdExposure() {
		return prodExposure;
	}

	public void setProdExposure(Double prodExposure) {
		this.prodExposure = prodExposure;
	}

	public Double getProdAirmass() {
		return prodAirmass;
	}

	public void setProdAirmass(Double prodAirmass) {
		this.prodAirmass = prodAirmass;
	}
	public String getProdObserver() {
		return prodObserver;
	}

	public void setProdObserver(String prodObserver) {
		this.prodObserver = prodObserver;
	}
	
	public String getProdObject() {
		return prodObject;
	}

	public void setProdObject(String prodObject) {
		this.prodObject = prodObject;
	}

	public Double getProdBandMin() {
		return prodBandMin;
	}

	public void setProdBandMin(Double prodBandMin) {
		this.prodBandMin = prodBandMin;
	}

	public Double getProdBandMax() {
		return prodBandMax;
	}

	public void setProdBandMax(Double prodBandMax) {
		this.prodBandMax = prodBandMax;
	}
	
	public String getProdPolig() {
		return prodPolig;
	}

	public void setProdPolig(String prodPolig) {
		this.prodPolig = prodPolig;
	}

	public String getProdPolig2() {
		return prodPolig2;
	}

	public void setProdPolig2(String prodPolig2) {
		this.prodPolig2 = prodPolig2;
	}

	public String getProdPoint() {
		return prodPoint;
	}

	public void setProdPoint(String prodPoint) {
		this.prodPoint = prodPoint;
	}

	public InputStream getInputStream() throws FileNotFoundException{
		FileInputStream fins = new FileInputStream(getAbsolutePath());
		
		return(fins);
	}

	public void writeToStream(OutputStream out) throws IOException {
		InputStream ins = this.getInputStream();
		byte[] buf = new byte[1024];
		int len = -1;
		while((len=ins.read(buf))>=0){
			out.write(buf, 0, len);
		}
		out.flush();
	}

	public WarningDb[] getWarnings() {
		return warnings;
	}

	public void setWarnings(WarningDb[] warnings) {
		this.warnings = warnings;
	}
	
	
	

	/*
	public void writePreviewToStream(OutputStream out) throws IOException {
		String path = getPreviewAbsolutePath();
		FileInputStream ins = new FileInputStream(path);
		byte[] buf = new byte[1024];
		int len = -1;
		while((len=ins.read(buf))>=0){
			out.write(buf, 0, len);
		}
		out.flush();
	}
	 */
	
}
