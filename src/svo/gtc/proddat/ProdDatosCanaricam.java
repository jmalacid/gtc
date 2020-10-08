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

public class ProdDatosCanaricam extends ProdDatos{
	
	private String cammode = null; 
	private String obsclass = null;
	private String obstype = null;
	private String grating = null; 
	private String compstat = null;
	private String gtcprgid = null;
	private String hwp = null;
	private String 	slit = null;
	private String aperture = null;
	private Double wplate = null;
	private Filter[] filters = new Filter[0];

	public ProdDatosCanaricam(File fichero, Connection con){
		super(fichero);
		
		try {
			rellenaCamposFits();
		} catch (FitsException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// DETERMINAMOS EL TIPO DE FICHERO
		// Modos: Imaging, Polarimetric, Spectroscopy
		// 		Submodos IMG: Ciencia: IMG. Calibracion: BIAS, DARK, FLAT, STDS. Reducido: RED.  
		// 		Submodos POL: Ciencia: SPEC. Calibracion: BIAS, DARK, FLAT, STDS. Reducido: RED.
		// 		Submodos SPE: Ciencia: SPEC. Calibracion: STDS. Reducido: RED.
		
		
		// Determinamos el modo del Observing Block
		String modoAux = this.getOblock().getModo(con, "CC");

		// Fijamos el modo general y el submodo
		// if(modoAux!=null){
			setModo(modoAux);

			String dirPadre = fichero.getParentFile().getName();
			
			
			//Intentamos obtener el modo/submodo del fichero, si hay algún error se queda a null y guarda más adelante
			try{
			
			// Las comunes a todos los modos
			if(dirPadre.equalsIgnoreCase("STDS")){
				
				if(getModo().equals("IMG")){
					//setModo("IMG");
				
					//Fichero STD
					if(getCammode().equalsIgnoreCase("Imaging") && (getObsclass().equalsIgnoreCase("CALIB") || 
							getObstype().equalsIgnoreCase("STD") || getObstype().equalsIgnoreCase("SKY")) && // Se pone a mano, se deja como warning
							getSlit().equalsIgnoreCase("Open") && 
							getGrating().equalsIgnoreCase("Mirror")  && 
							getFilters().length>0){
						
						Boolean pasan=false;
						
						for(int i=0; i<this.getFilters().length; i++){
								if(this.getFilters()[i].getName().contains("Q1") || this.getFilters()[i].getName().contains("Si")){ 
									pasan=true;
								}
						}
						
						if(pasan==true){
							if(fichero.getName().contains("REDUCEDFROM")){
								setSubmodo("STDS_R");
							}else{
								setSubmodo("STDS");
							}
						}
						
					}
					
				}else if (getModo().equals("POL")){
					
					Boolean pasan = false;
					
					for(int i=0; i<this.getFilters().length; i++){
						if(this.getFilters()[i].getName().contains("Q1") || this.getFilters()[i].getName().contains("Si")){ 
							pasan=true;
						}
					}
					if(pasan ==true && getObsclass().equalsIgnoreCase("CALIB")){
						if(fichero.getName().contains("REDUCEDFROM")){
							setSubmodo("STDS_R");
						}else{
							setSubmodo("STDS");
						}
					}
				
				}else{
					if(fichero.getName().contains("REDUCEDFROM")){
						this.setSubmodo("STDS_R");
					}else{
						this.setSubmodo("STDS");
					}
				}
					/// Determinamos el submodo
				setModType("CAL");
			}else if(dirPadre.equalsIgnoreCase("OBJECT")){
				
				//if(fichero.getName().indexOf("Imaging")>0){
				if(getModo().equals("IMG")){
					//setModo("IMG");
				
					//Fichero ciencia
					if(getCammode().equalsIgnoreCase("Imaging") && (getObsclass().equalsIgnoreCase("SCIENCE") 
							|| getObstype().equalsIgnoreCase("OTHER_TYPE")) && // Se pone a mano, se deja como warning
							getSlit().equalsIgnoreCase("Open") && 
							getGrating().equalsIgnoreCase("Mirror")  
							//&& getCompstat().equalsIgnoreCase("COMPLETE") //Se pone como error independiente E-0020
							){
						if(fichero.getName().contains("REDUCEDFROM")){
							setSubmodo("IMG_R");
							setModType("SCI_R");
						}else{
							setSubmodo("IMG");
							setModType("SCI");
						}
						
					
					
					//Estrella estandar
				} else if(getCammode().equalsIgnoreCase("Imaging") && ((getObsclass().equalsIgnoreCase("CALIB") || getObsclass().equalsIgnoreCase("ACQ") ) || 
							getObstype().equalsIgnoreCase("STD")) && // Se pone a mano, se deja como warning
							getSlit().equalsIgnoreCase("Open") && 
							getGrating().equalsIgnoreCase("Mirror")  && 
							getFilters().length>0){
						
						
						if(fichero.getName().contains("REDUCEDFROM")){
							setSubmodo("SS_R");
							setModType("CAL");
						}else{
							setSubmodo("SS");
							setModType("CAL");
						}
						
						
						
					}
					
					
				//}else if(fichero.getName().indexOf("Spectroscopy")>0){
				}else if(getModo().equals("SPE")){
					//setModo("SPE");
					//Fichero AC
					if(getCammode().equalsIgnoreCase("Imaging") && 
							//(getObsclass().equalsIgnoreCase("ACQ") || getObstype().equalsIgnoreCase("OTHER_TYPE")) && 
							getSlit().equalsIgnoreCase("Open") && getGrating().equalsIgnoreCase("Mirror")){
						if(fichero.getName().contains("REDUCEDFROM")){
							setSubmodo("ACIMG_R");
						}else{
							setSubmodo("ACIMG");
						}
						setModType("AC");
					
					//Fichero Throught slit
					}else if(getCammode().equalsIgnoreCase("Imaging") && 
							//(getObsclass().equalsIgnoreCase("ACQ") || getObstype().equalsIgnoreCase("OTHER_TYPE")) && 
							getGrating().equalsIgnoreCase("Mirror") 
							//&& getCompstat().equalsIgnoreCase("COMPLETE")  //Se pone como error independiente E-0020
							&& !(getSlit().equalsIgnoreCase("Open"))){
						if(fichero.getName().contains("REDUCEDFROM")){
							setSubmodo("ACTS_R");
						}else{
							setSubmodo("ACTS");
						}
						setModType("AC");
					}else if(getCammode().equalsIgnoreCase("Spectroscopy") && !(getSlit().equalsIgnoreCase("Open")) && 
							(getGrating().equalsIgnoreCase("LowRes-10") || getGrating().equalsIgnoreCase("HighRes-10") || getGrating().equalsIgnoreCase("LowRes-20")) 
							){
						//Fichero ciencia
						if(getObstype().equalsIgnoreCase("OTHER_TYPE")){
							if(fichero.getName().contains("REDUCEDFROM")){
								setSubmodo("SPE_R");
								setModType("SCI_R");
							}else{
								setSubmodo("SPE");
								setModType("SCI");
							}
						//Estrella estandard
						}else if(getObsclass().equalsIgnoreCase("CALIB") || getObstype().equalsIgnoreCase("STD")){
							if(fichero.getName().contains("REDUCEDFROM")){
								setSubmodo("SS_R");
							}else{
								setSubmodo("SS");
							}
							setModType("CAL");
						}
						
						
						
					}
					/*//Fichero ciencia
					if(getCammode().equalsIgnoreCase("Spectroscopy") && getObsclass().equalsIgnoreCase("SCIENCE") && 
							//getObstype().equalsIgnoreCase("OTHER_TYPE") && 
							!(getSlit().equalsIgnoreCase("Open")) && 
							(getGrating().equalsIgnoreCase("LowRes-10") || getGrating().equalsIgnoreCase("HighRes-10")) 
							//&& getCompstat().equalsIgnoreCase("COMPLETE") //Se pone como error independiente E-0020
							){
						if(fichero.getName().contains("REDUCEDFROM")){
							setSubmodo("SPE_R");
							setModType("SCI_R");
						}else{
							setSubmodo("SPE");
							setModType("SCI");
						}
						
					}
					//Estrella estandar
					if(getCammode().equalsIgnoreCase("Spectroscopy") && getObsclass().equalsIgnoreCase("CALIB") && 
							//getObstype().equalsIgnoreCase("STD") && 
							!(getSlit().equalsIgnoreCase("Open")) && 
							(getGrating().equalsIgnoreCase("LowRes-10") || getGrating().equalsIgnoreCase("HighRes-10")) 
							//&& getCompstat().equalsIgnoreCase("COMPLETE") //Se pone como error independiente E-0020
							){
						if(fichero.getName().contains("REDUCEDFROM")){
							setSubmodo("SS_R");
						}else{
							setSubmodo("SS");
						}
						setModType("CAL");
					}*/
					
				}else if(getModo().equals("POL")){
					
					//setModo("POL");
					//Fichero AC
					if(getCammode().equalsIgnoreCase("Imaging")){
						if(fichero.getName().contains("REDUCEDFROM")){
							setSubmodo("ACIMG_R");
						}else{
							setSubmodo("ACIMG");
						}
						setModType("AC");
					}else if(getCammode().equalsIgnoreCase("Polarimetry")){
						if(getObsclass().equalsIgnoreCase("CALIB")){
							if(fichero.getName().contains("REDUCEDFROM")){
								setSubmodo("STDSI_R");
							}else{
								setSubmodo("STDSI");
							}
							setModType("CAL");
						
						//Fichero ciencia
						}else if(getObsclass().equalsIgnoreCase("SCIENCE")){
							if(fichero.getName().contains("REDUCEDFROM")){
								setSubmodo("POL_R");
								setModType("SCI_R");
							}else{
								setSubmodo("POL");
								setModType("SCI");
							}
						}else if(getObsclass().equalsIgnoreCase("ACQ")){
							if(fichero.getName().contains("REDUCEDFROM")){
								setSubmodo("ACIMG_R");
							}else{
								setSubmodo("ACIMG");
							}
							setModType("AC");
						}
					
						
					}else{
						System.out.println("No tiene submodo, obsclass es : "+getObsclass());
					}
					
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public ProdDatosCanaricam(ProdDatos prodDatos, Connection con){
		this(prodDatos.getFile(), con);
	}

	/**
	 * Recoge los valores que provienen de los keywords del FITS 
	 * espec�ficos de CANARICAM.
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

		// Filters
		Vector<Filter> aux = new Vector<Filter>();
		for(int i=1; i<10; i++){
			try{
				String fil = header.findCard("FILTER"+i).getValue().trim();
				if(fil!=null && fil.trim().length()>0 && fil.trim().toUpperCase()!="NULL"){
					Filter filtro = new Filter();
					filtro.setName(fil);
					filtro.setOrder(i);
					aux.add(filtro);
				}
			}catch(NullPointerException e){
				//break;
			}
		}
		if(aux.size()>0) this.filters=(Filter[])aux.toArray(new Filter[0]);
	
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
		
		// GRATING
		try{
			this.grating = header.findCard("GRATING").getValue().trim();
		}catch(NullPointerException e){}
		// COMPSTAT
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
		
		// HWP
		try{
			this.hwp = header.findCard("HWP").getValue().trim();
		}catch(NullPointerException e){}
		// SLIT
		try{
			this.slit = header.findCard("SLIT").getValue().trim();
		}catch(NullPointerException e){}
		// APERTURE
		try{
			this.aperture = header.findCard("APERTURE").getValue();
		}catch(NullPointerException e){}
		//WPLATE
		try{
			this.wplate = Double.valueOf(header.findCard("WPLATE").getValue());
		}catch(NullPointerException e){
		}
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
		
		
		if( getObsMode()!=null &&  
			!getObsMode().equalsIgnoreCase("Imaging") &&
			!getObsMode().equalsIgnoreCase("Polarimetry") && !getObsMode().equalsIgnoreCase("Spectroscopy")){
				errors+="E-CANARICAM-0003: Not recognized value of keyword OBSMODE: "+getObsMode()+";";
		}
		
		
			
		if( getModo()==null || getSubmodo()==null){
			if(getModo()=="SPE"){
				throw new GtcFileException("E-CANARICAM-0027: SPE sin submodo conocido ");
			}else if(getModo()=="IMG"){
				throw new GtcFileException("E-CANARICAM-0028: IMG sin submodo conocido ");
			}else if(getModo()=="POL"){
				throw new GtcFileException("E-CANARICAM-0029: POL sin submodo conocido ");
			}else{
				throw new GtcFileException("E-CANARICAM-0004: Modo desconocido ");
			}
		}

		if(!getCompstat().equalsIgnoreCase("COMPLETE")){
			errors+="E-0020: COMPSTAT not equal to \"COMPLETE\".;";
		}
		
		
		
		if(getModo().equalsIgnoreCase("IMG")){
			
			if(this.getSubmodo().contains("SS") || this.getSubmodo().contains("STD")){
				if(!this.getObstype().equalsIgnoreCase("STD")){
					 errors+="W-CANARICAM-0001: Standard Start with OBSTYPE not equal to \"STD\".;";
				}
			}else if(this.getSubmodo().contains("IMG")){
				if(!this.getObstype().equalsIgnoreCase("OTHER_TYPE")){
					 errors+="W-CANARICAM-0003: IMG with OBSTYPE not equal to \"OTHER_TYPE\".;";
				}
			}
			
			
			if(this.getCammode()!=null && !this.getCammode().equalsIgnoreCase("Imaging")) errors+="E-CANARICAM-0001: Imaging with CAMMODE not equal to \"Imaging\".;";
			if(this.getGrating()!=null && !this.getGrating().equalsIgnoreCase("Mirror")) errors+="E-CANARICAM-0005: Imaging with GRATING not equal to \"Mirror\".;";
			if(this.getHwp()!=null && !this.getHwp().equalsIgnoreCase("Open")) errors+="E-CANARICAM-0006: Imaging with HWP not equal to \"Open\".;";
			if(this.getSlit()!=null && !this.getSlit().equalsIgnoreCase("Open")) errors+="E-CANARICAM-0007: Imaging with SLIT not equal to \"Open\".;";
			
			if(this.getFilters()!=null){
				for(int i=0; i<this.getFilters().length; i++){
					if(this.getFilters()[i].getName().equalsIgnoreCase("GR")) errors+="E-CANARICAM-X: Filtro "+this.getFilters()[i].getName()+";";
				}
			}
			
			if(this.getRa()==null || this.getDe()==null) errors+="E-CANARICAM-0020: File with no coordenates";
			
			if(getSubmodo().contains("IMG")){
				if(getGtcprgid()== null || getGtcprgid().equalsIgnoreCase("CALIB")){
					errors+="E-0019: GTCPRGID is equal to \"CALIB\".;";
				}
				if(this.getOpentime()==null || this.getClosetime()==null) errors+="E-CANARICAM-0015: No OPENTIME or CLOSETIME field.;";
				if(this.getExptime()==null || this.getExptime()<=0) errors+="E-CANARICAM-0014: EXPTIME not present or 0.;";
			}else if(getSubmodo().contains("STD")){
				if(this.getOpentime()==null || this.getClosetime()==null) errors+="E-CANARICAM-0019: STDS with no OPENTIME or CLOSETIME field.;";
				if(this.getExptime()==null || this.getExptime()<=0) errors+="E-CANARICAM-0018: STDS with EXPTIME not present or 0.;";
			}else if(getSubmodo().equals("BIAS")){
				if(this.getExptime()!=null){
					if(this.getExptime()>0)	
						errors+="E-CANARICAM-0016: BIAS with EXPTIME>0;";
				}else if(this.getOpentime()!=null && this.getClosetime()!=null && !this.getOpentime().equals(this.getClosetime())){ 
							errors+="E-CANARICAM-0016: BIAS with EXPTIME>0;";
				}
			}else{
				if(this.getExptime()==null || this.getExptime()==0){
					errors+="E-CANARICAM-0017: no BIAS with EXPTIME = 0;";
				}
			}
			
			
		}else if(getModo().equalsIgnoreCase("POL")){
			if(this.getInstrument()!=null && !this.getInstrument().equalsIgnoreCase("CanariCam")) errors+="E-0003: Polarimetric with instrument not Canaricam;";
			
			
			//if(this.getCompstat()!=null && !this.getCompstat().equalsIgnoreCase("Complete")) errors+="E-CANARICAM-0025: Polarimetric with COMPSTAT not equal to \"Complete\";";//REPETIDO
			//if(this.getGrating()!=null && !this.getGrating().equalsIgnoreCase("Mirror")) errors+="E-CANARICAM-0009: Polarimetric with GRATING not equal to \"Mirror\".;";
			if(this.getHwp()==null) errors+="E-CANARICAM-0010: Polarimetric with HWP null.;";
			//if(this.getSlit()!=null && !this.getSlit().equalsIgnoreCase("Open")) errors+="E-CANARICAM-0011: Polarimetric with SLIT not equal to \"Open\".;";
			if(this.getWplate()!=null && !(this.getWplate()==0 ||this.getWplate()==45 ||this.getWplate()==22.5 ||this.getWplate()==67.5)){
				errors+="E-CANARICAM-0026: Polarimetric with WPLATE not equal to 0,45,22.5,67.5;";
			}
			
			if(getSubmodo().equalsIgnoreCase("ACIMG") || getSubmodo().equalsIgnoreCase("ACIMG_R")){
				//if(this.getCammode()!=null && !this.getCammode().equalsIgnoreCase("Imaging")) errors+="E-CANARICAM-0002: Polarimetric-AC with CAMMODE not equal to \"Imaging\".;";
			}else if(getSubmodo().equalsIgnoreCase("STDS") || getSubmodo().equalsIgnoreCase("STDS_R")){
				//En este caso no tenemos condiciones cammode ni aperture, cammode=imaging??
			}else {
				if(this.getCammode()!=null && !this.getCammode().equalsIgnoreCase("Polarimetry")) errors+="E-CANARICAM-0002: Polarimetric with CAMMODE not equal to \"Polarimetric\".;";
				if(this.getAperture()!=null && !this.getAperture().startsWith("Polm")) errors+="E-CANARICAM-0008: Polarimetric with APERTURE not start with \"Polm\".;";
			}
			/*if(this.getFilters()!=null){
				for(int i=0; i<this.getFilters().length; i++){
					if(this.getFilters()[i].getName().equalsIgnoreCase("GR")) errors+="E-CANARICAM-X: Filtro "+this.getFilters()[i].getName()+";";
				}
			}*/
		
			if(this.getRa()==null || this.getDe()==null) errors+="E-CANARICAM-0020: File with no coordenates";
			if(getSubmodo().contains("POL")){
				if(getGtcprgid()==null || getGtcprgid().equalsIgnoreCase("CALIB")){
					errors+="E-0019: GTCPRGID is equal to \"CALIB\".;";
				}
				if(this.getOpentime()==null || this.getClosetime()==null) errors+="E-CANARICAM-0015: No OPENTIME or CLOSETIME field.;";
				if(this.getExptime()==null || this.getExptime()<=0) errors+="E-CANARICAM-0014: EXPTIME not present or 0.;";
			}else if(getSubmodo().contains("STD")){
				if(this.getOpentime()==null || this.getClosetime()==null) errors+="E-CANARICAM-0019: STDS with no OPENTIME or CLOSETIME field.;";
				if(this.getExptime()==null || this.getExptime()<=0) errors+="E-CANARICAM-0018: STDS with EXPTIME not present or 0.;";
			}else if(getSubmodo().equals("BIAS")){
				if(this.getExptime()!=null){
					if(this.getExptime()>0)	
						errors+="E-CANARICAM-0016: BIAS with EXPTIME>0;";
				}else if(this.getOpentime()!=null && this.getClosetime()!=null && !this.getOpentime().equals(this.getClosetime())){ 
							errors+="E-CANARICAM-0016: BIAS with EXPTIME>0;";
				}
			}else{
				if(this.getExptime()==null || this.getExptime()==0){
					errors+="E-CANARICAM-0017: no BIAS with EXPTIME = 0;";
				}
			}
			
		}else if(getModo().equalsIgnoreCase("SPE")){
			
			//if(this.getCammode()!=null && !this.getCammode().equalsIgnoreCase("Spectroscopy")) errors+="E-CANARICAM-0021: Spectroscopy with CAMMODE not equal to \"Spectroscopy\".;";
			//if(this.getGrating()!=null && this.getGrating().equalsIgnoreCase("Mirror")) errors+="E-CANARICAM-0022: Spectroscopy with GRATING equal to \"Mirror\".;";
			//if(this.getSlit()!=null && this.getSlit().equalsIgnoreCase("Open")) errors+="E-CANARICAM-0023: Spectroscopy with SLIT equal to \"Open\".;";
			if(this.getSubmodo().contains("SS")){
				if(!this.getObstype().equalsIgnoreCase("STD")){
					 errors+="W-CANARICAM-0001: Standard Start with OBSTYPE not equal to \"STD\".;";
				}
			}else if(this.getSubmodo().contains("AC") || this.getSubmodo().contains("SPE")){
				if(!this.getObstype().equalsIgnoreCase("OTHER_TYPE")){
					 errors+="W-CANARICAM-0002: SPEC with OBSTYPE not equal to \"OTHER_TYPE\".;";
				}
			}
			
			
			if(this.getFilters()!=null){
				for(int i=0; i<this.getFilters().length; i++){
					if(this.getFilters()[i].getName().equalsIgnoreCase("GR")) errors+="E-CANARICAM-X: Filtro "+this.getFilters()[i].getName()+";";
				}
			}
			
			if(this.getRa()==null || this.getDe()==null) errors+="E-CANARICAM-0020: File with no coordenates";
			
			if(getSubmodo().equals("SPE") || getSubmodo().equals("SPE_R")){
				if(getGtcprgid()==null || getGtcprgid().equalsIgnoreCase("CALIB")){
					errors+="E-0019: GTCPRGID is equal to \"CALIB\".;";
				}
				if(this.getOpentime()==null || this.getClosetime()==null) errors+="E-CANARICAM-0015: No OPENTIME or CLOSETIME field.;";
				if(this.getExptime()==null || this.getExptime()<=0) errors+="E-CANARICAM-0014: EXPTIME not present or 0.;";
			}else if(getSubmodo().equals("STD") || getSubmodo().equals("STD_R")){
				if(this.getOpentime()==null || this.getClosetime()==null) errors+="E-CANARICAM-0019: STDS with no OPENTIME or CLOSETIME field.;";
				if(this.getExptime()==null || this.getExptime()<=0) errors+="E-CANARICAM-0018: STDS with EXPTIME not present or 0.;";
			}else{
				if(this.getExptime()==null || this.getExptime()==0){
					errors+="E-CANARICAM-0017: no BIAS with EXPTIME = 0;";
				}
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
		
		filtros = new FiltroDb[this.getFilters().length];
		
		for(int i=0; i<filtros.length; i++){
			filtros[i]=filtroAccess.selectByName(this.getFilters()[i].getName().trim());
			if(filtros[i]==null){
				/// Si no existe insertamos el filtro.
				Integer filId = filtroAccess.selectNewId();
				FiltroDb newFiltro = new FiltroDb();
				newFiltro.setFilId(filId);
				newFiltro.setFilShortname(this.getFilters()[i].getName().trim().toUpperCase());
				newFiltro.setFilName(this.getFilters()[i].getName().trim().toUpperCase());
				
				filtroAccess.insert(newFiltro);
				filtros[i]=newFiltro;
				//throw new IngestionException("INGESTION ERROR: Filter "+this.getFilters()[i]+" not found in the database.");
			}
		}
		
		conf = confAccess.selectByFilters(det.getDetId(), submodo.getInsId(), submodo.getModId(), submodo.getSubmId(), this.getFilters());
		
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
	
	public String getAperture() {
		return aperture;
	}

	public void setAperture(String aperture) {
		this.aperture = aperture;
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

	public String getGrating() {
		return grating;
	}

	public void setGrating(String grating) {
		this.grating = grating;
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
	public String getHwp() {
		return hwp;
	}

	public void setHwp(String hwp) {
		this.hwp = hwp;
	}

	public String getSlit() {
		return slit;
	}

	public void setSlit(String slit) {
		this.slit = slit;
	}

	public Filter[] getFilters() {
		return filters;
	}

	public void setFilters(Filter[] filters) {
		this.filters = filters;
	}

	public Double getWplate() {
		return wplate;
	}

	public void setWplate(Double wplate) {
		this.wplate = wplate;
	}

}
