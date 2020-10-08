<%@ page import="svo.gtc.web.Html,
svo.gtc.web.FormICC,
svo.gtc.web.ContenedorSesion,
svo.gtc.web.FormParameterException,
svo.gtc.db.logquery.LogQueryDb,
svo.gtc.db.logquery.LogQueryAccess,
java.net.URLEncoder,
utiles.StringUtils" %>
                 
<%@ page isThreadSafe="true" %>
<%@ page info="GTC Search" %>


<%-- P  R  O  C  E  S  O  S --%>
<%
//*************************************************************
//*  SESION                                                   *
//*************************************************************
ContenedorSesion contenedorSesion = null;
if(session!=null){ 
	contenedorSesion = (ContenedorSesion)session.getAttribute("contenedorSesion");
}

if(contenedorSesion==null) contenedorSesion=new ContenedorSesion();
	

//*************************************************************
//*  COMPROBACIONES                                           *
//*************************************************************

String MSG="";

FormICC formulario = null;

try{
	formulario=new FormICC(request);
}catch(Exception e){
	MSG="Unable to process request.";
	e.printStackTrace();
}

if(MSG.length()==0 && (formulario==null || !formulario.isValid())){
	MSG = "No search conditions";
}

/* if(MSG.length()==0){
	
	//*************************************************************
	//*  ESTADISTICAS                                             *
	//*************************************************************
	LogQueryDb logQuery = new LogQueryDb();
	logQuery.setLogqTime(new Timestamp((new Date()).getTime()));
	logQuery.setLogqHost(request.getRemoteHost());
	logQuery.setLogqType("MOC");
	LogQueryAccess logqAccess = new LogQueryAccess(conex);
	logqAccess.insert(logQuery);
}
 */



// *************************************************************
// *  TECLAS DEL FORMULARIO                                    *
// *************************************************************

	//---- Elementos estáticos de la página
	Html elementosHtml = new Html(request.getContextPath());
	String cabeceraPagina = elementosHtml.cabeceraPagina("Search Results","Search results page for GTC Archive.");
	String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath());
	String pie            = elementosHtml.pie();
%>

<%--------     I N I C I O    P Á G I N A    H T M L    -------------------%>
<html>
<%=cabeceraPagina%>
<SCRIPT LANGUAGE="JavaScript" type="text/javascript" src="/gtc/js/wz_tooltip.js"></SCRIPT>
<body>
<%=encabezamiento%>

<%
	if(MSG.length()>0){
%>
	<img src="../pieces/nada50x1.gif" alt=""><p class="appstyle" style="font-size: 12pt"><blink><%=MSG%></blink></p><br>
<%
	}else{
%>




<%-----  SECCION QUE SOLO SE PRESENTA SI HAY RESULTADOS  ------%>
<%
	if (formulario.getCoordsList().length >0){
%>

<p>
<br>
</p>

<%--------  FORMULARIO DE PROCESADO MÚLTIPLE  -------------------%>

<!--   Tabla de resultados -->			
<table cellspacing="3" WIDTH="400px" border="0" >
	<tr>	
		<td class="rescab" align="center" nowrap>
			RA
		</td>
		<td class="rescab" align="center" nowrap>
			DEC
		</td>
		<td class="rescab" align="center" nowrap>
			Coverage
		</td>

	</tr>


<%for(int i = 0; i < formulario.getCoordsList().length; i++){

//Healpix moc = new Healpix();

%>
	
	<tr>
	 
		<td class="resfield" nowrap align="left"><!-- RA -->
			<%=formulario.getCoordsList()[i][0]%>
		</td>
		<td class="resfield" nowrap align="left"><!-- DEC -->
			<%=formulario.getCoordsList()[i][1] %>
		</td>
		<td class="resfield" nowrap align="left"><!-- Coverage -->
			
		</td>
		
	</tr>
<%} %>
</table>

<%-----  FIN SECCION QUE SOLO SE PRESENTA SI HAY RESULTADOS  ------%>
<% } %> 


<%-----  FIN DEL ELSE ERROR PÁGINA  ------%>
 
<p class="formbus1"><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<a href="searchform.jsp"><b>Volver </b></a></p>

	<%}%>


<p>
<br>
</p>

<%= pie %>
</body>
</html>
