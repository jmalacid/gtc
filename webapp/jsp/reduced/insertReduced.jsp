<%@ page import="svo.gtc.web.ContenedorSesion,
				svo.gtc.web.Form,
				svo.gtc.web.Html,
				
				svo.gtc.db.usuario.UsuarioDb,
				svo.gtc.db.colecciondatos.ColeccionDatosAccess,
				svo.gtc.db.colecciondatos.ColeccionDatosDb,
				
				svo.gtc.web.reduced.RedUploadManager,
				svo.gtc.web.reduced.RedProdStatus,
				
				java.sql.*,
				javax.sql.*,
				javax.naming.Context,
				javax.naming.InitialContext,
				javax.naming.NamingException" %>
                 
<%@ page isThreadSafe="true" %>
<%@ page info="GTC Reducted Data" %>


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
if(session!=null && session.getAttribute("contenedorSesion")!=null){
	contenedorSesion = (ContenedorSesion)session.getAttribute("contenedorSesion");
}else{
	contenedorSesion = new ContenedorSesion();
}

Form form = (Form)request.getAttribute("form");

//// Vemos si ya han sido subidos ficheros.
RedUploadManager manager=(RedUploadManager)request.getAttribute("redUploadManager"); 

//// Vemos si se ha realizado la inserción de ficheros.
RedProdStatus[] listado=(RedProdStatus[])request.getAttribute("listadoRedProdStatus"); 

if(1==0){
	request.setAttribute("mensaje", "Sorry, not sufficient privileges to enter reduced products.");
	request.setAttribute("origen","/jsp/reduced/insertReduced.jsp");
	getServletContext().getRequestDispatcher("/jsp/error.jsp").forward(request,response);
}



//---- Elementos estáticos de la página
Html elementosHtml = new Html(request.getContextPath());
String cabeceraPagina = elementosHtml.cabeceraPagina("Insert reduced data","Insert reduced data");
String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath());
String pie            = elementosHtml.pie();






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
GTC Archive: Insert new reduced products.
</td>
</tr>
</tbody>
</table>

<%

/// Comprobación usuario registrado
if(contenedorSesion.getUser()==null){
%>

<p class="appstyle salto"><font size="+1">You must log in to access this functionality.</font></p>
<p class="appstyle salto">
<form method="post" action="<%=request.getContextPath() %>/secure/login.jsp?origen=/jsp/reduced/insertReduced.jsp">
<input type="submit" name="Log in" value="Log in">
</form>
</p>


<%
}else if(manager==null && listado==null){
	UsuarioDb usuario = contenedorSesion.getUser();
	ColeccionDatosAccess colAccess= new ColeccionDatosAccess(cn);
	ColeccionDatosDb[] cols = colAccess.selectByUsr(usuario.getUsrId());
%>

<p class="appstyle salto">

<form name="insertReduced" method="post" action="<%=request.getContextPath() %>/servlet/Controlador" enctype="multipart/form-data">
<input type="hidden" name="origen" value="/jsp/reduced/insertReduced.jsp"></input>

<table class="appstyle" border="0" CELLSPACING="5" WIDTH="640em" bgcolor="#EEEEEE">
	<tr>
		<td width="100em">
			Data collection:
		</td>
		<td>
			<select name="col_id">
			<% for(int i=0; i<cols.length; i++){%>
				<option value="<%=cols[i].getColId()%>"><%=cols[i].getColName() %></option>
			<% }%>
			</select>
		</td>
		<td rowspan="3" valign="center">
			<input type="submit" name="submitNewCol" value="New Collection"/>
		</td>
	</tr>
</table>

<table class="appstyle" border="0" CELLSPACING="5" WIDTH="640em">
	<tr>
		<td width="300em">
			File (individual FITS, ZIP file or TAR.GZ file containing several FITS files):
		</td>
	</tr>
	<tr>
		<td>
			<input type="file" name="fileUpload">
		</td>
	</tr>
</table>

<p class="appstyle salto"><input type="submit" name="submitUploadReduced" value="Upload"></p>
</form>
<%
}else if(manager!=null){  /// Verificación de productos

	listado = manager.getProductList();
	boolean hayGreen=false;
	%>
<p class="salto">
<form name="insertReduced" method="post" action="<%=request.getContextPath() %>/servlet/Controlador" enctype="multipart/form-data">
<input type="hidden" name="origen" value="/jsp/reduced/insertReduced.jsp"></input>
<input type="hidden" name="colId" value="<%=form.getParameterValues("col_id")[0] %>"></input>

<table class="appstyle" border="0" CELLSPACING="5" WIDTH="640em">
	<tr>
		<td class="rescab">File</td>
		<td class="rescab">Status</td>
		<td class="rescab">Desc</td>
	</tr>
	<%	
	for(RedProdStatus c: listado){
		if(c.getStatus().equals(RedProdStatus.OK)) hayGreen=true;
%>
	<tr>
		<td class="resfield"><%=c.getFile().getName() %></td>
		<td class="resfield" style="background-color: <%=c.getWebColor() %>"><%=c.getStatus() %></td>
		<td class="resfield"><%if(c.getDesc()!=null){ %> <%=c.getDesc()%> <%} %></td>
	</tr>
<%
	}
	
	if(hayGreen){ session.setAttribute("listadoRedProdStatus", listado); }
%>
</table>

<p class="appstyle salto"><a href="<%=request.getContextPath() %>/jsp/reduced/insertReduced.jsp">&lt;&lt;Back</a> <%if(hayGreen){ %><input type="submit" name="submitInsertReduced" value="Upload green"> <%} %></p>

</form>




<%
}else if(listado!=null){
	boolean hayGreen=false;
%>

<p class="salto">

<table class="appstyle" border="0" CELLSPACING="5" WIDTH="640em">
	<tr>
		<td class="rescab">File</td>
		<td class="rescab">Desc</td>
	</tr>
	<%	
	for(RedProdStatus c: listado){
		if(c.getStatus().equals(RedProdStatus.OK)) hayGreen=true;
%>
	<tr>
		<td class="resfield"><%=c.getFile().getName() %></td>
		<td class="resfield"><%if(c.getDesc()!=null){ %> <%=c.getDesc()%> <%} %></td>
	</tr>
<%
	}
%>
</table>

<p class="appstyle salto"><a href="<%=request.getContextPath() %>/">&lt;&lt;Home</a></p>

<%
}  /// Fin del else
%>

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
