<%@ page import="svo.gtc.web.Html,
	java.sql.*,
	svo.gtc.web.ContenedorSesion,
	svo.gtc.db.priv.DBPrivate,
	svo.gtc.priv.objetos.InfoError,
	svo.gtc.priv.objetos.ErrCount,
	javax.sql.*,
	javax.naming.Context,
	javax.naming.InitialContext,
	javax.naming.NamingException" %>
                 
<%@ page info="GTC Scientific Archive Homepage" %>

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

//*************************************************************
//*  TECLAS DEL FORMULARIO                                    *
//*************************************************************

	//---- Elementos estáticos de la página
	Html elementosHtml = new Html(request.getContextPath());
	String cabeceraPagina = elementosHtml.cabeceraPagina("GTC","Private zone");
	String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath()+"?"+request.getQueryString());
	String pie            = elementosHtml.pie();
%>

<%--------     I N I C I O    P Á G I N A    H T M L    -------------------%>

<%= cabeceraPagina %>

<body>
<SCRIPT LANGUAGE="JavaScript" type="text/javascript" src="../js/wz_tooltip.js"></SCRIPT>
<%= encabezamiento %>

<%//Abrimos la conexión a la base de datos
Context initContext;
Connection cn=null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();
DBPrivate union = new DBPrivate(cn);

String[] progs = union.infoProgs();
String[] errs = union.infoErrors();

//Obtenemos valores para mostrar en las estadisticas
Integer sci = union.countErr("sci");
Integer cal = union.countErr("cal");
Integer sci_c = union.countErr("sci_c");
Integer cal_c = union.countErr("cal_c");

ErrCount [] infoErr = union.countErr();


%>

<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px" >

<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER><font face="arial,helvetica,san-serif"><font
color="#FFFFFF"><font size=+2>&nbsp;The Gran Telescopio CANARIAS Archive<br><font color=red>-Private zone-</font></font></font></font>
</td>
</tr>
</table>


<form id="formulario" name="formulario" method="get"
	action="controlObs.jsp" enctype="application/x-www-form-urlencoded"
	onSubmit="">


		<p class="formbus1"><font face="arial,helvetica,san-serif"><b> Buscar por programa id:</b></font></p>
		
		<select class="gform" name="prog">
			<option value="-" selected>-</option>
			<%for(int i=0; i<progs.length;i++){ %>
					<option value="<%=progs[i] %>"><%=progs[i] %></option>
					<% 
				}%>
			</select>
		
		<p class="formbus1"><font face="arial,helvetica,san-serif"><b> Buscar por error id:</b></font></p>
		
		<select class="gform" name="err">
			<option value="-" selected>-</option>
			<%for(int i=0; i<errs.length;i++){ %>
					<option value="<%=errs[i] %>"><%=errs[i] %></option>
					<% 
				}%>
			</select>
		
	
	<br><br>
	<input type="radio" name="order" value="pend" checked/><font face="arial,helvetica,san-serif"><b>Solo valores sin revisar</b></font><br>
 	<input type="radio" name="order" value="no"/><font face="arial,helvetica,san-serif"><b>Todos los valores</b></font>
	<br><br>
<input type="submit" name="submit" value="Submit Query" />&nbsp; &nbsp;<input
	type="reset" value="Reset Form" />&nbsp; &nbsp; <br>

	</form>
	<p class="formbus1"><a href="private.jsp"> Volver al menú </a></p>
	
	<font face="arial,helvetica,san-serif"><b> Esta página es dinámica, cada vez que se accede se actualizan los valores de los errores.</b></font><br>
	<font face="arial,helvetica,san-serif"><b> Ficheros de ciencia con error: <%=sci %>  (<%=sci_c %> ya revisados)</b></font><br>
	<font face="arial,helvetica,san-serif"><b> Ficheros de calibración con error: <%=cal %>  (<%=cal_c %> ya revisados)</b><br>Consideramos ficheros de cienca 
	cuando el nombre  del fichero contiene "Imaging", "Spectroscopy", "Polarimetry" o "MOS", aunque realmente no es así.</font><br><br>
	<font face="arial,helvetica,san-serif"><b> Número de veces que se repite cada error:</b></font><br>
	
	<table class="appstyle" cellspacing="3" cellpadding="20" >
	<tr>
	<td class="rescab" align="center" nowrap> Error ID </td>	
	<td class="rescab" align="center" nowrap> Total</td>
	<td class="rescab" align="center" nowrap> Revisados</td>
	<td class="rescab" align="center" nowrap> Pendientes</td>
	<td class="rescab" align="center" nowrap> Comentario</td>
	</tr>
	<%
	for(int i=0; i<infoErr.length;i++){
		%>
		<tr>
		<td class="resfield" align="right" onmouseover="Tip('<%=infoErr[i].getError_desc().trim() %>')" onmouseout="UnTip()">&nbsp;&nbsp;<%= infoErr[i].getError_id().trim() %>&nbsp;&nbsp;</td>
		<td class="resfield" align="center">&nbsp;&nbsp;<a href="controlObs.jsp?prog=-&err=<%=infoErr[i].getError_id().trim()%>&order=all"><%= infoErr[i].getError_count() %></a>&nbsp;&nbsp;</td>
		<td class="resfield" align="center">&nbsp;&nbsp;<a href="controlObs.jsp?prog=-&err=<%=infoErr[i].getError_id().trim()%>&order=rev"><%= infoErr[i].getError_rev() %></a>&nbsp;&nbsp;</td>
		<td class="resfield" align="center">&nbsp;&nbsp;<a href="controlObs.jsp?prog=-&err=<%=infoErr[i].getError_id().trim()%>&order=pend"><%= infoErr[i].getError_pend() %></a>&nbsp;&nbsp;</td>
		<%if(infoErr[i].getError_comment()!=null){%>
		<td class="resfield" align="right">&nbsp;&nbsp;<%= infoErr[i].getError_comment() %>&nbsp;&nbsp;</td>
		<%}else{ %>
		<td class="resfield" align="right">&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<%} %>
		</tr>
	<%}%>
	</table>
<%}catch(SQLException ex){
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
} %>
<%= pie %>
</body>
</html>
