package svo.gtc.adm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class DatQuery {

	Connection conex;
	
	private PreparedStatement viewDat = null;
	private PreparedStatement viewIni = null;
	private PreparedStatement viewEnd = null;
	
	private String qViewDat = "select count(*) as num, date_part('year',prod_initime) as year, date_part('month',prod_initime) as month from productos where prod_initime between ? and ? and mty_id='SCI' group by year, month order by year, month";
	private String qViewIni = "select date_part('year',prod_initime) as year, date_part('month',prod_initime) as month from productos where prod_initime is not null order by prod_initime limit 1";
	private String qViewEnd = "select date_part('year',prod_initime) as year, date_part('month',prod_initime) as month from productos where prod_initime is not null order by prod_initime desc limit 1";
	
public DatQuery(Connection con) throws SQLException{
		
		this.conex = con;
		
		conex.setAutoCommit(true);
		viewDat = conex.prepareStatement(qViewDat);
		viewIni = conex.prepareStatement(qViewIni);
		viewEnd = conex.prepareStatement(qViewEnd);
}

public DatHistogram[] viewDat(Timestamp ini, Timestamp end) throws SQLException, ParseException{
	
	viewDat.setTimestamp(1, ini);
	viewDat.setTimestamp(2, end);
	ResultSet result = viewDat.executeQuery();
	
	List<DatHistogram> list=new ArrayList<DatHistogram>();
	
		while (result.next()) {
			
			DatHistogram Resultado = new DatHistogram(result);

			list.add(Resultado);
		}

		DatHistogram res[]=list.toArray(new DatHistogram[0]);
	return res;		
}
public DatFecha ini() throws SQLException{
	ResultSet result = viewIni.executeQuery();
	
	DatFecha fech_ini=null;
	
	while (result.next()) {
		
		fech_ini= new DatFecha(result);
	}
	return fech_ini;
}

public DatFecha end() throws SQLException{
	ResultSet result = viewEnd.executeQuery();
	
	DatFecha fech_end=null;
	
	while (result.next()) {
		
		fech_end= new DatFecha(result);
	}
	return fech_end;
}

}
