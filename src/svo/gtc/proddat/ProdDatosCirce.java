package svo.gtc.proddat;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

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
import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.fits.PaddingException;

public class ProdDatosCirce extends ProdDatos{
	
	private String cammode = null; 
	private String obsclass = null;
	private String obstype = null;
	private String compstat = null;
	private String gtcprgid = null;
	private String hwprot = null;
	private String gw = null;
	private String linslide = null;
	private Filter[] filters = new Filter[0];
	
	public ProdDatosCirce(File fichero, Connection con){
		super(fichero);
		
		try {
			rellenaCamposFits();
		} catch (FitsException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
		// DETERMINAMOS EL TIPO DE FICHERO   (Por ahora solo Imaging)
		// Modos: Imaging
		// 		Submodos IMG: Ciencia: IMG. Calibracion: BIAS, DARK, FLAT, STDS.
		
		
		// Determinamos el modo del Observing Block
		String modoAux = this.getOblock().getModo(con, "CIR");
//Por ahora se define el modo como IMG, los ficheros de object tienen que tener image o other_type
		
		
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
					
					if(getModo()!=null && getModo().equals("POL")){
						this.setSubmodo("POL");
						this.setModType("SCI");
					}else if(getModo()!=null && getModo().equals("IMG")){
					
						//Fichero ciencia
						//No ponemos condiciones, luego vemos los errores
							
						this.setSubmodo("IMG");
						this.setModType("SCI");
							
					
						
					}
			}
			
		
	}
	
	public ProdDatosCirce(ProdDatos prodDatos, Connection con){
		this(prodDatos.getFile(), con);
	}

