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
<br>

<form name="form_stats" method="POST" action="resultSP.jsp">
<p class="appstyle" style="font-size:12pt;">Hasta solucionar el problema json/java/curl, habría que incluir en este recuadro el resultado de aplicar: <br>
curl -H 'Authorization: Bearer:WG8qpt5jwF4x1zX0LE4FCSbQjh7QOYz5TUHk7fHg' 
'http://api.adsabs.harvard.edu/v1/search/query?q=full%3A%22gran+telescopio+canarias%22or%22gran+telescopio+de+canarias%22or%22gtc%22&database=astronomy&sort=date+desc&rows=1000&fl=bibcode'</p>
<br>
Nota: Usar "start" y "rows" para paginaciones
<table border="0" CELLSPACING="5" WIDTH="640px" bgcolor="#f0f0f0">
<tr><td><textarea name="result" rows="10" cols="100"></textarea></td></tr>
</table>
<input type="submit" name="submit" value="Submit">
</form>

<br>
<p class="link"><a href="indexSepub.jsp">Ir al Menú</a></p>
<br>
<%= whtml.pie() %>
</body>
</html>