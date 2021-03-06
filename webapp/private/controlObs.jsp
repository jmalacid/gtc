<%@page import="svo.gtc.web.Html,
				svo.gtc.utiles.Mail,
				java.sql.*,
				svo.gtc.db.priv.DBPrivate,
				svo.gtc.priv.objetos.ObsInfo,
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

	//---- Elementos est�ticos de la p�gina
	Html elementosHtml = new Html(request.getContextPath());
	String cabeceraPagina = elementosHtml.cabeceraPagina("GTC","Private zone");
	String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath()+"?"+request.getQueryString());
	String pie            = elementosHtml.pie();
%>

<script>

function seleccionar_todo(){
if(document.formulario.elements[0].checked==1){
	for (i=1;i<document.formulario.elements.length;i++)
		if(document.formulario.elements[i].type == "checkbox")
			document.formulario.elements[i].checked=1
			
}if (document.formulario.elements[0].checked==0){
	for (i=1;i<document.formulario.elements.length;i++)
		if(document.formulario.elements[i].type == "checkbox")	
			document.formulario.elements[i].checked=0
			
}
}

</script>

<%--------     I N I C I O    P � G I N A    H T M L    -------------------%>

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
//String submit = request.getParameter("submit");

//if(submit!=null && submit.length()>0){
	//Aqu� mandamos el email y redirigimos a otra p�gina
	//Mail prueba = new Mail(request.getQueryString());
	%> 
	
<%

//} 
	
String prog = request.getParameter("prog");
String err = request.getParameter("err");
String order = request.getParameter("order");

//Abrimos la conexi�n a la base de datos
Context initContext;
Connection cn=null;
ObsInfo[] fichs = null; 

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();
DBPrivate union = new DBPrivate(cn);

fichs = union.obsInfo(prog, err, order); 
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

<form id="formulario" name="formulario" method="post"
	action="controlrev.jsp" enctype="application/x-www-form-urlencoded" onSubmit="">

<table class="appstyle" cellspacing="3" cellpadding="20" >
	<tr>
	<td class="rescab" align="center" nowrap> Program </td>	
	<td class="rescab" align="center" nowrap> observing block</td>	
	<td  colspan="2" class="rescab" align="center" nowrap> Fichero </td>	
	<td class="rescab" align="center" nowrap> Error ID</td>	
	<td class="rescab" align="center" nowrap> Error </td>
	<td class="rescab" align="center" nowrap> Revisado<br><input type="checkbox" name="markAll"  onclick="javascript:seleccionar_todo()"> </td>
	</tr>
	
	<%for(int i=0; i<fichs.length;i++){ 
	String prod_id = fichs[i].getProgram()+".."+fichs[i].getBlock()+".."+ fichs[i].getProde_id();
	%>
	<tr>
	<td class="resfield" align="right">&nbsp;&nbsp;<%= fichs[i].getProgram() %>&nbsp;&nbsp;</td>
	<td class="resfield" align="right">&nbsp;&nbsp;<%= fichs[i].getBlock() %>&nbsp;&nbsp;</td>
	<td class="resfield" align="right">&nbsp;&nbsp;<a href="<%=request.getContextPath() %>/servlet/FetchProd?prod_id=<%=prod_id%>"><%= fichs[i].getFilename() %></a>&nbsp;&nbsp;</td>
	<td class="resfield" align="right">&nbsp;&nbsp;<a target="_blank" href="../jsp/viewprodheader.jsp?prod_id=<%=prod_id%>">Header</a>&nbsp;&nbsp;</td>
	<td class="resfield" align="right">&nbsp;&nbsp;<%= fichs[i].getErr_id() %>&nbsp;&nbsp;</td>
	<td class="resfield" align="right">&nbsp;&nbsp;<%= fichs[i].getError() %>&nbsp;&nbsp;</td>
	<%if(fichs[i].getRev()==1){ %>
		<td class="resfield" align="center"> S�</td>
	<%}else{ %>
		<td class="resfield" align="center"> <input type="checkbox" name="prod_id" value="<%=fichs[i].getProgram()%>-.-<%= fichs[i].getBlock() %>-.-<%= fichs[i].getProde_id() %>-.-<%= fichs[i].getErr_id() %>"/></td>
	<%} %>
	</tr>
	<%} %>
</table>
<br>
<input type="submit" name="submit" value="Submit Query" />&nbsp; &nbsp;<input
	type="reset" value="Reset Form" />&nbsp; &nbsp; <br>
	
</form>

<br>
	<p class="formbus1"><a href="errors.jsp"> Volver al control de errores </a></p>
	<br>
<BR>

<%= pie %>