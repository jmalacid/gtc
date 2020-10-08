<%@page import="svo.gtc.web.Html,
				svo.gtc.utiles.Mail,
				java.sql.*,
				svo.gtc.db.priv.DBPrivate,
				svo.gtc.priv.objetos.Bibcodes,
				svo.gtc.web.ContenedorSesion,
				javax.sql.*,
				javax.naming.Context,
				javax.naming.InitialContext,
				javax.naming.NamingException" %>

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

<!-- titulo -->
<br>
<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px" >

<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER><font face="arial,helvetica,san-serif"><font
color="#FFFFFF"><font size=+2>&nbsp;The Gran Telescopio CANARIAS Archive<br></font></font></font>
</td>
</tr>
</table>

<%
String prog = request.getParameter("prog");
String obl = request.getParameter("obl");
Integer prod = Integer.valueOf(request.getParameter("prod"));

//Abrimos la conexión a la base de datos
Context initContext;
Connection cn=null;
String[] pub = null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();

DBPrivate conex = new DBPrivate(cn);
pub = conex.selectPub(prog, obl, prod);
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
<table class="appstyle" cellspacing="3" cellpadding="20" >
	<tr>
	<td class="rescab" align="center" nowrap> Bibcode </td>	
	</tr>
	
	<%for(int i = 0; i<pub.length; i++){ %>
	<tr>
	<td class="resfield" align="right">&nbsp;&nbsp;<a href="http://adsabs.harvard.edu/cgi-bin/nph-data_query?bibcode=<%=pub[i].replaceAll("&", "%26") %>&db_key=PRE&link_type=ABSTRACT"><%=pub[i] %></a>&nbsp;&nbsp;</td>
	</tr>
	<%} %>
</table>
<br>
<BR>

<%= pie %>