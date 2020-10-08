/*
 * @(#)Programa.java    Feb 17, 2011
 *
 *
 * Ra�l Guti�rrez S�nchez. (raul@cab.inta-csic.es)	
 * LAEFF: 	Laboratorio de Astrof�sica Espacial y F�sica Fundamental.
 *        	INTA
 *			Villafranca del Castillo Satellite Tracking Station
 *			Villafranca del Castillo, E-28691 Villanueva de la Ca�ada
 *			Madrid - Espa�a
 *			
 * Phone:  34 91-8131260
 * Fax..:  34 91-8131160   
 * -----------------------------------------------------------------------
 * 
 *	ESTE  PROGRAMA  ES  DE  USO EXCLUSIVO.  ES  PROPIEDAD  DE  SU AUTOR  Ra�l 
 *  Guti�rrez S�nchez  Y SE PRESENTA "COMO ESTA" SIN NINGUN TIPO DE GARANTIAS
 *  DENTRO Y FUERA  DE LA FINALIDAD PARA  LA QUE EL  AUTOR LO  HIZO. EL AUTOR 
 *  PERMITE  SU USO SIEMPRE QUE  SE LE REFERENCIE. SE PERMITE LA MODIFICACION
 *  DE SU  FUENTE SIEMPRE QUE SE  HAGA REFERENCIA A SU AUTORIA Y JUNTO A ELLA
 *  SE  ADJUNTE LA IDENTIFICACION DEL  AUTOR DE LAS NUEVAS MODIFICACIONES. EL
 *  AUTOR  NUNCA SERA RESPONSABLE DEL USO DE ESTE  PROGRAMA NI DE LOS EFECTOS
 *  QUE ESTE PROGRAMA PUDIEDA CAUSAR EN DETERIOROS, ALTERACIONES Y/O PERDIDAS 
 *  DE INFORMACION.
 *	
 */

package svo.gtc.db.usuario;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import svo.gtc.db.HibernateUtil;
import svo.varios.utiles.seguridad.Encriptado;

@Entity
@Table(name="usuario")
public class UsuarioDb {
	
	@Id
	@Column(name="usr_id")
	private String	usrId 		= null;
	
	@Column(name="usr_name")
	private String 	usrName 	= null;
	
	@Column(name="usr_surname")
	private String 	usrSurname 	= null;
	
	@Column(name="usr_email")
	private String 	usrEmail	= null;
	
	@Column(name="usr_passwd")
	private String 	usrPasswd	= null;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "usuario_grupo", joinColumns = { @JoinColumn(name = "usr_id") }, inverseJoinColumns = { @JoinColumn(name = "grp_id") })
	private Set<GrupoDb> grupos = new HashSet<GrupoDb>(0);
	
	
	public UsuarioDb(){}
	
	public UsuarioDb(ResultSet resset) throws SQLException{
		this.usrId 			= resset.getString("usr_id");
		this.usrName		= resset.getString("usr_name");
		this.usrSurname 	= resset.getString("usr_surname");
		this.usrEmail		= resset.getString("usr_email");
		this.usrPasswd		= resset.getString("usr_passwd");
	}

	public synchronized String getUsrId() {
		return usrId;
	}

	public synchronized void setUsrId(String usrId) {
		this.usrId = usrId;
	}

	public synchronized String getUsrName() {
		return usrName;
	}

	public synchronized void setUsrName(String usrName) {
		this.usrName = usrName;
	}

	public synchronized String getUsrSurname() {
		return usrSurname;
	}

	public synchronized void setUsrSurname(String usrSurname) {
		this.usrSurname = usrSurname;
	}

	public synchronized String getUsrEmail() {
		return usrEmail;
	}

	public synchronized void setUsrEmail(String usrEmail) {
		this.usrEmail = usrEmail;
	}

	public synchronized String getUsrPasswd() {
		return usrPasswd;
	}

	public synchronized void setUsrPasswd(String usrPasswd) {
		this.usrPasswd = usrPasswd;
	}

	

	public Set<GrupoDb> getGrupos() {
		return grupos;
	}

	public void setGrupos(Set<GrupoDb> grupos) {
		this.grupos = grupos;
	}

	/**
	 * Genera el hash MD5 de una contraseña proporcionada.
	 * 
	 * @param password
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String generaMD5(String password){
		try {
			return Encriptado.md5(password);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	

	/**
	 * 
	 * Comprueba si el usuario pertenece al grupo "ADM"
	 * 
	 * @return
	 */
	public boolean isAdm(){
		for(GrupoDb g: this.grupos){
			if(g.getGrpId().equalsIgnoreCase("ADM")) return true;
		}
		return false;
	}
	
	
	public static void main(String[] args) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			
			UsuarioDb user = (UsuarioDb)session.get(UsuarioDb.class, new String("raul"));

//			Set<GrupoDb> grupos = new HashSet<GrupoDb>();
//			grupos.add(new GrupoDb("A","aaaaa",null));
//
//			UsuarioDb user = new UsuarioDb();
//			user.setUsrId("A");
//			user.setUsrName("Aa");
//			user.setUsrSurname("Aaaaaaaa");
//			user.setUsrEmail("aajdfñakjd fñ");
//			user.setUsrPasswd("Aaldkfj añdlksf f");
//			user.setGrupos(grupos);
//
//
//			session.delete(user);
			transaction.commit();
			
			System.out.println(user.getUsrSurname());
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}

	}

}
