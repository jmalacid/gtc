package svo.gtc.db.web;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date;

import svo.gtc.db.permisos.ControlAcceso;
import svo.gtc.db.usuario.UsuarioDb;
import svo.gtc.web.InstMode;
import svo.gtc.web.FormMain;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

/**
 *@author Raul Gutierrez Sanchez (raul@cab.inta-csic.es)
*/
//Esta clase hace referencia al acceso a la tabla Object. 


public class WebMainSearch{
	static Logger logger = Logger.getLogger("svo.gtc");

	
	public static final String[] order = {"prod_initime DESC, prod.mod_id, prod.subm_id" ,
										"prod.mod_id, prod.subm_id",//"ins.ins_name, prod.mod_id, prod.subm_id",
										  "prod.prog_id, prod.obl_id, prod.prod_id, prod.mod_id, prod.subm_id",
										  "prod.prod_id, prod.prog_id, prod.obl_id"};
	
	
	Connection conex = null;
	ResultSet resSet = null;
	FormMain formulario = null;
	UsuarioDb usuario	=	null;
	ControlAcceso controlAcceso = null;
	//DEFINICIÓN DE LOS ATRIBUTOS DE LA CLASE
	
	private String selOblStart=
		"SELECT min(prod_initime) as oblstart FROM proddatos WHERE prog_id = ? AND obl_id = like ? ";
	private String selOblEnd=
		"SELECT max(prod_initime) as oblend FROM proddatos WHERE prog_id = ? AND obl_id like ? ";

	private String selHasPerm=
		"SELECT (SELECT count(*)>0 FROM progusuario WHERE prog_id=? AND user_id=?) " +
		"    OR (SELECT current_date>prog_periodoprop FROM programa WHERE prog_id=?) ";

	private String selPeriodoProp=
		"SELECT prog_periodoprop FROM programa WHERE prog_id=? ";
	
	/*private String selCountCal=
			"SELECT COUNT(*) as count_cal FROM productos WHERE prog_id=? AND obl_id like ? AND mty_id='CAL' ";
	private String selCountAC=
			"SELECT COUNT(*) as count_ac FROM productos WHERE prog_id=? AND obl_id like ? AND mty_id='AC' ";
	private String selCountEE=
			"SELECT COUNT(*) as count_ee FROM productos WHERE prog_id=? AND ((prod_initime>? and prod_initime<?) or (prod_endtime>? and prod_endtime<?)) AND subm_id like 'SS%' ";*/
	private String selCountCal=
			"SELECT COUNT(*) as count_cal FROM productos WHERE prog_id=? AND obl_id like ? AND mty_id='CAL' ";
	private String selCountAC=
			"SELECT COUNT(*) as count_ac FROM productos WHERE prog_id=? AND obl_id like ? AND mty_id='AC' ";
	private String selCountEE=
			"SELECT COUNT(*) as count_ee FROM productos WHERE prog_id=? AND ((prod_initime>? and prod_initime<?) or (prod_endtime>? and prod_endtime<?)) AND subm_id like 'SS%' ";
	private String selCountWarn=
		"SELECT COUNT(*) as count_warn FROM prod_warning WHERE prog_id=? AND obl_id like ? ";
	private String selCountQC=
			"SELECT COUNT(*) as count_qc FROM logfile WHERE prog_id=? AND obl_id like ? ";
	
	private String selCountUser=
			"select count(*) from proderror pe left join obsblock ob on pe.prog_id=ob.prog_id and pe.obl_id=ob.obl_id where obl_pi=?";
	
	PreparedStatement pstselOblStart=null;
	PreparedStatement pstselOblEnd=null;
	PreparedStatement pstselHasPerm=null;
	PreparedStatement pstselPeriodoProp=null;
	PreparedStatement pstselCountCal=null;
	PreparedStatement pstselCountAC=null;
	PreparedStatement pstselCountWarn=null;
	PreparedStatement pstselCountQC = null;
	PreparedStatement pstselCountEE=null;
	PreparedStatement pstCountUser = null;

   //Ordenes SQL
	
	public WebMainSearch(Connection conex, FormMain formulario, UsuarioDb usuario) throws SQLException{
		
		this.conex=conex;
		this.formulario=formulario;
		this.usuario=usuario;
		this.controlAcceso=new ControlAcceso(conex,usuario);
		String query = queryWeb();
		
		//System.out.println(query);
		this.resSet=conex.createStatement().executeQuery(query);
		
		pstselOblStart   = conex.prepareStatement(selOblStart);
		pstselOblEnd     = conex.prepareStatement(selOblEnd);
		pstselHasPerm    = conex.prepareStatement(selHasPerm);
		pstselPeriodoProp= conex.prepareStatement(selPeriodoProp);
		pstselCountCal   = conex.prepareStatement(selCountCal);
		pstselCountAC   = conex.prepareStatement(selCountAC);
		pstselCountWarn  = conex.prepareStatement(selCountWarn);
		pstselCountQC  = conex.prepareStatement(selCountQC);
		pstselCountEE   = conex.prepareStatement(selCountEE);
		pstCountUser = conex.prepareStatement(selCountUser);

	}
	
