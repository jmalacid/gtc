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

package svo.gtc.db.filtro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class FiltroAccess {

	Connection con = null;
	//SELECT
	private PreparedStatement pstSelById;
	private PreparedStatement pstSelByName;
	private PreparedStatement pstCountByName;
	private PreparedStatement pstSelNewId;
	private PreparedStatement pstIns;

	
	private String select=
		" SELECT fil_id, fil_shortname, fil_name, fil_landacentral " +
		" 	FROM filtro a ";

	private String selCount=
		" SELECT COUNT(*) " +
		" 	FROM filtro ";

	private String selectNewId = " SELECT max(fil_id)+1 FROM filtro a ";

	private String ins = " INSERT INTO filtro (fil_id, fil_shortname, fil_name, fil_landacentral) VALUES (?,?,?,?) ";
	
	public FiltroAccess(Connection conex) throws SQLException{
		con = conex;
		//Ordenes Precompiladas --------------------------------------------------
		pstSelById 		= conex.prepareStatement(select+" WHERE fil_id=?");
		pstSelByName 	= conex.prepareStatement(select+" WHERE upper(fil_name)=upper(?)");
		pstCountByName 	= conex.prepareStatement(selCount+" WHERE upper(fil_name)=upper(?)");
		pstSelNewId 	= conex.prepareStatement(selectNewId);
		pstIns			= conex.prepareStatement(ins);
	}
	
	public FiltroDb selectById(Integer fil_id) throws SQLException{
		pstSelById.setInt(1, fil_id);
		ResultSet resset = pstSelById.executeQuery();

		FiltroDb fil=null;
		if(resset.next()){
			fil = new FiltroDb(resset);
		}
		return fil;
	}

	public FiltroDb selectByName(String fil_name) throws SQLException{
		pstSelByName.setString(1, fil_name);
		ResultSet resset = pstSelByName.executeQuery();

		FiltroDb fil=null;
		if(resset.next()){
			fil = new FiltroDb(resset);
		}
		return fil;
	}
	
	public int countByName(String fil_name) throws SQLException{
		pstCountByName.setString(1, fil_name);
		ResultSet resset = pstCountByName.executeQuery();

		int count=0;
		if(resset.next()){
			count = resset.getInt(1);
		}
		return count;
	}
	
	/**
	 * Obtiene un nuevo identificador primario.
	 * @return
	 * @throws SQLException
	 */
	public Integer selectNewId() throws SQLException{
		ResultSet resset = pstSelNewId.executeQuery();

		Integer salida = null;
		if(resset.next()){
			salida=resset.getInt(1);
		}
		return salida;
	}

	
	public void insert(FiltroDb filtro) throws SQLException{
		pstIns.setInt(1, filtro.getFilId().intValue());
		pstIns.setString(2, filtro.getFilShortname());
		pstIns.setString(3, filtro.getFilName());
		
		if(filtro.getFilLandacentral()!=null){
			pstIns.setDouble(4, filtro.getFilLandacentral());
		}else{
			pstIns.setNull(4, Types.DOUBLE);
		}
		
		
		pstIns.execute();
	}

}
