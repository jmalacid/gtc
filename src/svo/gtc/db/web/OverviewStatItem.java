/*
 * @(#)OverviewStatItem.java    20/11/2012
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

package svo.gtc.db.web;

public class OverviewStatItem {

	private Integer numRaw=0;
	private Integer	gbRaw=0;
	private Integer numRed=0;
	private Integer	gbRed=0;
	private Integer numCal=0;
	private Integer gbCal=0;
	

	public Integer getNumRaw() { 
		return numRaw;
	}
	public void setNumRaw(Integer numRaw) {
		this.numRaw = numRaw;
	}
	public Integer getGbRaw() {
		return gbRaw;
	}
	public void setGbRaw(Integer gbRaw) {
		this.gbRaw = gbRaw;
	}
	
	public Integer getNumRed() {
		return numRed;
	}
	public void setNumRed(Integer numRed) {
		this.numRed = numRed;
	}
	public Integer getGbRed() {
		return gbRed;
	}
	public void setGbRed(Integer gbRed) {
		this.gbRed = gbRed;
	}
	public Integer getNumCal() {
		return numCal;
	}
	public void setNumCal(Integer numCal) {
		this.numCal = numCal;
	}
	public Integer getGbCal() {
		return gbCal;
	}
	public void setGbCal(Integer gbCal) {
		this.gbCal = gbCal;
	}
	
	
	
	
}
