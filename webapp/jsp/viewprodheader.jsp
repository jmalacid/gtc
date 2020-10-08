<%@ page import="svo.gtc.web.Html,
				 svo.gtc.web.ContenedorSesion,
				 svo.gtc.db.web.*,
				 svo.gtc.db.prodat.ProdDatosAccess,
				 svo.gtc.db.prodat.ProdDatosDb,
				 svo.gtc.db.prodred.ProdRedAccess,
				 svo.gtc.db.prodred.ProdRedDb,
				 svo.gtc.db.proderr.ProdErrorAccess,
				 svo.gtc.db.proderr.ProdErrorDb,
				 svo.gtc.db.permisos.ControlAcceso,
				
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

	String par_prodId = request.getParameter("prod_id");
	String[] aux = new String[3];
	if(par_prodId!=null){
		aux = par_prodId.split("\\.\\.");
	}

	String prog_id = "";
	String obl_id="";
	Integer prod_id = new Integer(-1);
	
	try{
		prog_id = aux[0];
		obl_id=aux[1];
		if(aux[2]!=null){
			prod_id= new Integer(aux[2]);
		}
	}catch(Exception e){}

	
	String par_predId = request.getParameter("pred_id");
	Integer pred_id = new Integer(-1);
	
	try{
		if(par_predId!=null){
			pred_id= new Integer(par_predId);
		}
	}catch(Exception e){}

	String pred_type = request.getParameter("pred_type");
	
	//======================================================= 
	//Control de Acceso                       
	//=======================================================
	ControlAcceso controlAcceso = new ControlAcceso(cn, contenedorSesion.getUser());
	ProdDatosAccess datosAccess = new ProdDatosAccess(cn);
	ProdDatosDb producto=datosAccess.selectById(prog_id, obl_id, prod_id);

	ProdRedAccess datosRedAccess = new ProdRedAccess(cn);
	ProdRedDb productoRed=datosRedAccess.selectById(pred_id);
	
	
	ProdErrorAccess errorAccess = new ProdErrorAccess(cn);
	ProdErrorDb productoErr=errorAccess.selectProdErrorById(prog_id, obl_id, prod_id);
	if(producto!=null){
		if(!controlAcceso.hasPerm(producto.getProgId(),producto.getOblId(),producto.getProdId(),contenedorSesion.getUser())){
			
			String pagOrigen = request.getServletPath()+"?"+request.getQueryString();
			request.setAttribute("pagOrigen",pagOrigen);
			%>
			<jsp:forward page="accessDenied.jsp"/>
			<%

		}
	
	}
	
	String salida = ""; 
	
	if(producto!=null){
		salida = producto.getHeader();
	}else if(productoRed!=null){
		if(pred_type!=null && (pred_type.equals("HiPERCAM") || pred_type.equals("txt"))){
			salida = productoRed.getHeaderText();
		}else{
			salida = productoRed.getHeader();
		}
	}else if(productoErr!=null){
		salida = productoErr.getHeader();
	}

%>



<%--------     I N I C I O    P � G I N A    H T M L    -------------------%>
<html>
<%= cabeceraPagina %>
<body>
<%= encabezamiento %>

<pre class="monospace"><%=salida %></pre>

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