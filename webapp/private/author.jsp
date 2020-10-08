<%@page import="svo.gtc.web.Html,
				svo.gtc.utiles.Mail,
				java.sql.*,
				svo.gtc.db.priv.DBPrivate,
				svo.gtc.priv.objetos.Author,
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
<%
String submit = request.getParameter("submit");

///// COMPROBACI�N DE VALORES DEL FORMULARIO
String msg = "";
Author parametrosFormulario = null;
if(submit!=null && submit.length()>0){
	try{
		parametrosFormulario = new Author(request);
		parametrosFormulario.comprueba();
		parametrosFormulario.updateAut();
	}catch(Exception e){
		msg+=e.getMessage();
	}
}


if(msg.length()>0){
%>
	<p class="formbus1"><font color="red"><blink><%=msg %></blink></font></p>
<% 	
}else if(submit!=null && submit.length()>0){
%> 
	<jsp:forward page="controlState.jsp?bib=&state=-"/> 
<%	} %>


<br>
<%

//} 
	
Integer aut_id = Integer.valueOf(request.getParameter("aut"));
//Abrimos la conexi�n a la base de datos
Context initContext;
Connection cn=null;
Author aut = null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();
DBPrivate union = new DBPrivate(cn);

aut = union.infoAuthor(aut_id);

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

<form id="formulario" name="formulario" method="get"
	action="author.jsp" enctype="application/x-www-form-urlencoded"
	onSubmit=""><input type="hidden" value="<%=aut_id %>" name="aut" />
	
<table class="appstyle" cellspacing="3" cellpadding="20" >
	<tr>
	<td class="rescab" align="right" > Autor </td>	
	<td class="resfield" align="center"> <%=aut.getAut_name() %></td>
	<td class="resfield" align="left"> <input type="text" name="name" maxlength="50" size="30" /></td>	
	</tr>
	<tr>
	<td class="rescab" align="right" > email </td>	
	<td class="resfield" align="center"> <%=aut.getAut_email() %></td>	
	<td class="resfield" align="left"> <input type="text" name="email" maxlength="50" size="30" /> </td>
	</tr>
	<tr>
	<td class="rescab" align="right" > Detalles </td>	
	<td class="resfield" align="center"> <%=aut.getAut_details() %></td>
	<td class="resfield" align="left"> <input type="text" name="det" maxlength="100" size="40" /></td>	
	</tr>
	
</table><br><br>
<input type="submit" name="submit" value="Modificar Usuario" />&nbsp; &nbsp;<input
	type="reset" value="Reset" />&nbsp; &nbsp; <br>
</form>
<br>

	<p class="formbus1"><a href="controlState.jsp?bib=&state=-"> Volver </a></p>
	<br>
<BR>

<%= pie %>