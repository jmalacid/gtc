package svo.gtc.priv.objetos;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import svo.gtc.web.FormParameterException;


public class FormProgram {
	
	 private     String[]	progList = null;
	
	
	public FormProgram(HttpServletRequest request) throws Exception {

		
		byte[] ficheroObjList = null;
		
		if (ServletFileUpload.isMultipartContent(request)) {
			
			
			//Vector<String> instCodeVcr = new Vector<String>();

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
					
				}else{
					/// Es el fichero
					if(elementos[i].getName()!=null && elementos[i].getName().length()>0 && elementos[i].getSize()>0){
						ficheroObjList=elementos[i].get();
					}
				}
			}
		
		}
		
		
		// Lista de objetos
				if(ficheroObjList!=null && ficheroObjList.length>0 ){
					
					//String[] lineas = new String[0];
					
					if(ficheroObjList!=null && ficheroObjList.length>0){
						progList = new String(ficheroObjList).split("[\\n\\r]");
					}
					
					
					
				}
		
		
	}


	public String[] getProgList() {
		return progList;
	}


	public void setProgList(String[] progList) {
		this.progList = progList;
	}

}
