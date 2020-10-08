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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import svo.gtc.db.basepath.BasepathAccess;
import svo.gtc.db.basepath.BasepathDb;
import svo.gtc.db.conf.ConfAccess;
import svo.gtc.db.conf.ConfDb;
import svo.gtc.db.detector.DetectorAccess;
import svo.gtc.db.detector.DetectorDb;
import svo.gtc.db.instrument.InstrumentoAccess;
import svo.gtc.db.instrument.InstrumentoDb;
import svo.gtc.db.modo.SubmodoAccess;
import svo.gtc.db.modo.SubmodoDb;
import svo.gtc.db.obsblock.ObsBlockAccess;
import svo.gtc.db.obsblock.ObsBlockDb;
import svo.gtc.db.prodat.ProdDatosAccess;
import svo.gtc.db.prodat.ProdDatosDb;
import svo.gtc.db.prodat.WarningDb;
import svo.gtc.db.proderr.ErrorDb;
import svo.gtc.db.proderr.ProdErrorDb;
import svo.gtc.db.proderr.ProdErrorAccess;
import svo.gtc.db.programa.ProgramaAccess;
import svo.gtc.db.programa.ProgramaDb;
import svo.gtc.ingestion.IngestionException;
import utiles.Coordenadas;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.fits.PaddingException;

public class ProdDatos {
	public static String CAL = "CAL";
	public static String SCI = "SCI";
	
	private File		file			= null;
	private Integer		prodId			= null;
	private String 		programKeyword 	= null;
	private String 		oblockKeyword	= null;
	private ObsBlock	oblock			= null;
	//private String 		filename		= null;
	private String 		path 			= null; 
	private String 		detector		= null;
	private String 		instrument 		= null;
	private String 		obsmode			= null;
	private Long		fileSize 		= null;
	private Double		ra				= null;
	private Double  	de 				= null;
	//private String  	obsType			= null;
	private Timestamp	date			= null;
	private Timestamp 	opentime		= null;
	private Timestamp 	closetime		= null;
	//private Double		exposure		= null;
	private Double		exptime			= null;
	private Double		airmass 		= null;
	private String		object	 		= null;
	private String 		observer		= null;
	
	//nueva columna mty_id
	//private String mty = null;
	
	//Informacion de bandas espectrales
	private String band = null;
	private Double band_min = null;
	private Double band_max = null;
	
	//Información pgsphere
	private String polig = null;
	private String polig2 = null;
	private String point = null;
	
	private String modo			=	null;
	private String modType		=	null;
	private String submodo		=	null;
	
	private String fatalError	= 	null;
	
	public ProdDatos(File fichero){
		this.file=fichero;
		this.prodId=new Integer(fichero.getName().substring(0, fichero.getName().indexOf("-")));
		this.path = fichero.getAbsolutePath().substring(0, fichero.getAbsolutePath().lastIndexOf("/")+1);
		this.fileSize = new Long(fichero.length());
		
		try {
			rellenaCamposFits();
		} catch (FitsException e) {
			e.printStackTrace();
			this.fatalError="E-0016: Unreadable or invalid file.;";
		} catch (IOException e) {
			e.printStackTrace();
			this.fatalError="E-0016: Unreadable or invalid file.;";
		} catch (Exception e){
			e.printStackTrace();
			this.fatalError="E-0016: Unreadable or invalid file.;";
		}
		
		this.oblock=new ObsBlock(this);

	}

	public ProdDatos(ProdDatos prod){
		this(prod.getFile());
	}

	// Comprobaciones a tener en cuenta:
	// - Si el nombre del fichero es el mismo que el campo filename
	// - Si el oblock coincide (en el campo y en el path).
	// - Si el program coincide con el del directorio.
	// - Si la fecha coincide con la del nombre del fichero.
	// - Si la exposicion (Closetime-Opentime) concuerda con el campo ELAPSED.
	

