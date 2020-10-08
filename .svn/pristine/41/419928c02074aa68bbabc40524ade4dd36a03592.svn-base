package svo.gtc.db.web;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import svo.gtc.db.permisos.ControlAcceso;
import svo.gtc.db.usuario.UsuarioDb;

import org.apache.log4j.Logger;

/**
 *@author José Manuel Alacid (jmalacid@cab.inta-csic.es)
*/
//Esta clase hace referencia al acceso a la tabla Object. 


public class BibResult{
	static Logger logger = Logger.getLogger("svo.gtc");
	
	
	Connection conex = null;
	ResultSet resSet = null;
	String bibcode = null;
	UsuarioDb usuario	=	null;
	ControlAcceso controlAcceso = null;
	//DEFINICIÓN DE LOS ATRIBUTOS DE LA CLASE
	
	private String select=
		"SELECT prod_id, prog_id, obl_id, ins_id, mod_id, mod_shortname, subm_id, prod_ra, prod_de, prod_initime, prod_endtime, prod_exposure, prod_airmass, ins_name, prod_filename";
	private String selCountCal=
			"SELECT COUNT(*) as count_cal FROM productos WHERE prog_id=? AND obl_id like ? AND mty_id='CAL' ";
	private String selCountWarn=
		"SELECT COUNT(*) as count_warn FROM prod_warning WHERE prog_id=? AND obl_id like ? ";
	
	PreparedStatement pstselect=null;
	PreparedStatement pstselCountCal=null;
	PreparedStatement pstselCountWarn=null;
	
   //Ordenes SQL
	
	public BibResult(Connection conex, String bibcode, Integer rpp, Integer pts) throws SQLException{
		this.conex=conex;
		this.bibcode=bibcode;
		
		String query = "SELECT * FROM productos where  mty_id='SCI' and prod_id in (select pub_prod from prod_pub pr,"
				+ "prodpub pp where pp.pub_id=pr.pub_id and pub_bibcode='"+bibcode+"')";
		/// Limit
				if(rpp!=null && rpp>0){
					query += " LIMIT "+rpp;
				}
				
				/// Offset
				if(rpp!=null && rpp>0 && 
				   pts!=null && pts>0){
					int offset = rpp*(pts-1);
					
					query += " OFFSET "+offset;
				}
		query += ";";
		
		//String query = "SELECT prod.prod_id, prod.prog_id, prod.obl_id, prod.ins_id, prod.mod_id, prod.subm_id, prod.prod_ra, prod.prod_de, prod.prod_initime, prod.prod_endtime, prod.prod_exposure, " +
		//		"prod.prod_airmass, prod.prod_filename" +
		//		" from proddatos prod, pred_prod pred, prodreducido pr, colecciondatos col where pred.prog_id=prod.prog_id and pred.obl_id=prod.obl_id and pred.prod_id=prod.prod_id and " +
		//		"pr.pred_id=pred.pred_id and col.usr_id=pr.usr_id and col.col_id=pr.col_id and col.col_bibcode='"+bibcode+"';";
		
		//System.out.println(query);
		this.resSet=conex.createStatement().executeQuery(query);
		
		pstselect   = conex.prepareStatement(select);
		pstselCountCal   = conex.prepareStatement(selCountCal);
		pstselCountWarn  = conex.prepareStatement(selCountWarn);

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
			logger.debug("Count= SELECT count(*) FROM productos where  mty_id='SCI' and prod_id in (select pub_prod from prod_pub pr,"
				+ "prodpub pp where pp.pub_id=pr.pub_id and pub_bibcode='"+bibcode+"')");
			ResultSet res = this.conex.createStatement().executeQuery("SELECT count(*) FROM productos where  mty_id='SCI' and prod_id in (select pub_prod from prod_pub pr,"
				+ "prodpub pp where pp.pub_id=pr.pub_id and pub_bibcode='"+bibcode+"')");
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
}
