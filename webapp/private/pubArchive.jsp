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
color="#FFFFFF"><font size=+2>&nbsp;The Gran Telescopio CANARIAS Archive<br><font color=red>-Private zone-</font></font></font></font>
</td>
</tr>
</table>

<br>
La búsqueda se realiza en las siguientes publicaciones:
<ul>
<li>MNRAS</li>
<li>A&A</li>
<li>AJ</li>
<li>ApJ</li>
<li>ApJS</li>
<li>ApJL</li>
<li>PASP</li>
<li>Natur</li>
<li>Sci</li>
</ul>
Sobre las siguientes palabras:
<ul>
<li>GTC Public Archive</li>
<li>GTC Archive</li>
<li>Gran Telescopio Canarias Public Archive</li>
<li>Gran Telescopio Canarias Archive</li>
<br>
<%

//} 
//Abrimos la conexión a la base de datos
Context initContext;
Connection cn=null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();

DBPrivate union = new DBPrivate(cn);

String[] fichs = union.pubArchive(); 
%>

<table class="appstyle" cellspacing="3" cellpadding="20" >
	<tr>
	<td class="rescab" align="center" nowrap> Bibcode </td>			
	<td class="rescab" align="center" nowrap> Detalles</td>	
	</tr>
	
	<%for(int i=0; i<fichs.length;i++){ %>
	<tr>
	<td class="resfield" align="right">&nbsp;&nbsp;<a href="http://adsabs.harvard.edu/cgi-bin/nph-data_query?bibcode=<%=fichs[i].split(".-.")[0].replaceAll("&", "%26") %>&db_key=PRE&link_type=ABSTRACT"><%= fichs[i].split(".-.")[0] %></a>&nbsp;&nbsp;</td>
	
	<%if(fichs[i].split(".-.")[1]!=null){ %>
		<td class="resfield" align="right">&nbsp;&nbsp;<%= fichs[i].split(".-.")[1] %>&nbsp;&nbsp;</td>
	<%}else{ %>
		<td class="resfield" align="right">&nbsp;&nbsp;-&nbsp;&nbsp;</td>
	<%} %>
	</tr>
	<%} %>
</table>
<br>

<br>
	<p class="formbus1"><a href="private.jsp"> Volver </a></p>
	<br>
<BR>
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
<%= pie %>