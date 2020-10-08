/*
 * @(#)ProdErrorAccess.java    Feb 10, 2011
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

package svo.gtc.db.proderr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import svo.gtc.db.basepath.BasepathAccess;

public class ProdErrorAccess {

	private Connection con = null;
	//SELECT
	private PreparedStatement pstSelProdErrorById;
	private PreparedStatement pstSelProdErrorByProdeId;
	private PreparedStatement pstCountProdErrorById;
	private PreparedStatement pstSelErrorsById;

	private PreparedStatement pstInsProdError;
	private PreparedStatement pstInsError;
	
	private PreparedStatement pstDelProdError;
	private PreparedStatement pstDelError;
	
	
	private static String selProdError=
		" SELECT prode_id, prog_id, obl_id, bpath_path, prode_filename, prode_path, prode_enterdate " +
		" 	FROM proderror a, basepath b WHERE a.bpath_id=b.bpath_id ";

	private String selCountProdDatos=
		" SELECT COUNT(*) " +
		" 	FROM proderror ";

	private String selErrors=
		" SELECT a.err_id, err_desc " +
		" 	FROM errors a , prode_error b WHERE a.err_id=b.err_id ";

	
	private String insProdError=
		" INSERT into proderror (prode_id, prog_id, obl_id, bpath_id, prode_filename, prode_path, prode_enterdate) VALUES (?,?,?,?,?,?,current_timestamp) ";

	private String insError=
		" INSERT into prode_error (prog_id, obl_id, prode_id, err_id, prode_rev) VALUES (?,?,?,?,0) ";

	private String delProdError=
		" DELETE from proderror WHERE prog_id=? and obl_id=? and prode_id=?; ";

	private String delError=
		" DELETE from prode_error WHERE prog_id=? and obl_id=? and prode_id=?; ";


	public ProdErrorAccess(Connection conex) throws SQLException{
		con = conex;
		//Ordenes Precompiladas --------------------------------------------------
		pstSelProdErrorById = conex.prepareStatement(selProdError+" AND prog_id=? AND obl_id=? AND prode_id= ?;");
		pstCountProdErrorById = conex.prepareStatement(selCountProdDatos+" WHERE prog_id= ? AND obl_id=? AND prode_id=?;");
		pstSelProdErrorByProdeId = conex.prepareStatement(selProdError+" AND prode_id= ?;");

		pstSelErrorsById = conex.prepareStatement(selErrors+" AND prog_id=? AND obl_id=? AND prode_id= ?;");

		pstInsProdError = conex.prepareStatement(insProdError);
		pstInsError = conex.prepareStatement(insError);

		pstDelProdError = conex.prepareStatement(delProdError);
		pstDelError = conex.prepareStatement(delError);

	}

	/**
	 * Devuelve el producto erroneo con un determinado identificador.
	 * @param prog_id
	 * @param obl_id
	 * @param prode_id
	 * @return
	 * @throws SQLException
	 */
	public ProdErrorDb selectProdErrorById(String prog_id, String obl_id, Integer prode_id) throws SQLException{
		pstSelProdErrorById.setString(1, prog_id);
		pstSelProdErrorById.setString(2, obl_id);
		pstSelProdErrorById.setInt(3, prode_id.intValue());
		ResultSet resset = pstSelProdErrorById.executeQuery();

		ProdErrorDb prod=null;
		if(resset.next()){
			prod = new ProdErrorDb(resset);
			ErrorDb[] errors = selectErrorsById(prog_id, obl_id, prode_id);
			prod.setErrors(errors);
		}
		
		return prod;
	}

	/**
	 * Devuelve los productos erroneos con un determinado prode_id.
	 * @param prode_id
	 * @return
	 * @throws SQLException
	 */
		public ProdErrorDb[] selectProdErrorByProdeId(Integer prode_id) throws SQLException{
			pstSelProdErrorByProdeId.setInt(1, prode_id.intValue());
			ResultSet resset = pstSelProdErrorByProdeId.executeQuery();

			Vector<ProdErrorDb> vcr = new Vector<ProdErrorDb>();
			
			ProdErrorDb prod=null;
			while(resset.next()){
				prod = new ProdErrorDb(resset);
				ErrorDb[] errors = selectErrorsById(prod.getProgId(), prod.getOblId(), prod.getProdeId());
				prod.setErrors(errors);
				vcr.add(prod);
			}
			
			return (ProdErrorDb[])vcr.toArray(new ProdErrorDb[0]);
		}

	/**
	 * Cuenta el numero de productos erroneos con un determinado identificador.
	 * @param prod_id
	 * @return
	 * @throws SQLException
	 */
	public int countProdErrorById(String prog_id, String obl_id, Integer prode_id) throws SQLException{
		pstCountProdErrorById.setString(1, prog_id);
		pstCountProdErrorById.setString(2, obl_id);
		pstCountProdErrorById.setInt(3, prode_id.intValue());
		ResultSet resset = pstCountProdErrorById.executeQuery();

		int count=0;
		if(resset.next()){
			count = resset.getInt(1);
		}
		return count;
	}
	

	/**
	 * Devuelve todos los errores asociados a un ProdError.
	 * @param prode_id
	 * @return
	 * @throws SQLException
	 */
	public ErrorDb[] selectErrorsById(String prog_id, String obl_id, Integer prode_id) throws SQLException{
		pstSelErrorsById.setString(1, prog_id);
		pstSelErrorsById.setString(2, obl_id);
		pstSelErrorsById.setInt(3, prode_id.intValue());
		ResultSet resset = pstSelErrorsById.executeQuery();
		Vector<ErrorDb> vcr = new Vector<ErrorDb>();
		
		ErrorDb error=null;
		while(resset.next()){
			error = new ErrorDb(resset);
			vcr.add(error);
		}
		
		
		return (ErrorDb[])vcr.toArray(new ErrorDb[0]);
	}


	/**
	 * Inserta un producto en la tabla de errores y le asocia los errores correspondientes.
	 * @param prod
	 * @throws SQLException 
	 * @throws SQLException 
	 * @throws SQLException
	 */
	public void insProdError(ProdErrorDb prod) throws SQLException{
		BasepathAccess basepathAccess = new BasepathAccess(con);
		
		//Integer bpath_id = basepathAccess.selectBpathId(prod.getBpathPath());
		Integer bpath_id=1;
		
		pstInsProdError.setInt(1, prod.getProdeId().intValue());		
		pstInsProdError.setString(2, prod.getProgId().toUpperCase().replaceAll("\\s", ""));		
		pstInsProdError.setString(3, prod.getOblId().toUpperCase().replaceAll("\\s", ""));		
		pstInsProdError.setInt(4, bpath_id);		
		pstInsProdError.setString(5, prod.getProdeFilename());		
		pstInsProdError.setString(6, prod.getProdePath());
		pstInsProdError.execute();
			
		//System.out.println("Inserto "+prod.getProg_id().toUpperCase().replaceAll("\\s", ""));
		//System.out.println("Inserto "+prod.getObl_id().toUpperCase().replaceAll("\\s", ""));
		//System.out.println("Inserto "+prod.getProde_id().intValue());
		
		insErrors(prod.getProgId(), prod.getOblId(), prod.getProdeId(), prod.getErrors());
	}
	
	/**
	 * Asocia los errores corespondientes a un producto erroneo. Los errores
	 * existentes se respetar�n.
	 * @param prode_id
	 * @param errors
	 * @throws SQLException 
	 */
	public void insErrors(String prog_id, String obl_id, Integer prode_id, String[] errorIds) throws SQLException{
		ErrorDb[] antiguos = selectErrorsById(prog_id, obl_id, prode_id);
		
		for(int i=0; i<errorIds.length; i++){
			boolean existe = false;
			for(int j=0; j<antiguos.length; j++){
				if(antiguos[j].getErr_id().equals(errorIds[i])){
					existe=true;
					break;
				}
			}
			if(existe) continue;
			
			pstInsError.setString(1, prog_id);
			pstInsError.setString(2, obl_id);
			pstInsError.setInt(3, prode_id.intValue());
			pstInsError.setString(4, errorIds[i]);
			pstInsError.execute();
		}
		
	}
	
	public void insErrors(String prog_id, String obl_id, Integer prode_id, ErrorDb[] errors) throws SQLException{
		String[] errorIds = new String[errors.length];
		for(int i=0; i<errorIds.length; i++){
			errorIds[i]=errors[i].getErr_id();
		}
		insErrors(prog_id, obl_id, prode_id, errorIds);
	}


	/**
	 * Borra todos los productos erroneos y sus errores asociados.
	 * @param prod_id
	 * @throws SQLException 
	 */
	public void delProdError(String prog_id, String obl_id, Integer prode_id) throws SQLException{
		int count = this.selectErrorsById(prog_id, obl_id, prode_id).length;
		if(count>0){
			delErrores(prog_id, obl_id, prode_id);
		}

		count = this.countProdErrorById(prog_id, obl_id, prode_id);
		if(count>0){
			pstDelProdError.setString(1, prog_id);
			pstDelProdError.setString(2, obl_id);
			pstDelProdError.setInt(3, prode_id.intValue());
			pstDelProdError.execute();
		}
	}

	
	/**
	 * Borra todos los errores asociados a un prod_id.
	 * @param prod_id
	 * @throws SQLException 
	 */
	public void delErrores(String prog_id, String obl_id, Integer prode_id) throws SQLException{
		pstDelError.setString(1, prog_id);
		pstDelError.setString(2, obl_id);
		pstDelError.setInt(3, prode_id);
		pstDelError.execute();
	}
	
	
	/**
	 * 
	 * 
	 * @return
	 */
	public static String getInicioSelect(){
		return selProdError;
	}

}
