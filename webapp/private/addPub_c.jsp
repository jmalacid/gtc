<%@ page import="svo.gtc.web.Html,
	svo.gtc.web.ContenedorSesion,
	svo.gtc.priv.objetos.Bibcodes" %>
                 
<%@ page info="GTC Scientific Archive Homepage" %>

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

	//---- Elementos estáticos de la página
	Html elementosHtml = new Html(request.getContextPath());
	String cabeceraPagina = elementosHtml.cabeceraPagina("GTC","Private zone");
	String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath()+"?"+request.getQueryString());
	String pie            = elementosHtml.pie();
%>

<%--------     I N I C I O    P Á G I N A    H T M L    -------------------%>

<%= cabeceraPagina %>

<body>

<%= encabezamiento %>
<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px" >

<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER><font face="arial,helvetica,san-serif"><font
color="#FFFFFF"><font size=+2>&nbsp;The Gran Telescopio CANARIAS Archive<br><font color=red>-Private zone-</font></font></font></font>
</td>
</tr>
</table>
<br><br>
<%
try{
	
	Bibcodes bc = new Bibcodes(request);
	String mensaje = bc.insertDB();
	%>
	<p class="formbus1"><font face="arial,helvetica,san-serif"><b> Publicación agregada correctamente, pero tenga en cuenta que: <br><%=mensaje %></b></font></p>
	
	<%
	
}catch(Exception E){
	%>
	<p class="formbus1"><font face="arial,helvetica,san-serif"><b> Se ha producido un error: <%= E.getMessage()%></b></font></p>
	
	<%
}
%>

<br>
	<p class="formbus1"><a href="reduc.jsp"> Volver </a></p>
	<br>



	

<%= pie %>
</body>
</html>