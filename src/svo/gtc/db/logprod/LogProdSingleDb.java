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

package svo.gtc.db.logprod;

import java.sql.Timestamp;
import java.util.Calendar;

import svo.gtc.proddat.ProdDatos;

public class LogProdSingleDb {
	private Timestamp	logpTime		= null;
	private String	 	lprogId			= null;
	private String		loblId			= null;
	private Integer	 	lprodId			= null;
	private Integer     logpPriv        = null;

	public LogProdSingleDb(){
		this.logpTime=new Timestamp(Calendar.getInstance().getTimeInMillis());
	}

	public LogProdSingleDb(ProdDatos prod){
		
		this.logpTime=new Timestamp(Calendar.getInstance().getTimeInMillis());
		this.lprogId = prod.getProgram().getProgId().trim().toUpperCase();
		this.loblId = prod.getOblock().getOblId().trim().toUpperCase();
		this.lprodId = prod.getProdId();
		this.setLogpPriv(prod.getDate());
	
	}

	public Timestamp getLogpTime() {
		return logpTime;
	}

	public void setLogpTime(Timestamp logpTime) {
		this.logpTime = logpTime;
	}

	public String getLprogId() {
		return lprogId;
	}

	public void setLprogId(String lprogId) {
		this.lprogId = lprogId;
	}

	public String getLoblId() {
		return loblId;
	}

	public void setLoblId(String loblId) {
		this.loblId = loblId;
	}

	public Integer getLprodId() {
		return lprodId;
	}

	public void setLprodId(Integer lprodId) {
		this.lprodId = lprodId;
	}

	public Integer getLogpPriv() {
		return logpPriv;
	}

	public void setLogpPriv(Timestamp date) {
		//Obtenemos la fecha actual
		Timestamp initime= new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(initime.getTime());	
		//Según el programa restamos lo correspondiente
		if(lprogId.contains("DDT")){
			calendar.add(calendar.MONTH, -6);
		}else{
			calendar.add(calendar.YEAR, -1);
		}
		
		Timestamp priv = new Timestamp(calendar.getTimeInMillis());  
		if(date!=null && priv.before(date)){
			//Se trata de un dato privado
			this.setLogpPriv(1);
		}else{
			this.setLogpPriv(0);
		}
	}
	
	public void setLogpPriv(Integer logpPriv) {
		this.logpPriv=logpPriv;
	}

	
	
}
