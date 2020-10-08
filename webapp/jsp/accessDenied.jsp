<%@ page import="svo.gtc.web.Html,
				 svo.gtc.web.ContenedorSesion,
				 svo.gtc.db.usuario.UsuarioDb,
				 svo.gtc.db.usuario.UsuarioAccess,
				
				 java.sql.*,
				 java.util.TimeZone,
                 java.util.Locale,
                 java.util.Enumeration" %>
                 
<%@ page isThreadSafe="true" %>
<%@ page info="GTC Access Denied" %>


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

//*************************************************************
//*  TECLAS DEL FORMULARIO                                    *
//*************************************************************

	String pagOrigen = request.getServletPath()+"?"+request.getQueryString();
	if(request.getAttribute("pagOrigen")!=null){
		pagOrigen=(String)request.getAttribute("pagOrigen");
	}

	//---- Elementos estáticos de la página
	Html elementosHtml = new Html(request.getContextPath());
	String cabeceraPagina = elementosHtml.cabeceraPagina("Login","Login for GTC Archive.");
	String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), pagOrigen);
	String pie            = elementosHtml.pie();
	
	

%>



<%--------     I N I C I O    P Á G I N A    H T M L    -------------------%>
<html>
<%= cabeceraPagina %>
<body>
<%= encabezamiento %>

<table width="800" cellspacing="0" cellpadding="2" border="0">
<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER>
<font face="arial,helvetica,san-serif"><font
color="#FFFFFF"><font size=+2>&nbsp;GTC Archive: Login Form</font></font></font>
</td>
</tr>
</table>
<p><br></p>
<p><br></p>

<p class="appstyle"><font color="red" size=+1>Not sufficient access privileges.</font></p><br><br>
<p class="appstyle"><a href="<%=request.getContextPath()%>/jsp/searchform.jsp">Back</a></p>


<p>
<br>
</p>
<p><br></p>

<%= pie %>
</body>
</html>

