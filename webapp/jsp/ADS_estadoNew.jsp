<%@ page import="svo.gtc.web.Html,
				svo.gtc.db.priv.DBPrivate,
				java.sql.*,
				svo.gtc.ads.TestADS,
				
				javax.sql.*,
				javax.naming.Context,
				javax.naming.InitialContext,
				javax.naming.NamingException" %>
<%
//response.setContentType ("text/plain");


//---- Elementos estáticos de la página
	Html elementosHtml = new Html(request.getContextPath());
	String cabeceraPagina = elementosHtml.cabeceraPagina("ADS estado","ADS estado");
	String encabezamiento = elementosHtml.encabezamiento(null, null);
	String pie            = elementosHtml.pie();
%>
	<html>
	<%= cabeceraPagina %>
	<body>
	<%= encabezamiento %>
	<table width="800" cellspacing="0" cellpadding="2" border="0">
	<tbody>
	<tr>
	<td class="headtitle">
	<center>GTC Archive: ADS State</center>
	</td>
	</tr>
	</tbody>
	</table>
	

<% 
//Valores de la sesión
//String serverName= request.getServerName();
//int port = request.getServerPort();

//Averiguamos si hay que comprobar todas las publicaciones (t=all)
String t = null;
try{
t = request.getParameter("t");
}catch(Exception e){
t = "normal";
}

//Abrimos la conexión a la base de datos
Context initContext;
Connection cn=null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();

DBPrivate union = new DBPrivate(cn);
if(t!=null && t.equalsIgnoreCase("ALL")){

	
String[] info = union.coleccionFecha("ALL");



%>
<table class="appstyle" cellspacing="3" cellpadding="20" >
<tr>
<td class="rescab" align="center" nowrap> Bibcode </td>	
<td class="rescab" align="center" nowrap> fecha</td>
<td class="rescab" align="center" nowrap> test</td>
</tr><%

for(int i =0; i<info.length; i++){
	
	String bibcode = info[i].split("-.-")[0];
	String fecha = info[i].split("-.-")[1];
	
//	System.out.println("bibcode: "+bibcode);
	
	TestADS a = new TestADS(bibcode);
	boolean state = false;
	
	try{
		state = a.bibcodeState();
	}catch(Exception e){
		//problema al acceder a esa publicación
	}
	if(state==true){
		
		//Habría que cambiar el estado a SI
		union.updateADS(bibcode);
		
	%>
	<tr>
		<td class="resfield" align="center">&nbsp;&nbsp;<%= bibcode %>&nbsp;&nbsp;</td>
		<td class="resfield" align="center">&nbsp;&nbsp;<%= fecha %>&nbsp;&nbsp;</td>
		<td class="resfield" align="right">&nbsp;&nbsp;<a href="http://adsabs.harvard.edu/cgi-bin/nph-data_query?bibcode=<%=bibcode.replaceAll("&", "%26")%>&link_type=DATA&db_key=AST&high=" target=blank>test</a>&nbsp;&nbsp;</td>
	</tr>
<%
	}else{
		%>
		<tr>
		<td class="resfield_error" align="center">&nbsp;&nbsp;<%= bibcode %>&nbsp;&nbsp;</td>
		<td class="resfield_error" align="right">&nbsp;&nbsp;<%= fecha %>&nbsp;&nbsp;</td>
		<td class="resfield_error" align="right">&nbsp;&nbsp;<a href="http://adsabs.harvard.edu/cgi-bin/nph-data_query?bibcode=<%=bibcode.replaceAll("&", "%26")%>&link_type=DATA&db_key=AST&high=" target=blank>test</a>&nbsp;&nbsp;</td>
		</tr>
		<% 
	}
}
%>

</table><%
}else{
	
String[] nuevos = union.coleccionFecha("NO");
String[] viejos = union.coleccionFecha("SI");

%>
<table class="appstyle" cellspacing="3" cellpadding="20" >
<tr>
	<th colspan=3> Publicaciones comprobadas </th>
</tr>
<tr>
<td class="rescab" align="center" nowrap> Bibcode </td>	
<td class="rescab" align="center" nowrap> fecha</td>
<td class="rescab" align="center" nowrap> test</td>
</tr><%

for(int i =0; i<nuevos.length; i++){
	
	String bibcode = nuevos[i].split("-.-")[0];
	String fecha = nuevos[i].split("-.-")[1];
	
	//System.out.println("bibcode nuevo: "+bibcode);
	
	TestADS a = new TestADS(bibcode);
	boolean state = false;
	
	try{
		state = a.bibcodeState();
	}catch(Exception e){
		//problema al acceder a esa publicación
	}
	if(state==true){
		union.updateADS(bibcode);
	%>
	
	<tr>
		<td class="resfield" align="center">&nbsp;&nbsp;<%= bibcode %>&nbsp;&nbsp;</td>
		<td class="resfield" align="center">&nbsp;&nbsp;<%= fecha %>&nbsp;&nbsp;</td>
		<td class="resfield" align="right">&nbsp;&nbsp;<a href="http://adsabs.harvard.edu/cgi-bin/nph-data_query?bibcode=<%=bibcode.replaceAll("&", "%26")%>&link_type=DATA&db_key=AST&high=" target=blank>test</a>&nbsp;&nbsp;</td>
	</tr>
<%
	}else{
		%>
		<tr>
		<td class="resfield_error" align="center">&nbsp;&nbsp;<%= bibcode %>&nbsp;&nbsp;</td>
		<td class="resfield_error" align="right">&nbsp;&nbsp;<%= fecha %>&nbsp;&nbsp;</td>
		<td class="resfield_error" align="right">&nbsp;&nbsp;<a href="http://adsabs.harvard.edu/cgi-bin/nph-data_query?bibcode=<%=bibcode.replaceAll("&", "%26")%>&link_type=DATA&db_key=AST&high=" target=blank>test</a>&nbsp;&nbsp;</td>
		</tr>
		<% 
	}
	%>


	<%
}
 
%>

</table>
<table class="appstyle" cellspacing="3" cellpadding="20" >
<tr>
	<th colspan=3> Publicaciones antiguas </th>
	</tr>
<tr>
<td class="rescab" align="center" nowrap> Bibcode </td>	
<td class="rescab" align="center" nowrap> fecha</td>
<td class="rescab" align="center" nowrap> test</td>
</tr>
<%
for(int i =0; i<viejos.length; i++){
	%><tr>
	<td class="resfield" align="center">&nbsp;&nbsp;<%= viejos[i].split("-.-")[0] %>&nbsp;&nbsp;</td>
	<td class="resfield" align="center">&nbsp;&nbsp;<%= viejos[i].split("-.-")[1] %>&nbsp;&nbsp;</td>
	<td class="resfield" align="right">&nbsp;&nbsp;<a href="http://adsabs.harvard.edu/cgi-bin/nph-data_query?bibcode=<%=viejos[i].split("-.-")[0].replaceAll("&", "%26")%>&link_type=DATA&db_key=AST&high=" target=blank>test</a>&nbsp;&nbsp;</td>
</tr><%
}
	%>
	</table>
	<% 

}
}catch(SQLException ex){
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
}
%>

<%= pie %>
</body>
</html>