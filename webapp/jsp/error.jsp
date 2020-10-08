<%@ page import="svo.gtc.web.Html,
				 svo.gtc.web.ContenedorSesion,
				 svo.gtc.db.usuario.UsuarioDb,
				 svo.gtc.db.usuario.UsuarioAccess,
				
				 java.sql.*" %>
                 
<%@ page isThreadSafe="true" %>
<%@ page info="GTC login" %>


<%-- P  R  O  C  E  S  O  S --%>
<%	


//======================================================= 
//  Conexion con la Base de Datos                       
//=======================================================



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


String mensaje = (String)request.getAttribute("mensaje");
if(mensaje==null) mensaje=request.getParameter("mensaje");
if(mensaje==null) mensaje="Undefined error.";


String origen = (String)request.getAttribute("origen");
if(origen==null) origen=request.getParameter("origen");
if(origen==null) origen="/index.jsp";


//*************************************************************
//*  TECLAS DEL FORMULARIO                                    *
//*************************************************************

	//---- Elementos estáticos de la página
	Html elementosHtml = new Html(request.getContextPath());
	String cabeceraPagina = elementosHtml.cabeceraPagina("Error","Error report.");
	String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), origen);
	String pie            = elementosHtml.pie();

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
<center>GTC Archive: Error report</center>
</td>
</tr>
</tbody>
</table>

<p class="aviso salto"><font size="+1"><%=mensaje %></p>

<p class="salto">
<form method="post" action="<%=request.getContextPath()+origen %>">
<input type="submit" name="Back" value="Back">
</form>
</p>


<%= pie %>
</body>
</html>
