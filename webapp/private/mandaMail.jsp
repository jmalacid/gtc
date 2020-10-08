<%@ page import="svo.gtc.web.Html,
	svo.gtc.utiles.Mail,
	svo.gtc.web.ContenedorSesion,
	svo.gtc.db.priv.DBPrivate,
	svo.gtc.priv.objetos.BibChange" %>
                 
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
String email = request.getParameter("email");
String type = request.getParameter("type");
String texto = null;
String bibcode = null;
String user = null;
String pass = null;
String state = null;
try{
	bibcode = request.getParameter("bib");
	user = request.getParameter("user");
	pass = request.getParameter("pass");
	state = request.getParameter("stateant");
}catch(Exception e){
	//No necesitaríamos estos valores
}
%>

<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px" >

<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER><font face="arial,helvetica,san-serif"><font
color="#FFFFFF"><font size=+2>&nbsp;The Gran Telescopio CANARIAS Archive<br><font color=red>-Private zone-</font></font></font></font>
</td>
</tr>
</table><br>

<%if(type.equals("1")){
 //Correo SUBIR DATOS

	texto = "Dear colleague,\n\nFirst of all I would like to thank you for your willingness to share your reduced data with the community through the GTC Archive." +
	"Please, find below some instructions on how to proceed.\n\na) Go to the archive web page  http://gtc.sdc.cab.inta-csic.es/gtc/index.jsp  and log in using the top right button."+
	"You will be prompted to enter your username/password\n\nuser:"+user+"\n\npass:"+ pass+"\n\nb) Click on \"upload reduced data\".\n\nc) Create the data collection by click on \"New Collection\"\n\n" +
	"d) Upload the reduced data.\n\nA detailed step-by-step guide on how to proceed can be found at: http://gtc.sdc.cab.inta-csic.es/gtc/help/overview.jsp#6\n\nShould you need any question,"+
	"do not hesitate to contact us.\n\nBest regards,\n\nGTC Archive Support Team";
	
	Mail prueba = new Mail(texto,"GTC publication "+bibcode,email);
	
	
}else if(type.equals("2")){ 
	//Correo para contactar con usuario	
	texto = "Dear colleague,\n\nAs you probably know, the GTC archive (http://gtc.sdc.cab.inta-csic.es/gtc/index.jsp) is operational since November 2011. "+ 
	"It is  our aim to provide access not only to raw and calibration data but also to reduced data ready to be used by the scientific community.\n\nOne of the adopted mechanisms to fulfil "+ 
	"this objective is to contact the first authors of refereed papers which make use of GTC data and invite them to send their reduced datasets to the archive. This is something entirely " + 
	"voluntary but we believe that publishing the reduced data in the GTC archive can be highly beneficial to your group as the data will be easily accessible to the community. In particular,"+ 
	"I am contacting you regarding the paper "+bibcode+".\n\nIf you agree in uploading your data "+
	"to the archive, please, let us know and we will send you the username/passwd and some instructions on how to proceed.\n\nMany thanks in " +
	"advance for your co-operation.\n\nLooking forward to hearing from you soon.\n\nBest regards,\n\nGTC Archive Support Team";

	BibChange bc = new BibChange(bibcode,"1");
	bc.ChangeState();
	Mail prueba = new Mail(texto,"GTC publication "+bibcode,email);
			
}else if(type.equals("3")){ 
	//Correo recordatorio

	texto = "Dear colleague,\n\nIn the last months we have send three emails inviting you to upload your reduced data ("+bibcode+") to the GTC Archive " + 
	"(http://gtc.sdc.cab.inta-csic.es/gtc/). Since we have not heard from you, we would be very grateful if you could let us know the reasons why you prefer " +
	"not to upload your data to the archive. Your feedback is very important to improve our service.\n\nThank you very much in advance for your collaboration."+
	"\n\nBest regards,\n\nGTC Archive Support Team";	

	Mail prueba = new Mail(texto,"GTC publication "+bibcode,email);
}else if(type.equals("5")){ 
	//Correo libre

	texto = request.getParameter("body")+"\n\nBest regards,\n\nGTC Archive Support Team";	
	String header = request.getParameter("titulo");

	Mail prueba = new Mail(texto, header, email);
}else if(type.equals("6")){
	 //Correo SUBIR DATOS

		texto = "Dear colleague,\n\nWe have seen that you already have an username to access to the archive ("+user+"), please let us know if you don't remember your password and we will "+
		"create a new one.\n\nThanks for your willingness to share your reduced data with the community through the GTC Archive." +
		"Please, find below some instructions on how to proceed.\n\na) Go to the archive web page  http://gtc.sdc.cab.inta-csic.es/gtc/index.jsp  and log in clicking the top right button and using your credentials."+
		"\n\nb) Click on \"upload reduced data\".\n\nc) Create the data collection by click on \"New Collection\"\n\n" +
		"d) Upload the reduced data.\n\nA detailed step-by-step guide on how to proceed can be found at: http://gtc.sdc.cab.inta-csic.es/gtc/help/overview.jsp#6\n\nShould you need any question,"+
		"do not hesitate to contact us.\n\nBest regards,\n\nGTC Archive Support Team";
		
		Mail prueba = new Mail(texto,"GTC publication "+bibcode,email);
		
		
	}
 
	String contSt ="controlState.jsp?order=pub_bibcode&bib=&state="+state+"&submit=Submit+Query";
	%>
	<jsp:forward page="<%=contSt %>"/> 



<%= pie %>
</body>
</html>