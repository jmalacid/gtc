<%@ page import="svo.gtc.web.Html,
				svo.gtc.web.ContenedorSesion,
				svo.gtc.db.prodat.ProdDatosAccess,
				java.sql.*,
				java.net.URLEncoder,
				java.util.TimeZone,
				java.util.Locale,
				java.util.Vector,
				java.util.Enumeration,
				java.util.Calendar,
				javax.sql.*,
				javax.naming.Context,
				javax.naming.InitialContext,
				javax.naming.NamingException" %>
                 
<%@ page isThreadSafe="true" %>
<%@ page info="GSA Search" %>


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

//Abrimos la conexión a la base de datos
Context initContext;
Connection cn=null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();

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




ProdDatosAccess prodDatosAccess = new ProdDatosAccess(cn);
Timestamp firstDate = prodDatosAccess.getFirstDate();
Timestamp lastDate = prodDatosAccess.getLastDate();

Calendar cal = Calendar.getInstance();

cal.setTime(firstDate);
int day1   = cal.get(Calendar.DAY_OF_MONTH);
int month1 = cal.get(Calendar.MONTH)+1;
int year1  = cal.get(Calendar.YEAR);

cal.setTime(lastDate);
int day2   = cal.get(Calendar.DAY_OF_MONTH);
int month2 = cal.get(Calendar.MONTH)+1;
int year2  = cal.get(Calendar.YEAR);

String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};

String camposOptionMeses1="";
String camposOptionMeses2="";

for(int i=1; i<=12; i++){
	String selected="";
	if(i==month1) selected="SELECTED";
	camposOptionMeses1+="<option value=\""+i+"\" "+selected+">"+months[i-1]+"</option>\n";

	selected="";
	if(i==month2) selected="SELECTED";
	camposOptionMeses2+="<option value=\""+i+"\" "+selected+">"+months[i-1]+"</option>\n";
}

String camposOptionAnos1  = "";
String camposOptionAnos2  = "";
for(int i=year2; i>=year1; i--){
	String selected="";
	if(i==year1) selected="SELECTED";
	camposOptionAnos1 += "<option value=\""+i+"\" "+selected+">"+i+"</option>\n"; 

	selected="";
	if(i==year2) selected="SELECTED";
	camposOptionAnos2 += "<option value=\""+i+"\">"+i+"</option>\n"; 
}

  
//---- Elementos estáticos de la página
Html elementosHtml = new Html(request.getContextPath());
String cabeceraPagina = elementosHtml.cabeceraPagina("Search form","Principal search page for GTC Scientific Archive");
String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath());
String pie            = elementosHtml.pie();


String bib = request.getParameter("bib");
String stateant = request.getParameter("stateant");
%>

<%--------     I N I C I O    P Á G I N A    H T M L    -------------------%>
<html>
<%= cabeceraPagina %>
<script>

function selecSpe(){
if(document.form_search.elements[13].checked==1){
	for (i=14;i<20;i++)	
			document.form_search.elements[i].checked=1
			
}
if (document.form_search.elements[13].checked==0){
	for (i=14;i<20;i++)	
			document.form_search.elements[i].checked=0
			
}
}

</script>

<script>

function selecImg(){
if(document.form_search.elements[20].checked==1){
	for (i=21;i<30;i++)	
			document.form_search.elements[i].checked=1
			
}
if (document.form_search.elements[20].checked==0){
	for (i=21;i<30;i++)	
			document.form_search.elements[i].checked=0
			
}
}

</script>

<script>

function selecPol(){
	
if(document.form_search.elements[30].checked==1){
	for (i=31;i<33;i++)	
			document.form_search.elements[i].checked=1
			
}
if (document.form_search.elements[30].checked==0){
	for (i=31;i<33;i++)	
			document.form_search.elements[i].checked=0
			
}
}

</script>

<body>
<%= encabezamiento %>
<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px" >

<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER><font face="arial,helvetica,san-serif"><font
color="#FFFFFF"><font size=+2>&nbsp;The Gran Telescopio CANARIAS Archive<br><font color=red>-Private zone-</font></font></font></font>
</td>
</tr>
</table>
<table width="800" cellspacing="0" cellpadding="2" border="0">
<tbody>
<tr>
<td class="headtitle">
<center>GTC Archive: Search of Products for <%=bib %></center>
</td>
</tr>
</tbody>
</table>

<img src="../pieces/nada50x1.gif" alt=""><p class="appstyle1"><blink><%= MSG %></blink></p><br>

<form name="form_search" method="post" action="addprods2.jsp" enctype="multipart/form-data">
<input type="hidden" name="origen" value="addprods1.jsp"></input><input type="hidden" name="bib" value="<%=bib%>"></input><input type="hidden" name="stateant" value="<%=stateant%>"></input>

<p class="appstyle" style="font-size:12pt;">Target Information:</p>

