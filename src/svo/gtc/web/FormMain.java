package svo.gtc.web;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import svo.gtc.utiles.cds.ResultadoSesameClient;
import svo.gtc.utiles.cds.SesameClient_new;
import svo.gtc.utiles.cds.NameResolver;
import svo.gtc.utiles.Coordenadas;

/**
 * @author jmalacid
 *
 */
public class FormMain {

	static Logger logger = Logger.getLogger("svo.gtc");
	
	
	private		String		origen	  = null;
	
    private     Double		posRadius = new Double(0.001388889);
    private     String[]	objectList = null;
    private     Integer[]	prodList = null;
	private 	Double[][]	coordsList = null;
	private		Date[]		dates = null;
	private		String[]	instModeCodes = null;
	private		String		progId = null;
	private		String		oblId = null;
	private		String		prodId = null;
	private		Integer		orderBy = new Integer(0); 
	private		Integer		rpp = new Integer(50);
	private		Integer		pts = new Integer(1);
	private		String		filename = null;
	private 	Integer		Lp		= new Integer(0);
	
	private String id = null;
	
	
	private 	String 		bibcode = null;
	
	private 	String 		stateant = null;

	private		String[]	errors = new String[0];
	
	private		String[]	warning = new String[0];
	
	private 	boolean		allInstruments = false;
	
	private		boolean		onlyReduced = false;
	
	public FormMain(){
	}

	public FormMain(HttpServletRequest request) throws FormParameterException{
		procesaRequest(request);
	}

	

