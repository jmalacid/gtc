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

public class ProdDatosMegara extends ProdDatos{

	//Keywords específicos
	private String telescop = null;
	private String observat = null;
	private String lamp1s = null;
	private String lamp2s = null;
	private String lamps1s = null;
	private String lamps2s = null;
	private String lamps3s = null;
	private String lamps4s = null;
	private String lamps5s = null;
	private String lampmir = null;
	private String speclamp = null;
	
	
	public ProdDatosMegara(File fichero, Connection con){
		super(fichero);
		
		try {
			rellenaCamposFits();
		} catch (FitsException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
		// DETERMINAMOS EL TIPO DE FICHERO
		// Modos: Spe
		// 		Submodos SPE: Ciencia: IFU. Calibracion: BIAS, DARK, FLAT, STDS,ARC.
		
		
		// Determinamos el modo del Observing Block
		String modoAux = this.getOblock().getModo(con, "MEGARA");
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
				}else if(dirPadre.equalsIgnoreCase("OBJECT")){
					
					if(getModo()!=null && getModo().equals("SPE")){
						
						this.setSubmodo("SPE");
						this.setModType("SCI");
						
					}else if(getModo()!=null && getModo().equals("IFU")){
						
						this.setSubmodo("SPE");
						this.setModType("SCI");
						
					}
			}
			
		
	}
	
