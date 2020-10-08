<%@ page import="svo.gtc.web.Html,
	java.sql.*,
	svo.gtc.web.ContenedorSesion,
	svo.gtc.db.priv.DBPrivate,
	svo.gtc.priv.objetos.BibChange,
	svo.gtc.priv.objetos.Bibcodes,
	svo.gtc.priv.objetos.RedInfo,
	javax.sql.*,
	javax.naming.Context,
	javax.naming.InitialContext,
	javax.naming.NamingException" %>
                 
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

	//---- Elementos est�ticos de la p�gina
	Html elementosHtml = new Html(request.getContextPath());
	String cabeceraPagina = elementosHtml.cabeceraPagina("GTC","Private zone");
	String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath()+"?"+request.getQueryString());
	String pie            = elementosHtml.pie();
%>

<%--------     I N I C I O    P � G I N A    H T M L    -------------------%>

<%= cabeceraPagina %>

<body>

<%= encabezamiento %>
<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px" >

<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER><font face="arial,helvetica,san-serif"><font
color="#FFFFFF"><font size=+2>&nbsp;The Gran Telescopio CANARIAS Archive<br><font color=red>-Private zone-</font></font></font></font>
</td>
</tr>
</table>
<%
String submit = request.getParameter("submit");

///// COMPROBACI�N DE VALORES DEL FORMULARIO
String msg = "";
BibChange parametrosFormulario = null;
if(submit!=null && submit.length()>0){
	try{
		parametrosFormulario = new BibChange(request);
		parametrosFormulario.AddChange();
	}catch(Exception e){
		msg+=e.getMessage();
	}
}


if(msg.length()>0){
%>
	<p class="formbus1"><font color="red"><blink><%=msg %></blink></font></p>
<% 	
}else if(submit!=null && submit.length()>0){
%> 
	
<%	} %>



<%

String bibcode = request.getParameter("bib");
String stateant = request.getParameter("stateant");
//Abrimos la conexi�n a la base de datos
Context initContext;
Connection cn=null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();

DBPrivate union = new DBPrivate(cn);

Bibcodes bib = union.BibInfo(bibcode);
String[] historial = union.infoHist(bibcode);
String[] state = union.infoState();
String[] productos = union.infoProd(bibcode);
RedInfo redtype = union.selectInfo(bibcode);

%>
<br>



<table class="appstyle" cellspacing="3" cellpadding="20" >
	<tr>
	<td class="rescab" align="right" > Bibcode </td>	
	<td class="resfield" align="center"> <a href="http://adsabs.harvard.edu/cgi-bin/nph-data_query?bibcode=<%=bibcode %>&db_key=PRE&link_type=ABSTRACT" target="_blank"><%=bibcode %></a></td>	
	</tr>
	<tr>
	<td class="rescab" align="right" > Producto </td>
	<%if(productos.length>0){ %>
		<td class="resfield" align="center">
		<%for(int i =0; i<productos.length;i++){ %>
	 		<%=productos[i] %><br>
		 <%}%></td>	
	<%}else{ %>
	<td class="resfield" align="center"> N/A</td>
	<%} %>
	</tr>
	<tr>
	<td class="rescab" align="right" > Autor </td>	
	<td class="resfield" align="center"> <%=bib.getAutor() %></td>	
	</tr>
	<tr>
	<td class="rescab" align="right" > Estado actual </td>	
	<td class="resfield" align="center"> <%=bib.getEstado_desc() %></td>	
	</tr>

	<tr>
	<td class="rescab" align="right" colspan="2"> Comentarios </td>		
	</tr>
	<%for(int i =0; i<historial.length;i++){ 
		String[] hist = historial[i].split("--");%>
		<tr>
		<td class="resfield" align="right" > <%= hist[0]%> </td>	
		<td class="resfield" align="right"> <%= hist[1]%></td>	
		</tr>
	<%} %>
	</table>
	<br>
	<a href="controlState.jsp?bib=&state=<%=stateant %>&order=pub_bibcode"><b> Volver </b></a>

<form id="formulario" name="formulario" method="get"
	action="publication.jsp" enctype="application/x-www-form-urlencoded"
	onSubmit=""><input type="hidden" value="<%=bibcode %>" name="bib" /><input type="hidden" value="<%=stateant %>" name="stateant" />
	

