<%@ page import="svo.gtc.web.Html,
				 svo.gtc.web.ContenedorSesion,
				 
				 svo.gtc.db.news.*,
				 
				 java.sql.Connection,
				 java.sql.SQLException,
				
				 java.net.URLEncoder,
                 java.util.Locale,
                 java.util.Vector,
                 java.util.Enumeration,
                 
                 org.hibernate.HibernateException,
                 svo.gtc.db.HibernateUtil,
				 org.hibernate.SessionFactory,
				 svo.gtc.db.usuario.UsuarioDb,
				 
				javax.sql.*,
				javax.naming.Context,
				javax.naming.InitialContext,
				javax.naming.NamingException" %>
                 
<%@ page isThreadSafe="true" %>
<%@ page info="GTC Scientific Archive Homepage" %>


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

//*************************************************************
//*  TECLAS DEL FORMULARIO                                    *
//*************************************************************

	//---- Elementos estáticos de la página
	Html elementosHtml = new Html(request.getContextPath());
	String cabeceraPagina = elementosHtml.cabeceraPagina("Product Details","Data Product Details.");
	String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), "");
	String pie            = elementosHtml.pie();




%>
<%--------     I N I C I O    P Á G I N A    H T M L    -------------------%>

<%= cabeceraPagina %>

<body>


<%= encabezamiento %>


<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px" >
<caption><!-- Introduction -->
<!-- Table of resources -->
<!-- Page footer --></caption>

<tr BGCOLOR="#3A50A0">
<td ALIGN=CENTER><font face="arial,helvetica,san-serif"><font
color="#FFFFFF"><font size=+2>&nbsp;The Gran Telescopio CANARIAS Public Archive</font></font></font>
</td>
</tr>
<tr>
<td ALIGN="justify"><br><i><font face="arial,helvetica,geneva">

<font size="+0">

This data server provides access to the <a href="http://www.gtc.iac.es/">GTC</a> Public Archive. GTC data become public once the proprietary (1 year) is over. The 
<b>Gran Telescopio CANARIAS (GTC)</b>, is a 10.4m telescope with a segmented primary mirror. 
It is located in one of the top astronomical sites in the Northern Hemisphere: the 
<a href="http://www.iac.es/eno.php?op1=2&lang=en">Observatorio del Roque de los Muchachos</a> 
(ORM, La Palma, Canary Islands). The GTC is a Spanish initiative led by the 
<a href="http://www.iac.es">Instituto de Astrofísica de Canarias (IAC)</a>. The project also includes the participation of Mexico 
(<a href="http://www.astroscu.unam.mx/">Instituto de Astronomía de la Universidad Nacional 
Autónoma de México (IA-UNAM)</a> and <a href="http://www.inaoep.mx/">Instituto Nacional de 
Astrofísica, Óptica y Electrónica (INAOE )</a>) and the US 
(<a href="http://www.astro.ufl.edu/">University of Florida (UFL)</a>). 
 The project is actively supported by the Spanish Government and the Local Government from the Canary Islands 
through the European Funds for Regional Development (FEDER) provided by the European Union. 


</font></font></i>
<br>&nbsp;
&nbsp;
<br>
<tr>
<td ALIGN=LEFT>

<% 
//Abrimos la conexión a la base de datos
Context initContext;
Connection cn=null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();


////// NEWS  /////////////
NewsAccess newsAccess = new NewsAccess(cn);
NewsDb[] news = null;//newsAccess.selectRecent();

if(news!=null && news.length>0){ 
%>
<center>
<table>
<tr>
	<td colspan="2">
		<b><center><font face="arial,helvetica,geneva"><font size=+0 color="red">
			<blink>APPLICATION NEWS</blink>  
		</font></font></b></center>
	</td>
</tr>
<%for(int i=0; i<news.length;i++){ %>
<tr>
	<td>
		<center><font face="arial,helvetica,geneva"><font size=-1 color="red">
          <%=news[i].getNewsPhrase()%> </p>
		</font></font></center>
	</td>
</tr>
<%} %>
</table>
</center>
<br>
<br>

<% } %>

