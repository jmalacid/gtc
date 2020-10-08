package svo.gtc.db.usuario;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserPI {
	
	private String usr_id = null;
	private String usr_email = null;
	private Integer usr_est = null;
	
	
	
	
	public String getUsr_id() {
		return usr_id;
	}
	public void setUsr_id(String usr_id) {
		this.usr_id = usr_id;
	}
	public String getUsr_email() {
		return usr_email;
	}
	public void setUsr_email(String usr_email) {
		this.usr_email = usr_email;
	}
	public Integer getUsr_est() {
		return usr_est;
	}
	public void setUsr_est(Integer usr_est) {
		this.usr_est = usr_est;
	}
	
	public UserPI(ResultSet resset) throws SQLException{
		this.usr_id 		= resset.getString("usr_id");
		this.usr_email		= resset.getString("usr_email");
		this.usr_est		= resset.getInt("usr_est");
	}

}
