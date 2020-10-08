/*
 * @(#)UsuarioAccess.java    Jun 6, 2012
 *
 *
 * Raul Gutierrez Sanchez. (raul@cab.inta-csic.es)	
 * LAEFF: 	Centro de Astrobiología
 *        	INTA
 *			Villafranca del Castillo Satellite Tracking Station
 *			Villafranca del Castillo, E-28691 Villanueva de la Cañada
 *			Madrid - España
 *			
 * Phone:  34 91-8131260
 * Fax..:  34 91-8131160   
 * -----------------------------------------------------------------------
 * 
 *	ESTE  PROGRAMA  ES  DE  USO EXCLUSIVO.  ES  PROPIEDAD  DE  SU AUTOR  Rael 
 *  Gutierrez Senchez  Y SE PRESENTA "COMO ESTA" SIN NINGUN TIPO DE GARANTIAS
 *  DENTRO Y FUERA  DE LA FINALIDAD PARA  LA QUE EL  AUTOR LO  HIZO. EL AUTOR 
 *  PERMITE  SU USO SIEMPRE QUE  SE LE REFERENCIE. SE PERMITE LA MODIFICACION
 *  DE SU  FUENTE SIEMPRE QUE SE  HAGA REFERENCIA A SU AUTORIA Y JUNTO A ELLA
 *  SE  ADJUNTE LA IDENTIFICACION DEL  AUTOR DE LAS NUEVAS MODIFICACIONES. EL
 *  AUTOR  NUNCA SERA RESPONSABLE DEL USO DE ESTE  PROGRAMA NI DE LOS EFECTOS
 *  QUE ESTE PROGRAMA PUDIEDA CAUSAR EN DETERIOROS, ALTERACIONES Y/O PERDIDAS 
 *  DE INFORMACION.
 *	
 */

package svo.gtc.db.usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioAccess {

	Connection con = null;
	//SELECT
	private PreparedStatement pstSelById;
	private PreparedStatement pstSelByIdPw;
	private PreparedStatement pstCountById;
	private PreparedStatement pstIns;

	
	private String select=
		" SELECT usr_id, usr_name, usr_surname, usr_email, usr_passwd " +
		" 	FROM usuario a ";

	private String selCount=
		" SELECT COUNT(*) " +
		" 	FROM usuario ";

	private String ins = " INSERT INTO usuario (usr_id, usr_name, usr_surname, usr_email, usr_passwd) VALUES (?,?,?,?,?) ";
	
	public UsuarioAccess(Connection conex) throws SQLException{
		con = conex;
		//Ordenes Precompiladas --------------------------------------------------
		pstSelById 		= conex.prepareStatement(select+" WHERE usr_id=?;");
		pstSelByIdPw 	= conex.prepareStatement(select+" WHERE usr_id=? AND usr_passwd=?;");
		pstCountById 	= conex.prepareStatement(selCount+" WHERE usr_id=?");
		pstIns			= conex.prepareStatement(ins);
	}
	
	public UsuarioDb selectById(String usr_id) throws SQLException{
		pstSelById.setString(1, usr_id);
		ResultSet resset = pstSelById.executeQuery();

		UsuarioDb usr=null;
		if(resset.next()){
			usr = new UsuarioDb(resset);
		}
		return usr;
	}

	public UsuarioDb selectByIdPw(String usr_id,String usr_passwd) throws SQLException{
		pstSelByIdPw.setString(1, usr_id);
		pstSelByIdPw.setString(2, usr_passwd);
		ResultSet resset = pstSelByIdPw.executeQuery();

		UsuarioDb usr=null;
		if(resset.next()){
			usr = new UsuarioDb(resset);
		}
		return usr;
	}

	public int countById(String usr_id) throws SQLException{
		pstCountById.setString(1, usr_id);
		ResultSet resset = pstCountById.executeQuery();

		int count=0;
		if(resset.next()){
			count = resset.getInt(1);
		}
		return count;
	}
	
	public void insert(UsuarioDb usuario) throws SQLException{
		pstIns.setString(1, usuario.getUsrId());
		pstIns.setString(2, usuario.getUsrName());
		pstIns.setString(3, usuario.getUsrSurname());
		pstIns.setString(4, usuario.getUsrEmail());
		pstIns.setString(5, usuario.getUsrPasswd());
		
		pstIns.execute();
	}

}
