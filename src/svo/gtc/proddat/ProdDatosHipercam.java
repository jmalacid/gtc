package svo.gtc.proddat;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.fits.PaddingException;
import svo.gtc.db.conf.ConfAccess;
import svo.gtc.db.conf.ConfDb;
import svo.gtc.db.conf.ConfFiltroAccess;
import svo.gtc.db.conf.ConfFiltroDb;
import svo.gtc.db.detector.DetectorAccess;
import svo.gtc.db.detector.DetectorDb;
import svo.gtc.db.filtro.FiltroAccess;
import svo.gtc.db.filtro.FiltroDb;
import svo.gtc.db.instrument.InstrumentoAccess;
import svo.gtc.db.instrument.InstrumentoDb;
import svo.gtc.db.modo.SubmodoAccess;
import svo.gtc.db.modo.SubmodoDb;
import svo.gtc.db.prodat.WarningDb;
import svo.gtc.ingestion.IngestionException;

public class ProdDatosHipercam extends ProdDatos{

	//Keywords específicos
	private String obstype = null;
	private String telescop = null;
	private String observat = null;
	
	public ProdDatosHipercam(File fichero, Connection con){
		super(fichero);
		
		try {
			rellenaCamposFits();
		} catch (FitsException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
		// DETERMINAMOS EL TIPO DE FICHERO
		// Modos: Imaging
		// 		Submodos BBI, NBI, MBI: Ciencia: IMG. Calibracion: BIAS, DARK, FLAT, STDS,ARC.
		
		
		// Determinamos el modo del Observing Block
		String modoAux = this.getOblock().getModo(con, "HIPERCAM");
			setModo(modoAux);//Directamente

			String dirPadre = fichero.getParentFile().getName();
			
				// Las comunes a todos los modos
				if (dirPadre.equalsIgnoreCase("BIAS")) {
					this.setSubmodo("BIAS");
					this.setModType("CAL");
				} else if (dirPadre.equalsIgnoreCase("DARK")) {
					this.setSubmodo("DARK");
					this.setModType("CAL");
				} else if (dirPadre.equalsIgnoreCase("FLAT")) {
					this.setSubmodo("FLAT");
					this.setModType("CAL");
				} else if (dirPadre.equalsIgnoreCase("STDS")) {
					this.setSubmodo("STDS");
					this.setModType("CAL");
				} else if (dirPadre.equalsIgnoreCase("ARC")) {
					this.setSubmodo("ARC");
					this.setModType("CAL");
				}else if(dirPadre.equalsIgnoreCase("OBJECT")){
					
					if(getModo()!=null && getModo().equals("IMG")){
						this.setSubmodo("IMG");
						this.setModType("SCI");
							
					}
			}
			
		
	}
	
	public ProdDatosHipercam(ProdDatos prodDatos, Connection con){
		this(prodDatos.getFile(), con);
	}
	
	/**
	 * Recoge los valores que provienen de los keywords del FITS 
	 * específicos de EMIR.
	 * 
	 * @throws FitsException
	 * @throws IOException
	 */
	private void rellenaCamposFits() throws FitsException, IOException{
		// TODO Completar método

		boolean compressed = false;
		
		if(this.getFile().getName().toUpperCase().endsWith(".GZ")){
			compressed = true;
		}
		
		Fits fEntrada = new Fits(this.getFile(), compressed);
	    	  
		 try{
		    	fEntrada.read();
		    }catch(PaddingException e){
		    	fEntrada.addHDU(e.getTruncatedHDU());
		    }
		
	    BasicHDU hdu = fEntrada.getHDU(0);
	    Header header = hdu.getHeader();

		
		try{
			this.obstype = header.findCard("OBSTYPE").getValue().trim();
		}catch(NullPointerException e){}
		// TELESCOP
		try{
			this.telescop = header.findCard("TELESCOP").getValue().trim();
		}catch(NullPointerException e){}
		// OBSERVAT
		try{
			this.observat= header.findCard("OBSERVAT").getValue().trim();			
		}catch(NullPointerException e){}
		// FILTER
		
		
	}
	
	/**
	 * Añade a las pruebas generales las específicas de CanariCam
	 */
	public void test(Connection con) throws GtcFileException{
		String err = "";
		try{
			super.test(con);
		}catch(GtcFileException e){
			err += e.getMessage(); 
			if(!err.trim().endsWith(";")){
				err+="; ";
			}
		}

		try {
			testSize();
		} catch (GtcFileException e) {
			err += e.getMessage() + "; ";
		}
		
		try{
			testModo();
		}catch(GtcFileException e){
			err += e.getMessage()+"; "; 
		}
		
		if(err.length()>0){
			throw new GtcFileException(err);
		}
	}
	
	public void testSize() throws GtcFileException {
		
		//Error tamaño, error si tienes menos de 50kB
		if(this.getFile().length()<50000){
			throw new GtcFileException("E-0022: Tamaño de fichero menor a 1MB ");
					
		}
	}
	
	/**
	 * Comprueba si el modo OBSMODE es uno de los conocidos y si cumple las condiciones.
	 * @throws GtcFileException
	 */
	public void testModo() throws GtcFileException{
		String errors = "";
		
		if(getModo()!=null && getModo().equals("RED")){
			throw new GtcFileException("E-HIP-0009: HiPERCAM con valores reducidos, no ingestar así ");
		}
		
		if( getModo()==null || getSubmodo()==null){
			
			throw new GtcFileException("E-HIP-0001: No tiene submodo conocido ");
			
		}

		if(getRa()==null || getDe()==null){
			errors+="E-HIP-0002: No tiene valores RA y DEC.; ";
		}
		
		if(this.getTelescop()==null || !this.getTelescop().equalsIgnoreCase("GTC")){
			errors+="E-HIP-0007: TELESCOP no tiene valor GTC.; ";
		}
		
		if(this.getObservat()==null || !this.getObservat().equalsIgnoreCase("ORM")){
			errors+="E-HIP-0008: OBSERVAT no tiene valor ORM.; ";
		}
		
		
		if(getModType().equalsIgnoreCase("SCI")){
			if(this.getOpentime()==null || this.getClosetime()==null) errors+="E-HIP-0004: No DATE-OBS field.;";
				if(this.getExptime()==null || this.getExptime()<=0) errors+="E-HIP-0003: EXPTIME not present or 0.;";
				
			}else if(getSubmodo().equals("BIAS")){
				if(this.getExptime()!=null){
					if(this.getExptime()>0)	
						errors+="E-HIP-0005: BIAS with EXPTIME>0;";
				}else if(this.getOpentime()!=null && this.getClosetime()!=null && !this.getOpentime().equals(this.getClosetime())){ 
						errors+="E-HIP-0005: BIAS with EXPTIME>0;";
				}
			}else{
				if(this.getExptime()==null || this.getExptime()<=0) errors+="E-HIP-0006: CAL with EXPTIME not present or 0.;";
			}
			
			
		
		
		
		
		if(errors.length()>0){
			throw new GtcFileException(errors);
		}
	
	}
	
	/**
	 * Obtiene una configuración de observación de la base de datos.
	 * 
	 */
	public ConfDb getConf(Connection con) throws SQLException, IngestionException{
		DetectorAccess detectorAccess 	= new DetectorAccess(con);
		InstrumentoAccess instAccess 	= new InstrumentoAccess(con);
		SubmodoAccess submodoAccess 		= new SubmodoAccess(con);

		DetectorDb 		det 		= null;
		InstrumentoDb 	inst 		= null;
		SubmodoDb 		submodo		= null;
		ConfDb[]		conf 		= null;

		// Buscamos el detector, instrumento y el modo
		det = detectorAccess.selectByShortName(this.getDetector());
		if(det==null) throw new IngestionException("INGESTION ERROR: Detector "+getDetector()+" not found in the database.");

		inst = instAccess.selectByName(this.getInstrument());
		if(inst==null) throw new IngestionException("INGESTION ERROR: Instrument "+getInstrument()+" not found in the database.");
		
		submodo = submodoAccess.selectById(inst.getInsId(), getModo(), getSubmodo());
		if(submodo==null) throw new IngestionException("INGESTION ERROR: Mode "+getModo()+"/"+getSubmodo()+" not found in the database.");
		
		ConfDb conf0 = new ConfDb();
		conf0.setDetId(5);
		conf0.setInsId(submodo.getInsId());
		conf0.setModId(submodo.getModId());
		conf0.setSubmId(submodo.getSubmId());
		conf0.setConfId(45557);
		
		conf = new ConfDb[1];
		conf[0]=conf0;
		
		/*// Si no existe la configuración, la creamos.
		if(conf.length<1){
			ConfDb newConf = new ConfDb();
			newConf.setDetId(det.getDetId());
			newConf.setInsId(submodo.getInsId());
			newConf.setModId(submodo.getModId());
			newConf.setSubmId(submodo.getSubmId());
			newConf = confAccess.insert(newConf);
			
			// Añadimos los filtros
			for(int i=0; i<filtros.length; i++){
				ConfFiltroDb newConfFiltro = new ConfFiltroDb();
				newConfFiltro.setFilId(filtros[i].getFilId());
				newConfFiltro.setDetId(newConf.getDetId());
				newConfFiltro.setInsId(newConf.getInsId());
				newConfFiltro.setModId(newConf.getModId());
				newConfFiltro.setSubmId(newConf.getSubmId());
				newConfFiltro.setConfId(newConf.getConfId());
				newConfFiltro.setCfilOrder(i+1);
				confFiltroAccess.Insert(newConfFiltro);
			}
			return newConf;
		}else if(conf.length>1){
			throw new IngestionException("INGESTION ERROR: More than one configuration for the data product.");
		}else{
			return conf[0];
		}*/
		return conf[0];
	}
	
	
	/**
	 * 
	 * @param con
	 * @param bpathId
	 * @return <p>True si el fichero se inserta como calibracion externa, False en caso contrario.</p>
	 * @throws SQLException
	 * @throws IngestionException
	 * @throws GtcFileException 
	 */
	
	public void insertaBD(Connection con, Integer bpathId, WarningDb[] warnings) throws SQLException, IngestionException, GtcFileException{
		
		super.insertaBD(con, bpathId, warnings);
	}

	public String getObstype() {
		return obstype;
	}
	public void setObstype(String obstype) {
		this.obstype = obstype;
	}
	public String getTelescop() {
		return telescop;
	}
	public void setTelescop(String telescop) {
		this.telescop = telescop;
	}
	public String getObservat() {
		return observat;
	}
	public void setObservat(String observat) {
		this.observat = observat;
	}

}