	/**
	 * Recoge los valores que provienen de los keywords del FITS 
	 * espec�ficos de CIRCE.
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

		// CAMMODE
		try{
			this.cammode = header.findCard("CAMMODE").getValue().trim();
		}catch(NullPointerException e){}
		// OBSCLASS
		try{
			this.obsclass = header.findCard("OBSCLASS").getValue().trim();
		}catch(NullPointerException e){}
		// OBSTYPE
		try{
			this.obstype = header.findCard("OBSTYPE").getValue().trim();
		}catch(NullPointerException e){}
		
		
		try{
			this.compstat = header.findCard("COMPSTAT").getValue().trim();
		}catch(NullPointerException e){}
		//gtcprgid
		try{
			this.gtcprgid= header.findCard("GTCPRGID").getValue().trim();			
		}catch(NullPointerException e){
			try {
				this.gtcprgid = header.findCard("GTCPROGI").getValue().trim();
			}catch (NullPointerException e1) {
			}
			
		}
		try{
			this.hwprot = header.findCard("HWPROT").getValue().trim();
		}catch(NullPointerException e){}
		try{
			this.gw = header.findCard("GW").getValue().trim();
		}catch(NullPointerException e){}
		try{
			this.linslide = header.findCard("LINSLIDE").getValue().trim();
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
				throw new GtcFileException("E-CIRC-0008: IMG sin submodo conocido ");
			}else if(getModo()=="POL"){
				throw new GtcFileException("E-CIRC-0011: POL sin submodo conocido ");
			}else{
				throw new GtcFileException("E-CIRC-0002: Modo desconocido ");
			}
			
		}

		
		if(!getCompstat().equalsIgnoreCase("COMPLETE")){
			errors+="E-0020: COMPSTAT not equal to \"COMPLETE\".;";
		}
		if(getRa()==null || getDe()==null){
			errors+="E-CIRC-0007: No tiene valores RA y DEC.; ";
		}
		
		
		
		if(getModo().equalsIgnoreCase("IMG")){
			
			if(this.getObsMode()!=null && !this.getObsMode().equalsIgnoreCase("Imaging")) errors+="E-CIRC-0001: OBSMODE y CAMMODE debe ser IMAGING.;";
			if(this.getCammode()!=null && !this.getCammode().equalsIgnoreCase("Imaging")) errors+="E-CIRC-0001: OBSMODE y CAMMODE debe ser IMAGING.;";
			
			
			if(this.getRa()==null || this.getDe()==null) errors+="E-CIRC-0007: File with no coordenates";
			
			if(getSubmodo().contains("IMG")){
				if(getGtcprgid()== null || getGtcprgid().equalsIgnoreCase("CALIB")){
					errors+="E-0019: GTCPRGID is equal to \"CALIB\".;";
				}
				if(this.getOpentime()==null || this.getClosetime()==null) errors+="E-CIRC-0004: No OPENTIME or CLOSETIME field.;";
				if(this.getExptime()==null || this.getExptime()<=0) errors+="E-CIRC-0003: EXPTIME not present or 0.;";
				if(!this.getObsMode().equalsIgnoreCase("SCIENCE"))errors+="W-CIRC-0001: OBSCLASS no valido para ciencia.;";
				if(!this.getObstype().equalsIgnoreCase("BROADBAND_IMAGE"))errors+="W-CIRC-0002: OBSTYPE no valido para ciencia.;";
				if((this.getHwprot()!=null && this.getHwprot().toUpperCase().startsWith("POL") )|| 
						(this.getGw()!=null && this.getGw().equalsIgnoreCase("Wolly")) || (this.getLinslide()!=null && this.getLinslide().equalsIgnoreCase("Full_F_Imaging"))){
					errors+="E-CIRC-0010: Modo imagen con configuración polarimetry.;";
				}
			}else if(getSubmodo().equals("BIAS")){
				if(this.getExptime()!=null){
					if(this.getExptime()>0)	
						errors+="E-CIRC-0005: BIAS with EXPTIME>0;";
				}else if(this.getOpentime()!=null && this.getClosetime()!=null && !this.getOpentime().equals(this.getClosetime())){ 
							errors+="E-CIRC-0005: BIAS with EXPTIME>0;";
				}
				if(!this.getObsMode().equalsIgnoreCase("CALIB"))errors+="W-CIRC-0003: OBSCLASS no valido para calibracion.;";
				if(this.getObstype().equalsIgnoreCase("BROADBAND_IMAGE") || this.getObstype().equalsIgnoreCase("POLARIMETRY") || this.getObstype().equalsIgnoreCase("OTHER_TYPE"))errors+="W-CIRC-0004: OBSTYPE no valido para calibracion.;";
			}else{
				if(this.getExptime()==null || this.getExptime()<=0) errors+="E-CIRC-0006: CAL with EXPTIME not present or 0.;";
				if(!this.getObsMode().equalsIgnoreCase("CALIB"))errors+="W-CIRC-0003: OBSCLASS no valido para calibracion.;";
				if(this.getObstype().equalsIgnoreCase("BROADBAND_IMAGE") || this.getObstype().equalsIgnoreCase("POLARIMETRY") || this.getObstype().equalsIgnoreCase("OTHER_TYPE"))errors+="W-CIRC-0004: OBSTYPE no valido para calibracion.;";
			}
			
			
		}else if (getModo().equalsIgnoreCase("POL")){

			//OBSMODE y CAMMODE deberían de ser POLARIMETRY??
			//if(this.getObsMode()!=null && !this.getObsMode().equalsIgnoreCase("Imaging")) errors+="E-CIRC-0001: OBSMODE y CAMMODE debe ser IMAGING.;";
			//if(this.getCammode()!=null && !this.getCammode().equalsIgnoreCase("Imaging")) errors+="E-CIRC-0001: OBSMODE y CAMMODE debe ser IMAGING.;";
			
			
			if(this.getRa()==null || this.getDe()==null) errors+="E-CIRC-0007: File with no coordenates";
			
			if(getSubmodo().contains("POL")){
				if(getGtcprgid()== null || getGtcprgid().equalsIgnoreCase("CALIB")){
					errors+="E-0019: GTCPRGID is equal to \"CALIB\".;";
				}
				if(this.getOpentime()==null || this.getClosetime()==null) errors+="E-CIRC-0004: No OPENTIME or CLOSETIME field.;";
				if(this.getExptime()==null || this.getExptime()<=0) errors+="E-CIRC-0003: EXPTIME not present or 0.;";
				if(!this.getObsMode().equalsIgnoreCase("SCIENCE"))errors+="W-CIRC-0001: OBSCLASS no valido para ciencia.;";
				if(!this.getObstype().equalsIgnoreCase("POLARIMETRY"))errors+="W-CIRC-0002: OBSTYPE no valido para ciencia.;";
				if(!(this.getHwprot()!=null && this.getGw()!=null && this.getLinslide()!=null && 
						this.getHwprot().toUpperCase().startsWith("POL") && this.getGw().equalsIgnoreCase("Wolly") && this.getLinslide().equalsIgnoreCase("Full_F_Imaging"))){
					errors+="E-CIRC-0009: Modo polarimetry con configuración incorrecta.;";
				}
				
			}else if(getSubmodo().equals("BIAS")){
				if(this.getExptime()!=null){
					if(this.getExptime()>0)	
						errors+="E-CIRC-0005: BIAS with EXPTIME>0;";
				}else if(this.getOpentime()!=null && this.getClosetime()!=null && !this.getOpentime().equals(this.getClosetime())){ 
							errors+="E-CIRC-0005: BIAS with EXPTIME>0;";
				}
				if(!this.getObsMode().equalsIgnoreCase("CALIB"))errors+="W-CIRC-0003: OBSCLASS no valido para calibracion.;";
				if(this.getObstype().equalsIgnoreCase("POLARIMETRY") || this.getObstype().equalsIgnoreCase("BROADBAND_IMAGE") || this.getObstype().equalsIgnoreCase("OTHER_TYPE"))errors+="W-CIRC-0004: OBSTYPE no valido para calibracion.;";
			}else{
				if(this.getExptime()==null || this.getExptime()<=0) errors+="E-CIRC-0006: CAL with EXPTIME not present or 0.;";
				if(!this.getObsMode().equalsIgnoreCase("CALIB"))errors+="W-CIRC-0003: OBSCLASS no valido para calibracion.;";
				if(this.getObstype().equalsIgnoreCase("POLARIMETRY") || this.getObstype().equalsIgnoreCase("BROADBAND_IMAGE") || this.getObstype().equalsIgnoreCase("OTHER_TYPE"))errors+="W-CIRC-0004: OBSTYPE no valido para calibracion.;";
			}
			
			
		
		}else{
		
			errors+="E-0018: Modo no válido;";
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
			filtros[i]=filtroAccess.selectByName("circe_filter");
			if(filtros[i]==null){
				
				/// Si no existe insertamos el filtro.
				Integer filId = filtroAccess.selectNewId();
				FiltroDb newFiltro = new FiltroDb();
				newFiltro.setFilId(filId);
				newFiltro.setFilShortname("CIRCE_FILTER");
				newFiltro.setFilName("CIRCE_FILTER");
				
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
		this.filters=(Filter[])aux.toArray(new Filter[0]);
		
		conf = confAccess.selectByFilters(det.getDetId(), submodo.getInsId(), submodo.getModId(), submodo.getSubmId(), this.filters);
		
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

	public String getCammode() {
		return cammode;
	}

	public void setCammode(String cammode) {
		this.cammode = cammode;
	}

	public String getObsclass() {
		return obsclass;
	}

	public void setObsclass(String obsclass) {
		this.obsclass = obsclass;
	}

	public String getObstype() {
		return obstype;
	}

	public void setObstype(String obstype) {
		this.obstype = obstype;
	}

	public String getCompstat() {
		return compstat;
	}

	public void setCompstat(String compstat) {
		this.compstat = compstat;
	}

	public String getGtcprgid(){
		return gtcprgid;
	}
	public void setGtcprgid(String gtcprgid){
		this.gtcprgid=gtcprgid;
	}

	public String getHwprot() {
		return hwprot;
	}

	public void setHwprot(String hwprot) {
		this.hwprot = hwprot;
	}

	public String getGw() {
		return gw;
	}

	public void setGw(String gw) {
		this.gw = gw;
	}

	public String getLinslide() {
		return linslide;
	}

	public void setLinslide(String linslide) {
		this.linslide = linslide;
	}
	

}
