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

import svo.gtc.db.filtro.FiltroAccess;
import svo.gtc.db.filtro.FiltroDb;
import svo.gtc.proddat.Filter;

public class ConfAccess {

	Connection con = null;
	//SELECT 
	private PreparedStatement pstSelById;
	private PreparedStatement pstSelCount;
	private PreparedStatement pstSelNewConfId;
	private PreparedStatement pstInsert;

	
	private String select=
		" SELECT det_id, ins_id, mod_id, subm_id, conf_id " +
		" 	FROM conf a ";

	private String selectNewConfId = " SELECT max(conf_id)+1 FROM conf a";

	private String selectCount=
		" SELECT COUNT(*) " +
		" 	FROM conf a ";

	private String insert = " INSERT INTO conf (det_id, ins_id, mod_id, subm_id, conf_id) VALUES (?,?,?,?,?) ";

	public ConfAccess(Connection conex) throws SQLException{
		con = conex;
		//Ordenes Precompiladas --------------------------------------------------
		pstSelById 		= conex.prepareStatement(select+" WHERE det_id=? AND ins_id=? AND mod_id=? AND subm_id=?;");
		pstSelCount		= conex.prepareStatement(selectCount+" WHERE det_id=? AND ins_id=? AND mod_id=? AND subm_id=?;");
		pstSelNewConfId	= conex.prepareStatement(selectNewConfId);
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
	public ConfDb[] selectByFilters(Integer det_id, String ins_id, String mod_id, String subm_id, Filter[] filters) throws SQLException{
		String select="SELECT det_id, ins_id, mod_id, subm_id, conf_id "+
							" 	FROM conffiltro a ";
		
		String selByFilters = "";
		
		// Nos quedamos con las configuraciones que tengan el mismo número de filtros.
		String join = "SELECT det_id, ins_id, mod_id, subm_id, conf_id " +
				"FROM (SELECT det_id, ins_id,mod_id, subm_id, conf_id, count(*) " +
				"		FROM conffiltro GROUP BY det_id, ins_id, mod_id, subm_id, conf_id having count(*)="+filters.length+") as foo " +
				" INTERSECT ";
 
		FiltroAccess filtroAccess 		= new FiltroAccess(con);
		
			for(int i=0; i<filters.length; i++){
				FiltroDb filtroDb = filtroAccess.selectByName(filters[i].getName().trim());
	
				int filId = filtroDb.getFilId().intValue();
				int cfilOrder = new Integer(filters[i].getOrder());
	
				
				selByFilters+=join+select+" WHERE fil_id="+filId+
						" AND det_id="+det_id.intValue()+
						" AND ins_id='"+StringEscapeUtils.escapeSql(ins_id)+"' "+
						" AND mod_id='"+StringEscapeUtils.escapeSql(mod_id)+"' "+
						" AND subm_id='"+StringEscapeUtils.escapeSql(subm_id)+"' "+
						" AND cfil_order="+cfilOrder;
				
				join=" INTERSECT ";
			}
		
		
			if(filters.length<1){
				// Si no hay filtros buscamos la configuración sin filtros
				selByFilters = "SELECT det_id, ins_id, mod_id, subm_id, conf_id "+
								"  FROM conf a WHERE " +
								"	det_id="+det_id.intValue()+
								"	AND ins_id='"+StringEscapeUtils.escapeSql(ins_id)+"' "+
								"	AND mod_id='"+StringEscapeUtils.escapeSql(mod_id)+"' "+
								"	AND subm_id='"+StringEscapeUtils.escapeSql(subm_id)+"' "+
								"	AND	(SELECT COUNT(*) " +
								"			FROM conffiltro b " +
								"			WHERE b.det_id=a.det_id " +
								"			AND   b.ins_id=a.ins_id " +
								"			AND   b.mod_id=a.mod_id " +
								"			AND   b.subm_id=a.subm_id " +
								"			AND   b.conf_id=a.conf_id ) = 0 ;"; 
			}
		
		
		Statement statement =  this.con.createStatement();
		
		
		ResultSet resset = statement.executeQuery(selByFilters);

		Vector<ConfDb> vcr = new Vector<ConfDb>();
		ConfDb conf=null;
		while(resset.next()){
			conf = new ConfDb(resset);
			vcr.add(conf);
		}
		ConfDb[] salida = (ConfDb[])vcr.toArray(new ConfDb[0]);
		
		return salida;
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
	
	
	/**
	 * Devuelve un valor de conf_id no utilizado.
	 * @return
	 * @throws SQLException
	 */
	public Integer selectNewConfId() throws SQLException{
		ResultSet resset = pstSelNewConfId.executeQuery();

		Integer salida = null;
		if(resset.next()){
			salida=resset.getInt(1);
		}
		return salida;
	}

	public ConfDb insert(ConfDb conf) throws SQLException{
		ConfDb salida = new ConfDb(conf);
		
		Integer conf_id = selectNewConfId();
		salida.setConfId(conf_id);
		
		pstInsert.setInt(1,conf.getDetId());
		pstInsert.setString(2, conf.getInsId());
		pstInsert.setString(3, conf.getModId());
		pstInsert.setString(4, conf.getSubmId());
		pstInsert.setInt(5, conf_id);
		pstInsert.execute();
		
		return salida;
	}
	
	
}
