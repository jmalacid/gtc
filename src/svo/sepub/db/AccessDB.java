package svo.sepub.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import svo.sepub.results.Bibcode;

public class AccessDB {
	
	Connection conex;
	
	private PreparedStatement viewBib = null;
	private PreparedStatement viewBibEst = null;
	private PreparedStatement viewBibcode = null;
	private PreparedStatement viewWords = null;
	private PreparedStatement existeBib = null;
	private PreparedStatement existeBibBus = null;
	private PreparedStatement addBib = null;
	private PreparedStatement maxBus = null;
	private PreparedStatement addBus = null;
	private PreparedStatement viewEsts = null;
	private PreparedStatement changeEst = null;
	private PreparedStatement changeCom = null;
	private PreparedStatement viewGTC = null;
	

	private String qViewBib = "SELECT BUS_ID, BIB_ID, EST_ID, BUS_COMMENT, pro_id FROM BUSQUEDA WHERE PRO_ID=? order by bib_id desc";
	private String qViewBibEst = "SELECT BUS_ID, BIB_ID, EST_ID, BUS_COMMENT, pro_id FROM BUSQUEDA WHERE PRO_ID=? AND EST_ID=? order by bib_id desc";
	private String qViewBibcode = "SELECT BUS_ID, BIB_ID, busqueda.EST_ID, BUS_COMMENT, pro_id, est_desc FROM BUSQUEDA, estado WHERE estado.est_id=busqueda.est_id and bus_id=?";
	private String qViewWords = "select pro_busqueda from proyecto where pro_id=?";
	private String qExisteBib = "select count(*) from bibcode where bib_id=?";
	private String qExisteBibBus = "select count(*) from busqueda where bib_id=? and pro_id=?";
	private String qAddBib = "insert into bibcode (bib_id) values (?)";
	private String qMaxBus = "SELECT max(bus_id)+1 as bus_id from busqueda;";
	private String qAddBus = "insert into busqueda (bus_id,bib_id,est_id,pro_id) values (?,?,0,?)";
	private String qViewEsts = "select est_id, est_desc from estado";
	private String qChangeEst = "update busqueda set est_id=? where bus_id=?";
	private String qChangeCom = "update busqueda set bus_comment=? where bus_id=?";
	private String qViewGTC = "select pub_bibcode from publication;";
	
	public AccessDB(Connection con) throws SQLException{
		
		this.conex = con;
		
		conex.setAutoCommit(true);
		viewBib = conex.prepareStatement(qViewBib);
		viewBibcode = conex.prepareStatement(qViewBibcode);
		viewBibEst = conex.prepareStatement(qViewBibEst);
		viewWords = conex.prepareStatement(qViewWords);
		existeBib = conex.prepareStatement(qExisteBib);
		existeBibBus = conex.prepareStatement(qExisteBibBus);
		addBib = conex.prepareStatement(qAddBib);
		maxBus = conex.prepareStatement(qMaxBus);
		addBus = conex.prepareStatement(qAddBus);
		viewEsts = conex.prepareStatement(qViewEsts);
		changeEst = conex.prepareStatement(qChangeEst);
		changeCom = conex.prepareStatement(qChangeCom);
		viewGTC = conex.prepareStatement(qViewGTC);
	}
	
	public Bibcode[] viewBib(String proyect) throws SQLException{
		
		viewBib.setString(1, proyect);
		
		ResultSet result = viewBib.executeQuery();
		
		Vector<Bibcode> aux = new Vector<Bibcode>();
		
			while (result.next()) {
				
				Bibcode Resultado = new Bibcode(result);

				aux.addElement(Resultado);
				
			}
		
			
		return (Bibcode[])aux.toArray(new Bibcode[0]);		
	}
	
	public Bibcode viewBibcode(Integer bus_id) throws SQLException{
		
		viewBibcode.setInt(1, bus_id);
		
		ResultSet result = viewBibcode.executeQuery();
		
		Bibcode Resultado = null;

		while (result.next()) {
				
			Resultado = new Bibcode(result);

		}
			
		return Resultado;		
	}
	public Bibcode[] viewBibEst(String proyect, Integer est) throws SQLException{
		
		viewBibEst.setString(1, proyect);
		viewBibEst.setInt(2, est);
		
		ResultSet result = viewBibEst.executeQuery();
		
		Vector<Bibcode> aux = new Vector<Bibcode>();
		

			while (result.next()) {
				
				Bibcode Resultado = new Bibcode(result);

				aux.addElement(Resultado);
			}

			
		return (Bibcode[])aux.toArray(new Bibcode[0]);		
	}
	
	public String viewWords(String proyect) throws SQLException{
		
		viewWords.setString(1, proyect);
		
		ResultSet result = viewWords.executeQuery();
		
		String words = null;
		if(result.next()){
			words=result.getString("pro_busqueda").trim();
		}
		
		return words;
	}
	public Integer existeBib(String bib) throws SQLException{
		
		existeBib.setString(1, bib);
		
		ResultSet result = existeBib.executeQuery();
		
		Integer count = null;
		if(result.next()){
			count=result.getInt(1);
		}
		
		return count;
	}
	
	public Integer existeBibBus(String bib, String pro) throws SQLException{
		
		existeBibBus.setString(1, bib);
		existeBibBus.setString(2, pro);
		
		ResultSet result = existeBibBus.executeQuery();
		
		Integer count = null;
		if(result.next()){
			count=result.getInt(1);
		}
		
		return count;
	}

	public void addBib(String bib) throws SQLException{
	
	addBib.setString(1, bib);

	//System.out.println(addBib.toString());
	addBib.executeUpdate();

	}
	
	public void addBus(String bib, String pro) throws SQLException{

		Integer bus_id= 1;
		
		
		try{
			conex.setAutoCommit(false);
			
			ResultSet resselTemp = maxBus.executeQuery();
			
			if(resselTemp.next()){
				bus_id = resselTemp.getInt("bus_id");
			}
			
			addBus.setInt(1, bus_id);
			addBus.setString(2, bib);
			addBus.setString(3, pro);
		
			//System.out.println(addBus.toString());
			addBus.executeUpdate();
			conex.commit();
		
		}catch(SQLException e){
			conex.rollback();
			conex.setAutoCommit(true);
			throw e;
		}
		
		conex.setAutoCommit(true);
	}
	
	public String[] viewEsts() throws SQLException {
		
		ResultSet result = viewEsts.executeQuery();
		
		//System.out.println(viewEsts);
		Vector<String> aux = new Vector<String>();
		
			while (result.next()) {
				
				String Resultado = result.getInt("est_id")+"-.-"+result.getString("est_desc");
				//System.out.println(Resultado);
				aux.addElement(Resultado);
			}
		
			
		return (String[])aux.toArray(new String[0]);
	}
	
	public void changeEst(Integer est_id, Integer bus_id) throws SQLException{
		
		changeEst.setInt(1, est_id);
		changeEst.setInt(2, bus_id);

		changeEst.executeUpdate();
	}
	
	public void changeCom(String com, Integer bus_id) throws SQLException{
			
			changeCom.setString(1, com);
			changeCom.setInt(2, bus_id);
			
			changeCom.executeUpdate();
	}
	
	public String[] viewGTC() throws SQLException {
		
		ResultSet result = viewGTC.executeQuery();
		
		//System.out.println(viewEsts);
		Vector<String> aux = new Vector<String>();
		
			while (result.next()) {
				
				String Resultado = result.getString("pub_bibcode");
				aux.addElement(Resultado);
			}
		
			
		return (String[])aux.toArray(new String[0]);
	}
}
