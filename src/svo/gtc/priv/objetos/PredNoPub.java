package svo.gtc.priv.objetos;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PredNoPub {
	private String prog_id = null;
	private String obl_id = null;
	private Integer prod_id = null;
	private String bibcode = null;
	public String getProg_id() {
		return prog_id;
	}
	public void setProg_id(String prog_id) {
		this.prog_id = prog_id;
	}
	public String getObl_id() {
		return obl_id;
	}
	public void setObl_id(String obl_id) {
		this.obl_id = obl_id;
	}
	public Integer getProd_id() {
		return prod_id;
	}
	public void setProd_id(Integer prod_id) {
		this.prod_id = prod_id;
	}
	public String getBibcode() {
		return bibcode;
	}
	public void setBibcode(String bibcode) {
		this.bibcode = bibcode;
	}
	
	public PredNoPub(ResultSet resselTemp) throws SQLException{
		prog_id = resselTemp.getString("prog_id");
		obl_id = resselTemp.getString("obl_id");
		prod_id = resselTemp.getInt("prod_id");
		bibcode = resselTemp.getString("bibcode");
	}
}
