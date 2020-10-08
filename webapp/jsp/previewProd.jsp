<?xml version="1.0" encoding="UTF-8"?>
<%
response.setContentType ("application/x-java-jnlp-file");
response.setHeader("Content-Disposition", "attachment;filename=accesoAladin.jnlp");

String[] par_prodId = request.getParameterValues("prod_id");
String[] par_predId = request.getParameterValues("pred_id");

//Valores de la sesión
	String serverName= request.getServerName();
	int port = request.getServerPort();
	String path = request.getContextPath();


//String scriptAladin = "reticle off; ";
String scriptAladin = "";

if(par_prodId!=null){
	for(int i=0; i<par_prodId.length; i++){
		String prodId = par_prodId[i].substring(par_prodId[i].lastIndexOf("..")+2);
		/* scriptAladin+="modeview 2; ";
		scriptAladin+=prodId+"=load http://"+serverName+":"+port+path+"/servlet/FetchProd?prod_id="+par_prodId[i]+"&fetch_type=PREVIEW; ";
		scriptAladin+="sync; pause;";
		scriptAladin+="sync; cview "+prodId+"[2];";
		scriptAladin+="sync; cm Log;";
		scriptAladin+="sync; zoom 1/4x;";
		scriptAladin+="sync; select "+prodId+"[1];";
		scriptAladin+="sync; cm Log;";
		scriptAladin+="sync; zoom 1/4x;"; */
		scriptAladin+="load http://"+serverName+":"+port+path+"/servlet/FetchProd?prod_id="+par_prodId[i]+"&fetch_type=PREVIEW\n";
		
	}
}else if(par_predId!=null){
	for(int i=0; i<par_predId.length; i++){
		String predId = par_predId[i].substring(par_predId[i].lastIndexOf("..")+2);
		scriptAladin+=predId+"=get Local(http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/servlet/FetchProd?pred_id="+par_predId[i]+"&fetch_type=PREVIEW); ";
		scriptAladin+="sync; pause;";
		scriptAladin+="sync; cm Log;";
	}
}
	


%>

<!-- <jnlp href="aladin.jnlp" codebase="http://aladin.u-strasbg.fr/java/"> -->
<jnlp href="http://<%= serverName%>:<%=port %><%= path%>/jsp/previewProd.jsp" codebase="http://<%= serverName%>:<%=port %><%= path%>/aladin/">
  <information>
    <title>Aladin V7</title>
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
    <description kind="one-line">Aladin V7 (CDS)</description>
    <description kind="short">Aladin V7 (CDS)</description>
    <description kind="tooltip">Aladin V7</description>
    <offline-allowed/>
    <shortcut online="false">
       <desktop/>
       <menu>Aladin</menu>
    </shortcut>
    <association mime-type="application-x/fits" extensions="fits"/>
    <association mime-type="application-x/ajs" extensions="ajs"/>
    <association mime-type="application-x/aj" extensions="aj"/>
  </information>
<!--
   <applet-desc
         main-class="cds.aladin.Aladin"
         name="Aladin"
         width="731"
         height="712">
      <param name="script" value="get m101"/>
  </applet-desc>
-->

   <application-desc main-class="cds.aladin.Aladin">
<!--      <argument>-from=CDS-WebStart</argument> -->
     <argument>-script=<%=scriptAladin %></argument>
  </application-desc>

  <resources>
     <j2se version="1.5+"
        initial-heap-size="32m"
	max-heap-size="1024m"
	java-vm-args="-ms1024m"/>
     <jar main="true" href="Aladin.jar"/>
  </resources>
  <security><all-permissions/></security>
</jnlp>
