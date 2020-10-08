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
<%

//} 
String url = "?"+request.getQueryString();
String bib = request.getParameter("bib");
String state = request.getParameter("state");
String order = request.getParameter("order");

//Abrimos la conexión a la base de datos
Context initContext;
Connection cn=null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();

DBPrivate union = new DBPrivate(cn);

Bibcodes[] fichs = union.BibInfo(bib, state, order); 

//Info
Integer countBib = union.countBib();
String[] infoEstado = union.infoEstado();
String[] pubsNo = union.selectpubsNo();


%>

<table class="appstyle" cellspacing="3" cellpadding="20" >
<tr>
	<td class="rescab" align="center" nowrap> Número total de bibcodes: </td>	
	<td class="resfield" align="center" nowrap> &nbsp;&nbsp;<%=countBib %>&nbsp;&nbsp; </td>
</tr>
<tr>
	<td colspan="2" class="rescab" align="center" nowrap> Número de estados: </td>
</tr>
<%for(int i=0; i<infoEstado.length;i++){ %>
<tr>
	<td class="rescab" align="center" nowrap> <%=infoEstado[i].split("-.-")[0] %>:</td>	
	<td class="resfield" align="center" nowrap> &nbsp;&nbsp;<%=infoEstado[i].split("-.-")[1] %>&nbsp;&nbsp; </td>
</tr>
<%} %>

</table>
<br>
<p class="formbus1"><a href="reduc.jsp"> Volver </a></p>
<br>
<table class="appstyle" cellspacing="3" cellpadding="20" >
	<tr>
	<td colspan="2" class="rescab" align="center" nowrap> Bibcode </td>	
	<td colspan="2" class="rescab" align="center" nowrap> Autor</td>	
	<td colspan="2" class="rescab" align="center" nowrap> Estado final </td>	
	<td rowspan="2" class="rescab" align="center" nowrap> Detalles</td>	
	<td rowspan="2" class="rescab" align="center" nowrap> Último comentario</td>
	</tr>
	
	<tr>
	<td nowrap class="rescab" style="cursor:pointer"> <a href="controlState.jsp<%=url.replaceAll("order="+order,"order=pub_bibcode") %>"><font color="white"><b>&uarr;</b></font></a> </td>
	<td nowrap class="rescab" style="cursor:pointer"> <a href="controlState.jsp<%=url.replaceAll("order="+order,"order=pub_bibcode_desc") %>"><font color="white"><b>&darr;</b></font></a> </td>
	<td nowrap class="rescab" style="cursor:pointer"> <a href="controlState.jsp<%=url.replaceAll("order="+order,"order=author.aut_name") %>"><font color="white"><b>&uarr;</b></font></a> </td>
	<td nowrap class="rescab" style="cursor:pointer"> <a href="controlState.jsp<%=url.replaceAll("order="+order,"order=author.aut_name_desc") %>"><font color="white"><b>&darr;</b></font></a> </td>
	<td nowrap class="rescab" style="cursor:pointer"> <a href="controlState.jsp<%=url.replaceAll("order="+order,"order=publication.fin_id") %>"><font color="white"><b>&uarr;</b></font></a> </td>
	<td nowrap class="rescab" style="cursor:pointer"> <a href="controlState.jsp<%=url.replaceAll("order="+order,"order=publication.fin_id_desc") %>"><font color="white"><b>&darr;</b></font></a> </td>
	
	</tr>
	
	<%for(int i=0; i<fichs.length;i++){ 
		String com = union.selectLastCom(fichs[i].getBibcode());%>
	<tr>
	<td colspan="2" class="resfield" align="right">&nbsp;&nbsp;<%= fichs[i].getBibcode() %>&nbsp;&nbsp;</td>
	<td colspan="2" class="resfield" align="right">&nbsp;&nbsp;<a href="author.jsp?aut=<%= fichs[i].getAutor_id() %>"><%= fichs[i].getAutor() %></a>&nbsp;&nbsp;</td>
	<td colspan="2" class="resfield" align="right">&nbsp;&nbsp;<%= fichs[i].getEstado_desc() %>&nbsp;&nbsp;</td>
	<td class="resfield" align="right">&nbsp;&nbsp;<a href="publication.jsp?bib=<%= fichs[i].getBibcode().replaceAll("&", "%26")%>&stateant=<%=state%>">Ver</a>&nbsp;&nbsp;</td>
	<%if(com!=null && com.length()>0){ %>
		<td class="resfield" align="left">&nbsp;&nbsp;<%=com %>&nbsp;&nbsp;</td>
	<%}else{ %>
		<td class="resfield" align="left">&nbsp;&nbsp;N/A&nbsp;&nbsp;</td>
	<%} %>
	</tr>
	<%} %>
</table>
<br>

<table class="appstyle" cellspacing="3" cellpadding="20">
<tr>
	<td class="rescab" align="center" nowrap> Productos con ficheros reducidos sin publicación </td>
	<td class="rescab" align="center" nowrap> Bibcode </td>
<tr>
	<%for(int j=0; j<pubsNo.length;j++){ %>
	<tr>
		<td class="resfield" align="right">&nbsp;&nbsp;<%= pubsNo[j].split(".--.")[0] %>&nbsp;&nbsp;</td>
		<td class="resfield" align="right">&nbsp;&nbsp;<%= pubsNo[j].split(".--.")[1] %>&nbsp;&nbsp;</td>
	</tr>
	<%} %>
		
</table>

<br>
	<p class="formbus1"><a href="reduc.jsp"> Volver </a></p>
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