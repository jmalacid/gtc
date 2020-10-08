<%@page import="svo.sepub.html.webHTML,
				svo.sepub.buscador.Buscador,
				java.io.IOException,java.io.InputStreamReader,
				java.net.MalformedURLException,
				java.sql.SQLException" %>

<%
webHTML whtml = new webHTML(request.getContextPath());
%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%=whtml.encabezado() %>
<body>

<table border=0>
<tr><td><img src="../images/sepub.gif"></td></tr>
</table>

<table style="width:800px">

<tr bgcolor="#3A50A0">
<td align="center"><font color="#FFFFFF" size=+2>SEarch of PUBlications</font>
</td>
</tr>
</table>
<br>
<%
String proyecto = request.getParameter("bus");

String mns = null;
Buscador bus = null;
String[] bib_ok = null;
Integer num = null;

try{
	bus = new Buscador(proyecto);
	bib_ok = bus.getBib_ok().split(";");
	num = bus.getTotal();
	
} catch (MalformedURLException e) {
	mns="Hay un error en el acceso al ADS Fulltext: "+e.getMessage();
	e.printStackTrace();
} catch (IOException e) {
	mns="Hay un error en el tratamiento de la página: "+e.getMessage();
	e.printStackTrace();
} catch (SQLException e) {
	mns="Hay un error en el acceso a la base de datos: "+e.getMessage();
	e.printStackTrace();
}

if(mns!=null){
%>
<font color=red><blink><b><%=mns %></b></blink></font>
<%
}else{

	if(bus.getBib_ok()==null || bus.getBib_ok().length()==0){
		%><p class="mensajeinfo">No se ha encontrado ninguna publicación nueva desde la última búsqueda<br> Total publicaciones revisadas: <%=num%></p><%
		
	}else{
%>
<p class="mensajeinfo">Total de publicaciones revisadas: <%=num%><br>
Bibcodes nuevos encontrados (<%=bib_ok.length %>):</p>
<table  class="appstyle">
<tr>
<th class="rescab">Bibcode</th>
</tr>
<%for(int i=0;i<bib_ok.length;i++){%>
	<tr>
	<td class="resfield"><a href="http://adsabs.harvard.edu/cgi-bin/nph-data_query?bibcode=<%=bib_ok[i].replaceAll("&", "%26") %>&db_key=PRE&link_type=ABSTRACT"><%=bib_ok[i] %></a></td>
	</tr>
<%} %>
</table>


<%	}
}%>
<br>
<p class="link"><a href="verSP.jsp?bus=<%=proyecto%>">Ir a la gestión de publicaciones</a></p>
<p class="link"><a href="indexSepub.jsp">Ir al Menú</a></p>
<br>
<%= whtml.pie() %>
</body>
</html>