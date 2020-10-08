package svo.gtc.proddat;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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

public class ProdDatosHorus extends ProdDatos{

	//Keywords específicos
	private String telescop = null;
	private String observat = null;
	private String imagetyp = null;
	private String obstype = null;
	private String mirror = null;
	private String callamp = null;
	private String flatlamp = null;
	
	
	public ProdDatosHorus(File fichero, Connection con){
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
		String modoAux = this.getOblock().getModo(con, "HORUS");
			setModo(modoAux);//Directamente

			String dirPadre = fichero.getParentFile().getName();
			
				// Las comunes a todos los modos
				if (dirPadre.equalsIgnoreCase("BIAS")) {
					this.setSubmodo("BIAS");
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
				} else if (dirPadre.equalsIgnoreCase("DARK")) {
					this.setSubmodo("DARK");
					this.setModType("CAL");
				}else if(dirPadre.equalsIgnoreCase("OBJECT")){
					
					if(getModo()!=null && getModo().equals("SPE")){
						if(getFile().getName().toUpperCase().contains("SPECTROSCOPY")){
							this.setSubmodo("SPE");
							this.setModType("SCI");
						}else if(getFile().getName().toUpperCase().contains("OTHER_TYPE")){
							this.setSubmodo("ACIMG");
							this.setModType("AC");
						}else if(getFile().getName().toUpperCase().contains("CAL")){
							this.setSubmodo("CAL");
							this.setModType("CAL");
						}
						
						
						
					}
			}
			
		
	}
	
