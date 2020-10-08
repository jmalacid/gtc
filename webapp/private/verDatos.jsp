<%@ page import="svo.gtc.web.Html,
				 svo.gtc.web.ContenedorSesion,
				 svo.gtc.db.web.*,
				 svo.gtc.db.permisos.ControlAcceso,
				 svo.gtc.adm.DatQuery,
				 svo.gtc.adm.DatFecha,
				 
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

// *************************************************************
// *  TECLAS DEL FORMULARIO                                    *
// *************************************************************

	//---- Elementos estáticos de la página
	Html elementosHtml = new Html(request.getContextPath());
	String cabeceraPagina = elementosHtml.cabeceraPagina("Product Details","Data Product Details.");
	String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath()+"?"+request.getQueryString());
	String pie            = elementosHtml.pie();

	String submit = request.getParameter("submit");
		
%>



<%--------     I N I C I O    P Á G I N A    H T M L    -------------------%>
<html>
<%= cabeceraPagina %>
<body>
<%= encabezamiento %>

<%//Abrimos la conexión a la base de datos
Context initContext;
Connection cn=null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();

DatQuery query = new DatQuery(cn);
DatFecha ini = query.ini();
DatFecha end = query.end();


String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};

String camposOptionMeses1="";
String camposOptionMeses2="";

for(int i=1; i<=12; i++){
	String selected="";
	if(i==ini.getMonth()) selected="SELECTED";
	camposOptionMeses1+="<option value=\""+i+"\" "+selected+">"+months[i-1]+"</option>\n";

	selected="";
	if(i==end.getMonth()) selected="SELECTED";
	camposOptionMeses2+="<option value=\""+i+"\" "+selected+">"+months[i-1]+"</option>\n";
}

String camposOptionAnos1  = "";
String camposOptionAnos2  = "";
for(int i=ini.getYear(); i<=end.getYear(); i++){
	String selected="";
	if(i==ini.getYear()) selected="SELECTED";
	camposOptionAnos1 += "<option value=\""+i+"\" "+selected+">"+i+"</option>\n"; 

	selected="";
	if(i==end.getYear()) selected="SELECTED";
	camposOptionAnos2 += "<option value=\""+i+"\" "+selected+">"+i+"</option>\n"; 
}
%>
<form name="verDatos" method="get" action="verDatos.jsp" enctype="application/x-www-form-urlencoded" onSubmit="">

<p class="formbustitle"><font>Búsqueda por rango de fechas:</font></p>
			<p class="appstyle">Between:</p>
		

			<select name="mi" >
				<%=camposOptionMeses1 %>
			</select>
			<select name="yi">
				<%=camposOptionAnos1 %>
			</select>
		
			<p class="appstyle">And:</p>
		
			<select name="me" >
				<%=camposOptionMeses2 %>
			</select>
			<select name="ye">
				<%=camposOptionAnos2 %>
			</select>
		
<br><br>

<input type="submit" name="submit" value="Aceptar">


</form>
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


<%
//Mostramos valores

if(submit!=null && submit.length()>0){
	
	String mi = request.getParameter("mi");
	String yi = request.getParameter("yi");
	String me = request.getParameter("me");
	String ye = request.getParameter("ye");
	
	//Valores del mes y año
	%><IMG ALT="[Spectrum PNG plot]" SRC="<%=request.getContextPath() %>/servlet/Plot_datos?mi=<%=mi%>&yi=<%=yi%>&me=<%=me%>&ye=<%=ye%>">

<%}

%>

<%= pie %>
</body>
</html>
