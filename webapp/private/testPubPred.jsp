<%@ page import="svo.gtc.web.Html,
	svo.gtc.web.ContenedorSesion,
	svo.gtc.db.priv.DBPrivate,
	svo.gtc.priv.objetos.PredNoPub,
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

PredNoPub[] progs = union.countPredNoPub();



if(progs.length>0){
%>
<table class="appstyle" cellspacing="3" cellpadding="20" >
	<tr><th class="rescab" colspan="7">Se han encontrado <%=progs.length %> productos reducidos que no tienen asociada una publicación</th></tr>
	<tr>
	<td class="rescab" align="center" nowrap> Programa </td>	
	<td class="rescab" align="center" nowrap> OB</td>
	<td class="rescab" align="center" nowrap> Producto</td>
	<td class="rescab" align="center" nowrap> Bibcode</td>
	</tr>
<%	
for(int i=0; i<progs.length;i++){
	%>
	<tr>
	<td class="resfield"><%=progs[i].getProg_id() %></td>
	<td class="resfield"><%=progs[i].getObl_id()%></td>
	<td class="resfield"><%=progs[i].getProd_id() %></td>
	<td class="resfield"><%=progs[i].getBibcode() %></td>
	</tr>
	<%
}
%>
</table>

<%
}else{
%>	<p>No hay ninguna publicación que </p>
<%}
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
<br>
<a href="private.jsp"><b>Volver</b></a>
<br>

<%= pie %>
</body>
</html>