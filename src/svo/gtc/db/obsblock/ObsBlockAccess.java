/*
 * @(#)ProdDatosAccess.java    Feb 17, 2011
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ObsBlockAccess {

	Connection con = null;
	//SELECT
	private PreparedStatement pstSelById;
	private PreparedStatement pstCountById;
	private PreparedStatement pstIns;
	
	private PreparedStatement pstSelModId;
	
	
	private String selObsBlock=
		" SELECT prog_id, obl_id, obl_pi " +
		" 	FROM obsblock a ";

	private String selCountObsBlock=
		" SELECT COUNT(*) " +
		" 	FROM obsblock ";

	private String insObsBlock=
		" INSERT INTO obsblock (prog_id, obl_id, obl_pi) " +
		" 			VALUES(?,?,?) ";

	private String selModId=
		" SELECT mod_id " +
		" 	FROM proddatos p " +
		"	WHERE p.prog_id=? " +
		"	AND   p.obl_id=? " +
		"	LIMIT 1 ";

	public ObsBlockAccess(Connection conex) throws SQLException{
		con = conex;
		//Ordenes Precompiladas --------------------------------------------------
		pstSelById = conex.prepareStatement(selObsBlock+" WHERE prog_id=? AND obl_id=?;");
		pstCountById = conex.prepareStatement(selCountObsBlock+" WHERE prog_id=? AND obl_id=?;");
		pstIns 		= conex.prepareStatement(insObsBlock);
		pstSelModId	= conex.prepareStatement(selModId);
	}
	
	public ObsBlockDb selectById(String prog_id, String obl_id) throws SQLException{
		pstSelById.setString(1, prog_id);
		pstSelById.setString(2, obl_id);
		ResultSet resset = pstSelById.executeQuery();

		ObsBlockDb obl=null;
		if(resset.next()){
			obl = new ObsBlockDb(resset);
		}
		return obl;
	}
	
	public int countById(String prog_id, String obl_id) throws SQLException{
		pstCountById.setString(1, prog_id);
		pstCountById.setString(2, obl_id);
		ResultSet resset = pstCountById.executeQuery();

		int count=0;
		if(resset.next()){
			count = resset.getInt(1);
		}
		return count;
	}
	

	public void insert(ObsBlockDb obl) throws SQLException{
		pstIns.setString(1, obl.getProg_id());
		pstIns.setString(2, obl.getObl_id());
		if(obl.getObl_pi()!=null && obl.getObl_pi().trim().length()>0){
			pstIns.setString(3, obl.getObl_pi().trim());
		}else{
			pstIns.setNull(3, Types.CHAR);
		}
		
		pstIns.execute();
	}
	
	public String selectModId(String prog_id, String obl_id) throws SQLException{
		pstSelModId.setString(1, prog_id);
		pstSelModId.setString(2, obl_id);
		
		ResultSet resset = pstSelModId.executeQuery();

		String modId=null;
		if(resset.next()){
			modId = resset.getString("mod_id").trim();
		}
		return modId;
	}
		
	
}
