package svo.gtc.web;

	import java.util.List;
	import java.util.Vector;

	import javax.servlet.http.HttpServletRequest;

	import org.apache.commons.fileupload.FileItem;
	import org.apache.commons.fileupload.FileItemFactory;
	import org.apache.commons.fileupload.FileUploadException;
	import org.apache.commons.fileupload.disk.DiskFileItemFactory;
	import org.apache.commons.fileupload.servlet.ServletFileUpload;
	import org.apache.log4j.Logger;

	import utiles.Coordenadas;

	/**
	 * @author J. Manuel Alacid
	 *
	 */
	public class FormICC {

		static Logger logger = Logger.getLogger("svo.gtc");
		
		private 	Double[][]	coordsList = null;
		private		String		filename = null;

		private		String[]	errors = new String[0];
		
		
		public FormICC(){
		}

		public FormICC(HttpServletRequest request) throws FormParameterException{
			procesaRequest(request);
		}

		

		public void procesaRequest(HttpServletRequest request) throws FormParameterException{
			
			//**************************************
			//      Check that we have a file upload request
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			logger.debug("isMultipart="+isMultipart);

			String obj_list = request.getParameter("obj_list"); 
			
			String filename_str = request.getParameter("filename");

			byte[] ficheroObjList = null;
			
			if(isMultipart){

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

						if(value.trim().length()>0){
							if( name.equalsIgnoreCase("obj_list")){
								obj_list=value.trim();
								logger.debug("obj_list="+obj_list);
							}else if(name.equals("filename")){
								filename_str=value.trim();
							}
						}

					}else{
						/// Es el fichero
						if(elementos[i].getName()!=null && elementos[i].getName().length()>0 && elementos[i].getSize()>0){
							ficheroObjList=elementos[i].get();
						}
					}
				}
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
					if(Coordenadas.checkCoordinatesFormat(lineas[i].trim())){
						Double[] coords = Coordenadas.coordsInDeg(lineas[i].trim());
						if(coords!=null){
							auxCoordsList.add(coords);
						}
					}
				}
				
				if(auxCoordsList.size()>0){
					coordsList = new Double[auxCoordsList.size()][2];
					for(int i=0; i<coordsList.length; i++){
						coordsList[i][0] = ((Double[])auxCoordsList.elementAt(i))[0];
						coordsList[i][1] = ((Double[])auxCoordsList.elementAt(i))[1];
					}
				}
				
			}
			
			// Filename
			if(filename_str!=null && filename_str.trim().length()>0){
				try{
					filename = filename_str.trim();
				}catch(NumberFormatException e){}
			}
			
			
		}
		
		public Double[][] getCoordsList() {
			return coordsList;
		}

		public String getFilename() {
			return filename;
		}
		
		public String[] getErrors() {
			return errors;
		}
				
		
		public boolean isValid(){
			boolean salida = (coordsList!=null && coordsList.length>0) ;
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


		
	}

