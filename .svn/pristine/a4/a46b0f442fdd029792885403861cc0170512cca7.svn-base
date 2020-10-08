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

package svo.gtc.db.programa;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import svo.gtc.proddat.Program;

public class ProgramaDb {
	private String 		prog_id 		= null;
	private String 		prog_title 		= null;
	private Date 		prog_periodoprop= null;
	private Integer		prog_year		= null;
	private String		prog_semester	= null;
	
	public ProgramaDb(Program prog){
		this.prog_id=prog.getProgId().toUpperCase().replaceAll("\\s", "");
		this.prog_title=prog.getProgId().trim();
		this.prog_year=prog.getProgYear();
		this.prog_semester=prog.getProgSemester();
	}

	public ProgramaDb(ResultSet resset) throws SQLException{
		this.prog_id 			= resset.getString("prog_id");
		this.prog_title			= resset.getString("prog_title");
		this.prog_periodoprop	= resset.getDate("prog_periodoprop");
		
		this.prog_year			= new Integer(resset.getInt("prog_year"));
		if(resset.wasNull()) this.prog_year=null;
		
		this.prog_semester		= resset.getString("prog_semester");
	}

	public String getProg_id() {
		return prog_id;
	}

	public void setProg_id(String progId) {
		prog_id = progId;
	}

	public String getProg_title() {
		return prog_title;
	}

	public void setProg_title(String progTitle) {
		prog_title = progTitle;
	}

	public Date getProg_periodoprop() {
		return prog_periodoprop;
	}

	public void setProg_periodoprop(Date progPeriodoprop) {
		prog_periodoprop = progPeriodoprop;
	}

	public Integer getProg_year() {
		return prog_year;
	}

	public void setProg_year(Integer prog_year) {
		this.prog_year = prog_year;
	}

	public String getProg_semester() {
		return prog_semester;
	}

	public void setProg_semester(String prog_semester) {
		this.prog_semester = prog_semester;
	}
	
}
