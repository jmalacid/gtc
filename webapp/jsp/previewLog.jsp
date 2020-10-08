<%@ page import="svo.gtc.web.Html,
				 svo.gtc.web.ContenedorSesion,
				 svo.gtc.web.AladinApplet,
				 svo.gtc.db.web.*,
				 svo.gtc.db.logfile.LogFileAccess,
				 svo.gtc.db.logfile.LogFileDb,
				 svo.gtc.db.permisos.ControlAcceso,
				 java.sql.*,
				 java.io.File,
				 java.io.FileReader,
				 java.io.InputStreamReader,
				 java.io.BufferedReader,
				 java.io.FileNotFoundException,
				 
				 javax.sql.*,
				javax.naming.Context,
				javax.naming.InitialContext,
				javax.naming.NamingException" %>
                 
<%@ page isThreadSafe="true" %>
<%@ page info="Product Details" %>


<%-- P  R  O  C  E  S  O  S --%>
<%	

//*************************************************************
//*  SESION                                                   *
//*************************************************************

ContenedorSesion contenedorSesion = null;
if(session!=null && session.getAttribute("contenedorSesion")!=null){
	contenedorSesion = (ContenedorSesion)session.getAttribute("contenedorSesion");
}else{
	contenedorSesion = new ContenedorSesion();
	session.setAttribute("contenedorSesion", contenedorSesion);
}



//Abrimos la conexión a la base de datos
Context initContext;
Connection cn=null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();


// *************************************************************
// *  TECLAS DEL FORMULARIO                                    *
// *************************************************************

	//---- Elementos estáticos de la página
	Html elementosHtml = new Html(request.getContextPath());
	String cabeceraPagina = elementosHtml.cabeceraPagina("Product Details","Data Product Details.");
	String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath()+"?"+request.getQueryString());
	String pie            = elementosHtml.pie();

	String par_progId = request.getParameter("prog_id");
	String par_oblId = request.getParameter("obl_id");
	String par_logFilename = request.getParameter("log_filename");
	
	String MSG ="";
	String contenidoFichero = "";

	//======================================================= 
	//Control de Acceso                       
	//=======================================================
	ControlAcceso controlAcceso=null;
	try {
		controlAcceso = new ControlAcceso(cn, contenedorSesion.getUser());
	} catch (SQLException e2) {
		e2.printStackTrace();
	}

	
	LogFileAccess datosAccess = null;
	LogFileDb log = null;
	/// Desactivamos la presentación de los ficheros de log
	
	try {
		datosAccess = new LogFileAccess(cn);
		log = datosAccess.selectById(par_progId, par_oblId, par_logFilename);
	} catch (SQLException e1) {
		e1.printStackTrace();
	}
	
		
//	if(log!=null || par_logFilename.equals("warn")){
		if(!controlAcceso.hasPerm(par_progId, par_oblId,contenedorSesion.getUser())){
			String pagOrigen = request.getServletPath()+"?"+request.getQueryString();
			request.setAttribute("pagOrigen",pagOrigen);
			
			getServletContext().getRequestDispatcher("/jsp/accessDenied.jsp").forward(request,
   					response);
		}

		
		if(log!=null){
			File fichero = log.getFile();
			try{
				BufferedReader br = new BufferedReader(new FileReader(fichero)); 
				String linea = "";
				
				while( (linea=br.readLine())!=null){
					contenidoFichero +=linea+"\n";
				}
						
						
			}catch(FileNotFoundException e){
				MSG="ERROR: File not found: "+fichero.getName();
			}
		}else{
			java.net.URL url = new java.net.URL("http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/servlet/WarningLog?progId="+par_progId.trim()+"&oblId="+par_oblId.trim());
			
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

			String linea = "";
			
			while( (linea=br.readLine())!=null){
				contenidoFichero +=linea+"\n";
			}
					
					
			
		}
	
	//}else{
//		MSG="ERROR: File not found: "+par_logFilename;
//	}
%>



<%--------     I N I C I O    P Á G I N A    H T M L    -------------------%>
<html>
<%= cabeceraPagina %>
<body>
<%= encabezamiento %>


<%if(MSG.length()>0){ %>

<font color="red"><%=MSG%></font>

<%}else{ %>

<%if(!par_logFilename.equals("warn")){ %>
<h1><%=par_logFilename %></h1>
<%} %>

<pre><%=contenidoFichero %></pre>

<%} %>

<p>
<br>
</p>

<%= pie %>
</body>
</html>

<%
}catch(SQLException ex){
	//fallo sql
	System.out.println("Error : "+ex.getMessage());
}catch(NamingException ex){
	System.out.println("Error al intentar obtener el DataSource:"+ex.getMessage());
}finally{
	if(cn != null){
		try{
			cn.close();
		}catch(SQLException ex){
			System.out.println("Error: "+ex.getMessage());
		}
	}
}
%>