	/**
	 * Recoge los valores que provienen de los keywords del FITS.
	 * 
	 * @throws FitsException
	 * @throws IOException
	 */
	private void rellenaCamposFits() throws FitsException, IOException, NullPointerException{
		
		boolean compressed = false;
		
		if(this.getFile().getName().toUpperCase().endsWith(".GZ")){
			compressed = true;
		}
	    Fits fEntrada = new Fits(this.file,compressed);
	   
	    try{
	    	fEntrada.read();
	    }catch(PaddingException e){
	    	fEntrada.addHDU(e.getTruncatedHDU());
	    }
	    BasicHDU hdu = fEntrada.getHDU(0);   
	    
	    Header header=hdu.getHeader();

		// FILENAME. SE ELIMINA ESTE KEYWORD DE LOS FITS.
		/*try{
			this.filename = header.findCard("FILENAME").getValue();
		}catch(NullPointerException e){}
		 */
		
		// INSTRUMENT
		try{
			this.instrument = header.findCard("INSTRUME").getValue().trim();
		}catch(NullPointerException e){}

		// DETECTOR
		try{
			this.detector = header.findCard("DETECTOR").getValue().trim();
		}catch(NullPointerException e){
			//Se trata de Canaricam, por tanto no tiene detector
			
			
		}
		//Para los instrumentos que no tienen detector añadimos el del instrumento
		if(this.detector==null){
			this.detector = this.instrument;
		}

		// OBSMODE
		try{
			this.obsmode = header.findCard("OBSMODE").getValue().trim();
			if(this.obsmode.equals("OsirisDomeFlats")) this.obsmode="OsirisDomeFlat";
		}catch(NullPointerException e){}
		//CANARYCAM coge el valor CAMMODE en vez de OBSMODE
		try{
			String cammode = header.findCard("CAMMODE").getValue().trim();
			this.obsmode = cammode;
		}catch(NullPointerException e){}


		// PROGID
		try{
			this.programKeyword = header.findCard("GTCPRGID").getValue().trim();
		}catch(NullPointerException e){}

		if(this.programKeyword==null){
			try{
				this.programKeyword = header.findCard("GTCPROGI").getValue().trim();
			}catch(NullPointerException e){}
		}

		// OBLOCK
		try{
			this.oblockKeyword = header.findCard("GTCOBID").getValue().trim();
			String aux =this.oblockKeyword.replaceAll("_", "-");
			if(aux.indexOf("-")!=aux.lastIndexOf("-")){
				this.oblockKeyword = aux.substring(aux.lastIndexOf("-")+1);
			}
		}catch(NullPointerException e){}

		// RA & DE para el caso de OSIRIS
		try{
			this.ra = new Double(header.findCard("RADEG").getValue());
			this.de = new Double(header.findCard("DECDEG").getValue());
		}catch(NumberFormatException e1){
			e1.printStackTrace();
		}catch(NullPointerException e){}
		
		// Si no existe RADEG o DECDEG será el caso CANARICAM (RA y DEC en grados)
		if(this.ra==null || this.de==null){
			try{
				this.ra= new Double(header.findCard("RA").getValue());
				this.de= new Double(header.findCard("DEC").getValue());
						
			}catch(NumberFormatException e){
			}catch(NullPointerException e1){
				//e1.printStackTrace();
			}
		}
		
		// Si no existe RADEG o DECDEG utilizamos los campos RA y DEC
		if(this.ra==null || this.de==null){
			try{
				String auxRA  = header.findCard("RA").getValue();
				String auxDec = header.findCard("DEC").getValue();
				
				if(Coordenadas.checkCoordinatesFormat(auxRA+" "+auxDec)){
					Double[] coords = Coordenadas.coordsInDeg(auxRA+" "+auxDec);
					this.ra=coords[0];
					this.de=coords[1];
				}else{
					this.ra=Double.valueOf(auxRA);
					this.de=Double.valueOf(auxDec);
				}
			}catch(NullPointerException e){
			}catch(Exception e1){
				e1.printStackTrace();
			}
		}
		
		//Información geometría
		
		//Tomamos los valores del polígono de header1
			try{
				BasicHDU hdu1 = fEntrada.getHDU(1);  
			    Header header1=hdu1.getHeader();
				
			    Double datasec1 = null;
			    Double datasec2 = null;
			    try{
				    String datasec = header1.findCard("DATASEC").getValue();
				    String[] ds = datasec.split(",");
				    datasec1 = Double.valueOf(ds[0].split(":")[1]);
				    datasec2 = Double.valueOf(ds[1].split(":")[1].replaceAll("]", ""));
			    }catch(Exception e){//CASO de EMIR, no tiene datasec
			    	datasec1 = new Double(header1.findCard("NAXIS1").getValue());
			    	datasec2 = new Double(header1.findCard("NAXIS2").getValue());
			    }
			    
			    
			    Double crval1_1 = new Double(header1.findCard("CRVAL1").getValue());
			    Double crval2_1 = new Double(header1.findCard("CRVAL2").getValue());
			    Double crpix1_1 = new Double(header1.findCard("CRPIX1").getValue());
			    Double crpix2_1 = new Double(header1.findCard("CRPIX2").getValue());
			    Double cd1_1 = new Double(header1.findCard("CD1_1").getValue());
			    Double cd2_1 = new Double(header1.findCard("CD2_2").getValue());
			    
			    //Obtenemos los valores
			    Double rmax1 = crval1_1+(0-crpix1_1)*cd1_1;
			    Double rmin1 = crval1_1+(datasec1-crpix1_1)*cd1_1;
			    Double decmax1 = crval2_1+(datasec2-crpix2_1)*cd2_1;
			    Double decmin1 = crval2_1+(0-crpix2_1)*cd2_1;

				//spoly'{(ramin,decmin),(ramin,decmax),(ramax,decmax),(ramax,decmin)}'
			    if(rmax1==rmin1 || decmax1==decmin1 || Math.abs(rmax1/rmin1 - 1) < 0.0000001 || Math.abs(decmax1/decmin1 - 1) < 0.0000001){
			    	
			    	cd1_1 = new Double(header1.findCard("CD1_2").getValue());
				    cd2_1 = new Double(header1.findCard("CD2_1").getValue());
				  
				    //Obtenemos los valores
				    rmax1 = crval1_1+(0-crpix1_1)*cd1_1;
				    rmin1 = crval1_1+(datasec1-crpix1_1)*cd1_1;
				    decmax1 = crval2_1+(datasec2-crpix2_1)*cd2_1;
				    decmin1 = crval2_1+(0-crpix2_1)*cd2_1;
				    if(rmax1==rmin1 || decmax1==decmin1 || Math.abs(rmax1/rmin1 - 1) < 0.0000001 || Math.abs(decmax1/decmin1 - 1) < 0.0000001){
				    	polig = null;
				    }else{
				    	polig= "spoly'{("+rmax1+"d,"+decmin1+"d),("+rmax1+"d,"+decmax1+"d),("+rmin1+"d,"+decmax1+"d),("+rmin1+"d,"+decmin1+"d)}'";
				    }
			    	//polig = null;
			    }else{
			    	polig= "spoly'{("+rmax1+"d,"+decmin1+"d),("+rmax1+"d,"+decmax1+"d),("+rmin1+"d,"+decmax1+"d),("+rmin1+"d,"+decmin1+"d)}'";
			    }
			    
				
			}catch(Exception e){
				//e.printStackTrace();
			}
			
			//Tomamos los valores del polígono de header2
			try{
				BasicHDU hdu2 = fEntrada.getHDU(2);
			    Header header2=hdu2.getHeader();
				
			    Double datasec1 = null;
			    Double datasec2 = null;
			    
			    try{
				    String datasec = header2.findCard("DATASEC").getValue();
				    String[] ds = datasec.split(",");
				    datasec1 = Double.valueOf(ds[0].split(":")[1]);
				    datasec2 = Double.valueOf(ds[1].split(":")[1].replaceAll("]", ""));
			    }catch(Exception e){
			    	datasec1 = new Double(header2.findCard("NAXIS1").getValue());
			    	datasec2 = new Double(header2.findCard("NAXIS2").getValue());
			    }
			    
			     Double crval1_2 = new Double(header2.findCard("CRVAL1").getValue());
			    Double crval2_2 = new Double(header2.findCard("CRVAL2").getValue());
			    Double crpix1_2 = new Double(header2.findCard("CRPIX1").getValue());
			    Double crpix2_2 = new Double(header2.findCard("CRPIX2").getValue());
			    Double cd1_2 = new Double(header2.findCard("CD1_1").getValue());
			    Double cd2_2 = new Double(header2.findCard("CD2_2").getValue());
			    
			    //Obtenemos los valores
			    Double rmax2 = crval1_2+(0-crpix1_2)*cd1_2;
			    Double rmin2 = crval1_2+(datasec1-crpix1_2)*cd1_2;
			    Double decmax2 = crval2_2+(datasec2-crpix2_2)*cd2_2;
			    Double decmin2 = crval2_2+(0-crpix2_2)*cd2_2;
				
			    if(rmax2==rmin2 || decmax2==decmin2 || Math.abs(rmax2/rmin2 - 1) < 0.0000001 || Math.abs(decmax2/decmin2 - 1) < 0.0000001){
			    	
			    	cd1_2 = new Double(header2.findCard("CD1_2").getValue());
				    cd2_2 = new Double(header2.findCard("CD2_1").getValue());
				  
				    //Obtenemos los valores
				    rmax2 = crval1_2+(0-crpix1_2)*cd1_2;
				    rmin2 = crval1_2+(datasec1-crpix1_2)*cd1_2;
				    decmax2 = crval2_2+(datasec2-crpix2_2)*cd2_2;
				    decmin2 = crval2_2+(0-crpix2_2)*cd2_2;
				    if(rmax2==rmin2 || decmax2==decmin2 || Math.abs(rmax2/rmin2 - 1) < 0.0000001 || Math.abs(decmax2/decmin2 - 1) < 0.0000001){
				    	polig2 = null;
				    }else{
				    	polig2= "spoly'{("+rmax2+"d,"+decmin2+"d),("+rmax2+"d,"+decmax2+"d),("+rmin2+"d,"+decmax2+"d),("+rmin2+"d,"+decmin2+"d)}'";
				    }
			    }else{
			    	polig2= "spoly'{("+rmax2+"d,"+decmin2+"d),("+rmax2+"d,"+decmax2+"d),("+rmin2+"d,"+decmax2+"d),("+rmin2+"d,"+decmin2+"d)}'";
			    }
				
			}catch(Exception e){
				
			}
			
		//}
		try{
			if(ra!=null && de !=null){
				point = "spoint'("+ra+"d,"+de+"d)'";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			this.exptime = new Double(header.findCard("EXPTIME").getValue());
		}catch(NumberFormatException e1){
			e1.printStackTrace();
		}catch(NullPointerException e){}
		
		// DATE, OPENTIME, CLOSETIME (READTIME)
		try{
			
			String auxDate 		= null;
			String auxOpentime 	= null;
			String auxClosetime = null;
			String auxReadtime 	= null;
				
			try{
				if(header.findCard("DATE-OBS").getValue().contains("T")){
					auxDate = header.findCard("DATE-OBS").getValue().replaceAll("T", " ");
				}else{
					auxDate = header.findCard("DATE-OBS").getValue()+" 00:00:01";
				}
			}catch(NullPointerException e1){}
			
			if(auxDate==null){
				try{
					if(header.findCard("DATE").getValue().contains("T")){
						auxDate = header.findCard("DATE").getValue().replaceAll("T", " ");
					}else{
						auxDate = header.findCard("DATE").getValue()+" 00:00:01";
					}
				}catch(NullPointerException e1){}
			}
			
			
			//Podremos encontrar OPENTIME o UTSTART según el instrumento
			try{
				auxOpentime = header.findCard("OPENTIME").getValue();
			}catch(NullPointerException e1){
				
			}
			if(auxOpentime==null || auxOpentime.length()==0){
				try{
					auxOpentime = header.findCard("UTSTART").getValue();
				}catch(NullPointerException e1){
					
				}
			}
			if(auxOpentime==null || auxOpentime.length()==0){
				try{
					auxOpentime = header.findCard("UT").getValue();
				}catch(NullPointerException e1){
					
				}
			}
			//CASO de EMIR
			if(auxOpentime==null || auxOpentime.length()==0){
				try{
					auxOpentime = header.findCard("UTC").getValue();
				}catch(NullPointerException e1){
					
				}
			}
			
			//Podremos encontrar CLOSTIME o UTEND según el instrumento
			try{
				auxClosetime = header.findCard("CLOSTIME").getValue();
			}catch(NullPointerException e1){
				
			}
			
			if(auxOpentime==null || auxOpentime.length()==0){
				try{
					auxClosetime = header.findCard("UTEND").getValue();
				}catch(NullPointerException e1){
					
				}
			}
			
			try{
				auxReadtime = header.findCard("READTIME").getValue();
			}catch(NullPointerException e1){}
			
			if( (auxClosetime==null || auxClosetime.trim().length()==0) && auxReadtime!=null){
				auxClosetime = auxReadtime;
			}
			//String auxDate = "2010-01-01 10:00:00";
			//String auxOpentime = "23:00:00";
			//String auxClosetime = "23:30:00";
			
			try{
				this.date = Timestamp.valueOf(auxDate);
			}catch(NullPointerException e){}
			catch(Exception e){ 
				e.printStackTrace(); 
			}
			
			try{
				//Si no tenemos el opentime cogemos solo el valor del date-obs
				if(auxOpentime!=null && auxOpentime.length()>0){
					this.opentime = Timestamp.valueOf(auxDate.substring(0,auxDate.indexOf(" "))+" "+auxOpentime);
				}else{
					this.opentime = this.date;
				}
				// Si el opentime>date restamos un dia al opentime, porque significaría 
				// que ha coincidido la media noche entre el opentime y el date de creacion del archivo.
				/*if(this.opentime.after(this.date)){
					Calendar cal = Calendar.getInstance();
					cal.setTime(this.opentime);
					cal.add(Calendar.DAY_OF_MONTH, -1);
					this.opentime=new Timestamp(cal.getTimeInMillis());
				}*/
			}catch(NullPointerException e){
				
			}catch(Exception e2){
				//System.out.println(this.getFile().getName()+" : "+auxDate.substring(0,auxDate.indexOf(" "))+" "+auxOpentime);
				//e2.printStackTrace(); 
			}
			
			try{
				//Si tenemos closetime lo calculamos
				if(auxClosetime!=null && auxClosetime.length()>0){
					this.closetime = Timestamp.valueOf(auxDate.substring(0,auxDate.indexOf(" "))+" "+auxClosetime);
				}else{
					//Obtenemos el closetime con el opentime más el exptime
					Calendar cal = Calendar.getInstance();
			        cal.setTimeInMillis(this.opentime.getTime());
			        cal.add(Calendar.SECOND, this.getExptime().intValue());
			        this.closetime = new Timestamp(cal.getTime().getTime());
			        
					
				}
				// Si closetime > date restamos un d�a (en milisegundos) al opentime, porque significar�
				// que ha coincidido la media noche entre el opentime y el date de creaci�n del archivo.
				if(this.closetime.before(this.opentime)){
					Calendar cal = Calendar.getInstance();
					cal.setTime(this.closetime);
					cal.add(Calendar.DAY_OF_MONTH, +1);
					this.closetime=new Timestamp(cal.getTimeInMillis());
				}
			}catch(NullPointerException e){}
			catch(Exception e){ 
				//System.out.println(this.getFile().getName()+" : "+auxDate.substring(0,auxDate.indexOf(" "))+" "+auxClosetime);
				e.printStackTrace(); 
			}
		}catch(NullPointerException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace(); 
		}


		
		// EXPOSURE
		/*
		try{
			this.exposure = new Double(header.findCard("ELAPSED").getValue());
		}catch(NumberFormatException e1){
			e1.printStackTrace();
		}catch(NullPointerException e){}
		 */

		// AIRMASS
		try{
			this.airmass = new Double(header.findCard("AIRMASS").getValue());
		}catch(NumberFormatException e1){
			e1.printStackTrace();
		}catch(NullPointerException e){}
		if(this.airmass==null){//para el caso de CIRCE es AIRMASS1
			try{
				this.airmass = new Double(header.findCard("AIRMASS1").getValue());
			}catch(Exception e){
				
			}
		}
		
		
		// OBJECT
		try{
			this.object = header.findCard("OBJECT").getValue().trim();
		}catch(NullPointerException e){}
		
		// OBSERVER
		try{
			this.observer = header.findCard("PI").getValue().trim();
		}catch(NullPointerException e){
			try{
				this.observer = header.findCard("OBSERVER").getValue().trim();
			}catch(NullPointerException e1){
			
			}
			
		}
		
		//BANDAS ESPECTRALES
		try{
			this.band = header.findCard("GRISM").getValue().trim();
			//Definir la band min y max a partir de este valor
			
			if(band!=null && band.length()>0){
				
				band_def(band);
				
			}
			
		}catch(NumberFormatException e1){
			e1.printStackTrace();
		}catch(NullPointerException e){}
		
		
		fEntrada.getStream().close();
		
	}

	// Comprobaciones a tener en cuenta:
	// - Si el oblock coincide (en el campo y en el path).
	// - Si el program coincide con el del directorio.
	// - Si la fecha coincide con la del nombre del fichero.
	// - Si la exposicion (Closetime-Opentime) concuerda con el campo ELAPSED.


	public void test(Connection con) throws GtcFileException{
		String err = "";
		if(this.fatalError!=null){
			throw new GtcFileException(this.fatalError);
		}
		
		try{
			testInstrumento();
		}catch(GtcFileException e){
			err += e.getMessage()+"; "; 
		}

		try{
			testFilename();
		}catch(GtcFileException e){
			err += e.getMessage()+"; "; 
		}

		try{
			testOblock(con);
		}catch(GtcFileException e){
			err += e.getMessage()+"; "; 
		}

		try{
			testProgram();
		}catch(GtcFileException e){
			err += e.getMessage()+"; "; 
		}
		
		/*try{
			testExptime();
		}catch(GtcFileException e){
			err += e.getMessage()+"; "; 
		}*/
		
		if(err.length()>0){
			throw new GtcFileException(err);
		}
	}
	
	/**
	 * Comprueba si el instrumento es uno de los conocidos.
	 * @throws ProdDatosException
	 */
	public void testInstrumento() throws GtcFileException{
		String errors = "";
		if( !(getInstrument().equalsIgnoreCase("OSIRIS") || getInstrument().equalsIgnoreCase("CANARICAM")|| getInstrument().equalsIgnoreCase("CIRCE") || getInstrument().equalsIgnoreCase("EMIR")
				|| getInstrument().equalsIgnoreCase("HIPERCAM") || getInstrument().equalsIgnoreCase("MEGARA")|| getInstrument().equalsIgnoreCase("HORuS")))
				errors+= "E-0003: Not recognized INSTRUMENT: "+getInstrument()+";";
		
		if(errors.length()>0){
			throw new GtcFileException(errors);
		}

	}
	
	/**
	 * Comprueba si:
	 * 	- El nombre del fichero es el mismo que el valor del keyword FILENAME. ---ELIMINADO---
	 *  - El instrumento coincide con el encontrado en el nombre del fichero.
	 *  - El obsmode coincide con el encontrado en el nombre del fichero.
	 *  
	 * NOTA: DESACTIVAMOS LA COMPROBACIÓN E-0004 PORQUE EL CAMPO FILENAME
	 * 		 VA A DESAPARECER.
	 * @throws GtcFileException
	 */
	public void testFilename() throws GtcFileException{
		String errors = "";
		//if(!this.filename.equals(this.file.getName())) errors+= "E-0004: Name of the file and FITS keyword FILENAME differs.;";
		
		if(!getInstrument().toUpperCase().contains("HORUS")){
			//Esta condición no se aplica a los ficheros HORuS
			if(!this.file.getName().matches("[0-9]{10}-[0-9]{8}-"+getInstrument().toUpperCase()+"-.*")){
				errors+="E-0007: Instrument in the file name and in the FITS keyword INSTRUME ("+getInstrument().toUpperCase()+") differs.;";
			}
		}
		if(this.obsmode!=null && !(this.file.getName().matches("[0-9]{10}-[0-9]{8}-[^-]+-"+this.obsmode+"\\..*") ||
				this.file.getName().matches("[0-9]{10}-[0-9]{8}-[^-]+-"+this.obsmode+"-.*"))){
			errors+="W-0005: ObsMode in the file name and in the FITS keyword OBSMODE ("+this.obsmode+") differs.;";
		}
		
		
		//Este error tendría que depender del instrumento, osiris se puede solucionar
		/*//Error tamaño, error si tienes menos de 1024kB (1MB)
		if(this.getFile().length()<1024000){
			
			//Comprobamos si cumple la condición de la ventana
			
			errors+="E-0022: Tamaño de fichero menor a 50KB.;";
		}*/
		
		if(errors.length()>0){
			throw new GtcFileException(errors);
		}
	}

	
	
	/**
	 * Comprueba si el oblock coincide (en el campo y en el path).
	 * @throws GtcFileException
	 */
	public void testOblock(Connection con) throws GtcFileException{
		String errors = "";
		//System.out.println("AAA "+auxOblockPath+"   "+this.oblock);
		//this.getOblock().test();
		
		if( this.oblockKeyword==null ||
			this.oblockKeyword.matches("[9]+") || 
			this.oblockKeyword.equalsIgnoreCase("CALIB") ||
			this.oblockKeyword.equalsIgnoreCase("CALIBRATION") ||
			this.oblockKeyword.equalsIgnoreCase("BIAS") ||
			this.oblockKeyword.equalsIgnoreCase("DARK") ||
			this.oblockKeyword.equalsIgnoreCase("NULL")){
			return;
		}
		
		
		// Para comprobar el OB vemos si el fichero ya está registrado. Si ya está registrado en otro OB
		// y ese OB coincide con el encontrado en el keyword, se supone que es correcto.
		ProdDatosAccess prodDatosAccess = null;
		ProdDatosDb prodExistente = null;
		try {
			prodDatosAccess = new ProdDatosAccess(con);
			prodExistente = prodDatosAccess.selectById(getProgram().getProgId(), getOblock().getOblId(),getProdId());
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		if(prodExistente!=null && prodExistente.getOblId().substring(0,4).equals(this.oblockKeyword.substring(0, 4))){
			return;
		}
		
		try{
			if(!this.oblock.getOblId().substring(0, 4).equals(this.oblockKeyword.substring(0, 4))) errors+= "W-0003: Observing Block in the file path ("+this.oblock.getOblId()+") and in keyword GTCOBID ("+this.oblockKeyword+") differs.;";
		}catch(StringIndexOutOfBoundsException e){
			errors+= "W-0003: Observing Block in the file path ("+this.oblock.getOblId()+") and in keyword GTCOBID ("+this.oblockKeyword+") differs.;";
		}catch(NullPointerException e1){
			errors+= "W-0003: Observing Block in the file path and in keyword GTCOBID differs.;";
		}
		
		if(errors.length()>0){
			throw new GtcFileException(errors);
		}

	}
	
	/**
	 * Comprueba si el program coincide con el del directorio.
	 * @throws GtcFileException
	 */
	public void testProgram() throws GtcFileException{
		String errors = "";
		
		try{
			this.getProgram().test();
		}catch(GtcFileException e){
			errors+=e.getMessage();
		}
		
		
		if( this.programKeyword==null ||
				this.programKeyword.matches("[9]+") ||
				this.programKeyword.equalsIgnoreCase("CALIB") ||
				this.programKeyword.equalsIgnoreCase("CALIBRATION") ||
				this.programKeyword.equalsIgnoreCase("999") ||
				this.programKeyword.equalsIgnoreCase("NULL")){
			return;
		}

		try{
			if(!this.programKeyword.replace("_", "-").startsWith(this.getProgram().getProgId())) errors+="W-0004: Program in the file path ("+this.getProgram().getProgId()+") and in keyword GTCPROGI ("+this.programKeyword+") differs.;";
		}catch(NullPointerException e){
			errors+= "W-0004: Program in the file path ("+this.getProgram().getProgId()+") and in keyword GTCPROGI ("+this.programKeyword+") differs.;";
		}
		
		if(errors.length()>0){
			throw new GtcFileException(errors);
		}

	}
	
	public void testExptime() throws GtcFileException{
		
		if(this.getExptime()!=null && this.getExptime()>0){
			long diff = this.getClosetime().getTime() - this.getOpentime().getTime();
			long exp = (long) (this.getExptime()*1000);
			
			if(exp>(diff+2000) || exp<(diff-2000)){
				throw new GtcFileException("E-0021: EXPTIME no es la diferencia closetime-opentime");
			}
		}
	}
	
	public static void main(String args[]) throws GtcFileException{
		
		ProdDatos prod = new ProdDatos(new File("/pcdisk/marconi/raul/proyectos/GTC/data/GTC19-09A/OB0002/object/0000003464-20090520-OSIRIS-OsirisBroadBandImage.fits"));
		//ProdDatos prod = new ProdDatos(new File("/media/Transcend/gtcData/GTC19-09A/OB0002/object/0000003464-20090520-OSIRIS-OsirisBroadBandImage.fits"));
		System.out.println("Filename "+prod.file.getName());
		System.out.println("ProdId   "+prod.prodId);
		System.out.println("Path 	"+prod.path);
		//System.out.println(prod.filename);
		System.out.println("Filesize "+prod.fileSize.longValue()/1024/1024+" MB");
		System.out.println("Instr 	"+prod.instrument);
		System.out.println("Detec 	"+prod.detector);
		System.out.println("Mode 	"+prod.obsmode);
		System.out.println("Program	"+prod.getProgram());
		System.out.println("Oblock 	"+prod.oblock);
		System.out.println("RA  	"+prod.ra.doubleValue());
		System.out.println("DEC 	"+prod.de.doubleValue());
		System.out.println("Date 	"+prod.date);
		System.out.println("OpenT 	"+prod.opentime);
		System.out.println("CloseT 	"+prod.closetime);
		System.out.println("Expos. 	"+prod.exptime+"    ["+((double)(prod.closetime.getTime()-prod.opentime.getTime())/1000)+"]");
		System.out.println("Airmass	"+prod.airmass);
		System.out.println("Object 	"+prod.object);
		System.out.println("Band 	"+prod.band);

		//prod.test();
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

		// INSERTAMOS LA INFORMACIÓN EN LA BASE DE DATOS
		con.setAutoCommit(false);
		ProgramaAccess programaAccess = new ProgramaAccess(con);
		ObsBlockAccess oblAccess = new ObsBlockAccess(con);
		ProdDatosAccess prodDatosAccess = new ProdDatosAccess(con);
		BasepathAccess basepathAccess = new BasepathAccess(con);
		ProdErrorAccess prodErrorAccess = new ProdErrorAccess(con);
		
		ProgramaDb 		programaDb 	= null;
		ObsBlockDb 		oblDb		= null;
		ConfDb 			confDb		= null;
		BasepathDb 		basepathDb	= null;
		
		// Comprobamos la existencia de Programa y si no existe lo añadimos
		programaDb = programaAccess.selectById(this.getProgram().getProgId().toUpperCase().replaceAll("\\s", ""));
		
		if(programaDb==null){
			programaDb = new ProgramaDb(this.getProgram());
			programaAccess.insert(programaDb);
		}

		// Comprobamos la existencia de Oblock y si no existe lo añadimos
		oblDb = oblAccess.selectById(this.getProgram().getProgId().toUpperCase().replaceAll("\\s", ""),
									 this.getOblock().getOblId().toUpperCase().replaceAll("\\s", ""));
		
		if(oblDb==null){
			oblDb = new ObsBlockDb(this.getOblock());
			oblAccess.insert(oblDb);
		}
		
		// Determinamos el path del fichero
		basepathDb = basepathAccess.selectBpathById(9000);//Ponemos 9000 para que coja el bpath del dataNew
		String path = this.getFile().getAbsolutePath().replaceAll(this.getFile().getName(), "").replaceAll(basepathDb.getBpathPath(), "");
		 
		// Buscamos / Creamos la configuración
		confDb = getConf(con);
		
		
		// Vemos si el fichero es una calibración externa

		ProdDatosDb prodExistente = prodDatosAccess.selectById(this.getProgram().getProgId(), this.getOblock().getOblId(), this.getProdId());
		if(prodExistente==null){
			// Insertamos el producto de datos
			ProdDatosDb prodDb = new ProdDatosDb();
			prodDb.setProdId(this.getProdId());
			prodDb.setProgId(programaDb.getProg_id().toUpperCase());
			prodDb.setOblId(oblDb.getObl_id().toUpperCase());
			prodDb.setDetId(confDb.getDetId());
			prodDb.setInsId(confDb.getInsId());
			prodDb.setModId(confDb.getModId());
			prodDb.setSubmId(confDb.getSubmId());
			prodDb.setConfId(confDb.getConfId());
			prodDb.setBpathId(bpathId);
			prodDb.setProdFilename(this.getFile().getName());
			prodDb.setProdPath(path);
			prodDb.setProdFilesize(this.getFileSize());
			prodDb.setProdRa(this.getRa());
			prodDb.setProdDe(this.getDe());
			//prodDb.setProdObsType(this.getObsType());
			prodDb.setProdInitime(this.getOpentime());
			prodDb.setProdEndtime(this.getClosetime());
			prodDb.setProdExposure(this.getExptime());
			prodDb.setProdAirmass(this.getAirmass());
			prodDb.setProdObserver(this.getObserver());
			prodDb.setProdObject(this.getObject());
			prodDb.setProdBandMin(this.getBand_min());
			prodDb.setProdBandMax(this.getBand_max());
			prodDb.setProdPolig(this.getPolig());
			prodDb.setProdPolig2(this.getPolig2());
			prodDb.setProdPoint(this.getPoint());
			//Introducimos mty
			//prodDb.setProdPoint();

			prodDb.setWarnings(warnings);
			
			prodDatosAccess.insProdDatos_new(prodDb);
		}
		
		
		////////// ELIMINACI�N DE ERRORES  ////////
		prodErrorAccess.delProdError(this.getProgram().getProgId(), this.getOblock().getOblId(), this.getProdId());
		///////////////////////////////////////////

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
		programaDb = programaAccess.selectById(this.getProgram().getProgId().toUpperCase().replaceAll("\\s", ""));
		
		if(programaDb==null){
			programaDb = new ProgramaDb(this.getProgram());
			programaAccess.insert(programaDb);
		}

		// Comprobamos la existencia de Oblock y si no existe lo añadimos
		oblDb = oblAccess.selectById(this.getProgram().getProgId().toUpperCase().replaceAll("\\s", ""),
									 this.getOblock().getOblId().toUpperCase().replaceAll("\\s", ""));
		
		if(oblDb==null){
			oblDb = new ObsBlockDb(this.getOblock());
			if(this.getOblock().getOblId().length()>5){
				System.out.println("Error en observing block name: "+ this.getOblock().getOblId());
			}

			oblAccess.insert(oblDb);
			
			//con.commit();
		}
		
		
		// Determinamos el path base
		basepathDb = basepathAccess.selectBpathById(9000);//Ponemos 9000
		String basePath = basepathDb.getBpathPath();

		
		// Si ya est� en la tabla de errores, se borran los que hab�a 
		// y se introducen los nuevos. Si no est� se introduce junto
		// con sus errores.
		if(prodErrorAccess.countProdErrorById(getProgram().getProgId(), getOblock().getOblId(), getProdId())>0){
			prodErrorAccess.delErrores(getProgram().getProgId(), getOblock().getOblId(),getProdId());
			prodErrorAccess.insErrors(getProgram().getProgId(), getOblock().getOblId(),getProdId(), errores );
		}else{
			ProdErrorDb prodError = new ProdErrorDb(this,basePath);
			prodError.setErrors(errores);
			prodErrorAccess.insProdError(prodError);
		}
	}

	/**
	 * Devuelve la configuración correspondiente al producto de datos. Si no existiese, se crea.
	 * @param con
	 * @return
	 * @throws SQLException
	 * @throws IngestionException
	 */
	public ConfDb getConf(Connection con) throws SQLException, IngestionException{
		DetectorAccess detectorAccess 	= new DetectorAccess(con);
		InstrumentoAccess instAccess 	= new InstrumentoAccess(con);
		SubmodoAccess submodoAccess 		= new SubmodoAccess(con);
		ConfAccess confAccess 			= new ConfAccess(con);

		DetectorDb 		det 		= null;
		InstrumentoDb 	inst 		= null;
		SubmodoDb 		submodo 	= null;
		ConfDb[]		conf 		= null;

		// Buscamos el detector, instrumento y el modo
		det = detectorAccess.selectByShortName(this.getDetector());
		if(det==null) throw new IngestionException("INGESTION ERROR: Detector "+getDetector()+" not found in the database.");

		inst = instAccess.selectByName(this.getInstrument());
		if(inst==null) throw new IngestionException("INGESTION ERROR: Instrument "+getInstrument()+" not found in the database.");
		
		submodo = submodoAccess.selectById(inst.getInsId(), getModo(), getSubmodo());
		if(submodo==null) throw new IngestionException("INGESTION ERROR: Mode "+getModo()+"/"+getSubmodo()+" not found in the database.");
		
		conf = confAccess.selectById(det.getDetId(), submodo.getInsId(), submodo.getModId(), submodo.getSubmId());
		
		// Si no existe la configuración, la creamos.
		if(conf.length<=0){
			
			ConfDb newConf = new ConfDb();
			newConf.setDetId(det.getDetId());
			newConf.setInsId(submodo.getInsId());
			newConf.setModId(submodo.getModId());
			newConf.setSubmId(submodo.getSubmId());
			newConf = confAccess.insert(newConf);
			
			return newConf;
		}else if(conf.length>1){
			throw new IngestionException("INGESTION ERROR: More than one configuration for the data product.");
		}else{
			return conf[0];
		}
	}
	
	/**
	 * Actualiza la tabla proddatos para cambiar los mty_id y que sean visibles en el archivo.
	 * @param con
	 * @throws SQLException 
	 */
	public void updateMty(Connection con) throws SQLException {
		ProgramaAccess programaAccess = new ProgramaAccess(con);
		
		programaAccess.updMty();
		
	}
	
	
	
	
	/** 
	 * Comprueba si un producto determinado es una calibración externa al bloque de
	 * observación en el que está. Para ello se mira si el producto ya ha sido 
	 * introducido en la base de datos y si el bloque de observación registrado
	 * es diferente al actual.
	 * 
	 * @param prodDatosAccess
	 * @param prod
	 * @return <p>Si existe, el producto de datos de otro observing block al que corresponde</p>
	 * @throws SQLException 
	 */

	
	////////////////////////////////////////////////////////////////
	
	/**
	 * A partir del nombre de la banda obtenemos la band_max y band_min
	 * 
	 * @param band
	 * @throws GtcFileException
	 */
	public void band_def(String band){
		
		if(band.equalsIgnoreCase("R300B") || band.equalsIgnoreCase("R500B")){
			this.band_min = 3600.0;
			this.band_max = 7200.0;
		}else if(band.equalsIgnoreCase("R300R") || band.equalsIgnoreCase("R500R")){
			this.band_min = 4800.0;
			this.band_max = 10000.0;
		}else if(band.equalsIgnoreCase("R1000B")){
			this.band_min = 3630.0;
			this.band_max = 7500.0;
		}else if(band.equalsIgnoreCase("R1000R")){
			this.band_min = 5100.0;
			this.band_max = 10000.0;
		}else if(band.equalsIgnoreCase("R2000B")){
			this.band_min = 3950.0;
			this.band_max = 5700.0;
		}else if(band.equalsIgnoreCase("R2500U")){
			this.band_min = 3440.0;
			this.band_max = 4610.0;
		}else if(band.equalsIgnoreCase("R2500V")){
			this.band_min = 4500.0;
			this.band_max = 6000.0;
		}else if(band.equalsIgnoreCase("R2500R")){
			this.band_min = 5575.0;
			this.band_max = 7685.0;
		}else if(band.equalsIgnoreCase("R2500I")){
			this.band_min = 7330.0;
			this.band_max = 10000.0;
		}
	}
	
	public File getFile() {
		return file;
	}
	
	public Integer getProdId() {
		return prodId;
	}


	public void setProdId(Integer prodId) {
		this.prodId = prodId;
	}

	public void setFile(File file) {
		this.file = file;
	}


	public Program getProgram() {
		return this.getOblock().getProgram();
	}


	public ObsBlock getOblock() {
		return oblock;
	}


	public void setOblock(ObsBlock oblock) {
		this.oblock = oblock;
	}

	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public String getDetector() {
		return detector;
	}


	public void setDetector(String detector) {
		this.detector = detector;
	}


	public String getInstrument() {
		return instrument;
	}


	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}


	public String getObsMode() {
		return obsmode;
	}


	public void setObsMode(String mode) {
		this.obsmode = mode;
	}


	public Long getFileSize() {
		return fileSize;
	}


	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}


