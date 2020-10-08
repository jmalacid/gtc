<%@ page import="svo.gtc.web.Html,
				 svo.gtc.web.ContenedorSesion,
				 svo.gtc.db.DriverBD,
				 svo.gtc.db.instrument.InstrumentoAccess,
				 svo.gtc.db.instrument.InstrumentoDb,
				 svo.gtc.db.modo.ModoDb,
				 svo.gtc.db.web.OverviewStats,
				 svo.gtc.db.web.OverviewInfo,
				svo.gtc.db.web.PrivInfo,
				 java.net.URLEncoder,
                 java.util.Locale,
                 java.util.Vector,
                 java.util.List,
                 java.util.Enumeration, 
                 
                 java.sql.*" %>
                 
<%@ page isThreadSafe="true" %>
<%@ page info="GTC Scientific Archive Homepage" %>


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

//*************************************************
//*  CABECERAS                                    *
//*************************************************

//---- Elementos est�ticos de la p�gina
Html elementosHtml = new Html(request.getContextPath());
String cabeceraPagina = elementosHtml.cabeceraPagina("Product Details","Data Product Details.");
String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath()+"?"+request.getQueryString());
String pie            = elementosHtml.pie();

	
	
//======================================================= 
//  Conexion con la Base de Datos                       
//=======================================================

Connection conex= null;

DriverBD drvBd = new DriverBD();

try {
	conex = drvBd.bdConexion();
} 
catch (SQLException errconexion) {
	errconexion.printStackTrace();
}
	

	
%>

<%--------     I N I C I O    P � G I N A    H T M L    -------------------%>

<%= cabeceraPagina %>

<body>
<%= encabezamiento %>


<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px" >
<caption><!-- Introduction -->
<!-- Table of resources -->
<!-- Page footer --></caption>
<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER><font face="arial,helvetica,san-serif"><font
color="#FFFFFF"><font size=+2>&nbsp;GTC Archive - System overview</font></font></font>
</td>
</tr>

</table>

<br><br>

	     <OL> 
	     <LI><a href="#1">Introduction</a></LI>
	     <LI><a href="#2">Archive status</a></LI>
	     <LI><a href="#3"> Functionalities</a>
		 <ul>
		 <LI><a href="#4"> Archive search</a></LI>
		 <LI><a href="#5"> Results from search</a></LI>
		 <LI><a href="#6"> Reduced data upload</a></LI>
		 

		 </ul>
		 </OL>
		
	   
		<a id="1"></a><H2>Introduction</H2>
		
		<p>
			This data server provides access to the <a href="http://www.gtc.iac.es/">GTC</a> Public Archive. GTC data become public once the proprietary (1 year) is over.The 
			<b>Gran Telescopio CANARIAS (GTC)</b>, is a 10.4m telescope with a segmented primary mirror. 
			It is located in one of the top astronomical sites in the Northern Hemisphere: the 
			<a href="http://www.iac.es/eno.php?op1=2&lang=en">Observatorio del Roque de los Muchachos</a> 
			(ORM, La Palma, Canary Islands). The GTC is a Spanish initiative led by the 
			<a href="http://www.iac.es">Instituto de Astrof�sica de Canarias (IAC)</a>. The project also includes the participation of Mexico 
			(<a href="http://www.astroscu.unam.mx/">Instituto de Astronom�a de la Universidad Nacional 
			Aut�noma de M�xico (IA-UNAM)</a> and <a href="http://www.inaoep.mx/">Instituto Nacional de 
			Astrof�sica, �ptica y Electr�nica (INAOE )</a>) and the US 
			(<a href="http://www.astro.ufl.edu/">University of Florida (UFL)</a>). 
			 The project is actively supported by the Spanish Government and the Local Government from the Canary Islands 
			through the European Funds for Regional Development (FEDER) provided by the European Union.		
			
		</p>


		<a id="2"></a><H2>Archive status</H2>
		
		<p>
			In the next tables the current status of the archive is shown. For each instrument and instrument mode,
			the number of files (<i>science</i> and <i>calibration</i>) and the volume of data are provided.   
		</p>
		
		
