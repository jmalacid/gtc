<%@ page import="svo.gtc.web.Html,
	java.sql.*,
	svo.gtc.web.ContenedorSesion,
	svo.gtc.db.priv.DBPrivate,
	svo.gtc.priv.objetos.FormProgram,
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

//con.close();
%>
<%
FormProgram formulario = null;

try{
	formulario=new FormProgram(request);
}catch(Exception e){
	//MSG="Unable to process request.";
	e.printStackTrace();
}

String[] filName = formulario.getProgList();

%>
<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px" >

<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER><font face="arial,helvetica,san-serif"><font
color="#FFFFFF"><font size=+2>&nbsp;The Gran Telescopio CANARIAS Archive<br><font color=red>-Private zone-</font></font></font></font>
</td>
</tr>
</table>

<p class="formbus1"><font face="arial,helvetica,san-serif"><b> Programas que no estaban en la base de datos:</b></font></p>

<%for(int i=0; i<filName.length; i++){ 

	
	
	try{
	if(!filName[i].startsWith("propID")){
		//Obtenemos los valores del programma por línea
		String[] values = filName[i].split(",");
		
		String prog_id = values[0].trim();
		String title = values[1].replaceAll("'", " ").replaceAll("\"", " ").trim();
		
		//Para obtener el nombre nos quedamos la primera parte y el resto sería apellido
		String[] user = values[2].replaceAll("'", " ").replaceAll("\"", " ").trim().split(" ");
		String user_name = user[0];
		String user_surname = values[2].replaceAll("'", " ").replaceAll("\"", " ").replaceAll(user_name, "").trim();
		
		String user_id = values[3].trim();
		String user_mail = values[4].trim();
		
		Integer isProg = union.isProg(prog_id);//Comprueba si existe el programa
	
		if(isProg>0){ //ya existe, actualizamos
			union.updateProg(prog_id, title, user_id);
			
		}else{
			
			%><p class="formbus1"><font face="arial,helvetica,san-serif"><b> <%=values[0] %></b></font></p>
			<%
		
			//Introducimos la información en la tabla programa (título y prog_id)
			union.insertProg(prog_id, title, user_id);
		}
			//Introducimos información del usuario si no existe (estado 1)
				//Por ahora solo los -17...
			//Buscamos si existe e usuario
			
			//if(prog_id.contains("-17")){
				Integer hayUser = union.isUser(user_id);
				if(hayUser==0){
					union.insertUser(user_name, user_surname, user_id, user_mail);
					
				}
			//}
	}	
	}catch(Exception e){
		//Error probablemente a que no tenemos todos los campos necesarios, imprimimos línea
		System.out.println("línea error: "+filName[i]);
		System.out.println(e.getMessage());
		%><p class="formbus1"><font face="arial,helvetica,san-serif" color="red"><b>ERROR: <%=filName[i] %></b></font></p>
		<%
		
	}
} 
}catch(SQLException ex){
	//fallo sql
	System.out.println("Error : "+ex.getMessage());
}catch(NamingException ex){
	System.out.println("Error al intentar obtener el DataSource:"+ex.getMessage());
}catch(Exception e){
	System.out.println("Error común: "+e.getMessage());
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

<a href="private.jsp">Volver</a>

<%= pie %>
</body>
</html>