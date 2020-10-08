package svo.gtc.adm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatFecha {

	Integer year = null;
	Integer month = null;
	
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	public DatFecha(String month, String year) {
		super();
		this.year = Integer.valueOf(year);
		this.month = Integer.valueOf(month);
	}
	
	public DatFecha(ResultSet resselTemp) throws SQLException{
		year = resselTemp.getInt("year");
		month = resselTemp.getInt("month");
	}
	
	public Timestamp fechaini() throws ParseException{
		String fechai = "01/"+month+"/"+year;
		Timestamp timeIni = ToTimeS(fechai);
		return timeIni;
	}
	
	public Timestamp fechaend() throws ParseException{
		String fechae = null;
		if(month==1 || month==3 || month==5|| month==7|| month==8 || month==10 || month==12){
			fechae="31/"+month+"/"+year;
		}else if(month==4 || month==6 || month==9 || month==11 ){
			fechae="30/"+month+"/"+year;
		}else if(month==2){
			fechae="27/"+month+"/"+year;
		}
		
		Timestamp timeEnd = ToTimeS(fechae);
		
		return timeEnd;
	}
	
	public Timestamp ToTimeS(String fecha) throws ParseException{
		DateFormat formatter;
	    formatter = new SimpleDateFormat("dd/MM/yyyy");
	    Date fech = (Date) formatter.parse(fecha);
	    Timestamp timeS = new Timestamp(fech.getTime());
	    
	    return timeS;
	}
	
	
	
	
	
}
