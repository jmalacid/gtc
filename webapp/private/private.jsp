<%@ page import="svo.gtc.web.Html,
	svo.gtc.web.ContenedorSesion" %>
                 
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

	//---- Elementos est�ticos de la p�gina
	Html elementosHtml = new Html(request.getContextPath());
	String cabeceraPagina = elementosHtml.cabeceraPagina("GTC","Private zone");
	String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath()+"?"+request.getQueryString());
	String pie            = elementosHtml.pie();
%>

<%--------     I N I C I O    P � G I N A    H T M L    -------------------%>

<%= cabeceraPagina %>

<body>

<%= encabezamiento %>


<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px" >

<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER><font face="arial,helvetica,san-serif"><font
color="#FFFFFF"><font size=+2>&nbsp;The Gran Telescopio CANARIAS Archive<br><font color=red>-Private zone-</font></font></font></font>
</td>
</tr>
<tr>
<td ALIGN="justify"><br><i><font face="arial,helvetica,geneva">

<br>
<tr>
<td ALIGN=LEFT>


<!-- **********  Tabla enlaces **************************  -->
<table BORDER=0 CELLSPACING=0 CELLPADDING=2 >

<tr>
<td NOWRAP><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<font face="arial,helvetica,san-serif">
	<font size=+0><b><a href="/gtc/private/stats.jsp">Estad�sticas </a></b></font></font></td>
</tr>

<tr>
<td NOWRAP><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<font face="arial,helvetica,san-serif">
	<font size=+0><b><a href="/gtc/private/errors.jsp">Control de Errores</a></b></font></font></td>
</tr>


<tr>
<td NOWRAP><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<font face="arial,helvetica,san-serif">
	<font size=+0><b><a href="/gtc/private/reduc.jsp">Control de datos reducidos</a></b></font></font></td>
</tr>
<tr>
<td NOWRAP><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<font face="arial,helvetica,san-serif">
	<font size=+0><b><a href="/gtc/private/testTempRed.jsp">Comprobar si alg�n dato reducido en espera se puede subir</a></b></font></font></td>
</tr>
<tr>
<tr>
<td NOWRAP><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<font face="arial,helvetica,san-serif">
	<font size=+0><b><a href="/gtc/private/testPubPred.jsp">Comprobar si datos reducidos no tienen publicaci�n asociada</a></b></font></font></td>
</tr>
<tr>
<td NOWRAP><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<font face="arial,helvetica,san-serif">
	<font size=+0><b><a href="/gtc/private/pubArchive.jsp">Publicaciones que hacen uso del archivo</a></b></font></font></td>
</tr>
<tr>
<td NOWRAP><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<font face="arial,helvetica,san-serif">
	<font size=+0><b><a href="/gtc/private/verDatos.jsp">Ver Histograma de datos por fecha</a></b></font></font></td>
</tr>

<tr><td>---------------------------------------------</td></tr>
<tr>
<td NOWRAP><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<font face="arial,helvetica,san-serif">
	<font size=+0><b><a href="/gtc/help/updateOverview.jsp">Actualizar System Overview</a></b></font></font></td>
</tr>
<tr>
<td NOWRAP><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<font face="arial,helvetica,san-serif">
	<font size=+0><b><a href="/gtc/private/programName.jsp">Actualizar nombre de programas y pi</a></b></font></font></td>
</tr>
<tr>
<tr>
<td NOWRAP><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<font face="arial,helvetica,san-serif">
	<font size=+0><b><a href="/gtc/private/pipend.jsp">PI pendientes de recibir user/pw</a></b></font></font></td>
</tr>
<tr>
<td NOWRAP><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<font face="arial,helvetica,san-serif">
	<font size=+0><b><a href="/gtc/jsp/ADS_estadoNew.jsp">Comprobar publicaciones en ADS</a> <a href="/gtc/jsp/ADS_estadoNew.jsp?t=all">(recomprobar todas)</a></b></font></font></td>
</tr>
<tr><td>---------------------------------------------</td></tr>
<tr>
<td NOWRAP><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<font face="arial,helvetica,san-serif">
	<font size=+0><b><a href="/gtc/private/indexSepub.jsp">SePub (b�squeda de publicaciones)</a></b></font></font></td>
</tr>


</table>
<!-- **********  Fin Tabla enlaces **************************  -->
</td>
</tr>

</table>
<br><br>

<%= pie %>
</body>
</html>
