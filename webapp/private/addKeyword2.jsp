<%@ page import="svo.gtc.web.Html,
	java.sql.*,
	java.io.File,
	nom.tam.fits.BasicHDU,
	nom.tam.fits.Fits,
	nom.tam.fits.FitsException,
	nom.tam.fits.Header,
	svo.gtc.priv.objetos.ProdKey,
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

%>
<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px" >

<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER><font face="arial,helvetica,san-serif"><font
color="#FFFFFF"><font size=+2>&nbsp;The Gran Telescopio CANARIAS Archive<br><font color=red>-Private zone-</font></font></font></font>
</td>
</tr>
</table>

<% 
DBPrivate union = new DBPrivate(cn);

//Obtenemos un listado de todas las observaciones donde crear spoly
ProdKey[] allprod =union.allProdSpe();
//En un bucle for entramos observacion por observación.
for(int i=0; i<allprod.length; i++){
	
	try{
	//Obtemos el valor del keyword que nos interesa
	File fits= new File(allprod[i].getFilname());
	
	boolean compressed = false;
	
	if(fits.getName().toUpperCase().endsWith(".GZ")){
		compressed = true;
	}
    
	//Caso de buscar solo un objecto
	//Fits fEntrada = new Fits(fits,compressed);	    
    //BasicHDU hdu = fEntrada.getHDU(0);   		    
    //Header header=hdu.getHeader();
	
    
    //String key = header.findCard("OBJECT").getValue();
	//Ingestamos ese valor en la base de datos
	//union.updateKey(key,allprod[i]);
	
	//Caso de trabajar con más de un objeto
//    Fits fEntrada = new Fits(fits,compressed);	    
//    BasicHDU hdu = fEntrada.getHDU(0); 
//    Header header=hdu.getHeader();
	
//    String ra = header.findCard("").getValue();
//    String dec = header.findCard("").getValue();
    
	//spoly'{(ramin,decmin),(ramin,decmax),(ramax,decmax),(ramax,decmin)}'
	String key= "spoint'("+allprod[i].getRa()+"d,"+allprod[i].getDec()+"d)'";
	
  	//Ingestamos ese valor en la base de datos
  	union.updatePol(3,key,allprod[i]);
  	
  	%><p class="formbus1"><font face="arial,helvetica,san-serif" style="color:blue"><b> <%=allprod[i].getProg()+"-"+allprod[i].getObl()+"-"+allprod[i].getProd() %> : Ok </b></font></p>
	<%}catch(Exception e){
	e.printStackTrace();
	%>
	<p class="formbus1"><font face="arial,helvetica,san-serif" style="color:red"><b> <%=allprod[i].getProg()+"-"+allprod[i].getObl()+"-"+allprod[i].getProd() %> : No se ha introducido </b></font></p>
	<%	
	}
}
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
	
<%=pie %>
</body>
</html>