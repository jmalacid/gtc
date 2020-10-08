package svo.gtc.ssap;

import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 *@author J. Manuel Alacid (jmalacid@cab.inta-csic.es)
*/


public class ConsultaSsap{
	static Logger logger = Logger.getLogger("svo.gtc");	
	
	Connection conex = null;
	ResultSet resSet = null;
	VOObject formulario = null;
	//UsuarioDb usuario	=	null;

   //Ordenes SQL
	
	public ConsultaSsap(Connection conex, VOObject formulario) throws SQLException{
		this.conex=conex;
		this.formulario=formulario;
		String query = queryWeb();
		
		//System.out.println(query);
		try{
		this.resSet=conex.createStatement().executeQuery(query);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}


	}
	
	/**
	 * Devuelve el siguiente resultado de la query web.
	 * 
	 * @return WebResult
	 * @throws SQLException
	 */
	public ResultSsap getNext() throws SQLException{
		if(this.resSet.next()==true){
			ResultSsap salida = new ResultSsap(this.resSet);
			
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
		String query = "SELECT distinct(pred.pred_id), pred.pred_ra, pred.pred_de, pred.pred_filesize, prod.ins_id  ";
		query += predicadoQuery();

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
		String query = " FROM prodreducido as pred, pred_prod as pp, proddatos as prod";

		query +=	" WHERE pred.pred_id=pp.pred_id and pp.prod_id=prod.prod_id ";
		
		String join = " AND ";
		
		// Busquedas por Prod id y coordenadas:
		if(formulario.getPos()!=null && formulario.getSr()!=null){
			
			//query += join+" ( ";
			
			double ra = formulario.getRa();
			double de = formulario.getDec();
			double rad = formulario.getSr();
			
			query += join +"( (prod.prod_point @ scircle'<("+ra+"d,"+de+"d), "+rad+"d>'=true ) OR "
					+ "(prod.prod_polig && scircle'<("+ra+"d,"+de+"d), "+rad+"d>'=true) OR "
							+ "(prod.prod_polig2 && scircle'<("+ra+"d,"+de+"d), "+rad+"d>'=true))";
					/*query += " acos( cos(radians(pred.pred_de))*cos(radians("
					+ de
					+ "))*cos(radians(pred.pred_ra - "
					+ ra
					+ ")) + sin(radians(pred.pred_de))*sin(radians("
					+ de
					+ ")) ) < radians("
					+ rad
					+ ") ";

			query += " ) ";*/
			join=" AND ";
		}

		//Búsqueda por Band
		if(formulario.getBand_id()!=null){
			
		}else if(formulario.getBand_min()!=null||formulario.getBand_max()!=null){
			if(formulario.getBand_min()!=null){
				double band_min = formulario.getBand_min();
				query += join + " (prod.prod_band_min >"+band_min+" OR prod.prod_band_min >"+band_min+") ";
			}
			if(formulario.getBand_max()!=null){
				double band_max = formulario.getBand_max();
				query += join + " (prod.prod_band_max <"+band_max+" OR prod.prod_band_max >"+band_max+") ";
			}
		}
		
		
		//Búsqueda por Time
		if(formulario.getYy_i()!=null ||formulario.getYy_e()!=null ){
			query += join + " (prod_initime>'"+formulario.getYy_i()+"-"+formulario.getMm_i()+"-"+formulario.getDd_i()+" 00:00:00'"
					+ " and prod_initime<'"+formulario.getYy_e()+"-"+formulario.getMm_e()+"-"+formulario.getDd_e()+" 23:59:29')";
		}

		//Busquedas por OBS_id:
		if(formulario.getId()!=null){
			query += join + "pred.pred_id="+formulario.getId();
			join=" AND ";
		}
		
		//System.out.println(query);
		return query;
	}
	
	
}
