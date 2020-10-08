package svo.gtc.db.web;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import svo.gtc.db.logfile.LogFileAccess;
import svo.gtc.db.logfile.LogFileDb;
import svo.gtc.db.prodat.ProdDatosAccess;
import svo.gtc.db.prodat.ProdDatosDb;
import svo.gtc.db.prodat.WarningDb;
import svo.gtc.db.proderr.ProdErrorAccess;
import svo.gtc.db.proderr.ProdErrorDb;
import svo.gtc.db.prodred.ProdRedAccess;
import svo.gtc.db.prodred.ProdRedDb;

import org.apache.commons.lang.StringEscapeUtils;

/**
 *@author Raul Gutierrez Sanchez (raul@cab.inta-csic.es)
*/
//Esta clase hace referencia al acceso a la tabla Object. 


public class WebSearcher{
	Connection conex = null;
	ResultSet resSet = null;
	
	public WebSearcher(Connection conex) throws SQLException{
		this.conex=conex;
	}
	
	/** 
	 * Selecciona todos los productos de un tipo determinado y de un bloque de observación.
	 * @param prog_id
	 * @param obl_id
	 * @param ins_id
	 * @param mod_id
	 * @return
	 * @throws SQLException
	 */
	
	public ProdDatosDb[] selectProdsByOblId(String prog_id, String obl_id, String ins_id, String modType, String ini, String end) throws SQLException{
		ProdDatosAccess prodDatosAccess = new ProdDatosAccess(this.conex);
		
		String query = ProdDatosAccess.getInicioSelect()+
				" FROM proddatos prod, basepath b, conf, submodo subm, modo mod " +
				" WHERE prod.bpath_id = b.bpath_id " +
				" AND   prod.det_id   = conf.det_id " +
				" AND   prod.ins_id   = conf.ins_id " +
				" AND   prod.mod_id   = conf.mod_id " +
				" AND   prod.subm_id  = conf.subm_id " +
				" AND   prod.conf_id  = conf.conf_id " +
				" AND   conf.ins_id   = subm.ins_id " +
				" AND   conf.mod_id   = subm.mod_id " +
				" AND   conf.subm_id  = subm.subm_id "+
				" AND   conf.mod_id  = mod.mod_id "+
				" AND   conf.ins_id  = mod.ins_id ";

		if(ini!=null && end != null  && modType.equals("CAL")){
			query += " AND ((prog_id = '"+StringEscapeUtils.escapeSql(prog_id.trim())+"' AND "
					+ "obl_id like '"+StringEscapeUtils.escapeSql(obl_id.trim().substring(0,4))+"%'"
					//+ "AND prod.ins_id = '"+StringEscapeUtils.escapeSql(ins_id.trim())+"'"
					+ "AND subm.mty_id = '"+StringEscapeUtils.escapeSql(modType.trim())+"')"
					+ " OR (prog_id = '"+StringEscapeUtils.escapeSql(prog_id.trim())+"' AND "
					+ "((prod_initime >'"+StringEscapeUtils.escapeSql(ini.split(" ")[0])+" 00:00:00' AND "
					+ "prod_initime <'"+StringEscapeUtils.escapeSql(ini.split(" ")[0])+" 23:59:59') OR "
					+ "(prod_endtime > '"+StringEscapeUtils.escapeSql(end.split(" ")[0])+" 00:00:00' AND "
					+ "prod_endtime < '"+StringEscapeUtils.escapeSql(end.split(" ")[0])+" 23:59:59')) AND prod.subm_id like 'SS%'))";
			
		}else{
			if(prog_id!=null && prog_id.trim().length()>0){
				query += " AND prog_id = '"+StringEscapeUtils.escapeSql(prog_id.trim())+"' "; 
			}
			
			if(obl_id!=null && obl_id.trim().length()>0){
				query += " AND obl_id like '"+StringEscapeUtils.escapeSql(obl_id.trim().substring(0,4))+"%' ";
			}
	
			if(ins_id!=null && ins_id.trim().length()>0){
				query += " AND prod.ins_id = '"+StringEscapeUtils.escapeSql(ins_id.trim())+"' "; 
			}
			
			if(modType!=null && modType.trim().length()>0){
				query += " AND subm.mty_id = '"+StringEscapeUtils.escapeSql(modType.trim())+"' "; 
			}
		}
		
		//System.out.println(query+" ORDER BY prod_id, ins_id, mod_id, prod_filename");
		
		ResultSet resset = conex.createStatement().executeQuery(query+" ORDER BY prod_id, mod_id, subm_id");

		
		
		Vector<ProdDatosDb> aux = new Vector<ProdDatosDb>();
		
		while(resset.next()){
			ProdDatosDb prod = new ProdDatosDb(resset);
			
			WarningDb[] warnings = prodDatosAccess.selectWarningsById(prog_id, obl_id, prod.getProdId());
			prod.setWarnings(warnings);
			
			aux.add(prod);
		}
		
		resset.close();

		return (ProdDatosDb[])aux.toArray(new ProdDatosDb[0]);
		
		
	}
	
