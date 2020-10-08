package svo.gtc.priv.objetos;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

public class ProdKey {
	
	private String filname = null;
	private String prog = null;
	private String obl = null;
	private Integer prod = null;
	private String ra = null;
	private String dec = null;
	
	public ProdKey(ResultSet resselTemp){
		try {

			filname = resselTemp.getString("bpath_path").trim()+resselTemp.getString("prod_path").trim()+resselTemp.getString("prod_filename").trim();
			prog = resselTemp.getString("prog_id").trim();
			obl = resselTemp.getString("obl_id").trim();
			prod = Integer.valueOf(resselTemp.getString("prod_id"));
			
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try{
			ra = resselTemp.getString("prod_ra").trim();
			dec = resselTemp.getString("prod_de").trim();
		}catch(Exception e){
			
		}
		
	}
	
	public String getFilname() {
		return filname;
	}
	public void setFilname(String filname) {
		this.filname = filname;
	}
	public String getProg() {
		return prog;
	}
	public void setProg(String prog) {
		this.prog = prog;
	}
	public String getObl() {
		return obl;
	}
	public void setObl(String obl) {
		this.obl = obl;
	}
	public Integer getProd() {
		return prod;
	}
	public void setProd(Integer prod) {
		this.prod = prod;
	}

	public String getRa() {
		return ra;
	}

	public void setRa(String ra) {
		this.ra = ra;
	}

	public String getDec() {
		return dec;
	}

	public void setDec(String dec) {
		this.dec = dec;
	}

}
