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

package svo.gtc.db.logprod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class LogProdAccess {

	Connection con = null;
	//SELECT
	private PreparedStatement pstIns;
	private PreparedStatement pstInsSingle;
	private PreparedStatement pstIsSCI;
	
	private String ins=
		" INSERT INTO logprod (logp_time, logp_host, logp_type, logp_elapsed, logp_size) " +
		" 			VALUES(?,?,?,?,?) ";

	private String insSingle=
		" INSERT INTO logprodsingle (logp_time, lprog_id, lobl_id, lprod_id, logp_priv, logp_sci) " +
		" 			VALUES(?,?,?,?,?,?) ";
	
	private String isSCI= "select count(*) from productos where prog_id=? and obl_id=? and prod_id=? and (mty_id='SCI' or mty_id='SCI_R')";

	public LogProdAccess(Connection conex) throws SQLException{
		con = conex;
		//Ordenes Precompiladas --------------------------------------------------
		pstIns 			= conex.prepareStatement(ins);
		pstInsSingle	= conex.prepareStatement(insSingle);
		pstIsSCI		= conex.prepareStatement(isSCI);
	}
	
	public void insert(LogProdDb logProd) throws SQLException{
		pstIns.setTimestamp(1, logProd.getLogpTime());
		if(logProd.getLogpHost()!=null){
			pstIns.setString(2, logProd.getLogpHost());
		}else{
			pstIns.setNull(2, Types.CHAR);
		}
		pstIns.setString(3, logProd.getLogpType());
		pstIns.setDouble(4, logProd.getLogpElapsed().doubleValue());
		pstIns.setLong(5, logProd.getLogpSize().longValue());
		
		//System.out.println("pst: "+pstIns.toString());
		pstIns.execute();
		
		LogProdSingleDb[] prods = logProd.getLogpSingle();
		
		for(int i=0; i<prods.length; i++){
			
			//Obtenemos si es SCI
			Integer sci = null;
			pstIsSCI.setString(1, prods[i].getLprogId());
			pstIsSCI.setString(2, prods[i].getLoblId());
			pstIsSCI.setInt(3, prods[i].getLprodId().intValue());
			
			ResultSet resset = pstIsSCI.executeQuery();
			while(resset.next()){
				sci = new Integer(resset.getInt(1));
			}
			
			//Itroducimos la información del producto
			pstInsSingle.setTimestamp(1, logProd.getLogpTime());
			pstInsSingle.setString(2, prods[i].getLprogId());
			pstInsSingle.setString(3, prods[i].getLoblId());
			pstInsSingle.setInt(4, prods[i].getLprodId().intValue());
			pstInsSingle.setInt(5,prods[i].getLogpPriv());
			pstInsSingle.setInt(6,sci);
			
			
			//System.out.println("logsSingle: "+pstInsSingle.toString());
			
			pstInsSingle.execute();
			
		}
		
		
	}
	
	public void insertPred(LogProdDb logProd, Integer red_id) throws SQLException{
		pstIns.setTimestamp(1, logProd.getLogpTime());
		if(logProd.getLogpHost()!=null){
			pstIns.setString(2, logProd.getLogpHost());
		}else{
			pstIns.setNull(2, Types.CHAR);
		}
		pstIns.setString(3, logProd.getLogpType());
		pstIns.setDouble(4, logProd.getLogpElapsed().doubleValue());
		pstIns.setLong(5, logProd.getLogpSize().longValue());
		
		//System.out.println("pst: "+pstIns.toString());
		pstIns.execute();
		
			pstInsSingle.setTimestamp(1, logProd.getLogpTime());
			pstInsSingle.setString(2, "pred");
			pstInsSingle.setString(3, "pred");
			pstInsSingle.setInt(4, red_id);
			pstInsSingle.setInt(5,0);
			pstInsSingle.setInt(6,1);
			pstInsSingle.execute();
		
		
	}
	
	
	
	
}