<br>
	<%if(redtype!=null){ %>
	<input type="hidden" value="<%=redtype.getCol_id() %>" name="col_id" /><input type="hidden" value="<%=redtype.getUsr_id() %>" name="usr_id" />
	<table class="appstyle" cellspacing="3" cellpadding="20">
	<tr>
	<td class="rescab" align="right" colspan="3" > Cambiar informaci�n de la colecci�n: </td>		
	</tr>
		<tr>
		<td class="rescab" rowspan="2" align="right" > redtype: </td>
		<td class="resfield"><%=redtype.getRedtype() %></td>
		<td class="resfield" align="left"><input type="text" name="redtype" maxlength="1000" size="100" /></td>	
		</tr>
		<tr>
		<td class="resfield" colspan=3>
		<%if(redtype.isBiassub()){ %>
			<input type="checkbox" name="bias" value="true" checked> Bias substraction
		<%}else{ %>
			<input type="checkbox" name="bias" value="true"> Bias substraction
		<%} %>
		<%if(redtype.isDarksub()){ %>
			<input type="checkbox" name="dark" value="true" checked> Dark substraction
		<%}else{ %>
			<input type="checkbox" name="bias" value="true"> Dark substraction
		<%} %>
		<%if(redtype.isFlatfield()){ %>
			<input type="checkbox" name="flatfield" value="true" checked> Flat-field correction
		<%}else{ %>
			<input type="checkbox" name="flatfield" value="true"> Flat-field correction
		<%} %>
		</td></tr>
		<tr>
		<td class="resfield" colspan=3>
		<%if(redtype.isPhotometric()){ %>
			<input type="checkbox" name="photometric" value="true" checked> Photometric correction
		<%}else{ %>
			<input type="checkbox" name="photometric" value="true"> Photometric correction
		<%} %>
		
		</td>
		</tr>
		<tr>
		<td class="resfield" colspan=3>
		<%if(redtype.isAstrometry()){ %>
			<input type="checkbox" name="astrometry" value="true" checked> Astrometric correction
			<br>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Status:
			<%if(redtype.getAststatus().equalsIgnoreCase("uncalibrated")){ %>
				<input type="radio" name="aststatus" value="uncalibrated" checked> uncalibrated<input type="radio" name="aststatus" value="raw"> raw
				<input type="radio" name="aststatus" value="calibrated">calibrated<br>
		
			<%}else if(redtype.getAststatus().equalsIgnoreCase("calibrated")){ %>
				<input type="radio" name="aststatus" value="uncalibrated"> uncalibrated<input type="radio" name="aststatus" value="raw"> raw
				<input type="radio" name="aststatus" value="calibrated" checked>calibrated<br>
			<%}else if(redtype.getAststatus().equalsIgnoreCase("raw")){ %>
				<input type="radio" name="aststatus" value="uncalibrated"> uncalibrated<input type="radio" name="aststatus" value="raw" checked> raw
				<input type="radio" name="aststatus" value="calibrated">calibrated<br>
			<%}else {%>
				<input type="radio" name="aststatus" value="uncalibrated" checked> uncalibrated<input type="radio" name="aststatus" value="raw"> raw
				<input type="radio" name="aststatus" value="calibrated">calibrated<br>
			<%} %>
			
			<%if(redtype.getAstprecision()!=null){ %>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Precision= <%=redtype.getAstprecision() %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" name="astprecision" maxlength="8" size="8" />
			<%}else{ %>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Precision=     <input type="text" name="astprecision" maxlength="8" size="4" /> arcsec
			<%} %>
			
		<%}else{ %>
			<input type="checkbox" name="astrometry" value="true"> Astrometric correction 
			<br>
		<input type="radio" name="aststatus" value="uncalibrated"> uncalibrated<input type="radio" name="aststatus" value="raw"> raw
		<input type="radio" name="aststatus" value="calibrated">calibrated<br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Precision=    <input type="text" name="astprecision" maxlength="8" size="4" /> arcsec
			
		<%} %>
				</td>
		</tr>
		</table>
	<%}else{ %>
	<input type="hidden" value="no" name="red" />
	<%} %>

	<table class="appstyle" cellspacing="3" cellpadding="20" >
	<tr>
	<td class="rescab" align="right" colspan="2"> Cambios en la publicaci�n: </td>		
	</tr>
	<tr>
	<td class="rescab" align="right" > A�adir nuevo comentario: </td>	
	<td class="resfield" align="left"><input type="text" name="com" maxlength="150" size="30" /></td>	
	</tr>
	<tr>
	<td class="rescab" align="right" > Cambiar estado: </td>	
	<td class="resfield" align="left"> <select class="gform" name="state">
			<option value="-" selected>-</option>
			<%for(int i=0; i<state.length;i++){
			String [] valor = state[i].split("--");%>
					<option value="<%=valor[0] %>"><%=valor[1] %></option>
					<% 
				}%>
			</select></td>	
	</tr>
	</table>
	<table class="appstyle" cellspacing="3" cellpadding="20" >
	<tr>
	<td class="rescab" align="right" colspan="2" > Cambiar producto: </td>		
	</tr>
	<tr>
	<td class="rescab" align="right" > Programa: </td>
	<td class="resfield" align="left"><input type="text" name="prog" maxlength="20" size="20" /></td>
	</tr>
	<tr>
	<td class="rescab" align="right" > Observing block: </td>
	<td class="resfield" align="left"><input type="text" name="obl" maxlength="8" size="8" /></td>
	</tr>
	<tr>
	<td class="rescab" align="right" > Producto: </td>
	<td class="resfield" align="left"><input type="text" name="prod" maxlength="50" size="20" /></td>
	</tr>
	<tr>
	<td colspan="2" class="resfield" align="center" > <a href="addprods1.jsp?bib=<%=bibcode.replaceAll("&", "%26")%>&stateant=<%=stateant%>">A�adir m�ltiples productos</a> </td>
	</tr>
	</table>

	<br>
