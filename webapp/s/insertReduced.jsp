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
String cabeceraPagina = elementosHtml.cabeceraPagina("Upload Reduced Data","Upload Reduced Data");
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
<center>GTC Archive: Reduced data upload</center>
</td>
</tr>
</tbody>
</table>


<s:actionerror cssClass="salto appstyle aviso"/>

<p class="salto" />


<table class="appstyle" border="0" CELLSPACING="5" WIDTH="640em" bgcolor="#EEEEEE">
	<tr>
		<td width="300">
			Data collection:
		</td>
		</tr>
	<tr>
		<td width="300">
		<s:form action="/s/newCollectionInput"  method="post">
			<s:submit value="Add New Collection"/>
		</s:form>
		</td>
	</tr>
</table>
<s:form action="/s/insertReduced"  method="post" enctype="multipart/form-data">
<table class="appstyle" border="0" CELLSPACING="5" WIDTH="640em" bgcolor="#EEEEEE">
	<tr>
	<td width="85">Select:</td>
		<td width="200">
		
			<s:select key="insertReducedBean.dataCollection" list="dataCollections" listKey="id" listValue="name" />
		
		</td>
		
	</tr>
</table>

 	  
<table class="appstyle" border="0" CELLSPACING="5" WIDTH="640em">
	<tr>
		<td width="300em">
			File (individual FITS or compress file containing several FITS files in zip format or tar.gz format):
		</td>
	</tr>
	<tr>
		<td>
			<s:file name="insertReducedBean.upload" />
		</td>
	</tr>
</table>

<p class="appstyle salto"><s:submit value="Upload" /></p>
   	  
   	  
</s:form>	


<p class="salto" style="font-style:italic;">A detailed step-by-step guide on how to upload reduced data can be found <a href="<%=request.getContextPath()%>/help/overview.jsp#6">here</a>.</p>


<%--   ********************************************************************************************************   --%>
<br />

<%=pie %>
</body>
</html>
