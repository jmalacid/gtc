<%@page import="svo.sepub.html.webHTML,
				java.sql.Connection,
				java.sql.SQLException,
				svo.sepub.db.AccessDB,
				svo.sepub.results.Bibcode,
				javax.sql.*,
				javax.naming.Context,
				javax.naming.InitialContext,
				javax.naming.NamingException" %>

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

<%
//Abrimos la conexión a la base de datos
Context initContext;
Connection cn=null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();

AccessDB adb = new AccessDB(cn);
AccessDB gtc = new AccessDB(cn);

Bibcode[] bib = adb.viewBibEst("gtc",2);
String[] gtc_bib = gtc.viewGTC(); 


%>

<table style="width:800px">

<tr bgcolor="#3A50A0">
<td align="center"><font color="#FFFFFF" size=+2>SEarch of PUBlications</font>
</td>
</tr>
</table>
<br>
<p class="link"><a href="verSP.jsp?bus=gtc&c=todo">Ver todos los Bibcodes</a></p>
<p class="link"><a href="verSP.jsp?bus=gtc&c=0">Ver Bibcodes sin modificar</a></p>
<p class="link"><a href="verSP.jsp?bus=gtc&c=2">Ver Bibcodes aceptados</a></p>
<p class="link"><a href="verSP.jsp?bus=gtc&c=1">Ver Bibcodes rechazados</a></p>
<br>
<p class="link"><a href="../index.jsp">Volver al Menú</a></p>
<br>

<table class="appstyle">
<tr  bgcolor="#FFFFFF">
<th class="rescab">Bibcode</th>
<th class="rescab">Detalles</th>
<th class="rescab">Modificar</th>
</tr>
<%


for(int i = 0; i<bib.length; i++){
	String is = "no";
	
	for(int j = 0; j<gtc_bib.length; j++){
		
		if(bib[i].getBibcode().toUpperCase().trim().equals(gtc_bib[j].toUpperCase().trim())){
			is="si";
		}
	}
	
	if(!is.equals("si")){
		%><tr bgcolor="resfield">
		<td class="resfield"><a href="http://adsabs.harvard.edu/cgi-bin/nph-data_query?bibcode=<%=bib[i].getBibcode().replaceAll("&", "%26")%>&db_key=PRE&link_type=ABSTRACT" target="blank"><%=bib[i].getBibcode() %></a></td>
		<%if(bib[i].getDesc()!=null){ %>
			<td class="resfield"><%=bib[i].getDesc() %></td>
		<%}else{ %>
			<td class="resfield" align=center> - </td>
		<%} %>
		<td class="resfield"><a href="modificar.jsp?id=<%=bib[i].getBus_id()%>">Modificar</a></td>
	</tr>
		
	<%}
%>
	
<%} %>
</table>
<br>
<%}catch(SQLException ex){
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
} %>
<%= whtml.pie() %>
</body>
</html>