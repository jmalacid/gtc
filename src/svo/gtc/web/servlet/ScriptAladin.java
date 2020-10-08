package svo.gtc.web.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import svo.gtc.utiles.Descargar;

public class ScriptAladin  extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public ScriptAladin() {
		super();
	}

	/**
	 * @throws SQLException
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		response.setContentType ("text/plain");
		response.setHeader("Content-Disposition", "inline;filename=aladin.ajs");
		//creamos el contenido del fichero
		String prodId = request.getParameter("prod_id");
		String predId = request.getParameter("pred_id");
		
		String texto = ""; 

		//Valores de la sesión
		String serverName= request.getServerName();
		int port = request.getServerPort();
		String path = request.getContextPath();

		texto = texto + "#cargar el fits\n";
		if(prodId!=null && prodId.length()>0){
			texto = texto + "load http://"+serverName+":"+port+path+"/servlet/FetchProd?prod_id="+prodId+"&fetch_type=PREVIEW&.xml\n";
		}else if(predId!=null && predId.length()>0){
			texto = texto + "load http://"+serverName+":"+port+path+"/servlet/FetchProd?pred_id="+predId+"&fetch_type=PREVIEW&.xml\n";
		}
		
		Descargar.descargar(response.getOutputStream(), texto);
	}
			

	/**
	 * @throws SQLException
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}
}
