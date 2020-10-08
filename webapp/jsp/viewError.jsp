<%@ page import="svo.gtc.web.Html,
				 svo.gtc.web.ContenedorSesion,
				 svo.gtc.web.WebUtils,
				 
				svo.gtc.db.priv.DBPrivate,
				svo.gtc.priv.objetos.ObsInfo,
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
ObsInfo[] fichs = null; 

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
%>
	<%--------     I N I C I O    P Á G I N A    H T M L    -------------------%>
	<html>
	<%=cabeceraPagina%>
	<SCRIPT LANGUAGE="JavaScript" type="text/javascript" src="/gtc/js/wz_tooltip.js"></SCRIPT>
	<body>
	<%=encabezamiento%>	
	<%
	if(contenedorSesion.getUser()!=null && contenedorSesion.getUser().getUsrId()!=null){
		DBPrivate union = new DBPrivate(cn);
		fichs = union.obsInfo(contenedorSesion.getUser().getUsrId());
		
		%><table class="appstyle" cellspacing="3" cellpadding="20" >
		<tr>
		<td class="rescab" align="center" nowrap> Program </td>	
		<td class="rescab" align="center" nowrap> observing block</td>	
		<td  colspan="2" class="rescab" align="center" nowrap> Fichero </td>	
		<td class="rescab" align="center" nowrap> Error ID</td>	
		<td class="rescab" align="center" nowrap> Error </td>
		</tr>
		
		<%for(int i=0; i<fichs.length;i++){ 
		String prod_id = fichs[i].getProgram()+".."+fichs[i].getBlock()+".."+ fichs[i].getProde_id();
		%>
		<tr>
		<td class="resfield" align="right">&nbsp;&nbsp;<%= fichs[i].getProgram() %>&nbsp;&nbsp;</td>
		<td class="resfield" align="right">&nbsp;&nbsp;<%= fichs[i].getBlock() %>&nbsp;&nbsp;</td>
		<td class="resfield" align="right">&nbsp;&nbsp;<a href="<%=request.getContextPath() %>/servlet/FetchProd?prod_id=<%=prod_id%>"><%= fichs[i].getFilename() %></a>&nbsp;&nbsp;</td>
		<td class="resfield" align="right">&nbsp;&nbsp;<a target="_blank" href="../jsp/viewprodheader.jsp?prod_id=<%=prod_id%>">Header</a>&nbsp;&nbsp;</td>
		<td class="resfield" align="right">&nbsp;&nbsp;<%= fichs[i].getErr_id() %>&nbsp;&nbsp;</td>
		<td class="resfield" align="right">&nbsp;&nbsp;<%= fichs[i].getError() %>&nbsp;&nbsp;</td>
		</tr>
		<%} %>
	</table>
		<%
	}else{
		%>
		<jsp:forward page="accessDenied.jsp"/>
		<%
	}
%>

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