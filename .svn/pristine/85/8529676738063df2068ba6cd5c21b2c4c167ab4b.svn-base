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

package svo.gtc.db.conf;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfDb {
	private Integer		detId 	= null;
	private String 		insId	= null;
	private String 		modId 	= null;
	private String 		submId 	= null;
	private Integer		confId = null;

	public ConfDb(){}
	
	public ConfDb(ConfDb conf){
		this.detId=conf.getDetId();
		this.insId=conf.getInsId();
		this.modId=conf.getModId();
		this.submId=conf.getSubmId();
		this.confId=conf.getConfId();
	}

	public ConfDb(ResultSet resset) throws SQLException{
		this.detId 	= new Integer(resset.getInt("det_id"));
		this.insId 	= resset.getString("ins_id");
		this.modId 	= resset.getString("mod_id");
		this.submId = resset.getString("subm_id");
		this.confId	= new Integer(resset.getString("conf_id"));
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


	
}
