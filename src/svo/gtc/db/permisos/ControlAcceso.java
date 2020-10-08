/*
 * @(#)ControlAcceso.java    Oct 16, 2009
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

package svo.gtc.db.permisos;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

import svo.gtc.db.prodat.ProdDatosDb;
import svo.gtc.db.usuario.UsuarioDb;
import svo.gtc.db.web.WebMainResult;

/**
 *@author J. Manuel Alacid (jmalacid@cab.inta-csic.es) ¡
*/
//Esta clase hace referencia al acceso a la tabla Object.


public class ControlAcceso{
	Connection conex = null;
	UsuarioDb    usuario=null;

	/*private String selHasPerm=
		"SELECT (SELECT count(*)>0 FROM progusuario WHERE prog_id=? AND user_id=?) " +
		"    OR (SELECT current_date>prog_periodoprop FROM programa WHERE prog_id=?) ";*/
	/*private String selHasPerm ="select (prod.prod_initime<now()-('1 year'::interval) or obl_pi=? or prod.prod_initime is null) from proddatos prod, obsblock ob "
			+ "where prod.prog_id=ob.prog_id and prod.obl_id=ob.obl_id and prod.prog_id=? and prod.obl_id=? and prod.prod_id=?";
	private String selHasPermDDT ="select (prod.prod_initime<now()-('6 month'::interval) or obl_pi=? or prod.prod_initime is null) from proddatos prod, obsblock ob "
			+ "where prod.prog_id=ob.prog_id and prod.obl_id=ob.obl_id and prod.prog_id=? and prod.obl_id=? and prod.prod_id=?";*/
	private String selHasPerm ="select (prod.prod_initime<now()-('1 year'::interval) or prog_pi=? or prod.prod_initime is null) from proddatos prod, programa pr "
			+ "where prod.prog_id=pr.prog_id and prod.prog_id=? and prod.obl_id=? and prod.prod_id=?";
	private String selHasPermDDT ="select (prod.prod_initime<now()-('6 month'::interval) or prog_pi=? or prod.prod_initime is null) from proddatos prod, programa pr "
			+ "where prod.prog_id=pr.prog_id and prod.prog_id=? and prod.obl_id=? and prod.prod_id=?";
	private String selPeriodoProp=
		"SELECT prog_periodoprop FROM programa WHERE prog_id=? ";
	
	private String selProd="select prod_id,prod_initime from proddatos where prog_id=? and obl_id=? and prod_initime is not null limit 1";
	
	PreparedStatement pstselHasPerm=null;
	PreparedStatement pstselHasPermDDT=null;
	PreparedStatement pstselPeriodoProp=null;
	PreparedStatement pstselProd=null;
   //Ordenes SQL
	
	public ControlAcceso(Connection conex, UsuarioDb usuario) throws SQLException{
		this.conex=conex;
		this.usuario=usuario;
		
		pstselHasPerm    = conex.prepareStatement(selHasPerm);
		pstselHasPermDDT    = conex.prepareStatement(selHasPermDDT);
		pstselPeriodoProp= conex.prepareStatement(selPeriodoProp);
		pstselProd    = conex.prepareStatement(selProd);
	}
	

