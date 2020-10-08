package svo.gtc.db.object;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *@author Almudena Velasco Trasmonte (avelasco@laeff.inta.es)
*/ 
//Esta clase hace referencia al acceso a la tabla Object.


public class ObjectAccess{
	
	//DEFINICIÓN DE LOS ATRIBUTOS DE LA CLASE
	
   //Ordenes SQL
	
	private PreparedStatement pstselObjectcount;
	private PreparedStatement pstselObjectcountAll;
	private PreparedStatement pstinsObject;
	private PreparedStatement pstselObjectAll;
	
	//SELECT
	
	private String selnewObject1=
		" SELECT count(*) FROM Object WHERE obj_corid= ? ";
	
	
	private String selObjectAll=
			" SELECT obj_corid, nam_id, obj_ra, obj_de, obj_absv, obj_magv, obj_magb, obj_magr, obj_magi, obj_bv, obj_sptype, obj_sptsubclass, obj_coltemp," 
			+ "obj_lumclass, obj_teff, obj_gravity, obj_metal, obj_obs FROM object; ";
	
	//INSERT
	
	private String insnewObject=
		" INSERT INTO Object "
		+ " (obj_corid, nam_id, obj_ra, obj_de, obj_absv, obj_magv, obj_magb, obj_magr, obj_magi, obj_bv, obj_sptype, obj_sptsubclass," 
		+ "  obj_coltemp, obj_lumclass, obj_teff, obj_gravity, obj_metal, obj_obs)"
		+ "  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
	
	
	private String selcountall=
		 " SELECT count(*) from Object ";
	
	public ObjectAccess(Connection conex) throws SQLException{
		
		
		//Ordenes Precompiladas --------------------------------------------------
		pstselObjectcount    = conex.prepareStatement(selnewObject1);
		pstselObjectcountAll = conex.prepareStatement(selcountall);
		pstinsObject 	     = conex.prepareStatement(insnewObject);
		pstselObjectAll      = conex.prepareStatement(selObjectAll);
		
	}
	
	/**
	 * <P>Selecciona un nuevo objeto.</p>
	 * 
	 * @param Object 
	 * 		<p>Objeto que representa el nuevo registro.</p>
	 * @return 
	 * 		<p>Entero que devuelve la ejecucion de la orden.</p>
	 * 
	 * @throws SQLException
	 */

	public Integer selectCount(Integer obj_corid) throws SQLException{
		System.out.println("selnewObject1="+selnewObject1);
		pstselObjectcount.setInt(1, obj_corid.intValue());
		ResultSet resselObject = pstselObjectcount.executeQuery();
		Integer count=null;	
		
		while(resselObject.next()){
			count=Integer.valueOf(resselObject.getInt(1));
		}
		resselObject.close();
		
		return count;
	}
	
		
	public Integer selectCount() throws SQLException{
		
		ResultSet resselObject = pstselObjectcountAll.executeQuery();
		Integer count=null;	
		
		while(resselObject.next()){
			count=Integer.valueOf(resselObject.getInt(1));
		}
		resselObject.close();
		
		return count;
	}
	
	public ResultSet selectAll() throws SQLException{
		return pstselObjectAll.executeQuery();
	}
	


	/**
	 * <P>Iserta un nuevo nombre.</p>
	 * 
	 * @param Object 
	 * 		<p>Objeto que representa el nuevo registro.</p>
	 * @return 
	 * 		<p>Entero que devuelve la ejecucion de la orden.</p>
	 * 
	 * @throws SQLException
	 */
	
	public void insert(Object object)throws SQLException{
		
		try{
			
			pstinsObject.setInt(1, object.getCorId().intValue());
			
			if(object.getNamId()!=null){
				pstinsObject.setInt(2, object.getNamId().intValue());
			}
			else{
				pstinsObject.setNull(2,  java.sql.Types.INTEGER);
			}
			
			pstinsObject.setFloat(3, object.getObjra().floatValue());
			
			pstinsObject.setFloat(4, object.getObjde().floatValue());
			
			if(object.getObjabsv()!=null){
				pstinsObject.setFloat(5, object.getObjabsv().floatValue());
			}
			else{
				pstinsObject.setNull(5,java.sql.Types.DOUBLE);
			}
			
			if(object.getObjmagv()!=null){
				pstinsObject.setFloat(6, object.getObjmagv().floatValue());
			}
			else{
				pstinsObject.setNull(6,java.sql.Types.DOUBLE);
			}
			
			if(object.getObjmagb()!=null){
				pstinsObject.setFloat(7, object.getObjmagb().floatValue());
			}
			else{
				pstinsObject.setNull(7,java.sql.Types.DOUBLE);
			}
			
			if(object.getObjmagr()!=null){
				pstinsObject.setFloat(8, object.getObjmagr().floatValue());
			}
			else{
				pstinsObject.setNull(8,java.sql.Types.DOUBLE);
			}
			
			if(object.getObjmagi()!=null){
				pstinsObject.setFloat(9, object.getObjmagi().floatValue());
			}
			else{
				pstinsObject.setNull(9,java.sql.Types.DOUBLE);
			}
			
			if(object.getObjbv()!=null){
				pstinsObject.setFloat(10, object.getObjbv().floatValue());
			}
			else{
				pstinsObject.setNull(10,java.sql.Types.DOUBLE);
			}
			
			if(object.getObjsptype()!=null){
				pstinsObject.setString(11, object.getObjsptype());
			}
			else{
				pstinsObject.setNull(11,java.sql.Types.CHAR);
			}
			
			if(object.getObjsptsubclass()!=null){
				pstinsObject.setString(12, object.getObjsptsubclass());
			}
			else{
				pstinsObject.setNull(12,java.sql.Types.CHAR);
			}
			
			if(object.getObjcoltemp()!=null){
				pstinsObject.setFloat(13, object.getObjcoltemp().floatValue());
			}
			else{
				pstinsObject.setNull(13,java.sql.Types.DOUBLE);
			}
			
			if(object.getObjlumclass()!=null){
				pstinsObject.setString(14, object.getObjlumclass());
			}
			else{
				pstinsObject.setNull(14,java.sql.Types.CHAR);
			}
			
			if(object.getObjteff()!=null){
				pstinsObject.setFloat(15, object.getObjteff().floatValue());
			}
			else{
				pstinsObject.setNull(15,java.sql.Types.DOUBLE);
			}
			
			if(object.getObjgravity()!=null){
				pstinsObject.setFloat(16, object.getObjgravity().floatValue());
			}
			else{
				pstinsObject.setNull(16,java.sql.Types.DOUBLE);
			}
			
			if(object.getObjmetal()!=null){
				pstinsObject.setFloat(17, object.getObjmetal().floatValue());
			}
			else{
				pstinsObject.setNull(17,java.sql.Types.DOUBLE);
			}
			
			if(object.getObjobs()!=null){
				pstinsObject.setString(18, object.getObjobs());
			}
			else{
				pstinsObject.setNull(18,java.sql.Types.CHAR);
			}
			
			System.out.println("pstinsObject="+pstinsObject);
			
			pstinsObject.executeUpdate();
			
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
}