<%@ page import="svo.gtc.web.Html,
				 svo.gtc.web.ContenedorSesion,
				 svo.gtc.web.AladinApplet,
				 svo.gtc.web.WebUtils,
				 
				 svo.gtc.db.web.*,
				 svo.gtc.db.prodat.ProdDatosAccess,
				 svo.gtc.db.prodat.ProdDatosDb,
				 svo.gtc.db.prodat.WarningDb,
				 svo.gtc.db.proderr.ProdErrorAccess,
				 svo.gtc.db.proderr.ProdErrorDb,
				 svo.gtc.db.proderr.ErrorDb,
				 svo.gtc.db.instrument.InstrumentoAccess,
				 svo.gtc.db.instrument.InstrumentoDb,
				 svo.gtc.db.logfile.LogFileDb,
				 svo.gtc.db.permisos.ControlAcceso,
				 
				java.sql.*,
				javax.sql.*,
				javax.naming.Context,
				javax.naming.InitialContext,
				javax.naming.NamingException" %>
                 
<%@ page isThreadSafe="true" %>
<%@ page info="Product Error Details" %>


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
	String cabeceraPagina = elementosHtml.cabeceraPagina("Product Error Details","Data Product Error Details.");
	String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath()+"?"+request.getQueryString());
	String pie            = elementosHtml.pie();

	String par_prodId = request.getParameter("prod_id");

	String prog_id = WebUtils.getWebProgId(par_prodId);
	String obl_id=WebUtils.getWebOblId(par_prodId);;
	Integer prod_id = WebUtils.getWebProdId(par_prodId);
	
  	
	ProdDatosAccess datosAccess = new ProdDatosAccess(cn);
	ProdErrorAccess errorAccess = new ProdErrorAccess(cn);
	ProdDatosDb prod  = datosAccess.selectById(prog_id, obl_id, prod_id);
	ProdErrorDb prode = errorAccess.selectProdErrorById(prog_id, obl_id, prod_id);
	
	String progId = null;
	String oblId = null;
	Integer prodId = null;
	String prodFilename = null;
	ErrorDb[] errores = new ErrorDb[0];
	WarningDb[] warnings = new WarningDb[0];
	
	if(prod!=null){
		progId = prod.getProgId();
		oblId = prod.getOblId();
		prodId = prod.getProdId();
		prodFilename=prod.getProdFilename();
		warnings = prod.getWarnings();
	}else if(prode!=null){
		progId = prode.getProgId();
		oblId = prode.getOblId();
		prodId = prode.getProdeId();
		prodFilename=prode.getProdeFilename();
		//errores = prode.getErrors();
	}
	
	//======================================================= 
	//Control de Acceso                       
	//=======================================================
	ControlAcceso controlAcceso = new ControlAcceso(cn, contenedorSesion.getUser());
	if(prod!=null || prode!=null){
		if( (prod!=null || prode!=null) && !controlAcceso.hasPerm(progId,oblId,contenedorSesion.getUser()) ){
			String pagOrigen = request.getServletPath()+"?"+request.getQueryString();
			request.setAttribute("pagOrigen",pagOrigen);
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

<p class="salto" />

<table  cellspacing="0" cellpadding="4" class="downloadall">
	<tr>
		<td><b>Program ID:</b></td><td><%=progId %></td>
	</tr>
	<tr>
		<td><b>ObsBlock ID:</b></td><td><%=oblId %></td>
	<tr>
	<tr>
		<td><b>Prod ID:</b></td><td><%=prodId %></td>
	<tr>
	<tr>
		<td><b>File:</b></td><td><%=prodFilename %></td>
	<tr>
</table>		

<p class="salto" />

<!--   Tabla de resultados -->			
<table cellspacing="3" WIDTH="800px" border="0" >
	<tr>
		<td class="rescab" align="center" nowrap>
			Warning ID
		</td>
		<td class="rescab" align="center" nowrap>
			Warning Desc.
		</td>
	</tr>


<% 

String puerto = "";

for( int i=0; i<warnings.length; i++ ){
	String tdClass="resfield_warn";
%>
	
	<tr>
		<td class="<%=tdClass%>" nowrap align="left">
			<%=warnings[i].getErr_id() %>
		</td>
		<td class="<%=tdClass%>" nowrap align="left"><!-- Mode -->
			<%=warnings[i].getErr_desc() %>
		</td>
	</tr>
<% } // fin del For que recorre los resultados.	


for( int i=0; i<errores.length; i++ ){
	String tdClass="resfield_error";
%>
	
	<tr>
		<td class="<%=tdClass%>" nowrap align="left">
			<%=errores[i].getErr_id() %>
		</td>
		<td class="<%=tdClass%>" nowrap align="left"><!-- Mode -->
			<%=errores[i].getErr_desc() %>
		</td>
	</tr>
<% } // fin del For que recorre los resultados.

%>
			
</table>




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