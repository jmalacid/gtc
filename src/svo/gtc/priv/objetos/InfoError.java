package svo.gtc.priv.objetos;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InfoError {
	
	private String prog = null;
	private String obl= null;
	
	
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
	
	public InfoError (ResultSet resselTemp){
		try {

			prog = resselTemp.getString("prog_id");
			obl = resselTemp.getString("obl_id");
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
