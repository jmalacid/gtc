/*
 * @(#)ProdDatosAccess.java    Aug 6, 2009
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExtCalAccess {

	Connection con = null;
	//SELECT
	private PreparedStatement pstCountByIdProgObl;
	private PreparedStatement pstIns;

	private String selCount=
		" SELECT COUNT(*) " +
		" 	FROM extcal a ";

	private String insert=
		" INSERT INTO extcal (prod_id, prog_id, obl_id) " +
		" 			VALUES(?,?,?) ";

	public ExtCalAccess(Connection conex) throws SQLException{
		con = conex;
		//Ordenes Precompiladas --------------------------------------------------
		pstCountByIdProgObl = conex.prepareStatement(selCount+" WHERE prod_id=? AND prog_id=? AND obl_id=?;");
		pstIns 		= conex.prepareStatement(insert);
	}
	
	
	public int countByIdProgObl(Integer prod_id, String prog_id, String obl_id) throws SQLException{
		pstCountByIdProgObl.setInt(1, prod_id.intValue());
		pstCountByIdProgObl.setString(2, prog_id);
		pstCountByIdProgObl.setString(3, obl_id);
		ResultSet resset = pstCountByIdProgObl.executeQuery();

		int count=0;
		if(resset.next()){
			count = resset.getInt(1);
		}

		return count;
		
	}
	
	public ExtCalDb insert(ExtCalDb extcal) throws SQLException{
			pstIns.setInt(1, extcal.getProdId());
			pstIns.setString(2, extcal.getProgId());
			pstIns.setString(3, extcal.getOblId());
			
			pstIns.execute();

			return extcal;
			
		}

}
