<%@ page import="svo.gtc.web.Html,
svo.gtc.web.FormMain,
svo.gtc.web.ContenedorSesion,
svo.gtc.web.FormParameterException,
svo.gtc.db.web.WebMainSearch,
svo.gtc.db.web.WebMainResult,
svo.gtc.db.logquery.LogQueryDb,
svo.gtc.db.logquery.LogQueryAccess,
svo.gtc.web.AladinApplet,java.sql.*,
java.net.URLEncoder,
java.util.Date,
utiles.StringUtils,
svo.gtc.db.priv.DBPrivate,
svo.gtc.db.permisos.ControlAcceso,

javax.sql.*,
javax.naming.Context,
javax.naming.InitialContext,
javax.naming.NamingException" %>
                 
<%@ page isThreadSafe="true" %>
<%@ page info="GTC Search" %>

<%-- P  R  O  C  E  S  O  S --%>
<%
//Abrimos la conexión a la base de datos
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
String WAR="";

FormMain formulario = null;

try{
	formulario=new FormMain(request);
}catch(Exception e){
	MSG="Unable to process request.";
	e.printStackTrace();
}


if(formulario!=null && formulario.getOrigen()!=null && formulario.getOrigen().equals("searchform.jsp")){
	contenedorSesion.setFormulario(formulario);
	session.setAttribute("contenedorSesion",contenedorSesion);
	if(formulario.getErrors().length>0){
		String[] errores = formulario.getErrors();
		for(int i=0; i<errores.length; i++){
			MSG+=errores[i]+"<br>";
		}
	}
	if(formulario.getWarning().length>0){
		String[] warning = formulario.getWarning();
		for(int i=0; i<warning.length; i++){
			WAR+=warning[i]+"<br>";
		}
	}
}else{
	if(contenedorSesion.getFormulario()!=null){
		formulario = contenedorSesion.getFormulario();
		try{
	formulario.procesaRequest(request);
	contenedorSesion.setFormulario(formulario);
	session.setAttribute("contenedorSesion",contenedorSesion);
		}catch(Exception e){
	MSG=e.getMessage();	
	e.printStackTrace();
		}
	}
}

if(MSG.length()==0 && (formulario==null || !formulario.isValid())){
	MSG = "No search conditions";
}

if(formulario==null){
	formulario = new FormMain();
}



//*************************************************************
//*  BUSQUEDA                                                 *
//*************************************************************

WebMainSearch resultados=null;
int cuentaResultados = 0;
int pts = 1;
int rpp = 10;

if(formulario!=null && formulario.getPts()!=null) pts = formulario.getPts();
if(formulario!=null && formulario.getRpp()!=null) rpp = formulario.getRpp().intValue();

