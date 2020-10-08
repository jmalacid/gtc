<%@page import="svo.gtc.web.Html,
				nom.tam.fits.BasicHDU,
				nom.tam.fits.Fits,
				nom.tam.fits.HeaderCard,
				nom.tam.util.Cursor,
				java.io.FileInputStream,
				java.sql.Connection,
				svo.gtc.web.ContenedorSesion;" %>

<% 
ContenedorSesion contenedorSesion = null;
if(session!=null && session.getAttribute("contenedorSesion")!=null){
	contenedorSesion = (ContenedorSesion)session.getAttribute("contenedorSesion");
}else{
	contenedorSesion = new ContenedorSesion();
	session.setAttribute("contenedorSesion", contenedorSesion);
}
	//---- Elementos estáticos de la página
	Html elementosHtml = new Html(request.getContextPath());
	String cabeceraPagina = elementosHtml.cabeceraPagina("GTC","Private zone");
	String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath()+"?"+request.getQueryString());
	String pie            = elementosHtml.pie();
%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%> 
 <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"> 
 <html> 
<%=cabeceraPagina %>
<%=encabezamiento %>

<body>
<table style="width:800px">

<tr bgcolor="#3A50A0">
<td align="center"><font color="#FFFFFF" size=+2>Header</font>
</td>
</tr>
</table>
<br>

<%
//Obtenemos el id del producto
//String id = request.getParameter("id");

//Obtenemos el path y el nombre del producto
String path = request.getParameter("id").trim();

try{
	
	FileInputStream fin = new FileInputStream(path);
	//GZIPInputStream gzis = new GZIPInputStream(fin);
	String salida = "";
    
    Fits f;
    
    try{
    	
    	BasicHDU[] hdus =  new BasicHDU[0];
    	f= new Fits(fin);
    
    	hdus = f.read();

    	for(int i=0; i<hdus.length; i++){
    		BasicHDU h = hdus[0];      
    
    		if( h != null) {
    			nom.tam.fits.Header hdr = h.getHeader();     
    			Cursor iter = hdr.iterator();
    			while(iter.hasNext()) {
    				HeaderCard hc = (HeaderCard) iter.next();
    				salida += hc.toString()+"\n";
    			}
    			salida+="\n";
    		}
    	}
    }catch (Exception e){
    	try{
    		BasicHDU hdus = null;
    	
    		f = new Fits(fin);
    		hdus = f.readHDU();
    		
    		salida += "Header listing for HDU :\n";
    		nom.tam.fits.Header hdr = hdus.getHeader();		
    		Cursor iter = hdr.iterator();
    		while(iter.hasNext()) {
    			HeaderCard hc = (HeaderCard) iter.next();
    			salida += hc.toString()+"\n";
    		}
    		salida+="\n";
    	}catch(Exception e1){
    		e.printStackTrace();
    	}
    }

	%>
	<%=salida.replaceAll("\n","<br>") %>

	
<%	
}catch(Exception e){
	 %>	
	Se ha producido un error
<% e.printStackTrace();

} %>

<br>

<%= pie %>
</body>
</html> 