	/** Comprueba si un determinado usuario tiene permiso de acceso sobre los productos
	 *  de un programa dado.
	 *  
	 * @param prog_id
	 * @param user_id
	 * @return True si el usuario tiene permiso, False en caso contrario.
	 */
	public boolean hasPerm(String prog, String obl, Integer prod, UsuarioDb usuario){
	//public boolean hasPerm(String prog_id, UsuarioDb usuario){
		//return true;
		
		try {
			//Si el usuario es super Usuario devolvemos true
			if(usuario!=null && usuario.getUsrId()!=null && usuario.getUsrId().equalsIgnoreCase("GTC_super")){
				return true;
			}
			
			//Si pertenece a programas DDT son 6 meses
			if(prog!=null && prog.contains("DDT")){
				if(usuario!=null && usuario.getUsrId()!=null){
					pstselHasPermDDT.setString(1, usuario.getUsrId().trim());
				}else{
					pstselHasPermDDT.setNull(1, java.sql.Types.CHAR);
				}
				
				pstselHasPermDDT.setString(2, prog);
				pstselHasPermDDT.setString(3, obl);
				pstselHasPermDDT.setInt(4, prod);
				
				//System.out.println("permiso: "+pstselHasPerm.toString());
				
				ResultSet ressel = pstselHasPermDDT.executeQuery();
				
				if(ressel.next()){
					return ressel.getBoolean(1);
				}else{
					return false;
				}
			//En caso contrario serán 12 meses	
			}else{
			
				if(usuario!=null && usuario.getUsrId()!=null){
					pstselHasPerm.setString(1, usuario.getUsrId().trim());
				}else{
					pstselHasPerm.setNull(1, java.sql.Types.CHAR);
				}
				
				pstselHasPerm.setString(2, prog);
				pstselHasPerm.setString(3, obl);
				pstselHasPerm.setInt(4, prod);
				
				//System.out.println("permiso: "+pstselHasPerm.toString());
				
				ResultSet ressel = pstselHasPerm.executeQuery();
				
				if(ressel.next()){
					return ressel.getBoolean(1);
				}else{
					return false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public boolean hasPerm(String prog, String obl, UsuarioDb usuario){
		//public boolean hasPerm(String prog_id, UsuarioDb usuario){
			
		try {
			//Si el usuario es super Usuario devolvemos true
			if(usuario!=null && usuario.getUsrId()!=null && usuario.getUsrId().equalsIgnoreCase("GTC_super")){
				return true;
			}
	
			//Cuando se trata de un prog/obl obtenemos la fecha con un producto cualquiera y vemos si tiene permiso
			Integer prod=null;
			pstselProd.setString(1, prog);
			pstselProd.setString(2, obl);
			ResultSet resselProd = pstselProd.executeQuery();
			if(resselProd.next()){
				prod=resselProd.getInt("prod_id");
			}
				
			//Si pertenece a programas DDT son 6 meses
			if(prog.contains("DDT")){
				if(usuario!=null && usuario.getUsrId()!=null){
					pstselHasPermDDT.setString(1, usuario.getUsrId().trim());
				}else{
					pstselHasPermDDT.setNull(1, java.sql.Types.CHAR);
				}
				
				pstselHasPermDDT.setString(2, prog);
				pstselHasPermDDT.setString(3, obl);
				pstselHasPermDDT.setInt(4, prod);
				
				//System.out.println("permiso: "+pstselHasPerm.toString());
				
				ResultSet ressel = pstselHasPermDDT.executeQuery();
				
				if(ressel.next()){
					return ressel.getBoolean(1);
				}else{
					return false;
				}
			//En caso contrario serán 12 meses	
			}else{
			
				if(usuario!=null && usuario.getUsrId()!=null){
					pstselHasPerm.setString(1, usuario.getUsrId().trim());
				}else{
					pstselHasPerm.setNull(1, java.sql.Types.CHAR);
				}
				
				pstselHasPerm.setString(2, prog);
				pstselHasPerm.setString(3, obl);
				pstselHasPerm.setInt(4, prod);
				
				ResultSet ressel = pstselHasPerm.executeQuery();
				
				if(ressel.next()){
					return ressel.getBoolean(1);
				}else{
					return false;
				}
			}
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
			
		}
	
	
	/**
	 * Devuelve la fecha de fin de propiedad de un programa determinado.
	 * @param prog_id
	 * @return
	 */
	public java.sql.Date getPeriodoProp(String prog_id){
		try {
			pstselPeriodoProp.setString(1, prog_id);
			
			ResultSet ressel = pstselPeriodoProp.executeQuery();
			
			if(ressel.next()){
				Date salida = ressel.getDate("prog_periodoprop");
				if(ressel.wasNull()){
					return null;
				}else{
					return salida;
				}
			}else{
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
