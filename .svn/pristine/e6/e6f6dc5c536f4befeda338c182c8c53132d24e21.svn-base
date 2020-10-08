/*
 * @(#)NewUserAction.java    23/08/2012
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

package svo.gtc.struts.user.action;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;

import com.googlecode.sslplugin.annotation.Secured;
import com.opensymphony.xwork2.ActionSupport;

import svo.gtc.db.HibernateUtil;
import svo.gtc.db.usuario.UsuarioDb;
import svo.gtc.struts.interceptor.RestrictedAccess;
import svo.gtc.struts.user.model.User;

@Secured
public class ChangePasswordAction extends ActionSupport implements RestrictedAccess{
	static Logger logger = Logger.getLogger("svo.gtc");

	private static final long serialVersionUID = 1L;

	UsuarioDb user = null;
	User newUser = null;
	private String nextAction;
 
	@Override
	public String execute() throws Exception {

		if(compruebaPrivilegios()!=true) return ERROR;

		if(newUser==null){
			addActionError("No data provided.");
			return ERROR;
		}
		
		SessionFactory factory = HibernateUtil.getSessionFactory();

		try {
			factory.getCurrentSession().beginTransaction();
			UsuarioDb u = ((UsuarioDb)factory.getCurrentSession().get(UsuarioDb.class, new String(user.getUsrId().trim())));
			factory.getCurrentSession().getTransaction().commit();
			
			if(newUser==null) newUser=new User();
			newUser.setId(u.getUsrId());
			newUser.setName(u.getUsrName());
			newUser.setSurname(u.getUsrSurname());
			newUser.setEmail(u.getUsrEmail());

			
			if(newUser.getPassword().trim().length()<6){
				addActionError("Password must be at least 6 characters long.");
				return ERROR;
			}

			if(!newUser.getPassword().equals(newUser.getPasswordConf())){
				addActionError("Password and confirmation of password do not match.");
				return ERROR;
			}

			u.setUsrPasswd(UsuarioDb.generaMD5(newUser.getPassword()));
			
			factory.getCurrentSession().beginTransaction();
			factory.getCurrentSession().update(u);
			factory.getCurrentSession().getTransaction().commit();
		} catch (HibernateException e) {
			factory.getCurrentSession().getTransaction().rollback();
			e.printStackTrace();
			addActionError("Undefined error. Please, try again later.");
			return ERROR;
		}
		
		addActionMessage("Password successfuly updated.");
		return SUCCESS;
	}
	
	
	private boolean compruebaPrivilegios(){
		UsuarioDb usuarioActivo = null;

		boolean salida = false;
		
		SessionFactory factory = HibernateUtil.getSessionFactory();
		factory.getCurrentSession().beginTransaction();
		usuarioActivo = (UsuarioDb)factory.getCurrentSession().get(UsuarioDb.class, new String(user.getUsrId()));
		if(usuarioActivo==null){
			addActionError("You must log in to perform this action.");
		}else{
			salida=true;
		}
		factory.getCurrentSession().getTransaction().commit();
		
		return salida;
	}
	
	@Override
	public String input() throws Exception {

		if(compruebaPrivilegios()!=true) return ERROR;

		SessionFactory factory = HibernateUtil.getSessionFactory();

		try {
			logger.debug("Usuario actual: "+user.getUsrId());
			
			factory.getCurrentSession().beginTransaction();
			UsuarioDb u = ((UsuarioDb)factory.getCurrentSession().get(UsuarioDb.class, new String(user.getUsrId().trim())));
			factory.getCurrentSession().getTransaction().commit();

			logger.debug("Usuario encontrado: "+u.getUsrId());

			if(newUser==null) newUser=new User();
			newUser.setId(u.getUsrId());
			newUser.setName(u.getUsrName());
			newUser.setSurname(u.getUsrSurname());
			newUser.setEmail(u.getUsrEmail());
			
		} catch (HibernateException e) {
			factory.getCurrentSession().getTransaction().rollback();
			e.printStackTrace();
			addActionError("Undefined error. Please, try again later.");
			return ERROR;
		}

		return INPUT;
	}

	public String getNextAction() {
		return nextAction;
	}

	public void setUser(UsuarioDb user) {
		this.user=user;
	}


	public User getNewUser() {
		return newUser;
	}


	public void setNewUser(User newUser) {
		this.newUser = newUser;
	}
	
	

}
