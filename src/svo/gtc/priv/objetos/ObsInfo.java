package svo.gtc.priv.objetos;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ObsInfo {
	
	private String program = null;
	private String block = null;
	private String filename = null;
	private String err_id = null;
	private String error = null;
	private Integer rev = null;
	private Integer prode_id = null;
	
	
	public Integer getRev() {
		return rev;
	}
	public void setRev(Integer rev) {
		this.rev = rev;
	}
	public String getProgram() {
		return program;
	} 
	public void setProgram(String program) {
		this.program = program;
	}
	public String getBlock() {
		return block;
	}
	public void setBlock(String block) {
		this.block = block;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getErr_id() {
		return err_id;
	}
	public void setErr_id(String err_id) {
		this.err_id = err_id;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public Integer getProde_id() {
		return prode_id;
	}
	public void setProde_id(Integer prode_id) {
		this.prode_id = prode_id;
	}
	public ObsInfo (ResultSet resselTemp){
		try {

			program = resselTemp.getString("prog_id").trim();
			block = resselTemp.getString("obl_id").trim();
			filename = resselTemp.getString("prode_filename").trim();
			err_id = resselTemp.getString("err_id").trim();
			error = resselTemp.getString("err_desc").trim();
			rev = Integer.valueOf(resselTemp.getString("prode_rev"));
			prode_id = Integer.valueOf(resselTemp.getString("prode_id"));
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
