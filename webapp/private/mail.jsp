<%@ page import="svo.gtc.web.Html,
	java.sql.*,
	svo.gtc.web.ContenedorSesion,
	svo.gtc.db.priv.DBPrivate" %>
                 
<%@ page info="GTC Scientific Archive Homepage" %>

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
	String cabeceraPagina = elementosHtml.cabeceraPagina("GTC","Private zone");
	String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath()+"?"+request.getQueryString());
	String pie            = elementosHtml.pie();
%>

<%--------     I N I C I O    P Á G I N A    H T M L    -------------------%>

<%= cabeceraPagina %>

<body>

<%= encabezamiento %>

<% 
String url = "?"+request.getQueryString();
String bibcode = request.getParameter("bib");
String user = request.getParameter("user");
String pass = request.getParameter("pass");
String email = request.getParameter("email");
String type = request.getParameter("type");
String state = request.getParameter("stateant");%>

<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px" >

<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER><font face="arial,helvetica,san-serif"><font
color="#FFFFFF"><font size=+2>&nbsp;The Gran Telescopio CANARIAS Archive<br><font color=red>-Private zone-</font></font></font></font>
</td>
</tr>
</table><br>

<%if(type.equals("1")){%>
<table class="appstyle" cellspacing="3" cellpadding="20" >
	
	<tr>
	<td class="rescab" align="center" > Correo SUBIR DATOS</td>	
	</tr>
	<tr>
	<td class="resfield" align="left">Dear colleague,<br><br>First of all I would like to thank you for your willingness to share your reduced data with the community through the GTC Archive.
	 Please, find below some instructions on how to proceed.<br><br>a) Go to the archive web page "http://gtc.sdc.cab.inta-csic.es/gtc/index.jsp" and log in using the top right button. 
	 You will be prompted to enter your username/password<br><br>user:
	 <%if(user.length()==0){ %>
	 <font color="red"><blink>INCOMPLETO</blink></font>
	 <%}else{ %>
	  <%=user %>
	  <%} %><br><br>pass: <%if(pass.length()==0){ %>
	  <font color="red"><blink>INCOMPLETO</blink></font>
	 <%}else{ %>
	  <%=pass %>
	  <%} %><br><br>b) Click on "Upload reduced data" <br><br>c)Create the data collection by click on "New Collection"<br><br>
	 d) Upload the reduced data.<br><br>A detailed step-by-step guide on how to proceed can be found at: http://gtc.sdc.cab.inta-csic.es/gtc/help/overview.jsp#6<br><br>Should you need any question, 
	 do not hesitate to contact me. If you want to change your password go to "https://gtc.sdc.cab.inta-csic.es/gtc/s/changePasswordInput.action" <br><br>Best regards,<br><br>GTC Archive Support Team</td>	
	</tr>
</table>	
	
	
	
