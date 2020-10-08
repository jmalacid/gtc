/*
 * @(#)PredProdAccess.java    Jun 21, 2012
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
import java.util.Vector;

import svo.gtc.db.prodat.ProdDatosDb;

public class PredProdAccess {

	Connection con = null;
	//SELECT
	//private PreparedStatement pstSelNewProdId;
	private PreparedStatement pstSelProdRedByProdDatos;
	private PreparedStatement pstSelProdDatosByProdRed;
	private PreparedStatement pstIns;
	private PreparedStatement pstProd;
	
	private static String select=
		" SELECT pred_id, prog_id, obl_id, prod_id ";
	
	private static String joins=
		" FROM pred_prod " +
		" WHERE 1=1 ";

	private String insert=
		" INSERT INTO pred_prod (pred_id, prog_id, obl_id, prod_id) " +
		" 			VALUES(	?," +
		"					?," +
		"					?," +
		"					?) ";

	private String selectProd = "select prod_id, prog_id, obl_id from proddatos where prod_filename=?";
	
	public PredProdAccess(Connection conex) throws SQLException{
		con = conex;
		//Ordenes Precompiladas --------------------------------------------------
		//pstSelNewProdId = conex.prepareStatement(selectNewProdId);
		pstSelProdRedByProdDatos = con.prepareStatement(select+joins+
				"AND   prog_id= ? " +
				"AND   obl_id= ? " +
				"AND   prod_id= ? ;");

		pstSelProdDatosByProdRed = con.prepareStatement(select+joins+
				"AND   pred_id= ? ;");
		pstIns 		= con.prepareStatement(insert);
		pstProd = con.prepareStatement(selectProd);
	}
	
	
	/**
	 * Devuelve el inicio de la select de productos reducidos de datos, con los campos por defecto.
	 * @return
	 */
	public static String getInicioSelect(){
		return select;
	}
	
/**
 * Devuelve los productos de datos asociados a un producto reducido.
 * @throws SQLException
 */
	public ProdDatosDb[] selectProdDatosByProdRed(ProdRedDb prod) throws SQLException{
		return selectProdDatosByProdRedId(prod.getPredId());

	}
	
	public ProdDatosDb[] selectProdDatosByProdRedId(Integer pred_id) throws SQLException{
		pstSelProdDatosByProdRed.setInt(1, pred_id);
		ResultSet resset = pstSelProdDatosByProdRed.executeQuery();

		PredProdDb predprod=null;
		Vector<ProdDatosDb> aux = new Vector<ProdDatosDb>();
		
		svo.gtc.db.prodat.ProdDatosAccess prodDatosAccess = new svo.gtc.db.prodat.ProdDatosAccess(con);
		
		while(resset.next()){
			predprod = new PredProdDb(resset);
			ProdDatosDb producto = prodDatosAccess.selectById(predprod.getProgId(), predprod.getOblId(), predprod.getProdId());
			aux.add(producto);
		}
		
		resset.close();

		return (ProdDatosDb[])aux.toArray(new ProdDatosDb[0]);
	}
	/**
	 * Devuelve los productos reducidos asociados a un producto de datos.
	 * @throws SQLException
	 */
		public ProdRedDb[] selectProdRedByProdDatos(ProdDatosDb prod) throws SQLException{

			pstSelProdRedByProdDatos.setString(1, prod.getProgId());
			pstSelProdRedByProdDatos.setString(2, prod.getOblId());
			pstSelProdRedByProdDatos.setInt(3, prod.getProdId());
			ResultSet resset = pstSelProdRedByProdDatos.executeQuery();

			PredProdDb predprod=null;
			Vector<ProdRedDb> aux = new Vector<ProdRedDb>();
			
			svo.gtc.db.prodred.ProdRedAccess prodRedAccess = new svo.gtc.db.prodred.ProdRedAccess(con);
			
			while(resset.next()){
				predprod = new PredProdDb(resset);
				ProdRedDb producto = prodRedAccess.selectById(predprod.getPredId());
				aux.add(producto);
			}
			
			resset.close();

			return (ProdRedDb[])aux.toArray(new ProdRedDb[0]);
		}

		public PredProdDb selectProd(String filename) throws SQLException{
			pstProd.setString(1, filename);
			ResultSet resset = pstProd.executeQuery();
			
			PredProdDb pred = new PredProdDb();
			
			while(resset.next()){
				pred.setOblId(resset.getString("obl_id"));
				pred.setProdId(resset.getInt("prod_id"));
				pred.setProgId(resset.getString("prog_id"));
			}
			return pred;
		}
		
		/**
		 * Inserta una relación entre producto reducido y producto de datos.
		 * @param prod
		 * @return
		 * @throws SQLException
		 */
		public void insPredProd(PredProdDb predprod) throws SQLException{
			con.setAutoCommit(false);
			
			
			pstIns.setInt(1, predprod.getPredId());
			pstIns.setString(2, predprod.getProgId());
			pstIns.setString(3, predprod.getOblId());
			pstIns.setInt(4, predprod.getProdId());
			//System.out.println(pstIns.toString()+";");
			
			pstIns.execute();
						
		}

}
