/*
 * @(#)GtcFits.java    May 21, 2009
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

package svo.gtc.fits;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;

public class GtcFits {
	
	BasicHDU[] hdus = null;
	/*
	private Double	osivers 	= null; 
	private Integer osistat 	= null;
	private String 	object 		= null;
	private String 	obsmode 	= null;
	private String 	obstype 	= null;
	private String 	osfilt 		= null;
	private Double 	oswav		= null;
	private String 	tfid 		= null;
	private String 	tfwave 		= null;
	private String 	tforder 	= null;
	private String 	tffsr 		= null;
	private String 	tffwhm 		= null;
	private String 	tfspac 		= null;
	private Double 	tftemp1 	= null;
	private Double 	tftemp2 	= null;
	private Double 	tftemp3 	= null;
	private Double 	tfpres1 	= null;
	private Double 	cscxoff 	= null;
	private Double 	cscyoff 	= null;
	private Double 	csczoff 	= null;
	private Double 	slitw 		= null;
	private Double 	slitpa 		= null;
	private Double 	camfocus	= null;
	private Double 	camtemp 	= null;
	private String 	filter1 	= null;
	private String 	filter2 	= null;
	private String 	filter3 	= null;
	private String 	filter4 	= null;
	*/
	
	public GtcFits(File file) throws FitsException, IOException{
	    Fits f = new Fits(file);
		this.hdus = f.read();
	}
		
	public String getStringKeyword(String keyword, int extension){
		Header hdr = this.hdus[extension].getHeader();
		return hdr.getStringValue(keyword);
	}
	
	public int getIntKeyword(String keyword, int extension){
		Header hdr = this.hdus[extension].getHeader();
		return hdr.getIntValue(keyword);
	}
	
	public float getFloatKeyword(String keyword, int extension){
		Header hdr = this.hdus[extension].getHeader();
		return hdr.getFloatValue(keyword);
	}
	
	/*
	public void cargaKeywords() throws FitsException, IOException{
		
		osivers 	= new Double(hdr.getFloatValue("OSIVERS"));
		
		osistat 	= new Integer(hdr.getIntValue("OSISTAT"));
		
		object 		= hdr.getStringValue("OBJECT");
		if(object!=null && object.trim().length()==0) object=null;
		
		obsmode 	= hdr.getStringValue("OBSMODE");
		if(obsmode!=null && obsmode.trim().length()==0) object=null;
		
		obstype 	= hdr.getStringValue("OBSTYPE");
		if(obstype!=null && obstype.trim().length()==0) object=null;
		
		osfilt 		= hdr.getStringValue("OBSFILT");
		if(osfilt!=null && osfilt.trim().length()==0) object=null;
		
		oswav		= new Double(hdr.getDoubleValue("OSWAV"));
		
		slitw 		= null;
		
		slitpa 		= null;
		
		camfocus	= null;
		
		camtemp 	= null;
		
		filter1 	= null;
		
		filter2 	= null;
		
		filter3 	= null;
		
		filter4 	= null;
		
	}
	*/
	
	
	
	
	public static void main(String[] args0) throws FitsException, IOException{
		
		GtcFits gf = new GtcFits(new File("/pcdisk/marconi/raul/proyectos/GTC/fits/0000000982-20090322-OSIRIS-OsirisLongSlitSpectroscopy.fits"));
		System.out.println(gf.getStringKeyword("OBJECT", 0));
		System.out.println(Calendar.getInstance().get(Calendar.YEAR));
		
	}
	
	
	

}
