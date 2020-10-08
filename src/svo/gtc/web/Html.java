/*
 * @(#)Html.java    2008-06-05
 *
 *
 * Ra�l Guti�rrez S�nchez. (raul@cab.inta-csic.es)	
 * LAEFF: 	Laboratorio de Astrof�sica Espacial y F�sica Fundamental.
 *        	INTA
 *			Villafranca del Castillo Satellite Tracking Station
 *			Villafranca del Castillo, E-28691 Villanueva de la Ca�ada
 *			Madrid - Espa�a
 *			
 * Phone:  34 91-8131260
 * Fax..:  34 91-8131160   
 * -----------------------------------------------------------------------
 * 
 *	ESTE  PROGRAMA  ES  DE  USO EXCLUSIVO.  ES  PROPIEDAD  DE  SU AUTOR  Ra�l 
 *  Guti�rrez S�nchez  Y SE PRESENTA "COMO ESTA" SIN NINGUN TIPO DE GARANTIAS
 *  DENTRO Y FUERA  DE LA FINALIDAD PARA  LA QUE EL  AUTOR LO  HIZO. EL AUTOR 
 *  PERMITE  SU USO SIEMPRE QUE  SE LE REFERENCIE. SE PERMITE LA MODIFICACION
 *  DE SU  FUENTE SIEMPRE QUE SE  HAGA REFERENCIA A SU AUTORIA Y JUNTO A ELLA
 *  SE  ADJUNTE LA IDENTIFICACION DEL  AUTOR DE LAS NUEVAS MODIFICACIONES. EL
 *  AUTOR  NUNCA SERA RESPONSABLE DEL USO DE ESTE  PROGRAMA NI DE LOS EFECTOS
 *  QUE ESTE PROGRAMA PUDIEDA CAUSAR EN DETERIOROS, ALTERACIONES Y/O PERDIDAS 
 *  DE INFORMACION.
 *	
 * @author Ra�l Guti�rrez S�nchez(raul@cab.inta-csic.es)
 * @version 0.0, 2007-01-23
 */

package svo.gtc.web;

import org.apache.commons.lang.StringEscapeUtils;

import svo.gtc.db.usuario.UsuarioDb;

/**
 * <H2>Html</H2>
 *
 * <P>Define los elementos est�ticos de la p�gina (cabecera, 
 *    pie de p�gina,..) 
 *    <UL>Consultas.
 *        <LI>cabecera(): Obtencion de la cabecera HTML de la p�gina.  
 *        <LI>encabezamiento():  Obtenci�n del encabezamiento de la p�gina (apariencia general). 
 *        <LI>pie():  Obtenci�n del pie de la p�gina (apariencia general). 
 *    </UL>
 *
 * @author José Manuel Alacid (jmalacid@cab.inta-csic.es), Raul Gutierrez Sanchez (raul@cab.inta-csic.es)
 * @version 0.0, 2008-04-24
 */
public class Html {
    
  //---Variables y areas de trabajo. ---------------------------------
	private final String VERSION = "Version 2.91 - Sept 2020";
	private String contextPath = "/gtc";
  
	int contador = 0;
  
  
	public Html(String contextPath) {
		this.contextPath = contextPath;
	}
  

	/**
	* <P>Obtenci�n de la cabecera Html de la p�gina (head). 
	*
	* <UL>LLAMADA
	*     <LI>Html elementosHtml = new utiles.Html();
	*     <LI>String cabeceraPagina = elementosHtml.cabeceraPagina("titulo","descripcion");
	* </UL>
	*  
	* @author J. Manuel Alacid Polo
	* @version 0.0, 2009-05-28
	*/
   
	public String cabeceraPagina(String title, String descripcion) {
    
    	String salida = new String("");

		salida += "<head>  \n";
		salida += "   <meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">  \n";
		salida += "   <meta name=\"Description\" content=\""+descripcion+"\">  \n";
		salida += "   <meta name=\"Author\" content=\"José Manuel Alacid\">  \n";
		salida += "   <title>GTC - "+title+"</title>  \n";
		salida += "   <LINK REL=\"STYLESHEET\" type=\"text/css\" href=\""+contextPath+"/css/estilos.css\">\n";
		salida += "	  <SCRIPT LANGUAGE=\"JavaScript\" type=\"text/javascript\" src=\""+contextPath+"/js/funciones.js\"></SCRIPT> \n";
		
		salida += "<!-- Global site tag (gtag.js) - Google Analytics -->\n<script async src=\"https://www.googletagmanager.com/gtag/js?id=UA-42195893-1\"></script>\n";
		salida += "<script>\nwindow.dataLayer = window.dataLayer || [];\nfunction gtag(){dataLayer.push(arguments);}\ngtag('js', new Date());\ngtag('config', 'UA-42195893-1');\n</script>\n";

		salida += "</head> \n";
		salida += "<body> \n";
		

    	return salida;
  	}