	public ProdDatosMegara(ProdDatos prodDatos, Connection con){
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
		
		// LAMPARAS
		// LAMPI1S
		try{
			this.lamp1s= header.findCard("LAMPI1S").getValue().trim();			
		}catch(NullPointerException e){}
		// LAMPI2S
		try{
			this.lamp2s= header.findCard("LAMPI2S").getValue().trim();			
		}catch(NullPointerException e){}
		// LAMPS1S
		try{
			this.lamps1s= header.findCard("LAMPS1S").getValue().trim();			
		}catch(NullPointerException e){}
		// LAMPS2S
		try{
			this.lamps2s= header.findCard("LAMPS2S").getValue().trim();			
		}catch(NullPointerException e){}
		// LAMPS3S
		try{
			this.lamps3s= header.findCard("LAMPS3S").getValue().trim();			
		}catch(NullPointerException e){}
		// LAMPS4S
		try{
			this.lamps4s= header.findCard("LAMPS4S").getValue().trim();			
		}catch(NullPointerException e){}
		// LAMPS5S
		try{
			this.lamps5s= header.findCard("LAMPS5S").getValue().trim();			
		}catch(NullPointerException e){}
		// LAMPMIR
		try{
			this.lampmir= header.findCard("LAMPMIR").getValue().trim();			
		}catch(NullPointerException e){}
		// SPECLAMP
		try{
			this.speclamp= header.findCard("SPECLAMP").getValue().trim();			
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
			if(getModo()=="IFU"){
				throw new GtcFileException("E-MEG-0001: IFU sin submodo conocido ");
			}else if(getModo()=="SPE"){
				throw new GtcFileException("E-MEG-0002:  SPE sin submodo conocido ");
			}else{
				throw new GtcFileException("E-MEG-0003: Modo desconocido ");
			}
			
		}

		if(getRa()==null || getDe()==null){
			errors+="E-MEG-0004: No tiene valores RA y DEC.; ";
		}
		
		
		if(this.getTelescop()==null || !this.getTelescop().equalsIgnoreCase("GTC")){
			errors+="E-MEG-0016: TELESCOP no tiene valor GTC.; ";
		}
		
		if(this.getObservat()==null || !this.getObservat().equalsIgnoreCase("ORM")){
			errors+="E-MEG-0017: OBSERVAT no tiene valor ORM.; ";
		}

			if(getModType().equalsIgnoreCase("SCI")){
				if(this.getOpentime()==null || this.getClosetime()==null) errors+="E-MEG-0013: No DATE-OBS field.;";
				if(this.getExptime()==null || this.getExptime()<=0) errors+="E-MEG-0012: EXPTIME not present or 0.;";
				if(!(this.getFile().getName().contains("LcbImage") || this.getFile().getName().contains("LcbAcquisition") || this.getFile().getName().contains("MOSImage") 
						|| this.getFile().getName().contains("MOSAcquisition") || this.getFile().getName().contains("MegaraSuccess"))){
					errors+="E-MEG-0011: Ficheros de ciencia que no son Lcb o Mos;";
				}
				if(!(this.lamp1s.equals("0") && this.lamp2s.equals("0") && this.lamps1s.equals("0") && this.lamps2s.equals("0") && this.lamps3s.equals("0")
						&& this.lamps4s.equals("0") && this.lamps5s.equals("0") && this.lampmir.toUpperCase().equals("PARK"))){
					errors+="E-MEG-0018: Ficheros de ciencia que no cumplen condiciones de lampara;";
					
				}
				
				
			}else if(getSubmodo().equals("BIAS")){
				if(this.getExptime()!=null){
					if(this.getExptime()>0)	
						errors+="E-MEG-0014: BIAS with EXPTIME>0;";
				}else if(this.getOpentime()!=null && this.getClosetime()!=null && !this.getOpentime().equals(this.getClosetime())){ 
							errors+="E-MEG-0014: BIAS with EXPTIME>0;";
				}
				if(!this.getFile().getName().toUpperCase().contains("BIAS")) errors+="E-MEG-0010: Ficheros no bias en directorio bias;";
				
			}else{
				if(this.getExptime()==null || this.getExptime()<=0) errors+="E-MEG-0015: CAL with EXPTIME not present or 0.;";
				if(getSubmodo().equals("STDS")){
					if(this.getFile().getName().toUpperCase().contains("ARC") || this.getFile().getName().toUpperCase().contains("FIBERFLAT") || this.getFile().getName().toUpperCase().contains("TRACEMAP") 
							||this.getFile().getName().toUpperCase().contains("BIAS")){
						errors+="E-MEG-0005: Fichero bias, arc o flat en directorio STDS;";
					}
				}else if(getSubmodo().equals("ARC")){
					if(!this.getFile().getName().toUpperCase().contains("ARCCALIBRATION")) errors+="E-MEG-0006: Ficheros no arccalibration en directorio arc;";
					
					if(!((this.lamp1s.equals("0") && this.lamp2s.equals("0") && this.lamps1s.equals("1") && this.lamps2s.equals("1") && this.lamps3s.equals("0")
							&& this.lamps4s.equals("1") && this.lamps5s.equals("0") && this.lampmir.toUpperCase().equals("WORK") && this.speclamp.toUpperCase().equals("THAR"))
							|| (this.lamp1s.equals("0") && this.lamp2s.equals("0") && this.lamps1s.equals("0") && this.lamps2s.equals("0") && this.lamps3s.equals("1")
							&& this.lamps4s.equals("0") && this.lamps5s.equals("1") && this.lampmir.toUpperCase().equals("WORK") && this.speclamp.toUpperCase().equals("THNE")))){
						errors+="E-MEG-0007: Ficheros arc que no cumplen condiciones de lampara;";
					}
					

				}else if(getSubmodo().equals("FLAT")){
					if((this.getFile().getName().toUpperCase().contains("TRACEMAP") || this.getFile().getName().toUpperCase().contains("FIBERFLAT"))){
						if(!(this.lamp1s.equals("1") && this.lamp2s.equals("1") && this.lamps1s.equals("0") && this.lamps2s.equals("0") && this.lamps3s.equals("0")
								&& this.lamps4s.equals("0") && this.lamps5s.equals("0") && this.lampmir.toUpperCase().equals("WORK") && this.speclamp.toUpperCase().equals("NONE"))){
							errors+="E-MEG-0009: Ficheros flat que no cumplen condiciones de lampara;";
							
						}
					}else if(!(this.getFile().getName().toUpperCase().contains("TWILIGHTFLATIMAGE"))){
						errors+="E-MEG-0008: Ficheros no tracemap o fiberflat en directorio flat;";
					}
				}
				
			}
			
			
		if (!(getModo().equalsIgnoreCase("SPE") || getModo().equalsIgnoreCase("IFU"))){
		
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
		conf0.setDetId(6);
		conf0.setInsId(submodo.getInsId());
		conf0.setModId(submodo.getModId());
		conf0.setSubmId(submodo.getSubmId());
		conf0.setConfId(82954);
		
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

	public String getLamp1s() {
		return lamp1s;
	}

	public void setLamp1s(String lamp1s) {
		this.lamp1s = lamp1s;
	}

	public String getLamp2s() {
		return lamp2s;
	}

	public void setLamp2s(String lamp2s) {
		this.lamp2s = lamp2s;
	}

	public String getLamps1s() {
		return lamps1s;
	}

	public void setLamps1s(String lamps1s) {
		this.lamps1s = lamps1s;
	}

	public String getLamps2s() {
		return lamps2s;
	}

	public void setLamps2s(String lamps2s) {
		this.lamps2s = lamps2s;
	}

	public String getLamps3s() {
		return lamps3s;
	}

	public void setLamps3s(String lamps3s) {
		this.lamps3s = lamps3s;
	}

	public String getLamps4s() {
		return lamps4s;
	}

	public void setLamps4s(String lamps4s) {
		this.lamps4s = lamps4s;
	}

	public String getLamps5s() {
		return lamps5s;
	}

	public void setLamps5s(String lamps5s) {
		this.lamps5s = lamps5s;
	}

	public String getLampmir() {
		return lampmir;
	}

	public void setLampmir(String lampmir) {
		this.lampmir = lampmir;
	}

	public String getSpeclamp() {
		return speclamp;
	}

	public void setSpeclamp(String speclamp) {
		this.speclamp = speclamp;
	}


}
