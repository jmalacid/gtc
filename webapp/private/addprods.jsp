<%@ page import="svo.gtc.web.Html,
	svo.gtc.web.ContenedorSesion,
	svo.gtc.priv.objetos.AddProd" %>
                 
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

<%

String submit = request.getParameter("submit");

///// COMPROBACI�N DE VALORES DEL FORMULARIO
String msg = "";
AddProd parametrosFormulario = null;
String stateant = "-";
try{
		stateant = request.getParameter("stateant");
		parametrosFormulario = new AddProd(request);
		parametrosFormulario.AddChange();
	}catch(Exception e){
		msg+=e.getMessage();
	}


if(msg.length()>0){
%>
	<p class="formbus1"><font color="red"><blink><%=msg %></blink></font></p>
<% 	
}else if(submit!=null && submit.length()>0){
%> 
	
<%	} %>

<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px" >

<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER><font face="arial,helvetica,san-serif"><font
color="#FFFFFF"><font size=+2>&nbsp;The Gran Telescopio CANARIAS Archive<br><font color=red>-Private zone-</font></font></font></font>
</td>
</tr>
</table>
<p class="formbus1"><font face="arial,helvetica,san-serif"><b> Resultado introducidos correctamente </b></font></p>
<p class="formbus1"><font face="arial,helvetica,san-serif"><b> <a href="publication.jsp?bib=<%=parametrosFormulario.getBibcode().replaceAll("&", "%26")%>&stateant=<%=stateant%>">Volver a la publicaci�n
</a> </b></font></p>
		
		
<%= pie %>
</body>
</html>