<table CELLSPACING="5" WIDTH="640px" bgcolor="#EEEEEE">
	<tr>
		<td colspan="2">
			<table>
				<tr>
					<td valign="top">
						<p class="appstyle">Object List:&nbsp;&nbsp;&nbsp;</p>
					</td>
					<td>
						<textarea name="obj_list" rows="6" cols="20"></textarea>
					</td>
					<td><p class="appstyle">Examples (coordinates in J2000):&nbsp;&nbsp;&nbsp;</p>
					<p class="appstyle">64.99061  52.98401</p>
					<p class="appstyle">4 19 57.75 +52 59 02.436</p>
					<p class="appstyle">4:19:57.75 +52:59:02.436</p>
					<p class="appstyle">ULAS J135058.86+081506.8</p>
					
					</td>
				</tr>
				<tr>
					<td valign="top">
						<p class="appstyle">&nbsp;</p>
					</td>
					<td colspan="2">
						<input type="file" name="file">
					</td>
				</tr>
				<tr>
					<td valign="top">
						<p class="appstyle">Search radius:&nbsp;&nbsp;&nbsp;</p>
					</td>
					<td>
						<input name="radius" size="3" value="5"></input> &nbsp; <font class="appstyle" style="font-weight:normal;">arcmin</font>
					</td>
					<td>&nbsp;</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<br></br>
<p class="appstyle" style="font-size:12pt;">Date:</p>
<%-- <table CELLSPACING="5" WIDTH="640px" bgcolor="#f6d8d8">--%>
<table border="0" CELLSPACING="5" WIDTH="640px" bgcolor="#EEEEEE">
	<tr>
		<td>
			<p class="appstyle">Between:</p>
		</td>
		<td>
			<table border="0" CELLSPACING="3" >
				<tr>
					<td nowrap>
						<input type="text" name="dateinit_d" size="2" maxlength="2" value="<%=day1 %>">&nbsp;
						<select name="dateinit_m">
							<%= camposOptionMeses1 %>
						</select>
						<select name="dateinit_y">
							<%= camposOptionAnos1 %>
						</select>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<p class="appstyle">And:</p>
		</td>
		<td>
			<table border="0" CELLSPACING="3" >
				<tr>
					<td nowrap>
						<input type="text" name="dateend_d" size="2" maxlength="2" value="<%=day2 %>">&nbsp;
						<select name="dateend_m">
							<%= camposOptionMeses2 %>
						</select>
						<select name="dateend_y">
							<%= camposOptionAnos2 %>
						</select>
						
					</td>
				</tr>
			</table>
		</td>	
	</tr>

</table>

<br></br>
<p class="appstyle" style="font-size:12pt;">Instrumentation:</p>
<p class="appstyle">Only reduced data: <input type="checkbox" name="onlyRed" value="true"></p>
<table border="0" CELLSPACING="5" WIDTH="640px" bgcolor="#cff9cf">
	<tr>
		<td>
			<p class="appstyle"><input type="checkbox" name="markAll"  checked onclick="javascript:selecSpe()">Spectroscopy:</p>
		</td>
	</tr>
	<tr>
		<td>
			<table border="0" CELLSPACING="3" >
				<tr>
					<td nowrap>
						<input type="checkbox" name="instCode" value="OSI_LSS" checked>
						<font class="appstyle" style="font-weight:normal">OSIRIS Long Slit</font>
					</td>
				</tr>
				<tr>
					<td nowrap>
						<input type="checkbox" name="instCode" value="OSI_MOS" checked>
						<font class="appstyle" style="font-weight:normal">OSIRIS Multi-Object Spectroscopy</font>
					</td>
				</tr>
				<tr>
					<td nowrap>
						<input type="checkbox" name="instCode" value="CC_SPE" checked>
						<font class="appstyle" style="font-weight:normal">CANARICAM Spectroscopy</font>
					</td>
				</tr>
				<tr>
					<td nowrap>
						<input type="checkbox" name="instCode" value="EMIR_SPE" checked>
						<font class="appstyle" style="font-weight:normal">EMIR Spectroscopy</font>
					</td>
				</tr>
				<tr>
					<td nowrap>
						<input type="checkbox" name="instCode" value="MEG_SPE" checked>
						<font class="appstyle" style="font-weight:normal">MEGARA MOS</font>
					</td>
				</tr>
				<tr>
					<td nowrap>
						<input type="checkbox" name="instCode" value="HORuS_SPE" checked>
						<font class="appstyle" style="font-weight:normal">HORuS Spectroscopy</font>
					</td>
				</tr>
				 
			</table>
		</td>
	</tr>
