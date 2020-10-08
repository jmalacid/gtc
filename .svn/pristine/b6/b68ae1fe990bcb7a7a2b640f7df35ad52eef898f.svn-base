
<%@ page import="svo.gtc.web.Html,
				svo.gtc.web.ContenedorSesion"%>
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
<table class="appstyle" cellspacing="3" cellpadding="20">
<tr>
	<td class="rescab" align="center" nowrap> Total publications </td>
	<td class="rescab" align="center" nowrap> Accepted </td>
	<td class="rescab" align="center" nowrap> Reject </td>
	<td class="rescab" align="center" nowrap> In progress </td>
</tr>
<tr>
	<td class="resfield" align="center" nowrap> 55 </td>
	<td class="resfield" align="center" nowrap> 23 </td>
	<td class="resfield" align="center" nowrap> 13 </td>
	<td class="resfield" align="center" nowrap> 19 </td>
</tr>

	
		
</table>
<%= pie %>