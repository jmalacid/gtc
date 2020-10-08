<%@ page import="svo.gtc.web.Html,
				 svo.gtc.web.ContenedorSesion,
				 svo.gtc.db.web.*,
				 svo.gtc.db.prodat.ProdDatosAccess,
				 svo.gtc.db.prodat.ProdDatosDb,
				 svo.gtc.db.prodred.ProdRedDb,
				 
				 svo.gtc.db.proderr.ProdErrorAccess,
				 svo.gtc.db.proderr.ProdErrorDb,
				 svo.gtc.db.logprod.LogProdAccess,
				 svo.gtc.db.logprod.LogProdDb,
				 svo.gtc.db.logprod.LogProdSingleDb,
				 
				 svo.gtc.db.logfile.LogFileDb,
				 svo.gtc.db.permisos.ControlAcceso,

				 svo.gtc.proddat.ProdDatos,
				
				 java.sql.*,
				 
				 
				 java.io.File,
				 java.io.InputStream,
				 java.io.OutputStream,
				 java.io.FileNotFoundException,
				 java.io.BufferedInputStream,
				 java.io.FileInputStream,
				 java.io.FileWriter,
				 java.io.BufferedWriter,
				 
				 org.apache.commons.compress.archivers.*,
				 org.apache.commons.compress.archivers.tar.*,
				 org.apache.commons.compress.compressors.gzip.*,
				 org.apache.commons.io.IOUtils,
				 
				 java.util.zip.ZipEntry,
				 java.util.zip.ZipOutputStream,
				 java.util.Vector,
				 
				 java.util.Date,
				 java.util.Calendar,
				 
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



String MSG = "";

//Abrimos la conexión a la base de datos
Context initContext;
Connection cn=null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();



