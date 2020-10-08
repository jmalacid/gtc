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

import svo.gtc.db.obsblock.ObsBlockAccess;
import svo.gtc.db.proderr.ErrorDb;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;


public class ObsBlock {
	public static final String RegExpObl = "(?:OB){0,1}([0-9]{4}[a-zA-Z]{0,1}[0-9]*)";

	private Program		program 	= null;

	private File		directorio	= null;
	private String 		oblId 		= null;
	
	private QcFile[]	qcFiles		= new QcFile[0];
	 
	public ObsBlock(File directorio) throws GtcFileException{
		this.directorio=directorio;
		this.oblId = directorio.getName().replaceAll("^"+RegExpObl+"$", "$1");
		File parent = this.directorio.getParentFile();
		this.program=new Program(parent);
		
		this.qcFiles = findQcFiles();
		//System.out.println("OblID: "+oblId);
	}
	
	public ObsBlock(ProdDatos prod){
		String aux = prod.getFile().getAbsolutePath();
		aux = aux.replaceAll("(^.+/"+Program.RegExpProgram+"/+"+ObsBlock.RegExpObl+")/.*$", "$1");
		this.directorio=new File(aux);
		this.oblId = directorio.getName().replaceAll("^"+RegExpObl+"$", "$1");
		File parent = this.directorio.getParentFile();
		this.program=new Program(parent);
		this.program=new Program(parent,2018,"B");
		
		this.qcFiles = findQcFiles();
		//System.out.println("OblID: "+oblId);
	}
	
	public ObsBlock(ProdDatos2 prod){
		String aux = prod.getFile().getAbsolutePath();
		aux = aux.replaceAll("(^.+/"+Program.RegExpProgram+"/+"+ObsBlock.RegExpObl+")/.*$", "$1");
		this.directorio=new File(aux);
		this.oblId = directorio.getName().replaceAll("^"+RegExpObl+"$", "$1");
		File parent = this.directorio.getParentFile();
//		this.program=new Program(parent);
		this.program=new Program(parent,2018,"B");
		
		this.qcFiles = findQcFiles();
		//System.out.println("OblID: "+oblId);
	}
	
	public ObsBlock(ProdDatosRedCanaricam prod){
		String aux = prod.getFile().getAbsolutePath();
		aux = aux.replaceAll("(^.+/"+Program.RegExpProgram+"/+"+ObsBlock.RegExpObl+")/.*$", "$1");
		this.directorio=new File(aux);
		this.oblId = directorio.getName().replaceAll("^"+RegExpObl+"$", "$1");
		File parent = this.directorio.getParentFile();
		this.program=new Program(parent);
		
		this.qcFiles = findQcFiles();
		//System.out.println("OblID: "+oblId);
	}

	
	public void test() throws GtcFileException{
		String err = "";
		try{
			testDirectorio();
		}catch(GtcFileException e){
			err += e.getMessage()+"; "; 
		}

		
		try{
			testQcFile();
		}catch(GtcFileException e){
			err += e.getMessage()+"; "; 
		}
		
		
		if(err.length()>0){
			throw new GtcFileException(err);
		}
	}

	
	/**
	 * Clase que testea que todo el directorio de bloque de observacion sea correcto
	 * @throws GtcFileException
	 */
	public void testDirectorio() throws GtcFileException{
		String errors = "";
		if(!this.oblId.matches("[0-9]{4}[a-zA-Z]{0,1}[0-9]*")){
			errors+= "E-0010: Invalid GTC Observing Block name: "+this.getOblId()+";";
		}else if(!this.directorio.isDirectory()){
			errors+= "E-0009: "+this.directorio.getAbsolutePath()+" is not a directory.;";
		}

		if(errors.length()>0){
			throw new GtcFileException(errors);
		}

	}
	
