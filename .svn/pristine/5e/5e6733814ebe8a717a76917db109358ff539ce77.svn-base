<%@ page import="svo.gtc.web.Html,
				 svo.gtc.web.ContenedorSesion,
				 svo.gtc.db.DriverBD,
				 svo.gtc.db.usuario.UsuarioDb,
				 svo.gtc.db.usuario.UsuarioAccess,
				
				 java.sql.*,
				 svo.varios.utiles.seguridad.Encriptado" %>
                 
<%@ page isThreadSafe="true" %>
<%@ page info="GTC login" %>


<%-- P  R  O  C  E  S  O  S --%>
<%	


//======================================================= 
//  Conexion con la Base de Datos                       
//=======================================================

Connection conex= null;

DriverBD drvBd = new DriverBD();

try {
	conex = drvBd.bdConexion("gtc","jdbc", "h$ZUzY$1");
} 
catch (SQLException errconexion) {
	errconexion.printStackTrace();
}

//*************************************************************
//*  SESION                                                   *
//*************************************************************

ContenedorSesion contenedorSesion = null;
if(session!=null && session.getAttribute("contenedorSesion")!=null){
	contenedorSesion = (ContenedorSesion)session.getAttribute("contenedorSesion");
}else{
	contenedorSesion = new ContenedorSesion();
}


	
// ***************************************************************
// *** PROCESADO DEL FORMULARIO
// ***************************************************************

String username = request.getParameter("username");
if(username==null){
	username="";
}

String password = request.getParameter("password");
if(password==null){
	password="";
}

String origen = (String)request.getAttribute("origen");
if(origen==null) origen=request.getParameter("origen");
if(origen==null) origen="/index.jsp";


String destino = (String)request.getAttribute("destino");

String submit = request.getParameter("executeLogin");
if(submit==null){
	submit="";
}

//*************************************************************
//*  TECLAS DEL FORMULARIO                                    *
//*************************************************************

	//---- Elementos estáticos de la página
	Html elementosHtml = new Html(request.getContextPath());
	String cabeceraPagina = elementosHtml.cabeceraPagina("Login","Login for GTC Archive.");
	String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), origen);
	String pie            = elementosHtml.pie();



UsuarioAccess usuarioAccess = new UsuarioAccess(conex);

UsuarioDb usuario = usuarioAccess.selectByIdPw(username,Encriptado.md5(password));

if(submit.length()>0 && usuario!=null){
	contenedorSesion.setUser(usuario);
	session.setAttribute("contenedorSesion",contenedorSesion);
	conex.close();
	%>
	<jsp:forward page="<%=origen%>"/>
	<%
}else if(username!=null && username.length()>0 && usuario==null){
	request.setAttribute("error", "Invalid username or password.");
	request.setAttribute("origen", origen);
	request.setAttribute("destino", destino);
	request.setAttribute("accion", "error");
	%>
	<jsp:forward page="/servlet/Controlador"/>
	<%
}

%>



<%--------     I N I C I O    P Á G I N A    H T M L    -------------------%>
<html>
<%= cabeceraPagina %>
<body>
<%= encabezamiento %>
<table width="800" cellspacing="0" cellpadding="2" border="0">
<tbody>
<tr>
<td class="headtitle">
<center>GTC Archive: Login Form</center>
</td>
</tr>
</tbody>
</table>
<p><br></p>
<p><br></p>


<form method="post" name="login" action="<%=request.getContextPath() %>/secure/login.jsp">
<input type="hidden" name="origen" value="<%=origen%>"></input>
<table>
	<tr>
		<td><font class="appstyle">Username: </font></td><td><input name="username" type="text" size="30" value="<%=username%>"></td>
	</tr><tr>
		<td><font class="appstyle">Password: </font></td><td><input name="password" type="password" size="30"></td>
	</tr>
</table>
<p>
<br>
</p>
<input type="Submit" name="executeLogin" value="Login"></input>
</form>
<p>
<br>
</p>
<p><br></p>

<%= pie %>
</body>
</html>

<%
	// Cierre de la conexión a la base de datos
    //System.out.println("res_busqueda.jsp: Conexion con la BD cerrada.");
	conex.close();
%>