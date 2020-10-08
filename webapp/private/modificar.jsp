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


<table style="width:800px">

<tr bgcolor="#3A50A0">
<td align="center"><font color="#FFFFFF" size=+2>SEarch of PUBlications</font>
</td>
</tr>
</table>

<%
String submit = request.getParameter("submit");

///// COMPROBACIÓN DE VALORES DEL FORMULARIO
String msg = "";
Bibcode parametrosFormulario = null;
if(submit!=null && submit.length()>0){
	try{
		parametrosFormulario = new Bibcode(request);
		parametrosFormulario.AddChange();
	}catch(Exception e){
		msg+=e.getMessage();
	}
}


if(msg.length()>0){
%>
	<p class="formbus1"><font color="red"><blink><%=msg %></blink></font></p>
<% 	
}else if(submit!=null && submit.length()>0){
%> 
	<jsp:forward page="verSP.jsp"/> 
<%	} %>

<%
Integer bus_id = Integer.valueOf(request.getParameter("id").trim());
//Abrimos la conexión a la base de datos
Context initContext;
Connection cn=null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();
AccessDB adb = new AccessDB(cn); 
Bibcode bib = adb.viewBibcode(bus_id);
String[] ests = adb.viewEsts();

//System.out.println("valor de ests: "+ests.length);
%>

<br>
<form id="modificar" name="modificar" method="get"
	action="modificar.jsp" enctype="application/x-www-form-urlencoded"
	onSubmit=""><input type="hidden" value="<%=bus_id %>" name="id" />
	<input type="hidden" value="<%=bib.getProyecto() %>" name="bus" /><input type="hidden" value="0" name="c" />
<table class="appstyle">
<tr  bgcolor="#FFFFFF">
<td class="rescab">Bibcode</td>
<td class="resfield"><a href="http://adsabs.harvard.edu/cgi-bin/nph-data_query?bibcode=<%=bib.getBibcode().replaceAll("&", "%26") %>&db_key=PRE&link_type=ABSTRACT" target="blank"><%=bib.getBibcode() %></a></td>
</tr>
<tr  bgcolor="#FFFFFF">
<td class="rescab">Proyecto</td>
<td class="resfield"><%=bib.getProyecto() %></td>
</tr>
<tr  bgcolor="#FFFFFF">
<td class="rescab">Estado</td>
<td class="resfield"><%=bib.getEstado() %></td>
<td class="resfield" align="center"><select class="gform" name="est_id">
			<option value="-" selected>-</option>
			<%for(int i=0; i<ests.length;i++){ 
			String [] valor = ests[i].split("-.-");%>
					<option value="<%=valor[0] %>"><%=valor[1] %></option>
					<% 
				}%>
			</select></td>
</tr>
<tr  bgcolor="#FFFFFF">
<td class="rescab">Comentarios</td>
<%if(bib.getDesc()==null || bib.getDesc().length()==0){ %>
	<td class="resfield">-</td>
<%}else{ %>
	<td class="resfield"><%=bib.getDesc() %></td>
<%} %>
<td class="resfield"><input type="text" name="desc" maxlength="200" size="40" /></td>
</tr>
</table>
<input type="submit" name="submit" value="Aplicar cambios" />
</form>
<br>
<p>Al aceptar un bibcode hay que completar la información de este <a href="addPub.jsp?bibcode=<%=bib.getBibcode().replaceAll("&", "%26")%>" target="_blank">aquí</a> </p>
<br>
<p>Si la publicación contiene datos del archivo añadirlo <a href="addPub.jsp?bibcode=<%=bib.getBibcode().replaceAll("&", "%26")%>" target="_blank">aquí</a> </p>
<br>

<p class="link"><a href="verSP.jsp?bus=<%=bib.getProyecto()%>&c=todo">Ver todos los Bibcodes</a></p>
<p class="link"><a href="verSP.jsp?bus=<%=bib.getProyecto()%>&c=0">Ver Bibcodes sin modificar</a></p>
<p class="link"><a href="verSP.jsp?bus=<%=bib.getProyecto()%>&c=2">Ver Bibcodes aceptados</a></p>
<p class="link"><a href="verSP.jsp?bus=<%=bib.getProyecto()%>&c=1">Ver Bibcodes rechazados</a></p>
<br>
<p class="link"><a href="indexSepub.jsp">Volver al Menú</a></p>

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