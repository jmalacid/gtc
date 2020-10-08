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

package svo.gtc.db.colecciondatos;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ColeccionDatosDb {
	private String 		usrId	 	= null;
	private Integer		colId 		= null;
	private String 		colName 	= null;
	private String		colBibcode	= null;
	private String		colDataType = null;
	private String 		colRedType	= null;
	private String 		colDesc		= null;

	public ColeccionDatosDb(){}
	
	public ColeccionDatosDb(ResultSet resset) throws SQLException{
		this.usrId			= resset.getString("usr_id");
		this.colId 			= resset.getInt("col_id");
		this.colName 		= resset.getString("col_name");
		this.colBibcode		= resset.getString("col_bibcode");
		this.colDataType	= resset.getString("col_datatype");
		this.colRedType		= resset.getString("col_redtype");
		this.colDesc		= resset.getString("col_desc");
	}

	public synchronized String getUsrId() {
		return usrId;
	}

	public synchronized void setUsrId(String usrId) {
		this.usrId = usrId;
	}

	public synchronized Integer getColId() {
		return colId;
	}

	public synchronized void setColId(Integer colId) {
		this.colId = colId;
	}

	public synchronized String getColName() {
		return colName;
	}

	public synchronized void setColName(String colName) {
		this.colName = colName;
	}

	public String getColBibcode() {
		return colBibcode;
	}

	public void setColBibcode(String colBibcode) {
		this.colBibcode = colBibcode;
	}

	public String getColDataType() {
		return colDataType;
	}

	public void setColDataType(String colDataType) {
		this.colDataType = colDataType;
	}

	public String getColRedType() {
		return colRedType;
	}

	public void setColRedType(String colRedType) {
		this.colRedType = colRedType;
	}

	public synchronized String getColDesc() {
		return colDesc;
	}

	public synchronized void setColDesc(String colDesc) {
		this.colDesc = colDesc;
	}
	
}
