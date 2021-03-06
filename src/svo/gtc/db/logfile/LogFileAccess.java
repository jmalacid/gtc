/*
 * @(#)ProdDatosAccess.java    Feb 17, 2011
 *
 *
 * Ra�l Guti�rrez S�nchez. (raul@cab.inta-csic.es)	
 * LAEFF: 	Laboratorio de Astrof�sica Espacial y F�sica Fundamental.
 *        	INTA
 *			Villafranca del Castillo Satellite Tracking Station
 *			Villafranca del Castillo, E-28691 Villanueva de la Ca�ada
 *			Madrid - Espa�a
 *			
 * Phone:  34 91-8131260
 * Fax..:  34 91-8131160   
 * -----------------------------------------------------------------------
 * 
 *	ESTE  PROGRAMA  ES  DE  USO EXCLUSIVO.  ES  PROPIEDAD  DE  SU AUTOR  Ra�l 
 *  Guti�rrez S�nchez  Y SE PRESENTA "COMO ESTA" SIN NINGUN TIPO DE GARANTIAS
 *  DENTRO Y FUERA  DE LA FINALIDAD PARA  LA QUE EL  AUTOR LO  HIZO. EL AUTOR 
 *  PERMITE  SU USO SIEMPRE QUE  SE LE REFERENCIE. SE PERMITE LA MODIFICACION
 *  DE SU  FUENTE SIEMPRE QUE SE  HAGA REFERENCIA A SU AUTORIA Y JUNTO A ELLA
 *  SE  ADJUNTE LA IDENTIFICACION DEL  AUTOR DE LAS NUEVAS MODIFICACIONES. EL
 *  AUTOR  NUNCA SERA RESPONSABLE DEL USO DE ESTE  PROGRAMA NI DE LOS EFECTOS
 *  QUE ESTE PROGRAMA PUDIEDA CAUSAR EN DETERIOROS, ALTERACIONES Y/O PERDIDAS 
 *  DE INFORMACION.
 *	
 */

package svo.gtc.db.logfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogFileAccess {

	Connection con = null;
	//SELECT
	private PreparedStatement pstSelById;
	private PreparedStatement pstCountById;
	private PreparedStatement pstIns;
	
	private static String select=
		" SELECT log.prog_id, log.obl_id, log.log_filename, log.log_path, b.bpath_id, b.bpath_path , log.log_type ";

	private String selCount=
		" SELECT COUNT(*) ";

	private String ins=
		" INSERT INTO logfile (prog_id, obl_id, log_filename, log_path, bpath_id, log_type) " +
		" 			VALUES(?,?,?,?,?,?) ";

	public LogFileAccess(Connection conex) throws SQLException{
		con = conex;
		//Ordenes Precompiladas --------------------------------------------------
		pstSelById = conex.prepareStatement(select+" FROM logfile log, basepath b WHERE log.bpath_id=b.bpath_id AND prog_id= ? AND obl_id=? AND log_filename=?;");
		pstCountById = conex.prepareStatement(selCount+" FROM logfile log WHERE prog_id= ? AND obl_id=? AND log_filename=?;");
		pstIns 		= conex.prepareStatement(ins);
	}
	
	public LogFileDb selectById(String prog_id, String obl_id, String log_filename) throws SQLException{
		pstSelById.setString(1, prog_id);
		pstSelById.setString(2, obl_id);
		pstSelById.setString(3, log_filename);
		ResultSet resset = pstSelById.executeQuery();

		LogFileDb logFile=null;
		if(resset.next()){
			logFile = new LogFileDb(resset);
		}

		return logFile;
	}
	
	public int countById(String prog_id, String obl_id, String log_filename) throws SQLException{
		pstCountById.setString(1, prog_id);
		pstCountById.setString(2, obl_id);
		pstCountById.setString(3, log_filename);
		ResultSet resset = pstCountById.executeQuery();

		int count=0;
		if(resset.next()){
			count = resset.getInt(1);
		}

		return count;
		
	}
	

	public void insert(LogFileDb logFile) throws SQLException{
		pstIns.setString(1, logFile.getProgId());
		pstIns.setString(2, logFile.getOblId());
		pstIns.setString(3, logFile.getLogFilename());
		pstIns.setString(4, logFile.getLogPath());
		pstIns.setInt(5, logFile.getBpathId().intValue());
		pstIns.setString(6, logFile.getLogtype());
		
		pstIns.execute();
	}
	
	public static String getInicioSelect(){
		return select;
	}
		
	
}
