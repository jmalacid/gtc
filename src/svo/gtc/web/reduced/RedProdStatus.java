/*
 * @(#)RedProdStatus.java    13/06/2012
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

package svo.gtc.web.reduced;

import java.io.File;

import svo.gtc.proddat.ProdDatosRed;

public class RedProdStatus {
	
	public static final String ERROR = "ERROR";
	public static final String OK = "OK";
	public static final String EXISTS = "EXISTS";
	public static final String WARNING = "WARNING";
	
	public static final String COLOR_ERROR = "red";
	public static final String COLOR_OK = "lime";
	public static final String COLOR_EXISTS = "gold";
	public static final String COLOR_W = "orange";
	

	private ProdDatosRed product;
	private File		 file; 
	private String		 status;
	private String		 desc;
	
	public RedProdStatus(File file){
		this.file=file;
	}
	
	public synchronized ProdDatosRed getProduct() {
		return product;
	}
	public synchronized void setProduct(ProdDatosRed product) {
		this.product = product;
	}
	public synchronized String getStatus() {
		return status;
	}
	public synchronized void setStatus(String status) {
		this.status = status;
	}
	public synchronized String getDesc() {
		return desc;
	}
	public synchronized void setDesc(String desc) {
		this.desc = desc;
	}
	public synchronized File getFile() {
		return file;
	}
	public synchronized void setFile(File file) {
		this.file = file;
	}

	public String getFileName(){
		return this.file.getName();
	}
	
	public synchronized String getWebColor() {
		
		if(status.equals(ERROR)) return COLOR_ERROR;
		if(status.equals(OK)) return COLOR_OK;
		if(status.equals(EXISTS)) return COLOR_EXISTS;
		if(status.equals(WARNING)) return COLOR_W;
		
		return "";
	}
	
	
}
