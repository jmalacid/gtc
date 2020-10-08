<%@ page import="svo.gtc.web.Html,
	svo.gtc.utiles.Mail,
	svo.gtc.web.ContenedorSesion,
	svo.gtc.db.priv.DBPrivate,
	svo.gtc.priv.objetos.BibChange,
	java.sql.*,
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

<% 
String id = request.getParameter("id");
String pw = request.getParameter("pw");
String email = request.getParameter("email");

%>

<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px" >

<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER><font face="arial,helvetica,san-serif" color="#FFFFFF" size=+2>&nbsp;The Gran Telescopio CANARIAS Archive<br><font color=red>-Private zone-</font></font>
</td>
</tr>
</table><br>
<%
Context initContext;
Connection cn=null;

try{

	initContext = new InitialContext();
	DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
	cn = ds.getConnection();
	DBPrivate union = new DBPrivate(cn);
	
	union.updateUsuario(id, pw);

	String cuerpo1 = "Dear colleague,\n\nAs of December 2017, the GTC archive(http://gtc.sdc.cab.inta-csic.es/gtc/index.jsp) also includes private "
	+"data, only accessible by the PI of each program.\n\nAs Principal Investigator of a private programme, we are sending you the username/passwd required to access your data.\n\n"
	+"     Username: "+id+"\n\n     Password  --> you will receive it in another mail\n\n"
	+"To enter your private area, click Login (you can find it at the top right of the page). Once you have logged in, click \"Archive search and data retrieval\". "
	+"Then, click \"here\" (you can find it at the top of the page) to get the data associated to your GTC programmes. Instead, if you want to make a more general query" 
	+ "you can use the form available in this page.\n\n"
	+"Best regards,\nGTC Archive Support Team";
	

	Mail mail_user = new Mail(cuerpo1, "GTC Archive user", email);
	
	String cuerpo2 = "Dear colleague, \n\nFollowing my previous mail, here is your password '"+pw+"'.\n\nIf you wish, you can modify it in the follow link: 'https://gtc.sdc.cab.inta-csic.es/gtc/s/changePasswordInput.action'"
			+"\n\nBest regards,\nGTC Archive Support Team";
			

	Mail mail_pw = new Mail(cuerpo2, "GTC Archive private access", email);

	%>
	<jsp:forward page="pipend.jsp"/> 
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
</body>
</html>