	public void procesaRequest(HttpServletRequest request) throws FormParameterException{
		
		//**************************************
		//      Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		logger.debug("isMultipart="+isMultipart);

		this.origen = request.getParameter("origen");
		String obj_list = request.getParameter("obj_list");
		String radius = request.getParameter("radius");
		String dateinit_d = request.getParameter("dateinit_d");
		String dateinit_m = request.getParameter("dateinit_m");
		String dateinit_y = request.getParameter("dateinit_y");
		String dateend_d = request.getParameter("dateend_d");
		String dateend_m = request.getParameter("dateend_m");
		String dateend_y = request.getParameter("dateend_y");
		String[] instCode = request.getParameterValues("instCode"); 
		String prog_id = request.getParameter("prog_id");
		String obl_id = request.getParameter("obl_id");
		String prod_id = request.getParameter("prod_id");
		String lp = request.getParameter("lp");
		String order_by = request.getParameter("order_by");
		String rpp_str = request.getParameter("rpp");
		String pts_str = request.getParameter("pts");
		String filename_str = request.getParameter("filename");
		String onlyRed_str = request.getParameter("onlyRed");
		//String ESO = request.getParameter("eso");
		String bibcode = request.getParameter("bib");
		String stateant = request.getParameter("stateant");
		id= request.getParameter("id");
		
		byte[] ficheroObjList = null;
		
		if(isMultipart){
			
			
			Vector<String> instCodeVcr = new Vector<String>();

			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Parse the request
			List items = null;
			try {
				items = upload.parseRequest(request);
			} catch (FileUploadException e) {
				e.printStackTrace();
				throw new FormParameterException("ERROR processing the request");
			}


			FileItem[] elementos = (FileItem[])items.toArray(new FileItem[0]);


			// Process the uploaded items
			for(int i=0;i<elementos.length; i++) {
				if(elementos[i].isFormField()){
					String name  = elementos[i].getFieldName();
					String value = elementos[i].getString();

					if(name.equals("instCode")){
						instCodeVcr.add(value.trim());
					}

					if(value.trim().length()>0){
						if( name.equalsIgnoreCase("origen")){
							this.origen = value.trim();
						}else if( name.equalsIgnoreCase("obj_list")){
							obj_list=value.trim();
							logger.debug("obj_list="+obj_list);
						}else if(name.equals("radius")){
							radius=value.trim();
						}else if(name.equals("dateinit_d")){
							dateinit_d=value.trim();
						}else if(name.equals("dateinit_m")){
							dateinit_m=value.trim();
						}else if(name.equals("dateinit_y")){
							dateinit_y=value.trim();
						}else if(name.equals("dateend_d")){
							dateend_d=value.trim();
						}else if(name.equals("dateend_m")){
							dateend_m=value.trim();
						}else if(name.equals("dateend_y")){
							dateend_y=value.trim();
						}else if(name.equals("prog_id")){
							prog_id=value.trim();
						}else if(name.equals("obl_id")){
							obl_id=value.trim();
						}else if(name.equals("prod_id")){
							prod_id=value.trim();
						}else if(name.equals("lp")){
							lp=value.trim();
						}else if(name.equals("order_by")){
							order_by=value.trim();
						}else if(name.equals("rpp")){
							rpp_str=value.trim();
						}else if(name.equals("pts")){
							pts_str=value.trim();
						}else if(name.equals("filename")){
							filename_str=value.trim();
						}else if(name.equals("onlyRed")){
							onlyRed_str=value.trim();
						}else if(name.equals("bib")){
							this.bibcode=value.trim();
						}else if(name.equals("stateant")){
							this.stateant=value.trim();
						}else if(name.equals("id")){
							id=value.trim();
						}
					}


				}else{
					/// Es el fichero
					if(elementos[i].getName()!=null && elementos[i].getName().length()>0 && elementos[i].getSize()>0){
						ficheroObjList=elementos[i].get();
					}
				}
			}
			
			// Generamos el array de inst_code
			instCode = (String[])instCodeVcr.toArray(new String[0]);
			
		}
		
		
		// Lista de objetos
		if((obj_list!=null && obj_list.trim().length()>0) || (ficheroObjList!=null && ficheroObjList.length>0) ){
			String[] lineas1 = new String[0];
			String[] lineas2 = new String[0];
			
			if(obj_list!=null && obj_list.trim().length()>0){
				lineas1 = obj_list.trim().split("[\n\r]");
			}
			if(ficheroObjList!=null && ficheroObjList.length>0){
				lineas2 = new String(ficheroObjList).split("[\\n\\r]");
			}
			
			String[] lineas = new String[lineas1.length+lineas2.length];
			int cont = 0;
			for(int i=0; i<lineas1.length; i++){
				lineas[cont]=lineas1[i];
				cont++;
			}
			for(int i=0; i<lineas2.length; i++){
				lineas[cont]=lineas2[i];
				cont++;
			}
			
			Vector<String> auxObjList = new Vector<String>();
			Vector<Integer> auxProdList = new Vector<Integer>();
			Vector<Double[]> auxCoordsList = new Vector<Double[]>();
			for(int i=0; i<lineas.length; i++){
				if(lineas[i].trim().length()==0) continue;
				if(lineas[i].trim().matches("^[0-9]{1,10}$")){
					
					Integer aux = new Integer(lineas[i].trim());
					auxProdList.add(aux);
				}else if(Coordenadas.checkCoordinatesFormat(lineas[i].trim())){
					Double[] coords = Coordenadas.coordsInDeg(lineas[i].trim());
					if(coords!=null){
						auxCoordsList.add(coords);
					}
				}else{
					try {
						
						NameResolver nr = new NameResolver(lineas[i].trim().replaceAll("%",""));

						nr.start();
						//System.out.println("Thread lanzado");
						
						while(!nr.isFinished()){
							//System.out.println("Paso");
							Thread.sleep(1000);
							if(nr.getElapsedTime()>30000){
								//this.addError("Sesame is not working properly, try it using coordinates.");
								throw new Exception("Sesame is not working properly, try it using coordinates.");
							}
						}
						
					//Si sesame está funcionando resolvemos
						ResultadoSesameClient resultado = SesameClient_new.sesameSearch(lineas[i].trim());
						
						
						if(resultado.getRa()!=null && resultado.getDe()!=null){	
							Double[] coords = new Double[]{resultado.getRa(),resultado.getDe()};
							auxCoordsList.add(coords);
						}else{
							//Probamos si es un J...
							if(lineas[i].trim().startsWith("J")){
								String coor = getCoordJ(lineas[i].trim());
								
								if(coor!=null && Coordenadas.checkCoordinatesFormat(coor)){
									Double[] coords = Coordenadas.coordsInDeg(coor);
									if(coords!=null){
										auxCoordsList.add(coords);
									}else{
										this.addError("Unable to resolve object");
									}
									
								}else{
									this.addError("Unable to resolve object");
								}
							}else{
								this.addError("Unable to resolve object");
							}
						}
						
					} catch (Exception e) {
						
						if(e.getMessage().contains("Sesame")){
							this.addError("Sesame is not working properly, try it using coordinates.");
							//throw new Exception(e.getMessage());
						}else{
						//this.addWarning("Unable to resolve object: \""+lineas[i].trim().replaceAll("%", "").replaceAll("<", "").replaceAll(">", "").replaceAll("script", "")+"\"");
						try{
							//Probamos si es un J...
							if(lineas[i].trim().startsWith("J")){
								String coor = getCoordJ(lineas[i].trim());
								
								if(coor!=null && Coordenadas.checkCoordinatesFormat(coor)){
									Double[] coords = Coordenadas.coordsInDeg(coor);
									if(coords!=null){
										auxCoordsList.add(coords);
									}else{
										this.addError("Unable to resolve object");
									}
									
								}else{
									this.addError("Unable to resolve object");
								}
							}else{
								this.addError("Unable to resolve object");
							}
						}catch (Exception e1) {
							this.addError("Unable to resolve object");
						}
					}
					}
					
				}
			}
			
			
			if(auxObjList.size()>0){
				objectList = (String[])auxObjList.toArray(new String[0]);
			}
			if(auxProdList.size()>0){
				prodList = (Integer[])auxProdList.toArray(new Integer[0]);
			}
			if(auxCoordsList.size()>0){
				coordsList = new Double[auxCoordsList.size()][2];
				for(int i=0; i<coordsList.length; i++){
					coordsList[i][0] = ((Double[])auxCoordsList.elementAt(i))[0];
					coordsList[i][1] = ((Double[])auxCoordsList.elementAt(i))[1];
				}
			}
			
			if(auxObjList.size()+auxProdList.size()+auxCoordsList.size()==0){
				this.addError("No object resolved");
			}
			
		}
		
		// Radio de busqueda
		if(radius!=null && radius.trim().length()>0){
			Double radiusAux = null;
			try{
				//pasamos el radio a arcominutos
				radiusAux=(new Double(radius)*60);
			}catch(NumberFormatException e){
				addError("Bad format for radius: \""+radius+"\"");
			}
			
			if(radiusAux!=null){
				// En el formulario está en segundos de arco, lo pasamos a deg.
				posRadius=new Double(radiusAux.doubleValue()/3600);
			}
		}
		
		//Fechas
		Date dateInit = null;
		Date dateEnd  = null;
		if(dateinit_d!=null && dateinit_m!=null && dateinit_y!=null
		   && dateinit_d.trim().length()>0 && dateinit_m.trim().length()>0 && dateinit_y.trim().length()>0){
			String auxDate = dateinit_y.trim()+"-"+dateinit_m.trim()+"-"+dateinit_d.trim();
			try{
				auxDate=validaDate(auxDate);
				dateInit=Date.valueOf(auxDate);
			}catch(IllegalArgumentException e){
				addError("Bad format for initial date: \""+auxDate+"\"");
			}
		}
		if(dateend_d!=null && dateend_m!=null && dateend_y!=null
		   && dateend_d.trim().length()>0 && dateend_m.trim().length()>0 && dateend_y.trim().length()>0){
			String auxDate = dateend_y.trim()+"-"+dateend_m.trim()+"-"+dateend_d.trim();
			try{
				auxDate=validaDate(auxDate);
				dateEnd=Date.valueOf(auxDate);
			}catch(IllegalArgumentException e){
				addError("Bad format for end date: \""+auxDate+"\"");
			}
		}
		if(dateInit!=null || dateEnd!=null){
			dates=new Date[]{dateInit,dateEnd};
		}

		// Only Reduced Data
		if(onlyRed_str!=null && onlyRed_str.trim().length()>0){
			onlyReduced=true;
		}

		// Instrument Codes
		if(instCode!=null && instCode.length>0){
			Vector<String> auxInstCode = new Vector<String>();
			for(int i=0; i<instCode.length; i++){
				if(instCode[i]!=null && instCode[i].trim().length()>0){
					auxInstCode.add(instCode[i]);
				}
			}
			if(auxInstCode.size()>0){
				instModeCodes = (String[])auxInstCode.toArray(new String[0]);
			}
		}
		
		// Program ID
		if(prog_id!=null && prog_id.trim().length()>0){
			progId=prog_id.trim();
		}
		
		// ObsBlock ID
		if(obl_id!=null && obl_id.trim().length()>0){
			oblId=obl_id.trim();
		}

		// Program ID
		if(prod_id!=null && prod_id.trim().length()>0){
			prodId=prod_id.trim();
		}
				
		// LP
		if(lp!=null && lp.trim().length()>0){
			try{
				Lp = new Integer(lp);
			}catch(NumberFormatException e){}
		}
		
		// Order By
		if(order_by!=null && order_by.trim().length()>0){
			try{
				orderBy = new Integer(order_by);
			}catch(NumberFormatException e){}
		}

		// Results per Page
		if(rpp_str!=null && rpp_str.trim().length()>0){
			try{
				rpp = new Integer(rpp_str);
			}catch(NumberFormatException e){}
		}

		// Page to show
		if(pts_str!=null && pts_str.trim().length()>0){
			try{
				pts = new Integer(pts_str);
			}catch(NumberFormatException e){}
		}

		// Filename
		if(filename_str!=null && filename_str.trim().length()>0){
			try{
				filename = filename_str.trim();
			}catch(NumberFormatException e){}
		}
		// ID
		if(obl_id!=null && obl_id.trim().length()>0){
			oblId=obl_id.trim();
		}
		
	}
	
