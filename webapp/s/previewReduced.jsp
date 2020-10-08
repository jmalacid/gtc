<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="svo.gtc.web.Html,java.sql.Connection, svo.gtc.web.ContenedorSesion" %>
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
String cabeceraPagina = elementosHtml.cabeceraPagina("Upload Reduced Data","Reduced Data Validation.");
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

<table width="800" cellspacing="0" cellpadding="2" border="0">
<tbody>
<tr>
<td class="headtitle">
<center>GTC Archive: Reduced data upload</center>
</td>
</tr>
</tbody>
</table>


<%--   ********************************************************************************************************   --%>
<%
	boolean hayGreen=false;
	
%>
<s:actionmessage cssClass="salto appstyle mensaje"/>
<s:actionerror cssClass="salto appstyle aviso"/>

<s:if test="%{previewReduced.notIngested>0}">
<p class="salto">
<table class="appstyle" border="0" CELLSPACING="5" WIDTH="640em">
	<tr>
		<td class="rescab">File</td>
		<td class="rescab">Status</td>
		<td class="rescab">Desc</td>
	</tr>

<s:iterator value="previewReduced.prodStatus">
	<s:if test="%{status!='OK'}">
	<tr>
		<td class="resfield"><s:property value="fileName"/></td>
		<td class="resfield" style="background-color: <s:property value="webColor"/>"><s:property value="status"/></td>
		<td class="resfield"><s:property value="desc"/></td>
	</tr>
	</s:if>
</s:iterator>

</table>
</s:if>

<p class="salto" />
<s:form action="">
<s:submit action="insertReducedInput" value="Back" />
</s:form>

<p class="salto" style="font-style:italic;">A detailed step-by-step guide on how to upload reduced data can be found <a href="<%=request.getContextPath()%>/help/overview.jsp#6">here</a>.</p>

<%--   ********************************************************************************************************   --%>
<br />

<%=pie %>
</body>
</html>