</table>
<table border="0" CELLSPACING="5" WIDTH="640px" bgcolor="#cff9cf">
	<tr>
		<td>
			<p class="appstyle"><input type="checkbox" name="markAll"  checked onclick="javascript:selecImg()">Imaging:</p>
		</td>
	</tr>
	<tr>
		<td>
			<table border="0" CELLSPACING="3" >
				<tr>
					<td nowrap>
						<input type="checkbox" name="instCode" value="OSI_BBI" checked>
						<font class="appstyle" style="font-weight:normal">OSIRIS Broad Band Photometry</font>
					</td>
				</tr>
				<tr>
					<td nowrap>
						<input type="checkbox" name="instCode" value="OSI_TF" checked>
						<font class="appstyle" style="font-weight:normal">OSIRIS Tunable Filters</font>
					</td>
				</tr>
				<tr>
					<td nowrap>
						<input type="checkbox" name="instCode" value="CC_IMG" checked>
						<font class="appstyle" style="font-weight:normal">CANARICAM Imaging</font>
					</td>
				</tr> 
				<tr>
					<td nowrap>
						<input type="checkbox" name="instCode" value="CIR_IMG" checked>
						<font class="appstyle" style="font-weight:normal">CIRCE Imaging</font>
					</td>
				</tr> 
				<tr>
					<td nowrap>
						<input type="checkbox" name="instCode" value="EMIR_BBI" checked>
						<font class="appstyle" style="font-weight:normal">EMIR Broad Band Imaging</font>
					</td>
				</tr> 
				<tr>
					<td nowrap>
						<input type="checkbox" name="instCode" value="EMIR_MBI" checked>
						<font class="appstyle" style="font-weight:normal">EMIR Medium Band Imaging</font>
					</td>
				</tr> 
				<tr>
					<td nowrap>
						<input type="checkbox" name="instCode" value="EMIR_NBI" checked>
						<font class="appstyle" style="font-weight:normal">EMIR Narrow Band Imaging</font>
					</td>
				</tr> 
				<tr>
					<td nowrap>
						<input type="checkbox" name="instCode" value="HIP_IMG" checked>
						<font class="appstyle" style="font-weight:normal">HiPERCAM Imaging</font>
					</td>
				</tr> 
				<tr>
					<td nowrap>
						<input type="checkbox" name="instCode" value="MEG_IMG" checked>
						<font class="appstyle" style="font-weight:normal">MEGARA Imaging</font>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<table border="0" CELLSPACING="5" WIDTH="640px" bgcolor="#cff9cf">
	<tr>
		<td>
			<p class="appstyle"><input type="checkbox" name="markAll"  checked onclick="javascript:selecPol()">Polarimetry:</p>
		</td>
	</tr>
	<tr>
		<td>
			<table border="0" CELLSPACING="3" >
				 <tr>
					<td nowrap>
						<input type="checkbox" name="instCode" value="CC_POL" checked>
						<font class="appstyle" style="font-weight:normal">CANARICAM Polarimetry</font>
					</td>
				</tr>  
				<tr>
					<td nowrap>
						<input type="checkbox" name="instCode" value="CIR_POL" checked>
						<font class="appstyle" style="font-weight:normal">CIRCE Polarimetry</font>
					</td>
				</tr> 
			</table>
		</td>
	</tr>
</table>
<br></br>
<p class="appstyle" style="font-size:12pt;">Program:</p>
<table border="0" CELLSPACING="5" WIDTH="640px" bgcolor="#EEEEEE">
	<tr>
		<td width="100px">
			<p class="appstyle">Program ID:</p>
		</td>
		<td>
			<input type="text" name="prog_id" value="" size="20">
		</td>
		<td rowspan="2" valign="center">
			<p class="appstyle" style="font-weight:normal">% caracter may be used as wildcard.</p>
		</td>
	</tr>
	<tr>
		<td>
			<p class="appstyle">Obs. Block:</p>
		</td>
		<td>
			<input type="text" name="obl_id" value=""  size="20">
		</td>
	</tr>
</table>


<br></br>
<p class="appstyle" style="font-size:12pt;">Order:</p>
<table border="0" CELLSPACING="5" WIDTH="640px" bgcolor="#EEEEEE">
	<tr>
		<td width="100px">
			<p class="appstyle">Order by:</p>
		</td>
		<td>
			<select name="order_by">
				<option value="0">Observing Date</option>
				<option value="1">Instrument</option>
				<option value="2">Program ID / Obs. Block</option>
				<option value="3">Prod ID.</option>
			</select>
		</td>
	</tr>
</table>

<br></br>
<p class="appstyle" style="font-size:12pt;">Pages:</p>
<table border="0" CELLSPACING="5" WIDTH="640px" bgcolor="#EEEEEE">
	<tr>
		<td width="150px" nowrap>
			<p class="appstyle">Results per page:</p>
		</td>
		<td>
			<select name="rpp">
				<option value="10">10</option>
				<option value="50">50</option>
				<option value="100">100</option>
				<option value="0" selected>ALL</option>
			</select>
		</td>
		<td width="100px">
			<p class="appstyle">Page to show:</p>
		</td>
		<td>
			<input type="text" name="pts" value="1" size="4">
		</td>
	</tr>
</table>

<br></br>
<p class="appstyle"><input type="submit" name="submit" value="Send">&nbsp;&nbsp;&nbsp;
	<a href="<%= request.getRequestURI() %>"><u>Reset</u></a></p>


</form>

<%= pie %>
</body>
</html>

<%
}catch(SQLException ex){
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
}
%>