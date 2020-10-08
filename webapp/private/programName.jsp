<%@ page import="svo.gtc.web.Html,
	svo.gtc.web.ContenedorSesion" %>
                 
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


<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px" >

<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER><font face="arial,helvetica,san-serif"><font
color="#FFFFFF"><font size=+2>&nbsp;The Gran Telescopio CANARIAS Archive<br><font color=red>-Private zone-</font></font></font></font>
</td>
</tr>
</table>

<form id="formulario" name="formulario" method="post"
	action="programUpdate.jsp" enctype="multipart/form-data" onSubmit="">

<br>
	<p class="formbus1"><font face="arial,helvetica,san-serif"><b>
		* Los datos deben de estar en formato csv separado por ","<br>Comprobar que cada programa est� en una sola l�nea y que los t�tulos no contienen ,</b></font>
	</p>
	


	<p class="formbus1"><font face="arial,helvetica,san-serif"><b>Subir fichero:</b></font></p>
	<input class="gform" type="file" name="fich" value=""
		size="18"> <br>
	
	<br> <input type="submit" name="submit" value="Aceptar" />&nbsp;
	&nbsp;<input type="reset" value="Borrar Formulario" />&nbsp; &nbsp; <br>
	<br>

</form>	

<a href="private.jsp">Volver</a>


<%= pie %>
</body>
</html>