	public static String validaDate(String date) throws IllegalArgumentException{
		
		String dateRegexp = "([0-9]{4})-([0-9]{1,2})-([0-9]{1,2})";
		int year=0;
		int month=0;
		int day=0;

		GregorianCalendar cal = new GregorianCalendar();
		if(date.trim().matches(dateRegexp)){
			try{
				year 	= Integer.parseInt(date.trim().replaceAll(dateRegexp, "$1"));
				month 	= Integer.parseInt(date.trim().replaceAll(dateRegexp, "$2"));
				day 	= Integer.parseInt(date.trim().replaceAll(dateRegexp, "$3"));
			}catch(NumberFormatException e){
				e.printStackTrace();
				throw new IllegalArgumentException(e.getMessage());
			}
			
			if(year<1900 || year>2200){
				throw new IllegalArgumentException("Year out of range: "+year);
			}
			if(month < 1 || month > 12){
				throw new IllegalArgumentException("Month out of range: "+month);
			}
			if( (month==1 || month==3 || month==5 || 
					month==7 || month==8 || month==10 || 
					month==12) && (day<1 || day>31) ){
				throw new IllegalArgumentException("Day out of range: "+day);
			}else if( (month==4 || month==6 || 
					month==9 || month==11 ) && (day<1 || day>30) ){
				throw new IllegalArgumentException("Day out of range: "+day);
			}else if(month==2 && 
					( (cal.isLeapYear(year) && (day<1 || day>29)) ||
					  (!cal.isLeapYear(year) && (day<1 || day>28)) ) ){
				throw new IllegalArgumentException("Day out of range: "+day);
			}
		}else{
			throw new IllegalArgumentException("Bad format for date "+date);
		}

		// Añadimos padding de 0 al principio de mes y dia.
		DecimalFormat nf = (DecimalFormat)NumberFormat.getInstance();
		nf.applyLocalizedPattern("00");

		return year+"-"+nf.format(month)+"-"+nf.format(day);
		
	}



