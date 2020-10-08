<%@ page import="svo.gtc.web.Html,
	java.sql.*,
	svo.gtc.web.ContenedorSesion,
	svo.gtc.db.priv.DBPrivate,
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

<%= encabezamiento %>

<%
//Abrimos la conexión a la base de datos
Context initContext;
Connection cn=null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();

DBPrivate union = new DBPrivate(cn);

String[] state = union.infoState();

%>

<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px" >

<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER><font face="arial,helvetica,san-serif"><font
color="#FFFFFF"><font size=+2>&nbsp;The Gran Telescopio CANARIAS Archive<br><font color=red>-Private zone-</font></font></font></font>
</td>
</tr>
</table>


<form id="formulario" name="formulario" method="get"
	action="controlState.jsp" enctype="application/x-www-form-urlencoded"
	onSubmit=""><input type="hidden" value="pub_bibcode" name="order" />


		<p class="formbus1"><font face="arial,helvetica,san-serif"><b> Buscar por programa bibcode:</b></font></p>
		
		 <input type="text" name="bib" maxlength="20" size="20" />
		
		<p class="formbus1"><font face="arial,helvetica,san-serif"><b> Buscar por estado:</b></font></p>
		
		<select class="gform" name="state">
			<option value="-" selected>-</option>
			<%for(int i=0; i<state.length;i++){ 
			String [] valor = state[i].split("--");%>
					<option value="<%=valor[0] %>"><%=valor[1] %></option>
					<% 
				}%>
			</select>
		
	
	<br><br>
<input type="submit" name="submit" value="Submit Query" />&nbsp; &nbsp;<input
	type="reset" value="Reset Form" />&nbsp; &nbsp; <br>
	
	
		
	<p class="formbus1"><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<a href="addPub.jsp"><b>Añadir publicación nueva </b></a></p>
	<p class="formbus1"><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<a href="private.jsp"> Volver al menú </a></p>
	</form>
	
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