	/** 
	 * Selecciona todos los productos de un tipo determinado y de un bloque de observación.
	 * @param prog_id
	 * @param obl_id
	 * @param ins_id
	 * @param mod_id
	 * @return
	 * @throws SQLException
	 */
	
	public ProdDatosDb[] selectProdsByOblId(String prog_id, String obl_id, String ins_id, String modType) throws SQLException{
		ProdDatosAccess prodDatosAccess = new ProdDatosAccess(this.conex);
		
		String query = ProdDatosAccess.getInicioSelect()+
				" FROM proddatos prod, basepath b, conf, submodo subm, modo mod " +
				" WHERE prod.bpath_id = b.bpath_id " +
				" AND   prod.det_id   = conf.det_id " +
				" AND   prod.ins_id   = conf.ins_id " +
				" AND   prod.mod_id   = conf.mod_id " +
				" AND   prod.subm_id  = conf.subm_id " +
				" AND   prod.conf_id  = conf.conf_id " +
				" AND   conf.ins_id   = subm.ins_id " +
				" AND   conf.mod_id   = subm.mod_id " +
				" AND   conf.subm_id  = subm.subm_id "+
				" AND   conf.mod_id  = mod.mod_id "+
				" AND   conf.ins_id  = mod.ins_id ";


			if(prog_id!=null && prog_id.trim().length()>0){
				query += " AND prog_id = '"+StringEscapeUtils.escapeSql(prog_id.trim())+"' "; 
			}
			
			if(obl_id!=null && obl_id.trim().length()>0){
				query += " AND obl_id like '"+StringEscapeUtils.escapeSql(obl_id.trim().substring(0,4))+"%' ";
			}
	
			if(ins_id!=null && ins_id.trim().length()>0){
				query += " AND prod.ins_id = '"+StringEscapeUtils.escapeSql(ins_id.trim())+"' "; 
			}
			
			if(modType!=null && modType.trim().length()>0){
				query += " AND subm.mty_id = '"+StringEscapeUtils.escapeSql(modType.trim())+"' "; 
			}
		
		
		
		ResultSet resset = conex.createStatement().executeQuery(query+" ORDER BY prod_id, mod_id, subm_id");

		//System.out.println(query+" ORDER BY ins_id, mod_id, prod_filename");
		
		Vector<ProdDatosDb> aux = new Vector<ProdDatosDb>();
		
		while(resset.next()){
			ProdDatosDb prod = new ProdDatosDb(resset);
			
			WarningDb[] warnings = prodDatosAccess.selectWarningsById(prog_id, obl_id, prod.getProdId());
			prod.setWarnings(warnings);
			
			aux.add(prod);
		}
		
		resset.close();

		return (ProdDatosDb[])aux.toArray(new ProdDatosDb[0]);
		
		
	}

	/**
	 * Busca todos los productos reducidos asociados a un determinado producto de datos.
	 * @param prog_id
	 * @param obl_id
	 * @param prod_id
	 * @return
	 * @throws SQLException
	 */
	public ProdRedDb[] selectRedByProdId(String prog_id, String obl_id, Integer prod_id, String type) throws SQLException{
		String query = ProdRedAccess.getInicioSelect()+
				" FROM pred_prod pprod, prodreducido p, basepath b " +
				" WHERE pprod.pred_id =  p.pred_id " +
				" AND   p.bpath_id = b.bpath_id";

		if(type!=null && type.equalsIgnoreCase("HERVE")){
			 query += " and (p.pred_type='HERVE' or p.pred_type='HiPER') ";
		}else{
			query += " and (p.pred_type!='HERVE' or p.pred_type!='HiPER') ";
		}
		if(type!=null && type.equalsIgnoreCase("MEG")){
			 query += " and  (p.pred_type='MEG' or p.pred_type='HOR_C' or p.pred_type='HOR_S')";
		}else{
			query += " and (p.pred_type!='MEG' or p.pred_type='HOR_C' or p.pred_type='HOR_S') ";
		}
		
		if(prog_id!=null && prog_id.trim().length()>0){
			query += " AND pprod.prog_id = '"+StringEscapeUtils.escapeSql(prog_id.trim())+"' "; 
		}
		
		if(obl_id!=null && obl_id.trim().length()>0){
			query += " AND pprod.obl_id like '"+StringEscapeUtils.escapeSql(obl_id.trim().substring(0,4))+"%' ";
		}

		if(prod_id!=null){
			query += " AND pprod.prod_id = "+prod_id+" "; 
		}
		
		//System.out.println(query+" ORDER BY p.col_id, p.pred_id");
		
		ResultSet resset = conex.createStatement().executeQuery(query+" ORDER BY p.col_id, p.pred_id");

		Vector<ProdRedDb> aux = new Vector<ProdRedDb>();
		
		while(resset.next()){
			ProdRedDb prod = new ProdRedDb(resset);
			
			aux.add(prod);
		}
		
		resset.close();

		return (ProdRedDb[])aux.toArray(new ProdRedDb[0]);
		
		
	}