	public Double getPosRadius() {
		return posRadius;
	}



	public String[] getObjectList() {
		return objectList;
	}


	public Integer[] getProdList() {
		return prodList;
	}

	public Double[][] getCoordsList() {
		return coordsList;
	}



	public Date[] getDates() {
		return dates;
	}


	/**
	 * Devuelve un array con los modos de instrumento solicitados.
	 * @return
	 */
	public InstMode[] getInstModeCodes() {
		Vector<InstMode> salida = new Vector<InstMode>();
		if(instModeCodes==null) return new InstMode[0];
		
		for(int i=0;i<instModeCodes.length; i++){
			String[] instModeStr = instModeCodes[i].split("_");
			if(instModeStr.length!=2) continue;
			InstMode instMode = new InstMode(instModeStr[0],instModeStr[1]);
			salida.add(instMode);
		}
		
		return (InstMode[])salida.toArray(new InstMode[0]);
	}

	
	public synchronized boolean isOnlyReduced() {
		return onlyReduced;
	}

	public String getProgId() {
		return progId;
	}
	
	public String getProdId() {
		return prodId;
	}

	public String getOblId(){
		return oblId;
	}

	public String getFilename() {
		return filename;
	}

	
	public void setLp(Integer Lp) {
		this.Lp = Lp;
	}

