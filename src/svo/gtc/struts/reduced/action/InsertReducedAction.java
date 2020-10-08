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
 
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import svo.gtc.db.DriverBD;
import svo.gtc.db.colecciondatos.ColeccionDatosAccess;
import svo.gtc.db.colecciondatos.ColeccionDatosDb;
import svo.gtc.db.prodat.ProdDatosDb;
import svo.gtc.db.prodred.PredProdAccess;
import svo.gtc.db.usuario.UsuarioDb;
import svo.gtc.proddat.ProdDatosRed;
import svo.gtc.struts.interceptor.RestrictedAccess;
import svo.gtc.struts.reduced.model.InsertReduced;
import svo.gtc.struts.reduced.model.PreviewReduced;
import svo.gtc.tools.Email;
import svo.gtc.web.reduced.RedProdStatus;
import svo.gtc.web.reduced.RedUploadManager;

public class InsertReducedAction extends ActionSupport implements RestrictedAccess, SessionAware{
	static Logger logger = Logger.getLogger("svo.gtc");

	private static final long serialVersionUID = 1L;
	
	//private static String[] destinatariosInforme = new String[]{"jmalacid@cab.inta-csic.es"};
	private static String[] destinatariosInforme = new String[]{"gtc-support@cab.inta-csic.es"};

	private UsuarioDb user;
	private Map<String,Object> session;
	private InsertReduced insertReducedBean;
	private PreviewReduced previewReduced = new PreviewReduced(); 
	
