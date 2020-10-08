/*
 * @(#)PredProdDb.java    Jun 21, 2012
 *
 *
 * Raul Gutierrez Sanchez. (raul@cab.inta-csic.es)	
 * LAEFF: 	Laboratorio de Astrofisica Espacial y Fisica Fundamental.
 *        	INTA
 *			Villafranca del Castillo Satellite Tracking Station
 *			Villafranca del Castillo, E-28691 Villanueva de la Cañada
 *			Madrid - España
 *			
 * Phone:  34 91-8131260
 * Fax..:  34 91-8131160   
 * -----------------------------------------------------------------------
 * 
 *	ESTE  PROGRAMA  ES  DE  USO EXCLUSIVO.  ES  PROPIEDAD  DE  SU AUTOR  Raul 
 *  Gutierrez Sanchez  Y SE PRESENTA "COMO ESTA" SIN NINGUN TIPO DE GARANTIAS
 *  DENTRO Y FUERA  DE LA FINALIDAD PARA  LA QUE EL  AUTOR LO  HIZO. EL AUTOR 
 *  PERMITE  SU USO SIEMPRE QUE  SE LE REFERENCIE. SE PERMITE LA MODIFICACION
 *  DE SU  FUENTE SIEMPRE QUE SE  HAGA REFERENCIA A SU AUTORIA Y JUNTO A ELLA
 *  SE  ADJUNTE LA IDENTIFICACION DEL  AUTOR DE LAS NUEVAS MODIFICACIONES. EL
 *  AUTOR  NUNCA SERA RESPONSABLE DEL USO DE ESTE  PROGRAMA NI DE LOS EFECTOS
 *  QUE ESTE PROGRAMA PUDIEDA CAUSAR EN DETERIOROS, ALTERACIONES Y/O PERDIDAS 
 *  DE INFORMACION.
 *	
 */

package svo.gtc.db.prodred;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PredProdDb {
	private Integer 	predId	 		= null;
	private String		progId			= null;
	private String		oblId			= null;
	private Integer		prodId			= null;

	public PredProdDb(){}

	public PredProdDb(ResultSet resset) throws SQLException{
		this.predId 	= resset.getInt("pred_id");
		
		this.progId 	= resset.getString("prog_id");
		this.oblId 		= resset.getString("obl_id");
		this.prodId 	= resset.getInt("prod_id");
	}

	public synchronized Integer getPredId() {
		return predId;
	}

	public synchronized void setPredId(Integer predId) {
		this.predId = predId;
	}

	public synchronized String getProgId() {
		return progId;
	}

	public synchronized void setProgId(String progId) {
		this.progId = progId;
	}

	public synchronized String getOblId() {
		return oblId;
	}

	public synchronized void setOblId(String oblId) {
		this.oblId = oblId;
	}

	public synchronized Integer getProdId() {
		return prodId;
	}

	public synchronized void setProdId(Integer prodId) {
		this.prodId = prodId;
	}

	public PredProdDb(Integer predId, String progId, String oblId, Integer prodId) {
		super();
		this.predId = predId;
		this.progId = progId;
		this.oblId = oblId;
		this.prodId = prodId;
	}
	
}
