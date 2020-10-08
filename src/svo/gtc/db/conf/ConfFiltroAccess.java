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

package svo.gtc.db.conf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import org.apache.commons.lang.StringEscapeUtils;

import svo.gtc.db.filtro.FiltroDb;

public class ConfFiltroAccess {

	Connection con = null;
	//SELECT
	private PreparedStatement pstSelById;
	private PreparedStatement pstSelCount;
	private PreparedStatement pstInsert;
	
	private String select=
		" SELECT det_id, ins_id, mod_id, subm_id, conf_id " +
		" 	FROM conf a ";
	
	private String selectCount=
		" SELECT COUNT(*) " +
		" 	FROM conf a ";

	private String insert=
		" INSERT INTO conffiltro (fil_id, det_id, ins_id, mod_id, subm_id, conf_id, cfil_order) VALUES (?,?,?,?,?,?,?)";

	public ConfFiltroAccess(Connection conex) throws SQLException{
		con = conex;
		//Ordenes Precompiladas --------------------------------------------------
		pstSelById 		= conex.prepareStatement(select+" WHERE det_id=? AND ins_id=? AND mod_id AND subm_id=?;");
		pstSelCount		= conex.prepareStatement(selectCount+" WHERE det_id=? AND ins_id=? AND mod_id=? AND subm_id=? ;");
		pstInsert		= conex.prepareStatement(insert);
	}
	
	public ConfDb[] selectById(Integer det_id, String ins_id, String mod_id, String subm_id) throws SQLException{
		pstSelById.setInt(1, det_id);
		pstSelById.setString(2, ins_id);
		pstSelById.setString(3, mod_id);
		pstSelById.setString(4, subm_id);
		ResultSet resset = pstSelById.executeQuery();

		Vector<ConfDb> salida = new Vector<ConfDb>();
		ConfDb conf=null;
		while(resset.next()){
			conf = new ConfDb(resset);
			salida.add(conf);
		}
		return (ConfDb[])salida.toArray(new ConfDb[0]);
	}

	/**
	 * Devuelve las configuraciones que cumplen tener los filtros indicados en el orden indicado.
	 * @param det_id
	 * @param ins_id
	 * @param mod_id
	 * @param filters
	 * @return
	 * @throws SQLException
	 */
	public ConfDb[] selectByFilters(Integer det_id, String ins_id, String mod_id, String subm_id, FiltroDb[] filters) throws SQLException{

		String selByFilters="";
		String join = "";
		
		for(int i=0; i<filters.length; i++){
			int filId = filters[i].getFilId().intValue();
			int cfilOrder = new Integer(i+1);
			
			
			selByFilters+=join+select+" WHERE fil_id="+filId+
					"AND det_id="+det_id.intValue()+
					" AND ins_id='"+StringEscapeUtils.escapeSql(ins_id)+
					"' AND mod_id='"+StringEscapeUtils.escapeSql(mod_id)+
					"' AND subm_id='"+StringEscapeUtils.escapeSql(subm_id)+
					"' AND cfil_order="+cfilOrder;
			
			join=" INTERSECT ";
		}
		
		Statement statement =  this.con.createStatement();
		
		ResultSet resset = statement.executeQuery(selByFilters);

		Vector<ConfDb> salida = new Vector<ConfDb>();
		ConfDb conf=null;
		while(resset.next()){
			conf = new ConfDb(resset);
			salida.add(conf);
		}
		return (ConfDb[])salida.toArray(new ConfDb[0]);
	}

	
	
	public int count(Integer det_id, String ins_id, String mod_id, String subm_id) throws SQLException{
		pstSelCount.setInt(1, det_id);
		pstSelCount.setString(2, ins_id);
		pstSelCount.setString(3, mod_id);
		pstSelCount.setString(4, subm_id);
		ResultSet resset = pstSelCount.executeQuery();

		int count=0;
		if(resset.next()){
			count = resset.getInt(1);
		}
		return count;
	}

	public void Insert(ConfFiltroDb  confFiltro) throws SQLException{
		pstInsert.setInt(1, confFiltro.getFilId());
		pstInsert.setInt(2, confFiltro.getDetId());
		pstInsert.setString(3, confFiltro.getInsId());
		pstInsert.setString(4, confFiltro.getModId());
		pstInsert.setString(5, confFiltro.getSubmId());
		pstInsert.setInt(6, confFiltro.getConfId());
		pstInsert.setInt(7, confFiltro.getCfilOrder());
		
		pstInsert.execute();
	}
	

}
