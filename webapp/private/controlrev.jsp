<%@page import="svo.gtc.web.Html,
				svo.gtc.utiles.Mail,
				java.sql.*,
				svo.gtc.db.priv.DBPrivate,
				svo.gtc.priv.objetos.ObsInfo,
				svo.gtc.web.ContenedorSesion,
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

<!-- titulo -->
<br>
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

if(submit!=null && submit.length()>0){
	
	String[] prod = request.getParameterValues("prod_id");
	
	//Abrimos la conexión a la base de datos
	Context initContext;
	Connection cn=null;

	try{
	initContext = new InitialContext();
	DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
	cn = ds.getConnection();
	DBPrivate union = new DBPrivate(cn);
	
	
	//Por cada prod tenemos que ingestarlo en la base de datos
	for(int i=0; i<prod.length; i++){
		//Obtenemos los valores
		String[] values = prod[i].split("-.-");
		
		union.updateRev(values[0], values[1], values[2], values[3]);
	}
	
	
	//Aquí mandamos el email y redirigimos a otra página
	//Mail prueba = new Mail(request.getQueryString());
	
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
	
} %>
	
	<jsp:forward page="errors.jsp"/> 
	
	
<%
	}else{
		%> 
		<jsp:forward page="errors.jsp"/> 
	<%
	}%> 
	


<%= pie %>