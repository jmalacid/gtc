<%@ page import="svo.gtc.web.Html,
				 svo.gtc.web.ContenedorSesion,

				 java.net.URLEncoder,
                 java.util.Locale,
                 java.util.Vector,
                 java.util.Enumeration" %>
                 
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

//*************************************************************
//*  TECLAS DEL FORMULARIO                                    *
//*************************************************************

	//---- Elementos estáticos de la página
	Html elementosHtml = new Html(request.getContextPath());
	String cabeceraPagina = elementosHtml.cabeceraPagina("Product Details","Data Product Details.");
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
color="#FFFFFF"><font size=+2>&nbsp;The Gran Telescopio CANARIAS Archive</font></font></font>
</td>
</tr>
<tr>
<td ALIGN=LEFT><br><i><font face="arial,helvetica,geneva"><font size=+0>

This data server provides access to the <a href="http://www.gtc.iac.es/">GTC</a> Archive. The 
<b>Gran Telescopio CANARIAS (GTC)</b>, is a 10.4m telescope with a segmented primary mirror. 
It is located in one of the top astronomical sites in the Northern Hemisphere: the 
<a href="http://www.iac.es/eno.php?op1=2&lang=en">Observatorio del Roque de los Muchachos</a> 
(ORM, La Palma, Canary Islands). The GTC is a Spanish initiative leaded by the 
<a href="http://www.iac.es">Instituto de Astrofísica de Canarias (IAC)</a>. The project is 
actively supported by the Spanish Government and the Local Government from the Canary Islands 
through the European Funds for Regional Development (FEDER) provided by the European Union. 
The project also includes the participation of Mexico 
(<a href="http://www.astroscu.unam.mx/">Instituto de Astronomía de la Universidad Nacional 
Autónoma de México (IA-UNAM)</a> and <a href="http://www.inaoep.mx/">Instituto Nacional de 
Astrofísica, Óptica y Electrónica (INAOE )</a>) and the US 
(<a href="http://www.astro.ufl.edu/">University of Florida (UFL)</a>). 


</font></font></i>
<br>&nbsp;
&nbsp;
<br>
<tr>
<td ALIGN="CENTER">

		<center><p><br><br><i><font face="arial,helvetica,geneva"><font size=+2>
		The GTC Archive is under maintenance. Please, try again after some minutes. Sorry for the inconvenience. 
		</font></font></i>

</td>
</tr>
<tr>
	<td colspan="2">
		<center><p><br><br><i><font face="arial,helvetica,geneva"><font size=+0>
		The GTC Archive has been developed in the framework of the Spanish Virtual 
		Observatory project supported by the Spanish MICINN through grant AYA 2008-02156. 
		The system is maintained by the Data Archive Unit of the 
		<a href="http://cab.inta.es/">CAB (CSIC -INTA)</a>.</font></font></i>
		
		<p><i><font face="arial,helvetica,geneva"><font size=+0>If you use this service 
		in your research, please include the following acknowledgement in
		any resulting publications:&nbsp;</font></font></i>
		<br><i><b><font face="arial,helvetica,geneva"><font size=+0>"Based on data
		from the GTC Archive at CAB (INTA-CSIC)".</font></font></b></i></center>	
	</td>
</tr>
</table>
<br><br>

<%= pie %>
</body>
</html>
