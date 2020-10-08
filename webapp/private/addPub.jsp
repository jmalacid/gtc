<%@ page import="svo.gtc.web.Html,
	java.sql.*,
	svo.gtc.web.ContenedorSesion,
	svo.gtc.db.priv.DBPrivate,
	svo.gtc.priv.objetos.Bibcodes,
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

	//---- Elementos est�ticos de la p�gina
	Html elementosHtml = new Html(request.getContextPath());
	String cabeceraPagina = elementosHtml.cabeceraPagina("GTC","Private zone");
	String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath()+"?"+request.getQueryString());
	String pie            = elementosHtml.pie();
%>

<%--------     I N I C I O    P � G I N A    H T M L    -------------------%>

<%= cabeceraPagina %>

<body>

<%= encabezamiento %>
<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px" >

<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER><font face="arial,helvetica,san-serif"><font
color="#FFFFFF"><font size=+2>&nbsp;The Gran Telescopio CANARIAS Archive<br><font color=red>-Private zone-</font></font></font></font>
</td>
</tr>
</table>
<br>
<%
String submit = request.getParameter("submit");

String bibcode = null;
try{
	bibcode = request.getParameter("bibcode");
}catch(Exception e){
	//No viene el bibcode
}

///// COMPROBACI�N DE VALORES DEL FORMULARIO
String msg = "";
Bibcodes parametrosFormulario = null;
if(submit!=null && submit.length()>0){
	try{
		parametrosFormulario = new Bibcodes(request);
		parametrosFormulario.comprueba();
	}catch(Exception e){
		msg+=e.getMessage();
	}
}


if(msg.length()>0){
%>
	<p class="formbus1"><font color="red"><blink><%=msg %></blink></font></p>
<% 	
}else if(submit!=null && submit.length()>0){
%> 
	<jsp:forward page="addPub_c.jsp"/> 
<%	} %>



<%
//Abrimos la conexi�n a la base de datos
Context initContext;
Connection cn=null;
String[] autor = null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();

DBPrivate union = new DBPrivate(cn);

autor = union.infoAutor();
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

<br>

<form id="formulario" name="formulario" method="get"
	action="addPub.jsp" enctype="application/x-www-form-urlencoded"
	onSubmit="">

<table class="appstyle" cellspacing="3" cellpadding="20" >
<tr>
<td class="rescab" align="right"><font face="arial,helvetica,san-serif"><b> Bibcode:</b></font> </td>
		
<td class="resfield" align="left">		
<%if(bibcode!=null){ %>
 <input type="text" name="bib" value="<%=bibcode %>" maxlength="20" size="20" />
 <%}else{ %>
 <input type="text" name="bib" maxlength="20" size="20" />
 <%} %>
  </td>
</tr>
<tr>
<td class="rescab" align="right"><font face="arial,helvetica,san-serif"><b> Comentarios:</b></font> </td>
		
<td class="resfield" align="left">		 <input type="text" name="comment" maxlength="100" size="50" /> </td>
</tr>
<tr>
<td class="rescab" align="right"><font face="arial,helvetica,san-serif"><b> Prog id:</b></font> </td>
		
<td class="resfield" align="left">		 <input type="text" name="prog" maxlength="20" size="20" /> </td>
</tr>
<tr>
<td class="rescab" align="right"><font face="arial,helvetica,san-serif"><b> Obl id:</b></font> </td>
		
<td class="resfield" align="left">		 <input type="text" name="obl" maxlength="8" size="8" /> </td>
</tr>
<tr>
<td class="rescab" align="right"><font face="arial,helvetica,san-serif"><b> Prod id:</b></font> </td>
		
<td class="resfield" align="left">		 <input type="text" name="prod" maxlength="50" size="20" /> </td>
</tr>

<tr>
<td class="rescab" align="right"><font face="arial,helvetica,san-serif"><b> Autor:</b></font></td>
<td class="resfield" align="left">
		<%if(autor.length>0){ %>
		<select class="gform" name="aut_id">
			<option value="-"selected>-</option>
			<%for(int i=0; i<autor.length;i++){ 
			String [] valor = autor[i].split("--");%>
					<option value="<%=valor[0] %>"><%=valor[1] %></option>
			<% 
				}%>
		</select>
		<%} %>
		</td></tr>
		<tr>
		<td colspan="2" class="formbus1"><br><font face="arial,helvetica,san-serif"  size="+1"><b>* Completar en caso de a�adir nuevo autor:</b></font></td></tr>
		<tr>
		<td class="rescab" align="right"><font face="arial,helvetica,san-serif"><b> Nombre:</b></font></td><td class="resfield" align="left"><input type="text" name="autor" maxlength="50" size="30" /></td>
		</tr>
		<tr>
		<td class="rescab" align="right"><font face="arial,helvetica,san-serif"><b> Email:</b></font></td><td class="resfield" align="left"><input type="text" name="email" maxlength="50" size="30" /></td>
		</tr>
		<tr>
		<td class="rescab" align="right"><font face="arial,helvetica,san-serif"><b> Detalles:</b></font></td><td class="resfield" align="left"><input type="text" name="det" maxlength="100" size="40" /></td>
		</tr>
</table>
	<br><br>
<input type="submit" name="submit" value="Submit Query" />&nbsp; &nbsp;<input
	type="reset" value="Reset Form" />&nbsp; &nbsp; <br>

	</form>
	<a href="reduc.jsp"> Volver </a>
	
	

<%= pie %>
</body>
</html>