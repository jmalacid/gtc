/*
 * @(#)ProdDatos.java    Jun 25, 2010
 *
 *
 * Ra�l Guti�rrez S�nchez. (raul@laeff.inta.es)	
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

package svo.gtc.proddat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import svo.gtc.db.DriverBD;
import svo.gtc.db.basepath.BasepathAccess;
import svo.gtc.db.basepath.BasepathDb;
import svo.gtc.db.obsblock.ObsBlockAccess;
import svo.gtc.db.obsblock.ObsBlockDb;
import svo.gtc.db.prodat.ProdDatosAccess;
import svo.gtc.db.prodat.ProdDatosDb;
import svo.gtc.db.proderr.ErrorDb;
import svo.gtc.db.proderr.ProdErrorAccess;
import svo.gtc.db.proderr.ProdErrorDb;
import svo.gtc.db.prodred.PredProdAccess;
import svo.gtc.db.prodred.PredProdDb;
import svo.gtc.db.prodred.ProdRedAccess;
import svo.gtc.db.prodred.ProdRedDb;
import svo.gtc.db.programa.ProgramaAccess;
import svo.gtc.db.programa.ProgramaDb;
import svo.gtc.ingestion.IngestionException;
import svo.varios.utiles.seguridad.Encriptado;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;

public class ProdDatosRed {
	static Logger logger = Logger.getLogger("svo.gtc");

	public static String CAL = "CAL";
	public static String SCI = "SCI";
	
	protected Connection	con				= null;
	
	protected int			predId 			= -1;
	private File		file			= null;
	
	private ProdDatosDb[] prodDatosRaw	= null;
	
	protected String 		programFits		= null;
	protected String 		oblockFits		= null; 
	protected Date 		opentimeFits	= null;
	protected Date 		clostimeFits	= null;
	private String 		obsmodeFits		= null;
	private String		instrumentFits  = null;

	private Double		ra				= null;
	private Double		de				= null;
	
	protected String		filenameorig	= null;
	private Long		fileSize 		= null;
	private String		md5sum			= null;
	
	private String		errores			= "";
	
	private String tipo = null;
	
	public ProdDatosRed(File fichero, Connection con) throws GtcFileException{
		
		this.con=con;
		
		ProdDatosAccess prodDatosAccess = null;
		try {
			prodDatosAccess = new ProdDatosAccess(con);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
		this.file=fichero;
		this.filenameorig=fichero.getName();
		this.fileSize = new Long(fichero.length());

		try {
			this.md5sum = Encriptado.md5(fichero);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {
			rellenaCamposFits();
		} catch (FitsException e) {
			e.printStackTrace();
			throw new GtcFileException(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new GtcFileException(e.getMessage());
		}
		
		if(this.opentimeFits==null){
			this.errores = "E-REDUCED-0003: OPENTIME keyword does not exist. OPENTIME keyword is needed to find the corresponding RAW product.";
			return;
		}
		
		try {
			// ATENCION:
			// Desactivamos la comprobación de observing block porque hay algunos productos raw donde estos datos
			// estan mal en la cabecera.
			//if(!file.getName().contains("CANARICAM")){
				
				ProdDatosDb[] prodDatosRaw = prodDatosAccess.selectByReducedProductInfo(this.programFits, null, this.opentimeFits, this.clostimeFits, tipo);
				//logger.debug("Número de datos reducidos encontrados= "+prodDatosRaw.length);
				if(prodDatosRaw.length==0){
					this.errores = "W-REDUCED-0001: Your data have been successfully ingested but they will not appear in the archive until they become public.";
				}else if(prodDatosRaw.length>1){
					//this.errores = "E-REDUCED-0002: More than one RAW product matching this reduced product.";
					for(int i=0;i<prodDatosRaw.length;i++){
						if(prodDatosRaw[i].getMtyId().equalsIgnoreCase("SCI")){
							this.setProdDatosRaw(prodDatosRaw);
						}
					}
					if(this.getProdDatosRaw()==null){
						for(int i=0;i<prodDatosRaw.length;i++){
							if(!prodDatosRaw[i].getMtyId().contains("_R")){
								this.setProdDatosRaw(prodDatosRaw);
							}
						}
					}
				}else{
					this.setProdDatosRaw(prodDatosRaw);
			}
			//	}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 * Recoge los valores que provienen de los keywords del FITS.
	 * 
	 * @throws FitsException
	 * @throws IOException
	 */
	private void rellenaCamposFits() throws FitsException, IOException{
		boolean compressed = false;
		
		if(this.getFile().getName().toUpperCase().endsWith(".GZ")){
			compressed = true;
		}
		
	    Fits fEntrada = new Fits(this.file,compressed);

	    BasicHDU hdu = null;
	    while((hdu=fEntrada.readHDU())!=null){
		    
		    Header header = hdu.getHeader();
	
			// INSTRUMENT
			try{
				if(this.instrumentFits==null) this.instrumentFits = header.findCard("INSTRUME").getValue();
			}catch(NullPointerException e){}
	
			// OBSMODE
			try{
				if(this.obsmodeFits==null) this.obsmodeFits = header.findCard("OBSMODE").getValue();
				if(this.obsmodeFits.equals("OsirisDomeFlats")) this.obsmodeFits="OsirisDomeFlat";
			}catch(NullPointerException e){}
			//CANARYCAM coge el valor CAMMODE en vez de OBSMODE
			try{
				String cammode = header.findCard("CAMMODE").getValue();
				this.obsmodeFits = cammode;
			}catch(NullPointerException e){}
			
			// PROGID
			try{
				if(this.programFits==null) this.programFits = header.findCard("GTCPRGID").getValue();
			}catch(NullPointerException e){}
	
			if(this.programFits==null){
				try{
					this.programFits = header.findCard("GTCPROGI").getValue();
				}catch(NullPointerException e){}
			}
			
			// OBLOCK
			try{
				if(this.oblockFits==null) this.oblockFits = header.findCard("GTCOBID").getValue();
				String aux =this.oblockFits.replaceAll("_", "-");
				if(aux.indexOf("-")!=aux.lastIndexOf("-")){
					this.oblockFits = aux.substring(aux.lastIndexOf("-")+1);
				}
			}catch(NullPointerException e){}
	
			// RA & DE
			if(this.ra==null) try{
				this.ra = new Double(header.findCard("RADEG").getValue());
			}catch(NumberFormatException e1){
				e1.printStackTrace();
			}catch(NullPointerException e){}
	
			if(this.de==null) try{
				this.de = new Double(header.findCard("DECDEG").getValue());
			}catch(NumberFormatException e1){
				e1.printStackTrace();
			}catch(NullPointerException e){}
			
			// Si no existe RADEG o DECDEG será el caso CANARICAM (RA y DEC en grados)
			if(this.ra==null || this.de==null){
				try{
					this.ra= new Double(header.findCard("RA").getValue());
					this.de= new Double(header.findCard("DEC").getValue());
							
				}catch(NullPointerException e){
				}catch(Exception e1){
					e1.printStackTrace();
				}
			}
			
			// DATE, OPENTIME, CLOSETIME (READTIME)
			if(this.opentimeFits==null){
				try{
					String auxOpentime 	= null;
					try{
						auxOpentime = header.findCard("OPENTIME").getValue();
						SimpleDateFormat sdf=new java.text.SimpleDateFormat("hh:mm:ss.SSS");
						this.opentimeFits = sdf.parse(auxOpentime);
						this.tipo="otro";
					}catch(NullPointerException e1){
						try{
							auxOpentime = header.findCard("UTSTART").getValue();
							SimpleDateFormat sdf=new java.text.SimpleDateFormat("hh:mm:ss.S");
							this.opentimeFits = sdf.parse(auxOpentime);
							this.tipo="S";
						}catch(Exception e2){
							logger.debug(this.getFile().getName()+": Opentime= "+auxOpentime);
							e2.printStackTrace(); 
						}
					}catch(Exception e2){
						logger.debug(this.getFile().getName()+": Opentime= "+auxOpentime);
						e2.printStackTrace(); 
					}
					
					
				
				}catch(NullPointerException e){}
				catch(Exception e){
					e.printStackTrace(); 
				}
			}
			
			if(this.clostimeFits==null){
				try{
					String auxClostime 	= null;
					try{
						auxClostime = header.findCard("CLOSTIME").getValue();
						SimpleDateFormat sdf=new java.text.SimpleDateFormat("hh:mm:ss.SSS");
						this.clostimeFits = sdf.parse(auxClostime);
						this.tipo="otro";
					}catch(NullPointerException e1){
						try{
							auxClostime = header.findCard("UTEND").getValue();
							SimpleDateFormat sdf=new java.text.SimpleDateFormat("hh:mm:ss.S");
							this.clostimeFits = sdf.parse(auxClostime);
							this.tipo="S";
						}catch(Exception e2){
							logger.debug(this.getFile().getName()+": Clostime= "+auxClostime);
							e2.printStackTrace(); 
						}
					}catch(Exception e2){
						logger.debug(this.getFile().getName()+": Clostime= "+auxClostime);
						e2.printStackTrace(); 
					}
					
				}catch(NullPointerException e){}
				catch(Exception e){
					e.printStackTrace(); 
				}
			}

	    }

		fEntrada.getStream().close();
		
	}

	/**
	 * Realiza los test pertinentes sobre el producto reducido
	 * @param con
	 * @throws GtcFileException
	 */
	public void test() throws GtcFileException{
		String err = this.errores;
		try{
			testInstrumento();
		}catch(GtcFileException e){
			e.printStackTrace();
			err += e.getMessage()+"; "; 
		}

		if(err.length()>0){
			this.errores=err;
			throw new GtcFileException(err);
		}
	}
	
	/**
	 * Comprueba si el instrumento es uno de los conocidos.
	 * @throws ProdDatosException
	 */
	public void testInstrumento() throws GtcFileException{
		String errors = "";
		if( instrumentFits!=null && !instrumentFits.equalsIgnoreCase("OSIRIS") && !instrumentFits.equalsIgnoreCase("CANARICAM"))
				errors+= "E-0003: Not recognized INSTRUMENT: "+instrumentFits+";";
		
		if(errors.length()>0){
			throw new GtcFileException(errors);
		}

	}
	
	public static void main(String args[]) throws GtcFileException, SQLException{
		// Conexi�n con la base de datos
		DriverBD driver = new  DriverBD();
		Connection con = null;
		try {
			con = driver.bdConexion();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		
		
		ProdDatosRed prod = new ProdDatosRed(new File("/pcdisk/oort6/raul/gtc/data-pru/reduced/RicardoAmorin/GP004054B_fcal_ok.fits"), con);
		//ProdDatosRed prod = new ProdDatosRed(new File("/pcdisk/marconi/raul/proyectos/GTC/gtcData/data-pru/reduced/reducedData3.fits"), con);
		//ProdDatos prod = new ProdDatos(new File("/media/Transcend/gtcData/GTC19-09A/OB0002/object/0000003464-20090520-OSIRIS-OsirisBroadBandImage.fits"));
		System.out.println("Filename "+prod.file.getName());
		System.out.println("ProgId   "+prod.programFits);
		System.out.println("OblId   "+prod.oblockFits);
		System.out.println("Filesize "+prod.fileSize.longValue()/1024/1024+" MB");
		System.out.println("MD5sum   "+prod.md5sum);
		System.out.println("Mode 	 "+prod.obsmodeFits);
		System.out.println("Program	 "+prod.programFits);
		System.out.println("Oblock 	 "+prod.oblockFits);
		System.out.println("RA  	 "+prod.ra.doubleValue());
		System.out.println("DEC 	 "+prod.de.doubleValue());
		System.out.println("OpenT 	 "+prod.opentimeFits);

		prod.test();
		
		if(con!=null) con.close();
	}
	

	public InputStream getInputStream() throws FileNotFoundException{
		FileInputStream fins = new FileInputStream(this.getFile());
		
		return(fins);
	}

	public long writeToStream(OutputStream out) throws IOException {
		long size = 0;
		InputStream ins = this.getInputStream();
		byte[] buf = new byte[1024];
		int len = -1;
		while((len=ins.read(buf))>=0){
			size+=len;
			out.write(buf, 0, len);
		}
		out.flush();
		return size;
	}

	
	
	public String getInstrumentFits() {
		return instrumentFits;
	}

	public void setInstrumentFits(String instrumentFits) {
		this.instrumentFits = instrumentFits;
	}

	public int getPredId() {
		return predId;
	}

	public synchronized File getFile() {
		return file;
	}

	public synchronized void setFile(File file) {
		this.file = file;
	}

	public synchronized Double getRa() {
		return ra;
	}

	public synchronized void setRa(Double ra) {
		this.ra = ra;
	}

	public synchronized Double getDe() {
		return de;
	}

	public synchronized void setDe(Double de) {
		this.de = de;
	}

	public synchronized String getFilenameorig() {
		return filenameorig;
	}

	public synchronized Long getFileSize() {
		return fileSize;
	}

	public synchronized void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	
	public synchronized String getMd5sum() {
		return md5sum;
	}

	public synchronized void setMd5sum(String md5sum) {
		this.md5sum = md5sum;
	}

	public synchronized String getErrores() {
		return errores;
	}

	public synchronized void setErrores(String errores) {
		this.errores = errores;
	}

	
	public synchronized void setCon(Connection con) {
		this.con = con;
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	
	

	public String getProgramFits() {
		return programFits;
	}

	public void setProgramFits(String programFits) {
		this.programFits = programFits;
	}

	public String getOblockFits() {
		return oblockFits;
	}

	public void setOblockFits(String oblockFits) {
		this.oblockFits = oblockFits;
	}

	public String getObsmodeFits() {
		return obsmodeFits;
	}

	public void setObsmodeFits(String obsmodeFits) {
		this.obsmodeFits = obsmodeFits;
	}

	public void ingest(String usrId, Integer colId) throws Exception{
		
		Integer basepath=1000;
		if(this.getProdDatosRaw()[0].getProgId()!=null){
			System.out.println(this.getProdDatosRaw()[0].getProgId());
		}
		if(this.getProdDatosRaw()[0].getOblId()!=null){
			System.out.println(this.getProdDatosRaw()[0].getOblId());
		}
		if(usrId!=null){
			System.out.println(usrId);
		}
		if(colId!=null){
			System.out.println(colId);
		}
		String  path = "/"+this.getProdDatosRaw()[0].getProgId()+"/OB"+this.getProdDatosRaw()[0].getOblId()+"/"+usrId+"/"+colId+"/";
		String ext = ".fits";
		
		if(this.filenameorig.toUpperCase().endsWith("FITS.GZ")){
			ext = ".fits.gz";
		}
		
		boolean autocommit = this.con.getAutoCommit();

		try{
			
			
			BasepathAccess bpathAccess = new BasepathAccess(this.con);
			BasepathDb bpathDb = bpathAccess.selectBpathById(basepath);


			/// Insertamos en la tabla ProdReducido
			ProdRedAccess prodRedAccess = new ProdRedAccess(this.con);
			ProdRedDb prodRedDb = new ProdRedDb();


			int ind=0;
			String nameFichDestino;
			boolean existe=false;
			do{
				nameFichDestino = this.getProdDatosRaw()[0].getProdFilename().replaceAll("\\.gz", "").replaceAll("\\.fits", "")+"_RED_"+ind;
				existe = prodRedAccess.existsName(nameFichDestino+"%");
				ind++;
			}while(existe);
			

			this.con.setAutoCommit(false);

			
			predId = prodRedAccess.getNewPredId(usrId, colId);

			//Si no tenemos ra y dec cogemos el del raw
			Double radeg = null;
			if(this.getRa()==null){
				//System.out.println("Ra null"+this.getProdDatosRaw()[0].getProdRa());
				radeg = this.getProdDatosRaw()[0].getProdRa();
			}else{
				radeg = this.getRa();
			}
			
			Double decdeg = null;
			if(this.getDe()==null){
				//System.out.println("Dec null"+this.getProdDatosRaw()[0].getProdDe());
				decdeg = this.getProdDatosRaw()[0].getProdDe();
			}else{
				decdeg = this.getDe();
			}
			
			prodRedDb.setPredId(predId);
			prodRedDb.setUsrId(usrId);
			prodRedDb.setColId(colId);
			prodRedDb.setBpathId(basepath);
			prodRedDb.setBpathPath(bpathDb.getBpathPath());
			prodRedDb.setPredPath(path);
			prodRedDb.setPredFilenameOrig(this.getFilenameorig());
			prodRedDb.setPredFilesize(this.getFileSize());
			prodRedDb.setPredMd5sum(this.getMd5sum());
			prodRedDb.setPredRa(radeg);
			prodRedDb.setPredDe(decdeg);
			prodRedDb.setPredFilename(nameFichDestino+ext);
			prodRedDb.setPredType("USER");
			prodRedAccess.insProdDatos(prodRedDb);
			
			
			/// Insertamos en la tabla PRed_Prod
			PredProdAccess predProdAccess = new PredProdAccess(this.con);
			PredProdDb predProdDb = new PredProdDb();
			
			predProdDb.setPredId(prodRedDb.getPredId());
			predProdDb.setProgId(this.getProdDatosRaw()[0].getProgId());
			predProdDb.setOblId(this.getProdDatosRaw()[0].getOblId());
			predProdDb.setProdId(this.getProdDatosRaw()[0].getProdId());

			predProdAccess.insPredProd(predProdDb);
			
			
			////// Copiamos los ficheros a su destino
			
			// Creamos el directorio destino si no existe
			File dirDestino = new File(bpathDb.getBpathPath()+path);
			
			if(!dirDestino.exists()){
				dirDestino.mkdirs();
			}
			File fichDestino = new File(prodRedDb.getAbsolutePath());
			// Copiamos el fichero
			copyFile(this.file,fichDestino);
			this.con.commit();
			this.con.setAutoCommit(autocommit);
		}catch(Exception e){
			e.printStackTrace();
			this.con.rollback();
			this.con.setAutoCommit(autocommit);
			throw e;
		}
		
	}
	
	public void copyTemp(String usrId, Integer colId) throws Exception{

		//String  path = "/pcdisk/oort/jmalacid/data/proyectos/GTC/reducidos/"+usrId+"/";
		String  path = "/export/data-gtc/reduced/pending/"+usrId+"/";
		

		try{
			////// Copiamos los ficheros a su destino
			
			// Creamos el directorio destino si no existe
			File dirDestino = new File(path);
			
			if(!dirDestino.exists()){
				dirDestino.mkdirs();
			}
			
			File fichDestino = new File(path+this.file.getName());
			
			// Copiamos el fichero
			if(!fichDestino.exists()){
				copyFile(this.file,fichDestino);
			}
			
		}catch(Exception e){
			throw e;
		}
		
	
	}
	
	
	private static void copyFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	    	logger.debug("New file: "+destFile.toString());
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;
	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();

	        // previous code: destination.transferFrom(source, 0, source.size());
	        // to avoid infinite loops, should be:
	        long count = 0;
	        long size = source.size();              
	        while((count += destination.transferFrom(source, count, size-count))<size);
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}

	/**
	 * Inserta un producto en la tabla ProdError y le asocia los errores correspondientes.
	 * @param con
	 * @param bpathId
	 * @throws SQLException 
	 * @throws SQLException
	 * @throws IngestionException
	 */
	public void insertaErrorBD(Connection con, Integer bpathId, ErrorDb[] errores) throws SQLException {
		ProgramaAccess programaAccess = new ProgramaAccess(con);
		ObsBlockAccess oblAccess = new ObsBlockAccess(con);
		BasepathAccess basepathAccess = new BasepathAccess(con);
		ProdErrorAccess prodErrorAccess = new ProdErrorAccess(con);
		
		ProgramaDb 		programaDb 	= null;
		ObsBlockDb 		oblDb		= null;
		BasepathDb 		basepathDb	= null;
		
		// Comprobamos la existencia de Programa y si no existe lo añadimos
		programaDb = programaAccess.selectById(this.getProgramFits().toUpperCase().replaceAll("\\s", ""));

		// Comprobamos la existencia de Oblock y si no existe lo añadimos
		oblDb = oblAccess.selectById(this.getProgramFits().toUpperCase().replaceAll("\\s", ""),
									 this.getOblockFits().toUpperCase().replaceAll("\\s", ""));		
		
		// Determinamos el path base
		basepathDb = basepathAccess.selectBpathById(bpathId);
		String basePath = basepathDb.getBpathPath();

		
		// Si ya est� en la tabla de errores, se borran los que hab�a 
		// y se introducen los nuevos. Si no est� se introduce junto
		// con sus errores.
		if(prodErrorAccess.countProdErrorById(getProgramFits(), getOblockFits(), this.predId)>0){
			prodErrorAccess.delErrores(getProgramFits(), getOblockFits(),this.predId);
			prodErrorAccess.insErrors(getProgramFits(), getOblockFits(),this.predId, errores );
		}else{
			ProdErrorDb prodError = new ProdErrorDb(this,basePath);
			prodError.setErrors(errores);
			prodErrorAccess.insProdError(prodError);
		}
	}
	
	public ProdDatosDb[] getProdDatosRaw() {
		return prodDatosRaw;
	}

	public void setProdDatosRaw(ProdDatosDb[] prodDatosRaw) {
		this.prodDatosRaw = prodDatosRaw;
	}
	
	
}