	/**
	 * Clase que testea que exista el fichero de control de calidad
	 * @throws GtcFileException
	 */
	public void testQcFile() throws GtcFileException{
		String errors = "";
		
		if(qcFiles.length<1){
			throw new GtcFileException("W-0001: Quality check file does not exist.");
		}
		
		/// Comprobación de los productos del OB
		File[] ficheros = extraeFits(this.directorio);
		String[] ficherosQcFile = qcFiles[0].getProductFileNames(); 
		
		String msg ="";
		String join = "";

		// Buscamos los ficheros del directorio de OB en el fichero qcFile
		for(int i=0; i<ficheros.length; i++){
			boolean encontrado=false;
			for(int j=0; j<ficherosQcFile.length; j++){
				
				if(ficheros[i].getName().replaceAll(".gz","").equals(ficherosQcFile[j])){
					encontrado=true;
					break;
				}
			}
			if(!encontrado){
				msg+=join+"File "+ficheros[i].getName()+" not found in the file "+qcFiles[0].getFile().getName();
				join=", ";
			}
		}
		
		// Buscamos los ficheros de qcFile en el directorio de OB
		for(int i=0; i<ficherosQcFile.length; i++){
			boolean encontrado=false;
			for(int j=0; j<ficheros.length; j++){
				if(ficherosQcFile[i].equals(ficheros[j].getName().replaceAll(".gz", ""))){
					encontrado=true;
					break;
				}
			}
			if(!encontrado){
				msg+=join+"File "+ficherosQcFile[i]+" in the file "+qcFiles[0].getFile().getName()+" not found in the OB directory";
				join=", ";
			}
		}

		
		if(msg.length()>0){
			errors+= "W-0002: "+msg+";";
		}
		
		if(errors.length()>0){
			throw new GtcFileException(errors);
		}

		
	}
	
	
	
	/**
	 * Entra recursivamente en un directorio y obtiene todos los productos de datos.
	 * 
	 * @param dir
	 * @return
	 * @throws GtcFileException 
	 */
	public ProdDatos[] getProdDatos(){
		Vector<ProdDatos> aux = new Vector<ProdDatos>();
		
		File[] ficheros = extraeFits();

		for(int i=0; i<ficheros.length; i++){
			ProdDatos prodAux = new ProdDatos(ficheros[i]);
			prodAux.setOblock(this);
			aux.add(prodAux);
		}
		
		return (ProdDatos[])aux.toArray(new ProdDatos[0]);
		
		
	}

	
	/**
	 * Entra recursivamente en un directorio y obtiene todos los ficheros .FITS o .FITS.GZ que encuentra.
	 * 
	 * @param dir
	 * @return
	 */
	public static File[] extraeFits(File dir){
		Vector<File> aux = new Vector<File>();
		
		if(!dir.isDirectory()) return new File[0];
		//System.out.println("dir: "+dir);
		File[] ficheros = dir.listFiles();		
		//System.out.println("ObsBlock-229, ficheros en OB: "+ficheros.length);
		for(int i=0; i<ficheros.length; i++){
			if(ficheros[i].isFile() && ficheros[i].getName().toUpperCase().endsWith(".FITS") || 
				ficheros[i].isFile() && ficheros[i].getName().toUpperCase().endsWith(".FITS.GZ")){
				aux.add(ficheros[i]);
			}else if(ficheros[i].isDirectory()){
				File[] ficherosAux = extraeFits(ficheros[i]);
				for(int j=0; j<ficherosAux.length; j++){
					aux.add(ficherosAux[j]);
				}
			}
		}
		
		return (File[])aux.toArray(new File[0]);
	}
	
	/**
	 * Entra recursivamente en un directorio y obtiene todos los ficheros .log o .red que encuentra. (HiPERCAM)
	 * 
	 * @param dir
	 * @return
	 */
	public static File[] extraeRed(File dir){
		Vector<File> aux = new Vector<File>();
		
		if(!dir.isDirectory()) return new File[0];
		
		File[] ficheros = dir.listFiles();
		for(int i=0; i<ficheros.length; i++){
			if(ficheros[i].isFile() && ficheros[i].getName().toUpperCase().endsWith(".RED") || 
				ficheros[i].isFile() && ficheros[i].getName().toUpperCase().endsWith(".LOG")){
				aux.add(ficheros[i]);
			}else if(ficheros[i].isDirectory()){
				File[] ficherosAux = extraeRed(ficheros[i]);
				for(int j=0; j<ficherosAux.length; j++){
					aux.add(ficherosAux[j]);
				}
			}
		}
		
		return (File[])aux.toArray(new File[0]);
	}

	/**
	 * Extrae todos los fits del observing block.
	 * @return
	 */
	public File[] extraeFits(){
		return extraeFits(this.directorio);
	}
	
