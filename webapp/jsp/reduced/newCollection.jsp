<%@ page import="svo.gtc.web.ContenedorSesion,
				svo.gtc.web.Form,
				svo.gtc.web.Html,
				
				java.sql.*" %>
                 
<%@ page isThreadSafe="true" %>
<%@ page info="GTC Reducted Data" %>


<%-- P  R  O  C  E  S  O  S --%>
<%

//*************************************************************
//*  SESION                                                   *
//*************************************************************

ContenedorSesion contenedorSesion = null;
if(session!=null && session.getAttribute("contenedorSesion")!=null){
	contenedorSesion = (ContenedorSesion)session.getAttribute("contenedorSesion");
}else{
	contenedorSesion = new ContenedorSesion();
}

if(contenedorSesion.getUser()==null){
	request.setAttribute("origen","/jsp/reduced/insertReduced.jsp");
	getServletContext().getRequestDispatcher("/secure/login.jsp").forward(request,response);
}else if(1==0){
	request.setAttribute("mensaje", "Sorry, not sufficient privileges to enter reduced products.");
	request.setAttribute("origen","/jsp/reduced/insertReduced.jsp");
	getServletContext().getRequestDispatcher("/jsp/error.jsp").forward(request,response);
}

//---- Elementos est�ticos de la p�gina
Html elementosHtml = new Html(request.getContextPath());
String cabeceraPagina = elementosHtml.cabeceraPagina("Insert reduced data","Create new collection.");
String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath());
String pie            = elementosHtml.pie();


%>

<%--------     I N I C I O    P � G I N A    H T M L    -------------------%>
<html>
<%= cabeceraPagina %>

<body>
<%= encabezamiento %>
<table width="800" cellspacing="0" cellpadding="2" border="0">
<tbody>
<tr>
<td class="headtitle" align="center">
GTC Archive: Create new data collection
</td>
</tr>
</tbody>
</table>

<p class="salto"></p>
<form name="insertCollection" method="post" action="<%=request.getContextPath() %>/servlet/Controlador">

<table class="appstyle" border="0" CELLSPACING="5" WIDTH="640em" bgcolor="#EEEEEE">
	<tr>
		<td width="150em">
			Data collection name:
		</td>
		<td>
			<input type="text" name="col_name" value="" size="20">
		</td>
	</tr>
	<tr>
		<td>
			Description:
		</td>
		<td rowspan="3" valign="center">
			<textarea name="col_desc" rows="6" cols="20"></textarea>
		</td>
	</tr>
</table>


<p class="appstyle salto"><input type="submit" name="submitInsertCol" value="Create collection"></p>
</form>

<%= pie %>
</body>
</html>

