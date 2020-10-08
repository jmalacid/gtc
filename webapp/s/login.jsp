<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="svo.gtc.web.Html, java.sql.Connection, svo.gtc.web.ContenedorSesion" %>
<%

//*************************************************************
//*  SESION                                                   *
//*************************************************************

ContenedorSesion contenedorSesion = null;
if(session!=null){ 
	contenedorSesion = (ContenedorSesion)session.getAttribute("contenedorSesion");
}

if(contenedorSesion==null) contenedorSesion=new ContenedorSesion();


//---- Elementos estáticos de la página
Html elementosHtml = new Html(request.getContextPath());
String cabeceraPagina = elementosHtml.cabeceraPagina("Search Results","Search results page for GTC Archive.");
String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath());
String pie            = elementosHtml.pie();
%>


<%--------     I N I C I O    P Á G I N A    H T M L    -------------------%>
<html>
<%=cabeceraPagina%>
<SCRIPT LANGUAGE="JavaScript" type="text/javascript" src="/gtc/js/wz_tooltip.js"></SCRIPT>
<body>
<%=encabezamiento%>
<br />
<%--   ********************************************************************************************************   --%>

<p class="appstyle"><s:actionerror /></p>

<s:form action="/s/login">
<table>
	<tr>
		<td><font class="appstyle">Username: </font></td><td><s:textfield name="loginBean.user" /></td>
	</tr><tr>
		<td><font class="appstyle">Password: </font></td><td><s:password name="loginBean.password" /></td>
	</tr>
</table>

 	  

   	  <s:submit/>
   	  
</s:form>	








<%--   ********************************************************************************************************   --%>
<br />

<%=pie %>
</body>
</html>