	/**
	 * Obtiene el fichero QcFile del observing block.
	 * @return
	 */
	public QcFile[] findQcFiles(){
		Vector<QcFile> aux = new Vector<QcFile>();
		File[] qcFileAux = new File[6];
		
		qcFileAux[0] = new File(this.directorio.getAbsolutePath()+"/"+this.getProgram().getProgId()+"_"+this.getOblId()+"_qc.txt");
		qcFileAux[1] = new File(this.directorio.getAbsolutePath()+"/"+this.getProgram().getProgId()+"_"+this.getOblId()+".qc");
		qcFileAux[2] = new File(this.directorio.getAbsolutePath()+"/"+this.getProgram().getProgId()+"_"+this.getOblId()+".qc.txt");

		qcFileAux[3] = new File(this.directorio.getAbsolutePath()+"/"+this.getProgram().getProgId()+"_"+this.getOblId().toLowerCase()+"_qc.txt");
		qcFileAux[4] = new File(this.directorio.getAbsolutePath()+"/"+this.getProgram().getProgId()+"_"+this.getOblId().toLowerCase()+".qc");
		qcFileAux[5] = new File(this.directorio.getAbsolutePath()+"/"+this.getProgram().getProgId()+"_"+this.getOblId().toLowerCase()+".qc.txt");
		
		qcFileAux[5] = new File(this.directorio.getAbsolutePath()+"/"+this.getProgram().getProgId()+"_OB"+this.getOblId().toLowerCase()+"_log.txt");

		//System.out.println(this.directorio.getAbsolutePath()+"/"+this.getProgram().getProgId()+"_OB"+this.getOblId().toLowerCase()+"log.txt");
		//System.out.println("/pcdisk/marconi/raul/proyectos/GTC/gtcData/data/GTC3-09GOS/OB0001a/GTC3-09GOS_qc.txt");
		for(int i=0; i<qcFileAux.length; i++){
			if(qcFileAux[i].exists())	aux.add(new QcFile(this, qcFileAux[i]));
		}
		
		return (QcFile[])aux.toArray(new QcFile[0]);
		
	}
	
