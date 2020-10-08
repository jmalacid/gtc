/*
 * @(#)ProdDatosAccess.java    Aug 6, 2009
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

package svo.gtc.db.prodred;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import svo.gtc.db.DriverBD;
import svo.gtc.priv.objetos.Prod_red;

public class ProdRedAccess {

	Connection con = null;
	//SELECT
	//private PreparedStatement pstSelNewProdId;
	private PreparedStatement pstSelById;
	private PreparedStatement pstSelNewPredId;
	private PreparedStatement pstCountByMd5;
	private PreparedStatement pstCountByName;
	private PreparedStatement pstIns;
	private PreparedStatement pstDel;
	
	private static String select=
		" SELECT p.pred_id, p.usr_id, p.col_id, b.bpath_id, b.bpath_path, p.pred_filename, p.pred_path, p.pred_filenameorig, " +
		"		 p.pred_filesize, p.pred_md5sum, p.pred_ra, p.pred_de, p.pred_type ";
	
	private static String selectNewPredId= "SELECT greatest(0,(select max(pred_id)+1 from prodreducido))";
	
	private static String joins=
		" FROM prodreducido p, basepath b " +
		" WHERE p.bpath_id=b.bpath_id ";

	private String selCount=
		" SELECT COUNT(*) " +
		" 	FROM prodreducido a ";

	
	private String insert=
		" INSERT INTO prodreducido (pred_id, usr_id, col_id, bpath_id, pred_filename, pred_path, pred_filenameorig, " +
		"		 pred_filesize, pred_md5sum, pred_ra, pred_de, pred_type) " +
		" 			VALUES(	?," +
		"				   	?," +
		"					?," +
		"					?," +
		"					?," +
		"					?," +
		"					?," +
		"					?," +
		"					?," +
		"					?," +
		"					?," +
		"					?) ";

	private String delete=
		" DELETE FROM prodreducido WHERE pred_id=? ";
	private static String selectProd=
			" SELECT prog_id, obl_id, prod_id from proddatos where prod_filename=? ";
		

	public ProdRedAccess(Connection conex) throws SQLException{
		con = conex;
		//Ordenes Precompiladas --------------------------------------------------
		//pstSelNewProdId = conex.prepareStatement(selectNewProdId);
		pstSelById = con.prepareStatement(select+joins+
				"AND   pred_id= ? ;");
		pstSelNewPredId = con.prepareStatement(selectNewPredId);
		pstCountByMd5 = con.prepareStatement(selCount+" WHERE pred_md5sum=?;");
		pstCountByName = con.prepareStatement(selCount+" WHERE pred_filename like ?;");
		pstIns 		= con.prepareStatement(insert);
		pstDel 		= con.prepareStatement(delete);
	}
	
	
	/**
	 * Devuelve el inicio de la select de productos reducidos de datos, con los campos por defecto.
	 * @return
	 */
	public static String getInicioSelect(){
		return select;
	}
	
/**
 * Devuelve el producto de datos a partir del identificador.
 * @param prog_id
 * @param obl_id
 * @param prod_id
 * @return
 * @throws SQLException
 */
	public ProdRedDb selectById(Integer pred_id) throws SQLException{

		pstSelById.setInt(1, pred_id.intValue());
		ResultSet resset = pstSelById.executeQuery();

		ProdRedDb prod=null;
		if(resset.next()){
			prod = new ProdRedDb(resset);
		}
		
		resset.close();

		return prod;
	}
	
	public int countByMd5(String pred_md5sum) throws SQLException{
		pstCountByMd5.setString(1, pred_md5sum);
		ResultSet resset = pstCountByMd5.executeQuery();

		int count=0;
		if(resset.next()){
			count = resset.getInt(1);
		}

		return count;
		
	}
	
	public boolean existsName(String pred_filename) throws SQLException{
		pstCountByName.setString(1, pred_filename);
		ResultSet resset = pstCountByName.executeQuery();

		int count=0;
		if(resset.next()){
			count = resset.getInt(1);
		}

		if(count>0) return true;
		else return false;
		
	}

		/**
		 * Elimina un producto de datos.
		 * @param prog_id
		 * @param obl_id
		 * @param prod_id
		 * @throws SQLException
		 */

		public void delete(Integer pred_id) throws SQLException{
			pstDel.setInt(1, pred_id);
			pstDel.executeUpdate();
		}

		/**
		 * Inserta un producto de datos y sus correspondientes warnings, si los hubiera.
		 * @param prod
		 * @return
		 * @throws SQLException
		 */
		public void insProdDatos(ProdRedDb pred) throws SQLException{
			
			// Insertamos el producto reducido
			pstIns.setInt(1, pred.getPredId());
			pstIns.setString(2, pred.getUsrId());
			pstIns.setInt(3, pred.getColId());
			pstIns.setInt(4, pred.getBpathId());
			pstIns.setString(5, pred.getPredFilename());
			pstIns.setString(6, pred.getPredPath());
			pstIns.setString(7, pred.getPredFilenameOrig());
			pstIns.setLong(8, pred.getPredFilesize());
			pstIns.setString(9, pred.getPredMd5sum());
			pstIns.setDouble(10, pred.getPredRa());
			pstIns.setDouble(11, pred.getPredDe());
			pstIns.setString(12, pred.getPredType());
			
			System.out.println(pstIns.toString()+";");
			pstIns.execute();
			
		}
		
		public int getNewPredId(String usrId, Integer colId) throws SQLException{
			
			/// Buscamos el nuevo ID de producto reducido
			ResultSet res = pstSelNewPredId.executeQuery();
			int predId = 0;
			if(res.next()) predId=res.getInt(1);
			
			return predId;
		}
				
		public static void main(String[] args) throws SQLException{
			//======================================================= 
			//Conexion con la Base de Datos                       
			//=======================================================
			DriverBD drvBd = new DriverBD();
			Connection conex = null;
			try {
				conex = drvBd.bdConexion();
			} catch (SQLException errconexion) {
				errconexion.printStackTrace();
			}
			
			
			
			ProdRedAccess acc = new ProdRedAccess(conex);

			System.out.println("Existe: "+acc.existsName("0000006571-20090624-OSIRIS-OsirisBroadBandImage_RED%"));

			conex.close();
			
		}

}