	public ProdDatosHorus(ProdDatos prodDatos, Connection con){
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

		
		// TELESCOP
		try{
			this.telescop = header.findCard("TELESCOP").getValue().trim();
		}catch(NullPointerException e){}
		// OBSERVAT
		try{
			this.observat= header.findCard("OBSERVAT").getValue().trim();			
		}catch(NullPointerException e){}
		
		// IMAGETYP
		try{
			this.imagetyp= header.findCard("IMAGETYP").getValue().trim();			
		}catch(NullPointerException e){}
		
		// OBSTYPE
		try{
			this.obstype= header.findCard("OBSTYPE").getValue().trim();			
		}catch(NullPointerException e){}	
		
		// Mirror
		try{
			this.mirror= header.findCard("MIRROR").getValue().trim();			
		}catch(NullPointerException e){}
				
		// OBSTYPE
		try{
			this.callamp= header.findCard("CALLAMP").getValue().trim();			
		}catch(NullPointerException e){}
		
		// OBSTYPE
		try{
			this.flatlamp= header.findCard("FLATLAMP").getValue().trim();			
		}catch(NullPointerException e){}
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
		
		//System.out.println("Modo: "+getModo()+", submod: "+getSubmodo());
		
		if( getModo()==null || getSubmodo()==null){
			if(getModo()=="SPE"){
				throw new GtcFileException("E-HOR-0002:  SPE sin submodo conocido ");
			}else{
				throw new GtcFileException("E-HOR-0001: Modo desconocido ");
			}
			
		}

		if(getRa()==null || getDe()==null){
			errors+="E-HOR-0003: No tiene valores RA y DEC.; ";
		}
		
		
		if(this.getTelescop()==null || !this.getTelescop().equalsIgnoreCase("GTC")){
			errors+="E-HOR-0004: TELESCOP no tiene valor GTC.; ";
		}
		
		if(!this.getFile().getName().contains("HORS")){
			errors+="E-HOR-0005: El nombre del fichero debe contener HORS; ";
		}
		if(this.getPath().equalsIgnoreCase("OBJECT")){
			if(!(this.getFile().getName().toUpperCase().contains("SPECTROSCOPY") && this.getFile().getName().toUpperCase().contains("OTHER_TYPE") && this.getFile().getName().toUpperCase().contains("CAL"))){
				errors+="E-HOR-0008: Fichero en object no Osiris, Spectroscopy, other_type ni cal; ";
			}
		}
		
		if(getSubmodo().equals("SPE")){
		//if(getModType().equalsIgnoreCase("SCI")){
			if(this.getOpentime()==null || this.getClosetime()==null) errors+="E-HOR-0007: No hay DATE-OBS, opentime o closetime;";
			if(this.getExptime()==null || this.getExptime()<=0) errors+="E-HOR-0006: EXPTIME not present or 0.;";
			
			if(!(this.getImagetyp().equalsIgnoreCase("Spectroscopy") && this.getObstype().equalsIgnoreCase("Spectroscopy") && getMirror().equalsIgnoreCase("Sky") && 
					getCallamp().equalsIgnoreCase("Off") && getFlatlamp().equalsIgnoreCase("Off"))){
				errors+="E-HOR-0009: No cumple las condiciones del fichero Spectroscopy; ";
			}
		}else if(getSubmodo().equals("ACIMG")){
			if(!(this.getImagetyp().equalsIgnoreCase("other_type") && this.getObstype().equalsIgnoreCase("other_type") && getMirror().equalsIgnoreCase("Sky") && 
					getCallamp().equalsIgnoreCase("Off") && getFlatlamp().equalsIgnoreCase("Off"))){
				errors+="E-HOR-0010: No cumple las condiciones del fichero other_type; ";
			}
		}else if(getSubmodo().equals("CAL")){
			if(!(this.getImagetyp().equalsIgnoreCase("cal") && this.getObstype().equalsIgnoreCase("cal") && getMirror().equalsIgnoreCase("calibration") && 
					getCallamp().equalsIgnoreCase("On") && getFlatlamp().equalsIgnoreCase("Off"))){
				errors+="E-HOR-0011: No cumple las condiciones del fichero Cal; ";
			}
		}else if(getSubmodo().equals("BIAS")){
				if(this.getExptime()!=null){
					if(this.getExptime()>0)	
						errors+="E-HOR-0014: BIAS with EXPTIME>0;";
				}else if(this.getOpentime()!=null && this.getClosetime()!=null && !this.getOpentime().equals(this.getClosetime())){ 
							errors+="E-HOR-0014: BIAS with EXPTIME>0;";
				}
				if(this.getFile().getName().toUpperCase().contains("BIAS")){
					if(!(this.getImagetyp().equalsIgnoreCase("bias") && this.getObstype().equalsIgnoreCase("bias") && getMirror().equalsIgnoreCase("Sky") && 
							getCallamp().equalsIgnoreCase("Off"))){
						errors+="E-HOR-0013: No cumple las condiciones del fichero Bias; ";
					}
				}else{
					errors+="E-HOR-0016: Ficheros no bias en directorio bias;";
				}
				
		}else{
			if(this.getExptime()==null || this.getExptime()<=0) errors+="E-HOR-0015: CAL with EXPTIME not present or 0.;";
			if(getSubmodo().equals("STDS")){
				if(this.getFile().getName().toUpperCase().contains("SPECTROSCOPY")){
					if(!(this.getImagetyp().equalsIgnoreCase("Spectroscopy") && this.getObstype().equalsIgnoreCase("Spectroscopy") && getMirror().equalsIgnoreCase("Sky") && 
							getCallamp().equalsIgnoreCase("Off") && getFlatlamp().equalsIgnoreCase("Off"))){
						errors+="E-HOR-0009: No cumple las condiciones del fichero Spectroscopy; ";
					}
				}else if(this.getFile().getName().toUpperCase().contains("OTHER_TYPE")){
					if(!(this.getImagetyp().equalsIgnoreCase("other_type") && this.getObstype().equalsIgnoreCase("other_type") && getMirror().equalsIgnoreCase("Sky") && 
							getCallamp().equalsIgnoreCase("Off") && getFlatlamp().equalsIgnoreCase("Off"))){
						errors+="E-HOR-0010: No cumple las condiciones del fichero other_type; ";
					}
				}else if(this.getFile().getName().toUpperCase().contains("CAL")){
					if(!(this.getImagetyp().equalsIgnoreCase("cal") && this.getObstype().equalsIgnoreCase("cal") && getMirror().equalsIgnoreCase("calibration") && 
							getCallamp().equalsIgnoreCase("On") && getFlatlamp().equalsIgnoreCase("Off"))){
						errors+="E-HOR-0011: No cumple las condiciones del fichero Cal; ";
					}
				}else{
					errors+="E-HOR-0018: Ficheros no Spectroscopy, other_type o cal en directorio stds;";
				}
			}else if(getSubmodo().equals("ARC")){
				if(this.getFile().getName().toUpperCase().contains("CAL")){
					if(!(this.getImagetyp().equalsIgnoreCase("cal") && this.getObstype().equalsIgnoreCase("cal") && getMirror().equalsIgnoreCase("calibration") && 
							getCallamp().equalsIgnoreCase("On") && getFlatlamp().equalsIgnoreCase("Off"))){
						errors+="E-HOR-0011: No cumple las condiciones del fichero Cal; ";
					}
				}else{
					errors+="E-HOR-0017: Ficheros no cal en directorio arc;";	
				}
			}else if(getSubmodo().equals("DARK")){
				if(this.getFile().getName().toUpperCase().contains("DARK")){
					if(!(this.getImagetyp().equalsIgnoreCase("DARK") && this.getObstype().equalsIgnoreCase("DARK") && getMirror().equalsIgnoreCase("SKY") && 
							getCallamp().equalsIgnoreCase("off") && getFlatlamp().equalsIgnoreCase("Off"))){
						errors+="E-HOR-0020: No cumple las condiciones del fichero Dark; ";
					}
				}else{
					errors+="E-HOR-0021: Ficheros no dark en directorio dark;";	
				}
			}else if(getSubmodo().equals("FLAT")){
				if(this.getFile().getName().toUpperCase().contains("FLAT")){
					if(!(this.getImagetyp().equalsIgnoreCase("flat") && this.getObstype().equalsIgnoreCase("flat") && getMirror().equalsIgnoreCase("calibration") && 
							getCallamp().equalsIgnoreCase("Off") && getFlatlamp().equalsIgnoreCase("On"))){
						errors+="E-HOR-0012: No cumple las condiciones del fichero flat; ";
					}
				}else if(this.getFile().getName().toUpperCase().contains("OTHER_TYPE")){
					if(!(this.getImagetyp().equalsIgnoreCase("other_type") && this.getObstype().equalsIgnoreCase("other_type") && getMirror().equalsIgnoreCase("Sky") && 
							getCallamp().equalsIgnoreCase("Off") && getFlatlamp().equalsIgnoreCase("Off"))){
						errors+="E-HOR-0010: No cumple las condiciones del fichero other_type; ";
					}
				}else{
					errors+="E-HOR-0019: Ficheros no flat, other_type o cal en directorio flat;";
				}
			}
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
		ConfAccess confAccess 			= new ConfAccess(con);
		ConfFiltroAccess confFiltroAccess 	= new ConfFiltroAccess(con);
		FiltroAccess filtroAccess 		= new FiltroAccess(con);

		DetectorDb 		det 		= null;
		InstrumentoDb 	inst 		= null;
		SubmodoDb 		submodo		= null;
		FiltroDb[]		filtros		= null;
		ConfDb[]		conf 		= null;

		// Buscamos el detector, instrumento y el modo
		det = detectorAccess.selectByShortName(this.getDetector());
		if(det==null) throw new IngestionException("INGESTION ERROR: Detector "+getDetector()+" not found in the database.");

		inst = instAccess.selectByName(this.getInstrument());
		if(inst==null) throw new IngestionException("INGESTION ERROR: Instrument "+getInstrument()+" not found in the database.");
		
		submodo = submodoAccess.selectById(inst.getInsId(), getModo(), getSubmodo());
		if(submodo==null) throw new IngestionException("INGESTION ERROR: Mode "+getModo()+"/"+getSubmodo()+" not found in the database.");
		
		/*filtros = new FiltroDb[0];

		//Filtro
		Vector<Filter> aux = new Vector<Filter>();
		Filter filtro = new Filter();
		filtro.setName("megara_filter");
		filtro.setOrder(1);
		aux.add(filtro);
		Filter[] filters=(Filter[])aux.toArray(new Filter[0]);
		
		//conf = confAccess.selectByFilters(det.getDetId(), submodo.getInsId(), submodo.getModId(), submodo.getSubmId(), filters);
		
		// Si no existe la configuración, la creamos.
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
		
		ConfDb conf0 = new ConfDb();
		conf0.setDetId(7);
		conf0.setInsId(submodo.getInsId());
		conf0.setModId(submodo.getModId());
		conf0.setSubmId(submodo.getSubmId());
		conf0.setConfId(132412);
		
		conf = new ConfDb[1];
		conf[0]=conf0;
		
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

	public String getImagetyp() {
		return imagetyp;
	}

	public void setImagetyp(String imagetyp) {
		this.imagetyp = imagetyp;
	}

	public String getObstype() {
		return obstype;
	}

	public void setObstype(String obstype) {
		this.obstype = obstype;
	}

	public String getMirror() {
		return mirror;
	}

	public void setMirror(String mirror) {
		this.mirror = mirror;
	}

	public String getCallamp() {
		return callamp;
	}

	public void setCallamp(String callamp) {
		this.callamp = callamp;
	}

	public String getFlatlamp() {
		return flatlamp;
	}

	public void setFlatlamp(String flatlamp) {
		this.flatlamp = flatlamp;
	}



}