<% }catch(SQLException ex){
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



<% 
boolean isAdm =false;
boolean isLog = false;

if(contenedorSesion.getUser()!=null){

	SessionFactory factory = HibernateUtil.getSessionFactory();
	
	try {
		factory.getCurrentSession().beginTransaction();
		UsuarioDb u = ((UsuarioDb)factory.getCurrentSession().get(UsuarioDb.class, new String(contenedorSesion.getUser().getUsrId().trim())));
		isLog=true;
		isAdm=u.isAdm();
		factory.getCurrentSession().getTransaction().commit();
		
		
	} catch (HibernateException e) {
		factory.getCurrentSession().getTransaction().rollback();
		e.printStackTrace();
	}

}

%>

<!-- **********  Tabla enlaces **************************  -->
<table BORDER=0 CELLSPACING=0 CELLPADDING=2 WIDTH="800px">
<tr>
<td BGCOLOR="#3A50A0" ALIGN=CENTER colspan="2"><font face="arial,helvetica,san-serif"><font color="#FFFFFF"><font size=+1>&nbsp;Resources&nbsp;</font></font></font></td>


<td rowspan=10></td>
<!-- <td rowspan=10 ALIGN=left>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<div id="clustrmaps-widget"></div><script type="text/javascript">var _clustrmaps = {'url' : 'gtc.sdc.cab.inta-csic.es/gtc/', 'user' : 1102286, 'server' : '2', 'id' : 'clustrmaps-widget', 'version' : 1, 'date' : '2013-07-04', 'lang' : 'es', 'corners' : 'square' };(function (){ var s = document.createElement('script'); s.type = 'text/javascript'; s.async = true; s.src = 'http://www2.clustrmaps.com/counter/map.js'; var x = document.getElementsByTagName('script')[0]; x.parentNode.insertBefore(s, x);})();</script><noscript><a href="http://www2.clustrmaps.com/user/dea10d1ce"><img src="http://www2.clustrmaps.com/stats/maps-no_clusters/gtc.sdc.cab.inta-csic.es-gtc--thumb.jpg" alt="Locations of visitors to this page" /></a></noscript></td>
 -->
<td rowspan=10></td>
</tr>
<tr>
<td><font size=-2>&nbsp;</font></td>
</tr>

<tr>
<td NOWRAP rowspan="2" bgcolor="#A9E2F3"><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<font face="arial,helvetica,san-serif">
	<font size=+1><b><a href="/gtc/jsp/searchform.jsp">Archive search and data retrieval</a></b></font></font></td>
<td  ALIGN=CENTER bgcolor="#81F781"><img SRC="/gtc/images/bluearrow.gif" >
&nbsp;<font size=+1><b>High Level Data Collections</b></font><br>
</td>	
</tr>
<tr>
<td NOWRAP bgcolor="#BCF5A9">
	&nbsp;<font face="arial,helvetica,san-serif">
	<img SRC="/gtc/images/bluearrow.gif" >&nbsp;<b><a href="http://svo2.cab.inta-csic.es/vocats/v2/gtc-osiris-detection/">OSIRIS BBI detection catalogue. DR1 (2009-2014)</a></b></font>
	<br>&nbsp;<font face="arial,helvetica,san-serif">
	<img SRC="/gtc/images/bluearrow.gif" >&nbsp;<b><a href="http://svo2.cab.inta-csic.es/vocats/v2/gtc-osiris-source/">OSIRIS BBI source catalogue. DR1 (2009-2014)</a></b></font>
</td>
</tr>
<%if(isLog){ %>
<tr><td>&nbsp;</td></tr>
<tr>
<td NOWRAP><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<font face="arial,helvetica,san-serif">
	<font size=+0><b><a href="/gtc/s/insertReducedInput.action">Upload reduced data</a></b></font></font></td>
</tr>
<tr>
<td NOWRAP><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<font face="arial,helvetica,san-serif">
	<font size=+0><b><a href="/gtc/s/changePasswordInput.action">Change your password</a></b></font></font></td>
</tr>

<%}
if(isAdm){ %>
<tr><td>&nbsp;</td></tr>

<tr>
<td NOWRAP><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<font face="arial,helvetica,san-serif">
	<font size=+0><b><a href="/gtc/s/newUserInput.action">Create new user</a></b></font></font></td>
</tr>
<%} %>
<tr><td>&nbsp;</td></tr>

<tr>
<td NOWRAP><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<font face="arial,helvetica,san-serif">
	<font size=+0><b><a href="/gtc/jsp/news.jsp">News</a></b></font></font></td>
</tr>

<tr>
<td NOWRAP><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<font face="arial,helvetica,san-serif">
	<font size=+0><b><a href="/gtc/help/overview.jsp">System Overview</a></b></font></font></td>
</tr>

<tr>
<td NOWRAP><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<font face="arial,helvetica,san-serif">
	<b><font size=+0><a href="/gtc/help/faq.jsp">Frequently Asked Questions</b></a></b></font></font></td>
</tr>
<tr>
<td NOWRAP><img SRC="/gtc/images/bluearrow.gif" >&nbsp;<font face="arial,helvetica,san-serif">
	<b><font size=+0><a href="mailto:gtc-support@cab.inta-csic.es">Help Desk</font></b> (gtc-support@cab.inta-csic.es)</a></b></font></font></td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr>

</table>
<!-- **********  Fin Tabla enlaces **************************  -->


</td>
</tr>
<tr> 
	<td colspan="2" align="center"> 
		<br><br><i><font face="arial,helvetica,geneva"><font size=+0>
			The GTC Public Archive is the result of a collaboration agreement between INTA and GRANTECAN. 
			It has been developed in the framework of the Spanish Virtual Observatory project supported 
			by the Spanish MICINN through grant AYA 2011-24052. The system is maintained by the Data 
			Archive Unit of the <a href="http://cab.inta.es/">CAB (CSIC -INTA)</a>.
		</font></font></i>
		<br></br>
		<i><font face="arial,helvetica,geneva"><font size=+0>
			If you use this service in your research, please include the following acknowledgement in 
			any resulting publications:
		</font></font></i>
		
		<br><i><b><font face="arial,helvetica,geneva"><font size=+0>
			"Based on data from the GTC PublicArchive at CAB (INTA-CSIC)".
		</font></font></b></i></center>	
	</td>
</tr>
</table>
<br><br>

<%= pie %>
</body>
</html>