// *************************************************************
// *  TECLAS DEL FORMULARIO                                    *
// *************************************************************

	//---- Elementos estáticos de la página
	Html elementosHtml = new Html(request.getContextPath());
	String cabeceraPagina = elementosHtml.cabeceraPagina("Product Details","Data Product Details.");
	String encabezamiento = elementosHtml.encabezamiento(contenedorSesion.getUser(), request.getServletPath()+"?"+request.getQueryString());
	String pie            = elementosHtml.pie();

	String[] par_datablock = request.getParameterValues("datablock");
	String[] par_prodId = request.getParameterValues("prod_id");
	//Obtenemos el valor del tipo de descarga
	String down_type = request.getParameter("down_type");
	
	//Obtenemos el bibcode si se descargan ficheros reducidos
	String bibcode = null;
	try{
		bibcode = request.getParameter("bib");
	}catch(Exception e){
		//Sin bibcode
	}

	if(par_datablock==null) par_datablock=new String[0];	
	ZipOutputStream zouts = null;

	boolean hayDatos = false;
	
	Vector<ProdDatos> auxProds = new Vector<ProdDatos>();
	Vector<ProdRedDb> auxRedProds = new Vector<ProdRedDb>();
	Vector<ProdRedDb> auxHerProds = new Vector<ProdRedDb>();
	Vector<ProdRedDb> auxMegProds = new Vector<ProdRedDb>();
	ProdDatos[] productos = new ProdDatos[0];
	ProdRedDb[] productosRed = new ProdRedDb[0];
	ProdRedDb[] productosHer = new ProdRedDb[0];
	ProdRedDb[] productosMeg = new ProdRedDb[0];
	LogFileDb[] logs      = new LogFileDb[0];
	ProdErrorDb[] errors     = new ProdErrorDb[0];

	Vector<String> warnObls = new Vector<String>();
	
	ProdDatosAccess prodDatosAccess = new ProdDatosAccess(cn);
	WebSearcher searcher = new WebSearcher(cn);

	
	//*************************************************************
	//*  ESTADISTICAS                                             *
	//*************************************************************
	LogProdDb log = new LogProdDb();
	Date time = new Date();
	long size = 0;
	log.setLogpTime(new Timestamp(time.getTime()));
	log.setLogpHost(request.getRemoteHost());
	log.setLogpType("ZIP");
	
	
	// Buscamos los productos individuales
	if(par_prodId!=null){
		for(int i=0; i<par_prodId.length; i++){
			String[] aux = new String[3];
			if(par_prodId!=null){
				aux = par_prodId[i].split("\\.\\.");
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

			ProdDatosDb newProd = prodDatosAccess.selectById(prog_id, obl_id, prod_id);
			if(newProd!=null){
				auxProds.add(new ProdDatos(new File(newProd.getAbsolutePath())));
			}
			ProdDatosDb newRedProd = prodDatosAccess.selectRedById(prog_id, obl_id, prod_id);
			if(newRedProd!=null){
				auxProds.add(new ProdDatos(new File(newRedProd.getAbsolutePath())));
			}
			
		}
	}
	
	// Buscamos los bloques de productos
	String progId = "";
	String oblId = "";
	for(int i=0; i<par_datablock.length; i++){
		
		String[] partes = par_datablock[i].split("__");
		if(partes.length==4){
			
			
			ProdDatosDb[] newProds = new ProdDatosDb[0];
			ProdRedDb[] newRedProds = new ProdRedDb[0];
			ProdRedDb[] newHerProds = new ProdRedDb[0];
			ProdRedDb[] newMegProds = new ProdRedDb[0];
			
			if(partes[3].equalsIgnoreCase("ALLSCI")){
				newProds = searcher.selectProdsByOblId(partes[0], partes[1].substring(0,4)+"%", partes[2], "SCI");
			}else if(partes[3].equalsIgnoreCase("ALLCAL")){
				newProds = searcher.selectProdsByOblId(partes[0], partes[1].substring(0,4)+"%", partes[2], "CAL");
			}else if(partes[3].equalsIgnoreCase("ALLCSS")){
				
				//Aquí tenemos los datos del prod_id
				ProdDatosDb prod = prodDatosAccess.selectById(partes[0], partes[1], Integer.valueOf(partes[2]));
				
				//En este caso el ins_id no lo tenemos
				newProds = searcher.selectProdsByOblId(partes[0], partes[1].substring(0,4)+"%", partes[2], "CAL", String.valueOf(prod.getProdInitime()), String.valueOf(prod.getProdEndtime()));
			}else if(partes[3].equalsIgnoreCase("ALLRED")){
				System.out.println("ALLRED");
				try{
					newRedProds = searcher.selectRedByProdId(partes[0], partes[1]+"%", new Integer(partes[2]),"USER");
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
			}else if(partes[3].equalsIgnoreCase("ALLHER")){
				System.out.println("ALLHER");
				try{
					newHerProds = searcher.selectRedByProdId(partes[0], partes[1]+"%", new Integer(partes[2]),"HERVE");
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
			}else if(partes[3].equalsIgnoreCase("ALLMEG")){
				System.out.println("ALLMEG");
				try{
					newMegProds = searcher.selectRedByProdId(partes[0], partes[1]+"%", new Integer(partes[2]),"MEG");
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
			}else if(partes[3].equalsIgnoreCase("ALLAC")){
				newProds = searcher.selectProdsByOblId(partes[0], partes[1].substring(0,4)+"%", partes[2], "AC");
			}else if(partes[3].equalsIgnoreCase("ALLLOG")){
				// DESACTIVADA LA DESCARGA DE LOGS
				logs=searcher.selectLogsByOblId(partes[0], partes[1]);
				if(searcher.selectProductsWithWarningsByOblId(partes[0], partes[1].substring(0,4)+"%").length>1){
					warnObls.add(par_datablock[i]);
				}
			}else if(partes[3].equalsIgnoreCase("ALLERR")){
				//errors=searcher.selectErrsByOblId(partes[0], partes[1]);
			}
			
			for(int j=0; j<newProds.length; j++){
				boolean existe=false;
				for(int k=0; k<auxProds.size(); k++){
					ProdDatos prodAux = (ProdDatos)auxProds.elementAt(k);
					if(prodAux.getProgram().getProgId().equalsIgnoreCase(newProds[j].getProgId()) &&
						prodAux.getOblock().getOblId().equalsIgnoreCase(newProds[j].getOblId()) &&
						prodAux.getProdId().intValue()==newProds[j].getProdId().intValue()){
						existe=true;
						break;
					}
				}
				if(!existe) auxProds.add(new ProdDatos(new File(newProds[j].getAbsolutePath())));
			}

			for(int j=0; j<newRedProds.length; j++){
				boolean existe=false;
				for(int k=0; k<auxRedProds.size(); k++){
					ProdRedDb prodAux = (ProdRedDb)auxRedProds.elementAt(k);
					if(prodAux.getUsrId().equalsIgnoreCase(newRedProds[j].getUsrId()) &&
						prodAux.getColId().intValue()==newRedProds[j].getColId().intValue() &&
						prodAux.getPredId().intValue()==newRedProds[j].getPredId().intValue()){
						existe=true;
						break;
					}
				}
				if(!existe) auxRedProds.add(newRedProds[j]);
			}
			for(int j=0; j<newHerProds.length; j++){
				boolean existe=false;
				for(int k=0; k<auxHerProds.size(); k++){
					ProdRedDb prodAux = (ProdRedDb)auxHerProds.elementAt(k);
					if(prodAux.getUsrId().equalsIgnoreCase(newHerProds[j].getUsrId()) &&
						prodAux.getColId().intValue()==newHerProds[j].getColId().intValue() &&
						prodAux.getPredId().intValue()==newHerProds[j].getPredId().intValue()){
						existe=true;
						break;
					}
				}
				if(!existe) auxHerProds.add(newHerProds[j]);
			}
			for(int j=0; j<newMegProds.length; j++){
				boolean existe=false;
				for(int k=0; k<auxMegProds.size(); k++){
					ProdRedDb prodAux = (ProdRedDb)auxMegProds.elementAt(k);
					if(prodAux.getUsrId().equalsIgnoreCase(newMegProds[j].getUsrId()) &&
						prodAux.getColId().intValue()==newMegProds[j].getColId().intValue() &&
						prodAux.getPredId().intValue()==newMegProds[j].getPredId().intValue()){
						existe=true;
						break;
					}
				}
				if(!existe) auxMegProds.add(newMegProds[j]);
			}

			for(int j=0; j<errors.length; j++){
				boolean existe=false;
				for(int k=0; k<auxProds.size(); k++){
					ProdDatos prodAux = (ProdDatos)auxProds.elementAt(k);
					if(prodAux.getProgram().getProgId().equalsIgnoreCase(errors[j].getProgId()) &&
						prodAux.getOblock().getOblId().equalsIgnoreCase(errors[j].getOblId()) &&
						prodAux.getProdId().intValue()==errors[j].getProdeId().intValue()){
						existe=true;
						break;
					}
				}
				if(!existe) auxProds.add(new ProdDatos(new File(errors[j].getAbsolutePath())));
			}

		}

		//System.out.println(" ParDataBlock: "+par_datablock[i]);
		//System.out.println(" prods: "+productos.length);
		//System.out.println(" logs:  "+logs.length);
	}
	
	productos = (ProdDatos[])auxProds.toArray(new ProdDatos[0]);
	productosRed = (ProdRedDb[])auxRedProds.toArray(new ProdRedDb[0]);
	productosHer = (ProdRedDb[])auxHerProds.toArray(new ProdRedDb[0]);
	productosMeg = (ProdRedDb[])auxMegProds.toArray(new ProdRedDb[0]);

	if(productos.length>0 || productosRed.length>0 || productosHer.length>0 || productosMeg.length>0 || logs.length>0 || warnObls.size()>0){
		if(down_type!=null && down_type.equalsIgnoreCase("TARGZ") && (productos.length>0 || productosRed.length>0 || productosHer.length>0 || productosMeg.length>0 || logs.length>0)){
			//Descarga de datos en formato tar.gz
			
			
			TarArchiveOutputStream tgzouts = null;
			if(!hayDatos){
				response.setContentType ("application/tar.gz");
				response.setHeader("Content-Disposition", "attachment;filename=GTC_Data_"+new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()).toString().replaceAll("\\s","_")+".tar.gz");
				
				tgzouts = new TarArchiveOutputStream(new GzipCompressorOutputStream(response.getOutputStream()));
			}
			hayDatos = true;
	
			//======================================================= 
			//Control de Acceso                       
			//=======================================================
			ControlAcceso controlAcceso = new ControlAcceso(cn, contenedorSesion.getUser());
	
			for(int k=0; k<productosRed.length; k++){
				//Quitamos los dos últimos directorios (usuario y colección)
				File path = new File(productosRed[k].getAbsolutePath());
				//path = path.getParentFile().getParentFile();
				//Quitamos los dos últimos directorios (usuario y colección)
				File path1 = new File(productosRed[k].getPredPath());
				path1 = path1.getParentFile().getParentFile();
				
				tgzouts.putArchiveEntry(new TarArchiveEntry(path,"/reduced"+path1.getAbsolutePath()+"/"+path.getName()));
				tgzouts.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
				
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
			     IOUtils.copy(bis, tgzouts); // copy with 8K buffer, not close
			     bis.close();
			     
				//tgzouts.putNextEntry(new ZipEntry("/reduced"+path.getAbsolutePath()+"/"+productosRed[k].getPredFilename()));
				//size+=tgzouts.;
	
				/// Estadisticas pred
				LogProdSingleDb logProdSingleDb = new LogProdSingleDb();
				logProdSingleDb.setLprogId("pred");
				logProdSingleDb.setLoblId("pred");
				logProdSingleDb.setLprodId(productosRed[k].getPredId()); 
				logProdSingleDb.setLogpPriv(0);
				log.addProd(logProdSingleDb);
				
				
				tgzouts.closeArchiveEntry();
			}
			
			for(int k=0; k<productosHer.length; k++){
				//Quitamos los dos últimos directorios (usuario y colección)
				File path = new File(productosHer[k].getAbsolutePath());;
				String pathHer="";
				//Modificamos path para quitar semestre
				if(productosHer[k].getAbsolutePath().contains("images")){
					pathHer = productosHer[k].getAbsolutePath().substring(productosHer[k].getAbsolutePath().indexOf("/images"));
					//System.out.println("PathHer: "+pathHer);
				}else{
					pathHer = productosHer[k].getAbsolutePath();
				}
				//Si continene el nombre lo borramos
				pathHer = pathHer.replaceAll(productosHer[k].getPredFilename(), "");
				
				tgzouts.putArchiveEntry(new TarArchiveEntry(path,pathHer));
				tgzouts.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
				
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
			     IOUtils.copy(bis, tgzouts); // copy with 8K buffer, not close
			     bis.close();
			     
				//tgzouts.putNextEntry(new ZipEntry("/reduced"+path.getAbsolutePath()+"/"+productosRed[k].getPredFilename()));
				//size+=tgzouts.;
	
				/// Estadisticas pred
				LogProdSingleDb logProdSingleDb = new LogProdSingleDb();
				logProdSingleDb.setLprogId("predH");
				logProdSingleDb.setLoblId("predH");
				logProdSingleDb.setLprodId(productosHer[k].getPredId());
				logProdSingleDb.setLogpPriv(0);
				log.addProd(logProdSingleDb);
				
				
				tgzouts.closeArchiveEntry();
			}
			
			String fichLog = null;
			
			for(int k=0; k<productosMeg.length; k++){
				//Quitamos los dos últimos directorios (usuario y colección)
				File path = new File(productosMeg[k].getAbsolutePath());
				String pathMeg="";
				//Modificamos path para quitar semestre
				//if(productosMeg[k].getAbsolutePath().contains("megara")){
				try{
					pathMeg = productosMeg[k].getAbsolutePath().substring(productosMeg[k].getAbsolutePath().indexOf("/GTC"));
				//}else{
				}catch(Exception e3){
					pathMeg = productosMeg[k].getAbsolutePath();
				}
				
				if(productosMeg[k].getPredFilename().contains("-HORS-")){
					
					fichLog=" - x...Spectroscopy.fits: Reduced, non-normalized science spectrum in FITS format (Hanisch et al. 2001; A&A, 376, 359-380)(1). Each x...Spectroscopy.fits has associated a previsualization image (x...Spectroscopy.png).\n\n"+
							" - n...Spectroscopy.fits: Reduced, normalized science spectrum in FITS format (Hanisch et al. 2001; A&A, 376, 359-380)(1). The spectrum has been normalized using the flatfield to track the spectral response of each order. Each n...Spectroscopy.fits has associated a previsualization image (n...Spectroscopy.png).\n\n"+
							" - v...Spectroscopy.fits: Reduced science spectrum in Virtual Observatory format (tabular FITS with three columns: Wavelength (in nanometers), flux (in electrons/pixel) and variance (in electrons/pixel)**2. The error in flux is given by sqrt(var). The spectrum has been normalized by fitting a low degree polynomial after substracting the flatfield.\n\n"+
							" - x...Cal.fits: Reduced, non-normalized emission lamp (arc) spectrum in FITS format (Hanisch et al. 2001; A&A, 376, 359-380)(1). Each x...Cal.fits has associated a previsualization image (x...Cal.png).\n"+
							"   The arc used in the reduction process is built by linearly interpolating between the two closest arcs (one before and one after the science observation). The FILECAL and MJDCAL keywords provide information on the arc spectra used in the reduction.\n\n"+
							" - xflats.fits: Flatfield used in the reduction proces in FITS format (Hanisch et al. 2001; A&A, 376, 359-380)(1). Each xflats.fits file has associated a previsualization image (x...flat.png).\n\n"+
							" (1) https://ui.adsabs.harvard.edu/abs/2001A%26A...376..359H/abstract";
				}
				
				tgzouts.putArchiveEntry(new TarArchiveEntry(path,pathMeg));
				tgzouts.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
				
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
			     IOUtils.copy(bis, tgzouts); // copy with 8K buffer, not close
			     bis.close();
			     
/* 			     
			     //Creamos el log
			     out.putNextEntry(new ZipEntry("log.txt"));

				out.write(log.getBytes());
			
				out.flush();
				out.closeEntry(); */
			     
				//tgzouts.putNextEntry(new ZipEntry("/reduced"+path.getAbsolutePath()+"/"+productosRed[k].getPredFilename()));
				//size+=tgzouts.;
	
				/// Estadisticas pred
				LogProdSingleDb logProdSingleDb = new LogProdSingleDb();
				logProdSingleDb.setLprogId("predM");
				logProdSingleDb.setLoblId("predM");
				logProdSingleDb.setLprodId(productosMeg[k].getPredId());
				logProdSingleDb.setLogpPriv(0);
				log.addProd(logProdSingleDb);
				
				
				tgzouts.closeArchiveEntry();
			}
				
			if(fichLog!=null){
				
				try{
					zouts.putNextEntry(new ZipEntry("AAAREAD.ME_HORuS"));
					
					//java.net.URL url = new java.net.URL("https://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/servlet/WarningLog?progId="+partes[0].trim()+"&oblId="+partes[1].trim());
					//InputStream ins = url.openStream();
					
					zouts.write(fichLog.getBytes());
					
					/* InputStream ins =fichLog.;
					
					byte[] buff = new byte[1024];
					int len = 0;
					
					while( (len=ins.read(buff))>0 ){
						zouts.write(buff, 0, len);
						size+=len;
					} */
					
					zouts.closeEntry();
				}catch(java.util.zip.ZipException e){
					// No hacemos nada si la entrada ya existe.
				}catch(Exception e){
					//System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
				
			for(int k=0; k<productos.length; k++){
				if(!controlAcceso.hasPerm(productos[k].getProgram().getProgId(), productos[k].getOblock().getOblId(), productos[k].getProdId(),contenedorSesion.getUser())){
					//Cogemos los que lo cumplen (true) y los que no dan resultado (sin fecha). Por eso !false entraría aquí y el resto no
				}else{
					
					
					File path = new File(productos[k].getFile().getAbsolutePath());
					
					tgzouts.putArchiveEntry(new TarArchiveEntry(path,productos[k].getFile().getAbsolutePath().substring(productos[k].getFile().getAbsolutePath().indexOf("/GTC"))));
					tgzouts.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
					
					//if(path.isFile()){
					
					BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
				     IOUtils.copy(bis, tgzouts); // copy with 8K buffer, not close
				     bis.close();
					//}else{
					//	System.out.println("No es file");
					//}
					//zouts.putNextEntry(new ZipEntry(productos[k].getFile().getAbsolutePath().substring(productos[k].getFile().getAbsolutePath().indexOf("/GTC"))));
					//size+=productos[k].writeToStream(tgzouts);
					
					
					/// Estadisticas
					LogProdSingleDb logProdSingleDb = new LogProdSingleDb();
					logProdSingleDb.setLprogId(productos[k].getProgram().getProgId());
					logProdSingleDb.setLoblId(productos[k].getOblock().getOblId());
					logProdSingleDb.setLprodId(productos[k].getProdId());
					logProdSingleDb.setLogpPriv(productos[k].getDate());
					log.addProd(logProdSingleDb);
	
					tgzouts.closeArchiveEntry();
				}
			}
	
			for(int k=0; k<logs.length; k++){
				if(!controlAcceso.hasPerm(logs[k].getProgId(), logs[k].getOblId(),contenedorSesion.getUser())){
					//Cogemos los que lo cumplen (true) y los que no dan resultado (sin fecha). Por eso !false entraría aquí y el resto no
				}else{
		
					File path = new File(logs[k].getLogPath().substring(1)+logs[k].getLogFilename());
					tgzouts.putArchiveEntry(new TarArchiveEntry(path,logs[k].getLogPath().substring(logs[k].getLogPath().indexOf("/GTC"))+logs[k].getLogFilename()));
					tgzouts.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
					
					BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
				     IOUtils.copy(bis, tgzouts); // copy with 8K buffer, not close
				     bis.close();
				     
//Estas dos líneas estaban quitadas ¿?
				    //zouts.putNextEntry(new ZipEntry(logs[k].getLogPath().substring(1)+logs[k].getLogFilename()));
					//size+=logs[k].writeToStream(tgzouts);
					tgzouts.closeArchiveEntry();
				}
	
			}
			
			/*for(int k=0; k<warnObls.size(); k++){
				String[] partes = ((String) warnObls.elementAt(k)).split("__");
	
				if(controlAcceso.hasPerm(partes[0],contenedorSesion.getUser())){
					//ProdDatosDb[] prods = searcher.selectProductsWithWarningsByOblId(partes[0], partes[1]);
	
					try{ 
						 
						
						File path = File.createTempFile("warnings_"+partes[0].trim()+"_"+partes[1].trim(),".txt");
						tgzouts.putArchiveEntry(new TarArchiveEntry(path,path.getName()));
						tgzouts.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
						
						BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
					     IOUtils.copy(bis, tgzouts); // copy with 8K buffer, not close
					     bis.close();
					     
						//zouts.putNextEntry(new ZipEntry(logs[k].getLogPath().substring(1)+logs[k].getLogFilename()));
						//size+=logs[k].writeToStream(tgzouts);
						tgzouts.closeArchiveEntry();
						
						/* 
						File path = File.createTempFile("warnings_"+partes[0].trim()+"_"+partes[1].trim(),".txt");
						BufferedWriter bw = new BufferedWriter(new FileWriter(path));
						
					
					     
						//zouts.putNextEntry(new ZipEntry("warnings_"+partes[0].trim()+"_"+partes[1].trim()+".txt"));
						
						java.net.URL url = new java.net.URL("http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/servlet/WarningLog?progId="+partes[0].trim()+"&oblId="+partes[1].trim());
			
						InputStream ins = url.openStream();
			
						byte[] buff = new byte[1024];
						int len = 0;
						
						while( (len=ins.read(buff))>0 ){
							bw.write("s"); 
							size+=len;
						}
						bw.close();
						
						tgzouts.putArchiveEntry(new TarArchiveEntry(path));
						tgzouts.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
						
						BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
					     IOUtils.copy(bis, tgzouts); // copy with 8K buffer, not close
					     bis.close();
						
						tgzouts.closeArchiveEntry();
						
						path.delete(); 
						
					 }catch(java.util.zip.ZipException e){
						// No hacemos nada si la entrada ya existe.
					}
				}
	
			}*/
		
			tgzouts.close();
			
			
			
			
			
			

			
			
		}else{
			if(!hayDatos){
				
				//Si es reducido metemos el bibcode
				String nameF = null;
				if(bibcode!=null){
					nameF = "bibcode_"+bibcode+"_"+new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()).toString().replaceAll("\\s","_");
				}else{
					nameF = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()).toString().replaceAll("\\s","_");
				}
				response.setContentType ("application/zip");
				response.setHeader("Content-Disposition", "attachment;filename=GTC_Data_"+nameF+".zip");
				zouts = new ZipOutputStream(response.getOutputStream());
			}
			hayDatos = true;
	
			//======================================================= 
			//Control de Acceso                       
			//=======================================================
			ControlAcceso controlAcceso = new ControlAcceso(cn, contenedorSesion.getUser());
	
			for(int k=0; k<productosRed.length; k++){
				//Quitamos los dos últimos directorios (usuario y colección)
				File path = new File(productosRed[k].getPredPath());
				path = path.getParentFile().getParentFile();
				
				zouts.putNextEntry(new ZipEntry("/reduced"+path.getAbsolutePath()+"/"+productosRed[k].getPredFilename()));
				size+=productosRed[k].writeToStream(zouts);
	
				/// Estadisticas pred
				LogProdSingleDb logProdSingleDb = new LogProdSingleDb();
				logProdSingleDb.setLprogId("pred");
				logProdSingleDb.setLoblId("pred");
				logProdSingleDb.setLprodId(productosRed[k].getPredId());
				logProdSingleDb.setLogpPriv(0);
				log.addProd(logProdSingleDb);
				
				
				zouts.closeEntry();
			}
			
			for(int k=0; k<productosHer.length; k++){
				//Quitamos los dos últimos directorios (usuario y colección)
				File path = new File(productosHer[k].getPredPath());
				String pathHer;
				//Modificamos path para quitar semestre
				if(productosHer[k].getAbsolutePath().contains("images")){
					pathHer = productosHer[k].getAbsolutePath().substring(productosHer[k].getAbsolutePath().indexOf("/images"));
					//System.out.println("PathHer: "+pathHer);
				}else{
					pathHer = productosHer[k].getAbsolutePath();
				}
				
				//zouts.putNextEntry(new ZipEntry(path.getAbsolutePath()+"/"+productosHer[k].getPredFilename()));
				//Si contiene el nombre lo quitamos
				pathHer = pathHer.replaceAll(productosHer[k].getPredFilename(), "");
				
				zouts.putNextEntry(new ZipEntry(pathHer+productosHer[k].getPredFilename()));
				size+=productosHer[k].writeToStream(zouts);
	
				/// Estadisticas pred
				LogProdSingleDb logProdSingleDb = new LogProdSingleDb();
				logProdSingleDb.setLprogId("predH");
				logProdSingleDb.setLoblId("predH");
				logProdSingleDb.setLprodId(productosHer[k].getPredId());
				logProdSingleDb.setLogpPriv(0);
				log.addProd(logProdSingleDb);
				
				
				zouts.closeEntry();
			}
			
			String fichLog = null;
			
			for(int k=0; k<productosMeg.length; k++){
				//Quitamos los dos últimos directorios (usuario y colección)
				File path = new File(productosMeg[k].getPredPath());
				String pathMeg;
				//Modificamos path para quitar semestre
				//if(productosMeg[k].getAbsolutePath().contains("megara")){
				try{
					pathMeg = productosMeg[k].getAbsolutePath().substring(productosMeg[k].getAbsolutePath().indexOf("/GTC"));
				//}else{
				}catch(Exception e3){
					pathMeg = productosMeg[k].getAbsolutePath();
				}
				
				if(productosMeg[k].getPredFilename().contains("-HORS-")){
					fichLog=" - x...Spectroscopy.fits: Reduced, non-normalized science spectrum in FITS format (Hanisch et al. 2001; A&A, 376, 359-380)(1). Each x...Spectroscopy.fits has associated a previsualization image (x...Spectroscopy.png).\n\n"+
							" - n...Spectroscopy.fits: Reduced, normalized science spectrum in FITS format (Hanisch et al. 2001; A&A, 376, 359-380)(1). The spectrum has been normalized using the flatfield to track the spectral response of each order. Each n...Spectroscopy.fits has associated a previsualization image (n...Spectroscopy.png).\n\n"+
							" - v...Spectroscopy.fits: Reduced science spectrum in Virtual Observatory format (tabular FITS with three columns: Wavelength (in nanometers), flux (in electrons/pixel) and variance (in electrons/pixel)**2. The error in flux is given by sqrt(var). The spectrum has been normalized by fitting a low degree polynomial after substracting the flatfield.\n\n"+
							" - x...Cal.fits: Reduced, non-normalized emission lamp (arc) spectrum in FITS format (Hanisch et al. 2001; A&A, 376, 359-380)(1). Each x...Cal.fits has associated a previsualization image (x...Cal.png).\n"+
							"   The arc used in the reduction process is built by linearly interpolating between the two closest arcs (one before and one after the science observation). The FILECAL and MJDCAL keywords provide information on the arc spectra used in the reduction.\n\n"+
							" - xflats.fits: Flatfield used in the reduction proces in FITS format (Hanisch et al. 2001; A&A, 376, 359-380)(1). Each xflats.fits file has associated a previsualization image (x...flat.png).\n\n"+
							" (1) https://ui.adsabs.harvard.edu/abs/2001A%26A...376..359H/abstract";
				}
//System.out.println(pathMeg+"  -name-   "+productosMeg[k].getPredFilename());
				
				//zouts.putNextEntry(new ZipEntry(path.getAbsolutePath()+"/"+productosHer[k].getPredFilename()));
				
				if(pathMeg.contains(productosMeg[k].getPredFilename())){
					zouts.putNextEntry(new ZipEntry(pathMeg));
				}else{
					zouts.putNextEntry(new ZipEntry(pathMeg+productosMeg[k].getPredFilename()));
				}
				
				size+=productosMeg[k].writeToStream(zouts);
	
				/// Estadisticas pred
				LogProdSingleDb logProdSingleDb = new LogProdSingleDb();
				logProdSingleDb.setLprogId("predM");
				logProdSingleDb.setLoblId("predM");
				logProdSingleDb.setLprodId(productosMeg[k].getPredId());
				logProdSingleDb.setLogpPriv(0);
				log.addProd(logProdSingleDb);
				
				
				zouts.closeEntry();
			}
			
				
			if(fichLog!=null){
				
				try{
					zouts.putNextEntry(new ZipEntry("AAAREAD.ME_HORuS"));
					
					//java.net.URL url = new java.net.URL("https://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/servlet/WarningLog?progId="+partes[0].trim()+"&oblId="+partes[1].trim());
					//InputStream ins = url.openStream();
					
					zouts.write(fichLog.getBytes());
					
					/* InputStream ins =fichLog.;
					
					byte[] buff = new byte[1024];
					int len = 0;
					
					while( (len=ins.read(buff))>0 ){
						zouts.write(buff, 0, len);
						size+=len;
					} */
					
					zouts.closeEntry();
				}catch(java.util.zip.ZipException e){
					// No hacemos nada si la entrada ya existe.
				}catch(Exception e){
					//System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
	
			for(int k=0; k<productos.length; k++){
				if(!controlAcceso.hasPerm(productos[k].getProgram().getProgId(), productos[k].getOblock().getOblId(),productos[k].getProdId(),contenedorSesion.getUser())){
					//Cogemos los que lo cumplen (true) y los que no dan resultado (sin fecha). Por eso !false entraría aquí y el resto no
				}else{
					zouts.putNextEntry(new ZipEntry(productos[k].getFile().getAbsolutePath().substring(productos[k].getFile().getAbsolutePath().indexOf("/GTC"))));
					size+=productos[k].writeToStream(zouts);
	
					
					/// Estadisticas
					LogProdSingleDb logProdSingleDb = new LogProdSingleDb();
					logProdSingleDb.setLprogId(productos[k].getProgram().getProgId());
					logProdSingleDb.setLoblId(productos[k].getOblock().getOblId());
					logProdSingleDb.setLprodId(productos[k].getProdId());
					logProdSingleDb.setLogpPriv(productos[k].getDate());
					log.addProd(logProdSingleDb);
	
					zouts.closeEntry();
				}
			}
	
			for(int k=0; k<logs.length; k++){
				if(!controlAcceso.hasPerm(logs[k].getProgId(), logs[k].getOblId(),contenedorSesion.getUser())){
					//Cogemos los que lo cumplen (true) y los que no dan resultado (sin fecha). Por eso !false entraría aquí y el resto no
				}else{
		
					zouts.putNextEntry(new ZipEntry(logs[k].getLogPath().substring(logs[k].getLogPath().indexOf("/GTC"))+logs[k].getLogFilename()));
					size+=logs[k].writeToStream(zouts);
					zouts.closeEntry();
				}
	
			}
			
			for(int k=0; k<warnObls.size(); k++){
				String[] partes = ((String) warnObls.elementAt(k)).split("__");
	
				//if(!controlAcceso.hasPerm(partes[0], partes[1],contenedorSesion.getUser())){
				//}else{
					//ProdDatosDb[] prods = searcher.selectProductsWithWarningsByOblId(partes[0], partes[1]);
	
					try{
						zouts.putNextEntry(new ZipEntry("warnings_"+partes[0].trim()+"_"+partes[1].trim()+".txt"));
						
						java.net.URL url = new java.net.URL("https://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/servlet/WarningLog?progId="+partes[0].trim()+"&oblId="+partes[1].trim());
						InputStream ins = url.openStream();
						
						byte[] buff = new byte[1024];
						int len = 0;
						
						while( (len=ins.read(buff))>0 ){
							zouts.write(buff, 0, len);
							size+=len;
						}
						
						zouts.closeEntry();
					}catch(java.util.zip.ZipException e){
						// No hacemos nada si la entrada ya existe.
					}catch(Exception e){
						//System.out.println(e.getMessage());
						e.printStackTrace();
					}
				}
	
			//}
		}		
	}else{
		MSG = "No data products found.";
	}

	if(zouts!=null){
		zouts.flush();
		zouts.close();
	}
	
	/// Estadisticas fin...
	Date timeEnd = new Date();
	log.setLogpElapsed( new Double((double)(timeEnd.getTime()-time.getTime())/1000) );
	log.setLogpSize(new Long(size));
	
	LogProdAccess logAccess;
	try {
		
		logAccess = new LogProdAccess(cn);
		logAccess.insert(log);
	} catch (SQLException e) {
		e.printStackTrace();
	}


	


%>


<%if(MSG.length()>0){ %>

<%--------     I N I C I O    P Á G I N A    H T M L    -------------------%>
<html>
<%= cabeceraPagina %>
<body>
<%= encabezamiento %>

<p><br></p>

<font color="red"><%=MSG %></font>

<%= pie %>
</body>
</html>
<%} %>


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