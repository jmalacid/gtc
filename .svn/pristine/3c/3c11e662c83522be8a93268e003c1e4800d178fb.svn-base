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

package svo.gtc.db.obsblock;

import java.sql.ResultSet;
import java.sql.SQLException;

import svo.gtc.proddat.ObsBlock;

public class ObsBlockDb {
	private String 		prog_id 		= null;
	private String 		obl_id 			= null;
	private String 		obl_pi			= null;


	public ObsBlockDb(ObsBlock obl) throws SQLException{
		this.prog_id 	= obl.getProgram().getProgId().toUpperCase().replaceAll("\\s", "");
		this.obl_id		= obl.getOblId().toUpperCase().replaceAll("\\s", "");
	}

	public ObsBlockDb(ResultSet resset) throws SQLException{
		this.prog_id 	= resset.getString("prog_id");
		this.obl_id		= resset.getString("obl_id");
		this.obl_pi		= resset.getString("obl_pi");
	}

	public String getProg_id() {
		return prog_id;
	}

	public void setProg_id(String progId) {
		prog_id = progId;
	}

	public String getObl_id() {
		return obl_id;
	}

	public void setObl_id(String oblId) {
		obl_id = oblId;
	}

	public String getObl_pi() {
		return obl_pi;
	}

	public void setObl_pi(String oblPi) {
		obl_pi = oblPi;
	}

	
}
