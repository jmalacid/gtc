<%@ page import="svo.gtc.web.Html,
svo.gtc.web.FormMain,
svo.gtc.web.ContenedorSesion,
svo.gtc.web.FormParameterException,
svo.gtc.db.web.WebMainSearch,
svo.gtc.db.web.WebMainResult,
svo.gtc.db.logquery.LogQueryDb,
svo.gtc.db.logquery.LogQueryAccess,
svo.gtc.web.AladinApplet,java.sql.*,
svo.gtc.priv.objetos.ReducInfo,
java.net.URLEncoder,
java.util.Date,
utiles.StringUtils,
svo.gtc.db.priv.DBPrivate,
svo.gtc.db.permisos.ControlAcceso,

javax.sql.*,
java.io.*,
javax.naming.Context,
javax.naming.InitialContext,
javax.naming.NamingException" %>
                 
<%@ page isThreadSafe="true" %>
<%@ page info="GTC Search" %>

<%-- P  R  O  C  E  S  O  S --%>
<%
//Abrimos la conexión a la base de datos
Context initContext;
Connection cn=null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();


//*************************************************************
//*  SESION                                                   *
//*************************************************************

ContenedorSesion contenedorSesion = null;
if(session!=null){ 
	contenedorSesion = (ContenedorSesion)session.getAttribute("contenedorSesion");
}

if(contenedorSesion==null) contenedorSesion=new ContenedorSesion();
	

//*************************************************************
//*  COMPROBACIONES                                           *
//*************************************************************


String MSG="";
String WAR="";

FormMain formulario = null;

try{
	formulario=new FormMain(request);
}catch(Exception e){
	MSG="Unable to process request.";
	e.printStackTrace();
}


if(formulario!=null && formulario.getOrigen()!=null && formulario.getOrigen().equals("searchform.jsp")){
	contenedorSesion.setFormulario(formulario);
	session.setAttribute("contenedorSesion",contenedorSesion);
	if(formulario.getErrors().length>0){
		String[] errores = formulario.getErrors();
		for(int i=0; i<errores.length; i++){
			MSG+=errores[i]+"<br>";
		}
	}
	if(formulario.getWarning().length>0){
		String[] warning = formulario.getWarning();
		for(int i=0; i<warning.length; i++){
			WAR+=warning[i]+"<br>";
		}
	}
}else{
	if(contenedorSesion.getFormulario()!=null){
		formulario = contenedorSesion.getFormulario();
		try{
	formulario.procesaRequest(request);
	contenedorSesion.setFormulario(formulario);
	session.setAttribute("contenedorSesion",contenedorSesion);
		}catch(Exception e){
	MSG=e.getMessage();	
	e.printStackTrace();
		}
	}
}

if(MSG.length()==0 && (formulario==null || !formulario.isValid())){
	MSG = "No search conditions";
}

if(formulario==null){
	formulario = new FormMain();
}



//*************************************************************
//*  BUSQUEDA                                                 *
//*************************************************************

WebMainSearch resultados=null;
int cuentaResultados = 0;
int pts = 1;
int rpp = 10;

if(formulario!=null && formulario.getPts()!=null) pts = formulario.getPts();
if(formulario!=null && formulario.getRpp()!=null) rpp = formulario.getRpp().intValue();


//Ponemos a null el formulario para que busque todo, luego habría que cambiarlo
formulario.setPts(null);
formulario.setRpp(null);

if(MSG.length()==0){
	resultados = new WebMainSearch(cn, formulario, contenedorSesion.getUser());
	cuentaResultados = resultados.countResults();
	
}


%>

<%--------     I N I C I O    P Á G I N A    H T M L    -------------------%>
<html>

<body>