<% 
try{
	//Hacemos la conexi�n
	OverviewStats stats = new OverviewStats(conex);
	
	ResultSet res = conex.createStatement().executeQuery("select ins_id, ins_name from instrumento;");
	
	while(res.next()){
		String ins_id = res.getString("ins_id").trim();
		String ins_name = res.getString("ins_name").trim();
	
	int cuentaRawTotal	= 0;
	int tamRawTotal		= 0;
	int cuentaRedTotal	= 0;
	int tamRedTotal		= 0;
	int cuentaCalTotal	= 0;
	int tamCalTotal		= 0;
	
	OverviewInfo[] results = stats.getStatIns(ins_id);
	
	%><p class="salto appstyle"><%=ins_name %></p>
	
	<table cellspacing="3" width="600px" border="0" >
		<tr class="rescab">
		<td></td>
		<td colspan="4" class="rescab">Sci. Files</td>
		<td colspan="2" rowspan="2" class="rescab">Cal. Files</td>
		</tr>
		<tr class="rescab">
		<td class="rescab">Mode</td>
		<td colspan="2" class="rescab">Raw Files</td>
		<td colspan="2" class="rescab">Red. Files</td>
		
		</tr><%
		
		for(int i=0; i<results.length;i++){
			cuentaRawTotal	+= results[i].getCuentaRaw();
			
			tamRawTotal	+= results[i].getGbRaw();
			cuentaRedTotal	+= results[i].getNumRed();
			tamRedTotal	+= results[i].getGbRed();
			cuentaCalTotal	+= results[i].getNumCal();
			tamCalTotal	+= results[i].getGbCal();
			
			%>
			<tr>
					<td class="resfield"><%=results[i].getModo() %></td>
					<td class="resfield" align="right"><%=results[i].getCuentaRaw() %></td>
					<td class="resfield" align="right"><%=results[i].getGbRaw() %> GB</td>
					<td class="resfield" align="right"><%=results[i].getNumRed() %></td>
					<td class="resfield" align="right"><%=results[i].getGbRed() %> GB</td>
					<td class="resfield" align="right"><%=results[i].getNumCal() %></td>
					<td class="resfield" align="right"><%=results[i].getGbCal() %> GB</td>
				</tr>
			<%
			
			
		}
		%>
		<tr>
					<td class="resfield"><b>Total</b></td>
					<td class="resfield" align="right"><b><%=cuentaRawTotal %></b></td>
					<td class="resfield" align="right"><b><%=tamRawTotal %> GB</b></td>
					<td class="resfield" align="right"><%=cuentaRedTotal %></td>
					<td class="resfield" align="right"><%=tamRedTotal %> GB</td>
					<td class="resfield" align="right"><b><%=cuentaCalTotal %></b></td>
					<td class="resfield" align="right"><b><%=tamCalTotal %> GB</b></td>
				</tr>
		
		</table>
		
		<%
		
		ResultSet resmod = conex.createStatement().executeQuery("select mod_id, mod_name from modo where ins_id='"+ins_id+"' and mod_name!='EMIRImaging'");
		
		while(resmod.next()){
			String mod_id = resmod.getString("mod_id").trim();
			String mod_name = resmod.getString("mod_name").trim();
			
			%><p class="salto appstyle" style="margin-left:2em"><%=mod_name %></p>
			
			<table style="margin-left:2em" cellspacing="3" width="600px" border="0" >
				<tr class="rescab">
				<td></td>
				<td colspan="4" class="rescab">Sci. Files</td>
				<td colspan="2" rowspan= "2" class="rescab">Cal. Files</td>
				</tr>
				<tr class="rescab">
				<td class="rescab">Semester</td>
				<td colspan="2" class="rescab">Raw Files</td>
				<td colspan="2" class="rescab">Red. Files</td>
				
				</tr>
				<%
			OverviewInfo[] resultsMod = stats.getStatIns(ins_id, mod_name);
			
				for(int i=0; i<resultsMod.length;i++){
					
					%>
					<tr>
				<td class="resfield"><%= resultsMod[i].getSemestre()%></td>
				<td class="resfield" align="right"><%=resultsMod[i].getCuentaRaw() %></td>
				<td class="resfield" align="right"><%=resultsMod[i].getGbRaw() %> GB</td>
				<td class="resfield" align="right"><%=resultsMod[i].getNumRed() %></td>
				<td class="resfield" align="right"><%=resultsMod[i].getGbRed() %> GB</td>
				<td class="resfield" align="right"><%=resultsMod[i].getNumCal() %></td>
				<td class="resfield" align="right"><%=resultsMod[i].getGbCal() %> GB</td>
				</tr>
					
					<%
					
				}
				%>
			</table>
				
				<%
		}
		
	}

	%>
	<br><br>
	<p>Private information:</p>
	<table cellspacing="3" width="600px" border="0" >
	<tr>
	<td  class="rescab">Instrument</td>
	<td class="rescab">Number of Programs</td>
	<td  class="rescab">Number of OBs</td>
	<td  class="rescab">Number of Sci. Files</td>
	</tr>
	
	<%
	PrivInfo osi= stats.getStatPriv("OSI");
	%>
	<tr>
	<td  class="resfield">OSIRIS</td>
	<td class="resfield"><%=osi.getProgs() %></td>
	<td  class="resfield"><%=osi.getObls() %></td>
	<td  class="resfield"><%=osi.getProds() %></td>
	</tr>
	<%
	PrivInfo cc= stats.getStatPriv("CC");
	%>
	<tr>
	<td  class="resfield">CANARICAM</td>
	<td class="resfield"><%=cc.getProgs() %></td>
	<td  class="resfield"><%=cc.getObls() %></td>
	<td  class="resfield"><%=cc.getProds() %></td>
	</tr>
	<%
	PrivInfo cir= stats.getStatPriv("CIR");
	%>
	<tr>
	<td  class="resfield">CIRCE</td>
	<td class="resfield"><%=cir.getProgs() %></td>
	<td  class="resfield"><%=cir.getObls() %></td>
	<td  class="resfield"><%=cir.getProds() %></td>
	</tr>
	
	<%
	PrivInfo emir= stats.getStatPriv("EMIR");
	%>
	<tr>
	<td  class="resfield">EMIR</td>
	<td class="resfield"><%=emir.getProgs() %></td>
	<td  class="resfield"><%=emir.getObls() %></td>
	<td  class="resfield"><%=emir.getProds() %></td>
	</tr>
	
	</table>
	<%
	
	//ResultSet res = conex.createStatement().executeQuery("select ins_id, ins_name from instrumento;");
	
	//while(res.next()){
	//	String ins_id = res.getString("ins_id");
	//	String ins_name = res.getString("ins_name");
		
		

		%><%-- <p class="salto appstyle"><%=ins_name %></p>
		
		<table cellspacing="3" width="600px" border="0" >
			<tr class="rescab">
			<td></td>
			<td colspan="4" class="rescab">Sci. Files</td>
			<td colspan="2" rowspan="2" class="rescab">Cal. Files</td>
			</tr>
			<tr class="rescab">
			<td class="rescab">Mode</td>
			<td colspan="2" class="rescab">Raw Files</td>
			<td colspan="2" class="rescab">Red. Files</td>
			
			</tr> --%>
		
		<%		
		
		//int cuentaRawTotal	= 0;
		//int tamRawTotal		= 0;
		//int cuentaRedTotal	= 0;
		//int tamRedTotal		= 0;
		//int cuentaCalTotal	= 0;
		//int tamCalTotal		= 0;
		
		//OverviewInfo[] results = stats.getStatIns(ins_id);
		//List<Vector> statsByMode = stats.getStatsByMode(ins_id);
		
		//for(Vector fila: statsByMode){
		//	String mod_name = (String)fila.elementAt(0);
		//	OverviewStatItem item = (OverviewStatItem)fila.elementAt(1);
			
		//	cuentaRawTotal	+= item.getNumRaw();
		//	tamRawTotal		+= item.getGbRaw();
		//	cuentaRedTotal	+= item.getNumRed();
		//	tamRedTotal		+= item.getGbRed();
		//	cuentaCalTotal	+= item.getNumCal();
		//	tamCalTotal		+= item.getGbCal();
			
			%>
				<%-- <tr>
					<td class="resfield"><%=mod_name %></td>
					<td class="resfield" align="right"><%=item.getNumRaw() %></td>
					<td class="resfield" align="right"><%=item.getGbRaw() %> GB</td>
					<td class="resfield" align="right"><%=item.getNumRed() %></td>
					<td class="resfield" align="right"><%=item.getGbRed() %> GB</td>
					<td class="resfield" align="right"><%=item.getNumCal() %></td>
					<td class="resfield" align="right"><%=item.getGbCal() %> GB</td>
				</tr> --%>
			<%
			
			
		//}
		%>
			<%-- 	<tr>
					<td class="resfield"><b>Total</b></td>
					<td class="resfield" align="right"><b><%=cuentaRawTotal %></b></td>
					<td class="resfield" align="right"><b><%=tamRawTotal %> GB</b></td>
					<td class="resfield" align="right"><%=cuentaRedTotal %></td>
					<td class="resfield" align="right"><%=tamRedTotal %> GB</td>
					<td class="resfield" align="right"><b><%=cuentaCalTotal %></b></td>
					<td class="resfield" align="right"><b><%=tamCalTotal %> GB</b></td>
				</tr>
		
		</table> --%>
<% 		
		// FOR por modos

		//List<ModoDb> modos = stats.getModesByInst(ins_id);
		//for(ModoDb modo: modos){
%>
		
			<%-- <p class="salto appstyle" style="margin-left:2em"><%=modo.getModName() %></p>
		
		<table style="margin-left:2em" cellspacing="3" width="600px" border="0" >
			<tr class="rescab">
			<td></td>
			<td colspan="4" class="rescab">Sci. Files</td>
			<td colspan="2" rowspan= "2" class="rescab">Cal. Files</td>
			</tr>
			<tr class="rescab">
			<td class="rescab">Semester</td>
			<td colspan="2" class="rescab">Raw Files</td>
			<td colspan="2" class="rescab">Red. Files</td>
			
			</tr> --%>
			
<% 		
			// FOR por Semestres
	
			//List<Vector> filas = stats.getStatsBySemester(modo.getInsId(), modo.getModId());
			//for(Vector fila: filas){
			//	String semestre = (String)fila.elementAt(0);
			//	OverviewStatItem item = (OverviewStatItem)fila.elementAt(1);
				
%>
			<%-- <tr>
				<td class="resfield"><%=semestre %></td>
				<td class="resfield" align="right"><%=item.getNumRaw() %></td>
				<td class="resfield" align="right"><%=item.getGbRaw() %> GB</td>
				<td class="resfield" align="right"><%=item.getNumRed() %></td>
				<td class="resfield" align="right"><%=item.getGbRed() %> GB</td>
				<td class="resfield" align="right"><%=item.getNumCal() %></td>
				<td class="resfield" align="right"><%=item.getGbCal() %> GB</td>
			</tr> --%>

<% 
			// END FOR por semestres
			//}
%>

<% 
		// END de FOR por modos
		//out.flush();
		//}

	// END de FOR por instrumento
	//out.flush();
	//}
}catch(Exception e){
	e.printStackTrace();
}
%>				
	
		
		 <a id="3"></a><H2>Functionalities</H2>

		 <a id="4"></a><H3>Archive search</H3>

		 <p>
		 		The GTC Archive, at CAB (INTA-CSIC), is accessed by means of a web-based fill-in form 
				which permits queries driven by a set of conditions. The form is divided into the next
				sections:
		</p>
		
		<H4>Target list</H4>
		
		<img src="../images/help/searchform_target.png" border="1"></img>
		
		<p>A list of targets may be provided in the form of coordinates or object names, one line each. If the object name is provided
		the system will automatically resolve the corresponding coordinates using the SIMBAD database. The search radius around each of the 
		coordinates may be specified.</p>

		<p>These searches are made using <a href="http://pgsphere.github.io/doc/index.html">pgsphere</a> in the database.</p>

		<p>To ease the search of a certain product, product ID numbers may also be provided to perform the search.</p>


		<H4>Date</H4>
		
		<img src="../images/help/searchform_date.png" border="1"></img>
		
		<p>A range of dates may be given to filter the results. By default the dates of the first and the last registered product
		are provided to perform a search without date restrictions.</p>

		<H4>Instrumentation</H4>
		
		<img src="../images/help/searchform_instrument.png" border="1"></img>
		
		<p>A list of all instrument and modes are available to check/uncheck.</p>


		<H4>Program</H4>
		
		<img src="../images/help/searchform_program.png" border="1"></img>
		
		<p>Specific programs and observing blocks may be selected. <b>Character '%' may be used as wildcard</b>. 
		In the example above, the user is interested in data of the 10A semester.</p>


		<H4>Order</H4>
		
		<img src="../images/help/searchform_order.png" border="1"></img>
		
		<p>This field is used to specify the order in which the results are displayed. 
		The results may be ordered by observing date, instrument or program/OB.</p>


		<H4>Pages</H4>
		
		<img src="../images/help/searchform_pages.png" border="1"></img>
		
		<p>The results may be returned in blocks of 10, 50 or 100 results per page. If more than
		one page is returned, a specific number of page may be selected to be displayed. By default, 
		the first page of the results is shown.</p>


		
		<a id="5"></a><H3>Result from search</H3>

		<p class="commonletter">
		The following image shows the typical output obtained after querying the archive.
		</p>
		
		
		<img src="../images/help/results.png" border="1"></img>
		
		
		<p>The number of scientific products found matching	the search criteria is given. If there are more than 
		one page of results, the "Next" and "Prev." links may be used to navigate among them.</p>

		<p>The scientific products found are presented in a table where, for each of them, the following information is shown:</p>
		
		<ul>
		<li>Product ID</li>
		<li>Coordinates</li>
		<li>Program</li>
		<li>Observing block</li>
		<li>Instrument</li>
		<li>Observing mode</li>
		<li>Time range</li>
		<li>Airmass</li>
		</ul>

		<p>Four different blocks are available to access the data details and to download the data products. In all of them checkboxes exist
		to download several products at a time, compressed in a ZIP file or in TAR.GZ file, using the "Download Selected" button:</p>
		
		<img src="../images/help/downloadSelected.png" border="1"></img>
		
		<p>In the next sections the different blocks of the page of results are described.</p>
		

		<H4>Raw Data</H4>
		
		<img src="../images/help/results_raw.png" border="1"></img>
		
		<p>This block gives access to the scientific products. Data can be downloaded or visualized using ALADIN. 
		FITS headers can also be visualized by clicking on the corresponding link.</p>
		
		
		<H4>Calibration Files & Acquisition Images</H4>
		
		<img src="../images/help/results_cal.png" border="1"></img>
		
		<p>For each of the scientific products, the associated set of calibration files and, if it is the case, acquisition images, 
		may be downloaded or previewed.		
		The "view" option will show a page with detailed information. Each calibration file may be previewed using ALADIN or 
		downloaded. It is also possible to preview several files at a time using ALADIN. To do so, several files may be selected
		and then ALADIN launched using the "Preview Selected" button. </p>

		

		<img src="../images/help/viewprod.png" border="1"></img>
		

		<p>The files marked in <b>yellow</b> are identified to have any kind of <b>warning</b> detected by the GTC Quality Control at CAB. The 
		associated message is shown by clicking the product ID number.  </p>

		
		<p>For those modes having acquisition images, the same functionalities as for calibration files are available.</p>
		
		
		<img src="../images/help/results_acimg.png" border="1"></img>
		
	
		<H4>QC Files</H4>
		
		<img src="../images/help/results_log.png" border="1"></img>
		
		<p>If warnings have been detected by the GTC Quality Control System for any of the associated files of the scientific product, a log
		containing that information may be accessed or downloaded. Detailed information can be found by clicking the "View" button:</p>
		
		<img src="../images/help/viewlog.png" border="1"></img>
	
		<H4>Download File</H4>
		
		<p>In the Download file you have the data you have selected organized by folders:</p>
		
		<ul>
		<li>Program id: 
		<ul><li>Observing block id (depending of the instrument):</li>
			<ul>
			<li>OSIRIS:
				<ul>
				<li>object: Science files and Adquisition Image files</li>
				<li>std: Quality Standard files</li>
				<li>bias: Bias files</li>
				<li>flat: Flat files</li>
				<li>dark: Dark files</li>
				</ul>
			</li>
			<li>CANARICAM:
				<ul>
				<li>object: Science files, Standard Stars files, Adquisition Image files and Adquisition Imagen Through Slits files.</li>
				<li>std: Quality Standard files</li>
				</ul>
			</li>
			</ul>
		</ul>
		</li>
		
		<li>Reduced: reduced data organized by Program_id/observing_block</li>
		
		<li>Log file</li>
		
		</ul>
	
		<a id="6"></a><H3>Reduced data upload</H3>
		
		<p>The system provides a simple way to upload reduced data for those users with the right privileges. To access the reduced data upload 
		module, select "Upload reduced data" in the main page of the archive.</p>
		
		<img src="../images/help/reduced/red1_FrontPage.png" border="1"></img>
		
		<p>If the user is not already logged in, the system will prompt a login window.<p>
		
		<img src="../images/help/reduced/red2_login.png" border="1"></img>
		
		<p>The procedure to upload reduced data is very simple.  Only two fields have to be provided: the data collection name (typically 
		associated to a paper) and the file containing the reduced data.</p>
		
		<H4>STEP I: Creation of a new data collection.</H4>
		
		<img src="../images/help/reduced/red4_newCollection.png" border="1"></img>
	
		<p>There are three mandatory items that have to be specified when creating a new data collection: data collection name, publication 
		bibliographic code and reduction type. Every collection is associated to the publication where the reduced data have been published 
		and its bibliographic code has to be given. For the reduction type, predefined reductions may be selected or free text may be added 
		to express a reduction type not present in the page.</p>
	
		<H4>STEP II: Data upload.</H4>

		<img src="../images/help/reduced/red3_insertReduced.png" border="1"></img>
		
		<p>The reduced data may be uploaded in several formats. The user may provide a single FITS file (FITS.gz is also admitted) or, 
		if more than one file is going to be uploaded, he/she can use tar, tar.gz or zip to submit the collection of FITS files. 
		All the files uploaded in this action will be associated to the selected data 
		collection.	In case the user needs to create a new data collection, the button "New Collection" may be used and a page to create a new
		data collection will be prompted.</p>
		
		<H4>STEP III: Results of the upload.</H4>
		
		<img src="../images/help/reduced/red5_insertReducedOutput.png" border="1"></img>
			
		<p>When the data upload has been committed, a page will present the result of the upload.</p>
		
		<p> The number of files correctly uploaded will be 
		given, as well as the number of files where problems were found. For the problematic files, the reason of the failure is expressed
		both with a color code and with a detailed description. Those files showing orange color had been already uploaded in the past and 
		cannot be uploaded again. Files showing red color have raised any kind of problem and cannot be ingested in the archive. Should 
		you need any help with this, please, do not hesitate to contact GTC archive staff (gtc-support@cab.inta-csic.es). Files uploaded
		without problems, and thus not present in the error log, are ingested in the archive automatically and available for the community through 
		the archive search functionality.</p>
		
		
		
		
	
<%= pie %>
</body>
</html>
