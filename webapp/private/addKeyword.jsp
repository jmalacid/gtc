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



%>
<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px" >

<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER><font face="arial,helvetica,san-serif"><font
color="#FFFFFF"><font size=+2>&nbsp;The Gran Telescopio CANARIAS Archive<br><font color=red>-Private zone-</font></font></font></font>
</td>
</tr>
</table>

<% 
try{
initContext = new InitialContext();
DataSource dso = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = dso.getConnection();

DBPrivate union = new DBPrivate(cn);

String error=null;
String prog = request.getParameter("prog");
System.out.println("prog= "+prog);
//Obtenemos un listado de todas las observaciones donde crear spoly
ProdKey[] allprod =union.allProd(prog);
//En un bucle for entramos observacion por observación.
for(int i=0; i<allprod.length; i++){
	
	error="no";
	
	System.out.println("file: "+allprod[i].getFilname());
	
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
    Fits fEntrada = new Fits(fits,compressed);	    
	
	//try{
		
	
	  	//Ingestamos ese valor en la base de datos
	  	try{
	  		
	  		BasicHDU hdu1 = fEntrada.getHDU(1); 
		    Header header1=hdu1.getHeader();
			
		    String datasec = header1.findCard("DATASEC").getValue();
		    String[] ds = datasec.split(",");
		    Double datasec1 = Double.valueOf(ds[0].split(":")[1]);
		    Double datasec2 = Double.valueOf(ds[1].split(":")[1].replaceAll("]", ""));
		    
		    //System.out.println("datasec1: "+datasec1+", datasec2: "+datasec2);
		    
		    Double crval1_1 = new Double(header1.findCard("CRVAL1").getValue());
		    Double crval2_1 = new Double(header1.findCard("CRVAL2").getValue());
		    Double crpix1_1 = new Double(header1.findCard("CRPIX1").getValue());
		    Double crpix2_1 = new Double(header1.findCard("CRPIX2").getValue());
		    Double cd1_1 = new Double(header1.findCard("CD1_1").getValue());
		    Double cd2_1 = new Double(header1.findCard("CD2_2").getValue());

		    //Obtenemos los valores
		    Double rmax1 = crval1_1+(0-crpix1_1)*cd1_1;
		    Double rmin1 = crval1_1+(datasec1-crpix1_1)*cd1_1;
		    Double decmax1 = crval2_1+(datasec2-crpix2_1)*cd2_1;
		    Double decmin1 = crval2_1+(0-crpix2_1)*cd2_1;
		    		
			//spoly'{(ramin,decmin),(ramin,decmax),(ramax,decmax),(ramax,decmin)}'
			String key= "spoly'{("+rmax1+"d,"+decmin1+"d),("+rmax1+"d,"+decmax1+"d),("+rmin1+"d,"+decmax1+"d),("+rmin1+"d,"+decmin1+"d)}'";
	  		
	  		union.updatePol(1,key,allprod[i]);
	  		
	  	}catch(Exception e){
	  		
	  		try{
	  			BasicHDU hdu1 = fEntrada.getHDU(1); 
			    Header header1=hdu1.getHeader();
				
			    String datasec = header1.findCard("DATASEC").getValue();
			    String[] ds = datasec.split(",");
			    Double datasec1 = Double.valueOf(ds[0].split(":")[1]);
			    Double datasec2 = Double.valueOf(ds[1].split(":")[1].replaceAll("]", ""));
			    
			    //System.out.println("datasec1: "+datasec1+", datasec2: "+datasec2);
			    
			    Double crval1_1 = new Double(header1.findCard("CRVAL1").getValue());
			    Double crval2_1 = new Double(header1.findCard("CRVAL2").getValue());
			    Double crpix1_1 = new Double(header1.findCard("CRPIX1").getValue());
			    Double crpix2_1 = new Double(header1.findCard("CRPIX2").getValue());
			    Double cd1_1 = new Double(header1.findCard("CD1_2").getValue());
			    Double cd2_1 = new Double(header1.findCard("CD2_1").getValue());
 
			    //Obtenemos los valores
			    Double rmax1 = crval1_1+(0-crpix1_1)*cd1_1;
			    Double rmin1 = crval1_1+(datasec1-crpix1_1)*cd1_1;
			    Double decmax1 = crval2_1+(datasec2-crpix2_1)*cd2_1;
			    Double decmin1 = crval2_1+(0-crpix2_1)*cd2_1;
			    		
				//spoly'{(ramin,decmin),(ramin,decmax),(ramax,decmax),(ramax,decmin)}'
				String key= "spoly'{("+rmax1+"d,"+decmin1+"d),("+rmax1+"d,"+decmax1+"d),("+rmin1+"d,"+decmax1+"d),("+rmin1+"d,"+decmin1+"d)}'";
		  		
		  		union.updatePol(1,key,allprod[i]);
		  		error="si3";
	  		}catch(Exception e1){
	  			error="si1";
		  		e.printStackTrace();
	  		}
	  		
	  		
	  	}
	  	try{
	  		
	  		BasicHDU hdu2 = fEntrada.getHDU(2);
		    Header header2=hdu2.getHeader();
			
		    String datasec = header2.findCard("DATASEC").getValue();
		    String[] ds = datasec.split(",");
		    Double datasec1 = Double.valueOf(ds[0].split(":")[1]);
		    Double datasec2 = Double.valueOf(ds[1].split(":")[1].replaceAll("]", ""));
		    
		    
		    Double crval1_2 = new Double(header2.findCard("CRVAL1").getValue());
		    Double crval2_2 = new Double(header2.findCard("CRVAL2").getValue());
		    Double crpix1_2 = new Double(header2.findCard("CRPIX1").getValue());
		    Double crpix2_2 = new Double(header2.findCard("CRPIX2").getValue());
		    Double cd1_2 = new Double(header2.findCard("CD1_1").getValue());
		    Double cd2_2 = new Double(header2.findCard("CD2_2").getValue());
		    
		    Double rmax2 = crval1_2+(0-crpix1_2)*cd1_2;
		    Double rmin2 = crval1_2+(datasec1-crpix1_2)*cd1_2;
		    Double decmax2 = crval2_2+(datasec2-crpix2_2)*cd2_2;
		    Double decmin2 = crval2_2+(0-crpix2_2)*cd2_2;
			
			String key2= "spoly'{("+rmax2+"d,"+decmin2+"d),("+rmax2+"d,"+decmax2+"d),("+rmin2+"d,"+decmax2+"d),("+rmin2+"d,"+decmin2+"d)}'";
	  		
	  		union.updatePol(2,key2,allprod[i]);
	  		
	  	}catch(Exception e){
	  		try{
	  			BasicHDU hdu2 = fEntrada.getHDU(2);
			    Header header2=hdu2.getHeader();
				
			    String datasec = header2.findCard("DATASEC").getValue();
			    String[] ds = datasec.split(",");
			    Double datasec1 = Double.valueOf(ds[0].split(":")[1]);
			    Double datasec2 = Double.valueOf(ds[1].split(":")[1].replaceAll("]", ""));
			    
			    
			    Double crval1_2 = new Double(header2.findCard("CRVAL1").getValue());
			    Double crval2_2 = new Double(header2.findCard("CRVAL2").getValue());
			    Double crpix1_2 = new Double(header2.findCard("CRPIX1").getValue());
			    Double crpix2_2 = new Double(header2.findCard("CRPIX2").getValue());
			    Double cd1_2 = new Double(header2.findCard("CD1_2").getValue());
			    Double cd2_2 = new Double(header2.findCard("CD2_1").getValue());
			    
			    Double rmax2 = crval1_2+(0-crpix1_2)*cd1_2;
			    Double rmin2 = crval1_2+(datasec1-crpix1_2)*cd1_2;
			    Double decmax2 = crval2_2+(datasec2-crpix2_2)*cd2_2;
			    Double decmin2 = crval2_2+(0-crpix2_2)*cd2_2;
				
				String key2= "spoly'{("+rmax2+"d,"+decmin2+"d),("+rmax2+"d,"+decmax2+"d),("+rmin2+"d,"+decmax2+"d),("+rmin2+"d,"+decmin2+"d)}'";
		  		
		  		union.updatePol(2,key2,allprod[i]);
		  		error = "si3";
	  		}catch(Exception e1){
	  			if(error.equals("si1")){
		  			error="si";
		  		}else{
		  			error="si2";
		  		}
		  		e.printStackTrace();
	  		}
	  		
	  		
	  		
	  	}
	  	if(error.equals("no")){
	  	%><p class="formbus1"><font face="arial,helvetica,san-serif" style="color:blue"><b> <%=allprod[i].getProg()+"-"+allprod[i].getObl()+"-"+allprod[i].getProd() %> : Ok </b></font></p>
	  	<%}else if(error.equals("si")){ %>
	  	<p class="formbus1"><font face="arial,helvetica,san-serif" style="color:red"><b> <%=allprod[i].getProg()+"-"+allprod[i].getObl()+"-"+allprod[i].getProd() %> : ERROR </b></font></p>
	  	<%}else if(error.equals("si1")){ %>
	  	<p class="formbus1"><font face="arial,helvetica,san-serif" style="color:orange"><b> <%=allprod[i].getProg()+"-"+allprod[i].getObl()+"-"+allprod[i].getProd() %> : ERROR solo en el POLIG </b></font></p>
	  	<%}else if(error.equals("si2")){ %>
	  	<p class="formbus1"><font face="arial,helvetica,san-serif" style="color:orange"><b> <%=allprod[i].getProg()+"-"+allprod[i].getObl()+"-"+allprod[i].getProd() %> : ERROR solo en el POLIG2 </b></font></p>
	  	<%}else if(error.equals("si3")){ %>
	  	<p class="formbus1"><font face="arial,helvetica,san-serif" style="color:green"><b> <%=allprod[i].getProg()+"-"+allprod[i].getObl()+"-"+allprod[i].getProd() %> : Solucionado el error </b></font></p>
	  	<% } %>
	  	
	  <%-- }catch(Exception e1){
	  		BasicHDU hdu = fEntrada.getHDU(0); 
	  		Header header=hdu.getHeader();
	  		
	  		String ra = header.findCard("RADEG").getValue();
	  		String de = header.findCard("DECDEG").getValue();
	  		
	  		String key= "spoint'("+ra+"d,"+de+"d)'";
			//Ingestamos ese valor en la base de datos
		  	union.updatePol(3,key,allprod[i]);
		  	%><p class="formbus1"><font face="arial,helvetica,san-serif" style="color:blue"><b> <%=allprod[i].getProg()+"-"+allprod[i].getObl()+"-"+allprod[i].getProd() %> : Ok Point </b></font></p>
		  	<%
	  	} --%>
	  	
	<% }catch(Exception e){
		
	e.printStackTrace();
	
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
	
<%= pie %>
</body>
</html>