	/** 
	 * Determina el modo del observing block.
	 * @return
	 * @throws SQLException 
	 * @throws FitsException 
	 * @throws IOException 
	 */
	public String getModo(Connection con, String ins){
		String modo = null;
		
		// Comprobamos si el observing block ya existe en la BD. Si es así utilizamos
		// el mismo modo. NOTA: En el caso de un OB de datos adicionales, se busca el modo del
		// OB principal.
		ObsBlockAccess oblAccess;
		try {
			oblAccess = new ObsBlockAccess(con);
			modo = oblAccess.selectModId(this.getProgram().getProgId().toUpperCase(), this.getOblId().substring(0,4));
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//if(modo!=null) return modo.trim();
		
		// Determinamos el modo del Observing Block
		File[] ficheros = extraeFits();
		
		
		if(ins.equals("OSI")){
			
			//System.out.println("Osi");
			
			
			// Vemos si es LSS (algún fichero del directorio object contiene LongSlitSpectroscopy)
			for(int i=0; i<ficheros.length; i++){
				if(ficheros[i].getParentFile().getName().toUpperCase().equals("OBJECT")){
					if(ficheros[i].getName().indexOf("LongSlit")>0){
						return "LSS";            
					}else if(ficheros[i].getName().indexOf("HORS")>0){
						modo= "HORUS_TEST"; 
					}
				}
			}
			if(modo!=null && modo.equals("HORUS_TEST")){
				for(int i=0; i<ficheros.length; i++){
					if(ficheros[i].getParentFile().getName().toUpperCase().equals("OBJECT")){
						if(ficheros[i].getName().indexOf("HORS-Spectroscopy")>0){
							return "HORUS";
						}
					}
				}
				if(modo.equals("HORUS_TEST")){
					return null;
				}
			}
			
			// Vemos si es TF o BBI (algún fichero del directorio object contiene OsirisTunableFilterImage)
			for(int i=0; i<ficheros.length; i++){
				
				//System.out.println("TF o BBI");
				
				
				
				if(ficheros[i].getParentFile().getName().toUpperCase().equals("OBJECT")){
					
					
					
					if(ficheros[i].getName().indexOf("TunableFilter")>0){
						modo="TF";
						break;
					}else if(ficheros[i].getName().indexOf("BroadBand")>0){
						
						
						//System.out.println("BBI");
						
						
						boolean compressed = false;
						String tfid = null;
						
						if(ficheros[i].getName().toUpperCase().endsWith(".GZ")){
							compressed = true;
						}
						
						Fits fEntrada;
						try {
							fEntrada = new Fits(ficheros[i], compressed);
						} catch (FitsException e1) {
							e1.printStackTrace();
							return null;
						}
					    	    
					    BasicHDU hdu;
						try {
							hdu = fEntrada.getHDU(0);
						} catch (FitsException e1) {
							e1.printStackTrace();
							return null;
						} catch (IOException e1) {
							e1.printStackTrace();
							return null;
						}
					    Header header = hdu.getHeader();
					    
					    
					    
						// TFID
						try{
							tfid = header.findCard("TFID").getValue();
						}catch(NullPointerException e){}
						
						if(tfid!=null){
							modo="TF";
							break;
						}else{
							modo="BBI";
							break;
						}
					}
				}
			}
			
			//Vemos si es MOS, algún fichero contiene OsirisMOS
			for(int i=0; i<ficheros.length; i++){
				if(ficheros[i].getParentFile().getName().toUpperCase().equals("OBJECT")){
					if(ficheros[i].getName().indexOf("OsirisMOS")>0){
						modo= "MOS";
						break;            
					}
				}
			}
			
		}else if(ins.equals("CC")){
			
			
			// 
			for(int i=0; i<ficheros.length; i++){
				if(ficheros[i].getParentFile().getName().toUpperCase().equals("OBJECT")){
					
					//Si es Spectroscopy, alguno de los ficheros serán así, aunque encontremos imágenes (ACQ)
					if(ficheros[i].getName().indexOf("Spectroscopy")>0){
						modo = "SPE";
						break;
					} else if(ficheros[i].getName().indexOf("Polarimetry")>0){
						modo = "POL";
						break;
					}
					
					
				}
			}
			if(modo==null || !(modo.equalsIgnoreCase("SPE") || modo.equalsIgnoreCase("POL"))){
				for(int i=0; i<ficheros.length; i++){
					if(ficheros[i].getParentFile().getName().toUpperCase().equals("OBJECT")){
					
						if(ficheros[i].getName().indexOf("Imaging")>0){
						modo = "IMG";
						break;
					}
				}
				}
			}
		}else if(ins.equals("CIR")){
			
			//Definir las condiciones de polarimetria
			for(int i=0; i<ficheros.length;i++){
				//Si alguno cumple las condiciones de polarimetría el OB será polarimetría
				if(ficheros[i].getParentFile().getName().equalsIgnoreCase("object")){
					boolean compressed = false;
					if(ficheros[i].getName().toUpperCase().endsWith(".GZ")){
						compressed = true;
					}
					
					Fits fEntrada;
					try {
						fEntrada = new Fits(ficheros[i], compressed);
					} catch (FitsException e1) {
						e1.printStackTrace();
						return null;
					}
					
					BasicHDU hdu;
					try {
						hdu = fEntrada.getHDU(0);
					} catch (FitsException e1) {
						e1.printStackTrace();
						return null;
					} catch (IOException e1) {
						e1.printStackTrace();
						return null;
					}
					
					try{
						Header header = hdu.getHeader();
						
						String HWPRot = header.findCard("HWPROT").getValue().trim();
						String GW = header.findCard("GW").getValue().trim();
						String LinSlide = header.findCard("LINSLIDE").getValue().trim();
						
						if(HWPRot.toUpperCase().startsWith("POL") && GW.equalsIgnoreCase("Wolly") && LinSlide.equalsIgnoreCase("Full_F_Imaging")){
							//En este caso sería polarimetría
							//return "POL";
							modo="POL";
							break;
						}
					}catch(Exception e){
						//No modo porque no puede abrir el fits
					}
				}
			}
			for(int i=0; i<ficheros.length;i++){
				//Si no es polarimetría vemos si es imagen
				if(ficheros[i].getParentFile().getName().equalsIgnoreCase("object")){
					if(ficheros[i].getName().toUpperCase().contains("OTHER_TYPE") || ficheros[i].getName().toUpperCase().contains("IMAG")){
						modo="IMG";
						break;
					}		
				}
			}
					
			
		}else if(ins.equals("EMIR")){
			
			for(int i=0; i<ficheros.length; i++){
				if(ficheros[i].getParentFile().getName().toUpperCase().equals("OBJECT")){
					
					//Si es Spectroscopy, ficheros deben ser STARE_SPECTRO
					//Si es Imagen, ficheros deben ser STARE_IMAGE
					if(ficheros[i].getName().indexOf("STARE_IMAGE")>0){
						modo = "IMG";
						break;
					} else if(ficheros[i].getName().indexOf("STARE_SPECTRA")>0){
						modo = "SPE";
						break;
					}
					
					
				}
			}
			//Comprobamos que todos lo ficheros son así, si no hay que ver los direntes casos
			if(modo!=null){
				
				boolean mezclados = false;
				
				if(modo.equalsIgnoreCase("SPE")){
				
					for(int i=0; i<ficheros.length; i++){
						if(ficheros[i].getParentFile().getName().toUpperCase().equals("OBJECT")){
						
							if(ficheros[i].getName().indexOf("STARE_IMAGE")>0){
								mezclados = true; // tiene varios mezclados
								//modo = null;//No existe este modo
								//break;
						}
					}
					}
				}else if(modo.equalsIgnoreCase("IMG")){
					for(int i=0; i<ficheros.length; i++){
						if(ficheros[i].getParentFile().getName().toUpperCase().equals("OBJECT")){
							
							if(ficheros[i].getName().indexOf("STARE_SPECTRA")>0){
								mezclados = true; // tiene varios mezclados
								//modo = null;//No existe este modo
								//break;
						}
					}
					}
				}
				
				if(mezclados){
					//Comprobamos si los Spectra tienen el grism=open, se trataría de imagen de calibración
					
					boolean gri_open=false;//No hay ningún fichero con gri=open, es spec entonces
					
					for(int i=0; i<ficheros.length; i++){
						if(ficheros[i].getParentFile().getName().toUpperCase().equals("OBJECT")){
							
							if(ficheros[i].getName().indexOf("STARE_SPECTRA")>0){
							
								boolean compressed = false;
								if(ficheros[i].getName().toUpperCase().endsWith(".GZ")){
									compressed = true;
								}
								
								Fits fEntrada;
								try {
									fEntrada = new Fits(ficheros[i], compressed);
						
									BasicHDU hdu;
								
									hdu = fEntrada.getHDU(0);
								
									Header header = hdu.getHeader();
									
									String Grism = header.findCard("GRISM").getValue().trim();
									if(!Grism.equalsIgnoreCase("OPEN")){
										//No es open, hay img y spe mezclados --> ERROR
										//modo = null;//No existe este modo
										//break;
										gri_open=true;
										
									}
								}catch(Exception e){
									
								}
							}
						}
					}
					
					if(gri_open){
						//modo="IMG";
						modo="SPE";
					}else{
						//modo="SPE";
						modo="IMG";
					}
					
				}
			}
					
		}else if(ins.equals("HIPERCAM")){	
			
			File[] logs = extraeRed(this.directorio);
			
			if(logs.length>0){
				modo = "RED";
			}else{
				modo = "IMG";
			}
			
			
		}else if(ins.equals("MEGARA")){
			
			for(int i=0; i<ficheros.length; i++){
				if(ficheros[i].getParentFile().getName().toUpperCase().equals("OBJECT")){
					
					//Si es Spectroscopy, ficheros deben ser MOSImage o MOSAcquisition
					//Si es Image, ficheros deber ser LcbImage o LcbAcquisition
					
					//comprobamos si tiene ficheros Lcb
					if(ficheros[i].getName().indexOf("LcbImage")>0 || ficheros[i].getName().indexOf("LcbAcquisition")>0){
						modo = "IFU";
	
					}else if(ficheros[i].getName().indexOf("MOSImage")>0 || ficheros[i].getName().indexOf("MOSAcquisition")>0){
						modo = "SPE";
	
					}
				}
			}
			
			//Comprobamos que todos lo ficheros son así, si hay de varios tipos no tenemos modo
			if(modo!=null){
				
				if(modo.equalsIgnoreCase("SPE")){
				
					for(int i=0; i<ficheros.length; i++){
						if(ficheros[i].getParentFile().getName().toUpperCase().equals("OBJECT")){
						
							if(ficheros[i].getName().indexOf("LcbImage")>0 || ficheros[i].getName().indexOf("LcbAcquisition")>0){
								modo = null;//No existe este modo
								break;
							}
					}
					}
				}else if(modo.equalsIgnoreCase("IFU")){
					for(int i=0; i<ficheros.length; i++){
						if(ficheros[i].getParentFile().getName().toUpperCase().equals("OBJECT")){
							
							if(ficheros[i].getName().indexOf("MOSImage")>0 || ficheros[i].getName().indexOf("MOSAcquisition")>0){
								modo = null;//No existe este modo
								break;
						}
					}
					}
				}
			}
					
		
		
		}else if(ins.equalsIgnoreCase("HORuS")){	
			
			for(int i=0; i<ficheros.length; i++){
				if(ficheros[i].getParentFile().getName().toUpperCase().equals("OBJECT")){
					
					//Tiene que contener al menos un fichero Spectroscopy
					if(ficheros[i].getName().indexOf("HORS-Spectroscopy")>0){
						modo = "SPE";
						break;
					}
				}
			}
			
		}else{
			//En este caso no se ha especificado el instrumento
			modo=null;
			
			

			/*// Vemos si es LSS (algún fichero del directorio object contiene LongSlitSpectroscopy)
			for(int i=0; i<ficheros.length; i++){
				if(ficheros[i].getParentFile().getName().toUpperCase().equals("OBJECT")){
					if(ficheros[i].getName().indexOf("LongSlit")>0){
						return "LSS";            
					}
				}
			}
			// Vemos si es TF o BBI (algún fichero del directorio object contiene OsirisTunableFilterImage)
			for(int i=0; i<ficheros.length; i++){
				if(ficheros[i].getParentFile().getName().toUpperCase().equals("OBJECT")){
					if(ficheros[i].getName().indexOf("TunableFilter")>0){
						modo="TF";
						break;
					}else if(ficheros[i].getName().indexOf("BroadBand")>0){
						boolean compressed = false;
						String tfid = null;
						
						if(ficheros[i].getName().toUpperCase().endsWith(".GZ")){
							compressed = true;
						}
						
						Fits fEntrada;
						try {
							fEntrada = new Fits(ficheros[i], compressed);
						} catch (FitsException e1) {
							e1.printStackTrace();
							return null;
						}
					    	    
					    BasicHDU hdu;
						try {
							hdu = fEntrada.getHDU(0);
						} catch (FitsException e1) {
							e1.printStackTrace();
							return null;
						} catch (IOException e1) {
							e1.printStackTrace();
							return null;
						}
					    Header header = hdu.getHeader();
					    
						// TFID
						try{
							tfid = header.findCard("TFID").getValue();
						}catch(NullPointerException e){}
						
						if(tfid!=null){
							modo="TF";
							break;
						}else{
							modo="BBI";
							break;
						}
					}
				}
			}

			//Vemos si es MOS, algún fichero contiene OsirisMOS
			for(int i=0; i<ficheros.length; i++){
				if(ficheros[i].getParentFile().getName().toUpperCase().equals("OBJECT")){
					if(ficheros[i].getName().indexOf("OsirisMOS")>0){
						modo= "MOS";
						return "MOS";            
					}
				}
			}
			
			// 
			for(int i=0; i<ficheros.length; i++){
				if(ficheros[i].getParentFile().getName().toUpperCase().equals("OBJECT")){
					
					//Si es Spectroscopy, alguno de los ficheros serán así, aunque encontremos imágenes (ACQ)
					if(ficheros[i].getName().indexOf("Spectroscopy")>0){
						modo = "SPE";
						break;
					} else if(ficheros[i].getName().indexOf("Polarimetry")>0){
						modo = "POL";
						break;
					}
					
					
				}
			}
			if(modo==null || !(modo.equalsIgnoreCase("SPE") || modo.equalsIgnoreCase("POL"))){
				for(int i=0; i<ficheros.length; i++){
					if(ficheros[i].getParentFile().getName().toUpperCase().equals("OBJECT")){
						
						if(ficheros[i].getName().indexOf("Imaging")>0){
							modo = "IMG";
							break;
						}
					}
				}
			}
		*/
			}
		
		
		
		
		return modo;

	}
	
	public String getModo(Connection con){

		String modo = null;
		
		// Comprobamos si el observing block ya existe en la BD. Si es así utilizamos
		// el mismo modo. NOTA: En el caso de un OB de datos adicionales, se busca el modo del
		// OB principal.
		ObsBlockAccess oblAccess;
		try {
			oblAccess = new ObsBlockAccess(con);
			modo = oblAccess.selectModId(this.getProgram().getProgId().toUpperCase(), this.getOblId().substring(0,4));
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//if(modo!=null) return modo.trim();
		
		// Determinamos el modo del Observing Block
		File[] otroDir = this.directorio.listFiles();
		
		for(int k=0; k<otroDir.length; k++){
			
		if(otroDir[k].getName().contains("object")){
					
		String[] ficheros = otroDir[k].list();
		
			
			for(int i=0; i<ficheros.length; i++){
				if(ficheros[i].contains("EMIR")){
					
					//Si es Spectroscopy, ficheros deben ser STARE_SPECTRO
					//Si es Imagen, ficheros deben ser STARE_IMAGE
					if(ficheros[i].indexOf("STARE_IMAGE")>0){
						modo = "IMG";
						break;
					} else if(ficheros[i].indexOf("STARE_SPECTRA")>0){
						modo = "SPE";
						break;
					}
					
					
				}
			}
			//Comprobamos que todos lo ficheros son así, si no hay que ver los direntes casos
			if(modo!=null){
				
				boolean mezclados = false;
				
				if(modo.equalsIgnoreCase("SPE")){
				
					for(int i=0; i<ficheros.length; i++){
						if(ficheros[i].indexOf("STARE_IMAGE")>0){
							mezclados = true; // tiene varios mezclados
							//modo = null;//No existe este modo
							//break;
						}
					
					}
				}else if(modo.equalsIgnoreCase("IMG")){
					for(int i=0; i<ficheros.length; i++){
						if(ficheros[i].indexOf("STARE_SPECTRA")>0){
							mezclados = true; // tiene varios mezclados
							//modo = null;//No existe este modo
							//break;
						
					}
					}
				}
				
				if(mezclados){
					
					//Comprobamos si los Spectra tienen el grism=open, se trataría de imagen de calibración
					
					boolean gri_open=false;//No hay ningún fichero con gri=open, es spec entonces
					
					for(int i=0; i<ficheros.length; i++){
						if(ficheros[i].indexOf("STARE_SPECTRA")>0){
							
							
							File emirFile = new File(this.directorio.getAbsolutePath()+"/object/"+ficheros[i]);
							boolean compressed = false;
							if(ficheros[i].toUpperCase().endsWith(".GZ")){
								compressed = true;
							}
								
							Fits fEntrada;
							try {
								fEntrada = new Fits(emirFile, compressed);
						
								BasicHDU hdu;
								
								hdu = fEntrada.getHDU(0);
								
								Header header = hdu.getHeader();
									
								String Grism = header.findCard("GRISM").getValue().trim();
								
								if(!Grism.equalsIgnoreCase("OPEN")){
										//No es open, hay img y spe mezclados --> ERROR
										//modo = null;//No existe este modo
										//break;
									
									gri_open=true;
										
								}
							}catch(Exception e){
									e.printStackTrace();
							}
							
						}
					}
					
					if(gri_open){
						//modo="IMG";
						modo="SPE";
					}else{
						//modo="SPE";
						modo="IMG";
					}
					
				}
			}
		}}
					
		return modo;
	
	}
	
	
	/**
	 * Marca todos los productos de datos del observing block como erroneos, insertandolos
	 * en la tabla ProdError y asociandoles el correspondiente error.
	 * 
	 * @param con
	 * @param errores
	 * @throws SQLException 
	 * @throws GtcFileException 
	 */
	public void markErrorsOnAllProducts(Connection con, Integer bpathId, ErrorDb[] errores) throws SQLException{
		
		// RECORREMOS LOS PRODUCTOS
		ProdDatos[] prods= new ProdDatos[0];
		prods = this.getProdDatos();
		for(int k=0; k<prods.length; k++){
			prods[k].insertaErrorBD(con, bpathId, errores);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////

	public Program getProgram() {
		return program;
	}


	public void setProgram(Program program) {
		this.program = program;
	}

	public File getDirectorio() {
		return directorio;
	}


	public void setDirectorio(File directorio) {
		this.directorio = directorio;
	}


	public String getOblId() {
		return oblId.toUpperCase().replaceAll("\\s", "");
	}


	public void setOblId(String oblId) {
		this.oblId = oblId;
	}

	public QcFile[] getQcFiles() {
		return qcFiles;
	}

	public void setQcFiles(QcFile[] qcFiles) {
		this.qcFiles = qcFiles;
	}

	public static void main(String[] args) throws GtcFileException{
		
		File directorio = new File("/pcdisk/marconi/raul/proyectos/GTC/gtcData/data/GTC3-09GOS/OB0001a");
		ObsBlock obl = new ObsBlock(directorio);
		obl.test();
		
	}
	
}
