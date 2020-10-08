package svo.gtc.adm;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatHistogram {

	String fecha = null;
	Integer num = null;

	public DatHistogram(ResultSet resselTemp) throws SQLException{
		fecha = resselTemp.getString("month")+"-"+resselTemp.getString("year");
		num = resselTemp.getInt("num");
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}
	
}