	/**
	 * Devuelve el siguiente resultado de la query web.
	 * 
	 * @return WebResult
	 * @throws SQLException
	 */
	public WebMainResult getNext() throws SQLException{
		if(this.resSet.next()==true){
			WebMainResult salida = new WebMainResult(this.resSet);
			//boolean hasPerm = controlAcceso.hasPerm(salida.getProgId(), usuario);
			/*
			boolean hasPerm = true;
			salida.setHasPerm(hasPerm);
			if(!hasPerm){
				Date periodoProp = controlAcceso.getPeriodoProp(salida.getProgId());
				salida.setPeriodoProp(periodoProp);
			}
			*/
			
			
			return salida;
		}else{
			return null;
		}
	}
	
	/**
	 * N�mero de resultados de la query web.
	 * 
	 * @return
	 */
	public int countResults(){
		int salida = 0;
		try {
			logger.debug("Count= "+queryCountWeb() );
			ResultSet res = this.conex.createStatement().executeQuery(queryCountWeb());
			if(res.next()){
				salida=res.getInt(1);
				if(!res.wasNull()){
					return salida;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Genera la query a partir de los par�metros del formulario.
	 * @return
	 */
	public String queryWeb(){
		String query = "SELECT * ";
		query += predicadoQuery();
		
		/// Order By
		if(formulario.getOrderBy()!=null && formulario.getOrderBy().intValue()<order.length){
			query += " ORDER BY "+order[formulario.getOrderBy().intValue()];
		}
		
		/// Limit
		if(formulario.getRpp()!=null && formulario.getRpp().intValue()>0){
			query += " LIMIT "+formulario.getRpp().intValue();
		}
		
		/// Offset
		
		if(formulario.getRpp()!=null && formulario.getRpp().intValue()>0 && 
		   formulario.getPts()!=null && formulario.getPts()>0){
			int offset = formulario.getRpp().intValue()*(formulario.getPts().intValue()-1);
			
			query += " OFFSET "+offset;
		}

		logger.debug("Query= "+query );
		return query+";";
	}
	
	/**
	 * Genera la query de cuenta de resultados.
	 * @return
	 */
	public String queryCountWeb(){
		String query = "SELECT COUNT(*) "+predicadoQuery();
		//System.out.println(query);
		return query+";";
	}
	
	/**
	 * 
	 * @return
	 */
	private String predicadoQuery(){
		//String query = " FROM productos prod";
		String query = "";
		
		if(formulario.getProgId()==null && formulario.getOblId()==null){
			//query = " FROM productos_old prod";
			query = " FROM productos prod";
		}else{
			query = " FROM productos prod";
		}
		
		
		/*
		if(formulario.getObjectList()!=null && formulario.getObjectList().length>0){
			query += ", objectname onam ";
		}
		*/

		query +=	" WHERE mty_id = 'SCI' ";
		
		// Devolvemos el producto solo si todo su observing block está correcto
		//query +=		"	AND     (SELECT COUNT(*) FROM proderror err WHERE err.prog_id=prod.prog_id AND err.obl_id=prod.obl_id)=0 ";

		
		String join = " AND ";
		/// Busquedas por objeto:
		/*
		if(formulario.getObjectList()!=null && formulario.getObjectList().length>0){
			query += "	AND     onam.obj_id	=	obj.obj_id ";
			String[] objetos = formulario.getObjectList();
			query += join+" ( ";
			join="";
			for(int i=0; i<objetos.length; i++){
				if(objetos[i].indexOf("%")>=0){
					query += join+" onam_noblanks like '"+StringEscapeUtils.escapeSql(objetos[i].toUpperCase().replaceAll("\\s",""))+"' "; 
				}else{
					query += join+" onam_noblanks = '"+StringEscapeUtils.escapeSql(objetos[i].toUpperCase().replaceAll("\\s",""))+"' ";
				}
				join=" OR ";
			}

			query += " ) ";
			join=" AND ";
		}
		*/
		//Busqueda por usuario, si está completo no buscamos nada más
		if(formulario.getId()!= null && formulario.getId().equals("usr") && usuario!=null){
			query += join + "prog_id in (select distinct(prog_id) from obsblock where obl_pi='"+StringEscapeUtils.escapeSql(usuario.getUsrId())+"')";
			
		}else{
			
			/// Busquedas por Prod id y coordenadas:
			if(formulario.getProdList()!=null || (formulario.getCoordsList()!=null && formulario.getPosRadius()!=null)){
				Integer[] prodList = formulario.getProdList();
				Double[][] coords = formulario.getCoordsList();
				query += join+" ( ";
				join = "";
	
				if(prodList!=null && prodList.length>0){
					for(int i=0; i<prodList.length; i++){
						query += join
						+ " prod_id = "+prodList[i].intValue();
						
						join=" OR ";
						
					}
				}
	
				if(coords!=null && coords.length>0){
					for(int i=0; i<coords.length; i++){
						double ra = coords[i][0].doubleValue();
						double de = coords[i][1].doubleValue();
						double rad = formulario.getPosRadius().doubleValue();
						
						query += join +" (prod.prod_point @ scircle'<("+ra+"d,"+de+"d), "+rad+"d>'=true ) OR "
								+ "(prod.prod_polig && scircle'<("+ra+"d,"+de+"d), "+rad+"d>'=true) OR "
										+ "(prod.prod_polig2 && scircle'<("+ra+"d,"+de+"d), "+rad+"d>'=true)";
						
						/*query += join
						+ " acos( cos(radians(prod.prod_de))*cos(radians("
						+ de
						+ "))*cos(radians(prod.prod_ra - "
						+ ra
						+ ")) + sin(radians(prod.prod_de))*sin(radians("
						+ de
						+ ")) ) < radians("
						+ rad
						+ ") ";*/
						
						join=" OR ";
						
					}
				}
	
				query += " ) ";
				join=" AND ";
			}
	
			/// Busquedas por fecha:
			if(formulario.getDates()!=null ){
				Date[] dates = formulario.getDates();
				
				if(dates[0]!=null){
					query += join+" prod_initime >= '"+dates[0].toString()+"' ";
				}			
	
				if(dates[1]!=null){
					query += join+" prod_endtime <= '"+dates[1].toString()+" 23:59:59.99999' ";
				}			
			}
			
			/// Limitación solo productos reducidos
			
			if(formulario.isOnlyReduced() ){
				//query += join+" (cuenta_red > 0 or cuenta_meg>0 or cuenta_her>0) ";
				query += join+" (cuenta_red > 0) ";
			}
			
			
			/// Busquedas por c�digo de istrumentos:
			if(formulario.getInstModeCodes()!=null && formulario.getInstModeCodes().length>0){
				InstMode[] modeCodes = formulario.getInstModeCodes();
				join=" AND ( ";
				for(int i=0; i<modeCodes.length; i++){
					query += join+" ( prod.ins_id='"+StringEscapeUtils.escapeSql(modeCodes[i].getIns_id())+"' " +
								  "   AND prod.mod_id='"+StringEscapeUtils.escapeSql(modeCodes[i].getMod_id())+"' ) ";
					join = " OR ";
					if( i==modeCodes.length-1 ){
						query += " ) ";
					}
				}
				
				join = " AND ";
	
			}else if(!formulario.isAllInstruments()){
				//query += " AND 1=0 ";
			}
	
			/// Busquedas por program ID:
			if(formulario.getLp()!=0){
				query += join+" prod.prog_id = 'GTC1-15GLP' ";
			}else{
				if(formulario.getProgId()!=null){
					String progID = formulario.getProgId();
					if(progID.indexOf("%")>=0){
						query += join+" prod.prog_id like '"+StringEscapeUtils.escapeSql(progID.toUpperCase())+"' "; 
					}else{
						query += join+" prod.prog_id = '"+StringEscapeUtils.escapeSql(progID.toUpperCase())+"' ";
					}
				}
			}
			/// Busquedas por OblId:
			if(formulario.getOblId()!=null){
				String oblID = formulario.getOblId();
				//if(oblID.indexOf("%")>=0){
					query += join+" prod.obl_id like '"+StringEscapeUtils.escapeSql(oblID.toUpperCase())+"%' "; 
				//}else{
				//	query += join+" prod.obl_id = '"+StringEscapeUtils.escapeSql(oblID.toUpperCase())+"' ";
				//}
			}
			
			/// Busquedas por ProdId:
			if(formulario.getProdId()!=null){
				String prodID = formulario.getProdId();
				//if(oblID.indexOf("%")>=0){
					query += join+" prod.prod_id = '"+StringEscapeUtils.escapeSql(prodID)+"' "; 
				//}else{
				//	query += join+" prod.obl_id = '"+StringEscapeUtils.escapeSql(oblID.toUpperCase())+"' ";
				//}
			}
	
			/// Busquedas por filename:
			if(formulario.getFilename()!=null){
				String filename = formulario.getFilename();
				if(filename.indexOf("%")>=0){
					query += join+" prod.prod_filename like '"+StringEscapeUtils.escapeSql(filename)+"' "; 
				}else{
					query += join+" prod.prod_filename = '"+StringEscapeUtils.escapeSql(filename.toUpperCase())+"' ";
				}
			}
			
			
		}

		//System.out.println(query);
		return query;
	}
	
	/**
	 * Devuelve el tiempo de inicio del Observing Block
	 * @param prog_id
	 * @param obl_id
	 * @return
	 */
	public Timestamp getOblStart(String prog_id, String obl_id){
		try {
			pstselOblStart.setString(1, prog_id);
			pstselOblStart.setString(2, obl_id.substring(0,4)+"%");
			ResultSet ressel = pstselOblStart.executeQuery();
			
			if(ressel.next()){
				return ressel.getTimestamp("oblstart");
			}else{
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Devuelve el tiempo de fin del Observing Block
	 * @param prog_id
	 * @param obl_id
	 * @return
	 */
	public Timestamp getOblEnd(String prog_id, String obl_id){
		try {
			pstselOblEnd.setString(1, prog_id);
			pstselOblEnd.setString(2, obl_id.substring(0,4)+"%");
			ResultSet ressel = pstselOblEnd.executeQuery();
			
			if(ressel.next()){
				return ressel.getTimestamp("oblend");
			}else{
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Cuenta el numero de productos de calibracion asociados a un observing block;
	 * @param prog_id
	 * @param obl_id
	 * @return
	 */
	public int getCountCal(String prog_id, String obl_id){
		try {
			pstselCountCal.setString(1, prog_id);
			pstselCountCal.setString(2, obl_id.substring(0,4)+"%");
			ResultSet ressel = pstselCountCal.executeQuery();
			
			if(ressel.next()){
				return ressel.getInt("count_cal");
			}else{
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Cuenta el numero de Estrellas Estandar que hay en el programa por esa fecha;
	 * @param prog_id
	 * @param obl_id
	 * @return
	 */
	public int getCountEE(String prog_id, String ini, String end){
		try {
			
			Timestamp ini1 = Timestamp.valueOf(ini.split(" ")[0]+" 00:00:00");
			Timestamp ini2 = Timestamp.valueOf(ini.split(" ")[0]+" 23:59:59");
			Timestamp end1 = Timestamp.valueOf(end.split(" ")[0]+" 00:00:00");
			Timestamp end2 = Timestamp.valueOf(end.split(" ")[0]+" 23:59:59");
			pstselCountEE.setString(1, prog_id);
			pstselCountEE.setTimestamp(2, ini1);
			pstselCountEE.setTimestamp(3, ini2);
			pstselCountEE.setTimestamp(4, end1);
			pstselCountEE.setTimestamp(5, end2);
			
			ResultSet ressel = pstselCountEE.executeQuery();
			
			
			if(ressel.next()){
				return ressel.getInt("count_ee");
			}else{
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Cuenta el numero de productos de AC asociados a un observing block;
	 * @param prog_id
	 * @param obl_id
	 * @return
	 */
	public int getCountAC(String prog_id, String obl_id){
		try {
			pstselCountAC.setString(1, prog_id);
			pstselCountAC.setString(2, obl_id.substring(0,4)+"%");
			
			ResultSet ressel = pstselCountAC.executeQuery();
			
			if(ressel.next()){
				return ressel.getInt("count_ac");
			}else{
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Cuenta el numero de productos con warning asociados a un observing block;
	 * @param prog_id
	 * @param obl_id
	 * @return
	 */
	public int getCountWarn(String prog_id, String obl_id){
		try {
			pstselCountWarn.setString(1, prog_id);
			pstselCountWarn.setString(2, obl_id.substring(0,4)+"%");
			ResultSet ressel = pstselCountWarn.executeQuery();
			
			if(ressel.next()){
				return ressel.getInt("count_warn");
			}else{
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Cuenta el numero de QC logs asociados a un observing block;
	 * @param prog_id
	 * @param obl_id
	 * @return
	 */
	public int getCountQC(String prog_id, String obl_id){
		try {
			pstselCountQC.setString(1, prog_id);
			pstselCountQC.setString(2, obl_id.substring(0,4)+"%");
			ResultSet ressel = pstselCountQC.executeQuery();
			
			if(ressel.next()){
				return ressel.getInt("count_qc");
			}else{
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Cuenta el numero productos con error de un usuario
	 * @param User
	 * @return
	 */
	public int getErrorUser(String user){
		try {
			pstCountUser.setString(1, user);
			ResultSet ressel = pstCountUser.executeQuery();
			
			if(ressel.next()){
				return ressel.getInt("count");
			}else{
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
}
