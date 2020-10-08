<%@ page import="svo.gtc.db.news.NewsAccess,
				 svo.gtc.db.news.NewsDb,
				 svo.gtc.web.ContenedorSesion,
				 
				 svo.gtc.web.Html,
				 
				 java.util.Vector,
				 java.sql.*,
				 
				 javax.sql.*,
				javax.naming.Context,
				javax.naming.InitialContext,
				javax.naming.NamingException" %>
                 
<%@ page isThreadSafe="true" %>
<%@ page info="OMC Archive Homepage" %>


<%-- P  R  O  C  E  S  O  S --%>
<% 

// *************************************************************
// *  ACTIVACION DE LA SESION Y VARIABLES DE SEGURIDAD         *
// *************************************************************
HttpSession sesion = request.getSession(true);
if(sesion.getAttribute("logged") == null){
	sesion.setAttribute("logged", "false");
}

//Abrimos la conexión a la base de datos
Context initContext;
Connection cn=null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();


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

	//---- Elementos estáticos de la página
	Html elementosHtml = new Html(request.getContextPath());
	String cabeceraPagina = elementosHtml.cabeceraPagina("Product Details","Data Product Details.");
	String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath()+"?"+request.getQueryString());
	String pie            = elementosHtml.pie();



// *************************************************************
// *  DEFINICION DE VARIABLES
// *************************************************************
String ACASA =  "http://";
       ACASA += request.getServerName();
       ACASA += ":";
       ACASA += request.getServerPort();


String MSG = "";


%>

<%--------     I N I C I O    P Á G I N A    H T M L    -------------------%>

<%= cabeceraPagina %>

<body>
<%= encabezamiento %>

<table BORDER="0" CELLSPACING="0" CELLPADDING="2" WIDTH="800px" >
<caption><!-- Introduction -->
<!-- Table of resources -->
<!-- Page footer --></caption>

<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER><font face="arial,helvetica,san-serif"><font
color="#FFFFFF"><font size=+2>&nbsp;The GTC Archive news history</font></font></font>
</td>
</tr>
<tr><td>&nbsp;</td></tr>
<%-- <tr><td><hr></td></tr>--%>

<%
NewsAccess newsAccess = new NewsAccess(cn);
NewsDb[] news = newsAccess.select();


%>

<table border="0" class="ayuda1" cellpadding="5">
  <tr valign="top"><td width="150px"><b>February 2018</b></td><td width="650px">GTC MOC (Multi Object Coverage) is now available.</td>
  <tr><td colspan=2 class="ayuda2"><img src="../images/news/moc.png" WIDTH="800px"></td></tr>
  
  <tr valign="top"><td width="100px"><b>July 2016</b></td><td>New columns included in the "Table of results": Object, RAJ2000 (hh:mm:ss.ss), DECJ2000 (dd:mm:ss.s), Exptime</td>
  <tr><td></td><td class="ayuda2"></td></tr>

 <tr valign="top"><td width="100px"><b>July 2016</b></td><td>HiPS (Hierarchical Progressive Surveys, http://aladin.u-strasbg.fr/hips/) method implemented in the GTC archive.</td>
  <tr><td colspan=2 class="ayuda2"><img src="../images/news/hips.png" WIDTH="800px"></td></tr>

<tr valign="top"><td width="100px"><b>July 2015</b></td><td>Information on the title of the proposal included in the "Table of results"</td>
  <tr><td></td><td class="ayuda2"></td></tr>

</table>
<%for(int i=0; i<news.length; i++){
	String news_desc = "";
	if(news[i].getNewsDesc() != null){
		news_desc = news[i].getNewsDesc();
	}
	news_desc = news_desc.replaceAll("\\n","<br>");
		
%>

<tr><td>
<!-- NEWS ITEM -->
<table border="0" class="ayuda1" cellpadding="5">
  <tr valign="top"><td width="150px"><b><%=news[i].getNewsDate() %></b></td><td  width="650px"><%=news[i].getNewsPhrase() %></td>
  <tr><td></td><td class="ayuda2"><%=news_desc %></td></tr>
</table>

<%-- <tr><td><hr></td></tr>--%>
<%	} // fin del for
%>
<br><br>
<%= pie %>
</body>
</html>
<% 
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
