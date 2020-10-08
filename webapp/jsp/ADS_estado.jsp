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
	
	

<table class="appstyle" cellspacing="3" cellpadding="20" >
<tr>
<td class="rescab" align="center" nowrap> Bibcode </td>	
<td class="rescab" align="center" nowrap> fecha</td>
<td class="rescab" align="center" nowrap> test</td>
</tr>

<% 
//Valores de la sesión
String serverName= request.getServerName();
int port = request.getServerPort();

//Abrimos la conexión a la base de datos
Context initContext;
Connection cn=null;
String[] info = null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();

DBPrivate union = new DBPrivate(cn);


//info = union.coleccionFecha();
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


for(int i =0; i<info.length; i++){
	
	String bibcode = info[i].split("-.-")[0];
	String fecha = info[i].split("-.-")[1];
	
	TestADS a = new TestADS(info[i]);
	boolean state = false;
	
	try{
		state = a.bibcodeState();
	}catch(Exception e){
		//problema al acceder a esa publicación
	}
	if(state==true){
		
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
<%= pie %>
</body>
</html>