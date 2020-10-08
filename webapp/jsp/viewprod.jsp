<%@ page import="svo.gtc.web.Html,
				 svo.gtc.web.ContenedorSesion,
				 svo.gtc.web.AladinApplet,
				 svo.gtc.db.web.*,
				 svo.gtc.db.prodat.ProdDatosAccess,
				 svo.gtc.db.prodat.ProdDatosDb,
				 svo.gtc.db.prodred.ProdRedAccess,
				 svo.gtc.db.prodred.ProdRedDb,
				 svo.gtc.db.proderr.ProdErrorAccess,
				 svo.gtc.db.proderr.ProdErrorDb,
				 svo.gtc.db.instrument.InstrumentoAccess,
				 svo.gtc.db.instrument.InstrumentoDb,
				 svo.gtc.db.logfile.LogFileDb,
				 svo.gtc.db.permisos.ControlAcceso,
				 svo.gtc.db.colecciondatos.ColeccionDatosAccess,
				 svo.gtc.db.colecciondatos.ColeccionDatosDb,
				 
				 java.sql.*,
				 javax.sql.*,
				javax.naming.Context,
				javax.naming.InitialContext,
				javax.naming.NamingException" %>
                 
<%@ page isThreadSafe="true" %>
<%@ page info="Product Details" %>


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

//Abrimos la conexión a la base de datos
Context initContext;
Connection cn=null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();


// *************************************************************
// *  TECLAS DEL FORMULARIO                                    *
// *************************************************************

	//---- Elementos estáticos de la página
	Html elementosHtml = new Html(request.getContextPath());
	String cabeceraPagina = elementosHtml.cabeceraPagina("Product Details","Data Product Details.");
	String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath()+"?"+request.getQueryString());
	String pie            = elementosHtml.pie();

	String par_prodId = request.getParameter("prod_id");
	String par_modtype = request.getParameter("modtype");
	String bibcode = null;
	
	
	String[] aux = new String[3];
	if(par_prodId!=null){
		aux = par_prodId.split("\\.\\.");
	}

	String prog_id = "";
	String obl_id="";
	Integer prod_id = new Integer(-1);
	
	try{
		prog_id = aux[0];
		obl_id=aux[1];
		if(aux[2]!=null){
			prod_id= new Integer(aux[2]);
		}
	}catch(Exception e){}
	
  	
	ProdDatosAccess datosAccess 	= new ProdDatosAccess(cn);
	ProdDatosDb prod 				= datosAccess.selectById(prog_id, obl_id, prod_id);
	ProdDatosDb prodRed 			= datosAccess.selectRedById(prog_id, obl_id, prod_id);
	ProdErrorAccess erroAccess 		= new ProdErrorAccess(cn);
	ProdDatosDb prodErr 			= datosAccess.selectById(prog_id, obl_id, prod_id);
	ColeccionDatosAccess colAccess 	= new ColeccionDatosAccess(cn);
	WebSearcher webSearcher 		= new WebSearcher(cn);

	InstrumentoAccess insAccess = new InstrumentoAccess(cn);
	InstrumentoDb ins = insAccess.selectById(prod.getInsId());

	
	ProdDatosDb[] productos 	= new ProdDatosDb[0];
	ProdRedDb[] productosRed 	= new ProdRedDb[0];
	ProdRedDb[] productosHer 	= new ProdRedDb[0];
	ProdErrorDb[] productosErr 	= new ProdErrorDb[0];
	LogFileDb[]   logs = new LogFileDb[0];

	// Si no hay modo mostramos el producto de datos
	if(par_modtype==null || par_modtype.trim().length()<=0){
		productos = new ProdDatosDb[1];
		productos[0]=prod;
	//}else if(1==0 && par_modtype.equalsIgnoreCase("LOG")){
	}else if(par_modtype.equalsIgnoreCase("LOG")){
		// Si hay modo, mostramos los ficheros de ese modo asociados al producto.
		logs = webSearcher.selectLogsByOblId(prod.getProgId(), prod.getOblId().substring(0,4));
	}else if(par_modtype.equalsIgnoreCase("ERR")){
		//productosErr = webSearcher.selectErrsByOblId(prod.getProgId(), prod.getOblId());
	}else if(par_modtype.equalsIgnoreCase("RED")){
		productosRed = webSearcher.selectRedByProdId(prod.getProgId(), prod.getOblId(), prod.getProdId(),"USER");
	}else if(par_modtype.equalsIgnoreCase("HER")){
		productosHer = webSearcher.selectRedByProdId(prod.getProgId(), prod.getOblId(), prod.getProdId(),"HERVE");
	}else if(par_modtype.equalsIgnoreCase("MEG")){
		productosHer = webSearcher.selectRedByProdId(prod.getProgId(), prod.getOblId(), prod.getProdId(),"MEG");
	}else if(par_modtype.equalsIgnoreCase("SCI")){
		//productosSci = webSearcher.selectSciByProdId(prod.getProgId(), prod.getOblId(), prod.getProdId());
	
	}else{
		productos = webSearcher.selectProdsByOblId(prod.getProgId(), prod.getOblId().substring(0,4), prod.getInsId(), par_modtype, String.valueOf(prod.getProdInitime()), String.valueOf(prod.getProdEndtime()));
	}

	//======================================================= 
	//Control de Acceso                       
	//=======================================================
	ControlAcceso controlAcceso = new ControlAcceso(cn, contenedorSesion.getUser());
	if(productos!=null && productos.length>0){
		
		for(int i=0;i<productos.length;i++){
			if(!controlAcceso.hasPerm(productos[i].getProgId(),productos[i].getOblId(),productos[i].getProdId(),contenedorSesion.getUser())){
				String pagOrigen = request.getServletPath()+"?"+request.getQueryString();
				request.setAttribute("pagOrigen",pagOrigen);
			
				%>
				<jsp:forward page="accessDenied.jsp"/>
				<%

			}
		}
	
	}
	if(logs!=null && logs.length>0){
		
		for(int i=0;i<logs.length;i++){
			if(!controlAcceso.hasPerm(logs[i].getProgId(),logs[i].getOblId(),contenedorSesion.getUser())){
				String pagOrigen = request.getServletPath()+"?"+request.getQueryString();
				request.setAttribute("pagOrigen",pagOrigen);
			
				%>
				<jsp:forward page="accessDenied.jsp"/>
				<%

			}
		}
	
	}
	

