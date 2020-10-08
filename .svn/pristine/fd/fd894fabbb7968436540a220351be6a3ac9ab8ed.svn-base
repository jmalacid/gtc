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
import java.util.Vector;

public class LogProdDb {
	private Timestamp	logpTime		= null;
	private String	 	logpType		= null;
	private String		logpHost		= null;
	private Double	 	logpElapsed		= null;
	private Long	 	logpSize		= null;
	
	private LogProdSingleDb[]	logpSingle	= new LogProdSingleDb[0];
	
	public LogProdDb(){}

	public Timestamp getLogpTime() {
		return logpTime;
	}

	public void setLogpTime(Timestamp logpTime) {
		this.logpTime = logpTime;
	}

	public String getLogpType() {
		return logpType;
	}

	public void setLogpType(String logpType) {
		this.logpType = logpType;
	}

	public String getLogpHost() {
		return logpHost;
	}

	public void setLogpHost(String logpHost) {
		this.logpHost = hostName(logpHost);
	}

	public Double getLogpElapsed() {
		return logpElapsed;
	}

	public void setLogpElapsed(Double logpElapsed) {
		this.logpElapsed = logpElapsed;
	}

	
	
	public LogProdSingleDb[] getLogpSingle() {
		return logpSingle;
	}

	public void setLogpSingle(LogProdSingleDb[] logpSingle) {
		this.logpSingle = logpSingle;
	}
	
	

	public Long getLogpSize() {
		return logpSize;
	}

	public void setLogpSize(Long logpSize) {
		this.logpSize = logpSize;
	}

	/**
	 * Añade un producto a la lista de productos.
	 * @param prod
	 */
	public void addProd(LogProdSingleDb newProdSingle){
		Vector<LogProdSingleDb> aux = new Vector<LogProdSingleDb>();

		for(int i=0; i<logpSingle.length; i++){
			aux.add(logpSingle[i]);
		}
		
		
		aux.add( newProdSingle );
		
		logpSingle=(LogProdSingleDb[]) aux.toArray(new LogProdSingleDb[0]);
		
	}
	
	/**
	 * Resuelve el nombre de HOST.
	 * @param hosts
	 * @return
	 */
	public String hostName(String host){
		if(host.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")){
			try{
				return java.net.InetAddress.getByName(host).getHostName();
			}catch(Exception e){
				return host;
			}
		}else{
			return host;
		}
	}


}
