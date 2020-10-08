package svo.gtc.db.web;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PrivInfo {

	private Integer progs,obls,prods;

	public Integer getProgs() {
		return progs;
	}

	public void setProgs(Integer progs) {
		this.progs = progs;
	}

	public Integer getObls() {
		return obls;
	}

	public void setObls(Integer obls) {
		this.obls = obls;
	}

	public Integer getProds() {
		return prods;
	}

	public void setProds(Integer prods) {
		this.prods = prods;
	}
	
	public PrivInfo(ResultSet resselTemp) throws SQLException{
		progs = resselTemp.getInt("progs");
		obls = resselTemp.getInt("obls");
		prods = resselTemp.getInt("prods");
		
	}
}