%>



<%--------     I N I C I O    P Á G I N A    H T M L    -------------------%>
<html>
<%= cabeceraPagina %>
<body>
<%= encabezamiento %>

<p><br></p>


<table cellspacing="0" cellpadding="4" class="downloadall">
	<tr>
		<td><b>Program ID:</b></td><td><%=prod.getProgId() %></td>
	</tr>
	<tr>
		<td><b>ObsBlock ID:</b></td><td><%=prod.getOblId() %></td>
	</tr>
	<tr>
		<td><b>Instrument:</b></td><td><%=ins.getInsName() %></td>
	</tr>
	
<%if(par_modtype.equalsIgnoreCase("RED")){
	bibcode = request.getParameter("bib"); 
	%>
	<tr>
		<td><b>Product ID:</b></td><td><%=prod.getProdId()%></td>
	</tr>
	<tr>
		<td><b>Reduced data from:</b></td><td><%=bibcode%></td>
	</tr>
<%} %>
<%if(par_modtype.equalsIgnoreCase("HER") || par_modtype.equalsIgnoreCase("MEG")){
	%>
	<tr>
		<td><b>Product ID:</b></td><td><%=prod.getProdId()%></td>
	</tr>
<%} %>
</table>		

<p>
<br>
</p>

<form name="form_preview_multiple" method="POST" action="previewProd.jsp">

<%if(par_modtype!=null && !par_modtype.trim().equalsIgnoreCase("LOG") && !par_modtype.trim().equalsIgnoreCase("RED")&& !par_modtype.trim().equalsIgnoreCase("HER") && !par_modtype.trim().equalsIgnoreCase("MEG")){ %>
<table cellspacing="0" cellpadding="4" class="downloadall">
	<tr>
		<td nowrap>
				<p class="downloadall">
				<input type="submit" value="Preview selected"></input></p>
		</td>
	</tr>
</table>
<%} %>

