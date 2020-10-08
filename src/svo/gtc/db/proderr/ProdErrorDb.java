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

package svo.gtc.db.proderr;

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
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.PaddingException;
import nom.tam.util.Cursor;
import svo.gtc.proddat.ProdDatos;
import svo.gtc.proddat.ProdDatos2;
import svo.gtc.proddat.ProdDatosRed;
import svo.gtc.proddat.ProdDatosRedCanaricam;

public class ProdErrorDb {
	private Integer		prodeId 		= null;
	private String		progId 		= null;
	private String		oblId 			= null;
	private String	 	bpathPath 		= null;
	private String 		prodeFilename 	= null;
	private String 		prodePath 		= null;
	private Timestamp	prodeEnterdate = null;
	
	private ErrorDb[]		errors			= new ErrorDb[0];
	
	public ProdErrorDb(ResultSet resset) throws SQLException{
		this.prodeId 		= new Integer(resset.getInt("prode_id"));
		this.progId 		= resset.getString("prog_id");
		this.oblId 		= resset.getString("obl_id");
		this.prodeFilename = resset.getString("prode_filename");
		this.bpathPath 	= resset.getString("bpath_path");
		this.prodePath 	= resset.getString("prode_path");
		this.prodeEnterdate= resset.getTimestamp("prode_enterdate");
	}

	public ProdErrorDb(ProdDatos prod, String basepath){
		this.prodeId	= prod.getProdId();
		this.progId	= prod.getProgram().getProgId();
		this.oblId		= prod.getOblock().getOblId();
		this.bpathPath = basepath;
		this.prodeFilename = prod.getFile().getName();
		this.prodePath = prod.getFile().getAbsolutePath().replaceAll(basepath, "").replaceAll(this.prodeFilename, "");
	}
	
	public ProdErrorDb(ProdDatos2 prod, String basepath){
		this.prodeId	= prod.getProdId();
		this.progId	= prod.getProgram().getProgId();
		this.oblId		= prod.getOblock().getOblId();
		this.bpathPath = basepath;
		this.prodeFilename = prod.getFile().getName();
		this.prodePath = prod.getFile().getAbsolutePath().replaceAll(basepath, "").replaceAll(this.prodeFilename, "");
	}
	
	public ProdErrorDb(ProdDatosRed prod, String basepath){
		this.prodeId	= prod.getPredId();
		this.progId	= prod.getProgramFits();
		this.oblId		= prod.getOblockFits();
		this.bpathPath = basepath;
		this.prodeFilename = prod.getFile().getName();
		this.prodePath = prod.getFile().getAbsolutePath().replaceAll(basepath, "").replaceAll(this.prodeFilename, "");
	}
	

	public String getAbsolutePath(){
		return this.getBpathPath()+this.getProdePath()+"/"+this.getProdeFilename();
	}

	
	public Integer getProdeId() {
		return prodeId;
	}

	public void setProdeId(Integer prodeId) {
		this.prodeId = prodeId;
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

	public String getBpathPath() {
		return bpathPath;
	}

	public void setBpathPath(String bpathPath) {
		this.bpathPath = bpathPath;
	}

	public String getProdeFilename() {
		return prodeFilename;
	}

	public void setProdeFilename(String prodeFilename) {
		this.prodeFilename = prodeFilename;
	}

	public String getProdePath() {
		return prodePath;
	}

	public void setProdePath(String prodePath) {
		this.prodePath = prodePath;
	}

	public Timestamp getProdeEnterdate() {
		return prodeEnterdate;
	}

	public void setProdeEnterdate(Timestamp prodeEnterdate) {
		this.prodeEnterdate = prodeEnterdate;
	}

	public ErrorDb[] getErrors() {
		return errors;
	}

	public void setErrors(ErrorDb[] errors) {
		this.errors = errors;
	}
	
	
	public String getHeader(){
		String salida = "";
		
		Fits f;
		BasicHDU[] hdus =  new BasicHDU[0];
		try {
			//System.out.println("XXXXXXXXXXXXX "+getAbsolutePath());
			f = new Fits(getAbsolutePath());
			
			try{
		    	f.read();
		    }catch(PaddingException e){
		    	f.addHDU(e.getTruncatedHDU());
		    }
			
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

	
}
