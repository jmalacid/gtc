package svo.gtc.siap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class ConsultaDL {

	static Connection conex;
	
	private PreparedStatement psRaw=null;
	private PreparedStatement psCal=null;
	private PreparedStatement psIns_id = null;
	private PreparedStatement psSize = null;
	private PreparedStatement psBib = null;
	
	private String viewRaw = "SELECT prog_id, obl_id, prod_id from pred_prod where pred_id=?";
	private String viewCal = "SELECT COUNT(*) as count_cal FROM productos WHERE prog_id=? AND obl_id=? AND mty_id='CAL' ";
	private String viewIns_id = "SELECT ins_id, prod_filesize from proddatos where prog_id=? and obl_id=? and prod_id=?";
	private String viewSize = "SELECT pred_filesize from prodreducido where pred_id=?";
	private String viewBib = "select col_bibcode from colecciondatos as cc, prodreducido as pr where cc.usr_id=pr.usr_id and cc.col_id=pr.col_id and pred_id=?";
	
	public ConsultaDL(Connection con) throws SQLException{
		conex=con;
		conex.setAutoCommit(true);
		
		psRaw=conex.prepareStatement(viewRaw);
		psCal=conex.prepareStatement(viewCal);
		psIns_id = conex.prepareStatement(viewIns_id);
		psSize = conex.prepareStatement(viewSize);
		psBib = conex.prepareStatement(viewBib);
				
		
	}
	
	/**
	 * Cuenta el numero de productos de calibracion asociados a un observing block;
	 * @param prog_id
	 * @param obl_id
	 * @return
	 */
	public int getCountCal(String prog_id, String obl_id){
		try {
			psCal.setString(1, prog_id);
			psCal.setString(2, obl_id);
			ResultSet ressel = psCal.executeQuery();
			
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
	 * Devuelve los productos raw relacionados con un producto reducido;
	 * @param pred_id
	 * @return prod-prog-obl
	 * @throws SQLException 
	 */
	public Prod[] getCountRaw(Integer pred_id) throws SQLException{

			psRaw.setInt(1, pred_id);
			
			ResultSet ressel = psRaw.executeQuery();
			
		
			int contador = 0;
			Vector aux = new Vector();
			while (ressel.next()) {
				
				 Prod Result = new Prod(ressel);

				aux.addElement(Result);
				contador++;
			}
			
			return  (Prod[])aux.toArray(new Prod[0]);

	}
	
	/**
	 * Encuentra el ins_id
	 * @param prog_id
	 * @param obl_id
	 * * @param prod_id
	 * @return ins_id
	 */
	public String getIns_id(String prog_id, String obl_id, Integer prod_id){
		try {
			psIns_id.setString(1, prog_id);
			psIns_id.setString(2, obl_id);
			psIns_id.setInt(3, prod_id);
			ResultSet ressel = psIns_id.executeQuery();
			
			if(ressel.next()){
				return ressel.getString("ins_id").trim()+".__."+ressel.getInt("prod_filesize");
			}else{
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Obtiene el tamaño del fichero
	 * @param pred_id
	 * @return pred_filesize
	 */
	public int getSize(Integer pred_id){
		try {
			psSize.setInt(1, pred_id);
			
			//System.out.println(psSize.toString());
			ResultSet ressel = psSize.executeQuery();
			
			if(ressel.next()){
				return ressel.getInt("pred_filesize");
			}else{
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Obtiene el Bibcode del Journal
	 * @param pred_id
	 * @return col_bibcode
	 */
	public String getBib(Integer pred_id){
		try {
			psBib.setInt(1, pred_id);
			
			//System.out.println(psSize.toString());
			ResultSet ressel = psBib.executeQuery();
			
			if(ressel.next()){
				return ressel.getString("col_bibcode");
			}else{
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
