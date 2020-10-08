package svo.gtc.db.web;

import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import svo.gtc.db.DriverBD;
import svo.gtc.db.instrument.InstrumentoDb;
import svo.gtc.db.modo.ModoAccess;
import svo.gtc.db.modo.ModoDb;

//Esta clase hace referencia al acceso a la tabla Object.


public class OverviewStats{
	
	Connection conex = null;
	
	public OverviewStats(Connection conex){
		this.conex=conex;
	}

	/**
	 * Obtiene los instrumentos registrados.
	 * @throws SQLException
	 */
	public List<InstrumentoDb> getInstruments() throws SQLException {
		List<InstrumentoDb> salida = new ArrayList<InstrumentoDb>();
		
		String query = "SELECT * FROM instrument order by ins_name;";

		//System.out.println(query);
		ResultSet resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			InstrumentoDb inst = new InstrumentoDb(resset);
			salida.add(inst);
		}
		resset.close();
		
		return salida;

	}
	
	/**
	 * Obtiene los modos de un instrumento.
	 * @throws SQLException
	 */
	public List<ModoDb> getModesByInst(String instId) throws SQLException {
		ModoAccess modoAccess = new ModoAccess(conex);
		
		ModoDb[] modos = modoAccess.selectByInsId(instId);
		
		return Arrays.asList(modos);
	}
	
	
	/**
	 * Obtiene las estadísticas por modo para un instrumento dado.
	 * @throws SQLException
	 */
	public List<Vector> getStatsByMode(String instId) throws SQLException {
		List<Vector> salida = new ArrayList<Vector>();
		
		// CIENCIA
		String query = "SELECT m.mod_name as modo " +
				" ,(select count(*) from productos b where b.mod_id=m.mod_id and b.mty_id='SCI') as cuentaRaw " +
				" ,(select sum(prod_filesize)/1024/1024/1024 from productos b where b.mod_id=m.mod_id and b.mty_id='SCI') as gbRaw " +
				" ,(select count(*) from productos b, pred_prod l, prodreducido r where b.mod_id=m.mod_id and l.prog_id=b.prog_id and l.obl_id=b.obl_id and l.prod_id=b.prod_id and r.pred_id=l.pred_id and b.mty_id='SCI') as cuentaRed " +
				" ,(select sum(pred_filesize)/1024/1024/1024 from productos b, pred_prod l, prodreducido r where b.mod_id=m.mod_id and l.prog_id=b.prog_id and l.obl_id=b.obl_id and l.prod_id=b.prod_id and r.pred_id=l.pred_id and b.mty_id='SCI') as gbRed " +
				" ,(select count(*) from productos b where b.mod_id=m.mod_id and b.mty_id!='SCI') as cuentaCal " +
				" ,(select sum(prod_filesize)/1024/1024/1024 from productos b where b.mod_id=m.mod_id and b.mty_id!='SCI') as gbCal " +
				" FROM modo m where ins_id = '" +instId +"'"+
				" ORDER BY m.mod_name;";

		//System.out.println(query);
		ResultSet resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			Vector fila = new Vector();
			
			OverviewStatItem item = new OverviewStatItem();
			
			item.setNumRaw(resset.getInt("cuentaRaw"));
			item.setGbRaw((int)resset.getDouble("gbRaw"));
			item.setNumRed(resset.getInt("cuentaRed"));
			item.setGbRed((int)resset.getDouble("gbRed"));
			item.setNumCal(resset.getInt("cuentaCal"));
			item.setGbCal((int)resset.getDouble("gbCal"));
			
			fila.add(resset.getString("modo"));
			fila.add(item);
			
			salida.add(fila);
		}
		resset.close();
		
		return salida;
	}
	
	
	/**
	 * Obtiene las estadísticas por modo para un instrumento dado y un modo dado por semestres.
	 * @throws SQLException
	 */
	public OverviewInfo[] getStatIns(String insId, String modoId) throws SQLException{
		
		String query = "select semester, modo, ins, cuentaRaw, gbRaw, cuentaRed, gbRed, cuentaCal, gbCal from overview where ins =  '"+insId+"' and modo= '"+modoId+"' order by semester;";
		
		ResultSet resset = conex.createStatement().executeQuery(query);
		
		Vector aux = new Vector();
		
		while (resset.next()) {

			// Obtenemos los resultado y los metemos en una cadena separada por -.-
			OverviewInfo result = new OverviewInfo(resset);
			
			aux.addElement(result);
		}

		return (OverviewInfo[]) aux.toArray(new OverviewInfo[0]);
		
	}
	
	
	/**
	 * Obtiene las estadísticas por modo para un instrumento dado.
	 * @throws SQLException
	 */
	public OverviewInfo[] getStatIns(String insId) throws SQLException{
		
		String query = "select modo, ins, sum(cuentaraw) as cuentaraw, sum(gbRaw) as gbRaw, sum(cuentaRed) as cuentaRed, sum(gbRed) as gbRed, sum(cuentaCal) as cuentacal, sum(gbCal) as gbcal from overview where ins =  '"+insId+"' group by ins, modo;";
		

		
		ResultSet resset = conex.createStatement().executeQuery(query);
		
		Vector aux = new Vector();
		
		while (resset.next()) {

			// Obtenemos los resultado y los metemos en una cadena separada por -.-
			OverviewInfo result = new OverviewInfo(resset);
			
			aux.addElement(result);
		}

		return (OverviewInfo[]) aux.toArray(new OverviewInfo[0]);
		
	}
	
	/**
	 * Obtiene las estadísticas por modo para un instrumento dado.
	 * @throws SQLException
	 */
	public PrivInfo getStatPriv(String ins) throws SQLException{
		
		String query = "select count(distinct(prog_id)) progs, count(distinct(prog_id, obl_id)) obls,count(*) prods from productos where ins_id='"+ins+"' and "
				+ "((((prod_initime)>now()-('1 year'::interval)) and prog_id not like '%DDT%') or (((prod_initime)>now()-('6 months'::interval)) and prog_id like '%DDT%')) and mty_id='SCI'";
		
		ResultSet resset = conex.createStatement().executeQuery(query);
		
		PrivInfo inf=null;
		
		while (resset.next()) {

			inf = new PrivInfo(resset);
			
		}

		return inf;	
	}
	
	/**
	 * Obtiene las estadísticas por semestre para un instrumento y modo dados.
	 * @throws SQLException
	 */
	public List<Vector> getStatsBySemester(String instId, String mod_id) throws SQLException {
		List<Vector> salida = new ArrayList<Vector>();
		
		// CIENCIA
		String query = "SELECT p.prog_year||p.prog_semester as semester " +
				" ,(select count(*) from productos b, programa bp where b.mod_id='"+mod_id+"' and b.prog_id=bp.prog_id and bp.prog_year=p.prog_year and bp.prog_semester=p.prog_semester and b.mty_id='SCI') as cuentaRaw " +
				" ,(select sum(prod_filesize)/1024/1024/1024 from productos b, programa bp where b.mod_id='"+mod_id+"' and b.prog_id=bp.prog_id and bp.prog_year=p.prog_year and bp.prog_semester=p.prog_semester and b.mty_id='SCI') as gbRaw " +
				" ,(select count(*) from productos b, programa bp, pred_prod l, prodreducido r where b.mod_id='"+mod_id+"' and b.prog_id=bp.prog_id and bp.prog_year=p.prog_year and bp.prog_semester=p.prog_semester and l.prog_id=b.prog_id and l.obl_id=b.obl_id and l.prod_id=b.prod_id and r.pred_id=l.pred_id) as cuentaRed " +
				" ,(select sum(pred_filesize)/1024/1024/1024 from productos b, programa bp, pred_prod l, prodreducido r where b.mod_id='"+mod_id+"' and b.prog_id=bp.prog_id and bp.prog_year=p.prog_year and bp.prog_semester=p.prog_semester and l.prog_id=b.prog_id and l.obl_id=b.obl_id and l.prod_id=b.prod_id and r.pred_id=l.pred_id) as gbRed " +
				" ,(select count(*) from productos b, programa bp where b.mod_id='"+mod_id+"' and b.prog_id=bp.prog_id and bp.prog_year=p.prog_year and bp.prog_semester=p.prog_semester and b.mty_id!='SCI') as cuentaCal " +
				" ,(select sum(prod_filesize)/1024/1024/1024 from productos b, programa bp where b.mod_id='"+mod_id+"' and b.prog_id=bp.prog_id and bp.prog_year=p.prog_year and bp.prog_semester=p.prog_semester and b.mty_id!='SCI') as gbCal " +
				" FROM programa p" +
				" GROUP BY p.prog_year, p.prog_semester ORDER BY semester;";

		//System.out.println(query);
		ResultSet resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			Vector fila = new Vector();
			
			OverviewStatItem item = new OverviewStatItem();
			
			item.setNumRaw(resset.getInt("cuentaRaw"));
			item.setGbRaw((int)resset.getDouble("gbRaw"));
			item.setNumRed(resset.getInt("cuentaRed"));
			item.setGbRed((int)resset.getDouble("gbRed"));
			item.setNumCal(resset.getInt("cuentaCal"));
			item.setGbCal((int)resset.getDouble("gbCal"));
			
			fila.add(resset.getString("semester"));
			fila.add(item);
			
			salida.add(fila);
		}
		resset.close();
		
		return salida;
	}
	
	
	
	public static void main(String[] args) throws SQLException{
		Connection conex= null;

		DriverBD drvBd = new DriverBD();

		try {
			conex = drvBd.bdConexion();
		} 
		catch (SQLException errconexion) {
			errconexion.printStackTrace();
		}
		
		OverviewStats stats = new OverviewStats(conex);

		List<Vector> salida = stats.getStatsByMode("OSI");
		
		for(Vector e: salida){
			String titulo = (String)e.elementAt(0);
			OverviewStatItem item = (OverviewStatItem)e.elementAt(1);
			System.out.println(titulo+"   "+item.getNumRaw());
		}

		System.out.println();
		
		salida = stats.getStatsBySemester("OSI","BBI");
		
		for(Vector e: salida){
			String titulo = (String)e.elementAt(0);
			OverviewStatItem item = (OverviewStatItem)e.elementAt(1);
			System.out.println(titulo+"   "+item.getNumRaw());
		}
		
		
	}
	
}
