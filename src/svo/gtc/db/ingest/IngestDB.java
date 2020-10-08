package svo.gtc.db.ingest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import svo.gtc.db.prodred.ProdRedDb;
import svo.gtc.priv.objetos.Prod_red;

public class IngestDB {


	Connection con = null;
	//SELECT
	//private PreparedStatement pstSelNewProdId;
	private PreparedStatement pstSelNewPredId;
	private PreparedStatement pstCountByMd5;
	private PreparedStatement pstIns;
	private PreparedStatement pstProd;

	private String selectMd5 = "select count(*) from prodreducido where pred_md5sum=?";
	private static String selectProd=" SELECT prog_id, obl_id, prod_id, subm_id, prod_ra, prod_de from proddatos where prod_filename=? and prog_id=? and obl_id=? ";
	private static String selectNewPredId= "SELECT greatest(0,(select max(pred_id)+1 from prodreducido))";
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
	
	public IngestDB(Connection conex) throws SQLException{
		con = conex;
			
		//Ordenes Precompiladas --------------------------------------------------
		pstProd = con.prepareStatement(selectProd);
		pstCountByMd5 = con.prepareStatement(selectMd5);
		pstSelNewPredId = con.prepareStatement(selectNewPredId);
		pstIns = con.prepareStatement(insert);
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
			
			//System.out.println(pstIns.toString()+";");
			pstIns.execute();
			
		}
		
		public int getNewPredId() throws SQLException{
			
			/// Buscamos el nuevo ID de producto reducido
			ResultSet res = pstSelNewPredId.executeQuery();
			int predId = 0;
			if(res.next()) predId=res.getInt(1);
			
			return predId;
		}
		
		public Prod_red getProd(String predFile, String prog, String obl) throws SQLException{
			
			/// Buscamos el nuevo ID de producto reducido
			pstProd.setString(1, predFile);
			pstProd.setString(2, prog);
			pstProd.setString(3, obl);
			
			System.out.println(pstProd);
			
			ResultSet res = pstProd.executeQuery();
			
			
			Prod_red Result = null;
			
			while (res.next()) {
				
				Result = new Prod_red(res);
			}
			
			res.close();
			return Result;
		}
		



}
