<%@ page import="svo.gtc.web.Html,
				 svo.gtc.web.ContenedorSesion,
				 svo.gtc.db.web.*,
				 svo.gtc.db.permisos.ControlAcceso,
				
				 java.sql.*,
				 java.util.Calendar,
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

//Abrimos la conexi�n a la base de datos
Context initContext;
Connection cn=null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();



// *************************************************************
// *  TECLAS DEL FORMULARIO                                    *
// *************************************************************

	//---- Elementos est�ticos de la p�gina
	Html elementosHtml = new Html(request.getContextPath());
	String cabeceraPagina = elementosHtml.cabeceraPagina("Product Details","Data Product Details.");
	String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath()+"?"+request.getQueryString());
	String pie            = elementosHtml.pie();

	String par_submit = request.getParameter("submit");
	String par_day1 = request.getParameter("day1");
	String par_month1 = request.getParameter("month1");
	String par_year1 = request.getParameter("year1");
	String par_day2 = request.getParameter("day2");
	String par_month2 = request.getParameter("month2");
	String par_year2 = request.getParameter("year2");
	
	
%>



<%--------     I N I C I O    P � G I N A    H T M L    -------------------%>
<html>
<%= cabeceraPagina %>
<body>
<%= encabezamiento %>


<%if(par_submit==null){ 


	WebStats stats = new WebStats(cn);
	Timestamp firstDate = stats.getInitDate();
	Timestamp lastDate = stats.getFinalDate();

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


%>
<form name="form_stats" method="POST" action="stats.jsp">

<p class="appstyle" style="font-size:12pt;">Date:</p>
<table border="0" CELLSPACING="5" WIDTH="640px" bgcolor="#f0f0f0">
	<tr>
		<td>
			<p class="appstyle">Between:</p>
		</td>
		<td>
			<table border="0" CELLSPACING="3" >
				<tr>
					<td nowrap>
						<input type="text" name="day1" size="2" maxlength="2" value="<%=day1 %>">&nbsp;
						<select name="month1">
							<%= camposOptionMeses1 %>
						</select>
						<select name="year1">
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
						<input type="text" name="day2" size="2" maxlength="2" value="<%=day2 %>">&nbsp;
						<select name="month2">
							<%= camposOptionMeses2 %>
						</select>
						<select name="year2">
							<%= camposOptionAnos2 %>
						</select>
						
					</td>
				</tr>
			</table>
		</td>	
	</tr>

</table>

<p>
<br>
</p>

<input type="submit" name="submit" value="Submit">


</form>
<%}else{ 
	WebStats stats = new WebStats(cn, par_day1, par_month1, par_year1, par_day2, par_month2, par_year2);


%>

<!--   Tabla de resultados -->
<h2>Query statistics:</h2>

<h3>Queries to the archive:</h3>

<table cellspacing="3" border="0" >
	<tr>
		<td class="rescab">Type</td><td class="rescab">Queries</td>
		<td class="rescab">Number of hosts</td>
	</tr>
	<tr>
		<td class="rescab">WEB</td><td class="resfield" align="center"><%=stats.getQueWebExt() %></td>
		<td class="resfield" align="center"><%=stats.getQueHostsExt() %></td>
	</tr>
	<tr>
		<td class="rescab">VO</td>
		<td class="resfield" align="center"><%=stats.getQueVoExt() %></td>
		<td class="resfield" align="center"><%=stats.getQueHostsExtVO() %></td>
	</tr>
</table>
<h5>
<b>WEB:</b> Number of queries made by the web Archive<br>
<b>VO:</b> Number of queries made by the VO<br>
<b>Number of host:</b> Total number of different host who made the queries</h5>

<h3>Download information:</h3>

<table cellspacing="3" border="0" >
	<tr>
		<td class="rescab">Type</td>
		<td class="rescab">Number</td>
		<td class="rescab">Number of hosts</td>
		<td class="rescab">Number of files</td>
		<td class="rescab">Number of privates files</td>
		<td class="rescab">Number of science files</td>
		<td class="rescab">Number of privates science files</td>
		<td class="rescab">MB</td>
	</tr>
	<tr>
		<td class="rescab">WEB</td>
		<td class="resfield" align="center"><%=stats.getDownWebExt() %></td>
		<td class="resfield" align="center"><%=stats.getDownHostsExt() %></td>
		<td class="resfield" align="center"><%=stats.getDownFilesExt() %></td>
		<td class="resfield" align="center"><%=stats.getDownFilesExtPriv() %></td>
		<td class="resfield" align="center"><%=stats.getDownSciExt() %></td>
		<td class="resfield" align="center"><%=stats.getDownSciExtPriv() %></td>
		<td class="resfield" align="center"><%=(int)Math.ceil(stats.getDownMbExt().floatValue()) %></td>
	</tr>
	<tr>
		<td class="rescab">VO</td>
		<td class="resfield" align="center"><%=stats.getDownVoExt() %></td>
		<td class="resfield" align="center"><%=stats.getDownHostsExtVO() %></td>
		<td class="resfield" align="center"><%=stats.getDownFilesExtVO() %></td>
		<td class="resfield" align="center">N/A</td>
		<td class="resfield" align="center">N/A</td>
		<td class="resfield" align="center">N/A</td>
		<td class="resfield" align="center"><%=(int)Math.ceil(stats.getDownMbExtVO().floatValue()) %></td>
	</tr>
</table>

<h5>
<b>WEB:</b> Number of downloads (single data and multidownload of any kind of data) made by the web Archive<br>
<b>VO:</b> Number of downloads (reduced data) made by the VO<br>
<b>Number of host:</b> Total number of different host who have download data (WEB + VO)<br>
<b>Number of files:</b> Total number of files (single data plus the total number of file in multidownload. This is science, reduced data, calibration,AC, QC and preview)<br>
<b>MB:</b> Total size of the download files</h5>
<table cellspacing="3" border="0" >
	<tr>
		<td class="rescab">Reducidos descargados sin crudos:</td>
		<td class="resfield" align="center"> <%=stats.getCase1() %></td>
	</tr>
	<tr>
		<td class="rescab">Reducidos descargados con sus crudos:</td>
		<td class="resfield" align="center"> <%=stats.getCase2() %></td>
	</tr>
	<tr>
		<td class="rescab">Crudos con reducido asociado, descargados solos:</td>
		<td class="resfield" align="center"> <%=stats.getCase3() %></td>
	</tr>
	<tr>
		<td class="rescab">Crudos descargados que no tienen reducido asociado:</td>
		<td class="resfield" align="center"> <%=stats.getCase4() %></td>
	</tr>
</table>

<h3>Top 10 hosts:</h3>

<table>
	<tr>
		<td valign="top">
			<table cellspacing="3" border="0" >
				<tr><td class="rescab" colspan="2">Queries</td></tr>
				<tr><td class="rescab">Host</td><td class="rescab">Queries</td></tr>
			<% 
			String[] topHosts = stats.getQueTopHostsExt();
			Integer[] topHostsCount = stats.getQueCountTopHostsExt();
			
			for(int i=0; i<topHosts.length; i++){
			%>
				<tr><td class="resfield" align="left"><%= topHosts[i] %></td><td class="resfield" align="right"><%=topHostsCount[i] %></td></tr>
			<%} %>
			
			</table>
		</td>
		<td valign="top">
			<table cellspacing="3" border="0" >
				<tr><td class="rescab" colspan="2">Previews</td></tr>
				<tr><td class="rescab">Host</td><td class="rescab">Previews</td></tr>
			<% 
			topHosts = stats.getPrevTopHostsExt();
			topHostsCount = stats.getPrevCountTopHostsExt();
			
			for(int i=0; i<topHosts.length; i++){
			%>
				<tr><td class="resfield" align="left"><%= topHosts[i] %></td><td class="resfield" align="right"><%=topHostsCount[i] %></td></tr>
			<%} %>
			
			</table>
		</td>
		<td valign="top">
			<table cellspacing="3" border="0" >
				<tr><td class="rescab" colspan="3">Downloads</td></tr>
				<tr><td class="rescab">Host</td><td class="rescab">MB</td><td class="rescab">Number</td></tr>
			<% 
			topHosts = stats.getDownTopHostsExt();
			topHostsCount = stats.getDownTopHostsFilesExt();
			Float[] topHostsMb = stats.getDownTopHostsMbExt();
			
			for(int i=0; i<topHosts.length; i++){
			%>
				<tr><td class="resfield" align="left"><%= topHosts[i] %></td><td class="resfield" align="right"><%=(int)Math.ceil(topHostsMb[i].floatValue()) %></td><td class="resfield" align="right"><%=topHostsCount[i] %></td></tr>
			<%} %>
			
			</table>
		</td>
	</tr>
</table>
<h5><b>Queries:</b> Number of Queries (WEB + VO)<br>
<b>Preview:</b> Number of previews in the WEB<br>
<b>Download:</b> Total number of downloads (WEB + VO)<br>
<ul>
<li><b>Number: </b> Number of downloads (single data download (WEB+VO) plus multidownload)</li>
</ul>
</h5>
<%}// Fin tabla resultados %>

<%= pie %>
</body>
</html>

<%
}catch(SQLException ex){
	//fallo sql
	System.out.println("Error : "+ex.getMessage());
	ex.printStackTrace();
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

