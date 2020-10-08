/*
 * @(#)RedUploadManager.java    13/06/2012
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

package svo.gtc.web.reduced;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import svo.gtc.db.DriverBD;
import svo.gtc.db.basepath.BasepathAccess;
import svo.gtc.db.basepath.BasepathDb;
import svo.gtc.db.prodred.ProdRedAccess;
import svo.gtc.proddat.GtcFileException;
import svo.gtc.proddat.ProdDatosRed;
import svo.gtc.struts.reduced.model.InsertReduced;

public class RedUploadManager {
	static Logger logger = Logger.getLogger("svo.gtc");

	private String			usrId  	= null;
	private Integer			collId 	= null;
	private RedProdStatus[] productList = new RedProdStatus[0];
	
	private File			carpetaTemp = null;
	
	public RedUploadManager(String usrId, InsertReduced insertReducedBean) throws Exception{
		
		this.usrId=usrId;
		this.collId=insertReducedBean.getDataCollection();

		InputStream streamFileUpload = null;
		String fileName = null;
		String contentType = null; 
		
		if(insertReducedBean.getUpload()!=null && insertReducedBean.getUploadFileName()!=null){
			try {
				streamFileUpload = new FileInputStream(insertReducedBean.getUpload());
				fileName = insertReducedBean.getUploadFileName();
				contentType = insertReducedBean.getUploadContentType();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if(streamFileUpload==null) throw new Exception("No file provided.");
		
		if(!contentType.equalsIgnoreCase("application/zip") 
		//		&& !contentType.toUpperCase().endsWith("FITS") && !contentType.toUpperCase().endsWith("FIT")
				&& !(fileName.toUpperCase().endsWith("FITS") || fileName.toUpperCase().endsWith("FIT") ||
						fileName.toUpperCase().endsWith("FIT.GZ") || fileName.toUpperCase().endsWith("FITS.GZ") || fileName.toUpperCase().endsWith(".ZIP") || 
						fileName.toUpperCase().endsWith("TAR.GZ") || fileName.toUpperCase().endsWith(".TAR"))){
			throw new Exception("Not recognized file type: not a .zip, .tar.gz, .fits or .fits.gz file.");
		}

		
		
		//======================================================= 
		//Conexion con la Base de Datos                       
		//=======================================================
		DriverBD drvBd = new DriverBD();
		Connection conex = null;
		File temp = null;
		try {
			conex = drvBd.bdConexion();
			
			BasepathAccess bpathAccess = new BasepathAccess(conex);
			BasepathDb bpathDb = bpathAccess.selectBpathById(2000);
			temp = new File(bpathDb.getBpathPath());
			
		} catch (SQLException errconexion) {
			errconexion.printStackTrace();
		}

		
		
		eliminaCarpetasAntiguas(temp);
		
		try {
			this.carpetaTemp  = generaCarpetaDescomprimida(temp,streamFileUpload, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		productList = generaStatus(this.carpetaTemp);
	}
	
	
	/** 
	 * Genera los objetos status para un directorio dado.
	 * @param carpetaTemp
	 * @return
	 */
	private RedProdStatus[] generaStatus(File carpetaTemp){
		Vector<RedProdStatus> aux = new Vector<RedProdStatus>();
		
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

		
		for(File c: carpetaTemp.listFiles()){
			if(c.isFile() && (c.getName().toUpperCase().endsWith("FITS") || c.getName().toUpperCase().endsWith("FITS.GZ")
					|| c.getName().toUpperCase().endsWith("FIT") || c.getName().toUpperCase().endsWith("FIT.GZ")) && 
					!(c.getName().startsWith("._"))){
				RedProdStatus status = new RedProdStatus(c);
				status.setStatus(RedProdStatus.ERROR);
				
				ProdDatosRed prod = null;
				try {
					prod = new ProdDatosRed(c,conex);
					status.setProduct(prod);
					prod.test();
					logger.debug("Errores producto="+prod.getErrores());
				} catch (GtcFileException e) {
					
					//Para hacer que aparezca el warning
					if(e.getMessage().startsWith("W-REDUCED")){
						status.setStatus(RedProdStatus.WARNING);
						status.setDesc(e.getMessage().replaceAll("W-[^-]+-[0-9]{4}\\:\\s", ""));
						aux.add(status);
						continue;
					}else{
						status.setStatus(RedProdStatus.ERROR);
						status.setDesc(e.getMessage().replaceAll("E-[^-]+-[0-9]{4}\\:\\s", ""));
						aux.add(status);
						continue;
					}
				}
				
				status.setStatus(RedProdStatus.OK);
				
				// Comprobación de la existencia del fichero
				try {
					ProdRedAccess prodRedAccess = new ProdRedAccess(conex);
					if(prodRedAccess.countByMd5(prod.getMd5sum())>0){
						status.setStatus(RedProdStatus.EXISTS);
						status.setDesc("Product already ingested.");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				aux.add(status);
				
			}
		}
		
		
	
		try {
			if(conex!=null) conex.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		

		return (RedProdStatus[])aux.toArray(new RedProdStatus[0]);
	}
	
	
	/**
	 * Genera una carpeta temporal con todos los ficheros subidos por el usuario.
	 * @param streamFileUpload
	 * @return
	 * @throws IOException
	 */
	private static File generaCarpetaDescomprimida(File temp, InputStream streamFileUpload, String filename) throws IOException{

		
		
		File carpetaTemp = new File(temp.getAbsolutePath()+"/GTCUpload"+Calendar.getInstance().getTimeInMillis());
		carpetaTemp.mkdir();
		
		
		if(filename.toUpperCase().endsWith("FITS") || filename.toUpperCase().endsWith("FITS.GZ")
				|| filename.toUpperCase().endsWith("FIT") || filename.toUpperCase().endsWith("FIT.GZ")){
			File origen = new File(filename);
			// Copiamos el fichero a la carpeta
			File destino = new File(carpetaTemp.getAbsolutePath()+"/"+origen.getName());
			
			copiaStream(streamFileUpload, destino);
			streamFileUpload.close();
		}else if(filename.toUpperCase().endsWith("ZIP")){
			
			/// Descomprimimos todos los ficheros de dentro del zip
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(streamFileUpload));
			ZipEntry entry;
			while((entry = zis.getNextEntry()) != null) {
				File out = new File(entry.getName());
				out = new File(carpetaTemp.getAbsolutePath()+"/"+out.getName());
				copiaStream(zis, out);
			}
			zis.close();
			streamFileUpload.close();
		}else if(filename.toUpperCase().endsWith("TAR")){

            TarArchiveInputStream myTarFile=new TarArchiveInputStream(new BufferedInputStream(streamFileUpload));
            
            TarArchiveEntry entry = null;
     
            while ((entry = myTarFile.getNextTarEntry()) != null) {
            	File out = new File(entry.getName());
            	out = new File(carpetaTemp.getAbsolutePath()+"/"+out.getName());
            	copiaStream(myTarFile,out);
            }
            
            myTarFile.close();
            streamFileUpload.close();
			
		}else if(filename.toUpperCase().endsWith("TAR.GZ")){
			//Completar

            TarArchiveInputStream myTarFile=new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(streamFileUpload)));
            
            TarArchiveEntry entry = null;
     
            while ((entry = myTarFile.getNextTarEntry()) != null) {
            	File out = new File(entry.getName());
            	out = new File(carpetaTemp.getAbsolutePath()+"/"+out.getName());
            	copiaStream(myTarFile,out);
            }
            
            myTarFile.close();
            streamFileUpload.close();
		}
		
		
		return carpetaTemp;
	}
	
	/**
	 * Limpia el directorio temporal de carpetas generadas hace más de un día.
	 */
	private static void eliminaCarpetasAntiguas(File temp){
				
		// Sacamos el nombre correspondiente a hace 7 Días.
		String nameBase = "GTCUpload"+(Calendar.getInstance().getTimeInMillis()-7*24*3600*1000);
		
		for(File c : temp.listFiles()){
			if(c.isDirectory() && c.getName().startsWith("GTCUpload") && c.getName().compareTo(nameBase)<0){
				try {
					delete(c);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**
	 * Elimina recursivamente un directorio.
	 * @param f
	 * @throws IOException
	 */
	private static void delete(File f) throws IOException {
		  if (f.isDirectory()) {
		    for (File c : f.listFiles())
		      delete(c);
		  }
		  f.delete();
	}
	
	/**
	 * Copia el contenido de un InputStream a un fichero.
	 * @param in
	 * @param fichero
	 * @throws IOException
	 */
	private static void copiaStream(InputStream in, File fichero) throws IOException{
		int count;
		byte data[] = new byte[1024];
		// write the files to the disk
		FileOutputStream fos = new FileOutputStream(fichero);
		BufferedOutputStream dest = new BufferedOutputStream(fos, data.length);
		while ((count = in.read(data, 0, data.length)) != -1) {
			dest.write(data, 0, count);
		}
		dest.flush();
		dest.close();
	}

	////////////////////////////////////////////////////////////////////////////////////////

	public synchronized String getUsrId() {
		return this.usrId;
	}

	public synchronized Integer getCollId() {
		return collId;
	}

	public synchronized RedProdStatus[] getProductList() {
		return productList;
	}

	public synchronized void setProductList(RedProdStatus[] productList) {
		this.productList = productList;
	}


	public File getCarpetaTemp() {
		return carpetaTemp;
	}
	
	

}
