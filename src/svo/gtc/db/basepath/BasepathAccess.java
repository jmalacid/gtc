/*
 * @(#)BasepathAccess.java    Feb 15, 2011
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

package svo.gtc.db.basepath;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import svo.gtc.db.usuario.UserPI;

public class BasepathAccess {

	Connection con = null;
	//SELECT
	private PreparedStatement pstSelBpathId;
	private PreparedStatement pstSelBpathById;
	private PreparedStatement pstIsOblPriv;
	private PreparedStatement pstIsOblPrivDDT;
	private PreparedStatement pstSelPI;
	private PreparedStatement pstAddPI;
	
	private String selBpathId=
		" SELECT bpath_id " +
		" 	FROM basepath WHERE bpath_path=? ";

	private String selBpathById=
		" SELECT bpath_id,bpath_path " +
		" 	FROM basepath WHERE bpath_id=? ";
	private String isOblPriv = "select (max(prod_initime)<now()-('1year'::interval)) from proddatos prod where prod.prog_id=? and prod.obl_id=?";
	private String isOblPrivDDT = "select (max(prod_initime)<now()-('6 month'::interval)) from proddatos prod where prod.prog_id=? and prod.obl_id=?";
	private String selPI = "select usr_id,usr_email, usr_est from programa join usuario on prog_pi=usr_id and prog_id=?";
	private String addPI = "update obsblock set obl_pi=? where prog_id=?";

	public BasepathAccess(Connection conex) throws SQLException{
		con = conex;
		//Ordenes Precompiladas --------------------------------------------------
		pstSelBpathId 	= conex.prepareStatement(selBpathId);
		pstSelBpathById = conex.prepareStatement(selBpathById);
		pstIsOblPriv = conex.prepareStatement(isOblPriv);
		pstIsOblPrivDDT = conex.prepareStatement(isOblPrivDDT);
		pstSelPI = conex.prepareStatement(selPI);
		pstAddPI = conex.prepareStatement(addPI);
	}
	
	
	/**
	 * Devuelve el identificador de BasePath para un path determinado.
	 * @param bpath
	 * @return
	 * @throws SQLException
	 */
	public Integer selectBpathId(String bpath) throws SQLException{
		// Quitamos la/s barras del final.
		String bpathAux = bpath.replaceAll("^(.+[^/])[/]*$", "$1");

		pstSelBpathId.setString(1, bpathAux);
		ResultSet resset = pstSelBpathId.executeQuery();
		
		Integer pathId=null;
		if(resset.next()){
			pathId = new Integer(resset.getInt(1));
		}
				
		return pathId;
	}

	
	/**
	 * 
	 * @param bpathId
	 * @return
	 * @throws SQLException
	 */
	public BasepathDb selectBpathById(Integer bpathId) throws SQLException{
		pstSelBpathById.setInt(1, bpathId);
		ResultSet resset = pstSelBpathById.executeQuery();
		
		BasepathDb bpathDb=null;
		if(resset.next()){
			bpathDb = new BasepathDb(resset);
		}
				
		return bpathDb;
	}

	
//Métodos añadidos para ver la privacidad de los datos que nos llegan	
	public boolean isOblPriv(String prog, String obl) throws SQLException{
		
		//Si pertenece a programas DDT son 6 meses
		if(prog.contains("DDT")){
			
			pstIsOblPrivDDT.setString(1, prog);
			pstIsOblPrivDDT.setString(2, obl);
			
			ResultSet ressel = pstIsOblPrivDDT.executeQuery();
			
			if(ressel.next()){
				return ressel.getBoolean(1);
			}else{
				return false;
			}
			
		//En caso contrario serán 12 meses	
		}else{
		
			pstIsOblPriv.setString(1, prog);
			pstIsOblPriv.setString(2, obl);
			
			ResultSet ressel = pstIsOblPriv.executeQuery();
			
			if(ressel.next()){
				return ressel.getBoolean(1);
			}else{
				return false;
			}
		}
	}
	
	public UserPI selPI(String prog) throws SQLException{
		
		pstSelPI.setString(1, prog);
		ResultSet resset = pstSelPI.executeQuery();
		
		UserPI pi= null;
		
		if(resset.next()){
			pi = new UserPI(resset);
		}
				
		return pi;
		
	}
	
	public void addPI(String prog, String pi) throws SQLException{
		pstAddPI.setString(1, pi);
		pstAddPI.setString(2, prog);
		
		pstAddPI.executeUpdate();
		
	}

}
