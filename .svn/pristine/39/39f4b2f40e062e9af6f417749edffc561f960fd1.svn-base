package svo.gtc.db.web;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OverviewInfo {
	
	private String semestre = null;
	private String modo = null;
	private String ins = null;
	private Integer cuentaRaw = 0;
	private Integer	gbRaw=0;
	private Integer numRed=0;
	private Integer	gbRed=0;
	private Integer numCal=0;
	private Integer gbCal=0;
	
	
	public String getSemestre() {
		return semestre;
	}
	public void setSemestre(String semestre) {
		this.semestre = semestre;
	}
	public String getModo() {
		return modo;
	}
	public void setModo(String modo) {
		this.modo = modo;
	}
	public String getIns() {
		return ins;
	}
	public void setIns(String ins) {
		this.ins = ins;
	}
	public Integer getCuentaRaw() {
		return cuentaRaw;
	}
	public void setCuentaRaw(Integer cuentaRaw) {
		this.cuentaRaw = cuentaRaw;
	}
	public Integer getGbRaw() {
		return gbRaw;
	}
	public void setGbRaw(Integer gbRaw) {
		this.gbRaw = gbRaw;
	}
	public Integer getNumRed() {
		return numRed;
	}
	public void setNumRed(Integer numRed) {
		this.numRed = numRed;
	}
	public Integer getGbRed() {
		return gbRed;
	}
	public void setGbRed(Integer gbRed) {
		this.gbRed = gbRed;
	}
	public Integer getNumCal() {
		return numCal;
	}
	public void setNumCal(Integer numCal) {
		this.numCal = numCal;
	}
	public Integer getGbCal() {
		return gbCal;
	}
	public void setGbCal(Integer gbCal) {
		this.gbCal = gbCal;
	}

	
	public OverviewInfo(ResultSet resselTemp) throws SQLException{
		try{
			semestre = resselTemp.getString("semester");
		}catch(Exception e){
			semestre = null;
		}
		modo = resselTemp.getString("modo");
		ins = resselTemp.getString("ins");
		cuentaRaw = resselTemp.getInt("cuentaraw");
		gbRaw = resselTemp.getInt("gbRaw");
		numRed = resselTemp.getInt("cuentared");
		gbRed = resselTemp.getInt("gbRed");
		numCal = resselTemp.getInt("cuentaCal");
		gbCal = resselTemp.getInt("gbcal");
		
	}

}
