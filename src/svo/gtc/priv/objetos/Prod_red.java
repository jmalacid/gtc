package svo.gtc.priv.objetos;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Prod_red {

	private String filename = null;
	private String prog_id= null;
	private String obl_id = null;
	private Integer prod_id = null;
	private String subm_id = null;
	private Double prod_ra = null;
	private Double prod_dec = null;
	private Integer pred_id = null;
	
	
	public Prod_red (ResultSet resselTemp) throws SQLException{
			prog_id = resselTemp.getString("prog_id").trim();
			obl_id = resselTemp.getString("obl_id").trim();
			prod_id = resselTemp.getInt("prod_id");
			subm_id = resselTemp.getString("subm_id").trim();
			prod_ra = resselTemp.getDouble("prod_ra");
			prod_dec = resselTemp.getDouble("prod_de");
	}

	public Prod_red (String filename, String prog_id, String obl_id){
		this.filename=filename;
		this.prog_id=prog_id;
		this.obl_id=obl_id;
		
	}
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


	public String getSubm_id() {
		return subm_id;
	}


	public void setSubm_id(String subm_id) {
		this.subm_id = subm_id;
	}


	public Double getProd_ra() {
		return prod_ra;
	}


	public void setProd_ra(Double prod_ra) {
		this.prod_ra = prod_ra;
	}


	public Double getProd_dec() {
		return prod_dec;
	}


	public void setProd_dec(Double prod_dec) {
		this.prod_dec = prod_dec;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Integer getPred_id() {
		return pred_id;
	}

	public void setPred_id(Integer pred_id) {
		this.pred_id = pred_id;
	}
	
}
