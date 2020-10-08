package svo.gtc.priv.objetos;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReducInfo {

	private Integer cuenta_user = null;
	private Integer cuenta_meg = null;
	private Integer cuenta_her = null;
	private String bibcode = null;
	
	
	public ReducInfo (ResultSet resselTemp) throws SQLException{
		cuenta_user = resselTemp.getInt("cuenta_user");
		cuenta_meg = resselTemp.getInt("cuenta_meg");
		cuenta_her = resselTemp.getInt("cuenta_her");
		bibcode = resselTemp.getString("bibcode").trim();
		
	}


	public Integer getCuenta_user() {
		return cuenta_user;
	}


	public void setCuenta_user(Integer cuenta_user) {
		this.cuenta_user = cuenta_user;
	}


	public Integer getCuenta_meg() {
		return cuenta_meg;
	}


	public void setCuenta_meg(Integer cuenta_meg) {
		this.cuenta_meg = cuenta_meg;
	}


	public Integer getCuenta_her() {
		return cuenta_her;
	}


	public void setCuenta_her(Integer cuenta_her) {
		this.cuenta_her = cuenta_her;
	}


	public String getBibcode() {
		return bibcode;
	}


	public void setBibcode(String bibcode) {
		this.bibcode = bibcode;
	}
	
	
}
