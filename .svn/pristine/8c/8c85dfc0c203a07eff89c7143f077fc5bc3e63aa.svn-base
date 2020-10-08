/*
 * @(#)Programa.java    Feb 17, 2011
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

package svo.gtc.db.filtro;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FiltroDb {
	private Integer		filId 			= null;
	private String 		filShortname 	= null;
	private String 		filName 		= null;
	private Double 		filLandacentral	= null;

	public FiltroDb(){}
	
	public FiltroDb(ResultSet resset) throws SQLException{
		this.filId 			= new Integer(resset.getInt("fil_id"));
		this.filShortname	= resset.getString("fil_shortname");
		this.filName 		= resset.getString("fil_name");
		this.filLandacentral= resset.getDouble("fil_landacentral");
		if(resset.wasNull()){
			this.filLandacentral=null;
		}
	}

	public Integer getFilId() {
		return filId;
	}

	public void setFilId(Integer filId) {
		this.filId = filId;
	}

	public String getFilShortname() {
		return filShortname;
	}

	public void setFilShortname(String filShortname) {
		this.filShortname = filShortname;
	}

	public String getFilName() {
		return filName;
	}

	public void setFilName(String filName) {
		this.filName = filName;
	}

	public Double getFilLandacentral() {
		return filLandacentral;
	}

	public void setFilLandacentral(Double filLandacentral) {
		this.filLandacentral = filLandacentral;
	}


	
}
