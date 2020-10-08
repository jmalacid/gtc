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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import svo.gtc.db.DriverBD;
import svo.gtc.db.logfile.LogFileAccess;
import svo.gtc.db.logfile.LogFileDb;
import svo.gtc.db.logprod.LogProdAccess;
import svo.gtc.db.logprod.LogProdDb;
import svo.gtc.db.logprod.LogProdSingleDb;
import svo.gtc.db.permisos.ControlAcceso;
import svo.gtc.db.prodat.ProdDatosAccess;
import svo.gtc.db.prodat.ProdDatosDb;
import svo.gtc.db.proderr.ProdErrorAccess;
import svo.gtc.db.proderr.ProdErrorDb;
import svo.gtc.db.prodred.ProdRedAccess;
import svo.gtc.db.prodred.ProdRedDb;
import svo.gtc.proddat.ProdDatos;
import svo.gtc.web.ContenedorSesion;
import svo.gtc.web.Html;


/**
 * Servlet implementation class Entrada
 */
public class FetchProd extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FetchProd() { 
        super();       
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//*************************************************************
		//*  SESION                                                   *
		//*************************************************************

		ContenedorSesion contenedorSesion = null;
		HttpSession session = request.getSession();
		if(session!=null && session.getAttribute("contenedorSesion")!=null){
			contenedorSesion = (ContenedorSesion)session.getAttribute("contenedorSesion");
		}else{
			contenedorSesion = new ContenedorSesion();
		}



		String MSG = "";

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

		//---- Elementos estáticos de la página
		Html elementosHtml = new Html(request.getContextPath());
		String cabeceraPagina = elementosHtml.cabeceraPagina("Product Details","Data Product Details.");
		String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath()+"?"+request.getQueryString());
		String pie            = elementosHtml.pie();

		String par_prodId = request.getParameter("prod_id");
		String par_predId = request.getParameter("pred_id");
		String par_fetchType = request.getParameter("fetch_type");
		
		String par_progId = request.getParameter("prog_id");
		String par_oblId = request.getParameter("obl_id");
		String par_logFilename = request.getParameter("log_filename");
		
		//Obtenemos el bibcode si lo manda
		String bibcode = null;
		try{
			bibcode = request.getParameter("bib");
		}catch(Exception e){
			//No hay bibcode
		}
		
		String[] aux = new String[3];
		if(par_prodId!=null){
			aux = par_prodId.split("\\.\\.");
		}

		String prog_id = "";
		String obl_id="";
		Integer prod_id = new Integer(-1);
		
		try{
			prog_id = aux[0];
			obl_id=aux[1];
			if(aux[2]!=null){
				prod_id= new Integer(aux[2]);
			}
		}catch(Exception e){}


		Integer pred_id = new Integer(-1);
		if(par_predId!=null){
			try{
				pred_id = new Integer(par_predId);
			}catch(NumberFormatException e){}
		}

