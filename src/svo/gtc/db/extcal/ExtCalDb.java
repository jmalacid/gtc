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

package svo.gtc.db.extcal;

import java.sql.ResultSet;
import java.sql.SQLException;

import svo.gtc.proddat.ProdDatos;

public class ExtCalDb {
	private Integer		prodId 			= null;
	private String 		progId 			= null;
	private String 		oblId	 		= null;
	
	public ExtCalDb(){}

	public ExtCalDb(ProdDatos prod){
		this.prodId			=	prod.getProdId();
		this.progId			=	prod.getProgram().getProgId().toUpperCase().replaceAll("\\s", "");
		this.oblId			=	prod.getOblock().getOblId().toUpperCase().replaceAll("\\s", "");
	}
	
	public ExtCalDb(ResultSet resset) throws SQLException{
		this.prodId 		= new Integer(resset.getInt("prod_id"));
		this.progId 		= resset.getString("prog_id");
		this.oblId 		= resset.getString("obl_id");
	}
	

	public Integer getProdId() {
		return prodId;
	}

	public void setProdId(Integer prodId) {
		this.prodId = prodId;
	}

	public String getProgId() {
		return progId;
	}

	public void setProgId(String progId) {
		this.progId = progId;
	}

	public String getOblId() {
		return oblId;
	}

	public void setOblId(String oblId) {
		this.oblId = oblId;
	}
	
}
