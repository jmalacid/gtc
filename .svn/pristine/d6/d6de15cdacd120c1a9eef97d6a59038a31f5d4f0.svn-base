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

package svo.gtc.db.instrument;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class InstrumentoAccess {

	Connection con = null;
	//SELECT
	private PreparedStatement pstSelById;
	private PreparedStatement pstSelByName;
	private PreparedStatement pstCountByName;
	private PreparedStatement pstInsert;
	
	
	
	private String select=
		" SELECT ins_id, ins_name, ins_tipo, ins_foco " +
		" 	FROM instrumento a ";

	private String selCount=
		" SELECT COUNT(*) " +
		" 	FROM instrumento ";

	private String insert=
		" INSERT INTO instrumento (ins_id, ins_name, ins_tipo, ins_foco) " +
		" 			VALUES(?,?,?,?) ";

	public InstrumentoAccess(Connection conex) throws SQLException{
		con = conex;
		//Ordenes Precompiladas --------------------------------------------------
		pstSelById 		= conex.prepareStatement(select+" WHERE ins_id=?;");
		pstSelByName 	= conex.prepareStatement(select+" WHERE upper(ins_name)=upper(?);");
		pstCountByName 	= conex.prepareStatement(selCount+" WHERE upper(ins_name)=upper(?)");
		pstInsert 		= conex.prepareStatement(insert);
	}
	
	public InstrumentoDb selectById(String ins_id) throws SQLException{
		pstSelById.setString(1, ins_id);
		ResultSet resset = pstSelById.executeQuery();

		InstrumentoDb ins=null;
		if(resset.next()){
			ins = new InstrumentoDb(resset);
		}
		return ins;
	}

	public InstrumentoDb selectByName(String ins_name) throws SQLException{
		pstSelByName.setString(1, ins_name);
		ResultSet resset = pstSelByName.executeQuery();

		InstrumentoDb ins=null;
		if(resset.next()){
			ins = new InstrumentoDb(resset);
		}
		return ins;
	}
	
	public int countByName(String ins_name) throws SQLException{
		pstCountByName.setString(1, ins_name);
		ResultSet resset = pstCountByName.executeQuery();

		int count=0;
		if(resset.next()){
			count = resset.getInt(1);
		}
		return count;
	}
	

	public void insert(InstrumentoDb ins) throws SQLException{
		pstInsert.setString(1, ins.getInsId());
		
		// Name
		if(ins.getInsName()!=null && ins.getInsName().trim().length()>0){
			pstInsert.setString(2, ins.getInsName());
		}else{
			pstInsert.setNull(2, Types.CHAR);
		}

		// Tipo
		if(ins.getInsTipo()!=null && ins.getInsTipo().trim().length()>0){
			pstInsert.setString(3, ins.getInsTipo());
		}else{
			pstInsert.setNull(3, Types.CHAR);
		}

		// Foco
		if(ins.getInsFoco()!=null){
			pstInsert.setDouble(4, ins.getInsFoco());
		}else{
			pstInsert.setNull(4, Types.DOUBLE);
		}

		pstInsert.execute();
	}
		
	
}