//Comprobamos si tiene permisos
		ControlAcceso controlAcceso;
		try {
			controlAcceso = new ControlAcceso(conex, contenedorSesion.getUser());

		
		if(prod_id!=-1){
			if(!controlAcceso.hasPerm(prog_id,obl_id,prod_id,contenedorSesion.getUser())){
				
				String pagOrigen = request.getServletPath()+"?"+request.getQueryString();
				request.setAttribute("pagOrigen",pagOrigen);
				response.sendRedirect(request.getContextPath()+"/jsp/accessDenied.jsp");
			/*
				<jsp:forward page="accessDenied.jsp"/>*/

			}
		
		}
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		ServletOutputStream outs = response.getOutputStream();

		if(prod_id.intValue()>=0 || pred_id.intValue()>=0){
			ProdDatosAccess datosAccess;
			ProdRedAccess datosRedAccess;
			ProdErrorAccess errorAccess;
			ProdDatosDb prodDb=null;
			ProdRedDb prodRedDb=null;
			ProdErrorDb prodErrDb=null;
			try {
				datosAccess = new ProdDatosAccess(conex);
				datosRedAccess = new ProdRedAccess(conex);
				prodDb=datosAccess.selectById(prog_id, obl_id, prod_id);
				prodRedDb=datosRedAccess.selectById(pred_id);
				errorAccess = new ProdErrorAccess(conex);
				prodErrDb=errorAccess.selectProdErrorById(prog_id, obl_id, prod_id);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			ProdDatos producto = null;

			if(prodDb!=null){
					producto = new ProdDatos(new File(prodDb.getAbsolutePath()));
			}else if(prodErrDb!=null){
				producto = new ProdDatos(new File(prodErrDb.getAbsolutePath()));
			}

			if(prodDb!=null || prodRedDb!=null || prodErrDb!=null){
				
				try{
					/*
					if(par_type!=null && par_type.equals("PREVIEW")){
						response.setContentType ("image/png");
						response.setHeader("Content-Disposition", "attachment;filename="+producto.getPreviewFilename());
						producto.writePreviewToStream(outs);
					}*/
					String filename = "";
					int filesize = 0;
					if(prodRedDb!=null){
						filename=prodRedDb.getPredFilename();
						filesize=prodRedDb.getPredFilesize().intValue();
					}else{
						filename=producto.getFile().getName();
						filesize=producto.getFileSize().intValue();
					}
					
					if(bibcode!=null){
						filename = filename.replaceAll(".fit", "_bibcode_"+bibcode+".fit");
					}
					
					response.setContentType ("application/fits");
					response.setHeader("Content-Disposition", "attachment;filename="+filename);
					response.setContentLength(filesize);
				
					
					if(prodRedDb==null){
						//*************************************************************
						//*  ESTADISTICAS                                             *
						//*************************************************************
						LogProdDb log = new LogProdDb();
						Date time = new Date();
						long size= 0;
						log.setLogpTime(new Timestamp(time.getTime()));
						log.setLogpHost(request.getRemoteHost());
						
						if(par_fetchType!=null && par_fetchType.trim().equalsIgnoreCase("PREVIEW")){
							log.setLogpType("PREVIEW");
						}else if (par_fetchType!=null && par_fetchType.trim().equalsIgnoreCase("PRED")){
							System.out.println("pred...");
							log.setLogpType("PRED");
						}else if (par_fetchType!=null && par_fetchType.trim().equalsIgnoreCase("VO_PRED")){
							log.setLogpType("VO_PRED");
						}else{
							log.setLogpType("SINGLE");
						}
						
						LogProdSingleDb prodSingle = new LogProdSingleDb(producto);
						log.addProd(prodSingle);
						
						size=producto.writeToStream(outs);
	
						/// Estadisticas cont...
						Date timeEnd = new Date();
						log.setLogpElapsed( new Double((double)(timeEnd.getTime()-time.getTime())/1000) );
						log.setLogpSize(new Long(size));
						
						LogProdAccess logAccess;
						try {
							logAccess = new LogProdAccess(conex);
							logAccess.insert(log);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}else{
						
						//*************************************************************
						//*  ESTADISTICAS                                             *
						//*************************************************************
						LogProdDb log = new LogProdDb();
						Date time = new Date();
						long size= 0;
						log.setLogpTime(new Timestamp(time.getTime()));
						log.setLogpHost(request.getRemoteHost());
						
						if(par_fetchType!=null && par_fetchType.trim().equalsIgnoreCase("PREVIEW")){
							log.setLogpType("PREVIEW");
						}else if (par_fetchType!=null && par_fetchType.trim().equalsIgnoreCase("PRED")){
							log.setLogpType("PRED");
						}else if (par_fetchType!=null && par_fetchType.trim().equalsIgnoreCase("VO_PRED")){
							log.setLogpType("VO_PRED");
						}else{
							log.setLogpType("SINGLE");
						}
						
						//LogProdSingleDb prodSingle = new LogProdSingleDb(producto);
						//log.addProd(prodSingle);
						
						size = prodRedDb.writeToStream(outs);
						//size=producto.writeToStream(outs);
	
						/// Estadisticas cont...
						Date timeEnd = new Date();
						log.setLogpElapsed( new Double((double)(timeEnd.getTime()-time.getTime())/1000) );
						log.setLogpSize(new Long(size));
						
						LogProdAccess logAccess;
						try {
							logAccess = new LogProdAccess(conex);
							logAccess.insertPred(log,pred_id);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					//prodRedDb.writeToStream(outs);
					}

				}catch(FileNotFoundException e){
					MSG="ERROR: File not found: "+producto.getFile().getName();
				}
			}else{
				MSG="ERROR: Product not found: "+par_prodId;
			}
			
			
		}else if(par_progId!=null && par_oblId!=null && par_logFilename!=null){
			LogFileAccess datosAccess = null;
			LogFileDb log = null;
			/// Desactivamos la descarga de Logs
			
			try {
				datosAccess = new LogFileAccess(conex);
				log = datosAccess.selectById(par_progId, par_oblId, par_logFilename);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			
			if(log!=null){
			
				File fichero = log.getFile();
			
				try{
					/*
					if(par_type!=null && par_type.equals("PREVIEW")){
						response.setContentType ("image/png");
						response.setHeader("Content-Disposition", "attachment;filename="+producto.getPreviewFilename());
						producto.writePreviewToStream(outs);
					}*/
					
					response.setContentType ("text/plain");
					response.setHeader("Content-Disposition", "attachment;filename="+fichero.getName());
					
					response.setContentLength((int)fichero.length());
					log.writeToStream(outs);
					
				}catch(FileNotFoundException e){
					MSG="ERROR: File not found: "+fichero.getName();
				}
			}else{
				MSG="ERROR: File not found: "+par_logFilename;
			}
		}
			
		if(MSG.length()>0){
			outs.println("<html>");
			outs.println(cabeceraPagina);
			outs.println("<body>");
			outs.println(encabezamiento);
			outs.println("<p><br></p>");
			outs.println("<font color=\"red\">"+MSG+"</font>");
			outs.println(pie);
			outs.println("</body>");
			outs.println("</html>");
		}


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