/*
 * @(#)ProdDatosAccess.java    Aug 6, 2009
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

package svo.gtc.db.prodat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

public class ProdDatosAccess {
	static Logger logger = Logger.getLogger("svo.gtc");

	Connection con = null;
	//SELECT
	//private PreparedStatement pstSelNewProdId;
	private PreparedStatement pstSelById;
	private PreparedStatement pstSelRedById;
	private PreparedStatement pstSelByProdId;
	private PreparedStatement pstCountById;
	private PreparedStatement pstSelWarningsById;
	private PreparedStatement pstIns;
	private PreparedStatement pstDel;
	
	private PreparedStatement pstInsWarning;
	private PreparedStatement pstDelWarning;

	
	//private String selectNewProdId = " SELECT max(prod_id)+1 FROM proddatos a; ";

	private static String select=
		" SELECT prod.prod_id, prod.prog_id, prod.obl_id, prod.det_id, prod.ins_id, prod.mod_id, prod.subm_id, prod.conf_id, subm.mty_id, mod.mod_shortname, " +
		"        b.bpath_id, b.bpath_path, prod_filename, prod_path, prod_filesize, prod_ra, prod_de, prod_initime, prod_endtime, " +
		"	     prod_exposure, prod_airmass, prod_observer, prod_object, prod_band_min, prod_band_max ";
	private static String joins=
		" FROM proddatos prod, basepath b, conf, submodo subm, modo mod  " +
		"WHERE prod.bpath_id=b.bpath_id " +
		"AND   prod.det_id=conf.det_id " +
		"AND   prod.ins_id=conf.ins_id " +
		"AND   prod.mod_id=conf.mod_id " +
		"AND   prod.subm_id=conf.subm_id " +
		"AND   prod.conf_id=conf.conf_id " +
		"AND   conf.ins_id=subm.ins_id " +
		"AND   conf.mod_id=subm.mod_id " +
		"AND   conf.subm_id=subm.subm_id  " +
		"AND   subm.ins_id=mod.ins_id "+
		"AND   subm.mod_id=mod.mod_id ";

	private String selCount=
		" SELECT COUNT(*) " +
		" 	FROM proddatos a ";

	
	private String insert=
		" INSERT INTO proddatos (prod_id, prog_id, obl_id, det_id, ins_id, mod_id, subm_id, conf_id, bpath_id,  " +
		" 						 prod_filename, prod_path, prod_filesize, prod_ra, prod_de, prod_initime, " +
		" 						 prod_endtime, prod_exposure, prod_airmass, prod_observer, prod_object, prod_band_min, prod_band_max, prod_polig, prod_polig2, prod_point) " +
		" 			VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

	private String delete=
		" DELETE FROM proddatos WHERE prog_id=? AND obl_id=? AND prod_id=? ";

	
	
	private String selWarnings=
		" SELECT a.err_id, err_desc " +
		" 	FROM errors a , prod_warning b WHERE a.err_id=b.err_id ";

	private String insWarning=
		" INSERT into prod_warning (prog_id, obl_id, prod_id, err_id) VALUES (?,?,?,?) ";

	private String delWarning=
		" DELETE from prod_warning WHERE prog_id=? and obl_id=? and prod_id=?; ";


	public ProdDatosAccess(Connection conex) throws SQLException{
		con = conex;
		//Ordenes Precompiladas --------------------------------------------------
		//pstSelNewProdId = conex.prepareStatement(selectNewProdId);
		pstSelById = conex.prepareStatement(select+joins+
				"AND   prog_id= ? " +
				"AND   obl_id= ? " +
				"AND   prod_id= ? ;");
		pstSelRedById = conex.prepareStatement(select+joins+
				"AND   prog_id= ? " +
				"AND   obl_id= ? " +
				"AND   prod_filename like '%REDUCEDFROM%' and prod_filename like ? ;");
		pstSelByProdId = conex.prepareStatement(select+joins+
				"AND   prod_id= ? ;");
		pstCountById = conex.prepareStatement(selCount+" WHERE prog_id=? AND obl_id=? AND prod_id=?;");
		pstIns 		= conex.prepareStatement(insert);
		pstDel 		= conex.prepareStatement(delete);

		pstSelWarningsById 	= conex.prepareStatement(selWarnings+" AND prog_id=? AND obl_id=? AND prod_id= ?;");
		pstInsWarning 		= conex.prepareStatement(insWarning);
		pstDelWarning 		= conex.prepareStatement(delWarning);


	}
	
	
	/**
	 * Devuelve el inicio de la select de productos de datos, con los campos por defecto.
	 * @return
	 */
	public static String getInicioSelect(){
		return select;
	}
	
