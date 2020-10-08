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
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import svo.gtc.db.DriverBD;
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
import svo.gtc.ingestion.IngestionException;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.fits.PaddingException;

public class ProdDatosOsiris extends ProdDatos {

	private String grism = null;
	private String maskname = null;
	//private Double slitw = null;
	private Filter[] filters = new Filter[0];

	private String tfid = null;

	private String compstat = null;
	private String gtcprgid = null;
	
	private String dirPadre = null;

	public ProdDatosOsiris(File fichero, Connection con) {
		super(fichero);

		dirPadre = fichero.getParentFile().getName();
		//Si los ficheros BIAS están en object los movemos
		if(dirPadre.equalsIgnoreCase("OBJECT") && fichero.getName().contains("Bias")){
			fichero= moveBias(fichero);
			dirPadre = fichero.getParentFile().getName();
			//System.out.println("Ha cambiado: "+dirPadre);
		}
		
		try {
			rellenaCamposFits();
		} catch (FitsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// DETERMINAMOS EL TIPO DE FICHERO
		// Modos: BroadBandImage (BBI), LongSlitSpectroscopy (LSS),
		// TunableFilters (TF)
		// Submodos BBI: Ciencia: IMG. Calibracion: BIAS, DARK, FLAT, STDS.
		// Submodos LSS: Ciencia: SPEC, ACIMG. Calibracion: ARC, BIAS, DARK,
		// FLAT, STDS.
		// Submodos TF: Ciencia: SPEC. Calibracion: BIAS, DARK, FLAT, STDS.

		// Determinamos el modo del Observing Block
		String modoAux = this.getOblock().getModo(con, "OSI");
		// Fijamos el modo general y el submodo
		
		if (modoAux != null) {
			
			setModo(modoAux);

			/// Determinamos el submodo
			setModType("CAL");

			// Las comunes a todos los modos
			if (dirPadre.equalsIgnoreCase("BIAS")) {
				this.setSubmodo("BIAS");
			} else if (dirPadre.equalsIgnoreCase("DARK")) {
				this.setSubmodo("DARK");
			} else if (dirPadre.equalsIgnoreCase("FLAT")) {
				this.setSubmodo("FLAT");
			} else if (dirPadre.equalsIgnoreCase("STDS")) {
				this.setSubmodo("STDS");
			} else if (dirPadre.equalsIgnoreCase("ARC")) {
				setSubmodo("ARC");
			}

			//Modo dentro del instrumento HORuS
			if (getModo().equals("HORUS")){
				setModo("SPE");
				setInstrument("HORuS");
				if(dirPadre.equalsIgnoreCase("OBJECT")) {
					setSubmodo("ACOSI");
					setModType("AC");
				}else if(dirPadre.equalsIgnoreCase("STDS")){
					setSubmodo("STDS");
					setModType("CAL");
				}
			}
			
			// Específicas de BBI
			if (getModo().equals("BBI") && dirPadre.equalsIgnoreCase("OBJECT")) {
				setSubmodo("IMG");
				setModType("SCI");
			}

			// Específicas de LSS
			if (getModo().equals("LSS")) {
				if (dirPadre.equalsIgnoreCase("OBJECT")) {

					if ((getGrism() == null || getGrism().equalsIgnoreCase("OPEN"))
							&& (getMaskname() == null || getMaskname().equalsIgnoreCase("NOMASK")
									|| getMaskname().equalsIgnoreCase("NULL"))) {
						setSubmodo("FLDIMG");
						setModType("AC");

					} else if ((getGrism() == null || getGrism().equalsIgnoreCase("OPEN")) && getMaskname() != null
							&& !getMaskname().equalsIgnoreCase("NOMASK") && !getMaskname().equalsIgnoreCase("NULL")) {
						setSubmodo("ACIMG");
						setModType("AC");

					} else if (getGrism() != null && !getGrism().equalsIgnoreCase("OPEN") && getMaskname() != null
							&& !getMaskname().equalsIgnoreCase("NOMASK") && !getMaskname().equalsIgnoreCase("NULL")) {
						setSubmodo("SPEC");
						setModType("SCI");
					}
				}
			}

			// Específicas de TF
			if (getModo().equals("TF")) {
				if (dirPadre.equalsIgnoreCase("OBJECT")) {
					setSubmodo("IMG");
					setModType("SCI");
				}
			}

			// Específicas de MOS
			if (getModo().equals("MOS")) {
				if (dirPadre.equalsIgnoreCase("OBJECT")) {

					if ((getGrism() == null || getGrism().equalsIgnoreCase("OPEN")) && (getMaskname() == null
							|| getMaskname().equalsIgnoreCase("NOMASK") || getMaskname().equalsIgnoreCase("NULL"))) {
						setSubmodo("FLDIMG");
						setModType("AC");

					} else if ((getGrism() == null || getGrism().equalsIgnoreCase("OPEN")) && getMaskname() != null
							&& !getMaskname().equalsIgnoreCase("NULL") && !getMaskname().equalsIgnoreCase("NOMASK")) {
						setSubmodo("ACIMG");
						setModType("AC");

					} else if (getGrism() != null && !getGrism().equalsIgnoreCase("OPEN") && getMaskname() != null
							&& getMaskname().contains("GTC") && !getMaskname().equalsIgnoreCase("NOMASK") && !getMaskname().equalsIgnoreCase("NULL")) {
						setSubmodo("SPEC");
						setModType("SCI");
					}
				}
			}
		}

	}

	public ProdDatosOsiris(ProdDatos prodDatos, Connection con) {
		this(prodDatos.getFile(), con);
	}

	/**
	 * Recoge los valores que provienen de los keywords del FITS espec�ficos de
	 * OSIRIS.
	 * 
	 * @throws FitsException
	 * @throws IOException
	 */
	private void rellenaCamposFits() throws FitsException, IOException {
		boolean compressed = false;

		if (this.getFile().getName().toUpperCase().endsWith(".GZ")) {
			compressed = true;
		}

		Fits fEntrada = new Fits(this.getFile(), compressed);

		BasicHDU hdu = fEntrada.getHDU(0);
		Header header = hdu.getHeader();

		// GRISM
		try {
			this.grism = header.findCard("GRISM").getValue().trim();
		} catch (NullPointerException e) {
		}

		// MASKNAME
		try {
			this.maskname = header.findCard("MASKNAME").getValue().trim();
		} catch (NullPointerException e) {
		}

		/*// SLITW
		try {
			this.slitw = new Double(header.findCard("SLITW").getValue());
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (NullPointerException e) {
		}*/

		// Filters
		Vector<Filter> aux = new Vector<Filter>();
		for (int i = 1; i < 10; i++) {
			try {
				String fil = header.findCard("FILTER" + i).getValue().trim();
				if (fil != null && fil.trim().length() > 0 && fil.trim().toUpperCase() != "NULL") {
					Filter filtro = new Filter();
					filtro.setName(fil);
					filtro.setOrder(i);
					aux.add(filtro);
				}
			} catch (NullPointerException e) {
				// break;
			}
		}
		if (aux.size() > 0)
			this.filters = (Filter[]) aux.toArray(new Filter[0]);

		// TFID
		try {
			this.tfid = header.findCard("TFID").getValue().trim();
		} catch (NullPointerException e) {
		}

		// COMPSTAT
		try {
			this.compstat = header.findCard("COMPSTAT").getValue().trim();
		} catch (NullPointerException e) {
		}
		// gtcprgid
		try {
			this.gtcprgid = header.findCard("GTCPRGID").getValue().trim();
		
		} catch (NullPointerException e) {
			
			try {
				this.gtcprgid = header.findCard("GTCPROGI").getValue().trim();
			}catch (NullPointerException e1) {
				//e.printStackTrace();
			}
		}
	}

	/**
	 * A�ade a las pruebas generales las espec�ficas de Osiris
	 */
	public void test(Connection con) throws GtcFileException {
		String err = "";
		try {
			super.test(con);
		} catch (GtcFileException e) {
			err += e.getMessage();
			if (!err.trim().endsWith(";")) {
				err += "; ";
			}
		}

		try {
			testSize();
		} catch (GtcFileException e) {
			err += e.getMessage() + "; ";
		}catch (Exception e){
			//Error no GTC
		}
		
		try {
			testModo();
		} catch (GtcFileException e) {
			err += e.getMessage() + "; ";
		}

		if (err.length() > 0) {
			throw new GtcFileException(err);
		}
	}

	
	public void testSize() throws GtcFileException, FitsException, IOException {
		
		//Error tamaño, error si tienes menos de 1024kB (1MB)
		if(this.getFile().length()<1024000){
			
			boolean compressed = false;

			if (this.getFile().getName().toUpperCase().endsWith(".GZ")) {
				compressed = true;
			}

			Fits fEntrada = new Fits(this.getFile(), compressed);

			BasicHDU hdu = fEntrada.getHDU(0);
			Header header = hdu.getHeader();
	
			//Buscamos los keywords relacionados
		    String rx,ry,r1x,r1y;
		    
			rx = header.findCard("ROI_X").getValue();
			ry = header.findCard("ROI_Y").getValue();
			r1x = header.findCard("ROI_1X").getValue();
			r1y = header.findCard("ROI_1Y").getValue();
			
			if(rx.equals("50") && ry.equals("50") && r1x.equals("0") && r1y.equals("0")){
			
				throw new GtcFileException("E-0022: Tamaño de fichero menor a 1MB ");
			}		
		}
	}
	
	/**
	 * Comprueba si el modo OBSMODE es uno de los conocidos.
	 * 
	 * @throws GtcFileException
	 */
	public void testModo() throws GtcFileException {
		String errors = "";

		if (getObsMode() != null && !getObsMode().equalsIgnoreCase("OsirisBroadBandImage")
				&& !getObsMode().equalsIgnoreCase("OsirisBias")
				&& !getObsMode().equalsIgnoreCase("OsirisTunableFilterImage")
				&& !getObsMode().equalsIgnoreCase("OsirisDomeFlat") && !getObsMode().equalsIgnoreCase("OsirisSkyFlat")
				&& !getObsMode().equalsIgnoreCase("OsirisLongSlitSpectroscopy")
				&& !getObsMode().equalsIgnoreCase("OsirisMOS")) {
			errors += "W-OSIRIS-0010: Not recognized value of keyword OBSMODE: " + getObsMode() + ";";
		}

		// Modificamos esto para diferenciar entre Modo null/submodo null o LSS
		// no submodo
		/*
		 * if( getModo()==null || getSubmodo()==null){ throw new
		 * GtcFileException(
		 * "E-OSIRIS-0015: Unable to determine type of product: "
		 * +getModo()+"/"+getSubmodo()); }
		 */
		if (getModo() == null) {
			if (dirPadre!=null && dirPadre.equalsIgnoreCase("OBJECT")){
				throw new GtcFileException("E-OSIRIS-0015: Fichero de ciencia sin modo conocido ");
			}else{
				throw new GtcFileException("E-OSIRIS-0089: Fichero de calibración sin modo conocido ");
			}
		} else if (getModo().trim().equalsIgnoreCase("LSS") && getSubmodo() == null) {
			throw new GtcFileException("E-OSIRIS-0080: LSS with no submodo recognized ");
		} else if (getModo().trim().equalsIgnoreCase("BBI") && getSubmodo() == null) {
			throw new GtcFileException("E-OSIRIS-0081: BBI with no submodo recognized ");
		} else if (getModo().trim().equalsIgnoreCase("TF") && getSubmodo() == null) {
			throw new GtcFileException("E-OSIRIS-0082: TF with no submodo recognized " );
		} else if (getModo().trim().equalsIgnoreCase("MOS") && getSubmodo() == null) {
			throw new GtcFileException("E-OSIRIS-0086: MOS with no submodo recognized ");
		}

		// if(!getCompstat().equalsIgnoreCase("COMPLETE")){
		// errors+="E-0020: COMPSTAT not equal to \"COMPLETE\".;";
		// }

		/*
		 * if(getModType().equals("CAL")){ if(this.getGrism()==null ||
		 * this.getGrism().equalsIgnoreCase("OPEN")) errors+=
		 * "E-OSIRIS-0004: Calibration file with GRISM=\"OPEN\".;";
		 * if(this.getMaskname()==null ||
		 * this.getMaskname().equalsIgnoreCase("NOMASK") ||
		 * this.getMaskname().equalsIgnoreCase("NULL")) errors+=
		 * "E-OSIRIS-0005: Calibration file with MASKNAME \"NOMASK\" or \"NULL\".;"
		 * ; if(this.getSlitw()==null || this.getSlitw().doubleValue()==0)
		 * errors+="E-OSIRIS-0006: Calibration file with SLITW=\"0\" or NULL.;";
		 * }
		 */

		if (getModo().equals("BBI")) {
			if (getModType().equals("SCI")) {
				if (this.getGrism() != null && !this.getGrism().equalsIgnoreCase("OPEN"))
					errors += "E-OSIRIS-0007: Science BroadBandImage with GRISM not equal to \"OPEN\".;";
				if (this.getMaskname() != null && !(this.getMaskname().equalsIgnoreCase("NOMASK")
						|| this.getMaskname().equalsIgnoreCase("NULL")) && !this.getFile().getName().contains("FrameTransfe"))
					errors += "E-OSIRIS-0008: Science BroadBandImage with MASKNAME not equal to \"NOMASK\" or \"NULL\".;";
				/*if (this.getSlitw() != null && this.getSlitw().doubleValue() != 0)
					errors += "E-OSIRIS-0009: Science BroadBandImage with SLITW not equal to \"0\";";*/
				if (this.getFilters() != null) {
					for (int i = 0; i < this.getFilters().length; i++) {
						if (this.getFilters()[i].getName().equalsIgnoreCase("GR"))
							errors += "E-OSIRIS-0010: Science BroadBandImage with FILTER equal to \"GR\";";
						if (!(this.getFilters()[i].getName().toUpperCase().startsWith("U")
								|| this.getFilters()[i].getName().toUpperCase().startsWith("F")
								|| this.getFilters()[i].getName().toUpperCase().startsWith("P")
								|| this.getFilters()[i].getName().toUpperCase().startsWith("SLOAN")
								|| this.getFilters()[i].getName().startsWith("OPEN"))) {
							errors += "E-OSIRIS-0079: Science BroadBandImage with FILTER not standard (SLOAN, Uxxx/xx, Fxxx/xx)";

						}
					}
				}
			}

			if (getSubmodo().equals("IMG")) {
				if (this.getGtcprgid() == null || this.getGtcprgid().equalsIgnoreCase("CALIB")) {
					errors += "E-0019: GTCPRGID is equal to \"CALIB\".;";
				}
				if (!getFile().getName().toUpperCase().contains("BROADBAND"))
					errors += "E-OSIRIS-0025: BBI/IMG does not contain \"OsirisBroadBandImage\" in its file name.;";
				if (this.getOpentime() == null || this.getClosetime() == null)
					errors += "E-OSIRIS-0040: BBI/IMG with no OPENTIME or CLOSETIME field.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0041: BBI/IMG with EXPTIME not present or 0.;";
			} else if (getSubmodo().equals("BIAS")) {
				if (!getFile().getName().toUpperCase().contains("OSIRISBIAS"))
					errors += "W-OSIRIS-0001: BBI/BIAS does not contain \"OsirisBias\" in its file name.;";
				if (this.getExptime() != null) {
					if (this.getExptime() > 0)
						errors += "E-OSIRIS-0042: BBI/BIAS with EXPTIME>0;";
				} else {
					if (this.getOpentime() != null) {
						if (this.getClosetime() != null && !this.getOpentime().equals(this.getClosetime()))
							errors += "E-OSIRIS-0042: BBI/BIAS with value in OPENTIME and CLOSTIME values differ.;";
					} else {
						if (this.getOpentime() == null && this.getClosetime() == null)
							errors += "E-OSIRIS-0042: BBI/BIAS with no values in EXPTIME, OPENTIME, CLOSTIME or READTIME;";
					}
				}
			} else if (getSubmodo().equals("DARK")) {
				if (!getFile().getName().toUpperCase().contains("OSIRISBIAS"))
					errors += "W-OSIRIS-0002: BBI/DARK does not contain \"OsirisBias\" in its file name.;";
				// if(this.getOpentime()!=null || this.getClosetime()!=null)
				// errors+="E-OSIRIS-0043: BBI/DARK should not have OPENTIME or
				// CLOSETIME values.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0044: BBI/DARK with EXPTIME not present or 0.;";
			} else if (getSubmodo().equals("FLAT")) {
				if (!getFile().getName().toUpperCase().contains("OSIRISDOMEFLAT")
						&& !getFile().getName().toUpperCase().contains("OSIRISSKYFLAT"))
					errors += "W-OSIRIS-0003: BBI/FLAT does not contain \"OsirisDomeFlat\" or \"OsirisSkyFlat\" in its file name.;";
				if (this.getObsMode() != null && !this.getObsMode().equalsIgnoreCase("OsirisDomeFlat")
						&& !this.getObsMode().equalsIgnoreCase("OsirisSkyFlat"))
					errors += "W-OSIRIS-0015: BBI/FLAT with OBSMODE not equal to \"OsirisDomeFlat\" or \"OsirisSkyFlat\".;";
				if (this.getOpentime() == null || this.getClosetime() == null)
					errors += "E-OSIRIS-0046: BBI/FLAT with no OPENTIME or CLOSETIME field.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0047: BBI/FLAT with EXPTIME not present or 0.;";
			} else if (getSubmodo().equals("STDS")) {
				if (!getFile().getName().toUpperCase().contains("BROADBAND")
						&& !getFile().getName().toUpperCase().contains("TUNABLEFILTER")
						&& !getFile().getName().toUpperCase().contains("LONGSLIT"))
					errors += "E-OSIRIS-0029: BBI/STDS does not contain \"OsirisBroadBandImage\", \"OsirisTunableFilterImage\" or \"OsirisLongSlitSpectroscopy\" in its file name.;";
				if (this.getOpentime() == null || this.getClosetime() == null)
					errors += "E-OSIRIS-0048: BBI/STDS with no OPENTIME or CLOSETIME field.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0049: BBI/STDS with EXPTIME not present or 0.;";
			}else if (getSubmodo().equals("ARC")) {
				if (this.getOpentime() == null || this.getClosetime() == null)
					errors += "E-OSIRIS-0048: ARC with no OPENTIME or CLOSETIME field.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0049: ARC with EXPTIME not present or 0.;";
			}
		} else if (getModo().equals("LSS")) {
			/*
			 * if(getModType().equals("SCI") &&
			 * !getFile().getName().contains("OsirisLongSlitSpectroscopy")){
			 * errors+=
			 * "E-OSIRIS-0030: LSS Science file does not contain \"OsirisLongSlitSpectroscopy\" in its file name.;"
			 * ; }
			 */
			if (getSubmodo().equals("SPEC")) {
				if (getGtcprgid() == null || getGtcprgid().equalsIgnoreCase("CALIB")) {
					errors += "E-0019: GTCPRGID is equal to \"CALIB\".;";
				}
				if (!(getFile().getName().toUpperCase().contains("LONGSLIT") || getFile().getName().toUpperCase().contains("MOS")))
					errors += "E-OSIRIS-0030: LSS/SPEC file does not contain \"OsirisLongSlitSpectroscopy\" in its file name.;";
				if (this.getGrism() == null || this.getGrism().equalsIgnoreCase("OPEN"))
					errors += "E-OSIRIS-0011: Science LSS Spectrum with GRISM equal to \"OPEN\" or NULL.;";
				if (this.getMaskname() == null || this.getMaskname().equalsIgnoreCase("NOMASK")
						|| this.getMaskname().equalsIgnoreCase("NULL"))
					errors += "E-OSIRIS-0012: Science LSS Spectrum with MASKNAME \"NOMASK\" or \"NULL\".;";
				/*if (this.getSlitw() == null || this.getSlitw().doubleValue() == 0)
					errors += "E-OSIRIS-0013: Science LSS Spectrum with SLITW=\"0\" or NULL.;";*/
				if (this.getOpentime() == null || this.getClosetime() == null)
					errors += "E-OSIRIS-0054: LSS/SPEC with no OPENTIME or CLOSETIME field.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0055: LSS/SPEC with EXPTIME not present or 0.;";
			}
			if (getSubmodo().equals("ACIMG")) {
				if (!getFile().getName().toUpperCase().contains("LONGSLIT")
						&& !getFile().getName().toUpperCase().contains("BROADBAND"))
					errors += "E-OSIRIS-0078: LSS/ACIMG file does not contain \"OsirisLongSlitSpectroscopy\" or \"OsirisBroadBandImage\" in its file name.;";
				if (this.getGrism() != null && !this.getGrism().equalsIgnoreCase("OPEN"))
					errors += "E-OSIRIS-0016: Science LSS Ac. Image with GRISM not equal to \"OPEN\" or NULL.;";
				if (this.getMaskname() == null || this.getMaskname().equalsIgnoreCase("NOMASK")
						|| this.getMaskname().equalsIgnoreCase("NULL"))
					errors += "E-OSIRIS-0017: Science LSS Ac. Image with MASKNAME \"NOMASK\" or \"NULL\".;";
				/*if (this.getSlitw() == null || this.getSlitw().doubleValue() == 0)
					errors += "E-OSIRIS-0018: Science LSS Ac. Image with SLITW=\"0\" or NULL.;";*/
				if (this.getOpentime() == null || this.getClosetime() == null)
					errors += "E-OSIRIS-0052: LSS/ACIMG with no OPENTIME or CLOSETIME field.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0053: LSS/ACIMG with EXPTIME not present or 0.;";
			}
			if (getSubmodo().equals("FLDIMG")) {
				if (!getFile().getName().toUpperCase().contains("LONGSLIT")
						&& !getFile().getName().toUpperCase().contains("BROADBAND"))
					errors += "E-OSIRIS-0077: LSS/FLDIMG file does not contain \"OsirisLongSlitSpectroscopy\" or \"OsirisBroadBandImage\" in its file name.;";
				if (this.getGrism() != null && !this.getGrism().equalsIgnoreCase("OPEN"))
					errors += "E-OSIRIS-0019: Science LSS Field Image with GRISM equal to \"OPEN\".;";
				if (this.getMaskname() != null && !this.getMaskname().equalsIgnoreCase("NOMASK")
						&& !this.getMaskname().equalsIgnoreCase("NULL"))
					errors += "E-OSIRIS-0020: Science LSS Field Image with MASKNAME not \"NOMASK\" nor \"NULL\".;";
				/*if (this.getSlitw() != null && this.getSlitw().doubleValue() != 0)
					errors += "E-OSIRIS-0021: LSS/FLDIMG with SLITW=\"0\" or NULL.;";*/
				if (this.getOpentime() == null || this.getClosetime() == null)
					errors += "E-OSIRIS-0050: LSS/FLDIMG with no OPENTIME or CLOSETIME field.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0051: LSS/FLDIMG with EXPTIME not present or 0.;";
			}

			if (getSubmodo().equals("ARC")) {
				if (this.getObject() != null && (!this.getObject().toUpperCase().contains("ARC")
						&& !this.getObject().toUpperCase().contains("CALIBRATION-LAMPS")))
					errors += "W-OSIRIS-0011: The file is of type ARC but the OBJECT keyword does not contain \"ARC\".;";
				if (this.getObsMode() != null && !this.getObsMode().equalsIgnoreCase("OsirisLongSlitSpectroscopy"))
					errors += "W-OSIRIS-0014: The file is of type ARC but the OBSMODE is not \"OsirisLongSlitSpectroscopy\".;";
				if ( this.getMaskname() == null || this.getMaskname().length() == 0
						|| this.getGrism() == null || this.getGrism().length() == 0)
					errors += "W-OSIRIS-0018: The file is of type ARC but one of the keywords MASKNAME or GRISM has no value.;";
				if (this.getOpentime() == null || this.getClosetime() == null)
					errors += "E-OSIRIS-0056: LSS/ARC with no OPENTIME or CLOSETIME field.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0057: LSS/ARC with EXPTIME not present or 0.;";
			} else if (getSubmodo().equals("BIAS")) {
				if (this.getExptime() != null) {
					if (this.getExptime() > 0)
						errors += "E-OSIRIS-0022: LSS/BIAS with EXPTIME>0;";
				} else if (this.getOpentime() != null && this.getClosetime() != null
						&& !this.getOpentime().equals(this.getClosetime())) {
					errors += "E-OSIRIS-0022: LSS/BIAS with value in OPENTIME and CLOSTIME values differ.;";
				}
				if (this.getExptime() == null && (this.getOpentime() == null || this.getClosetime() == null)) {
					errors += "W-OSIRIS-0016: LSS/BIAS with EXPTIME and one of OPENTIME or CLOSTIME with no value;";
				}
				if (!getFile().getName().toUpperCase().contains("OSIRISBIAS"))
					errors += "W-OSIRIS-0004: LSS/BIAS does not contain \"OsirisBias\" in its file name.;";
			}else if (getSubmodo().equals("DARK")) {
				if (!getFile().getName().toUpperCase().contains("OSIRISBIAS"))
					errors += "W-OSIRIS-0005: LSS/DARK does not contain \"OsirisBias\" in its file name.;";
				// if(this.getOpentime()!=null || this.getClosetime()!=null)
				// errors+="E-OSIRIS-0058: LSS/DARK should not have OPENTIME or
				// CLOSETIME values.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0059: LSS/DARK with EXPTIME not present or 0.;";
			}else if (getSubmodo().equals("FLAT")) {
				if (!getFile().getName().toUpperCase().contains("OSIRISDOMEFLAT")
						&& !getFile().getName().toUpperCase().contains("OSIRISSKYFLAT")
						&& !getFile().getName().toUpperCase().contains("LONGSLIT"))
					errors += "W-OSIRIS-0006: LSS/FLAT does not contain \"OsirisDomeFlat\", \"OsirisDomeFlat\" or \"OsirisLongSlit\" in its file name.;";
				if (this.getObsMode() != null && !this.getObsMode().equalsIgnoreCase("OsirisDomeFlat")
						&& !this.getObsMode().equalsIgnoreCase("OsirisSkyFlat")
						&& !this.getObsMode().equalsIgnoreCase("OsirisLongSlitSpectroscopy")
						&& !this.getObsMode().equalsIgnoreCase("OsirisSpectralFlat")
						&& !this.getObsMode().equalsIgnoreCase("OsirisCalibrationLamp"))
					errors += "E-OSIRIS-0061: LSS/FLAT with OBSMODE not equal to \"OsirisDomeFlat\", \"OsirisSkyFlat\" or \"OsirisLongSlitSpectroscopy\" or \"OsirisLongSpectralFlat\" or \"OsirisCalibrationLamp\".;";
				if (this.getOpentime() == null || this.getClosetime() == null)
					errors += "E-OSIRIS-0062: LSS/FLAT with no OPENTIME or CLOSETIME field.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0063: LSS/FLAT with EXPTIME not present or 0.;";
			}else if (getSubmodo().equals("STDS")) {
				if (!getFile().getName().toUpperCase().contains("BROADBAND")
						&& !getFile().getName().toUpperCase().contains("TUNABLEFILTER")
						&& !getFile().getName().toUpperCase().contains("LONGSLIT"))
					errors += "E-OSIRIS-0034: LSS/STDS does not contain \"OsirisBroadBandImage\", \"OsirisTunableFilterImage\" or \"OsirisLongSlitSpectroscopy\" in its file name.;";
				if (this.getOpentime() == null || this.getClosetime() == null)
					errors += "E-OSIRIS-0074: LSS/STDS with no OPENTIME or CLOSETIME field.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0075: LSS/STDS with EXPTIME not present or 0.;";
			}else if (getSubmodo().equals("ARC")) {
				if (this.getOpentime() == null || this.getClosetime() == null)
					errors += "E-OSIRIS-0083: ARC with no OPENTIME or CLOSETIME field.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0085: ARC with EXPTIME not present or 0.;";
			}

		} else if (getModo().equals("TF")) {
			if (getSubmodo().equals("IMG")) {
				if (this.getGtcprgid() == null || this.getGtcprgid().equalsIgnoreCase("CALIB")) {
					errors += "E-0019: GTCPRGID is equal to \"CALIB\".;";
				}
				if (this.getMaskname() != null && !this.getMaskname().equalsIgnoreCase("NOMASK")
						&& this.getMaskname().equalsIgnoreCase("NULL"))
					errors += "W-OSIRIS-0012: Science TF Image with MASKNAME \"NOMASK\" or \"NULL\".;";
				/*if (this.getSlitw() != null && this.getSlitw().doubleValue() != 0)
					errors += "W-OSIRIS-0013: Science TF Image with SLITW=\"0\" or NULL.;";*/
				if (this.getOpentime() == null || this.getClosetime() == null)
					errors += "E-OSIRIS-0064: TF/IMG with no OPENTIME or CLOSETIME field.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0065: TF/IMG with EXPTIME not present or 0.;";
			}

			if (getSubmodo().equals("BIAS")) {
				if (!getFile().getName().toUpperCase().contains("OSIRISBIAS"))
					errors += "W-OSIRIS-0007: TF/BIAS does not contain \"OsirisBias\" in its file name.;";
				if (this.getExptime() != null) {
					if (this.getExptime() > 0)
						errors += "E-OSIRIS-0068: TF/BIAS with EXPTIME>0;";
				} else {
					if (this.getOpentime() != null) {
						if (this.getClosetime() != null && !this.getOpentime().equals(this.getClosetime()))
							errors += "E-OSIRIS-0068: TF/BIAS with value in OPENTIME and CLOSTIME values differ.;";
					} else {
						if (this.getOpentime() == null && this.getClosetime() == null)
							errors += "E-OSIRIS-0068: TF/BIAS with no values in EXPTIME, OPENTIME, CLOSTIME or READTIME;";
					}
				}
			}else if(getSubmodo().equals("DARK")) {
				if (!getFile().getName().toUpperCase().contains("OSIRISBIAS"))
					errors += "W-OSIRIS-0008: TF/DARK does not contain \"OsirisBias\" in its file name.;";
				// if(this.getOpentime()!=null || this.getClosetime()!=null)
				// errors+="E-OSIRIS-0066: TF/DARK should not have OPENTIME or
				// CLOSETIME values.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0067: TF/DARK with EXPTIME not present or 0.;";
			}else if (getSubmodo().equals("FLAT")) {
				if (!getFile().getName().toUpperCase().contains("OSIRISDOMEFLAT")
						&& !getFile().getName().toUpperCase().contains("OSIRISSKYFLAT")
						&& !getFile().getName().toUpperCase().contains("OSIRISTUNABLEFILTERIMAGE"))
					errors += "W-OSIRIS-0009: TF/FLAT does not contain \"OsirisDomeFlat\", \"OsirisSkyFlat\" or \"OsirisTunableFilterImage\" in its file name.;";
				if (this.getObsMode() != null && !this.getObsMode().equalsIgnoreCase("OsirisDomeFlat")
						&& !this.getObsMode().equalsIgnoreCase("OsirisSkyFlat")
						&& !this.getObsMode().equalsIgnoreCase("OsirisLongSlitSpectroscopy")
						&& !this.getObsMode().equalsIgnoreCase("OsirisTunableFilterImage"))
					errors += "W-OSIRIS-0017: TF/FLAT with OBSMODE not equal to \"OsirisDomeFlat\", \"OsirisSkyFlat\" or \"OsirisLongSlitSpectroscopy\".;";
				if (this.getOpentime() == null || this.getClosetime() == null)
					errors += "E-OSIRIS-0070: TF/FLAT with no OPENTIME or CLOSETIME field.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0071: LSS/FLAT with EXPTIME not present or 0.;";
			}else if (getSubmodo().equals("STDS")) {
				if (!getFile().getName().toUpperCase().contains("LONGSLIT")
						&& !getFile().getName().toUpperCase().contains("TUNABLEFILTER")
						&& !getFile().getName().toUpperCase().contains("BROADBAND"))
					errors += "E-OSIRIS-0039: TF/STDS does not contain \"OsirisLongSlitSpectroscopy\", \"OsirisTunableFilterImage\" or  \"OsirisBroadBandImage\" in its file name.;";
				if (this.getOpentime() == null || this.getClosetime() == null)
					errors += "E-OSIRIS-0072: TF/STDS with no OPENTIME or CLOSETIME field.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0073: TF/STDS with EXPTIME not present or 0.;";
			}else if (getSubmodo().equals("ARC")) {
				if (this.getOpentime() == null || this.getClosetime() == null)
					errors += "E-OSIRIS-0072: ARC with no OPENTIME or CLOSETIME field.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0073: ARC with EXPTIME not present or 0.;";
			}

			if (getModType().equals("SCI") && !getFile().getName().toUpperCase().contains("BROADBAND")
					&& !getFile().getName().toUpperCase().contains("TUNABLEFILTER")) {
				errors += "E-OSIRIS-0035: TF Science file does not contain \"OsirisBroadBandImage\" in its file name.;";
			}
		} else if (getModo().equals("MOS")) {

			if (getSubmodo().equals("SPEC")) {
				if (getGtcprgid() == null || getGtcprgid().equalsIgnoreCase("CALIB")) {
					errors += "E-0019: GTCPRGID is equal to \"CALIB\".;";
				}
				if (!getFile().getName().toUpperCase().contains("OSIRISMOS"))
					errors += "E-OSIRIS-0087: MOS/SPEC file does not contain \"OsirisMOS\" in its file name.;";
				if (this.getOpentime() == null || this.getClosetime() == null)
					errors += "E-OSIRIS-0083: MOS/SPEC with no OPENTIME or CLOSETIME field.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0085: MOS/SPEC with EXPTIME not present or 0.;";
			}
			if (getSubmodo().equals("ACIMG")) {
				// if(!getFile().getName().toUpperCase().contains("OSIRISMOS")
				// &&
				// !getFile().getName().toUpperCase().contains("OSIRISBROADBANDIMAGE"))
				// errors+="E-OSIRIS-0078: LSS/ACIMG file does not contain
				// \"OsirisLongSlitSpectroscopy\" or \"OsirisBroadBandImage\" in
				// its file name.;";
				if (this.getOpentime() == null || this.getClosetime() == null)
					errors += "E-OSIRIS-0083: MOS/ACIMG with no OPENTIME or CLOSETIME field.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0085: MOS/ACIMG with EXPTIME not present or 0.;";
			}
			if (getSubmodo().equals("FLDIMG")) {
				// if(!getFile().getName().toUpperCase().contains("OSIRISLONGSLITSPECTROSCOPY")
				// &&
				// !getFile().getName().toUpperCase().contains("OSIRISBROADBANDIMAGE"))
				// errors+="E-OSIRIS-0077: LSS/FLDIMG file does not contain
				// \"OsirisLongSlitSpectroscopy\" or \"OsirisBroadBandImage\" in
				// its file name.;";
				if (this.getOpentime() == null || this.getClosetime() == null)
					errors += "E-OSIRIS-0083: MOS/FLDIMG with no OPENTIME or CLOSETIME field.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0085: MOS/FLDIMG with EXPTIME not present or 0.;";
			}

			if (getSubmodo().equals("ARC")) {
				if (this.getOpentime() == null || this.getClosetime() == null)
					errors += "E-OSIRIS-0083: ARC with no OPENTIME or CLOSETIME field.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0085: ARC with EXPTIME not present or 0.;";
			}

			if (getSubmodo().equals("BIAS")) {
				if (this.getExptime() != null) {
					if (this.getExptime() > 0)
						errors += "E-OSIRIS-0088: MOS/BIAS with EXPTIME>0;";
				} else if (this.getOpentime() != null && this.getClosetime() != null
						&& !this.getOpentime().equals(this.getClosetime())) {
					errors += "E-OSIRIS-0088: MOS/BIAS with value in OPENTIME and CLOSTIME values differ.;";
				}
			}

			if (getSubmodo().equals("DARK")) {
				if (this.getOpentime() != null || this.getClosetime() != null)
					errors += "E-OSIRIS-0083: MOS/DARK should not have OPENTIME or CLOSETIME values.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0085: MOS/DARK with EXPTIME not present or 0.;";
			}

			if (getSubmodo().equals("FLAT")) {
				if (this.getOpentime() == null || this.getClosetime() == null)
					errors += "E-OSIRIS-0083: MOS/FLAT with no OPENTIME or CLOSETIME field.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0085: MOS/FLAT with EXPTIME not present or 0.;";
			}

			if (getSubmodo().equals("STDS")) {
				if (this.getOpentime() == null || this.getClosetime() == null)
					errors += "E-OSIRIS-0083: MOS/STDS with no OPENTIME or CLOSETIME field.;";
				if (this.getExptime() == null || this.getExptime() <= 0)
					errors += "E-OSIRIS-0085: MOS/STDS with EXPTIME not present or 0.;";
			}
		}

		if (getModType().equals("SCI") && (getRa() == null || this.getDe() == null)) {
			errors += "E-OSIRIS-0076: Science file with no coordinates.;";
		}

		if (errors.length() > 0) {
			throw new GtcFileException(errors);
		}

	}

	/**
	 * Obtiene una configuración de observación de la base de datos.
	 * 
	 */
	public ConfDb getConf(Connection con) throws SQLException, IngestionException {
		DetectorAccess detectorAccess = new DetectorAccess(con);
		InstrumentoAccess instAccess = new InstrumentoAccess(con);
		SubmodoAccess submodoAccess = new SubmodoAccess(con);
		ConfAccess confAccess = new ConfAccess(con);
		ConfFiltroAccess confFiltroAccess = new ConfFiltroAccess(con);
		FiltroAccess filtroAccess = new FiltroAccess(con);

		DetectorDb det = null;
		InstrumentoDb inst = null;
		SubmodoDb submodo = null;
		FiltroDb[] filtros = null;
		ConfDb[] conf = null;

		// Buscamos el detector, instrumento y el modo
		det = detectorAccess.selectByShortName(this.getDetector());
		if (det == null)
			throw new IngestionException("INGESTION ERROR: Detector " + getDetector() + " not found in the database.");

		inst = instAccess.selectByName(this.getInstrument());
		if (inst == null)
			throw new IngestionException(
					"INGESTION ERROR: Instrument " + getInstrument() + " not found in the database.");

		submodo = submodoAccess.selectById(inst.getInsId(), getModo(), getSubmodo());
		if (submodo == null)
			throw new IngestionException(
					"INGESTION ERROR: Mode " + getModo() + "/" + getSubmodo() + " not found in the database.");

		filtros = new FiltroDb[this.getFilters().length];

		for (int i = 0; i < filtros.length; i++) {
			filtros[i] = filtroAccess.selectByName(this.getFilters()[i].getName().trim());
			if (filtros[i] == null) {
				/// Si no existe insertamos el filtro.
				Integer filId = filtroAccess.selectNewId();
				FiltroDb newFiltro = new FiltroDb();
				newFiltro.setFilId(filId);
				newFiltro.setFilShortname(this.getFilters()[i].getName().trim().toUpperCase());
				newFiltro.setFilName(this.getFilters()[i].getName().trim().toUpperCase());

				filtroAccess.insert(newFiltro);
				filtros[i] = newFiltro;
				// throw new IngestionException("INGESTION ERROR: Filter
				// "+this.getFilters()[i]+" not found in the database.");
			}
		}

		
		conf = confAccess.selectByFilters(det.getDetId(), submodo.getInsId(), submodo.getModId(), submodo.getSubmId(),
				this.getFilters());

		// Si no existe la configuración, la creamos.
		if (conf.length < 1) {
			ConfDb newConf = new ConfDb();
			newConf.setDetId(det.getDetId());
			newConf.setInsId(submodo.getInsId());
			newConf.setModId(submodo.getModId());
			newConf.setSubmId(submodo.getSubmId());
			newConf = confAccess.insert(newConf);

			// Añadimos los filtros
			for (int i = 0; i < filtros.length; i++) {
				ConfFiltroDb newConfFiltro = new ConfFiltroDb();
				newConfFiltro.setFilId(filtros[i].getFilId());
				newConfFiltro.setDetId(newConf.getDetId());
				newConfFiltro.setInsId(newConf.getInsId());
				newConfFiltro.setModId(newConf.getModId());
				newConfFiltro.setSubmId(newConf.getSubmId());
				newConfFiltro.setConfId(newConf.getConfId());
				newConfFiltro.setCfilOrder(i + 1);
				confFiltroAccess.Insert(newConfFiltro);
			}
			return newConf;
		} else if (conf.length > 1) {
			throw new IngestionException("INGESTION ERROR: More than one configuration for the data product.");
		} else {
			return conf[0];
		}
	}

	public static void main(String args[]) throws GtcFileException {
		// Conexi�n con la base de datos
		DriverBD driver = new DriverBD();
		Connection con = null;
		try {
			con = driver.bdConexion();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		ProdDatosOsiris prod = new ProdDatosOsiris(
				new File(
						"/export/pcdisk6/raul/gtc/dataNew/GTC4-10BFLO/OB0017/object/0000097014-20110221-OSIRIS-OsirisTunableFilterImage.fits.gz"),
				con);
		// ProdDatosOsiris prod = new ProdDatosOsiris(new
		// File("/pcdisk/marconi5/raul/gtc/data/GTC9-09B/OB0018/bias/0000020269-20091123-OSIRIS-OsirisBias.fits.gz"),con);
		System.out.println("Filename " + prod.getFile().getName());
		System.out.println("Path 	" + prod.getPath());
		// System.out.println(prod.filename);
		System.out.println("Filesize " + prod.getFileSize().longValue() / 1024 / 1024 + " MB");
		System.out.println("Instr 	" + prod.getInstrument());
		System.out.println("Detec 	" + prod.getDetector());
		System.out.println("Mode 	" + prod.getModo());
		System.out.println("Program	" + prod.getProgram());
		System.out.println("Oblock 	" + prod.getOblock());
		System.out.println("RA  	" + prod.getRa().doubleValue());
		System.out.println("DEC 	" + prod.getDe().doubleValue());
		System.out.println("Date 	" + prod.getDate());
		System.out.println("OpenT 	" + prod.getOpentime());
		System.out.println("CloseT 	" + prod.getClosetime());
		// System.out.println("Expos. "+prod.getExptime()+"
		// ["+((double)(prod.getClosetime().getTime()-prod.getOpentime().getTime())/1000)+"]");
		System.out.println("Airmass	" + prod.getAirmass());
		System.out.println("Object 	" + prod.getObject());

		System.out.println("Grism 	" + prod.getGrism());
		System.out.println("Maskname " + prod.getMaskname());
		//System.out.println("Slitw 	" + prod.getSlitw());
		for (int i = 0; i < prod.getFilters().length; i++) {
			System.out.println("Filter" + (i + 1) + " " + prod.getFilters()[i]);
		}

		prod.test(con);
		// prod.testOblock();
		// prod.testProgram();

		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	////////////////////////////////////////////////////////////

	public String getGrism() {
		return grism;
	}

	public void setGrism(String grism) {
		this.grism = grism;
	}

	public String getMaskname() {
		return maskname;
	}

	public void setMaskname(String maskname) {
		this.maskname = maskname;
	}

	/*public Double getSlitw() {
		return slitw;
	}

	public void setSlitw(Double slitw) {
		this.slitw = slitw;
	}*/

	public Filter[] getFilters() {
		return filters;
	}

	public void setFilters(Filter[] filters) {
		this.filters = filters;
	}

	public String getTfid() {
		return tfid;
	}

	public void setTfid(String tfid) {
		this.tfid = tfid;
	}

	public String getCompstat() {
		return compstat;
	}

	public void setCompstat(String compstat) {
		this.compstat = compstat;
	}

	public String getGtcprgid() {
		return gtcprgid;
	}

	public void setGtcprgid(String gtcprgid) {
		this.gtcprgid = gtcprgid;
	}
	private File moveBias(File fichBias){

		//Fichero destino
		File dest = new File(fichBias.getAbsolutePath().replaceAll("object", "bias"));
		File dirBias = new File(fichBias.getParent().replaceAll("object", "bias"));
			if(!(dirBias.isDirectory() && dirBias.exists())){
				dirBias.mkdir();

			}
		boolean move = fichBias.renameTo(dest);
		setFile(dest);
		
		return dest;
	}
	
}
