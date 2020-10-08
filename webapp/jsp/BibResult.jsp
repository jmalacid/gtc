<%@ page import="svo.gtc.web.Html,
svo.gtc.web.ContenedorSesion,
svo.gtc.db.web.BibResult,
svo.gtc.db.web.WebMainResult,
svo.gtc.db.logquery.LogQueryDb,
svo.gtc.db.logquery.LogQueryAccess,
svo.gtc.priv.objetos.ReducInfo,
svo.gtc.web.AladinApplet,java.sql.*,
java.net.URLEncoder,
java.util.Date,
utiles.StringUtils,
svo.gtc.db.priv.DBPrivate,

javax.sql.*,
javax.naming.Context,
javax.naming.InitialContext,
javax.naming.NamingException" %>
                 
<%@ page isThreadSafe="true" %>
<%@ page info="GTC Search" %>


<%-- P  R  O  C  E  S  O  S --%>
<%
//Abrimos la conexi�n a la base de datos
Context initContext;
Connection cn=null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();


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

String bibcode = null;

try{
	bibcode=request.getParameter("bib");
}catch(Exception e){
	MSG="Unable to process request.";
	e.printStackTrace();
}


if(bibcode!=null){
	
	session.setAttribute("contenedorSesion",contenedorSesion);
	
}

if(bibcode==null ){
	MSG = "No search conditions";
}




//*************************************************************
//*  BUSQUEDA                                                 *
//*************************************************************

BibResult resultados=null;
int cuentaResultados = 0;
int pts = 1;
int rpp = 10;
int maxPages = 0;

try{
	pts = Integer.valueOf(request.getParameter("pts"));
	rpp = Integer.valueOf(request.getParameter("rpp"));
}catch(Exception e){
	//Se quedan los valores por defecto
}

if(MSG.length()==0){
	resultados = new BibResult(cn, bibcode, rpp, pts);
	cuentaResultados = resultados.countResults();
	
	/// Calculamos si la p�gina est� dentro de rango.

	maxPages = (int)Math.ceil((float)cuentaResultados/(float)rpp);

	if(pts>maxPages){
		pts=maxPages;
	}else if(pts<1){
		pts=1;
	}
	
	
	//*************************************************************
	//*  ESTADISTICAS                                             *
	//*************************************************************
	LogQueryDb logQuery = new LogQueryDb();
	logQuery.setLogqTime(new Timestamp((new Date()).getTime()));
	logQuery.setLogqHost(request.getRemoteHost());
	logQuery.setLogqType("WEB");
	LogQueryAccess logqAccess = new LogQueryAccess(cn);
	logqAccess.insert(logQuery);
}




// *************************************************************
// *  TECLAS DEL FORMULARIO                                    *
// *************************************************************

	//---- Elementos est�ticos de la p�gina
	Html elementosHtml = new Html(request.getContextPath());
	String cabeceraPagina = elementosHtml.cabeceraPagina("Search Results","Search results page for GTC Archive.");
	String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath());
	String pie            = elementosHtml.pie();
%>

<%--------     I N I C I O    P � G I N A    H T M L    -------------------%>
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
<!--   Tabla Numero de objetos -->
<table cellspacing="0" cellpadding="4" class="downloadall">
	<tr>
		<td nowrap>
				<p class="downloadall">
				<%=cuentaResultados%> Product<%
					if(cuentaResultados!=1){
				%>s<%
					}
				%> found 
				matching your criteria &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</p>
		</td>
	</tr>
</table>


