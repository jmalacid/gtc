package svo.gtc.siap;

import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 *@author J. Manuel Alacid (jmalacid@cab.inta-csic.es)
*/


public class ConsultaSiap{
	static Logger logger = Logger.getLogger("svo.gtc");	
	
	Connection conex = null;
	ResultSet resSet = null;
	VOObject formulario = null;
	String tipe = null;
	//UsuarioDb usuario	=	null;

   //Ordenes SQL
	
	public ConsultaSiap(Connection conex, VOObject formulario, String tipe) throws SQLException{
		this.conex=conex;
		this.formulario=formulario;
		this.tipe=tipe;
		
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
	public ResultSiap getNext() throws SQLException{
		if(this.resSet.next()==true){
			ResultSiap salida = new ResultSiap(this.resSet);
			
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
		
		//Añadimos si son datos de usuario o reducidos osiris
		if(tipe.equals("user")){
			query += join + "pred.pred_type='USER' ";
		}else if(tipe.equals("osiris")){
			query += join + "pred.pred_type='HERVE' ";
		}
		
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
					+ ") ";*/

			//query += " ) ";
			join=" AND ";
		}

		
		if(tipe.equals("user")){
			// Busquedas por código de istrumentos:
			if(formulario.getInstrument()!=null && formulario.getInstrument().length()>0){
				
				if(formulario.getInstrument().equalsIgnoreCase("OSIRIS")){
					query += join+" prod.ins_id='OSI' AND prod.mod_id='BBI'";
					join=" AND ";
				}else if(formulario.getInstrument().equalsIgnoreCase("CANARICAM")){
					query += join+" prod.ins_id='CC' AND prod.mod_id='IMG'";
					join=" AND ";
				}else if(formulario.getInstrument().equalsIgnoreCase("CIRCE")){
					query += join+" prod.ins_id='CIR' AND prod.mod_id='IMG'";
					join=" AND ";
				}else if(formulario.getInstrument().equalsIgnoreCase("EMIR")){
					query += join+" prod.ins_id='EMIR' AND prod.mod_id!='SPE'";
					join=" AND ";
				}else if(formulario.getInstrument().equalsIgnoreCase("HIPERCAM")){
					query += join+" prod.ins_id='HIP' AND prod.mod_id='IMG'";
					join=" AND ";
				}else{
					query += join+" ((prod.ins_id='OSI' AND prod.mod_id='BBI') OR ( prod.ins_id='CC' AND prod.mod_id='IMG') OR ( prod.ins_id='CIR' AND prod.mod_id='IMG') OR ( prod.ins_id='EMIR' AND prod.mod_id!='SPE') OR ( prod.ins_id='HIP' AND prod.mod_id='IMG'))";
					join=" AND ";
				}
			}else if(tipe.equals("osiris")){
				query += join+" prod.ins_id='OSI' AND prod.mod_id='BBI'";
				join=" AND ";
			}
		}else{
			query += join+" ((prod.ins_id='OSI' AND prod.mod_id='BBI') OR ( prod.ins_id='CC' AND prod.mod_id='IMG')  OR ( prod.ins_id='CIR' AND prod.mod_id='IMG') OR ( prod.ins_id='EMIR' AND prod.mod_id!='SPE') OR ( prod.ins_id='HIP' AND prod.mod_id='IMG'))";
			join=" AND ";
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
