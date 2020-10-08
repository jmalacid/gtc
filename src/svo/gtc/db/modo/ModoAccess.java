/*
 * @(#)InstrumentAccess.java    Feb 17, 2011
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

package svo.gtc.db.modo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class ModoAccess {

	Connection con = null;
	//SELECT
	private PreparedStatement pstSelByInsId;
	
	private String select=
		" SELECT ins_id, mod_id, mod_shortname, mod_name, mod_desc " +
		" 	FROM modo a ";

	public ModoAccess(Connection conex) throws SQLException{
		con = conex;
		//Ordenes Precompiladas --------------------------------------------------
		pstSelByInsId 		= conex.prepareStatement(select+" WHERE ins_id=?;");
	}
	
	public ModoDb[] selectByInsId(String ins_id) throws SQLException{
		Vector<ModoDb> aux = new Vector<ModoDb>();
		
		pstSelByInsId.setString(1, ins_id);
		ResultSet resset = pstSelByInsId.executeQuery();

		ModoDb modo=null;
		while(resset.next()){
			modo = new ModoDb(resset);
			aux.add(modo);
		}
		return (ModoDb[])aux.toArray(new ModoDb[0]);
	}

	
}