	public Double getRa() {
		return ra;
	}


	public void setRa(Double ra) {
		this.ra = ra;
	}


	public Double getDe() {
		return de;
	}


	public void setDe(Double de) {
		this.de = de;
	}


	public Timestamp getDate() {
		return date;
	}


	public void setDate(Timestamp date) {
		this.date = date;
	}


	public Timestamp getOpentime() {
		return opentime;
	}


	public void setOpentime(Timestamp opentime) {
		this.opentime = opentime;
	}


	public Timestamp getClosetime() {
		return closetime;
	}


	public void setClosetime(Timestamp closetime) {
		this.closetime = closetime;
	}

	/*
	public Double getExposure() {
		return exposure;
	}


	public void setExposure(Double exposure) {
		this.exposure = exposure;
	}
	 */
	

	public Double getExptime() {
		return exptime;
	}

	public void setExptime(Double exptime) {
		this.exptime = exptime;
	}

	public Double getAirmass() {
		return airmass;
	}


	public void setAirmass(Double airmass) {
		this.airmass = airmass;
	}


	public String getObject() {
		return object;
	}


	public void setObject(String object) {
		this.object = object;
	}

	public String getModo() {
		return modo;
	}

	public void setModo(String modo) {
		this.modo = modo;
	}

	public String getModType() {
		return modType;
	}

	public void setModType(String modType) {
		this.modType = modType;
	}

	public String getSubmodo() {
		return submodo;
	}

	public void setSubmodo(String submodo) {
		this.submodo = submodo;
	}

	public String getObserver() {
		return observer;
	}

	public void setObserver(String observer) {
		this.observer = observer;
	}
	
	public String getBand() {
		return band;
	}

	public void setBand(String band) {
		this.band = band;
	}

	public Double getBand_min() {
		return band_min;
	}

	public void setBand_min(Double band_min) {
		this.band_min = band_min;
	}

	public Double getBand_max() {
		return band_max;
	}

	public void setBand_max(Double band_max) {
		this.band_max = band_max;
	}
	
	public String getPolig() {
		return polig;
	}

	public void setPolig(String polig) {
		this.polig = polig;
	}

	public String getPolig2() {
		return polig2;
	}

	public void setPolig2(String polig2) {
		this.polig2 = polig2;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
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

	
	
}
