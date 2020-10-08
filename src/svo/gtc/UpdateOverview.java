package svo.gtc;

import java.sql.Connection;
import java.sql.SQLException;

import svo.gtc.db.DriverBD;

public class UpdateOverview {

	public static void main(String[] args) {
		System.out.print(update());
	}
	/**
	 * Realiza los delete y los inserts
	 * 
	
	 */
	public static String update(){
		String error = "";
		
		try{
			deleteOverview();
			insertOverview();
			deleteOverviewSemester();
		}catch(Exception e){
			error= e.getMessage();
		}
		
		if(error.length()==0){
			error ="Se ha actualizado correctamente";
		}
		
		return error;
	}

	
	/**
	 * Borra los valores antiguos de la tabla overview
	 * 
	 */
	private static void deleteOverview() throws Exception{
		/////////////////////////////////////////////////////
		////  Conexi�n con la Base de Datos                                    
		/////////////////////////////////////////////////////

		Connection conex = null;

		DriverBD con = new  DriverBD();

		try {
			conex = con.bdConexion();
			conex.createStatement().executeUpdate("DELETE FROM overview");
			
			conex.close();
		} catch (SQLException errconexion)  {
			throw new Exception ("No se ha podido borrar la base de datos");
		}


	}
	
	/**
	 * Borra los valores nulos
	 * 
	 */
	private static void deleteOverviewSemester() throws Exception{
		/////////////////////////////////////////////////////
		////  Conexi�n con la Base de Datos                                    
		/////////////////////////////////////////////////////

		Connection conex = null;

		DriverBD con = new  DriverBD();

		try {
			conex = con.bdConexion();
			conex.createStatement().executeUpdate("DELETE FROM overview where semester is null");
			
			conex.close();
		} catch (SQLException errconexion)  {
			throw new Exception ("No se ha podido borrar la base de datos");
		}


	}
	
	/**
	 * Borra EMIR Imaging, no es un modo real...
	 * 
	 */
	private static void deleteOverviewEMIRI() throws Exception{
		/////////////////////////////////////////////////////
		////  Conexi�n con la Base de Datos                                    
		/////////////////////////////////////////////////////

		Connection conex = null;

		DriverBD con = new  DriverBD();

		try {
			conex = con.bdConexion();
			conex.createStatement().executeUpdate("DELETE FROM overview where modo='EMIRImaging'");
			
			conex.close();
		} catch (SQLException errconexion)  {
			throw new Exception ("No se ha podido borrar la base de datos");
		}


	}
	
	/**
	 * Inserta los nuevos valores en la tabla overview
	 * 
	 */
	private static void insertOverview() throws Exception{
		/////////////////////////////////////////////////////
		////  Conexi�n con la Base de Datos                                    
		/////////////////////////////////////////////////////

		Connection conex = null;

		DriverBD con = new  DriverBD();

		try {
			conex = con.bdConexion();
			
			String query = "INSERT into overview (semester, modo, ins, cuentaraw, gbraw, cuentared, gbred, cuentacal, gbcal) select p.prog_year||p.prog_semester as semester, " +
					"m.mod_name as modo, m.ins_id as ins, (select count(*) from productos b, programa bp where b.mod_id=m.mod_id and b.prog_id=bp.prog_id and bp.prog_year=p.prog_year and " +
					"bp.prog_semester=p.prog_semester and b.ins_id=m.ins_id and b.mty_id='SCI') as cuentaRaw  ,(select sum(prod_filesize)/1024/1024/1024 from productos b, programa bp where b.mod_id=m.mod_id and " +
					"b.prog_id=bp.prog_id and bp.prog_year=p.prog_year and bp.prog_semester=p.prog_semester and b.ins_id=m.ins_id and b.mty_id='SCI') as gbRaw  ,(select count(*) from productos b, programa bp, " +
					"pred_prod l, prodreducido r where b.mod_id=m.mod_id and b.prog_id=bp.prog_id and bp.prog_year=p.prog_year and bp.prog_semester=p.prog_semester and l.prog_id=b.prog_id and " +
					"l.obl_id=b.obl_id and l.prod_id=b.prod_id and r.pred_id=l.pred_id and b.ins_id=m.ins_id and b.mty_id='SCI') as cuentaRed,(select sum(pred_filesize)/1024/1024/1024 from productos b, programa bp, pred_prod l, " +
					"prodreducido r where b.mod_id=m.mod_id and b.prog_id=bp.prog_id and bp.prog_year=p.prog_year and bp.prog_semester=p.prog_semester and l.prog_id=b.prog_id and l.obl_id=b.obl_id " +
					"and l.prod_id=b.prod_id and r.pred_id=l.pred_id and b.ins_id=m.ins_id and b.mty_id='SCI') as gbRed,(select count(*) from productos b, programa bp where  b.mod_id=m.mod_id and b.prog_id=bp.prog_id and " +
					"bp.prog_year=p.prog_year and bp.prog_semester=p.prog_semester and b.ins_id=m.ins_id and b.mty_id!='SCI') as cuentaCal,(select sum(prod_filesize)/1024/1024/1024 from productos b, programa bp " +
					"where b.mod_id=m.mod_id and b.prog_id=bp.prog_id and bp.prog_year=p.prog_year and bp.prog_semester=p.prog_semester and b.ins_id=m.ins_id and b.mty_id!='SCI') as gbCal FROM programa p, modo m " +
					"GROUP BY p.prog_year, p.prog_semester, m.mod_name, m.mod_id, m.ins_id ORDER BY semester;";
			
			
			conex.createStatement().executeUpdate(query);

			conex.close();
		} catch (SQLException errconexion)  {
			errconexion.printStackTrace();
			throw new Exception("No se han podido insertar los datos en la base de datos");
		}

	}
	

}
