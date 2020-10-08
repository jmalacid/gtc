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

package svo.gtc.db.colecciondatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

public class ColeccionDatosAccess {

	Connection con = null;
	//SELECT
	private PreparedStatement pstSelById;
	private PreparedStatement pstSelByUsr;
	private PreparedStatement pstCountByName;
	private PreparedStatement pstIns;

	
	private String select=
		" SELECT usr_id, col_id, col_name, col_bibcode, col_datatype, col_redtype, col_desc " +
		" 	FROM colecciondatos a ";

	private String selCount=
		" SELECT COUNT(*) " +
		" 	FROM colecciondatos ";

	private String ins = " INSERT INTO colecciondatos (usr_id, col_id , col_name, col_bibcode, col_datatype, col_redtype, col_desc) " +
			"					VALUES (?," +
			"							greatest(0,(select max(col_id)+1 from colecciondatos where usr_id=?))," +
			"							?," +
			"							?," +
			"							?," +
			"							?," +
			"							?) ";
	
	public ColeccionDatosAccess(Connection conex) throws SQLException{
		con = conex;
		//Ordenes Precompiladas --------------------------------------------------
		pstSelById 		= conex.prepareStatement(select+" WHERE usr_id=? AND col_id=?;");
		pstSelByUsr		= conex.prepareStatement(select+" WHERE usr_id=?;");
		pstCountByName 	= conex.prepareStatement(selCount+" WHERE usr_id=? AND upper(col_name)=upper(?)");
		pstIns			= conex.prepareStatement(ins);
	}
	
	public ColeccionDatosDb selectById(String usr_id, Integer col_id) throws SQLException{
		pstSelById.setString(1, usr_id);
		pstSelById.setInt(2, col_id);
		ResultSet resset = pstSelById.executeQuery();

		ColeccionDatosDb usr=null;
		if(resset.next()){
			usr = new ColeccionDatosDb(resset);
		}
		return usr;
	}

	/**
	 * Devuelve todas las colecciones de datos de un usuario.
	 * @param usr_id
	 * @param col_id
	 * @return
	 * @throws SQLException
	 */
	public ColeccionDatosDb[] selectByUsr(String usr_id) throws SQLException{
		pstSelByUsr.setString(1, usr_id);
		ResultSet resset = pstSelByUsr.executeQuery();

		ColeccionDatosDb registro=null;
		Vector<ColeccionDatosDb> aux = new Vector<ColeccionDatosDb>();
		while(resset.next()){
			registro = new ColeccionDatosDb(resset);
			aux.add(registro);
		}
		
		return (ColeccionDatosDb[])aux.toArray(new ColeccionDatosDb[0]);
	}
	
	public void insert(ColeccionDatosDb col) throws SQLException{
		pstIns.setString(1, col.getUsrId());
		pstIns.setString(2, col.getUsrId());
		pstIns.setString(3, col.getColName());
		pstIns.setString(4, col.getColBibcode());
		pstIns.setString(5, col.getColDataType());
		pstIns.setString(6, col.getColRedType());
		
		if(col.getColDesc()==null){
			pstIns.setNull(7, Types.CHAR);
		}else{
			pstIns.setString(7, col.getColDesc());
		}
		
		pstIns.execute();
	}
	
	
	/**
	 * Cuenta el numero de colecciones que hay con un determinado nombre 
	 * (insensible a mayúsculas).
	 * 
	 * @param col_name
	 * @return
	 * @throws SQLException
	 */
	public int countByName(String usr_id, String col_name){
		int salida = 0;
		if(col_name!=null){
			try {
				pstCountByName.setString(1, usr_id);
				pstCountByName.setString(2, col_name.trim().toUpperCase());
				ResultSet resset = pstCountByName.executeQuery();

				if(resset.next()){
					salida = resset.getInt(1);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return salida;
	}


}
