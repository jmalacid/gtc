<%@ page import="svo.gtc.web.Html,
				svo.gtc.web.ContenedorSesion,
				svo.gtc.web.FormMain,
				svo.gtc.web.WebUtils,
				
				svo.gtc.db.DriverBD,
				svo.gtc.db.web.WebMainSearch,
				svo.gtc.db.web.WebMainResult,
				
				
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


FormMain form=new FormMain(request);
form.setAllInstruments(true);
form.setOrderBy(new Integer(3));

// Fijamos un limite de resultados de 50
form.setRpp(new Integer(50));


String prog_id=form.getProgId();
String obl_id=form.getOblId();
String filename=form.getFilename();


if(prog_id==null){
	prog_id="";
	if(form.getOrigen()!=null && form.getOrigen().equals("searchRawProduct.jsp")) MSG+="No program provided.<br />";              
}
if(obl_id==null){
	obl_id="";
	if(form.getOrigen()!=null && form.getOrigen().equals("searchRawProduct.jsp")) MSG+="No observing block provided.<br />";
}
if(filename==null){
	filename="";
}


//---- Elementos estáticos de la página
Html elementosHtml = new Html(request.getContextPath());
String cabeceraPagina = elementosHtml.cabeceraPagina("Insert reduced data","Search Raw Product");
String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath());
String pie            = elementosHtml.pie();



WebMainSearch busqueda = null;
if(prog_id!="" && obl_id!=""){
	busqueda = new WebMainSearch(conex, form, contenedorSesion.getUser());
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

<p class="appstyle salto">Please, find the corresponding RAW product:</p>

<%if(MSG.length()>0){ %>
<p class="appstyle salto aviso"><%= MSG %></p>
<%} %>

<form name="form_search" method="post" action="searchRawProduct.jsp">
<input type="hidden" name="origen" value="searchRawProduct.jsp"></input>

<p class="appstyle salto" style="font-size:12pt;">Program:</p>
<table class="appstyle" border="0" CELLSPACING="5" WIDTH="640em" bgcolor="#EEEEEE">
	<tr>
		<td width="100em">
			Program ID:
		</td>
		<td>
			<input type="text" name="prog_id" value="<%=prog_id %>" size="20">
		</td>
		<td rowspan="3" valign="center">
			<p class="appstyle" style="font-weight:normal">% caracter may be used as wildcard.</p>
		</td>
	</tr>
	<tr>
		<td>
			Obs. Block:
		</td>
		<td>
			<input type="text" name="obl_id" value="<%=obl_id %>"  size="20">
		</td>
	</tr>
	<tr>
		<td>
			Filename:
		</td>
		<td>
			<input type="text" name="filename" value="<%=filename %>"  size="20">
		</td>
	</tr>
</table>

<p class="appstyle salto"><input type="submit" name="submit" value="Search">&nbsp;&nbsp;&nbsp;
	<a href="<%= request.getRequestURI() %>"><u>Reset</u></a></p>
</form>

<%if(busqueda!=null){ %>
<form name="form_search" method="post" action="insertReduced.jsp">
<input type="hidden" name="origen" value="searchRawProduct.jsp"></input>

<p class="appstyle salto"></p>

<!--   Tabla de resultados -->			
<table cellspacing="3" WIDTH="800px" border="0" >
	<tr>
		<td class="rescab" align="center" nowrap>
			Program ID
		</td>
		<td class="rescab" align="center" nowrap>
			O.Block
		</td>
		<td class="rescab" align="center" nowrap>
			Prod ID
		</td>
		<td class="rescab" align="center" nowrap>
			Instr.
		</td>
		<td class="rescab" align="center" nowrap>
			Obs. Mode
		</td>
		<td class="rescab" align="center" nowrap>
			File Name
		</td>
		<td class="rescab" align="center" nowrap>
		</td>
	</tr>
	<%if(busqueda.countResults()<=0){ %>
	<tr>
		<td class="resfield" align="center" colspan="7">No files found.</td>
	</tr>
	<%} %>
<% 
WebMainResult resultado=null;
while( (resultado=busqueda.getNext())!=null ){
	//String urlAladin70 = AladinApplet.getAladinAppletURL("http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/FetchProd?prod_id="+resultado.getProdId().intValue());
	boolean  hasPerm=true;
	String prod_id = WebUtils.generaWebProdId(resultado.getProgId(), resultado.getOblId(), resultado.getProdId());
	String urlAladin70 = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/jsp/previewProd.jsp?prod_id="+prod_id;
%>
	<tr>
	<%if(hasPerm){ %>
		<%-- 
		<td class="resfield" nowrap align="left"><!-- Object ID -->
			<a href="JavaScript:ventana_equiv_names('<%= resultado.getObjId()%>')"><%=resultado.getObject() %></a>
		</td>
		--%>
		<td class="resfield" nowrap align="left"><!-- Program ID -->
			<%=resultado.getProgId() %>
		</td>
		<td class="resfield" nowrap align="left"><!-- OBlock ID -->
			<%=resultado.getOblId() %>
		</td>
		<td class="resfield" nowrap align="left"><!-- ProdId -->
			<%=resultado.getProdId().intValue()%>
		</td>
		<td class="resfield" nowrap align="left"><!-- Instrument -->
			<%=resultado.getInsName() %>
		</td>
		<td class="resfield" nowrap align="left" id="mode" 
			onmouseover="Tip('<%=resultado.getModShortname()%>')" 
			onmouseout="UnTip()"><!-- Obs Mode -->
			<%=resultado.getModId() %>
		</td>
		<td class="resfield" nowrap align="left"><!-- Filename -->
			<%=resultado.getFileName().substring(0,resultado.getFileName().indexOf(".gz")) %>
		</td>
		<td class="resfield" style="padding:0.5em 0.5em 0.5em 0.5em" nowrap><!-- Boton -->
			<%-- <input type="submit" name="submit" value="Upload reduced file"> --%>
			<a href="insertReducedProduct.jsp?prod_id=<%=prod_id %>">Upload reduced file </a>
		</td>
	<%}else{ %>
		<td class="resfield" nowrap align="left" colspan="6"><!-- No hay permisos -->
			<font color="red">Private data untill XXX</font>
		</td>
	<%} %>
	</tr>
<%} // fin del While que recorre los resultados. %>	
</table>

</form>
<%} %>


<%= pie %>
</body>
</html>

<%
	// Cierre de la conexión a la base de datos
    //System.out.println("res_busqueda.jsp: Conexion con la BD cerrada.");
	conex.close();
%>
