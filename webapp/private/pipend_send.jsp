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

	String id = request.getParameter("id");

	UsuarioDb pi = union.infoPI(id);

%>
<form id="formMail" name="formMail" method="post"
	action="mailIP.jsp" enctype="application/x-www-form-urlencoded" onSubmit=""><input type="hidden" value="<%= pi.getUsrId() %>" name="id" /><input type="hidden" value="<%=pi.getUsrEmail() %>" name="email" />

<table class="appstyle" cellspacing="3" cellpadding="20" >
<tr>
<td class="rescab" align="center" nowrap> User_id </td>	
<td class="resfield" align="center" nowrap> <%= pi.getUsrId() %></td>
</tr>
<tr>
<td class="rescab" align="center" nowrap> Name </td>	
<td class="resfield" align="center" nowrap> <%= pi.getUsrName() %></td>
</tr>
<tr>
<td class="rescab" align="center" nowrap> Surname </td>	
<td class="resfield" align="center" nowrap> <%= pi.getUsrSurname() %></td>
</tr>
<tr>
<td class="rescab" align="center" nowrap> Email </td>	
<td class="resfield" align="center" nowrap> <%= pi.getUsrEmail() %></td>
</tr>
<tr>
<td class="rescab" align="center" nowrap> New Password </td>	
<td class="resfield" align="center" nowrap> <input type="text" name="pw" maxlength="20" size="20" /></td>
</tr>

</table>
<br>
<input type="submit" name="submit" value="Mandar mail" />&nbsp; &nbsp;<input
	type="reset" value="Reset Form" />
<br><br>
<a href="pipend.jsp"><b>Back</b></a>
</form>
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