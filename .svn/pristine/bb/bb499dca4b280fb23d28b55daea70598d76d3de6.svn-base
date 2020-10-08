package svo.sepub.results;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import svo.sepub.db.AccessDB;
import svo.gtc.db.DriverBD;

public class Bibcode {
	
	public Integer bus_id = null;
	public String bibcode = null;
	public String desc = null;
	public Integer est_id = null;
	public String estado = null;
	public String proyecto = null;
	
	
	public String getBibcode() {
		return bibcode;
	}
	public void setBibcode(String bibcode) {
		this.bibcode = bibcode;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Integer getBus_id() {
		return bus_id;
	}
	public void setBus_id(Integer bus_id) {
		this.bus_id = bus_id;
	}
	
	public String getProyecto() {
		return proyecto;
	}
	public void setProyecto(String proyecto) {
		this.proyecto = proyecto;
	}
	
	public Integer getEst_id() {
		return est_id;
	}
	public void setEst_id(Integer est_id) {
		this.est_id = est_id;
	}
	public Bibcode(ResultSet result) throws SQLException{
		bus_id = result.getInt("bus_id");
		bibcode = result.getString("bib_id");
		desc = result.getString("bus_comment");
		est_id = result.getInt("est_id");
		proyecto = result.getString("pro_id");
		try{
			estado = result.getString("est_desc");
		}catch(Exception e){
			estado = null;
		}
	}
	
	public Bibcode(HttpServletRequest request){
		
		bus_id = Integer.valueOf(request.getParameter("id"));
		try {
			est_id = Integer.valueOf(request.getParameter("est_id"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		desc = request.getParameter("desc");
	}
	
	public void AddChange() throws Exception{
		//primero comprobamos que no todos los campos son nulos
		if((desc==null || desc.length()==0 ) && est_id==null ){
			throw new Exception("No se han introducido cambios");
		}
		
		DriverBD DDB = new DriverBD();
		Connection conex = DDB.bdConexion();

		AccessDB adb = new AccessDB(conex);
		
		if(est_id !=null){
			adb.changeEst(est_id, bus_id);
		}
		if(desc!=null && desc.length()>0){
			adb.changeCom(desc, bus_id);
		}
		conex.close();
	}
}