<input type="submit" name="submit" value="A�adir cambios" />&nbsp; &nbsp;<input
	type="reset" value="Reset" />&nbsp; &nbsp; <br>

	</form>
	
	<form id="mail_user" name="mail_user" method="get" action="mail.jsp"  enctype="application/x-www-form-urlencoded"
	onSubmit=""><input type="hidden" value="<%=bibcode %>" name="bib" /><input type="hidden" value="<%=stateant %>" name="stateant" /><input type="hidden" value="<%=bib.getEmail() %>" name="email" />
	<table class="appstyle" cellspacing="3" cellpadding="20" >
	<tr>
	<td class="rescab" align="center" colspan="2"> Mandar mail a usuario: </td>	
	</tr>
	<tr>
	<td class="resfield" align="right" colspan="2"> *En caso de mandar usuario y password completarlo</td>	
	</tr>
	<tr>
	<td class="rescab" align="right" > User </td>	
	<td class="resfield" align="center"><input type="text" name="user" maxlength="30" size="20" /></td>	
	</tr>
	<tr>
	<td class="rescab" align="right" > Password </td>	
	<td class="resfield" align="center"><input type="text" name="pass" maxlength="30" size="20" /></td>	
	</tr>
	<tr>
	<td class="rescab" align="right" > Tipo </td>
	<td class="resfield" align="center"> <input type="radio" name="type" value="2" checked> Primer mensaje<br>
<input type="radio" name="type" value="1"> Subir datos<br><input type="radio" name="type" value="3"> �ltimo intento
<br><input type="radio" name="type" value="4"> �ltimo intento (Espa�ol)<br><input type="radio" name="type" value="5"> Texto libre<br>
<input type="radio" name="type" value="6"> Ya tiene usuario<br></td>		
	</tr>
	</table>
	<br>
<input type="submit" name="submit" value="Mandar mail" />

	</form>
	
<%}catch(SQLException ex){
	//fallo sql
	System.out.println("Error : "+ex.getMessage());
}catch(NamingException ex){
	System.out.println("Error al intentar obtener el DataSource:"+ex.getMessage());
}finally{
	if(cn != null){
		try{
			cn.close();
		}catch(SQLException ex){
			System.out.println("Error: "+ex.getMessage());
		}
	}
} %>	
	
<%= pie %>
</body>
</html>