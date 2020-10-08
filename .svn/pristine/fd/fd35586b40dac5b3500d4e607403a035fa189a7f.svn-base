/*
 * @(#)ReducedAction.java    23/08/2012
 *
 *
 * Raúl Gutiérrez Sánchez. (raul@laeff.inta.es)	
 * LAEFF: 	Laboratorio de Astrofísica Espacial y Física Fundamental.
 *        	INTA
 *			Villafranca del Castillo Satellite Tracking Station
 *			Villafranca del Castillo, E-28691 Villanueva de la Cañada
 *			Madrid - España
 *			
 * Phone:  34 91-8131260
 * Fax..:  34 91-8131160   
 * -----------------------------------------------------------------------
 * 
 *	ESTE  PROGRAMA  ES  DE  USO EXCLUSIVO.  ES  PROPIEDAD  DE  SU AUTOR  Raúl 
 *  Gutiérrez Sánchez  Y SE PRESENTA "COMO ESTA" SIN NINGUN TIPO DE GARANTIAS
 *  DENTRO Y FUERA  DE LA FINALIDAD PARA  LA QUE EL  AUTOR LO  HIZO. EL AUTOR 
 *  PERMITE  SU USO SIEMPRE QUE  SE LE REFERENCIE. SE PERMITE LA MODIFICACION
 *  DE SU  FUENTE SIEMPRE QUE SE  HAGA REFERENCIA A SU AUTORIA Y JUNTO A ELLA
 *  SE  ADJUNTE LA IDENTIFICACION DEL  AUTOR DE LAS NUEVAS MODIFICACIONES. EL
 *  AUTOR  NUNCA SERA RESPONSABLE DEL USO DE ESTE  PROGRAMA NI DE LOS EFECTOS
 *  QUE ESTE PROGRAMA PUDIEDA CAUSAR EN DETERIOROS, ALTERACIONES Y/O PERDIDAS 
 *  DE INFORMACION.
 *	
 */

package svo.gtc.struts.reduced.action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionSupport;

import svo.gtc.db.DriverBD;
import svo.gtc.db.colecciondatos.ColeccionDatosAccess;
import svo.gtc.db.colecciondatos.ColeccionDatosDb;
import svo.gtc.db.usuario.UsuarioDb;
import svo.gtc.struts.interceptor.RestrictedAccess;
import svo.gtc.struts.reduced.model.InsertReduced;
import svo.gtc.struts.reduced.model.NewCollection;
 

public class NewCollectionAction extends ActionSupport implements RestrictedAccess{
	static Logger logger = Logger.getLogger("svo.gtc");

	private static final long serialVersionUID = 1L;

	private UsuarioDb user;
	private InsertReduced insertReducedBean;
	private NewCollection newCollectionBean;
	
	
	@Override 
	public String execute() throws Exception {

		if(user==null){
			logger.debug("Usuario no registrado");
			addActionError("You are not authorized to perform this operation.");
			return ERROR;
		}

		validateNewCollectionForm();
		
		if(!getActionErrors().isEmpty()){
			return ERROR;
		}
		
		
		// Insertamos la nueva colección
		
		//======================================================= 
		//Conexion con la Base de Datos                       
		//=======================================================

		Connection conex= null;

		DriverBD drvBd = new DriverBD();

		try {
			conex = drvBd.bdConexion();
		} catch (SQLException errconexion) {
			errconexion.printStackTrace();
		}

		ColeccionDatosAccess colAccess = new ColeccionDatosAccess(conex);
		
		if(colAccess.countByName(user.getUsrId(),newCollectionBean.getName().trim())==0){
			
			ColeccionDatosDb colDatosDb = new ColeccionDatosDb();
			colDatosDb.setUsrId(user.getUsrId());
			colDatosDb.setColName(newCollectionBean.getName());
			colDatosDb.setColBibcode(newCollectionBean.getBibcode().trim());
			colDatosDb.setColDataType(newCollectionBean.getDataType());
			
			String aux = "";
			String coma="";
			
			for(String redType: newCollectionBean.getRedType() ){
				aux+=coma+redType;
				coma=", ";
			}
			if(aux.trim().length()>0){
				colDatosDb.setColRedType(aux);
			}
			
			colDatosDb.setColDesc(newCollectionBean.getDesc());
				
			colAccess.insert(colDatosDb);
		}else{
			addActionError("Collection \""+newCollectionBean.getName()+"\" already exists.");
			return ERROR;
		}

		conex.close();
		
		return SUCCESS;
	}

