<%@page import="java.io.*, 	
				java.util.*,
				java.sql.*,
				java.net.URLEncoder,
				svo.gtc.utiles.Descargar,
				org.apache.commons.lang.StringEscapeUtils" %>

<%
response.setContentType ("text/plain");
response.setHeader("Content-Disposition", "inline;filename=aladin.ajs");
//creamos el contenido del fichero
String texto = ""; 

//Valores de la sesión
String serverName= request.getServerName();
int port = request.getServerPort();
String path = request.getContextPath();


texto = texto + "#cargar el fits\n";
//Cargamos el fichero de estrellas
texto = texto + "load http://"+serverName+":"+port+path+"/moc/gtcMOC.fits?.xml\n";

//texto = texto + "load http://caha.sdc.cab.inta-csic.es/calto/jsp/descargaSci.jsp?id=16663&tipe=raw&t=web &.xml\n";

Descargar.descargar(response.getOutputStream(), texto);

%>