<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="svo.gtc.web.Html, java.sql.Connection,svo.gtc.web.ContenedorSesion" %>
<%

ContenedorSesion contenedorSesion = null;
if(session!=null){ 
	contenedorSesion = (ContenedorSesion)session.getAttribute("contenedorSesion");
}

if(contenedorSesion==null) contenedorSesion=new ContenedorSesion();

//---- Elementos estáticos de la página
Html elementosHtml = new Html(request.getContextPath());
String cabeceraPagina = elementosHtml.cabeceraPagina("Change Password","Change your password.");
String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(),request.getServletPath());
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

<table width="800" cellspacing="0" cellpadding="2" border="0">
<tbody>
<tr>
<td class="headtitle">
<center>Register new user</center>
</td>
</tr>
</tbody>
</table>


<s:actionmessage cssClass="salto appstyle mensaje"/>
<s:actionerror cssClass="salto appstyle aviso"/>

<s:form action="/s/changePassword">

<p class="salto"></p>

<table CELLSPACING="5" WIDTH="640px" bgcolor="#EEEEEE">
	<tr>
		<td valign="top" width="100em">
			<p class="appstyle">User:</p>
		</td>
		<td>
			<p class="appstyle"><s:property value="newUser.id"/></p>
		</td>
	</tr>
	<tr>
		<td valign="top" width="100em">
			<p class="appstyle">New password:</p>
		</td>
		<td>
			<s:password name="newUser.password" size="20"></s:password>
		</td>
	</tr>
	<tr>
		<td valign="top" width="100em">
			<p class="appstyle">Confirm password:</p>
		</td>
		<td>
			<s:password name="newUser.passwordConf" size="20"></s:password>
		</td>
	</tr>
</table>


<p class="appstyle salto"><s:submit value="Update password" /></p>
</s:form>	

<%--   ********************************************************************************************************   --%>
<br />

<%=pie %>
</body>
</html>
