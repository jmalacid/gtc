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

package svo.gtc.db.detector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DetectorAccess {

	Connection con = null;
	//SELECT
	private PreparedStatement pstSelById;
	private PreparedStatement pstSelByShortName;
	private PreparedStatement pstCountByShortName;

	
	private String select=
		" SELECT det_id, ins_id, det_shortname, det_name, det_desc " +
		" 	FROM detector a ";

	private String selCount=
		" SELECT COUNT(*) " +
		" 	FROM detector ";

	public DetectorAccess(Connection conex) throws SQLException{
		con = conex;
		//Ordenes Precompiladas --------------------------------------------------
		pstSelById 		= conex.prepareStatement(select+" WHERE det_id=?;");
		pstSelByShortName 	= conex.prepareStatement(select+" WHERE upper(det_shortname)=upper(?);");
		pstCountByShortName 	= conex.prepareStatement(selCount+" WHERE upper(det_name)=upper(?)");
	}
	
	public DetectorDb selectById(Integer det_id) throws SQLException{
		pstSelById.setInt(1, det_id);
		ResultSet resset = pstSelById.executeQuery();

		DetectorDb det=null;
		if(resset.next()){
			det = new DetectorDb(resset);
		}
		return det;
	}

	public DetectorDb selectByShortName(String det_name) throws SQLException{
		//Quitamos todo lo que no sean letras o numeros.
		pstSelByShortName.setString(1, det_name.replaceAll("[^a-zA-Z0-9]", ""));
		
		ResultSet resset = pstSelByShortName.executeQuery();

		DetectorDb det=null;
		if(resset.next()){
			det = new DetectorDb(resset);
		}
		return det;
	}
	
	public int countByShortName(String det_name) throws SQLException{
		pstCountByShortName.setString(1, det_name);
		ResultSet resset = pstCountByShortName.executeQuery();

		int count=0;
		if(resset.next()){
			count = resset.getInt(1);
		}
		return count;
	}
	
}