<!--   Tabla de resultados -->			
<table cellspacing="3" WIDTH="800px" border="0" >
	<tr>
		<%if(par_modtype!=null && par_modtype.trim().equalsIgnoreCase("RED")){ 
			if(productosRed.length>0){
		%>
			<td class="rescab" align="center" nowrap>
				Filename
			</td>
			<td class="rescab" align="center">
				Reduction
			</td>
			<td class="rescab" colspan="3" align="center" nowrap>
				&nbsp;
			</td>
		<%
			}else{
			%>
			<td class="resfield" colspan="3" align="center" nowrap>
				No products found.
			</td>
				
			<%
			}
		}else if(par_modtype!=null && (par_modtype.trim().equalsIgnoreCase("HER") || par_modtype.trim().equalsIgnoreCase("MEG"))){ 
				if(productosHer.length>0){
			%>
				<td class="rescab" align="center" nowrap>
					Filename
				</td>
				<td class="rescab" align="center">
					Reduction
				</td>
				<td class="rescab" colspan="3" align="center" nowrap>
					&nbsp;
				</td>
			<%
				}else{
				%>
				<td class="resfield" colspan="3" align="center" nowrap>
					No products found.
				</td>
					
				<%
				}
		}else if(par_modtype!=null && par_modtype.trim().equalsIgnoreCase("SCI")){
				%>
				<td class="rescab" align="center" nowrap>
					Prod ID
				</td>
				<td class="rescab" align="center" nowrap>
					Mode
				</td>
				<!-- <td class="rescab" align="center" nowrap>
					Submode
				</td> -->
				<td class="rescab" align="center" nowrap>
					Type
					<%if(ins.getInsName().equalsIgnoreCase("CANARICAM")){ %>
					 <a href='../help/helptype.jsp' target='blank'><font color='red'>?</font></a>
					 <%} %>
				</td>
				<td class="rescab" align="center" nowrap>
					Init. Time
				</td>
				<td class="rescab" align="center" nowrap>
					End Time
				</td>
				<td class="rescab" colspan="3" align="center" nowrap>
					&nbsp;
				</td>
			<%	
			}else if(par_modtype==null || (!par_modtype.trim().equalsIgnoreCase("Err") && !par_modtype.trim().equalsIgnoreCase("LOG"))){ 
			
			if(productos.length>0){
		%>
			<td class="rescab" align="center" nowrap>
				Prod ID
			</td>
			<td class="rescab" align="center" nowrap>
				Mode
			</td>
			<td class="rescab" align="center" nowrap>
				Type
				<%if(ins.getInsName().equalsIgnoreCase("CANARICAM")){ %>
					 <a href='../help/helptype.jsp' target='blank'><font color='red'>?</font></a>
					 <%} %>
			</td>
			<td class="rescab" align="center" nowrap>
				Init. Time
			</td>
			<td class="rescab" align="center" nowrap>
				End Time
			</td>
			<td class="rescab" colspan="3" align="center" nowrap>
				&nbsp;
			</td>
		<%
			}else{
			%>
			<td class="resfield" colspan="3" align="center" nowrap>
				No products found.
			</td>
				
			<%
			}
		}else if(par_modtype!=null && par_modtype.trim().equalsIgnoreCase("ERR")){ 
			if(productosErr.length>0){

		%>
			<td class="rescab" align="center" nowrap>
				Prod ID
			</td>
			<td class="rescab" align="center" nowrap>
				Dir.
			</td>
			<td class="rescab" align="center" nowrap>
				File
			</td>
			<td class="rescab" colspan="3" align="center" nowrap>
				&nbsp;
			</td>
		<%
		}else{
			%>
			<td class="resfield" colspan="3" align="center" nowrap>
				No errors found in the Observing Block
			</td>
				
			<%
			}

		
		}else{ %>
			<td class="rescab" align="center" nowrap>
				File
			</td>
			<td class="rescab" align="center" nowrap>
				Mode
			</td>
			<td class="rescab" align="center" nowrap>
				Type
			</td>
			<td class="rescab" colspan="2" align="center" nowrap>
				&nbsp;
			</td>
		<%} %>
		
	</tr>


<% 

String puerto = "";


