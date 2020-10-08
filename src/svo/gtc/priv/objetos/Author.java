package svo.gtc.priv.objetos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import svo.gtc.db.DriverBD;
import svo.gtc.db.priv.DBPrivate;

public class Author {
	
	private Integer aut_id = null;
	private String aut_name = null;
	private String aut_email = null;
	private String aut_details = null; 
	public Integer getAut_id() {
		return aut_id;
	}
	public void setAut_id(Integer aut_id) {
		this.aut_id = aut_id;
	}
	public String getAut_name() {
		return aut_name;
	}
	public void setAut_name(String aut_name) {
		this.aut_name = aut_name;
	}
	public String getAut_email() {
		return aut_email;
	}
	public void setAut_email(String aut_email) {
		this.aut_email = aut_email;
	}
	public String getAut_details() {
		return aut_details;
	}
	public void setAut_details(String aut_details) {
		this.aut_details = aut_details;
	}
	public Author(String aut_name, String aut_email, String aut_details) {
		super();
		this.aut_name = aut_name;
		this.aut_email = aut_email;
		this.aut_details = aut_details;
	}
	public Author(ResultSet resselTemp) throws SQLException {
	try{
		aut_name = resselTemp.getString("aut_name");
		aut_email = resselTemp.getString("aut_email");
		aut_details = resselTemp.getString("aut_details");
	}catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	public Author(HttpServletRequest request){
		
		aut_id = Integer.valueOf(request.getParameter("aut"));
		aut_name = request.getParameter("name").trim();
		aut_email = request.getParameter("email").trim();
		aut_details = request.getParameter("det").trim();
	}
	
	public void updateAut() throws SQLException{
		//Creamos el ins_id apartir del número de datos guardados en la bbdd
		DriverBD driver = new  DriverBD();
		Connection con = driver.bdConexion();
	
		DBPrivate conex = new DBPrivate(con);
		
		conex.updateAut(this);
		
		con.close();
	}
	
	public void comprueba() throws Exception{
		if(aut_name.length()==0 && aut_email.length()==0 && aut_details.length()==0){
			throw new Exception("No ha modificado valores del usuario");
		}
		
	}
	

}

