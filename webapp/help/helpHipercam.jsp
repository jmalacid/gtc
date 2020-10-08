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
String cabeceraPagina = elementosHtml.cabeceraPagina("Product Details","CanariCam Data");
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
color="#FFFFFF"><font size=+2>&nbsp;GTC Archive - HiperCam Data</font></font></font>
</td>
</tr>
</table>

		<h3>HiPERCAM Data</h3>
		 
<p>The raw files are very heavy, and they can not be downloaded from the GTC Archive. If you want to have this data, please contact us through the email 'gtc-support@cab.inta-csic.es'.</p>

 

A HiPERCAM observation can have associated the following files
<ul>
<li type="disc"><b>Reduced files</b>:
	<ul>
	<li type="circle">The <b>.red files</b> are configuration files for the hipercam pipeline. They're useful to show the PIs what settings were used to perform the quick look data reduction. They're relatively simple ascii files the PI can open in any editor.</li>
	<li type="circle">http://deneb.astro.warwick.ac.uk/phsaap/hipercam/docs/html/index.html</li>
	</ul>
</li>

<li type="disc">The <b>.log files</b> are how we store the reduced lightcurves. They are also ascii files and the comments in the file explain the format, so a PI can write their own plotting scripts to look at their data. However, if the user has installed the Hipercam pipeline there are two easier options:
	<ul>
	<li type="circle">Use the built-in 'plog' command (http://deneb.astro.warwick.ac.uk/phsaap/hipercam/docs/html/commands.html#hipercam.scripts.plog)</li>
	<li type="circle">Write their own Python script using the `Hlog` and `TSeries` objects inside the pipeline. This isn't documented at the moment</li>
	</ul>
</li>

</ul>

More information on HiPERCAM data <a href='http://www.gtc.iac.es/instruments/hipercam/hipercam.php'>here</a><p>
		 
<br>	 
	
<%= pie %>
</body>
</html>
