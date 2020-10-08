/*
 * @(#)InterLogin.java    28/08/2012
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

package svo.gtc.struts.interceptor;

import java.util.Map;

import org.apache.log4j.Logger;

import svo.gtc.db.usuario.UsuarioDb;
import svo.gtc.web.ContenedorSesion;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class InterLogin implements Interceptor{
	static Logger logger = Logger.getLogger("svo.gtc");
 

	private static final long serialVersionUID = 1L;

	public void destroy() {}
	public void init() {}

	/**
	 * Intercepta la acción si esta requiere autentificación (implementa RestrictedAccess).
	 * Si el usuario no está registrado redirige a login, guardando la acción origen en la sesion.
	 * En caso contrario, continua a la acción.
	 */
	public String intercept(ActionInvocation actionInvocation) throws Exception {
	      Map<String,Object> session = actionInvocation.getInvocationContext().getSession();
	      ContenedorSesion contenedorSesion = (ContenedorSesion) session.get("contenedorSesion");
	      if(contenedorSesion==null) contenedorSesion=new ContenedorSesion();
	      UsuarioDb user = contenedorSesion.getUser();
	      
	      
	      Action action = (Action) actionInvocation.getAction();
	      if (action instanceof RestrictedAccess){
	    	  if(user == null) {
	    		  contenedorSesion.setOrigen(actionInvocation.getInvocationContext().getName());
	    		  session.put("contenedorSesion", contenedorSesion);
	    		  actionInvocation.getInvocationContext().setSession(session);
	    		  return Action.LOGIN;
	    	  } else{
	    		  logger.debug("UsuarioRegistrado");
	    		  if (action instanceof RestrictedAccess) {
	    			  logger.debug("Guardando usuario para clase RestrictedAccess");
	    			  ((RestrictedAccess) action).setUser(user);
	    		  }
	    		  return actionInvocation.invoke();
	    	  }
	      }else{
	    	  return actionInvocation.invoke();
	      }
	}

}
