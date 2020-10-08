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
String proyecto = request.getParameter("bus");
String est = request.getParameter("c");

if(est==null || est.length()==0){
	est="0";
}
//Abrimos la conexión a la base de datos
Context initContext;
Connection cn=null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();

//System.out.println("conex: "+conex);
AccessDB adb = new AccessDB(cn);
Bibcode[] bib = null;

if(est.equals("todo")){
	bib = adb.viewBib(proyecto);
}else{
	bib = adb.viewBibEst(proyecto,Integer.valueOf(est));
}
%>

<table style="width:800px">

<tr bgcolor="#3A50A0">
<td align="center"><font color="#FFFFFF" size=+2>SEarch of PUBlications</font>
</td>
</tr>
</table>
<br>
<p class="link"><a href="verSP.jsp?bus=<%=proyecto%>&c=todo">Ver todos los Bibcodes</a></p>
<p class="link"><a href="verSP.jsp?bus=<%=proyecto%>&c=0">Ver Bibcodes sin modificar</a></p>
<p class="link"><a href="verSP.jsp?bus=<%=proyecto%>&c=2">Ver Bibcodes aceptados</a></p>
<p class="link"><a href="verGTC.jsp">Ver Bibcodes aceptados no introducidos en GTC</a></p>
<p class="link"><a href="verSP.jsp?bus=<%=proyecto%>&c=1">Ver Bibcodes rechazados</a></p>
<br>
<p class="link"><a href="indexSepub.jsp">Volver al Menú</a></p>
<br>

<table class="appstyle">
<tr  bgcolor="#FFFFFF">
<th class="rescab">Bibcode</th>
<th class="rescab">Detalles</th>
<th class="rescab">Modificar</th>
</tr>
<%
for(int i = 0; i<bib.length; i++){
	
	if(!bib[i].getBibcode().contains("MNRAS.tmp")){
		//Modificamos el color de la celda según el estado
		String color = "resfield";
		if(bib[i].getEst_id()==1){
			color = "resfieldRojo";
		}else if(bib[i].getEst_id()==2){
			color = "resfieldVerde";
		}
	%>
		<tr bgcolor="<%=color %>">
			<td class="<%=color %>"><a href="http://adsabs.harvard.edu/cgi-bin/nph-data_query?bibcode=<%=bib[i].getBibcode().replaceAll("&", "%26")%>&db_key=PRE&link_type=ABSTRACT" target="blank"><%=bib[i].getBibcode() %></a></td>
			<%if(bib[i].getDesc()!=null){ %>
				<td class="<%=color %>"><%=bib[i].getDesc() %></td>
			<%}else{ %>
				<td class="<%=color %>" align=center> - </td>
			<%} %>
			<td class="<%=color %>"><a href="modificar.jsp?id=<%=bib[i].getBus_id()%>">Modificar</a></td>
		</tr>
<%	}
} %>
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