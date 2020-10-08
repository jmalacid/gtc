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
color="#FFFFFF"><font size=+2>&nbsp;GTC Archive - CanariCam Data</font></font></font>
</td>
</tr>
</table>

		<h3>CanariCam Data</h3>
		 
<p>CanariCam data is delivered in standard multi-extension FITS (MEF) file format. Each extension contains an image, as well as specific headers relevant to those extensions. The zeroth header is a general header, containing information about the full dataset. 
<br><br>
Each multi-extension FITS has associated a single (photon accumulated) FITS file. These files are labelled <i>"reduced"</i> . Nevertheless,
<ul>
<li type="disc">They are <font color='red'><b>NOT</b></font> science-ready products (despite the word "reduced" might indicate)</li>
<li type="disc">Reduced science files are astrometrically corrected. However, astrometry comes from the telescope pointing and errors up to several arcseconds can be expected.</li>
</ul>

 

A CanariCam observation can have associated the following files
<ul>
<li type="disc"><b>Science files</b>:
	<ul>
	<li type="circle"><b>Raw_cube</b>: Raw science data in multi-extension FITS format.</li>
	<li type="circle"><b>Raw_reduced</b>: Raw science data in single FITS format.</li>
	</ul>
</li>

<li type="disc"><b>Calibration files</b>:
	<ul>
	<li type="circle"><b>Quality Standard_cube</b>: Night quality standard in multi-extension FITS format. They are images taken in the Si5 and Q1 filters to monitor the mid-IR quality of the night.</li>
	<li type="circle"><b>Quality Standard_reduced</b>:  Night quality standard in single FITS format.</li>
	<li type="circle"><b>Standard star_cube</b>: Standard star in  multi-extension FITS format. The different types of standard stars for CanariCam can be found
	 <a href='http://www.gtc.iac.es/instruments/canaricam/canaricam.php#Calibrations'>here</a></li>
	<li type="circle"><b>Standard star_reduced</b>: Standard star in single FITS format.</li>
	</ul>
</li>

<li type="disc"><b>Acquisition images</b>:
<ul>
	<li type="circle"><b>Acquisition image_cube</b>: Acquisition image  in multi-extension FITS format.</li>
	<li type="circle"><b>Acquisition image_reduced</b>: Acquisition image  in single FITS format.</li>
	<li type="circle"><b>Acquisition image through slit_cube</b>: Acquisition image  through slit in multi-extension FITS format.</li>
	<li type="circle"><b>Acquisition image through slit_reduced</b>: Acquisition image through slit in single FITS format.</li>

</ul>
 </li>

</ul>

More information on CanariCam data <a href='http://www.gtc.iac.es/instruments/canaricam/canaricam.php'>here</a><p>
		 
<br>	 
	
<%= pie %>
</body>
</html>
