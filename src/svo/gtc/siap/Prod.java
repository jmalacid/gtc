package svo.gtc.siap;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Prod {
	
	String prog = null;
	String obl = null;
	Integer prod = null;
	
	
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
	
	public Prod(ResultSet resSet) throws SQLException{

		prog 	= resSet.getString("prog_id".trim()).trim();
		obl 	= resSet.getString("obl_id".trim()).trim();
		prod 	= new Integer(resSet.getInt("prod_id"));
		
}
	
	

}
