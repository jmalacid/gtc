<%@ page import="svo.gtc.web.Html,
				 svo.gtc.web.ContenedorSesion,
				 
				 java.util.TimeZone,
                 java.util.Locale,
                 java.util.Enumeration" %>
                 
<%@ page isThreadSafe="true" %>
<%@ page info="GTC login" %>


<%-- P  R  O  C  E  S  O  S --%>
<%	


//*************************************************************
//*  SESION                                                   *
//*************************************************************

ContenedorSesion contenedorSesion = null;
if(session!=null && session.getAttribute("contenedorSesion")!=null){
	contenedorSesion = (ContenedorSesion)session.getAttribute("contenedorSesion");
}else{
	contenedorSesion = new ContenedorSesion();
}


	
// ***************************************************************
// *** PROCESADO DEL FORMULARIO
// ***************************************************************

//String origen = request.getParameter("origen");
//if(origen==null){
	String origen="/index.jsp";
//}


String port="";
if(request.getServerPort()>1000){
	port=":8080";
}
origen="http://"+request.getServerName()+port+request.getContextPath()+origen;

contenedorSesion.setUser(null);
session.setAttribute("contenedorSesion",contenedorSesion);

response.sendRedirect(origen);
%>