<%}else if(type.equals("2")){ %>
<table class="appstyle" cellspacing="3" cellpadding="20" >
	
	<tr>
	<td class="rescab" align="center" > Correo para contactar con usuario</td>	
	</tr>
	<tr>
	<td class="resfield" align="left">Dear colleague,<br><br>As you probably know, the <b>GTC archive</b> (http://gtc.sdc.cab.inta-csic.es/gtc/index.jsp) is operational since November 2011. 
	It is  our aim to provide access not only to raw and calibration data but also to reduced data ready to be used by the scientific community.<br><br>One of the adopted mechanisms to fulfil 
	this objective is to contact the first authors of refereed papers which make use of GTC data and invite them to send their reduced datasets to the archive. This is something entirely 
	voluntary but we believe that publishing the reduced data in the GTC archive can be highly beneficial to your group as the data will be easily accessible to the community. In particular, 
	I am contacting you regarding the paper "
	<%if(bibcode.length()==0){ %>
	 <font color="red"><blink>INCOMPLETO</blink></font>
	 <%}else{ %>
	  <%=bibcode %>
	  <%} %>".<br><br>If you agree in uploading your data to the archive, please, let us know and we will send you the username/passwd and some instructions on how to proceed.<br>Many thanks in 
	  advance for your co-operation.<br><br>Looking forward to hearing from you soon.<br><br>Best regards,<br><br>GTC Archive Support Team</td>	
	</tr>
</table>

<%}else if(type.equals("3")){ %>
<table class="appstyle" cellspacing="3" cellpadding="20" >
	
	<tr>
	<td class="rescab" align="center" > Correo recordatorio</td>	
	</tr>
	<tr>
	<td class="resfield" align="left">Dear colleague,<br><br>In the last months we have send three emails inviting you to upload your reduced data (
	<%if(bibcode.length()==0){ %>
	 <font color="red"><blink>INCOMPLETO</blink></font>
	 <%}else{ %>
	  <%=bibcode %>
	  <%} %>) to the GTC Archive 
	(http://gtc.sdc.cab.inta-csic.es/gtc/). Since we have not heard from you, we would be very grateful if you could let us know the reasons why you prefer not to upload your data to the archive. Your feedback is very important to improve our service.<br><br>Thank you very much in advance for your collaboration.<br><br>Best regards,<br><br>GTC Archive Support Team</td>	
	</tr>
</table>

<%}else if(type.equals("4")){ %>
<table class="appstyle" cellspacing="3" cellpadding="20" >
	
	<tr>
	<td class="rescab" align="center" > Último intento (Español)</td>	
	</tr>
	<tr>
	<td class="resfield" align="left">Hola <font color="red"><blink>NOMBRE</blink></font>,<br><br>El pasado mes de <font color="red"><blink>MES</blink></font> contactamos contigo invitándote a enviar al archivo de GTC los datos reducidos 
	que aparecen en el artículo <%if(bibcode.length()==0){ %>
	 <font color="red"><blink>INCOMPLETO</blink></font>
	 <%}else{ %>
	  <%=bibcode %>
	  <%} %>.<br><br>En caso de que hayas decidido no enviarlos nos gustaria saber los motivos. Tus comentarios nos serán muy útiles para mejorar el archivo.<br><br>Saludos,<br><br>GTC Archive Support Team</td>	
	</tr>
</table>

<%}else if(type.equals("5")){ %>

<form id="formMail" name="formMail" method="post"
	action="mandaMail.jsp" enctype="application/x-www-form-urlencoded" onSubmit=""><input type="hidden" value="5" name="type" /><input type="hidden" value="<%=email %>" name="email" />
	
<table class="appstyle" cellspacing="3" cellpadding="20" >

<tr>
<td class="rescab" align="center" > Correo texto libre</td>	
</tr>
<tr>
<td class="resfield" align="center">Título</td>	
</tr><tr>
<td class="resfield" align="center"><input type="text" name="titulo" maxlength="50" size="50" /></td>	
</tr><tr>
<td class="resfield" align="center">Cuerpo</td>	
</tr><tr>
<td class="resfield" align="left"><textarea name="body" rows="10" cols="60" ></textarea><br>Best regards,<br><br>GTC Archive Support Team</td>	
</tr>
</table>

<input type="submit" name="submit" value="Mandar mail" />

</form>

<%} else if(type.equals("6")){%>
<table class="appstyle" cellspacing="3" cellpadding="20" >

<tr>
<td class="rescab" align="center" > Correo SUBIR DATOS</td>	
</tr>
<tr>
<td class="resfield" align="left">Dear colleague,<br><br>We have seen that you already have an username to access to the archive (
<%if(user.length()==0){ %>
 <font color="red"><blink>INCOMPLETO</blink></font>
 <%}else{ %>
  <%=user %>
  <%} %>
  ), please let us know if your don't remember your password and we will create a new one.<br><br> Thanks you for your willingness to share your reduced data with the community through the GTC Archive.
 Please, find below some instructions on how to proceed.<br><br>a) Go to the archive web page "http://gtc.sdc.cab.inta-csic.es/gtc/index.jsp" and log in using the top right button. 
 You will be prompted to enter your username/password.<br><br>b) Click on "Upload reduced data" <br><br>c)Create the data collection by click on "New Collection"<br><br>
 d) Upload the reduced data.<br><br>A detailed step-by-step guide on how to proceed can be found at: http://gtc.sdc.cab.inta-csic.es/gtc/help/overview.jsp#6<br><br>Should you need any question, 
 do not hesitate to contact me. If you want to change your password go to "https://gtc.sdc.cab.inta-csic.es/gtc/s/changePasswordInput.action" <br><br>Best regards,<br><br>GTC Archive Support Team</td>	
</tr>
</table>	



<%} %>

	<p class="formbus1"><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<a href="publication.jsp?bib=<%=bibcode.replaceAll("&", "%26")%>&state=<%=state %>&order=pub_bibcode"> Volver </a></p>
	<%if(email.length()<=0 || email.equals("null")){ %>
		<p class="formbus1"><img SRC="/gtc/images/bluearrow.gif" >&nbsp; <strike>Mandar mail</strike> (No se dispone del correo del usuario)</p>
	<%}else if(type.equals("4")){%>
		<p class="formbus1"><img SRC="/gtc/images/bluearrow.gif" >&nbsp; <strike>Mandar mail</strike> (Debido a la personalización de este email, no se puede mandar automaticamente)</p>
	<%}else if(type.equals("5")){%>
	<%}else{ %>
		<p class="formbus1"><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<a href="mandaMail.jsp<%=url%>"> Mandar mail </a></p>
	<%} %>
	</form>

<%= pie %>
</body>
</html>