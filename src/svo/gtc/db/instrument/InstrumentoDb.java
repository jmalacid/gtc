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

package svo.gtc.db.instrument;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InstrumentoDb {
	private String 		insId 		= null;
	private String 		insName	= null;
	private String 		insTipo 	= null;
	private Double 		insFoco 	= null;

	public InstrumentoDb(ResultSet resset) throws SQLException{
		this.insId 	= resset.getString("ins_id");
		this.insName 	= resset.getString("ins_name");
		this.insTipo 	= resset.getString("ins_tipo");
		
		this.insFoco 	= new Double(resset.getDouble("ins_foco"));
		if(resset.wasNull()){
			this.insFoco = null;
		}
		
	}

	public String getInsId() {
		return insId;
	}

	public void setInsId(String insId) {
		this.insId = insId;
	}

	public String getInsName() {
		return insName;
	}

	public void setInsName(String insName) {
		this.insName = insName;
	}

	public String getInsTipo() {
		return insTipo;
	}

	public void setInsTipo(String insTipo) {
		this.insTipo = insTipo;
	}

	public Double getInsFoco() {
		return insFoco;
	}

	public void setInsFoco(Double insFoco) {
		this.insFoco = insFoco;
	}


	
}
