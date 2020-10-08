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

public class ProdDatosEmir2 extends ProdDatos{

	//Keywords específicos
	private String detver = null;
//	private String csustate = null;
	private String obstype = null;
	private String telescop = null;
	private String observat = null;
	private String filter = null;
	private String grism = null;
	
	public ProdDatosEmir2(File fichero, Connection con, String modo){
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
//		String modoAux = this.getOblock().getModo(con, "EMIR");
//			setModo(modoAux);//Directamente
		
		setModo(modo);

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
					
					if(getModo()!=null && getModo().equals("SPE")){
						
						if(this.getGrism().equalsIgnoreCase("OPEN")){
							//Imagen de adquisición
							this.setSubmodo("ACIMG");
							this.setModType("AC");
						}else{
							this.setSubmodo("SPE");
							this.setModType("SCI");
						}
					}else if(getModo()!=null && getModo().equals("IMG")){
						//Miramos el filtro para definir su submodo
						if(this.getFilter().equals("Y") || this.getFilter().equals("J") /*|| this.getFilter().equals("K")*/ || this.getFilter().equals("Ks") || this.getFilter().equals("H")){
							this.setModo("BBI");
							this.setSubmodo("BBI");
							this.setModType("SCI");
						}else if(/*this.getFilter().equals("F0960HBP40") || this.getFilter().equals("F1000HBP40") || this.getFilter().equals("F1042HBP42") || 
								this.getFilter().equals("F1084HBP45") || this.getFilter().equals("F1180HBP50") ||*/ this.getFilter().equals("F1230")){
							this.setModo("MBI");
							this.setSubmodo("MBI");
							this.setModType("SCI");
						}else if(this.getFilter().contains("Brg") || this.getFilter().contains("H2") || this.getFilter().contains("Fe")){
							this.setModo("NBI");
							this.setSubmodo("NBI");
							this.setModType("SCI");
							
						}
					}
			}
			
		
	}
	
	public ProdDatosEmir2(ProdDatos prodDatos, Connection con, String modo){
		this(prodDatos.getFile(), con, modo);
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

		// DETVER
		try{
			this.detver = header.findCard("DETVER").getValue().trim();
		}catch(NullPointerException e){}
/*		// CSUSTATE
		try{
			this.csustate = header.findCard("CSUSTATE").getValue().trim();
		}catch(NullPointerException e){}*/
		// OBSTYPE
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
		try{
			this.filter = header.findCard("FILTER").getValue().trim();
		}catch(NullPointerException e){}
		// GRISM
		try{
			this.grism = header.findCard("GRISM").getValue().trim();
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
		
		if( getModo()==null || getSubmodo()==null){
			if(getModo()=="IMG"){
				throw new GtcFileException("E-EMIR-0001: IMG sin submodo conocido ");
			}else if(getModo()=="SPE"){
				throw new GtcFileException("E-EMIR-0002:  SPE sin submodo conocido ");
			}else{
				throw new GtcFileException("E-EMIR-0003: Modo desconocido ");
			}
			
		}

		if(getRa()==null || getDe()==null){
			errors+="E-EMIR-0004: No tiene valores RA y DEC.; ";
		}
		
		if(this.getDetver()==null || !this.getDetver().equalsIgnoreCase("SCI")){
			errors+="E-EMIR-0005: DETVER no tiene valor SCI.; ";
		}
		
		
		
		/*if(this.getObstype()==null || !this.getObstype().equalsIgnoreCase("OBJECT")){
			errors+="E-EMIR-0007: OBSTYPE no tiene valor OBJECT.; ";
		}*/
		
		if(this.getTelescop()==null || !this.getTelescop().equalsIgnoreCase("GTC")){
			errors+="E-EMIR-0012: TELESCOP no tiene valor GTC.; ";
		}
		
		if(this.getObservat()==null || !this.getObservat().equalsIgnoreCase("ORM")){
			errors+="E-EMIR-0014: OBSERVAT no tiene valor ORM.; ";
		}
		
		
		if(getModo().equalsIgnoreCase("IMG") || getModo().equalsIgnoreCase("BBI")|| getModo().equalsIgnoreCase("MBI")|| getModo().equalsIgnoreCase("NBI")){
			
			/*if(this.getObsMode()!=null && !this.getObsMode().equalsIgnoreCase("STARE_IMAGE")) errors+="E-EMIR-0013: OBSMODE debe ser STARE_IMAGE.;";*/
			/*if(this.getCsustate()==null || !this.getCsustate().equalsIgnoreCase("CSU_OPEN")) errors+="E-EMIR-0006: CSUSTATE no tiene valor CSU_OPEN.; ";*/
			
			if(getModType().equalsIgnoreCase("SCI")){
				if(this.getOpentime()==null || this.getClosetime()==null) errors+="E-EMIR-0009: No DATE-OBS field.;";
				if(this.getExptime()==null || this.getExptime()<=0) errors+="E-EMIR-0008: EXPTIME not present or 0.;";
				if(this.getGrism()==null || !this.getGrism().equalsIgnoreCase("OPEN"))errors+="E-EMIR-0015: En IMG GRISM no tiene valor OPEN.; ";
				
			}else if(getSubmodo().equals("BIAS")){
				if(this.getExptime()!=null){
					if(this.getExptime()>0)	
						errors+="E-EMIR-0010: BIAS with EXPTIME>0;";
				}else if(this.getOpentime()!=null && this.getClosetime()!=null && !this.getOpentime().equals(this.getClosetime())){ 
							errors+="E-EMIR-0010: BIAS with EXPTIME>0;";
				}
			}else{
				if(this.getExptime()==null || this.getExptime()<=0) errors+="E-EMIR-0011: CAL with EXPTIME not present or 0.;";
			}
			
			
		}else if (getModo().equalsIgnoreCase("SPE")){
			/*if(this.getObsMode()!=null && !this.getObsMode().equalsIgnoreCase("STARE_SPECTRA")) errors+="E-EMIR-0016: OBSMODE debe ser STARE_SPECTRA.;";*/
			/*if(this.getCsustate()==null || !this.getCsustate().contains("SLIT")) errors+="E-EMIR-0017: CSUSTATE no contiene el valor SLIT.; ";*/
			
			if(getSubmodo().equalsIgnoreCase("SPE")){
				if(this.getOpentime()==null || this.getClosetime()==null) errors+="E-EMIR-0009: No DATE-OBS field.;";
				if(this.getExptime()==null || this.getExptime()<=0) errors+="E-EMIR-0008: EXPTIME not present or 0.;";
				
			}else if(getSubmodo().equals("BIAS")){
				if(this.getExptime()!=null){
					if(this.getExptime()>0)	
						errors+="E-EMIR-0010: BIAS with EXPTIME>0;";
				}else if(this.getOpentime()!=null && this.getClosetime()!=null && !this.getOpentime().equals(this.getClosetime())){ 
							errors+="E-EMIR-0010: BIAS with EXPTIME>0;";
				}
			}else{
				if(this.getExptime()==null || this.getExptime()<=0) errors+="E-EMIR-0011: CAL with EXPTIME not present or 0.;";
			}
		}else{
		
			errors+="E-EMIR-0003: Modo no válido;";
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
		
		filtros = new FiltroDb[0];
		
		for(int i=0; i<filtros.length; i++){
			filtros[i]=filtroAccess.selectByName(this.getFilter());
			if(filtros[i]==null){
				/// Si no existe insertamos el filtro.
				Integer filId = filtroAccess.selectNewId();
				FiltroDb newFiltro = new FiltroDb();
				newFiltro.setFilId(filId);
				newFiltro.setFilShortname(this.getFilter().trim().toUpperCase());
				newFiltro.setFilName(this.getFilter().trim().toUpperCase());
				
				filtroAccess.insert(newFiltro);
				filtros[i]=newFiltro;
				//throw new IngestionException("INGESTION ERROR: Filter "+this.getFilters()[i]+" not found in the database.");
			}
		}
		
		//Filtro
		Vector<Filter> aux = new Vector<Filter>();
		Filter filtro = new Filter();
		filtro.setName("circe_filter");
		filtro.setOrder(1);
		aux.add(filtro);
		Filter[] filters=(Filter[])aux.toArray(new Filter[0]);
		
		conf = confAccess.selectByFilters(det.getDetId(), submodo.getInsId(), submodo.getModId(), submodo.getSubmId(), filters);
		
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
		}
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

	
	public String getDetver() {
		return detver;
	}
	public void setDetver(String detver) {
		this.detver = detver;
	}
/*	public String getCsustate() {
		return csustate;
	}
	public void setCsustate(String csustate) {
		this.csustate = csustate;
	}*/
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

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getGrism() {
		return grism;
	}

	public void setGrism(String grism) {
		this.grism = grism;
	}

}
