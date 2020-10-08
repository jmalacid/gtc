/*
 * @(#)FetchProd.java    Apr 4, 2011
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

package svo.gtc.web.servlet;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import svo.gtc.db.DriverBD;
import svo.gtc.db.prodat.ProdDatosDb;
import svo.gtc.db.prodat.WarningDb;
import svo.gtc.db.web.WebSearcher;


/**
 * Servlet implementation class Entrada
 */
public class WarningLog extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WarningLog() {
        super();       
    }
 
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		

		//======================================================= 
		//  Conexion con la Base de Datos                       
		//=======================================================

		Connection conex= null;

		DriverBD drvBd = new DriverBD();

		try {
			conex = drvBd.bdConexion();
		} 
		catch (SQLException errconexion) {
			errconexion.printStackTrace();
		}



		// *************************************************************
		// *  TECLAS DEL FORMULARIO                                    *
		// *************************************************************

		String par_progId = request.getParameter("progId");
		String par_oblId  = request.getParameter("oblId");
		
		String progId = "";
		String oblId="";
		
		if(par_progId!=null && par_oblId!=null){
			progId = par_progId.trim();
			oblId=par_oblId.trim().substring(0,4);
		}

		ServletOutputStream outs = response.getOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outs));
		response.setContentType ("text/plain");
		response.setHeader("Content-Disposition", "attachment;filename=warnings_"+progId+"_"+oblId+".txt");

		bw.write("-------------------------------\n");
		bw.write(" WARNINGS for Observing Block: \n");
		bw.write(" Prog ID: "+progId+"\n");
		bw.write(" Obl ID:  "+oblId+"\n");
		bw.write("-------------------------------\n");
		

		ProdDatosDb[] prodDb=new ProdDatosDb[0];

		if(progId.length()>0 && oblId.length()>0){
			try {
				WebSearcher webSearch = new WebSearcher(conex);
				prodDb = webSearch.selectProductsWithWarningsByOblId(progId, oblId);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		if(prodDb.length==0){
			bw.write("There are no products with warning messages in this OB.\n");
		}

		for(int i=0; i<prodDb.length; i++){
			bw.write("\n\n"+prodDb[i].getProdFilename()+" : \n");
			bw.write("-------------------------------------------------------------------------------\n");
			WarningDb[] warnings = prodDb[i].getWarnings();
			for(int j=0; j<warnings.length; j++){
				bw.write(warnings[j].getErr_id()+"\t : \t"+warnings[j].getErr_desc()+"\n");
			}
		}

		bw.flush();
		
		// Cierre de la conexión a la base de datos
	    //System.out.println("res_busqueda.jsp: Conexion con la BD cerrada.");
		try {
			conex.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		

	}	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
	}

}