if(par_modtype!=null && par_modtype.trim().equalsIgnoreCase("RED")){
	String desc = "";
	for( int i=0; i<productosRed.length; i++ ){
		ColeccionDatosDb colDb 	= colAccess.selectById(productosRed[i].getUsrId(), productosRed[i].getColId());
		String predId= ""+productosRed[i].getPredId().intValue();
		String redType=colDb.getColRedType();
		desc=colDb.getColDesc();
		
		//String urlAladin70 = AladinApplet.getAladinAppletURL("http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/FetchProd?prod_id="+productos[i].getProdId().intValue()); 
		//String urlAladin70 = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/jsp/previewProd.jsp?pred_id="+predId;
		//String urlAladin70 = request.getContextPath()+"/jsp/previewProd.jsp?pred_id="+predId;
		//String urlAladin70 = request.getContextPath()+"/jsp/aladinjnlp.jsp?pred_id="+predId;
		String urlAladin70 ="http://gtc.sdc.cab.inta-csic.es/gtc/jsp/aladinjnlp.jsp?pred_id="+predId;
	%>
		
		<tr>
			<td class="resfield" nowrap align="left"><!-- Filename -->
				<%=productosRed[i].getPredFilename() %>
			</td>
			<td class="resfield" nowrap align="left"><!-- Filename -->
				<%=redType %>
			</td>
			<td class="resfield" nowrap align="left" <%if(!(prod.getModId().equals("BBI") || prod.getModId().equals("TF") || prod.getModId().equals("IMG"))){ %>colspan="2"<%} %>><!-- Header -->
				<a target="_blank" href="viewprodheader.jsp?pred_id=<%=predId%>">Header</a>
			</td>
		    <%if(prod.getModId().equals("BBI") || prod.getModId().equals("TF") || prod.getModId().equals("IMG")){ %>
			<td class="resfield" nowrap align="left"><!-- Plot -->
				<a target="_blank" href="<%= urlAladin70%>">Preview</a>&nbsp;</input>
			</td>
			<%} %>
			<td class="resfield" nowrap align="left"><!-- Fetch -->
<%-- 				<a href="http://<%=request.getServerName() %>:<%=request.getServerPort() %><%=request.getContextPath() %>/servlet/FetchProd?pred_id=<%=predId%>&fetch_type=PRED&bib=<%=bibcode.replaceAll("&", "%26")%>">Fetch</a> --%>
				<a href="<%=request.getContextPath() %>/servlet/FetchProd?pred_id=<%=predId%>&fetch_type=PRED&bib=<%=bibcode.replaceAll("&", "%26")%>">Fetch</a>
			</td>
		</tr>
	<%} // fin del While que recorre los resultados.	
	%>
	<%if(desc.length()>0){ %>
	</table>
	<br><br>
	<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="500px">
	<tr><td class="rescab" align="left"><b>Collection description: </b></td></tr>
	
	<tr> 
	<td class="resfield" align="left"> 
			<%=desc.replaceAll("\r", "<br>")%>
	</td>
	</tr>
		
	<%}
	}else if(par_modtype!=null && (par_modtype.trim().equalsIgnoreCase("HER") || par_modtype.trim().equalsIgnoreCase("MEG"))){
	for( int i=0; i<productosHer.length; i++ ){
		
		String predId= ""+productosHer[i].getPredId().intValue();
		
		
		//String urlAladin70 = AladinApplet.getAladinAppletURL("http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/FetchProd?prod_id="+productos[i].getProdId().intValue()); 
		//String urlAladin70 = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/jsp/previewProd.jsp?pred_id="+predId;
		//String urlAladin70 = request.getContextPath()+"/jsp/previewProd.jsp?pred_id="+predId;
		String urlAladinHer = "http://gtc.sdc.cab.inta-csic.es/gtc/jsp/aladinjnlp.jsp?pred_id="+predId;

	%>
		
		<tr>
			<td class="resfield" nowrap align="left"><!-- Filename -->
				<%=productosHer[i].getPredFilename() %>
			<%if(productosHer[i].getPredFilename().contains("final_rss")){ %>	
				<span class="form_text" onmouseover="TagToTip('final', CLICKSTICKY, true, CLICKCLOSE, false, CLOSEBTN, true);" onmouseout="UnTip()"><sup>?</sup></span>
					<span id="final"> Processed image after the subtraction of the sky spectrum </span>
			<%}else if(productosHer[i].getPredFilename().contains("reduced_rss")){  %>
			<span class="form_text" onmouseover="TagToTip('reduced', CLICKSTICKY, true, CLICKCLOSE, false, CLOSEBTN, true);" onmouseout="UnTip()"><sup>?</sup></span>
					<span id="reduced"> Processed image prior to the subtraction of the sky spectrum </span>
			<%}else if(productosHer[i].getPredFilename().contains("sky_rss")){  %>
			<span class="form_text" onmouseover="TagToTip('sky', CLICKSTICKY, true, CLICKCLOSE, false, CLOSEBTN, true);" onmouseout="UnTip()"><sup>?</sup></span>
					<span id="sky"> RSS image showing signal only in the valid sky fibers. All other pixels are set to zero. </span>
			<%} %>
			</td>
	<% if(productosHer[i].getPredFilename().endsWith(".txt")){	%>
			<td class="resfield" nowrap align="left"><!-- Header -->
				<a target="_blank" href="viewprodheader.jsp?pred_id=<%=predId%>&pred_type=txt">Header</a>
			</td>
			<td  class="resfield" nowrap align="left"></td>
	<%}else{ %>	
			<td class="resfield" nowrap align="left" <%if(!(prod.getModId().equals("BBI") || prod.getModId().equals("TF") || prod.getModId().equals("IMG"))){ %>colspan="2"<%} %>><!-- Header -->
				<a target="_blank" href="viewprodheader.jsp?pred_id=<%=predId%>">Header</a>
			</td>
		    <%if(prod.getModId().equals("BBI") || prod.getModId().equals("TF") || prod.getModId().equals("IMG")){ %>
			<td class="resfield" nowrap align="left"><!-- Plot -->
				<a target="_blank" href="<%= urlAladinHer%>">Preview</a>&nbsp;
			</td>
			<%} 
		}%>
			<td class="resfield" nowrap align="left"><!-- Fetch -->
<%-- 				<a href="http://<%=request.getServerName() %>:<%=request.getServerPort() %><%=request.getContextPath() %>/servlet/FetchProd?pred_id=<%=predId%>&fetch_type=PRED&bib=<%=bibcode.replaceAll("&", "%26")%>">Fetch</a> --%>
				<a href="<%=request.getContextPath() %>/servlet/FetchProd?pred_id=<%=predId%>&fetch_type=PRED">Fetch</a>
			</td>
		</tr>
	<%} // fin del While que recorre los resultados.	
	%>
	
<% }else if(par_modtype!=null && par_modtype.trim().equalsIgnoreCase("LOG")){
	/// La descarga de logs está desactivada más arriba.
	for( int i=0; i<logs.length; i++ ){
	%>
		
		<tr>
			<td class="resfield" nowrap align="left"><!-- File -->
				<%=logs[i].getLogFilename() %>
			</td>
			<td class="resfield" nowrap align="left" id="mode" 
			onmouseover="Tip('<%=prod.getModShortname()%>')" 
			onmouseout="UnTip()"><!-- Obs Mode -->
			<%=prod.getModId() %><font color="blue">?</font>
			</td>
		
			<td class="resfield" nowrap align="left"><!-- Type -->
				<%if(logs[i].getLogtype().equalsIgnoreCase("QC")){ %>
				GTC observing log
				<%}else  if(logs[i].getLogtype().equalsIgnoreCase("LOG")){%>
				IP quality control
				<%}else{ %>
				LOG
				<%} %>
			</td>
			<td class="resfield" nowrap align="left"><!-- Header -->
				<a href="previewLog.jsp?prog_id=<%=prod.getProgId().trim()%>&obl_id=<%=prod.getOblId().trim()%>&log_filename=<%=logs[i].getLogFilename()%>">Show</a>
			</td>
			<td class="resfield" nowrap align="left"><!-- Fetch -->
				<a href="<%=request.getContextPath() %>/servlet/FetchProd?prog_id=<%=prod.getProgId().trim()%>&obl_id=<%=prod.getOblId().trim()%>&log_filename=<%=logs[i].getLogFilename()%>">Fetch</a>
			</td>
		</tr>
	<%} // fin del While que recorre los resultados. 	
	
	
	// Vemos si hay warnings. Si los hay, añadimos el enlace al log correspondiente.
	if(webSearcher.selectProductsWithWarningsByOblId(prod.getProgId().trim(), prod.getOblId().trim()).length>0){
	%>
		
		<tr>
			<td class="resfield" nowrap align="left"><!-- Mode -->
				warnings_<%=prod.getProgId().trim()%>_<%=prod.getOblId().trim().substring(0,4)%>.txt
			</td>
			<td class="resfield" nowrap align="left" id="mode" 
			onmouseover="Tip('<%=prod.getModShortname()%>')" 
			onmouseout="UnTip()"><!-- Obs Mode -->
			<%=prod.getModId() %><font color="blue">?</font>
		</td>
			<td class="resfield" nowrap align="left"><!-- Mode -->
				LOG
			</td>
			<td class="resfield" nowrap align="left"><!-- Header -->
				<a href="previewLog.jsp?prog_id=<%=prod.getProgId().trim()%>&obl_id=<%=prod.getOblId().trim()%>&log_filename=warn">Show</a>
			</td>
			<td class="resfield" nowrap align="left"><!-- Fetch -->
<%-- 				<a href="http://<%=request.getServerName() %>:<%=request.getServerPort() %><%=request.getContextPath() %>/servlet/WarningLog?progId=<%=prod.getProgId().trim()%>&oblId=<%=prod.getOblId().trim()%>">Fetch</a> --%>
				<a href="<%=request.getContextPath() %>/servlet/WarningLog?progId=<%=prod.getProgId().trim()%>&oblId=<%=prod.getOblId().trim()%>">Fetch</a>
			</td>
		</tr>
	<% // fin del While que recorre los resultados. 	
	}
	
	
}else if(par_modtype!=null && par_modtype.trim().equalsIgnoreCase("ERR")){
	for( int i=0; i<productosErr.length; i++ ){
		String prodId= productosErr[i].getProgId().trim()+".."+productosErr[i].getOblId().trim()+".."+productosErr[i].getProdeId().intValue();
		
		//String urlAladin70 = AladinApplet.getAladinAppletURL("http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/FetchProd?prod_id="+productos[i].getProdId().intValue()); 
		//String urlAladin70 = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/jsp/previewProd.jsp?prod_id="+prodId;
		//String urlAladin70 = request.getContextPath()+"/jsp/previewProd.jsp?prod_id="+prodId;
		//String urlAladin70 = request.getContextPath()+"/jsp/aladinjnlp.jsp?prod_id="+prodId;
		String urlAladin70 = "http://gtc.sdc.cab.inta-csic.es/gtc/jsp/aladinjnlp.jsp?prod_id="+prodId;
				
		String tdClass="resfield_error";
	%>
		
		<tr>
			<td class="<%=tdClass%>" nowrap align="left"><!-- ProdID -->
				<a href="proderrors.jsp?prod_id=<%=prodId %>"><%=productosErr[i].getProdeId().intValue() %> </a>
				<%-- <%=productos[i].getProdId().intValue() %>--%>
			</td>
			<td class="<%=tdClass%>" nowrap align="left"><!-- Dir -->
				<%=productosErr[i].getProdePath() %>
			</td>
			<td class="<%=tdClass%>" nowrap align="left"><!-- File -->
				<%=productosErr[i].getProdeFilename() %>
			</td>
			<td class="<%=tdClass%>" nowrap align="left"><!-- Header -->
				<a target="_blank" href="viewprodheader.jsp?prod_id=<%=prodId%>">Header</a>
			</td>
			<td class="<%=tdClass%>" nowrap align="left"><!-- Plot -->
				<a target="_blank" href="<%= urlAladin70%>">Preview</a>&nbsp;<input type="checkbox" name="prod_id" value="<%=prodId%>"></input>
			</td>
			<td class="<%=tdClass%>" nowrap align="left"><!-- Fetch -->
				<!--  <a href="http://<%=request.getServerName() %>:<%=request.getServerPort() %><%=request.getContextPath() %>/servlet/FetchProd?prod_id=<%=prodId%>">Fetch</a>-->
				<a href="<%=request.getContextPath() %>/servlet/FetchProd?prod_id=<%=prodId%>">Fetch</a>
			</td>
		</tr>
	<%} // fin del While que recorre los resultados.	
	
}else if(par_modtype!=null && par_modtype.trim().equalsIgnoreCase("SCI")){

	//for( int i=0; i<productos.length; i++ ){
		Timestamp start = prod.getProdInitime();
		Timestamp end   = prod.getProdEndtime();
		Double exposure = prod.getProdExposure();
		Double airmass = prod.getProdAirmass();
		
		String prodId= prod.getProgId()+".."+prod.getOblId()+".."+prod.getProdId().intValue();
		
		String startAux = "&nbsp;";
		if(start!=null) startAux=start.toString().substring(0,21);
		String endAux = "&nbsp;";
		if(end!=null) endAux=end.toString().substring(0,21);
		String exposureAux = "&nbsp;";
		if(exposure!=null) exposureAux=exposure.doubleValue()+"";
		String airmassAux = "&nbsp;";
		if(airmass!=null) airmassAux=airmass.doubleValue()+"";
		//String urlAladin70 = AladinApplet.getAladinAppletURL("http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/FetchProd?prod_id="+productos[i].getProdId().intValue()); 
		//String urlAladin70 = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/jsp/previewProd.jsp?prod_id="+prodId;
		//String urlAladin70 = request.getContextPath()+"/jsp/previewProd.jsp?prod_id="+prodId;
		//String urlAladin70 = request.getContextPath()+"/jsp/aladinjnlp.jsp?prod_id="+prodId;
		String urlAladin70 = "http://gtc.sdc.cab.inta-csic.es/gtc/jsp/aladinjnlp.jsp?prod_id="+prodId;
		
		String tdClass="resfield";
		if(prod.getWarnings().length>0){ 
			tdClass="resfield_warn";
		}
		
	%>
		
		<tr>
			<td class="<%=tdClass%>" nowrap align="left"><!-- Mode -->
				<% if(prod.getWarnings().length>0){%><a href="proderrors.jsp?prod_id=<%=prodId %>"><%} %><%=prod.getProdId().intValue() %> <% if(prod.getWarnings().length>0){%></a><%} %>
				<%-- <%=productos[i].getProdId().intValue() %>--%>
			</td>
			<td class="resfield" nowrap align="left" id="mode" 
			onmouseover="Tip('<%=prod.getModShortname()%>')" 
			onmouseout="UnTip()"><!-- Obs Mode -->
			<%=prod.getModId() %><font color="blue">?</font>
		</td>
			<%-- <td class="<%=tdClass%>" nowrap align="left"><!-- Mode -->
				<%=prod.getSubmId() %>
			</td> --%>
			
			<!-- <td class="resfield" nowrap align="left" id="mode" 
			onmouseover="Tip('Raw data in multi-extension FITS (MEF) file format. Each extension contains an image, as well as specific headers. The zeroth header is a general header, which contains information concerning the full data set.')" 
			onmouseout="UnTip()">Obs Mode
			Raw_cube<font color="blue">?</font>
			</td> -->
			<td class="<%=tdClass%>" nowrap align="left"><!-- Type -->
				Raw_cube
			</td>
			<td class="<%=tdClass%>" nowrap align="left"><!-- Time start -->
				<%=startAux %>
			</td>
			<td class="<%=tdClass%>" nowrap align="left"><!-- Time end -->
				<%=endAux %>
			</td>
			<td class="<%=tdClass%>" nowrap align="left"><!-- Header -->
				<a target="_blank" href="viewprodheader.jsp?prod_id=<%=prodId%>">Header</a>
			</td>
			<td class="<%=tdClass%>" nowrap align="left"><!-- Plot -->
				<a target="_blank" href="<%= urlAladin70%>">Preview</a>&nbsp;<input type="checkbox" name="prod_id" value="<%=prodId%>"></input>
			</td>
			<td class="<%=tdClass%>" nowrap align="left"><!-- Fetch -->
				<!-- <a href="http://<%=request.getServerName() %>:<%=request.getServerPort() %><%=request.getContextPath() %>/servlet/FetchProd?prod_id=<%=prodId%>">Fetch</a>-->
				<a href="<%=request.getContextPath() %>/servlet/FetchProd?prod_id=<%=prodId%>">Fetch</a>
			</td>
		</tr>
	<%//} // fin del While que recorre los resultados.	

	if(prodRed!=null){


		//for( int i=0; i<productos.length; i++ ){
			start = prodRed.getProdInitime();
			end   = prodRed.getProdEndtime();
			exposure = prodRed.getProdExposure();
			airmass = prodRed.getProdAirmass();
			
			prodId= prodRed.getProgId()+".."+prodRed.getOblId()+".."+prodRed.getProdId().intValue();
			
			startAux = "&nbsp;";
			if(start!=null) startAux=start.toString().substring(0,21);
			endAux = "&nbsp;";
			if(end!=null) endAux=end.toString().substring(0,21);
			exposureAux = "&nbsp;";
			if(exposure!=null) exposureAux=exposure.doubleValue()+"";
			airmassAux = "&nbsp;";
			if(airmass!=null) airmassAux=airmass.doubleValue()+"";
			//String urlAladin70 = AladinApplet.getAladinAppletURL("http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/FetchProd?prod_id="+productos[i].getProdId().intValue()); 
			//urlAladin70 = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/jsp/previewProd.jsp?prod_id="+prodId;
			//urlAladin70 = request.getContextPath()+"/jsp/previewProd.jsp?prod_id="+prodId;
			//urlAladin70 = request.getContextPath()+"/jsp/aladinjnlp.jsp?prod_id="+prodId;
			urlAladin70 = "http://gtc.sdc.cab.inta-csic.es/gtc/jsp/aladinjnlp.jsp?prod_id="+prodId;
			tdClass="resfield";
			if(prodRed.getWarnings().length>0){ 
				tdClass="resfield_warn";
			}
			
		%>
			
			<tr>
				<td class="<%=tdClass%>" nowrap align="left"><!-- Mode -->
					<% if(prodRed.getWarnings().length>0){%><a href="proderrors.jsp?prod_id=<%=prodId %>"><%} %><%=prodRed.getProdId().intValue() %> <% if(prodRed.getWarnings().length>0){%></a><%} %>
					<%-- <%=productos[i].getProdId().intValue() %>--%>
				</td>
				<td class="resfield" nowrap align="left" id="mode" 
			onmouseover="Tip('<%=prodRed.getModShortname()%>')" 
			onmouseout="UnTip()"><!-- Mode -->
			<%=prodRed.getModId() %><font color="blue">?</font>
		</td>
				<!-- <td class="resfield" nowrap align="left" id="mode" 
			onmouseover="Tip('Single (photon accumulated) raw data.\nWARNING-1: This is not a science-ready product (despite what the word reduced might indicate).\nWARNING-2: Astrometry comes from the telescope pointing. Errors up to several arcsecons can be expected')" 
			onmouseout="UnTip()">Obs Mode
			Raw_reduced<font color="blue">?</font>
			</td> -->
				<td class="<%=tdClass%>" nowrap align="left"><!-- Type -->
					Raw_reduced
				</td>
				<td class="<%=tdClass%>" nowrap align="left"><!-- Time start -->
					<%=startAux %>
				</td>
				<td class="<%=tdClass%>" nowrap align="left"><!-- Time end -->
					<%=endAux %>
				</td>
				<td class="<%=tdClass%>" nowrap align="left"><!-- Header -->
					<a target="_blank" href="viewprodheader.jsp?prod_id=<%=prodId%>">Header</a>
				</td>
				<td class="<%=tdClass%>" nowrap align="left"><!-- Plot -->
					<a target="_blank" href="<%= urlAladin70%>">Preview</a>&nbsp;<input type="checkbox" name="prod_id" value="<%=prodId%>"></input>
				</td>
				<td class="<%=tdClass%>" nowrap align="left"><!-- Fetch -->
					<!-- <a href="http://<%=request.getServerName() %>:<%=request.getServerPort() %><%=request.getContextPath() %>/servlet/FetchProd?prod_id=<%=prodId%>">Fetch</a>-->
					<a href="<%=request.getContextPath() %>/servlet/FetchProd?prod_id=<%=prodId%>">Fetch</a>
				</td>
			</tr>
		<%
			//} // fin del While que recorre los resultados.	

				

			}
		}else{
			for( int i=0; i<productos.length; i++ ){
				Timestamp start = productos[i].getProdInitime();
				Timestamp end   = productos[i].getProdEndtime();
				Double exposure = productos[i].getProdExposure();
				Double airmass = productos[i].getProdAirmass();
				
				String prodId= productos[i].getProgId()+".."+productos[i].getOblId()+".."+productos[i].getProdId().intValue();
				
				String startAux = "&nbsp;";
				if(start!=null) startAux=start.toString().substring(0,21);
				String endAux = "&nbsp;";
				if(end!=null) endAux=end.toString().substring(0,21);
				String exposureAux = "&nbsp;";
				if(exposure!=null) exposureAux=exposure.doubleValue()+"";
				String airmassAux = "&nbsp;";
				if(airmass!=null) airmassAux=airmass.doubleValue()+"";
				//String urlAladin70 = AladinApplet.getAladinAppletURL("http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/FetchProd?prod_id="+productos[i].getProdId().intValue()); 
				//String urlAladin70 = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/jsp/previewProd.jsp?prod_id="+prodId;
				//String urlAladin70 = request.getContextPath()+"/jsp/previewProd.jsp?prod_id="+prodId;
				//String urlAladin70 = request.getContextPath()+"/jsp/aladinjnlp.jsp?prod_id="+prodId;
				String urlAladin70 = "http://gtc.sdc.cab.inta-csic.es/gtc/jsp/aladinjnlp.jsp?prod_id="+prodId;
				
				String tdClass="resfield";
				if(productos[i].getWarnings().length>0){ 
			tdClass="resfield_warn";
				}
				
				String type = productos[i].getSubmId();
				
				if(ins.getInsName().equalsIgnoreCase("CANARICAM")){
					if(type.equalsIgnoreCase("SS")){
						type="Standard Star_cube";
					} else if(type.equalsIgnoreCase("SS_R")){
						type="Standard Star_reduced";
					} else if(type.equalsIgnoreCase("STDS")){
						type="Quality Standard_cube";
					} else if(type.equalsIgnoreCase("STDS_R")){
						type="Quality Standard_reduced";
					} else if(type.equalsIgnoreCase("ACIMG")){
						type="Adquisition image_cube";
					} else if(type.equalsIgnoreCase("ACIMG_R")){
						type="Adquisition image_reduced";
					} else if(type.equalsIgnoreCase("ACTS")){
						type="Adquisition image through slit_cube";
					} else if(type.equalsIgnoreCase("ACTS_R")){
						type="Adquisition image through slit_reduced";
					} 
				}
				
		%>
		
		<tr>
			<td class="<%=tdClass%>" nowrap align="left"><!-- Mode -->
				<% if(productos[i].getWarnings().length>0){%><a href="proderrors.jsp?prod_id=<%=prodId %>"><%} %><%=productos[i].getProdId().intValue() %> <% if(productos[i].getWarnings().length>0){%></a><%} %>
				<%-- <%=productos[i].getProdId().intValue() %>--%>
			</td>
			<td class="resfield" nowrap align="left" id="mode" 
			onmouseover="Tip('<%=productos[i].getModShortname()%>')" 
			onmouseout="UnTip()"><!-- Mode -->
			<%=productos[i].getModId() %><font color="blue">?</font>
			</td>
			
			<td class="<%=tdClass%>" nowrap align="left"><!-- Type -->
				<%=type %>
			</td>
			<td class="<%=tdClass%>" nowrap align="left"><!-- Time start -->
				<%=startAux %>
			</td>
			<td class="<%=tdClass%>" nowrap align="left"><!-- Time end -->
				<%=endAux %>
			</td>
			<td class="<%=tdClass%>" nowrap align="left"><!-- Header -->
				<a target="_blank" href="viewprodheader.jsp?prod_id=<%=prodId%>">Header</a>
			</td>
			<td class="<%=tdClass%>" nowrap align="left"><!-- Plot -->
				<a target="_blank" href="<%= urlAladin70%>">Preview</a>&nbsp;<input type="checkbox" name="prod_id" value="<%=prodId%>"></input>
			</td>
			<td class="<%=tdClass%>" nowrap align="left"><!-- Fetch -->
				<a href="<%=request.getContextPath() %>/servlet/FetchProd?prod_id=<%=prodId%>">Fetch</a>
			</td>
		</tr>
	<%} // fin del While que recorre los resultados.	
}

%>
			
</table>

</form>
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
<br></br>

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