/*
  * @(#)ProductoDatos.java    Feb 10, 2011
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

package svo.gtc.db.prodat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class WarningDb {
	private String		err_id 		= null;
	private String 		err_desc	= null;
	
	public WarningDb(ResultSet resset) throws SQLException{
		this.err_id 	= resset.getString("err_id");
		this.err_desc 	= resset.getString("err_desc");
	}

	public WarningDb(){}

	public String getErr_id() {
		return err_id;
	}

	public void setErr_id(String errId) {
		err_id = errId;
	}

	public String getErr_desc() {
		return err_desc;
	}

	public void setErr_desc(String errDesc) {
		err_desc = errDesc;
	}
	
	public static String extraeId(String error){
		return error.replaceAll("^.*([EW]-.*[0-9]{4}):.+$", "$1");
		
	}

	/**
	 * Devuelve todos los identificadores de error encontrados en el mensaje (eliminando los Warnings).
	 * @param mensaje
	 * @return
	 */
	public static WarningDb[] getWarnings(String mensaje){
		String[] errIds = mensaje.split(";");
		Vector<WarningDb> aux = new Vector<WarningDb>();
		for(int err=0; err<errIds.length; err++){
			WarningDb error=new WarningDb();
			error.setErr_id(extraeId(errIds[err]));
			if(error.getErr_id().startsWith("W-")) aux.add(error);
		}
		
		return (WarningDb[])aux.toArray(new WarningDb[0]);
	}
	
}
