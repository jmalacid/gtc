<%@page import="svo.sepub.html.webHTML" %>

<%
webHTML whtml = new webHTML(request.getContextPath());
%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%> 
 <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"> 
 <html> 
<%=whtml.encabezado() %>

<table border=0>
<tr><td><img src="<%=request.getContextPath() %>/images/sepub.gif"></td></tr>
</table>
<body>
<table style="width:800px">

<tr bgcolor="#3A50A0">
<td align="center"><font color="#FFFFFF" size=+2>SEarch of PUBlications</font>
</td>
</tr>
</table>
<br><br>

<form name="sp" action="sendSP.jsp" method="POST">
Aplicación que busca una serie de palabras en ADS, y devuelve los bibcodes encontrados.<br><br> La búsqueda se realiza en las siguientes publicaciones:
<ul>
<li>MNRAS</li>
<li>A&A</li>
<li>AJ</li>
<li>ApJ</li>
<li>ApJS</li>
<li>ApJL</li>
<li>PASP</li>
<li>Natur</li>
<li>Sci</li>
</ul>
Sobre las siguientes palabras:
<ul>
<li>GTC</li>
<li>Gran Telescopio Canarias</li>
<li>Gran Telescopio de Canarias</li>
</ul>
<table>
<tr>
<td>Seleccionar búsqueda:</td>
<td><input type="radio" name="bus" value="gtc" checked> GTC</td>

</tr>
<tr><td><br>Seleccionar acción:<br><br></td></tr>
<tr>
<td colspan="2"><input type="radio" name="act" value="0" checked>&nbsp;&nbsp;<font size=+1><b>Actualizar búsqueda de publicaciones</b></font><br><br></td>
</tr>

<tr>
<td colspan="2"><input type="radio" name="act" value="1">&nbsp;&nbsp;<font size=+1><b>Gestionar publicaciones</b></font></td>
</tr>

<tr>
<td colspan="2" align="center"><br><input type="submit" value="Aceptar"></td>
</tr>

</table>

</form>

<br>
<%= whtml.pie() %>
</body>
</html> 