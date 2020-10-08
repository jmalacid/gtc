package svo.gtc.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * @author raul
 *
 */
public class Form {

	private		boolean		isMultipart = false;
	
	private		Map<String,String[]>			parameterMap = null;
	private		List<FileItem>		elementosMultipart = null;
	
	public Form(HttpServletRequest request) throws FormParameterException{
		procesaRequest(request);
	}

	private void procesaRequest(HttpServletRequest request) throws FormParameterException{
		
		
		
		//**************************************
		//      Check that we have a file upload request
		isMultipart = ServletFileUpload.isMultipartContent(request);
		 
		if(!isMultipart){
			parameterMap = request.getParameterMap();
		}else{
			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Parse the request
			try {
				elementosMultipart = upload.parseRequest(request);
			} catch (FileUploadException e) {
				e.printStackTrace();
				throw new FormParameterException("ERROR processing the request");
			}

		}
	}
		
	
	
	/**
	 * Devuelve los valores asociados a un campo del formulario en forma de array de strings. Si el campo tiene
	 * un único valor, se devolverá un array de un único elemento. Si el campo no existe o no tiene valores, se devuelve NULL
	 * @param paramName <p>Nombre del parámetro</p>
	 * @return <p>Array de Strings con los valores asociados al parámetro o NULL si el parámetro no se encuentra.</p>
	 */
	
	public String[] getParameterValues(String paramName){
		String[] salida = null;
		if(!isMultipart){
			if(parameterMap==null) return null;
			String[] valores = parameterMap.get(paramName);
			if(valores!=null && valores.length>0) salida = valores;
		}else{
			if(elementosMultipart==null) return null;
			
			Vector<String> valores = new Vector<String>();
			for(int i=0; i<elementosMultipart.size(); i++){
				FileItem elemento=elementosMultipart.get(i);
				if(elemento.isFormField() && elemento.getFieldName().equals(paramName)){
					valores.add(elemento.getString());
				}
			}
			
			if(valores.size()>0) salida = valores.toArray(new String[0]);
		}
		
		return salida;
	}
	
	
	/**
	 * Devuelve el fichero asociado a un campo del formulario de tipo "file".
	 * @param paramame <p>Nombre del campo de tipo fichero</p>
	 * @return Array de bytes con el contenido del fichero.
	 */
	public byte[] getFile(String paramName){
		byte[] salida = null;
		if(isMultipart){
			if(elementosMultipart==null) return null;
			
			for(int i=0; i<elementosMultipart.size(); i++){
				FileItem elemento=elementosMultipart.get(i);
				if(!elemento.isFormField() && elemento.getFieldName().equals(paramName)){
					salida=elemento.get();
				}
			}
		}
		
		return salida;
	}
	
	/**
	 * Devuelve el fichero asociado a un campo del formulario de tipo "file".
	 * @param paramame <p>Nombre del campo de tipo fichero</p>
	 * @return InputStream para leer el contenido del fichero.
	 * @throws IOException 
	 */
	public InputStream getFileInputStream(String paramName) throws IOException{
		InputStream salida = null;
		if(isMultipart){
			if(elementosMultipart==null) return null;
			
			for(int i=0; i<elementosMultipart.size(); i++){
				FileItem elemento=elementosMultipart.get(i);
				if(!elemento.isFormField() && elemento.getFieldName().equals(paramName)){
					salida=elemento.getInputStream();
				}
			}
		}
		
		return salida;
	}

	
	/**
	 * Devuelve el nombre de fichero asociado a un campo del formulario de tipo "file".
	 * @param paramame <p>Nombre del campo de tipo fichero</p>
	 * @return String con el nombre del fichero como lo da el usuario.
	 * @throws IOException 
	 */
	public String getFileName(String paramName) throws IOException{
		String salida = null;
		if(isMultipart){
			if(elementosMultipart==null) return null;
			
			for(int i=0; i<elementosMultipart.size(); i++){
				FileItem elemento=elementosMultipart.get(i);
				if(!elemento.isFormField() && elemento.getFieldName().equals(paramName)){
					salida=elemento.getName();
				}
			}
		}
		
		return salida;
	}

}