	public Integer getLp(){
		return Lp;
	}

	public void setOrderBy(Integer orderBy) {
		this.orderBy = orderBy;
	}

	public Integer getOrderBy(){
		return orderBy;
	}
	
	public Integer getRpp() {
		return rpp;
	}

	public void setRpp(Integer rpp) {
		this.rpp = rpp;
	}


	public Integer getPts() {
		return pts;
	}

	public void setPts(Integer pts) {
		this.pts = pts;
	}

	public String getOrigen() {
		return origen;
	}

	
	public String[] getErrors() {
		return errors;
	}
	
	public String[] getWarning() {
		return warning;
	}
	

	public boolean isAllInstruments() {
		return allInstruments;
	}

	public void setAllInstruments(boolean allInstruments) {
		this.allInstruments = allInstruments;
	}

	public String getBibcode() {
		return bibcode;
	}

	public void setBibcode(String bibcode) {
		this.bibcode = bibcode;
	}
	
	public String getStateant() {
		return stateant;
	}

	public void setStateant(String stateant) {
		this.stateant = stateant;
	}
	
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isValid(){
		boolean salida = ((getInstModeCodes()!=null && getInstModeCodes().length>0)
							||
						 (objectList!=null && objectList.length>0) 
						|| (prodList!=null && prodList.length>0)
						|| (coordsList!=null && coordsList.length>0) 
						|| (dates!=null && dates.length>0)
						|| progId!=null
						|| oblId!=null 
						|| prodId!=null
						|| id!=null) ;
		return( salida );
	}
	
	/**
	 * Añade un error al registro de errores del formulario.
	 * @param error
	 */
	private void addError(String error){
		String[] newErrors = new String[this.errors.length+1];
		
		for(int i=0; i<this.errors.length; i++){
			newErrors[i]=this.errors[i];
		}
		
		newErrors[newErrors.length-1]=error;
		
		this.errors=newErrors;
		
	}
	
	/**
	 * Añade un warning al registro de warning del formulario.
	 * @param error
	 */
	private void addWarning(String warning){
		String[] newWarning = new String[this.warning.length+1];
		
		for(int i=0; i<this.warning.length; i++){
			newWarning[i]=this.warning[i];
		}
		
		newWarning[newWarning.length-1]=warning;
		
		this.warning=newWarning;
		
	}

	/**
	 * Resolvemos si tiene nombre J
	 * @param nombre
	 */
	public String getCoordJ(String name){
		
			//Componemos las coordenadas
			String[] partes=null;
			String coor = null;
			
			if(name.contains("+")){
				partes=name.split("\\+");
				coor = partes[0].substring(1, 3)+":"+partes[0].substring(3, 5)+":"+partes[0].substring(5)+" +"+partes[1].substring(0, 2)+":"+partes[1].substring(2, 4)+":"+partes[1].substring(4);
				
			}else if(name.contains("-")){
				partes=name.trim().split("\\-");
				coor = partes[0].substring(1, 3)+":"+partes[0].substring(3, 5)+":"+partes[0].substring(5)+" -"+partes[1].substring(0, 2)+":"+partes[1].substring(2, 4)+":"+partes[1].substring(4);
				
			}
			
			return coor;
	}
	
}


