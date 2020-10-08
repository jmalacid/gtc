<%@ page import="svo.gtc.web.Html,
				svo.gtc.web.ContenedorSesion,
				svo.gtc.web.FormMain,
				svo.gtc.web.WebUtils,
				
				svo.gtc.db.DriverBD,
				svo.gtc.db.prodat.ProdDatosDb,
				svo.gtc.db.prodat.ProdDatosAccess,
				svo.gtc.db.permisos.ControlAcceso,
				
				
				java.sql.*,
				java.util.TimeZone,
				java.util.Locale
				" %>
                 
<%@ page isThreadSafe="true" %>
<%@ page info="GTC Reducted Data" %>


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


//======================================================= 
//Conexion con la Base de Datos                       
//=======================================================

Connection conex= null;

DriverBD drvBd = new DriverBD();

try {
	conex = drvBd.bdConexion();
} catch (SQLException errconexion) {
	errconexion.printStackTrace();
}

ProdDatosAccess datosAccess = new ProdDatosAccess(conex);


//********************************************************************* 
//*  Zona horaria se fija a UTC                                       *
//*********************************************************************
TimeZone.setDefault(TimeZone.getTimeZone("UTC"));



// *************************************************************
// *  DEFINICION DE VARIABLES
// *************************************************************

Locale local  = request.getLocale();
String lengua = (request.getHeader("accept-language")).trim().substring(0, 2);
String ACASA =  "http://";
       ACASA += request.getServerName();
       ACASA += ":";
       ACASA += request.getServerPort();


String MSG = "";


//---- Elementos estáticos de la página
Html elementosHtml = new Html(request.getContextPath());
String cabeceraPagina = elementosHtml.cabeceraPagina("Insert reduced data","Insert Reduced Product");
String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath());
String pie            = elementosHtml.pie();

String par_prodId = request.getParameter("prod_id");

String prog_id = WebUtils.getWebProgId(par_prodId);
String obl_id=WebUtils.getWebOblId(par_prodId);;
Integer prod_id = WebUtils.getWebProdId(par_prodId);

ProdDatosDb prod  = datosAccess.selectById(prog_id, obl_id, prod_id);

String progId = "";
String oblId = "";
Integer prodId = null;
String prodFilename = "";

if(prod!=null){
	progId = prod.getProgId();
	oblId = prod.getOblId();
	prodId = prod.getProdId();
	prodFilename=prod.getProdFilename();
}else{
	MSG="No RAW product found with this id: "+par_prodId;
}


//======================================================= 
//Control de Acceso                       
//=======================================================
ControlAcceso controlAcceso = new ControlAcceso(conex, contenedorSesion.getUser());
if(prod!=null){
	if( (prod!=null) && !controlAcceso.hasPerm(progId,contenedorSesion.getUser()) ){
		String pagOrigen = request.getServletPath()+"?"+request.getQueryString();
		request.setAttribute("pagOrigen",pagOrigen);
		conex.close();
		%>
		<jsp:forward page="accessDenied.jsp"/>
		<%

	}

}


%>

<%--------     I N I C I O    P Á G I N A    H T M L    -------------------%>
<html>
<%= cabeceraPagina %>

<body>
<%= encabezamiento %>
<table width="800" cellspacing="0" cellpadding="2" border="0">
<tbody>
<tr>
<td class="headtitle" align="center">
GTC Archive: Insert new reduced product.
</td>
</tr>
</tbody>
</table>


<%if(MSG.length()>0){ %>
<p class="appstyle salto aviso"><%= MSG %></p>
<%} %>

<%if(prod!=null){ %>
<p class="appstyle salto" style="font-size:12pt;">Raw product:</p>
<table>
	<tr>
		<td class="rescab" >Program ID:</td><td class="resfield"><%=progId %></td>
	</tr>
	<tr>
		<td class="rescab">ObsBlock ID:</td><td class="resfield"><%=oblId %></td>
	<tr>
	<tr>
		<td class="rescab">Prod ID:</td><td class="resfield"><%=prodId %></td>
	<tr>
	<tr>
		<td class="rescab">File:</td><td class="resfield"><%=prodFilename %></td>
	<tr>
</table>		


<form name="form_insert" method="post" enctype="multipart/form-data" action="insertReducedProduct.jsp">
<input type="hidden" name="origen" value="insertReducedProduct.jsp"></input>

<p class="appstyle salto" style="font-size:12pt;">New reduced product:</p>
<table class="appstyle" style="width:800px; background-color:#eeeeee;">
	<tr>
		<td width="100em">
			Type:
		</td>
		<td nowrap>
			<select name="reduced_type">
				<option>Tipo 1</option>
				<option>Tipo 2</option>
				<option>Tipo 3</option>
		</td>
	</tr>
	<tr>
		<td>
			File:
		</td>
		<td>
			<input type="file" name="reduced_file">
		</td>
	</tr>
	<tr>
		<td>
			Target name:
		</td>
		<td>
			<input type="text" name="reduced_target">
		</td>
	</tr>
	<tr>
		<td colspan="2">
			Description:
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<textarea name="reduced_desc" width="100%" rows="15" cols="80"></textarea>
		</td>
	</tr>
</table>

<p class="appstyle salto"><input type="submit" name="submit" value="Upload"></p>
</form>
<%} %>


<%= pie %>
</body>
</html>

<%
	// Cierre de la conexión a la base de datos
	conex.close();
%>
