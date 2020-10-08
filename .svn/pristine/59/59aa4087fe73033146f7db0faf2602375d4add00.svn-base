/*
 * @(#)LoginAction.java    23/08/2012
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

package svo.gtc.struts.login.action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;

import com.googlecode.sslplugin.annotation.Secured;
import com.opensymphony.xwork2.ActionSupport;

import svo.gtc.db.DriverBD;
import svo.gtc.db.usuario.UsuarioAccess;
import svo.gtc.db.usuario.UsuarioDb;
import svo.gtc.struts.login.model.Login;
import svo.gtc.web.ContenedorSesion;
import svo.varios.utiles.seguridad.Encriptado;

@Secured
public class LoginAction extends ActionSupport implements SessionAware{
	static Logger logger = Logger.getLogger("svo.gtc");

	private static final long serialVersionUID = 1L; 
	private Login loginBean;
	private Map<String,Object> session;
	private String nextAction;

	@Override
	public String execute() throws Exception {

		//======================================================= 
		//  Conexion con la Base de Datos                       
		//=======================================================

		Connection conex= null;
		
		

		DriverBD drvBd = new DriverBD();

		try {
			conex = drvBd.bdConexion();
		} 
		catch (SQLException errconexion) {
			errconexion.printStackTrace();
		}

		UsuarioAccess usuarioAccess = new UsuarioAccess(conex);

		UsuarioDb usuario = usuarioAccess.selectByIdPw(loginBean.getUser(),Encriptado.md5(loginBean.getPassword()));


		//call Service class to store personBean's state in database

		conex.close();

		/// Actualizamos el usuario de la sesion.
		ContenedorSesion contenedorSesion = (ContenedorSesion) session.get("contenedorSesion");
		if(contenedorSesion==null) contenedorSesion = new ContenedorSesion();
		contenedorSesion.setUser(usuario);
		this.nextAction=contenedorSesion.getOrigen();
		logger.debug("Next action: "+this.nextAction);
		
		if(usuario!=null){
			contenedorSesion.setOrigen(null);
			session.put("contenedorSesion", contenedorSesion);
			
			if(this.nextAction!=null){
				return "next";
			}
			return SUCCESS;
		}
		else{
			addActionError("Invalid username or password. Please, try again.");
			return ERROR;
		}
	}
	
	@Override
	public String input() throws Exception {
		return INPUT;
	}


	public Login getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(Login loginBean) {
		this.loginBean = loginBean;
	}

	
	
	public Map<String,Object> getSession() {
		return session;
	}

	public void setSession(Map<String,Object> session) {
		this.session = session;
	}

	public String getNextAction() {
		return nextAction;
	}
	
	

}
