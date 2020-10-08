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

package svo.gtc.db.logquery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class LogQueryDb {
	private Timestamp	logqTime		= null;
	private String		logqHost		= null;
	private String	 	logqType		= null;
	
	public LogQueryDb(){}
	
	public LogQueryDb(ResultSet resset) throws SQLException{
		this.logqTime		= resset.getTimestamp("logq_time");
		this.logqHost		= resset.getString("logq_host");
		this.logqType 		= resset.getString("logq_type");
	}

	public Timestamp getLogqTime() {
		return logqTime;
	}

	public void setLogqTime(Timestamp logqTime) {
		this.logqTime = logqTime;
	}

	public String getLogqHost() {
		return logqHost;
	}

	public void setLogqHost(String logqHost) {
		//Tratamos de resolver la IP
		
		
		this.logqHost = hostName(logqHost);
	}

	public String getLogqType() {
		return logqType;
	}

	public void setLogqType(String logqType) {
		this.logqType = logqType;
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