	/** 
	 * 
	 * Obtiene todos los productos erroneos de un bloque de observación
	 * 
	 * @param prog_id
	 * @param obl_id
	 * @param ins_id
	 * @param mod_id
	 * @return
	 * @throws SQLException
	 */
	
	public ProdErrorDb[] selectErrsByOblId(String prog_id, String obl_id) throws SQLException{
		String query = ProdErrorAccess.getInicioSelect();
		
		if(prog_id!=null && prog_id.trim().length()>0){
			query += " AND prog_id = '"+StringEscapeUtils.escapeSql(prog_id.trim())+"' "; 
		}
		
		if(obl_id!=null && obl_id.trim().length()>0){
			query += " AND obl_id like '"+StringEscapeUtils.escapeSql(obl_id.trim().substring(0,4))+"%' ";
		}
		
		ResultSet resset = conex.createStatement().executeQuery(query+" order by prod_id");

		//System.out.println(query+" ORDER BY ins_id, mod_id, prod_filename");
		
		Vector<ProdErrorDb> aux = new Vector<ProdErrorDb>();
		
		while(resset.next()){
			ProdErrorDb prod = new ProdErrorDb(resset);
			aux.add(prod);
		}
		
		resset.close();

		return (ProdErrorDb[])aux.toArray(new ProdErrorDb[0]);
		
		
	}

	/**
	 * Devuelve todos los productos que han sido marcados con Warnings de un OB determinado.
	 * 
	 * @param prog_id
	 * @param obl_id
	 * @return
	 * @throws SQLException
	 */
	public ProdDatosDb[] selectProductsWithWarningsByOblId(String prog_id, String obl_id) throws SQLException{
		ProdDatosAccess prodDatosAccess = new ProdDatosAccess(this.conex);
		
		String query = ProdDatosAccess.getInicioSelect()+
				" FROM proddatos prod, basepath b, conf, submodo subm, prod_warning warn, modo mod " +
				" WHERE prod.bpath_id = b.bpath_id " +
				" AND   prod.det_id   = conf.det_id " +
				" AND   prod.ins_id   = conf.ins_id " +
				" AND   prod.mod_id   = conf.mod_id " +
				" AND   prod.subm_id  = conf.subm_id " +
				" AND   prod.conf_id  = conf.conf_id " +
				" AND   conf.ins_id   = subm.ins_id " +
				" AND   conf.mod_id   = subm.mod_id " +
				" AND   conf.subm_id  = subm.subm_id " +
				" AND   warn.prog_id   = prod.prog_id " +
				" AND   warn.obl_id  = prod.obl_id " +
				" AND   warn.prod_id   = prod.prod_id "+
				" AND   conf.mod_id  = mod.mod_id "+
				" AND   conf.ins_id  = mod.ins_id ";
		
		if(prog_id!=null && prog_id.trim().length()>0){
			query += " AND prod.prog_id = '"+StringEscapeUtils.escapeSql(prog_id.trim())+"' "; 
		}
		
		if(obl_id!=null && obl_id.trim().length()>0){
			query += " AND prod.obl_id like '"+StringEscapeUtils.escapeSql(obl_id.trim().substring(0,4))+"%' ";
		}

		ResultSet resset = conex.createStatement().executeQuery(query+" ORDER BY prod.prod_id, prod.prod_filename ");

		//System.out.println(query+" ORDER BY ins_id, mod_id, prod_filename");
		
		Vector<ProdDatosDb> aux = new Vector<ProdDatosDb>();
		
		while(resset.next()){
			ProdDatosDb prod = new ProdDatosDb(resset);
			
			WarningDb[] warnings = prodDatosAccess.selectWarningsById(prog_id, obl_id, prod.getProdId());
			prod.setWarnings(warnings);
			
			aux.add(prod);
		}
		
		resset.close();

		return (ProdDatosDb[])aux.toArray(new ProdDatosDb[0]);
		
		
	}

	/** 
	 * Selecciona todos los ficheros de log de un bloque de observación.
	 * @param prog_id
	 * @param obl_id
	 * @param ins_id
	 * @param mod_id
	 * @return
	 * @throws SQLException
	 */
	
	public LogFileDb[] selectLogsByOblId(String prog_id, String obl_id) throws SQLException{
		String query = LogFileAccess.getInicioSelect()+
				" FROM logfile log, basepath b " +
				" WHERE log.bpath_id = b.bpath_id ";
		
		if(prog_id!=null && prog_id.trim().length()>0){
			query += " AND prog_id = '"+StringEscapeUtils.escapeSql(prog_id.trim())+"' "; 
		}
		
		if(obl_id!=null && obl_id.trim().length()>0){
			query += " AND obl_id like '"+StringEscapeUtils.escapeSql(obl_id.trim().substring(0,4))+"%' ";
		}
		
		ResultSet resset = conex.createStatement().executeQuery(query);

		System.out.println(query);
		
		Vector<LogFileDb> aux = new Vector<LogFileDb>();
		
		while(resset.next()){
			LogFileDb prod = new LogFileDb(resset);
			aux.add(prod);
		}
		
		resset.close();

		return (LogFileDb[])aux.toArray(new LogFileDb[0]);
		
		
	}

	
}
