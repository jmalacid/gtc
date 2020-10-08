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

<table width="800" cellspacing="0" cellpadding="2" border="0">
<tbody>
<tr>
<td class="headtitle">
<center>GTC Archive: New data collection</center>
</td>
</tr>
</tbody>
</table>


<s:actionerror cssClass="salto appstyle aviso"/>

<p class="salto" />

<s:form action="/s/newCollection">

<table class="appstyle" border="0" CELLSPACING="5" WIDTH="640em" bgcolor="#EEEEEE">
	<tr>
		<td width="150em">
			Data collection name: <font class="aviso">*</font>
		</td>
		<td>
			<s:textfield name="newCollectionBean.name" size="20" />
		</td>
	</tr>
	<tr>
		<td valign="top">
			Data collection description:
		</td>
		<td valign="center">
			<s:textarea name="newCollectionBean.desc" rows="6" cols="20" />
		</td>
	</tr>
	<tr>
		<td width="150em">
			Publication bibcode: <font class="aviso">*</font>
		</td>
		<td>
			<s:textfield name="newCollectionBean.bibcode" size="20" />
		</td>
	</tr>
	<tr>
		<td width="150em">
			Reduction type: <font class="aviso">*</font>
		</td>
		<td>
		</td>
	</tr>
	<tr>
		<td width="150em" style="text-align:right">
			Image: 
		</td>
		<td style="font-weight:normal">
			<s:checkboxlist list="redTypesImg" name="newCollectionBean.redTypeImg" onclick="newcol_update_form(document.newCollection, this)" />
		</td>
	</tr>
	<tr>
		<td width="150em" style="text-align:right">
		</td>
		<td style="font-weight:normal">
			Other: <s:textfield name="newCollectionBean.redTypeImgOther" size="20" onfocus="newcol_update_form(document.newCollection, this)" />
		</td>
	</tr>
	<tr>
		<td width="150em" style="text-align:right">
			Spectrum:
		</td>
		<td style="font-weight:normal">
			<s:checkboxlist list="redTypesSpec" name="newCollectionBean.redTypeSpec" onclick="newcol_update_form(document.newCollection, this)" />
		</td>
	</tr>
	<tr>
		<td width="150em" style="text-align:right">
		</td>
		<td style="font-weight:normal">
			Other: <s:textfield name="newCollectionBean.redTypeSpecOther" size="20" onfocus="newcol_update_form(document.newCollection, this)" />
		</td>
	</tr>
</table>


<p class="appstyle salto"><s:submit value="Create collection" /></p>
</s:form>	

<p class="salto" style="font-style:italic;">A detailed step-by-step guide on how to upload reduced data can be found <a href="<%=request.getContextPath()%>/help/overview.jsp#6">here</a>.</p>


<%--   ********************************************************************************************************   --%>
<br />

<%=pie %>
</body>
</html>
