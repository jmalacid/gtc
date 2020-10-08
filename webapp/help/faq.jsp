<%@ page import="svo.gtc.web.Html,
				 svo.gtc.web.ContenedorSesion,
				 svo.gtc.db.DriverBD,

				 java.net.URLEncoder,
                 java.util.Locale,
                 java.util.Vector,
                 java.util.Enumeration, 
                 
                 java.sql.*" %>
                 
<%@ page isThreadSafe="true" %>
<%@ page info="GTC Scientific Archive Homepage" %>


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
	session.setAttribute("contenedorSesion", contenedorSesion);
}

//*************************************************
//*  CABECERAS                                    *
//*************************************************

//---- Elementos estáticos de la página
Html elementosHtml = new Html(request.getContextPath());
String cabeceraPagina = elementosHtml.cabeceraPagina("FAQ","Frequently Asked Questions");
String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath()+"?"+request.getQueryString());
String pie            = elementosHtml.pie();

%>

<%--------     I N I C I O    P Á G I N A    H T M L    -------------------%>

<%= cabeceraPagina %>

<body>
<%= encabezamiento %>


<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px" >
<caption><!-- Introduction -->
<!-- Table of resources -->
<!-- Page footer --></caption>
<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER><font face="arial,helvetica,san-serif"><font
color="#FFFFFF"><font size=+2>&nbsp;GTC Archive - Frequently Asked Questions</font></font></font>
</td>
</tr>
</table>

		<h3>Search</h3>
		 
		 <h4>Do I need user registration to make a query to the archive?</h4>
		 <p>No. User identification is only needed to upload reduced data.<p>
		 
		 <h4>I want to search recent data.</h4>
		 <p>The GTC Archive contains public data only. GTC data become public one year after the observing date.<p>
		 
		 <h4>I cannot find reduced data.</h4>
		 <p>Reduced data are uploaded by the group that performed the observations. We encourage them to deliver 
		 reduced data, but it is not compulsory. Therefore, it is normal to find raw data without associated reduced data.<p>
		 
		<H3>More questions</H3>
		
		 <h4>Is there any recommended format of the acknowledgment for use of GTC data archive?</h4>
		 <p>If you use this service in your research, please include the following acknowledgement in any resulting publications: 
		 <b>"Based on data from the GTC Archive at CAB (INTA-CSIC)"</b>.<p>
		
		<h4>I can not preview an image in Aladin</h4>
		 <p>For the preview option to work, you have to have java installed on your machine.<br>
		 Clicking the "preview" option, a windows will ask you what to do with this file, you have to choose 
		 "Open with" and select the option "javaws".<br>
		 You can find "javaws" in the "bin" directory in your java folder. Other distributions (e.g. Iced Tea) may not work.<p>
		 
		 
	
<%= pie %>
</body>
</html>