<%-----  SECCION QUE SOLO SE PRESENTA SI HAY RESULTADOS  ------%>
<%
if (cuentaResultados >0){

response.setContentType("text/csv");
		response.setHeader("Content-Disposition",
				"attachment;filename=gtc_results.csv");

		ServletOutputStream outs = response.getOutputStream();
		OutputStreamWriter osWriter = new OutputStreamWriter(outs);

		
		String linea1 = "Prod ID, Program ID, O.Block, Object, RA (deg), DEC (deg), RAJ2000, DECJ2000, Instr., Obs. Mode, Init.Time, End Time, Exptime, Airmass, Pub, User Reduced Data, GTC Reduced Data, Raw Data, Cal. Files, Acq. Images, QC Files";

		osWriter.write(linea1 + "\n");

	WebMainResult resultado=null;
while( (resultado=resultados.getNext())!=null ){
	
	//Comprobamos si hay alguna publicación relacionada
	DBPrivate conex1 = new DBPrivate(cn);
	String[] pub = conex1.selectPub(resultado.getProgId(), resultado.getOblId(), resultado.getProdId());
	String progName = conex1.progName(resultado.getProgId());
	String prod_id = resultado.getProgId()+".."+resultado.getOblId()+".."+resultado.getProdId().intValue();
	
	String line = resultado.getProdId().intValue()+", "+resultado.getProgId()+", "+resultado.getOblId()+", "+resultado.getObject().replaceAll(",", ";")+", "+resultado.getFormatedProdRa()+", "+resultado.getFormatedProdDe()+
	", "+resultado.getSexaProdRa()+", "+resultado.getSexaProdDe()+", "+resultado.getInsName()+", "+resultado.getModId()+", "+resultado.getFormatedProdInitime()+", "+resultado.getFormatedProdEndtime()+
	", "+resultado.getExptime()+", "+resultado.getFormatedProdAirmass()+", "+pub.length+", ";
	
	//Aquí comprobamos si es público o privado
	ControlAcceso controlAcceso = new ControlAcceso(cn, contenedorSesion.getUser());
		if(controlAcceso.hasPerm(resultado.getProgId(),resultado.getOblId(),resultado.getProdId(),contenedorSesion.getUser())){

			if(resultado.getInsName().equalsIgnoreCase("HIPERCAM")){

				int red_users= 0;
				int red_hip= 0;
				String bibcode = null;
				
				if(resultado.getCountRed()>0){ 
					
					//Obtenemos el reducido USER y el Hipercam ya lo tenemos
					//red_users = conex1.numRed(resultado.getProgId(), resultado.getOblId(), resultado.getProdId(), "USER");
					//red_hip = conex1.numRed(resultado.getProgId(), resultado.getOblId(), resultado.getProdId(), "HIPER");
					
					//DBPrivate conex1 = new DBPrivate(cn);
					ReducInfo red = conex1.RedInfo(resultado.getProgId(), resultado.getOblId(), resultado.getProdId());
					
					//Valores
					red_users = red.getCuenta_user();
					red_hip = red.getCuenta_her();
					bibcode = red.getBibcode();
					
				} 
				
/* 				
				if(resultado.getCountRed()>0){ 
					
					//Obtenemos el reducido USER y el Hipercam ya lo tenemos
					red_users = conex1.numRed(resultado.getProgId(), resultado.getOblId(), resultado.getProdId(), "USER");
					red_hip = conex1.numRed(resultado.getProgId(), resultado.getOblId(), resultado.getProdId(), "HIPER");
				} */
					if(red_users>0){
						line +="https://gtc.sdc.cab.inta-csic.es/gtc/jsp/fetchDataBlock.jsp?datablock="+resultado.getProgId()+"__"+resultado.getOblId()+"__"+resultado.getProdId()+"__ALLRED&bib="+bibcode.trim().replaceAll("&", "%26")+", ";
						
					}else{
						line +=", ";
					}
					if(red_hip>0){
						//Obtenemos el valor del predId del fichero reducido
						String predId = conex1.getPredS(resultado.getProgId(), resultado.getOblId(), resultado.getProdId());
						
						if(predId!=null && predId.length()>0){
							line +="https://gtc.sdc.cab.inta-csic.es/gtc/jsp/fetchDataBlock.jsp?datablock="+resultado.getProgId()+"__"+resultado.getOblId()+"__"+resultado.getProdId()+"__ALLHER, ";
						
						}else{
							line +=", ";
						}//No añadimos raw y calibracion 
						line +=", , ";
					}else{
					//Añadimos raw y calibracion
					resultado.setCountCal(resultados.getCountCal(resultado.getProgId(), resultado.getOblId()));
					resultado.setCountAC(resultados.getCountAC(resultado.getProgId(), resultado.getOblId()));
					
					line +=",https://gtc.sdc.cab.inta-csic.es/gtc/servlet/FetchProd?prod_id="+prod_id+", ";
						
					if((resultado.getCountCal()+resultado.getCountEE())>0){
						String datablock = null; 
						if(resultado.getModId().equals("SPE")){
							datablock = resultado.getProgId()+"__"+resultado.getOblId()+"__"+resultado.getProdId()+"__ALLCSS";
						}else{
							datablock = resultado.getProgId()+"__"+resultado.getOblId()+"__"+resultado.getInsId()+"__ALLCAL";
						}
						line +="https://gtc.sdc.cab.inta-csic.es/gtc/jsp/fetchDataBlock.jsp?datablock="+datablock+", ";
					}else{ 
						line +=", ";
					}
					
				}  
				
			resultado.setCountWarn(resultados.getCountWarn(resultado.getProgId(), resultado.getOblId()));
			resultado.setCountQC(resultados.getCountQC(resultado.getProgId(), resultado.getOblId()));
			
				line +=",https://gtc.sdc.cab.inta-csic.es/gtc/jsp/fetchDataBlock.jsp?datablock="+resultado.getProgId()+"__"+resultado.getOblId()+"__"+resultado.getInsId()+"__ALLLOG";
			
			}else{
		
			if(resultado.getCountRed()>0){ 
				
				//Comprobamos si son reducidos de usuario o de otro tipo
				/* int red_users = conex1.numRed(resultado.getProgId(), resultado.getOblId(), resultado.getProdId(), "USER");
				int red_herv = conex1.numRed(resultado.getProgId(), resultado.getOblId(), resultado.getProdId(), "HERVE");
				int red_meg = conex1.numRed(resultado.getProgId(), resultado.getOblId(), resultado.getProdId(), "MEG");
 *///No queremos mostrar los reducidos Herve, ponemos que hay 0
				//int red_herv =0;

				ReducInfo red = conex1.RedInfo(resultado.getProgId(), resultado.getOblId(), resultado.getProdId());
					
				//Valores
				int red_users = red.getCuenta_user();
				int	red_her = red.getCuenta_her();
				int	red_meg = red.getCuenta_meg();
				String	bibcode = red.getBibcode();
				
				if(red_users>0){
					
					line +="https://gtc.sdc.cab.inta-csic.es/gtc/jsp/fetchDataBlock.jsp?datablock="+resultado.getProgId()+"__"+resultado.getOblId()+"__"+resultado.getProdId()+"__ALLRED&bib="+bibcode.trim().replaceAll("&", "%26")+", ";
				}else{
					line +=", ";
				}
				
 				if((red_her)==1){
 					
 				
					String predId = conex1.getPredS(resultado.getProgId(), resultado.getOblId(), resultado.getProdId());
					line +="https://gtc.sdc.cab.inta-csic.es/gtc/jsp/fetchDataBlock.jsp?datablock="+resultado.getProgId()+"__"+resultado.getOblId()+"__"+resultado.getProdId()+"__ALLHER, ";

				
				}else if((red_her)>1){
					line +="https://gtc.sdc.cab.inta-csic.es/gtc/jsp/fetchDataBlock.jsp?datablock="+resultado.getProgId()+"__"+resultado.getOblId()+"__"+resultado.getProdId()+"__ALLHER, ";
				}else{
				 	line +=", ";
				} 
 				if((red_meg)>0){
 					line +="https://gtc.sdc.cab.inta-csic.es/gtc/jsp/fetchDataBlock.jsp?datablock="+resultado.getProgId()+"__"+resultado.getOblId()+"__"+resultado.getProdId()+"__ALLMEG, ";
 				}else{
 					line +=", ";
 				}
			}else{
				line +=", , ";	
			} 
			
			
			
		resultado.setCountCal(resultados.getCountCal(resultado.getProgId(), resultado.getOblId()));
		resultado.setCountAC(resultados.getCountAC(resultado.getProgId(), resultado.getOblId()));
		resultado.setCountWarn(resultados.getCountWarn(resultado.getProgId(), resultado.getOblId()));
		resultado.setCountQC(resultados.getCountQC(resultado.getProgId(), resultado.getOblId()));
	
		if(resultado.getModId().equals("SPE")){
			resultado.setCountEE(resultados.getCountEE(resultado.getProgId(), String.valueOf(resultado.getProdInitime()), String.valueOf(resultado.getProdEndtime())));
		}
		
		
					
			if(resultado.getInsName().equals("CANARICAM")){
				line +="https://gtc.sdc.cab.inta-csic.es/gtc/jsp/fetchDataBlock.jsp?prod_id="+prod_id+", ";
			
			}else{
			line +="https://gtc.sdc.cab.inta-csic.es/gtc/servlet/FetchProd?prod_id="+prod_id+", ";
		}
			if((resultado.getCountCal()+resultado.getCountEE())>0){ 
				String datablock = null; 
					if(resultado.getModId().equals("SPE")){
						datablock = resultado.getProgId()+"__"+resultado.getOblId()+"__"+resultado.getProdId()+"__ALLCSS";
					}else{
						datablock = resultado.getProgId()+"__"+resultado.getOblId()+"__"+resultado.getInsId()+"__ALLCAL";
					}
					
					line +="https://gtc.sdc.cab.inta-csic.es/gtc/jsp/fetchDataBlock.jsp?datablock="+datablock+", ";
			}else{ 
			 line +=", ";
			}
		
	
		if((resultado.getModId().equalsIgnoreCase("LSS") || resultado.getModId().equalsIgnoreCase("SPE")|| resultado.getModId().equalsIgnoreCase("POL")) && resultado.getCountAC()>0){ 
			line +="https://gtc.sdc.cab.inta-csic.es/gtc/jsp/fetchDataBlock.jsp?datablock="+resultado.getProgId()+"__"+resultado.getOblId()+"__"+resultado.getInsId()+"__ALLAC, ";
		}else{
			 line +=", ";
		}
	
		if((resultado.getCountWarn()+resultado.getCountQC())>0){
			line +="https://gtc.sdc.cab.inta-csic.es/gtc/jsp/fetchDataBlock.jsp?datablock="+resultado.getProgId()+"__"+resultado.getOblId()+"__"+resultado.getInsId()+"__ALLLOG";
		} else {
			
		}
						
		}//Fin de else instrumento
		
		}else{
			line +="private, private, private, private, private, private";
		}
		osWriter.write(line + "\n");
		
} // fin del While que recorre los resultados.	
			
osWriter.flush();
osWriter.close();

//Volvemos a cambiar el formulario, para dejar valores por defecto
formulario.setPts(pts);
formulario.setRpp(rpp);


 } %> 

</body>
</html>
<%}catch(SQLException ex){
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