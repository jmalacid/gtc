<%@page import="svo.sepub.html.webHTML" %>

<%
webHTML whtml = new webHTML(request.getContextPath());
%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%=whtml.encabezado() %>
<body>
<table border=0>
<tr><td><img src="../images/sepub.gif"></td></tr>
</table>

<table style="width:800px">

<tr bgcolor="#3A50A0">
<td align="center"><font color="#FFFFFF" size=+2>SEarch of PUBlications</font>
</td>
</tr>
</table>
<br><br>

<%
String bus = request.getParameter("bus");
String act = request.getParameter("act");

if(act.equals("0")){
	%><jsp:forward page="resultSP.jsp"/><%
}else if(act.equals("1")){
	%><jsp:forward page="verSP.jsp"/><%
}else{
	%>Se ha producido un error, no hay ninguna opción señalada<%
}
%>



<br>

<%= whtml.pie() %>
</body>
</html>