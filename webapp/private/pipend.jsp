<%@ page import="svo.gtc.web.Html,
	svo.gtc.web.ContenedorSesion,
	svo.gtc.db.usuario.UsuarioDb,
	javax.sql.*,
	java.sql.*,
	svo.gtc.db.priv.DBPrivate,
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


<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px" >

<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER><font face="arial,helvetica,san-serif"><font
color="#FFFFFF"><font size=+2>&nbsp;The Gran Telescopio CANARIAS Archive<br><font color=red>-Private zone-</font></font></font></font>
</td>
</tr>
</table>
<%//Abrimos la conexión a la base de datos
Context initContext;
Connection cn=null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();
DBPrivate union = new DBPrivate(cn);

UsuarioDb[] pis = union.infoPI();



%>
<p><b>Usuarios con datos privados a los que todavía no hemos mandado el usuario/pw:</b></p>
<br>
<table class="appstyle" cellspacing="3" cellpadding="20" >
<tr>
<td class="rescab" align="center" nowrap> User_id </td>	
<td class="rescab" align="center" nowrap> Nombre</td>
<td class="rescab" align="center" nowrap> Apellidos</td>
<td class="rescab" align="center" nowrap> Mandar mail</td>
</tr>
<%
for(int i=0; i<pis.length;i++){
	%>
	<tr>
	<td class="resfield" align="left" ><%= pis[i].getUsrId() %></td>
	<td class="resfield" align="left"><%= pis[i].getUsrName() %></td>
	<td class="resfield" align="left"><%=pis[i].getUsrSurname()%></td>
	<td class="resfield" align="left"><a href="pipend_send.jsp?id=<%=pis[i].getUsrId()%>">Mandar</a></td>
<%} %>
</table>
<br>
<b><a href="private.jsp">Volver</a></b>

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