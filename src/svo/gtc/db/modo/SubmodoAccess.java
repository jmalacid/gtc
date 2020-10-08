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

public class SubmodoAccess {

	Connection con = null;
	//SELECT
	private PreparedStatement pstSelById;
	//private PreparedStatement pstSelByName;
	private PreparedStatement pstCountById;

	
	private String select=
		" SELECT ins_id, mod_id, subm_id, mty_id, subm_name, subm_shortname, subm_desc " +
		" 	FROM submodo a ";

	private String selCount=
		" SELECT COUNT(*) " +
		" 	FROM submodo ";

	public SubmodoAccess(Connection conex) throws SQLException{
		con = conex;
		//Ordenes Precompiladas --------------------------------------------------
		pstSelById 		= conex.prepareStatement(select+" WHERE ins_id=? AND mod_id=? AND subm_id=?;");
		//pstSelByName 	= conex.prepareStatement(select+" WHERE upper(mod_name)=upper(?);");
		pstCountById 	= conex.prepareStatement(selCount+" WHERE ins_id=? AND mod_id=? AND subm_id=?;");
	}
	
	public SubmodoDb selectById(String ins_id, String mod_id, String subm_id) throws SQLException{
		pstSelById.setString(1, ins_id);
		pstSelById.setString(2, mod_id);
		pstSelById.setString(3, subm_id);
		ResultSet resset = pstSelById.executeQuery();

		SubmodoDb submodo=null;
		if(resset.next()){
			submodo = new SubmodoDb(resset);
		}
		return submodo;
	}

	/*
	public ModoDb selectByName(String mod_name) throws SQLException{
		pstSelByName.setString(1, mod_name);
		ResultSet resset = pstSelByName.executeQuery();

		ModoDb modo=null;
		if(resset.next()){
			modo = new ModoDb(resset);
		}
		return modo;
	}
	*/
	
	public int countById(String ins_id, String mod_id, String subm_id) throws SQLException{
		pstCountById.setString(1, ins_id);
		pstCountById.setString(2, mod_id);
		pstCountById.setString(3, subm_id);
		ResultSet resset = pstCountById.executeQuery();

		int count=0;
		if(resset.next()){
			count = resset.getInt(1);
		}
		return count;
	}
	
}