/**
 * Devuelve el producto de datos a partir del identificador.
 * @param prog_id
 * @param obl_id
 * @param prod_id
 * @return
 * @throws SQLException
 */
	public ProdDatosDb selectById(String prog_id, String obl_id, Integer prod_id) throws SQLException{

		pstSelById.setString(1, prog_id);
		pstSelById.setString(2, obl_id);
		pstSelById.setInt(3, prod_id.intValue());
		ResultSet resset = pstSelById.executeQuery();
		
		
		ProdDatosDb prod=null;
		if(resset.next()){
			prod = new ProdDatosDb(resset);
			
			WarningDb[] warnings = selectWarningsById(prog_id, obl_id, prod_id);
			prod.setWarnings(warnings);

		}
		
		resset.close();

		return prod;
		
		
	}
	
	/**
	 * Devuelve el producto REDUCEDFROM a partir del identificador.
	 * @param prog_id
	 * @param obl_id
	 * @param prod_id
	 * @return
	 * @throws SQLException
	 */
		public ProdDatosDb selectRedById(String prog_id, String obl_id, Integer prod_id) throws SQLException{

			pstSelRedById.setString(1, prog_id);
			pstSelRedById.setString(2, obl_id);
			pstSelRedById.setString(3, "%"+prod_id+"%");
			ResultSet resset = pstSelRedById.executeQuery();
			
			ProdDatosDb prod=null;
			if(resset.next()){
				prod = new ProdDatosDb(resset);
				
				WarningDb[] warnings = selectWarningsById(prog_id, obl_id, prod_id);
				prod.setWarnings(warnings);

			}
			
			resset.close();

			return prod;
			
			
		}
	
	public int countById(String prog_id, String obl_id, Integer prod_id) throws SQLException{
		pstCountById.setString(1, prog_id);
		pstCountById.setString(2, obl_id);
		pstCountById.setInt(3, prod_id);
		ResultSet resset = pstCountById.executeQuery();

		int count=0;
		if(resset.next()){
			count = resset.getInt(1);
		}

		return count;
		
	}
	
	
	/**
	 * Devuelve todos los productos de datos registrados con el prod_id especificado.
	 * @param prod_id
	 * @return
	 * @throws SQLException
	 */
	public ProdDatosDb[] selectByProdId(Integer prod_id) throws SQLException{
		Vector<ProdDatosDb> vctr = new Vector<ProdDatosDb>();
		pstSelByProdId.setInt(1, prod_id.intValue());
		
		//System.out.println(pstSelByProdId.toString());
		
		ResultSet resset = pstSelByProdId.executeQuery();

		ProdDatosDb prod=null;
		while(resset.next()){
			prod = new ProdDatosDb(resset);
			vctr.add(prod);
		}
		
		resset.close();

		return (ProdDatosDb[])vctr.toArray(new ProdDatosDb[0]);
		
		
	}
	

	/** Devuelve todos los productos de datos de un observing block. Pueden especificarse
	 *  los modos deseados.
	 * 	
	 * @param prog_id
	 * @param obl_id
	 * @param ins_id
	 * @param mod_id
	 * @return
	 * @throws SQLException
	 */
		public ProdDatosDb[] selectByOblId(String prog_id, String obl_id, String ins_id, String[] mod_id) throws SQLException{
			String query = select+" 	FROM proddatos prod, basepath b, conf, submodo subm  " +
					"WHERE prod.bpath_id=b.bpath_id " +
					"AND   prod.det_id=conf.det_id " +
					"AND   prod.ins_id=conf.ins_id " +
					"AND   prod.mod_id=conf.mod_id " +
					"AND   prod.subm_id=conf.subm_id " +
					"AND   prod.conf_id=conf.conf_id " +
					"AND   conf.ins_id=subm.ins_id " +
					"AND   conf.mod_id=subm.mod_id " +
					"AND   conf.subm_id=subm.subm_id ";
;
			
			if(prog_id!=null && prog_id.trim().length()>0){
				query += " AND prog_id = '"+StringEscapeUtils.escapeSql(prog_id.trim())+"' "; 
			}
			
			if(obl_id!=null && obl_id.trim().length()>0){
				query += " AND obl_id = '"+StringEscapeUtils.escapeSql(obl_id.trim())+"' "; 
			}
			
			if(ins_id!=null && ins_id.trim().length()>0){
				query += " AND ins_id = '"+StringEscapeUtils.escapeSql(ins_id.trim())+"' "; 
			}
			
			if(mod_id!=null && mod_id.length>0){
				query += " AND ( ";
				String join = "";
				for(int i=0; i<mod_id.length; i++){
					if(mod_id[i].trim().length()>0){
						query += join+"mod_id='"+StringEscapeUtils.escapeSql(mod_id[i].trim())+"' ";
						join = " OR ";
					}
				}
				query += " ) ";
			}
			
			ResultSet resset = con.createStatement().executeQuery(query+" ORDER BY ins_id, mod_id, prod_filename");

			//System.out.println(query+" ORDER BY ins_id, mod_id, prod_filename");
			
			Vector<ProdDatosDb> aux = new Vector<ProdDatosDb>();
			
			while(resset.next()){
				ProdDatosDb prod = new ProdDatosDb(resset);
				aux.add(prod);
			}
			
			resset.close();

			return (ProdDatosDb[])aux.toArray(new ProdDatosDb[0]);
			
			
		}
		

		/**
		 * Elimina un producto de datos y sus correspondientes warnings, si los hubiera.
		 * @param prog_id
		 * @param obl_id
		 * @param prod_id
		 * @throws SQLException
		 */

		public void delete(String prog_id, String obl_id, Integer prod_id) throws SQLException{
			int count = this.selectWarningsById(prog_id, obl_id, prod_id).length;
			if(count>0){
				delWarnings(prog_id, obl_id, prod_id);
			}
			
			pstDel.setString(1, prog_id.trim().toUpperCase());
			pstDel.setString(2, obl_id.trim().toUpperCase());
			pstDel.setInt(3, prod_id.intValue());
			pstDel.executeUpdate();
		}

		/**
		 * Inserta un producto de datos y sus correspondientes warnings, si los hubiera.
		 * @param prod
		 * @return
		 * @throws SQLException
		 */
		public ProdDatosDb insProdDatos(ProdDatosDb prod) throws SQLException{
			pstIns.setInt(1, prod.getProdId());
			pstIns.setString(2, prod.getProgId());
			pstIns.setString(3, prod.getOblId());
			pstIns.setInt(4, prod.getDetId());
			pstIns.setString(5, prod.getInsId());
			pstIns.setString(6, prod.getModId());
			pstIns.setString(7, prod.getSubmId());
			pstIns.setInt(8, prod.getConfId());
			pstIns.setInt(9, prod.getBpathId());
			pstIns.setString(10, prod.getProdFilename());
			pstIns.setString(11, prod.getProdPath());
			pstIns.setLong(12, prod.getProdFilesize());

			if(prod.getProdRa()!=null){
				pstIns.setDouble(13, prod.getProdRa());
			}else{
				pstIns.setNull(13, Types.DOUBLE);
			}

			if(prod.getProdDe()!=null){
				pstIns.setDouble(14, prod.getProdDe());
			}else{
				pstIns.setNull(14, Types.DOUBLE);
			}

			if(prod.getProdInitime()!=null){
				pstIns.setTimestamp(15, prod.getProdInitime());
			}else{
				pstIns.setNull(15, Types.TIMESTAMP);
			}

			if(prod.getProdEndtime()!=null){
				pstIns.setTimestamp(16, prod.getProdEndtime());
			}else{
				pstIns.setNull(16, Types.TIMESTAMP);
			}

			if(prod.getProdExposure()!=null){
				pstIns.setDouble(17, prod.getProdExposure());
			}else{
				pstIns.setNull(17, Types.DOUBLE);
			}
			
			if(prod.getProdAirmass()!=null){
				pstIns.setDouble(18, prod.getProdAirmass());
			}else{
				pstIns.setNull(18, Types.DOUBLE);
			}

			pstIns.setString(19, prod.getProdObserver());
			
			pstIns.setString(20, prod.getProdObject());
			
			if(prod.getProdBandMin()!=null){
				pstIns.setDouble(21, prod.getProdBandMin());
			}else{
				pstIns.setNull(21, Types.DOUBLE);
			}
			
			if(prod.getProdBandMax()!=null){
				pstIns.setDouble(22, prod.getProdBandMax());
			}else{
				pstIns.setNull(22, Types.DOUBLE);
			}
			
			pstIns.setString(23, prod.getProdPolig());
			pstIns.setString(24, prod.getProdPolig2());
			pstIns.setString(25, prod.getProdPoint());
			
			//System.out.println(pstIns.toString());
			
			pstIns.execute();

			insWarnings(prod.getProgId(), prod.getOblId(), prod.getProdId(), prod.getWarnings());

			
			return prod;
			
		}

		/**
		 * Inserta un producto de datos y sus correspondientes warnings, si los hubiera.
		 * @param prod
		 * @return
		 * @throws SQLException
		 */
		public ProdDatosDb insProdDatos_new(ProdDatosDb prod) throws SQLException{
			
			String insert1 = "INSERT INTO proddatos (prod_id, prog_id, obl_id, det_id, ins_id, mod_id, subm_id, conf_id, bpath_id,  " +
					" 						 prod_filename, prod_path, prod_filesize ";
			String insert2 = ") VALUES ('"+prod.getProdId()+"','"+prod.getProgId()+"','"+prod.getOblId()+"','"+prod.getDetId()+"','"+prod.getInsId()+"','"+prod.getModId()+"','"+prod.getSubmId()+"',"
							+ "'"+prod.getConfId()+"','"+prod.getBpathId()+"','"+prod.getProdFilename()+"','"+prod.getProdPath()+"','"+prod.getProdFilesize()+"'";
			
			if(prod.getProdRa()!=null){
				insert1 += ", prod_ra";
				insert2 += ","+prod.getProdRa();
			}
			
			if(prod.getProdDe()!=null){
				insert1 += ", prod_de";
				insert2 += ","+prod.getProdDe();
			}
			
			if(prod.getProdInitime()!=null){
				insert1 += ", prod_initime";
				insert2 += ",'"+prod.getProdInitime()+"'";
			}
			
			if(prod.getProdEndtime()!=null){
				insert1 += ", prod_endtime";
				insert2 += ",'"+prod.getProdEndtime()+"'";
			}
			
			if(prod.getProdExposure()!=null){
				insert1 += ", prod_exposure";
				insert2 += ","+prod.getProdExposure();
			}
			
			if(prod.getProdAirmass()!=null){
				insert1 += ", prod_airmass";
				insert2 += ","+prod.getProdAirmass();
			}
			
			if(prod.getProdObserver()!=null){
				insert1 += ", prod_observer";
				insert2 += ",'"+prod.getProdObserver()+"'";
			}
			
			if(prod.getProdObject()!=null){
				insert1 += ", prod_object";
				insert2 += ",'"+prod.getProdObject()+"'";
			}
			
			if(prod.getProdBandMin()!=null){
				insert1 += ", prod_band_min";
				insert2 += ","+prod.getProdBandMin();
			}
			
			if(prod.getProdBandMax()!=null){
				insert1 += ", prod_band_max";
				insert2 += ","+prod.getProdBandMax();
			}
			
			if(prod.getProdPolig()!=null && (prod.getModId().trim().equalsIgnoreCase("TF") || prod.getModId().trim().equalsIgnoreCase("BBI"))){
				insert1 += ", prod_polig";
				insert2 += ","+prod.getProdPolig();
			}
			
			if(prod.getProdPolig2()!=null && (prod.getModId().trim().equalsIgnoreCase("TF") || prod.getModId().trim().equalsIgnoreCase("BBI"))){
				insert1 += ", prod_polig2";
				insert2 += ","+prod.getProdPolig2();
			}
			
			if(prod.getProdPoint()!=null){
				insert1 += ", prod_point";
				insert2 += ","+prod.getProdPoint();
			}
			
			String insert = insert1 + insert2 + ");";
			//System.out.println(insert);
			
			con.setAutoCommit(true);
			
			Statement stm = con.createStatement();
			stm.executeUpdate(insert);
				
			stm.close();

			insWarnings(prod.getProgId(), prod.getOblId(), prod.getProdId(), prod.getWarnings());

			
			return prod;
			
		}
		
		/**
		 * Devuelve la fecha del producto más antiguo registrada en la base de datos.
		 * @return
		 * @throws SQLException 
		 */
		public Timestamp getFirstDate() throws SQLException{
			String query = "SELECT min(prod_initime) from proddatos";
			ResultSet resset = con.createStatement().executeQuery(query);

			if(resset.next()){
				return resset.getTimestamp(1);
			}else{
				return null;
			}
		}
		
		/**
		 * Devuelve la fecha del producto más moderno registrado en la base de datos.
		 * @return
		 * @throws SQLException 
		 */
		public Timestamp getLastDate() throws SQLException{
			String query = "SELECT max(prod_endtime) from proddatos";
			ResultSet resset = con.createStatement().executeQuery(query);

			if(resset.next()){
				return resset.getTimestamp(1);
			}else{
				return null;
			}
		}
		

		/**
		 * Devuelve todos los warnings asociados a un producto.
		 * @param prog_id
		 * @param obl_id
		 * @param prode_id
		 * @return
		 * @throws SQLException
		 */
		
		
		public WarningDb[] selectWarningsById(String prog_id, String obl_id, Integer prod_id) throws SQLException{
			pstSelWarningsById.setString(1, prog_id);
			pstSelWarningsById.setString(2, obl_id);
			pstSelWarningsById.setInt(3, prod_id.intValue());
			ResultSet resset = pstSelWarningsById.executeQuery();
			Vector<WarningDb> vcr = new Vector<WarningDb>();
			
			WarningDb warning=null;
			while(resset.next()){
				warning = new WarningDb(resset);
				vcr.add(warning);
			}
			
			
			return (WarningDb[])vcr.toArray(new WarningDb[0]);
		}


		/**
		 * Asocia los warnings corespondientes a un producto erroneo. Los warnings
		 * existentes se respetarán.
		 * @param prog_id
		 * @param obl_id
		 * @param prod_id
		 * @param warningIds
		 * @throws SQLException
		 */
		public void insWarnings(String prog_id, String obl_id, Integer prod_id, String[] warningIds) throws SQLException{
			WarningDb[] antiguos = selectWarningsById(prog_id, obl_id, prod_id);
			
			for(int i=0; i<warningIds.length; i++){
				boolean existe = false;
				for(int j=0; j<antiguos.length; j++){
					if(antiguos[j].getErr_id().equals(warningIds[i])){
						existe=true;
						break;
					}
				}
				if(existe) continue;
				
				pstInsWarning.setString(1, prog_id);
				pstInsWarning.setString(2, obl_id);
				pstInsWarning.setInt(3, prod_id.intValue());
				pstInsWarning.setString(4, warningIds[i]);
				pstInsWarning.execute();
			}
			
		}
		
		public void insWarnings(String prog_id, String obl_id, Integer prod_id, WarningDb[] warnings) throws SQLException{
			String[] warningIds = new String[warnings.length];
			for(int i=0; i<warningIds.length; i++){
				warningIds[i]=warnings[i].getErr_id();
			}
			insWarnings(prog_id, obl_id, prod_id, warningIds);
		}


		/**
		 * Borra todos los warnings asociados a un producto.
		 * @param prog_id
		 * @param obl_id
		 * @param prode_id
		 * @throws SQLException
		 */
		public void delWarnings(String prog_id, String obl_id, Integer prod_id) throws SQLException{
			pstDelWarning.setString(1, prog_id);
			pstDelWarning.setString(2, obl_id);
			pstDelWarning.setInt(3, prod_id);
			pstDelWarning.execute();
		}
		
		
		/** 
		 * Pensado para buscar el producto RAW que corresponde a un producto reducido. Se establece la búsqueda 
		 * a partir de los campos ProgId, OblId, ModId, y OpenTime. 
		 * @param prog_id
		 * @param obl_id
		 * @param ins_id
		 * @param mod_id
		 * @param opentime
		 * @return
		 * @throws SQLException
		 */
		public ProdDatosDb[] selectByReducedProductInfo(String prog_id, String obl_id, Date opentime, Date clostime, String tipo) throws SQLException{
			String query = select+joins;
			
			if(prog_id!=null && prog_id.trim().length()>0){
				query += " AND prog_id = '"+StringEscapeUtils.escapeSql(prog_id.trim())+"' "; 
			}
			
			if(obl_id!=null && obl_id.trim().length()>0){
				query += " AND obl_id = '"+StringEscapeUtils.escapeSql(obl_id.trim())+"' "; 
			}
			
			
			
			SimpleDateFormat sdf = null;
			if(tipo.equals("S")){
				sdf=new java.text.SimpleDateFormat("HH:mm:ss.S");
			}else{
				sdf=new java.text.SimpleDateFormat("HH:mm:ss.SSS");
			}

			if(opentime!=null){
				query += " AND date_trunc('milliseconds',cast(prod_initime as time))=date_trunc('milliseconds', time '"+sdf.format(opentime)+"') ";
				//logger.debug("Buscando producto RAW: Opentime= "+sdf.format(opentime));
			}else query += " AND 1=0 ";

			if(clostime!=null){
				query += " AND date_trunc('milliseconds',cast(prod_endtime as time))=date_trunc('milliseconds', time '"+sdf.format(clostime)+"') ";
				//logger.debug("Buscando producto RAW: Clostime= "+sdf.format(clostime));
			}//else query += " AND 1=0 ";

			logger.debug(query);

			
			ResultSet resset = con.createStatement().executeQuery(query);

			Vector<ProdDatosDb> aux = new Vector<ProdDatosDb>();
			
			while(resset.next()){
				ProdDatosDb prod = new ProdDatosDb(resset);
				aux.add(prod);
			}
			
			resset.close();

			//logger.debug("Buscando producto RAW: "+query);
			return (ProdDatosDb[])aux.toArray(new ProdDatosDb[0]);
			
			
		}

	
}