	/**
	 * Obtiene el encabezamiento com�n de las p�ginas.
	 * 
	 * @param user Usuario activo o null si no se ha hecho login.
	 * @param pagOrigen P�gina actual.
	 * @return C�digo HTML correspondiente al encabezamiento.
     * @author Raul Gutierrez Sanchez (raul@cab.inta-csic.es)
   	 * @version 0.0, 2008-04-24
	 */
	public String encabezamiento(UsuarioDb user, String pagOrigen){
    	String salida = new String("");

		if(user!=null ){
			salida += "<form name=\"form_login\" method=\"POST\" action=\""+this.contextPath+"/jsp/logout.jsp\"> \n ";
		}else{
			salida += "<form name=\"form_login\" method=\"POST\" action=\""+this.contextPath+"/s/loginInput.action\"> \n ";
		}

   		salida += "<table style=\"userinfo\" border=\"0\"  cellspacing=\"0\" cellpadding=\"0\" WIDTH=\"800px\">";
   		salida += "<tr><td colspan=\"2\"><img src=\""+contextPath+"/images/cabGTC.png\" WIDTH=\"800px\"></td></tr>";
		
		/////////////////////////////////////////////////////////
	    /// Formulario LOGIN
	    /////////////////////////////////////////////////////////
   		
   		salida += "<tr class=\"headinfo\">";
		if(user!=null ){
			salida += "<td align=\"left\">&nbsp;</td><td class=\"headinfo\" align=\"right\"> ";
			salida +=  "<input type=\"submit\" name=\"Logout\" value=\"Logout\"> "+user.getUsrName()+" "+user.getUsrSurname()+"\n";
		}else{
			salida += "<td align=\"left\">&nbsp;</td><td class=\"headinfo\" align=\"right\"> ";
			salida +=  "<input type=\"submit\" name=\"Login\" value=\"Login\"> Not logged\n";
			//salida +=  "Not logged in\n";
		}
		salida +=  "<input type=\"hidden\" name=\"origen\" value=\""+pagOrigen+"\">\n";
		salida += "</td></tr>";
   		
	    /////////////////////////////////////////////////////////
	    /// FIN Formulario LOGIN
	    /////////////////////////////////////////////////////////

		salida += "</table>";
		salida += "</form>";

    	return salida;
	}


  	/**
   	* <P>Obtenci�n del pie de la p�gina (apariencia general). 
   	*
   	* <UL>LLAMADA
   	*     <LI>Html elementosHtml = new utiles.Html();
   	*     <LI>String pie = elementosHtml.pie();
   	* </UL>
   	*  
   	* @author Raul Gutierrez Sanchez (raul@cab.inta-csic.es)
   	* @version 0.0, 2008-04-24
   	*/

	public String pie() {
    
    	String salida = new String("");
    	
		salida += "<hr align=\"left\" width=\"800px\"> \n";
		salida += "<table width=\"800px\"><tr><td nowrap><i><font face=\"arial,helvetica,geneva\">"+VERSION+"&nbsp;&nbsp;&nbsp; &copy; CAB</font></i> \n";
		salida += "</td><td align=\"right\"> \n";
		salida += "<a href=\""+contextPath+"/private/private.jsp\">Admin</a>&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;<a href=\""+contextPath+"/index.jsp\">Home</a>&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;<a href=\"http://www.gtc.iac.es/\">GTC</a>&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;<a href=\"mailto:gtc-support@cab.inta-csic.es\">Help Desk</a> \n";
		salida += "</td></tr></table> \n";
		salida += "<SCRIPT LANGUAGE=\"JavaScript\" type=\"text/javascript\" src=\""+contextPath+"/js/wz_tooltip.js\"></SCRIPT> \n";

    	return salida;
  	}

  	/**
   	* <P>Obtenci�n del pie de la p�gina (peque�o). 
   	*
   	* <UL>LLAMADA
   	*     <LI>Html elementosHtml = new utiles.Html();
   	*     <LI>String pie = elementosHtml.piePeq();
   	* </UL>
   	*  
   	* @author Raul Gutierrez Sanchez (raul@cab.inta-csic.es)
   	* @version 0.0, 2008-04-24
   	*/

	public String piePeq() {
    
    	String salida = new String("");
    	
		salida += "<hr align=\"left\" width=\"90%\"> \n";
		salida += "<table width=\"90%\"><tr><td nowrap><i><font face=\"arial,helvetica,geneva\" size=\"-2\">"+VERSION+"&nbsp;&nbsp;&nbsp; &copy; CAB</font></i> \n";
		salida += "</td><td align=\"right\"> \n";
		salida += "<font size=\"-2\"><a href=\""+contextPath+"/index.jsp\">Home</a>&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;<a href=\"http://www.gtc.iac.es/\">GTC</a></font> \n";
		salida += "</td></tr></table> \n";

    	return salida;
  	}
	
	
	/**
	 * Devuelve el comando necesario para añadir un elemento JavaScript que presente 
	 * el texto indicado en una ventana de ayuda.
	 * 
	 * @param 
	 * 		texto <p>Texto de la ayuda.</p>
	 * 
	 * @return
	 * 		Elemento <p>"onMouseOver" correspondiente.</p> 
	 */
	public static String ayudaOnMouseOver(String texto){
			return " onmouseover=\"this.T_WIDTH=250;this.T_OFFSETX=-110;this.T_OFFSETY=-30;this.T_STICKY=false;this.T_TEMP=5000;this.T_STATIC=true;return escape('"+StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(texto))+"');\" ";	
	}
	

} 	
  