	@Override
	public String execute() throws Exception {

		if(insertReducedBean==null){
			logger.debug("No se han especificado valores en el formulario de entrada.");
			addActionError("Please, fill the form.");
			return ERROR;
		}
		
		if(user==null){
			logger.debug("Usuario no registrado");
			addActionError("You are not authorized to perform this operation.");
			return ERROR;
		}
		
		if(insertReducedBean.getDataCollection()==null){
			logger.debug("No se ha especificado colección de datos.");
			addActionError("No data collection specified.");
			return ERROR;
		}
		
		logger.debug("Content Type: "+insertReducedBean.getUploadContentType());
		
		try{
			RedUploadManager redUploadManager = new RedUploadManager(user.getUsrId(),insertReducedBean);
			int ingested = ingestGreen(redUploadManager);
			int notIngested = redUploadManager.getProductList().length-ingested;
			
			previewReduced.setIngested(ingested);
			previewReduced.setNotIngested(notIngested);
			
			if(ingested == 1){
				addActionMessage(ingested+" product has been ingested.");
			}else if(ingested>0){
				addActionMessage(ingested+" products have been ingested.");
			}

			if(notIngested == 1){
				addActionError(notIngested+" product has not been ingested due to the next reason.");
			}else if(notIngested>0){
				addActionError(notIngested+" products have not been ingested due to the next reasons.");
			}
			
			this.sendEmail(previewReduced, redUploadManager);
			
		}catch(Exception e){
			e.printStackTrace();
			addActionError(e.getMessage());
			return ERROR;
		}
		
		return SUCCESS;

	}

	
	/**
	 * Realiza la ingestión de los ficheros OK y devuelve el numero de ficheros ingeridos.
	 * @param redUploadManager
	 * @return
	 */
	public int ingestGreen(RedUploadManager redUploadManager) {
		int ingested=0;
		
		if(redUploadManager==null){
			logger.debug("No hay lista de productos para subir.");
			return 0;
		}
		
		// Procedemos a la ingestión
		//======================================================= 
		//Conexion con la Base de Datos                       
		//=======================================================
		DriverBD drvBd = new DriverBD();
		Connection conex = null;
		try {
			conex = drvBd.bdConexion();
		} catch (SQLException errconexion) {
			errconexion.printStackTrace();
		}

		RedProdStatus[] listaStatus = redUploadManager.getProductList();
		for(RedProdStatus c: listaStatus){
			if(c.getStatus().equals(RedProdStatus.OK)){
				try {
					//Refrescamos la conexión
					c.getProduct().setCon(conex);
					c.getProduct().ingest(user.getUsrId(),redUploadManager.getCollId());
					c.setDesc("Product successfully ingested.");
					ingested++;
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
					c.setStatus(RedProdStatus.ERROR);
					c.setDesc("Unknown error ingesting product.");
				}
			}else if(c.getStatus().equals(RedProdStatus.WARNING)){

				try {
					
					c.getProduct().copyTemp(user.getUsrId(),redUploadManager.getCollId());
					//c.setDesc("Product ingested with warning.");
					
				} catch (Exception e) {
					logger.error(e.getMessage());
					c.setStatus(RedProdStatus.ERROR);
					c.setDesc("Unknown error ingesting product.");
				}
			
			}
		}

		this.previewReduced.setProdStatus(new ArrayList<RedProdStatus>(Arrays.asList(listaStatus)));
		
		try {
			conex.close();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return ingested;
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


	public PreviewReduced getPreviewReduced() {
		return previewReduced;
	}

	public void setPreviewReduced(PreviewReduced previewReduced) {
		this.previewReduced = previewReduced;
	}

	/**
	 * Genera la lista de colecciones de datos del usuario. Para ser usado
	 * en la generación del formulario.
	 * @return
	 * @throws Exception
	 **/
	public ArrayList<DataCollection> getDataCollections() throws Exception{
		ArrayList<DataCollection> salida = new ArrayList<DataCollection>();
		
		
		//======================================================= 
		//Conexion con la Base de Datos                       
		//=======================================================
		DriverBD drvBd = new DriverBD();
		Connection conex = null;
		try {
			conex = drvBd.bdConexion();
		} catch (SQLException errconexion) {
			errconexion.printStackTrace();
		}

		
		ColeccionDatosAccess colAccess= new ColeccionDatosAccess(conex);
		ColeccionDatosDb[] cols = colAccess.selectByUsr(user.getUsrId());

		conex.close();

		for(ColeccionDatosDb col : cols){
			salida.add(new DataCollection(col.getColId(), col.getColName()));
		}
		//return (DataCollection[])salida.toArray();
		return salida;

	}	

	public class DataCollection{
		private int id;
		private String name;
		
		DataCollection(int id, String name){
			this.id=id;
			this.name=name;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		
	}
	
	
	public Map<String,Object> getSession() {
		return session;
	}

	public void setSession(Map<String,Object> session) {
		this.session = session;
	}
	
	
	/**
	 * Envia un correo electrónico con un informe sobre los ficheros reducidos que han fallado
	 * @param prev
	 */
	private void sendEmail(PreviewReduced prev, RedUploadManager redUploadManager){
		
		//======================================================= 
		//Conexion con la Base de Datos                       
		//=======================================================
		DriverBD drvBd = new DriverBD();
		Connection conex = null;
		try {
			conex = drvBd.bdConexion();
		} catch (SQLException errconexion) {
			errconexion.printStackTrace();
		}

		String colName = ""+redUploadManager.getCollId();
		try {
			ColeccionDatosAccess colDatosAccess = new ColeccionDatosAccess(conex);
			ColeccionDatosDb colDb=colDatosAccess.selectById(redUploadManager.getUsrId(), redUploadManager.getCollId());
			if(colDb!=null){
				colName=colDb.getColName();
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		
		String subject = "[GTC] Reduced data ingestion performed.";
		String body = "";
		
		body+="INFORMACION DE USUARIO:\n";
		body+="-----------------------\n";
		body+="Usuario: "+user.getUsrId()+" ("+user.getUsrName()+" "+user.getUsrSurname()+")\n";
		body+="eMail:   "+user.getUsrEmail()+"\n";

		body+="\n";

		body+="COLECCIÓN DE DATOS: \n";
		body+="-------------------------------- \n";
		body+=colName+"\n";
		body+="\n";

		body+="DIRECTORIO DE SUBIDA EN CORNELL: \n";
		body+="-------------------------------- \n";
		body+=redUploadManager.getCarpetaTemp().getAbsolutePath()+"\n";
		body+="\n";

		int ingested=this.previewReduced.getIngested();
		int notIngested=this.previewReduced.getNotIngested();
		
		body+="Productos insertados en el archivo correctamente: "+ingested+"\n";
		body+="Productos no insertados en el archivo:            "+notIngested+"\n";

		body+="\n";

		if(ingested>0){
			body+="INFORME DE PRODUCTOS INSERTADOS:\n";
			body+="----------------------------------\n";
			try {
				PredProdAccess predProdAccess = new PredProdAccess(conex);
				for(RedProdStatus stat: this.previewReduced.getProdStatus()){
					if(stat.getStatus().equalsIgnoreCase("OK")){
						ProdDatosRed pred = stat.getProduct();
						ProdDatosDb[] prodsRaw = predProdAccess.selectProdDatosByProdRedId(pred.getPredId());
						for(ProdDatosDb raw:prodsRaw){
							body+=stat.getFileName()+":\t"+stat.getStatus()+"\t "+raw.getProdId()+"\t"+raw.getProgId()+"\t"+raw.getOblId()+"\n";
						}
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		body+="\n";
		}

		if(notIngested>0){
			body+="INFORME DE PRODUCTOS NO INSERTADOS:\n";
			body+="----------------------------------\n";
			for(RedProdStatus stat: this.previewReduced.getProdStatus()){
				if(!stat.getStatus().equalsIgnoreCase("OK")){
					body+=stat.getFileName()+"\t"+stat.getStatus()+"\t"+stat.getDesc()+"\n";
				}
			}
		}

		try{
			Email.sendMail(subject, body, destinatariosInforme);
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
		
		try{
			conex.close();
		}catch(SQLException e){}
			
	}
	
}