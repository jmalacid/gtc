<?xml version="1.0" encoding="UTF-8"?>
<%response.setContentType ("application/x-java-jnlp-file");
response.setHeader("Content-Disposition", "attachment;filename=aladinGTC.jnlp");%>
 
<%//Valores de la sesión
	String serverName= request.getServerName();
	int port = request.getServerPort();
	String path = request.getContextPath();
	String url = "?"+request.getQueryString();
	%>

<jnlp href="http://<%= serverName%>:<%=port %><%= path%>/jsp/aladinjnlp.jsp<%=url %>" codebase="http://<%= serverName%>:<%=port %><%= path%>/aladin/">

  <information>
    <title>Aladin V10</title>
    <vendor>CDS - Observatoire Astronomique de Strasbourg</vendor>
    <homepage href="http://aladin.u-strasbg.fr"/>
    <icon href="Aladin-logo-OS.gif"/>
    <icon kind="splash" href="AladinBanner.jpg"/>
    <description>
       Aladin is an interactive software sky atlas allowing
       the user to visualize digitized images of any part of the sky,
       to superimpose entries from astronomical catalogs or personal
       user data files, and to interactively access related data
       and information from the SIMBAD, NED, VizieR, or other archives
       for all known objects in the field
    </description>
    <description kind="one-line">Aladin V10 (CDS)</description>
    <description kind="short">Aladin V10 (CDS)</description>
    <description kind="tooltip">Aladin V10</description>
    <offline-allowed/>
    <shortcut online="false">
       <desktop/>
       <menu>Aladin</menu>
    </shortcut>
    <association mime-type="application-x/fits" extensions="fits"/>
    <association mime-type="application-x/ajs" extensions="ajs"/>
    <association mime-type="application-x/aj" extensions="aj"/>
  </information>

	<application-desc main-class="cds.aladin.Aladin">
 <%
String prodId = request.getParameter("prod_id");
String predId = request.getParameter("pred_id");%>
	<%if(prodId!=null && prodId.length()>0){
		System.out.println("http://"+serverName+":"+port+path+"/servlet/ScriptAladin"+url+"&.ajs");%> 
		<argument>-scriptfile=http://<%= serverName%>:<%=port %><%= path%>/servlet/ScriptAladin<%=url %>&.ajs</argument>
	<%}else if(predId!=null && predId.length()>0){ %>
		<argument>-scriptfile=http://<%= serverName%>:<%=port %><%= path%>/servlet/ScriptAladin<%=url %>&.ajs</argument>
	<%}else{ %>
		<argument>-scriptfile=http://<%= serverName%>:<%=port %><%= path%>/jsp/scriptAladinMOC.jsp?.ajs</argument>
	<%} %>
  	</application-desc>
   

  <resources>
     <j2se version="1.4+"
        initial-heap-size="32m"
	max-heap-size="1024m"
	java-vm-args="-ms1024m"/>
     <jar main="true" href="Aladin.jar"/>
  </resources>
  <security><all-permissions/></security>
</jnlp>