	@Override
	public String input() throws Exception {
		
		if(user==null){
			logger.debug("Usuario no registrado");
			addActionError("You are not authorized to perform this operation.");
			return ERROR;
		}
		
		return INPUT;
	}

	public void setUser(UsuarioDb user) {
		this.user=user;
	}
	
	
	
	
	public InsertReduced getInsertReducedBean() {
		return insertReducedBean;
	}

	public void setInsertReducedBean(InsertReduced insertReducedBean) {
		this.insertReducedBean = insertReducedBean;
	}

	public NewCollection getNewCollectionBean() {
		return newCollectionBean;
	}

	public void setNewCollectionBean(NewCollection newCollectionBean) {
		this.newCollectionBean = newCollectionBean;
	}

	
	/**
	 * Genera la lista de valores checkbox predefinidos para los tipos de reducción posibles de imágenes.
	 * @return
	 * @throws Exception
	 **/
	public ArrayList<String> getRedTypesImg() throws Exception{
		ArrayList<String> salida = new ArrayList<String>();
		salida.add("Bias subtraction");
		salida.add("Flatfield correction");
		salida.add("Dark current subtraction");
		return salida;
	}	

	/**
	 * Genera la lista de valores checkbox predefinidos para los tipos de reducción posibles de espectros.
	 * @return
	 * @throws Exception
	 **/
	public ArrayList<String> getRedTypesSpec() throws Exception{
		ArrayList<String> salida = new ArrayList<String>();
		salida.add("Bias subtraction");
		salida.add("Flatfield correction");
		salida.add("Dark current subtraction");
		salida.add("Wavelenght calibration");
		salida.add("Flux calibration");
		return salida;
	}	
	
	
	/**
	 * Comprueba que el formulario se ha rellenado correctamente.
	 * @return
	 */
	private void validateNewCollectionForm(){
		if(newCollectionBean.getName().length()==0) addActionError("Collection name is compulsory");
		
		if(newCollectionBean.getBibcode().length()==0){
			addActionError("Publication bibcode is compulsory");
		}else if(!newCollectionBean.getBibcode().trim().matches("[0-9]{4}[A-Za-z].{12}[0-9][A-Z]")){
			addActionError("Incorrect bibcode: "+newCollectionBean.getBibcode());
		}
		
		
		if( (newCollectionBean.getRedTypeImg().length>0  || newCollectionBean.getRedTypeImgOther().length()>0) 
			   && (newCollectionBean.getRedTypeSpec().length>0  || newCollectionBean.getRedTypeSpecOther().length()>0) ){
			addActionError("It is not possible to specify reduction type for both image and spectrum data types.");
		}
		
		if( newCollectionBean.getRedTypeImg().length==0  && newCollectionBean.getRedTypeImgOther().trim().length()==0 
			   && newCollectionBean.getRedTypeSpec().length==0  && newCollectionBean.getRedTypeSpecOther().trim().length()==0 ){
			addActionError("No reduction type specified.");
		}else if(newCollectionBean.getRedTypeImg().length>0  && newCollectionBean.getRedTypeImgOther().length()>0){
			addActionError("It is not possible to select both a predefined and a free text reduction type.");
		}else if(newCollectionBean.getRedTypeSpec().length>0  && newCollectionBean.getRedTypeSpecOther().length()>0){
			addActionError("It is not possible to select both a predefined and a free text reduction type.");
		}
	}
	
	
	
}
