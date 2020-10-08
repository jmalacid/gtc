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

package svo.gtc.db.detector;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DetectorDb {
	private Integer		detId 		= null;
	private String 		insId		= null;
	private String 		detShortname 	= null;
	private String 		detName 	= null;
	private String 		detDesc 	= null;

	public DetectorDb(ResultSet resset) throws SQLException{
		this.detId 	= new Integer(resset.getInt("det_id"));
		this.insId 	= resset.getString("ins_id");
		this.detShortname	= resset.getString("det_shortname");
		this.detName 	= resset.getString("det_name");
		this.detDesc 	= resset.getString("det_desc");
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

	public String getDetShortname() {
		return detShortname;
	}

	public void setDetShortname(String detShortname) {
		this.detShortname = detShortname;
	}

	public String getDetName() {
		return detName;
	}

	public void setDetName(String detName) {
		this.detName = detName;
	}

	public String getDetDesc() {
		return detDesc;
	}

	public void setDetDesc(String detDesc) {
		this.detDesc = detDesc;
	}

	
}
