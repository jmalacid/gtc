<%@ page import="svo.gtc.web.Html,
	svo.gtc.web.ContenedorSesion,
	svo.gtc.priv.objetos.TempRed,
	svo.gtc.priv.objetos.RedObj" %>
                 
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



<%
TempRed tr = new TempRed();

RedObj[] fichs = tr.getFicheros_ok(); 

if(fichs.length>0){
%>
<table class="appstyle" cellspacing="3" cellpadding="20" >
	<tr><th class="rescab" colspan="7">Se han encontrado <%=fichs.length %> ficheros para subir</th></tr>
	<tr>
	<td class="rescab" align="center" nowrap> Nombre </td>	
	<td class="rescab" align="center" nowrap> User</td>
	<td class="rescab" align="center" nowrap> Programa</td>
	<td class="rescab" align="center" nowrap> Obl</td>
	<td class="rescab" align="center" nowrap> Date Obs</td>
	<td class="rescab" align="center" nowrap> Open Time</td>
	<td class="rescab" align="center" nowrap> Close Time</td>
	<td class="rescab" align="center" nowrap> Mensaje</td>
	<td class="rescab" align="center" nowrap> Header</td>
	</tr>
<%	
for(int i=0; i<fichs.length;i++){
	%>
	<tr>
	<td class="resfield"><%=fichs[i].getFich().getName() %></td>
	<td class="resfield"><%=fichs[i].getFich().getParentFile().getName()%></td>
	<td class="resfield"><%=fichs[i].getProg() %></td>
	<td class="resfield"><%=fichs[i].getObl() %></td>
	<td class="resfield"><%=fichs[i].getDate() %></td>
	<td class="resfield"><%=fichs[i].getOpenTime() %></td>
	<td class="resfield"><%=fichs[i].getCloseTime() %></td>
	<td class="resfield"><%=fichs[i].getMensaje() %></td>
	<td class="resfield"><a href="viewHeader.jsp?id=<%=fichs[i].getFich().getAbsolutePath()%>" target=_blank>ver</a></td>
	</tr>
	<%
}
%>
</table>

<%
}
%>
<br>
<a href="private.jsp"><b>Volver</b></a>
<br>
<%

RedObj[] fichs_no = tr.getFicheros_no();
if(fichs_no.length>0){
%>
<table class="appstyle" cellspacing="3" cellpadding="20" >
	<tr><th class="rescab" colspan="7">El resto de los <%=fichs_no.length %> ficheros no tienen un correspondiente raw</th></tr>
	<tr>
	<td class="rescab" align="center" nowrap> Nombre </td>	
	<td class="rescab" align="center" nowrap> User</td>
	<td class="rescab" align="center" nowrap> Programa</td>
	<td class="rescab" align="center" nowrap> Obl</td>
	<td class="rescab" align="center" nowrap> DateObs</td>
	<td class="rescab" align="center" nowrap> Open Time</td>
	<td class="rescab" align="center" nowrap> Close Time</td>
	<td class="rescab" align="center" nowrap> Mensaje</td>
	<td class="rescab" align="center" nowrap> Header</td>
	</tr> 
<%	
for(int i=0; i<fichs_no.length;i++){
	%>
	<tr>
	<td class="resfield"><%=fichs_no[i].getFich().getName() %></td>
	<td class="resfield"><%=fichs_no[i].getFich().getParentFile().getName() %></td>
	<td class="resfield"><%=fichs_no[i].getProg() %></td>
	<td class="resfield"><%=fichs_no[i].getObl() %></td>
	<td class="resfield"><%=fichs_no[i].getDate() %></td>
	<td class="resfield"><%=fichs_no[i].getOpenTime() %></td>
	<td class="resfield"><%=fichs_no[i].getCloseTime() %></td>
	<td class="resfield"><%=fichs_no[i].getMensaje() %></td>
	<td class="resfield"><a href="viewHeader.jsp?id=<%=fichs_no[i].getFich().getAbsolutePath()%>" target=_blank>ver</a></td>
	</tr>
	<%
}
%>
</table>
<%
}
%>

<a href="private.jsp"><b>Volver</b></a>


<%= pie %>
</body>
</html>