<%-----  SECCION QUE SOLO SE PRESENTA SI HAY RESULTADOS  ------%>
<%
	if (cuentaResultados >0){
%>
<p>
<br>
</p>

<%--------  FORMULARIO DE PROCESADO M�LTIPLE  -------------------%>
<!--   Tabla procesado m�ltiple -->
<form name="form_fetch_multiple" method="POST" action="fetchDataBlock.jsp">
<table cellspacing="0" cellpadding="4" class="downloadall">
	<tr>
		<td nowrap>
				<p class="downloadall">
				<input type="submit" value="Download selected">
				&nbsp;in&nbsp;
				<select>
					<option class="goption" value="zip">zip</option>
				</select>
				&nbsp;format
				</p>
		</td>
	</tr>
</table>

<p>
<br>
</p>
<table class="appstyle" border="0" cellpadding="5" cellspacing="0">   
<tr>       
	<td>Page <%=pts%> of <%=maxPages%></td>
	<td>&nbsp;</td>
	<%
		if(pts>1){
	%><td bgcolor="#EEEEEE"><a href="BibResult.jsp?bib=<%=bibcode %>&pts=<%=pts-1%>">&laquo; Prev.</a></td><%
		}
	%>
	<%
		if(pts<maxPages){
	%><td bgcolor="#EEEEEE"><a href="BibResult.jsp?bib=<%=bibcode %>&pts=<%=pts+1%>">Next &raquo;</a></td><%
		}
	%>
</tr> 
</table>

<!--   Tabla de resultados -->			
<table cellspacing="3" WIDTH="800px" border="0" >
	<tr>
		<%--
		<td class="rescab" align="center" nowrap>
			<p>Object</p>
		</td>
		--%>	
		<td class="rescab" align="center" nowrap>
			Prod ID
		</td>
		<td class="rescab" align="center" nowrap>
			Program ID
		</td>
		<td class="rescab" align="center" nowrap>
			O.Block
		</td>
		<td class="rescab" align="center" nowrap>
			RA (deg) <br> J2000
		</td>
		<td class="rescab" align="center" nowrap>
			DEC (deg) <br> J2000
		</td>
		<td class="rescab" align="center" nowrap>
			Instr.
		</td>
		<td class="rescab" align="center">
			Obs. Mode
		</td>
		<td class="rescab" align="center" nowrap>
			Init.Time
		</td>
		<td class="rescab" align="center" nowrap>
			End Time
		</td>
		<td class="rescab" align="center" nowrap>
			Airmass
		</td>
		<td class="rescab" style="background-color: #40FF00" align="center" nowrap>
			Pub
		</td>
		<td class="rescab" style="background-color: darkorange" colspan="3" align="center" nowrap>
			Reduc. Data <input name="markRed" type="checkbox" onClick="mark_red(document.form_fetch_multiple)"></input>
		</td> 
		<td class="rescab" colspan="3" align="center" nowrap>
			Raw Data <input name="markRaw" type="checkbox" onClick="mark_raw(document.form_fetch_multiple)"></input>
		</td>
		<td class="rescab" colspan="2" align="center" nowrap>
			Cal. Files <input name="markCal" type="checkbox" onClick="mark_cal(document.form_fetch_multiple)"></input>
		</td> 
		<td class="rescab" colspan="2" align="center" nowrap>
			Acq. Images <input name="markAc" type="checkbox" onClick="mark_ac(document.form_fetch_multiple)"></input>
		</td>
		<td class="rescab" colspan="2" align="center" nowrap>
			QC Files <input name="markLog" type="checkbox" onClick="mark_log(document.form_fetch_multiple)"></input>
		</td>
		<%-- 
		<td class="rescab" colspan="2" align="center" nowrap>
			Err. Files <input name="markLog" type="checkbox" onClick="mark_err(document.form_fetch_multiple)"></input>
		</td>
		--%>
		
	</tr>


<%
	WebMainResult resultado=null;
while( (resultado=resultados.getNext())!=null ){
	resultado.setCountCal(resultados.getCountCal(resultado.getProgId(), resultado.getOblId()));
	resultado.setCountWarn(resultados.getCountWarn(resultado.getProgId(), resultado.getOblId()));

	//String urlAladin70 = AladinApplet.getAladinAppletURL("http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/FetchProd?prod_id="+resultado.getProdId().intValue());
	boolean  hasPerm=true;
	
	String prod_id = resultado.getProgId()+".."+resultado.getOblId()+".."+resultado.getProdId().intValue();

	String urlAladin70 = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/jsp/previewProd.jsp?prod_id="+prod_id;
%>
	
	<tr>
	<%if(hasPerm){ %>
		<%-- 
		<td class="resfield" nowrap align="left"><!-- Object ID -->
			<a href="JavaScript:ventana_equiv_names('<%= resultado.getObjId()%>')"><%=resultado.getObject() %></a>
		</td>
		--%>
		<td class="resfield" nowrap align="left"><!-- ProdId -->
			<%=resultado.getProdId().intValue()%>
		</td>
		<td class="resfield" nowrap align="left"><!-- Program ID -->
			<%=resultado.getProgId() %>
		</td>
		<td class="resfield" nowrap align="left"><!-- OBlock ID -->
			<%=resultado.getOblId() %>
		</td>
		<td class="resfield" nowrap align="right"><!-- RA -->
			<%=resultado.getFormatedProdRa() %>
		</td>
		<td class="resfield" nowrap align="right"><!-- DEC -->
			<%=resultado.getFormatedProdDe() %>
		</td>
	<%}else{ %>
		<td class="resfield" nowrap align="left" colspan="5"><!-- No hay permisos -->
			<font color="red">Private data untill XXX</font>
		</td>
	<%} %>
		<td class="resfield" nowrap align="left"><!-- Instrument -->
			<%=resultado.getInsName() %>
		</td>
		<td class="resfield" nowrap align="left" id="mode" 
			onmouseover="Tip('<%=resultado.getModShortname()%>')" 
			onmouseout="UnTip()"><!-- Obs Mode -->
			<%=resultado.getModId() %>
		</td>
		<td class="resfield" nowrap align="center"><!-- Init Time -->
			<%=resultado.getFormatedProdInitime() %>
		</td>
		<td class="resfield" nowrap align="center"><!-- End Time -->
			<%=resultado.getFormatedProdEndtime() %>
		</td>
		<td class="resfield" nowrap align="right"><!-- Airmass -->
			<%=resultado.getFormatedProdAirmass() %>
		</td>
		<td class="resfield" nowrap align="center"><!-- Pub -->
			if(resultado.getPub()>0){%>
				<a href="pub_red.jsp?prog=<%=resultado.getProgId() %>&obl=<%=resultado.getOblId() %>&prod=<%=resultado.getProdId() %>" target="blank"><%=resultado.getPub() %></a>
			<%	
			%>
		</td>
		
	<td class="resfield" nowrap align="center"><!-- View Reduc. Pub -->
		<%if(resultado.getCountRed()>0){
			//Comprobamos si hay alguna publicaci�n relacionada
			DBPrivate conex1 = new DBPrivate(cn);
			//String[] pub = conex1.selectPub(resultado.getProgId(), resultado.getOblId(), resultado.getProdId());
			ReducInfo red = conex1.RedInfo(resultado.getProgId(), resultado.getOblId(), resultado.getProdId());
			
		
			if(resultado.getInsName()!=null && resultado.getInsName().equals("CANARICAM")){%>
				<a target="_blank" href="http://www.gtc.iac.es/instruments/canaricam/canaricam.php#Calibrations">INFO</a>
			<%}else{ %>
				<a target="_blank" href="http://adsabs.harvard.edu/abs/<%=red.getBibcode().trim()%>">ADS</a>
		<%	}
		}%>
	</td>
	<td class="resfield" nowrap align="center"><!-- View Reduc. Files -->
		<%if(resultado.getCountRed()>0){ %>
		<a href="viewprod.jsp?prod_id=<%=prod_id%>&modtype=RED">View</a>
		<%} %>
	</td>
	<td class="resfield" nowrap align="center"><!-- Fetch Reduc. Files -->
		<table>
			<tr>
				<td  class="resfield" nowrap>
				<%if(resultado.getCountRed()>0){ %>
					<a href="fetchDataBlock.jsp?datablock=<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getProdId()%>__ALLRED">Fetch</a>
				<%} %>
				</td>
				<td>
				<%if(resultado.getCountRed()>0){ %>
					<input name="datablock" value="<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getProdId()%>__ALLRED" type="checkbox">
				<%} %>
				</td>
			</tr>
		</table>
	</td>
		
	<%if(hasPerm){ %>
		<td class="resfield" nowrap align="center"><!-- View Header -->
			<a target="_blank" href="viewprodheader.jsp?prod_id=<%=prod_id%>">Header</a>
		</td>
		<td class="resfield" nowrap align="center"><!-- Preview Data -->
			<%if(resultado.getInsName()!= null && resultado.getInsName().equals("CANARICAM")){%>
				-
			<%}else{ %>
				<a target="_blank" href="<%= urlAladin70 %>">Preview</a>
			<%} %>
		</td>
		<td class="resfield" nowrap align="center"><!-- Fetch Spectra -->
			<table>
				<tr>
					<td  class="resfield" nowrap>
						<a href="<%=request.getContextPath() %>/servlet/FetchProd?prod_id=<%=prod_id%>">Fetch</a>
						
					</td>
					<td>
						<input name="prod_id" value="<%=prod_id%>" type="checkbox">

					</td>
				</tr>
			</table>
		</td>
	<%}else{ %>
		<td class="resfield" nowrap align="center" colspan="2">
			<font color="red">Private</font>
		</td>
	<%} %>
	<%if(hasPerm){ %>
		<td class="resfield" nowrap align="center"><!-- View Calib. Files -->
			<%if(resultado.getCountCal()>0){ %>
			<a href="viewprod.jsp?prod_id=<%=prod_id%>&modtype=CAL">View</a>
			<%} %>
		</td>
		<td class="resfield" nowrap align="center"><!-- Fetch Calib. Files -->
			<table>
				<tr>
					<td  class="resfield" nowrap>
					<%if(resultado.getCountCal()>0){ %>
						<a href="fetchDataBlock.jsp?datablock=<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getInsId()%>__ALLCAL">Fetch</a>
					<%} %>
					</td>
					<td>
					<%if(resultado.getCountCal()>0){ %>
						<input name="datablock" value="<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getInsId()%>__ALLCAL" type="checkbox">
					<%} %>
					</td>
				</tr>
			</table>
		</td>
	<%}else{ %>
		<td class="resfield" nowrap align="center">
			<font color="red">Private</font>
		</td>
	<%} %>

	<%if(hasPerm && resultado.getModId().equalsIgnoreCase("LSS")){ %>
		<td class="resfield" nowrap align="center"><!-- View Acquisition Images -->
			<a href="viewprod.jsp?prod_id=<%=prod_id%>&modtype=AC">View</a>
		</td>
		<td class="resfield" nowrap align="center"><!-- Fetch Acquisition Images -->
			<table>
				<tr>
					<td  class="resfield" nowrap>
						<a href="fetchDataBlock.jsp?datablock=<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getInsId()%>__ALLAC">Fetch</a>
					</td>
					<td>
						<input name="datablock" value="<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getInsId()%>__ALLAC" type="checkbox">

					</td>
				</tr>
			</table>
		</td>
	<%}else{ %>
		<td class="resfield" nowrap align="center">
			<font color="red">&nbsp;</font>
		</td>
		<td class="resfield" nowrap align="center">
			<font color="red">&nbsp;</font>
		</td>
	<%} %>

	<%if(hasPerm){ %>
		<td class="resfield" nowrap align="center"><!-- View Log Files -->
			<%if(resultado.getCountWarn()>0){ %>
			<a href="viewprod.jsp?prod_id=<%=prod_id%>&modtype=LOG">View</a>
			<%} %>
		</td>
		<td class="resfield" nowrap align="center"><!-- Fetch Log Files -->
			<table>
				<tr>
					<td  class="resfield" nowrap>
						<%if(resultado.getCountWarn()>0){ %>
							<a href="fetchDataBlock.jsp?datablock=<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getInsId()%>__ALLLOG">Fetch</a>
						<%} %>
					</td>
					<td>
						<%if(resultado.getCountWarn()>0){ %>
						<input name="datablock" value="<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getInsId()%>__ALLLOG" type="checkbox">
						<%} %>
					</td>
				</tr>
			</table>
		</td>
	<%}else{ %>
		<td class="resfield" nowrap align="center">
			<font color="red">Private</font>
		</td>
	<%} %>

	</tr>
<%} // fin del While que recorre los resultados. %>	
			
</table>

<table class="appstyle" border="0" cellpadding="5" cellspacing="0">   
<tr>       
	<td>Page <%=pts%> of <%=maxPages%></td>
	<td>&nbsp;</td>
	<%
		if(pts>1){
	%><td bgcolor="#EEEEEE"><a href="BibResult.jsp?bib=<%=bibcode %>&pts=<%=pts-1%>">&laquo; Prev.</a></td><%
		}
	%>
	<%
		if(pts<maxPages){
	%><td bgcolor="#EEEEEE"><a href="BibResult.jsp?bib=<%=bibcode %>&pts=<%=pts+1%>">Next &raquo;</a></td><%
		}
	%>
</tr> 
</table>

<p>
<br>
</p>

</form>
<%--------  FIN FORMULARIO DE DESCARGA M�LTIPLE  -------------------%>

<%-----  FIN SECCION QUE SOLO SE PRESENTA SI HAY RESULTADOS  ------%>
<% } %> 
<%-----  FIN DEL ELSE ERROR P�GINA  ------%>
<% } %>


<p>
<br>
</p>

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