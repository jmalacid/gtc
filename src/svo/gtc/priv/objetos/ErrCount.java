package svo.gtc.priv.objetos;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ErrCount {
	
	private String error_id = null;
	private Integer error_count = null;
	private Integer error_rev = null;
	private Integer error_pend = null;
	private String error_comment = null;
	private String error_desc = null;
	
	
	public String getError_id() {
		return error_id;
	}
	public void setError_id(String error_id) { 
		this.error_id = error_id;
	}
	public Integer getError_count() {
		return error_count;
	}
	public void setError_count(Integer error_count) {
		this.error_count = error_count;
	}
	public Integer getError_rev() {
		return error_rev;
	}
	public void setError_rev(Integer error_rev) {
		this.error_rev = error_rev;
	}
	public Integer getError_pend() {
		return error_pend;
	}
	public void setError_pend(Integer error_pend) {
		this.error_pend = error_pend;
	}
	public String getError_comment() {
		return error_comment;
	}
	public void setError_comment(String error_comment) {
		this.error_comment = error_comment;
	}
	public String getError_desc() {
		return error_desc;
	}
	public void setError_desc(String error_desc) {
		this.error_desc = error_desc;
	}
	public ErrCount(ResultSet resselTemp){
		
		try {

			error_id = resselTemp.getString("err_id");
			error_count = resselTemp.getInt("cantidad");
			error_rev = resselTemp.getInt("rev");
			error_pend = resselTemp.getInt("pend");
			error_comment = resselTemp.getString("err_comment");
			error_desc = resselTemp.getString("err_desc");
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
