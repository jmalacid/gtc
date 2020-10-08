package svo.sepub.html;

public class webHTML {

	private final String VERSION = "Versión 0.2 - Junio 2015"; 
	private String contextPath = "/sepub";
	
	public webHTML(String contextPath) {
		this.contextPath = contextPath;
	}
	public String encabezado(){
		
		String encabezado = "<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\n<meta name=\"Description\" content=\"Aplicación que busca publicaciones\">" +
				"\n<meta name=\"Author\" content=\"José Manuel Alacid\">\n<title>SEarch PUBlications</title>\n<LINK REL=\"STYLESHEET\" type=\"text/css\" href=\""+contextPath+"/css/estiloSePub.css\">\n</head>";
		
		return encabezado;
	}
	
	public String pie(){
		String pie = "<hr size=2 align=\"left\" style=\"width:800px\">\n<table style=\"width:800px\">\n<tr>\n<td style=\"white-space: nowrap\"><i>" +
				VERSION+"&nbsp;&nbsp;&nbsp; &copy; CAB</i>\n</td>\n<td align=\"right\"><a href=\""+contextPath+"/index.jsp\">GTC Home</a>&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;<a href=\""+contextPath+"/private/indexSepub.jsp\">SePub Home</a>&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;<a href=\"mailto:" +
				"jmalacid@cab.inta-csic.es\">Help Desk</a>\n</td>\n</tr>\n</table>";
		
		return pie;
	}
	
	
}
