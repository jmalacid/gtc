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

package svo.gtc.db.programa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ProgramaAccess {

	Connection con = null;
	//SELECT
	private PreparedStatement pstSelProgramaById;
	private PreparedStatement pstCountProgramaById;
	private PreparedStatement pstInsPrograma;
	private PreparedStatement pstUpdMty;
	
	private String selPrograma=
		" SELECT prog_id, prog_title, prog_periodoprop, prog_year, prog_semester " +
		" 	FROM programa a ";

	private String selCountPrograma=
		" SELECT COUNT(*) " +
		" 	FROM programa ";

	private String insPrograma=
		" INSERT INTO programa (prog_id, prog_title, prog_periodoprop, prog_year, prog_semester) " +
		" 			VALUES(?,?,?,?,?) ";
	private String updMty=
			"update proddatos pro set mty_id=(select mty_id from submodo s where s.ins_id=pro.ins_id and s.mod_id=pro.mod_id and s.subm_id=pro.subm_id) where mty_id is null; ";

	public ProgramaAccess(Connection conex) throws SQLException{
		con = conex;
		//Ordenes Precompiladas --------------------------------------------------
		pstSelProgramaById = conex.prepareStatement(selPrograma+" WHERE prog_id= ?;");
		pstCountProgramaById = conex.prepareStatement(selCountPrograma+" WHERE prog_id= ?;");
		pstInsPrograma 		= conex.prepareStatement(insPrograma);
		pstUpdMty 		= conex.prepareStatement(updMty);
	}
	
	public ProgramaDb selectById(String prog_id) throws SQLException{
		pstSelProgramaById.setString(1, prog_id);
		ResultSet resset = pstSelProgramaById.executeQuery();

		ProgramaDb prog=null;
		if(resset.next()){
			prog = new ProgramaDb(resset);
		}

		return prog;
	}
	
	public int countById(String prog_id) throws SQLException{
		pstCountProgramaById.setString(1, prog_id);
		ResultSet resset = pstCountProgramaById.executeQuery();

		int count=0;
		if(resset.next()){
			count = resset.getInt(1);
		}

		return count;
		
	}
	

	public void insert(ProgramaDb prog) throws SQLException{
		pstInsPrograma.setString(1, prog.getProg_id());
		pstInsPrograma.setString(2, prog.getProg_title());
		if(prog.getProg_periodoprop()!=null){
			pstInsPrograma.setDate(3, prog.getProg_periodoprop());
		}else{
			pstInsPrograma.setNull(3, Types.DATE);
		}

		if(prog.getProg_year()!=null){
			pstInsPrograma.setInt(4, prog.getProg_year());
		}else{
			pstInsPrograma.setNull(4, Types.INTEGER);
		}

		if(prog.getProg_semester()!=null){
			pstInsPrograma.setString(5, prog.getProg_semester());
		}else{
			pstInsPrograma.setNull(5, Types.CHAR);
		}

		//System.out.println("insert: "+pstInsPrograma.toString());
		pstInsPrograma.execute();
	}
	public void updMty() throws SQLException{
		pstUpdMty.execute();
	}
	
}