if(MSG.length()==0){
	resultados = new WebMainSearch(cn, formulario, contenedorSesion.getUser());
	cuentaResultados = resultados.countResults();
	
	
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


/// Calculamos si la página está dentro de rango.

int maxPages = (int)Math.ceil((float)cuentaResultados/(float)rpp);

if(pts>maxPages){
	pts=maxPages;
	formulario.setPts(new Integer(pts));
	contenedorSesion.setFormulario(formulario);
	session.setAttribute("contenedorSesion",contenedorSesion);
}else if(pts<1){
	pts=1;
	formulario.setPts(new Integer(pts));
	contenedorSesion.setFormulario(formulario);
	session.setAttribute("contenedorSesion",contenedorSesion);
}



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

		if(WAR.length()>0){
		%>
			<img src="../pieces/nada50x1.gif" alt=""><p class="appstyle" style="font-size: 12pt"><blink><%=WAR%></blink></p><br>
		<%
		}
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
<% 
if(contenedorSesion.getUser()!=null && contenedorSesion.getUser().getUsrId()!=null){
	Integer filerror= resultados.getErrorUser(contenedorSesion.getUser().getUsrId());
	if(filerror>0){
%>
<table cellspacing="0" cellpadding="4">
	<tr>
		<td nowrap>
				<p class="appstyle">
				<%=filerror%> of your files have not been included in the archive (Click <a href="viewError.jsp" target="_blank"> <font color="red">here</font></a> for more info)
				</p>
		</td>
	</tr>
</table>
<%	}
	}%>	
<%-----  SECCION QUE SOLO SE PRESENTA SI HAY RESULTADOS  ------%>
<%
	if (cuentaResultados >0){
%>

<br>

<%--------  FORMULARIO DE PROCESADO MÚLTIPLE  -------------------%>
<!--   Tabla procesado múltiple -->
<form name="form_fetch_multiple" method="POST" action="fetchDataBlock.jsp">
<table cellspacing="0" cellpadding="4" class="downloadall" >
	<tr>
		<td nowrap>
				<p class="downloadall">
				<input type="submit" value="Download selected">
				&nbsp;in&nbsp;
				<select name="down_type">
					<option class="goption" value="zip">zip</option>
					<option class="goption" value="targz">tar.gz</option>
				</select>
				&nbsp;format
				</p>
		</td>
	</tr>
</table>

<br>

<!--<form name="form_resultados" action="<%=request.getRequestURI()%>">-->


<table class="appstyle" border="0" cellpadding="5" cellspacing="0">   
<tr>       
	<td>Page <%=pts%> of <%=maxPages%></td>
	<td>&nbsp;</td>
	<%
		if(pts>1){
	%><td bgcolor="#EEEEEE"><a href="searchres.jsp?pts=<%=pts-1%>">&laquo; Prev.</a></td><%
		}
	%>
	<%
		if(pts<maxPages){
	%><td bgcolor="#EEEEEE"><a href="searchres.jsp?pts=<%=pts+1%>">Next &raquo;</a></td><%
		}
	%>
	<td>&nbsp;</td>
	<td><a href="searchform.jsp">New Search</a></td>
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
			Object
		</td>
		<td class="rescab" align="center" nowrap>
			RA (deg) <br> J2000
		</td>
		<td class="rescab" align="center" nowrap>
			DEC (deg) <br> J2000
		</td>
		<td class="rescab" align="center" nowrap>
			RAJ2000 <br> (hh:mm:ss.ss)
		</td>
		<td class="rescab" align="center" nowrap>
			DECJ2000 <br> (dd:mm:ss.s)
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
			Exptime<br>(s)
		</td>
		<td class="rescab" align="center" nowrap>
			Airmass
		</td>
		<td class="rescab" style="background-color: #40FF00" align="center" nowrap>
			Pub
		</td>
		<td class="rescab" style="background-color: darkorange" colspan="3" align="center" nowrap>
			User Reduced Data <span class="form_text" onmouseover="TagToTip('userRed', CLICKSTICKY, true, CLICKCLOSE, false, CLOSEBTN, true);" onmouseout="UnTip()"><sup>?</sup></span>
					<span id="userRed"> User reduced data refers to GTC reduced data published in refereed journals and ingested into the GTC archive by the paper's authors. </span><input name="markRed" type="checkbox" onClick="mark_red(document.form_fetch_multiple)"></input>
		</td> 
		<td class="rescab" style="background-color:#D7DF01" colspan="2" align="center" nowrap>
			GTC Reduced Data <span class="form_text" onmouseover="TagToTip('gtcRed', CLICKSTICKY, true, CLICKCLOSE, false, CLOSEBTN, true);" onmouseout="UnTip()"><sup>?</sup></span>
					<span id="gtcRed"> Data reduced as described in Cortés-Contreras et al. (XXXX) </span><input name="markRedS" type="checkbox" onClick="mark_redS(document.form_fetch_multiple)"></input>
		</td>
		<td class="rescab" style="background-color:#74DF00" colspan="2" align="center" nowrap>
			QLA Reduced Data <span class="form_text" onmouseover="TagToTip('qlaRed', CLICKSTICKY, true, CLICKCLOSE, false, CLOSEBTN, true);" onmouseout="UnTip()"><sup>?</sup></span>
					<span id="qlaRed"> Quick Look Analysis reduced data. They may not be valid for scientific analysis </span><input name="markRedS" type="checkbox" onClick="mark_redS(document.form_fetch_multiple)"></input>
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
		<td class="rescab" colspan="2" style="background-color:#FA5858" align="center" nowrap>
			External Archives
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
	
	//Comprobamos si hay alguna publicación relacionada
	DBPrivate conex1 = new DBPrivate(cn);
	String[] pub = conex1.selectPub(resultado.getProgId(), resultado.getOblId(), resultado.getProdId());
	String progName = conex1.progName(resultado.getProgId());
	String prod_id = resultado.getProgId()+".."+resultado.getOblId()+".."+resultado.getProdId().intValue();
	
	%>
	<tr>
	<%-- 
	<td class="resfield" nowrap align="left"><!-- Object ID -->
		<a href="JavaScript:ventana_equiv_names('<%= resultado.getObjId()%>')"><%=resultado.getObject() %></a>
	</td>
	--%>
	<td class="resfield" nowrap align="left"><!-- ProdId -->
		<%=resultado.getProdId().intValue()%>
	</td>

	<td class="resfield" nowrap align="left" id="prog" 
		onmouseover="Tip('<%=progName%>')" 
		onmouseout="UnTip()"><!-- Obs Mode -->
		<%=resultado.getProgId() %><font color="blue">?</font>
	</td>
	
	
	<td class="resfield" nowrap align="left"><!-- OBlock ID -->
		<%=resultado.getOblId() %>
	</td>
	<td class="resfield" nowrap align="left"><!-- Object -->
		
		<%if(resultado.getObject()!=null){ %>
		<%=resultado.getObject() %>
		<%} %>
	</td>
	<td class="resfield" nowrap align="right"><!-- RA -->
		<%=resultado.getFormatedProdRa() %>
	</td>
	<td class="resfield" nowrap align="right"><!-- DEC -->
		<%=resultado.getFormatedProdDe() %>
	</td>
	<td class="resfield" nowrap align="right"><!-- RAsex -->
		<%=resultado.getSexaProdRa() %>
	</td>
	<td class="resfield" nowrap align="right"><!-- DECsex -->
		<%=resultado.getSexaProdDe() %>
	</td>

	<td class="resfield" nowrap align="left"><!-- Instrument -->
		<%=resultado.getInsName() %>
	</td>
	<td class="resfield" nowrap align="left" id="mode" 
		onmouseover="Tip('<%=resultado.getModShortname()%>')" 
		onmouseout="UnTip()"><!-- Obs Mode -->
		<a href="<%=resultado.getModDesc()%>" target="_blank"><%=resultado.getModId() %></a><font color="blue">?</font>
	</td>
	<td class="resfield" nowrap align="center"><!-- Init Time -->
		<%=resultado.getFormatedProdInitime() %>
	</td>
	<td class="resfield" nowrap align="center"><!-- End Time -->
		<%=resultado.getFormatedProdEndtime() %>
	</td>
	<td class="resfield" nowrap align="center"><!-- ExpTime -->
		<%=resultado.getExptime()%>
	</td>
	<td class="resfield" nowrap align="right"><!-- Airmass -->
		<%=resultado.getFormatedProdAirmass() %>
	</td>
	<td class="resfield" nowrap align="center"><!-- Pub -->
		<%
		if(pub!=null && pub.length>0){%>
			<a href="pub_red.jsp?prog=<%=resultado.getProgId() %>&obl=<%=resultado.getOblId() %>&prod=<%=resultado.getProdId() %>" target="blank"><%=pub.length %></a>
		<%	
		}else{ %>	
		0				
		<%}%>
	</td>
	
	
	<%-- <td class="resfield" nowrap align="center"><!-- View Reduc. Pub -->
	<%if(resultado.getCountRed()>0){%>
			<a target="_blank" href="http://adsabs.harvard.edu/abs/<%=resultado.getBibcode().trim()%>">ADS</a>
	<%	}%>
	</td>
	<td class="resfield" nowrap align="center"><!-- View Reduc. Files -->
	<%if(resultado.getCountRed()>0){ %>
			<a href="viewprod.jsp?prod_id=<%=prod_id%>&modtype=RED&bib=<%=resultado.getBibcode().trim().replaceAll("&", "%26")%>">View</a>
	<%	}%>
	</td>
	<td class="resfield" nowrap align="center"><!-- Fetch Reduc. Files -->
	<table>
		<tr>
			<td  class="resfield" nowrap>
			<%if(resultado.getCountRed()>0){ %>
				<a href="fetchDataBlock.jsp?datablock=<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getProdId()%>__ALLRED&bib=<%=resultado.getBibcode().trim().replaceAll("&", "%26")%>">Fetch</a>
			<%} %>
			</td>
			<td>
			<%if(resultado.getCountRed()>0){ %>
				<input name="datablock" value="<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getProdId()%>__ALLRED" type="checkbox">
			<%}%>
			</td>
		</tr>
	</table>
	</td> --%>
	
	<%
	//Aquí comprobamos si es público o privado
	//if(resultado.isHasPerm() || (contenedorSesion!=null && contenedorSesion.getUser()!=null && contenedorSesion.getUser().getUsrId().equals(resultado.getPi()))){
	ControlAcceso controlAcceso = new ControlAcceso(cn, contenedorSesion.getUser());
		if(controlAcceso.hasPerm(resultado.getProgId(),resultado.getOblId(),resultado.getProdId(),contenedorSesion.getUser())){

			
			
			
			if(resultado.getInsName().equalsIgnoreCase("HIPERCAM")){

				int red_users=0;
				int red_hip=0;
				
				if(resultado.getCountRed()>0){ 
					
					//Obtenemos el reducido USER y el Hipercam ya lo tenemos
					red_users = conex1.numRed(resultado.getProgId(), resultado.getOblId(), resultado.getProdId(), "USER");
					red_hip = conex1.numRed(resultado.getProgId(), resultado.getOblId(), resultado.getProdId(), "HIPER");
				}
					if(red_users>0){
						%>
						<td class="resfield" nowrap align="center"><!-- View Reduc. Pub -->
								<a target="_blank" href="http://adsabs.harvard.edu/abs/<%=resultado.getBibcode().trim()%>">ADS</a>
						</td>
						<td class="resfield" nowrap align="center"><!-- View Reduc. Files -->
								<a href="viewprod.jsp?prod_id=<%=prod_id%>&modtype=RED&bib=<%=resultado.getBibcode().trim().replaceAll("&", "%26")%>">View</a>
						</td>
						<td class="resfield" nowrap align="center"><!-- Fetch Reduc. Files -->
						<table>
							<tr>
								<td  class="resfield" nowrap>
									<a href="fetchDataBlock.jsp?datablock=<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getProdId()%>__ALLRED&bib=<%=resultado.getBibcode().trim().replaceAll("&", "%26")%>">Fetch</a>
							
								</td>
								<td>
									<input name="datablock" value="<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getProdId()%>__ALLRED" type="checkbox">
								</td>
							</tr>
						</table>
						</td>
					<%}else{%>
						<td class="resfield" nowrap align="center"><!-- View Reduc. Pub -->
						</td>
						<td class="resfield" nowrap align="center"><!-- View Reduc. Files -->
						</td>
						<td class="resfield" nowrap align="center"><!-- Fetch Reduc. Files -->
						</td>
					<% }
					if(red_hip>0){
						//Obtenemos el valor del predId del fichero reducido
						String predId = conex1.getPredS(resultado.getProgId(), resultado.getOblId(), resultado.getProdId());
						
						if(predId!=null && predId.length()>0){
						%>
						
						<td class="resfield" nowrap align="center"><!-- View Reduc. Files -->
									<a target="_blank"  href="viewprodheader.jsp?pred_id=<%=predId%>&pred_type=HiPERCAM">Header</a> <a target="_blank"  href="../help/helpHipercam.jsp">?</a>
							</td>
							<td class="resfield" nowrap align="center"><!-- Fetch Reduc. Files -->
							<table>
								<tr>
									<td  class="resfield" nowrap>
										<a href="fetchDataBlock.jsp?datablock=<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getProdId()%>__ALLHER">Fetch</a>
									</td>
									<td>
										<input name="datablock" value="<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getProdId()%>__ALLHER" type="checkbox">
									</td>
								</tr>
							</table>
							</td>
	
						
						<%}else{%>
						
							<td class="resfield" nowrap align="center"><!-- View Reduc. Files -->
							</td>
							<td class="resfield" nowrap align="center"><!-- Fetch Reduc. Files -->
							
							</td>
						<%}//No añadimos raw y calibracion %>
						<td class="resfield" nowrap align="center"> </td><!-- QLA -->
						<td class="resfield" nowrap align="center"> </td><!-- QLA -->
						<td class="resfield" nowrap align="center"> </td>
						<td class="resfield" nowrap align="center"> </td>
						<td class="resfield" nowrap align="center"> </td>
						
						
						<td class="resfield" nowrap align="center"><!-- View Calib. Files --></td>
						<td class="resfield" nowrap align="center"><!-- Fetch Calib. Files --></td>
					
					<%}else{
						
						
					//Añadimos raw y calibracion
					resultado.setCountCal(resultados.getCountCal(resultado.getProgId(), resultado.getOblId()));
					resultado.setCountAC(resultados.getCountAC(resultado.getProgId(), resultado.getOblId()));
					
					//String urlAladin70 = request.getContextPath()+"/jsp/previewProd.jsp?prod_id="+prod_id;
					String urlAladin70 = request.getContextPath()+"/jsp/aladinjnlp.jsp?prod_id="+prod_id;
					%>
					<td class="resfield" nowrap align="center"> </td>
					<td class="resfield" nowrap align="center"> </td>
					
					<td class="resfield" nowrap align="center"><!-- View Header -->
						<a target="_blank" href="viewprodheader.jsp?prod_id=<%=prod_id%>">Header</a>
					</td>
					<td class="resfield" nowrap align="center"><!-- Preview Data -->
							<a target="_blank" href="<%= urlAladin70 %>">Preview</a>
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
					
					<%if((resultado.getCountCal()+resultado.getCountEE())>0){ %>
					<td class="resfield" nowrap align="center"><!-- View Calib. Files -->
						<%String datablock = null; 
						if(resultado.getModId().equals("SPE")){
							datablock = resultado.getProgId()+"__"+resultado.getOblId()+"__"+resultado.getProdId()+"__ALLCSS";
						}else{
							datablock = resultado.getProgId()+"__"+resultado.getOblId()+"__"+resultado.getInsId()+"__ALLCAL";
						}
						%>
						<a href="viewprod.jsp?prod_id=<%=prod_id%>&modtype=CAL">View</a>
						
					</td>
					<td class="resfield" nowrap align="center"><!-- Fetch Calib. Files -->
						<table>
							<tr>
								<td  class="resfield" nowrap>
										<a href="fetchDataBlock.jsp?datablock=<%=datablock%>">Fetch</a>
								</td>
								<td>
									<input name="datablock" value="<%=datablock%>" type="checkbox">
								</td>
							</tr>
						</table>
					</td>
				<%}else{ %>
				<td class="resfield" nowrap align="center"><!-- View Calib. Files -->
					
				</td>
				<td class="resfield" nowrap align="center"><!-- Fetch Calib. Files -->
					<table>
						<tr>
							<td  class="resfield" nowrap>
							
							</td>
							<td>
							
							</td>
						</tr>
					</table>
				</td>
				<%} %>
					
					
					
					
					
					
					
					
					
					
					<%}  
				
				
				
			resultado.setCountWarn(resultados.getCountWarn(resultado.getProgId(), resultado.getOblId()));
			resultado.setCountQC(resultados.getCountQC(resultado.getProgId(), resultado.getOblId()));
		
			
			%>
			
				<td class="resfield" nowrap align="center"></td>
				<td class="resfield" nowrap align="center"></td>
						
				<td class="resfield" nowrap align="center"><!-- View Log Files -->
					<%if((resultado.getCountWarn()+resultado.getCountQC())>0){ %>
					<a href="viewprod.jsp?prod_id=<%=prod_id%>&modtype=LOG">View</a> 
					<%if(red_hip>0){ %>
					<a target="_blank"  href="../help/helpHipercam.jsp">?</a>
					<%}
					}%>
				</td>
				<td class="resfield" nowrap align="center"><!-- Fetch Log Files -->
					<table>
						<tr>
							<td  class="resfield" nowrap>
								<%if((resultado.getCountWarn()+resultado.getCountQC())>0){ %>
									<a href="fetchDataBlock.jsp?datablock=<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getInsId()%>__ALLLOG">Fetch</a>
								<%} %>
							</td>
							<td>
								<%if((resultado.getCountWarn()+resultado.getCountQC())>0){ %>
								<input name="datablock" value="<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getInsId()%>__ALLLOG" type="checkbox">
								<%} %>
							</td>
						</tr>
					</table>
				</td>
			<%
			}else{
		
			if(resultado.getCountRed()>0){ 
				
				//Comprobamos si son reducidos de usuario o de otro tipo
				int red_users = conex1.numRed(resultado.getProgId(), resultado.getOblId(), resultado.getProdId(), "USER");
				int red_herv = conex1.numRed(resultado.getProgId(), resultado.getOblId(), resultado.getProdId(), "HERVE");
				int red_meg = conex1.numRed(resultado.getProgId(), resultado.getOblId(), resultado.getProdId(), "MEG");
//No queremos mostrar los reducidos Herve, ponemos que hay 0
				//int red_herv =0;
				
				if(red_users>0){
					%>
					<td class="resfield" nowrap align="center"><!-- View Reduc. Pub -->
							<a target="_blank" href="http://adsabs.harvard.edu/abs/<%=resultado.getBibcode().trim()%>">ADS</a>
					</td>
					<td class="resfield" nowrap align="center"><!-- View Reduc. Files -->
							<a href="viewprod.jsp?prod_id=<%=prod_id%>&modtype=RED&bib=<%=resultado.getBibcode().trim().replaceAll("&", "%26")%>">View</a>
					</td>
					<td class="resfield" nowrap align="center"><!-- Fetch Reduc. Files -->
					<table>
						<tr>
							<td  class="resfield" nowrap>
								<a href="fetchDataBlock.jsp?datablock=<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getProdId()%>__ALLRED&bib=<%=resultado.getBibcode().trim().replaceAll("&", "%26")%>">Fetch</a>
						
							</td>
							<td>
								<input name="datablock" value="<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getProdId()%>__ALLRED" type="checkbox">
							</td>
						</tr>
					</table>
					</td>
				<%}else{%>
					<td class="resfield" nowrap align="center"><!-- View Reduc. Pub -->
					</td>
					<td class="resfield" nowrap align="center"><!-- View Reduc. Files -->
					</td>
					<td class="resfield" nowrap align="center"><!-- Fetch Reduc. Files -->
					</td>
				<% }
				
 				if((red_herv)==1){
 					
 				
					String predId = conex1.getPredS(resultado.getProgId(), resultado.getOblId(), resultado.getProdId());
				%>
				
				<td class="resfield" nowrap align="center"><!-- View Reduc. Files -->
							<a target="_blank"  href="viewprodheader.jsp?pred_id=<%=predId%>">Header</a>
					</td>
					<td class="resfield" nowrap align="center"><!-- Fetch Reduc. Files -->
					<table>
						<tr>
							<td  class="resfield" nowrap>
								<a href="fetchDataBlock.jsp?datablock=<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getProdId()%>__ALLHER">Fetch</a>
						
							</td>
							<td>
								<input name="datablock" value="<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getProdId()%>__ALLHER" type="checkbox">
							</td>
						</tr>
					</table>
					</td>

				
				<%}else if((red_herv)>1){%>
					<td class="resfield" nowrap align="center"><!-- View Reduc. Files -->
							<a href="viewprod.jsp?prod_id=<%=prod_id%>&modtype=HER">View</a>
					</td>
					<td class="resfield" nowrap align="center"><!-- Fetch Reduc. Files -->
					<table>
						<tr>
							<td  class="resfield" nowrap>
								<a href="fetchDataBlock.jsp?datablock=<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getProdId()%>__ALLHER">Fetch</a>
						
							</td>
							<td>
								<input name="datablock" value="<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getProdId()%>__ALLHER" type="checkbox">
							</td>
						</tr>
					</table>
					</td> 
				<%}else{%>
				
					<td class="resfield" nowrap align="center"><!-- View Reduc. Files -->
					</td>
					<td class="resfield" nowrap align="center"><!-- Fetch Reduc. Files -->
					
					</td>
				<%} 
 //Datos reducidos megara
 				if((red_meg)>1){%>
					<td class="resfield" nowrap align="center"><!-- View Reduc. Files -->
							<a href="viewprod.jsp?prod_id=<%=prod_id%>&modtype=HER">View</a>
					</td>
					<td class="resfield" nowrap align="center"><!-- Fetch Reduc. Files -->
					<table>
						<tr>
							<td  class="resfield" nowrap>
								<a href="fetchDataBlock.jsp?datablock=<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getProdId()%>__ALLMEG">Fetch</a>
						
							</td>
							<td>
								<input name="datablock" value="<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getProdId()%>__ALLMEG" type="checkbox">
							</td>
						</tr>
					</table>
					</td> 
				<%}else{%>
				
					<td class="resfield" nowrap align="center"> </td><!-- QLA -->
					<td class="resfield" nowrap align="center"> </td><!-- QLA -->
				<%} 
 				
 				
				}else{ %>
				
				<td class="resfield" nowrap align="center"><!-- View Reduc. Pub -->
				</td>
				<td class="resfield" nowrap align="center"><!-- View Reduc. Files -->
				</td>
				<td class="resfield" nowrap align="center"><!-- Fetch Reduc. Files -->
				</td>
				<td class="resfield" nowrap align="center"><!-- View ReducS. Files -->
				</td>
				<td class="resfield" nowrap align="center"><!-- Fetch ReducS. Files -->
				</td>
				
				<%} 
			
			
			
		resultado.setCountCal(resultados.getCountCal(resultado.getProgId(), resultado.getOblId()));
		resultado.setCountAC(resultados.getCountAC(resultado.getProgId(), resultado.getOblId()));
		resultado.setCountWarn(resultados.getCountWarn(resultado.getProgId(), resultado.getOblId()));
		resultado.setCountQC(resultados.getCountQC(resultado.getProgId(), resultado.getOblId()));
	
		if(resultado.getModId().equals("SPE")){
			resultado.setCountEE(resultados.getCountEE(resultado.getProgId(), String.valueOf(resultado.getProdInitime()), String.valueOf(resultado.getProdEndtime())));
		}
		
		//String urlAladin70 = request.getContextPath()+"/jsp/previewProd.jsp?prod_id="+prod_id;
		String urlAladin70 = request.getContextPath()+"/jsp/aladinjnlp.jsp?prod_id="+prod_id;
					
			if(resultado.getInsName().equals("CANARICAM")){%>
			<td class="resfield" nowrap align="center"><!-- View Header -->
				<a target="_blank" href="viewprod.jsp?prod_id=<%=prod_id%>&modtype=SCI">View</a>
			</td>
			<td class="resfield" nowrap align="center"><!-- Preview Data -->
					-
			</td>
			<td class="resfield" nowrap align="center"><!-- Fetch Spectra -->
				<table>
					<tr>
						<td  class="resfield" nowrap>
						<a href="fetchDataBlock.jsp?prod_id=<%=prod_id%>">Fetch</a>
							
						</td>
						<td>
							<input name="prod_id" value="<%=prod_id%>" type="checkbox">
	
						</td>
					</tr>
				</table>
			</td>
			
			<%}else{ %>
			<td class="resfield" nowrap align="center"><!-- View Header -->
				<a target="_blank" href="viewprodheader.jsp?prod_id=<%=prod_id%>">Header</a>
			</td>
			<td class="resfield" nowrap align="center"><!-- Preview Data -->
					<a target="_blank" href="<%= urlAladin70 %>">Preview</a>
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
		<%}%>
		<% %>
			<%if((resultado.getCountCal()+resultado.getCountEE())>0){ %>
				<td class="resfield" nowrap align="center"><!-- View Calib. Files -->
					<%String datablock = null; 
					if(resultado.getModId().equals("SPE")){
						datablock = resultado.getProgId()+"__"+resultado.getOblId()+"__"+resultado.getProdId()+"__ALLCSS";
					}else{
						datablock = resultado.getProgId()+"__"+resultado.getOblId()+"__"+resultado.getInsId()+"__ALLCAL";
					}
					%>
					<a href="viewprod.jsp?prod_id=<%=prod_id%>&modtype=CAL">View</a>
					
				</td>
				<td class="resfield" nowrap align="center"><!-- Fetch Calib. Files -->
					<table>
						<tr>
							<td  class="resfield" nowrap>
									<a href="fetchDataBlock.jsp?datablock=<%=datablock%>">Fetch</a>
							</td>
							<td>
								<input name="datablock" value="<%=datablock%>" type="checkbox">
							</td>
						</tr>
					</table>
				</td>
			<%}else{ %>
			<td class="resfield" nowrap align="center"><!-- View Calib. Files -->
				
			</td>
			<td class="resfield" nowrap align="center"><!-- Fetch Calib. Files -->
				<table>
					<tr>
						<td  class="resfield" nowrap>
						
						</td>
						<td>
						
						</td>
					</tr>
				</table>
			</td>
			<%} %>
		
	
		<%if((resultado.getModId().equalsIgnoreCase("LSS") || resultado.getModId().equalsIgnoreCase("SPE")|| resultado.getModId().equalsIgnoreCase("POL")) && resultado.getCountAC()>0){ %>
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
	
			<td class="resfield" nowrap align="center"><!-- View Log Files -->
				<%if((resultado.getCountWarn()+resultado.getCountQC())>0){ %>
				<a href="viewprod.jsp?prod_id=<%=prod_id%>&modtype=LOG">View</a>
				<%} %>
			</td>
			<td class="resfield" nowrap align="center"><!-- Fetch Log Files -->
				<table>
					<tr>
						<td  class="resfield" nowrap>
							<%if((resultado.getCountWarn()+resultado.getCountQC())>0){ %>
								<a href="fetchDataBlock.jsp?datablock=<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getInsId()%>__ALLLOG">Fetch</a>
							<%} %>
						</td>
						<td>
							<%if((resultado.getCountWarn()+resultado.getCountQC())>0){ %>
							<input name="datablock" value="<%=resultado.getProgId()%>__<%=resultado.getOblId() %>__<%=resultado.getInsId()%>__ALLLOG" type="checkbox">
							<%} %>
						</td>
					</tr>
				</table>
			</td>
		<%}//Fin de else instrumento
		
		}else{%>
			<td class="resfield" nowrap align="center" colspan="16"> <font color="red">Private Data. They will become public on: <%=resultado.getProdPrivate() %></font></td>
		<%}
		//NED
		String ned = "http://ned.ipac.caltech.edu/cgi-bin/objsearch?search_type=Near+Position+Search&in_csys=Equatorial&in_equinox=J2000.0&ra="+resultado.getFormatedProdRa()+"&dec="+resultado.getFormatedProdDe()+"&radius=10&obj_sort=Distance+to+search+center";
		%>
		
		<td class="resfield" nowrap align="center"><!-- Preview Data -->
				<a target="_blank" href="<%= ned %>">NED</a>
		</td>
		
		<%
		//CDS
		String cds = "http://cdsportal.u-strasbg.fr/#"+resultado.getFormatedProdRa()+"%20"+resultado.getFormatedProdDe();
		%>
		
		<td class="resfield" nowrap align="center"><!-- Preview Data -->
				<a target="_blank" href="<%= cds %>">CDS</a>
		</td>

	</tr>
<%} // fin del While que recorre los resultados. %>	
			
</table>


<table class="appstyle" border="0" cellpadding="5" cellspacing="0">   
<tr>       
	<td>Page <%=pts%> of <%=maxPages%></td>
	<td>&nbsp;</td>
	<%
		if(pts>1){
	%>
	<td bgcolor="#EEEEEE">
	<a href="searchres.jsp?pts=<%=pts-1%>">&laquo; Prev.</a>
	</td><%
		}
	
		
		if(pts<maxPages){
	%><td bgcolor="#EEEEEE">
	<a href="searchres.jsp?pts=<%=pts+1%>">Next &raquo;</a>
	</td><%
		}
	%>
	<td>&nbsp;</td>
	<td><a href="searchform.jsp">New Search</a></td>
</tr> 
</table>
</form>
<%
	if(maxPages>1){
%>
<form id="go_page" name="go_page" method="get" action="searchres.jsp"  enctype="application/x-www-form-urlencoded"
	onSubmit=""><%-- <input type="hidden" value="<%= %>" name="bib" /> --%>
<table class="appstyle" border="0" cellpadding="5" cellspacing="0">	
	<tr>
	<td> Go to page: </td>	
	<td><input type="text" name="pts" maxlength="10" size="4" />
	
<input type="submit" name="submit" value="go" /></td>	
</tr>
</table>
</form>
<%} %>
<table class="appstyle" border="0" cellpadding="5" cellspacing="0">	
	<tr>
	<td> <a href="resultCSV.jsp">Download the result in a CSV format</a> </td>	
	</tr>
</table>
<!--</form>-->
<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px">
<tr> 
	<td colspan="2" align="center"> 
		<br><br><i><font face="arial,helvetica,geneva"><font size=+0>
			The GTC Public Archive is the result of a collaboration agreement between INTA and GRANTECAN. 
			It has been developed in the framework of the Spanish Virtual Observatory project supported 
			by the Spanish MICINN through grant AYA 2011-24052. The system is maintained by the Data 
			Archive Unit of the <a href="http://cab.inta.es/">CAB (CSIC -INTA)</a>.
		</font></font></i>
		<br></br>
		<i><font face="arial,helvetica,geneva"><font size=+0>
			If you use this service in your research, please include the following acknowledgement in 
			any resulting publications:
		</font></font></i>
		
		<br><i><b><font face="arial,helvetica,geneva"><font size=+0>
			"Based on data from the GTC PublicArchive at CAB (INTA-CSIC)".
		</font></font></b></i></center>	
	</td>
</tr>
</table>
<p>
</p>


<%--------  FIN FORMULARIO DE DESCARGA MÚLTIPLE  -------------------%>


<%-----  FIN SECCION QUE SOLO SE PRESENTA SI HAY RESULTADOS  ------%>
<% } %> 


<%-----  FIN DEL ELSE ERROR PÁGINA  ------%>
<% } %>


<p>
<br>
</p>

<%= pie %>
</body>
</html>
<% }catch(SQLException ex){
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