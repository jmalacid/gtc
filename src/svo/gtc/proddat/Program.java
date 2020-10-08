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
import java.util.Arrays;
import java.util.Vector;

public class Program {
	//public static final String RegExpProgram = "^.+/(GTC[^/]+)/+"+ObsBlock.RegExpObl+"/.*$";
	public static final String RegExpProgram = "GTC[^/]+";
	public static final String RegExpSemester = "[0-9]{4}[ABE].*";

	private File		directorio		= null;
	private String 		progId 		= null;
	private Integer		progYear	= null;
	private String 		progSemester= null;
	
	
	public Program(File directorio){
		this.directorio=directorio;
		this.progId = directorio.getName();
		this.progYear=	new Integer(directorio.getParentFile().getName().substring(0,4));
		this.progSemester = directorio.getParentFile().getName().substring(4,5);
		if(this.progSemester.equalsIgnoreCase("E")){
			this.progSemester ="ESO";
		}
	}
	
	public Program(File directorio, Integer progYear, String progSemester){
		this.directorio=directorio;
		this.progId = directorio.getName();
		this.progYear=	progYear;
		this.progSemester = progSemester;
		if(this.progSemester.equalsIgnoreCase("E")){
			this.progSemester ="ESO";
		}
	}
	
	public Program(ProdDatos prod){
		String aux = prod.getFile().getAbsolutePath();
		aux = aux.replaceAll("(^.+/"+RegExpSemester+"/"+RegExpProgram+")/+"+ObsBlock.RegExpObl+"/.*$", "$1");
		this.directorio=new File(aux);
		this.progId=directorio.getName();
		
		this.progYear=	new Integer(directorio.getParentFile().getName().substring(0,4));
		this.progSemester = directorio.getParentFile().getName().substring(4,5);
		if(this.progSemester.equalsIgnoreCase("E")){
			this.progSemester ="ESO";
		}

	}
	
	public Program(ProdDatosRedCanaricam prod){
		String aux = prod.getFile().getAbsolutePath();
		aux = aux.replaceAll("(^.+/"+RegExpSemester+"/"+RegExpProgram+")/+"+ObsBlock.RegExpObl+"/.*$", "$1");
		this.directorio=new File(aux);
		this.progId=directorio.getName();
		
		this.progYear=	new Integer(directorio.getParentFile().getName().substring(0,4));
		this.progSemester = directorio.getParentFile().getName().substring(4,5);
		if(this.progSemester.equalsIgnoreCase("E")){
			this.progSemester ="ESO";
		}

	}

	
	/**
	 * Clase que testea que todo el directorio de programa sea correcto
	 * @throws GtcFileException
	 */
	public void test() throws GtcFileException{
		String errors = "";
		
		if(!this.progId.matches("^"+Program.RegExpProgram+"$")){
			errors+= "E-0002: Invalid GTC program name: "+this.progId;
		}else if(!this.directorio.isDirectory()){
			errors+= "E-0001: "+this.directorio.getAbsolutePath()+" is not a directory.;";
		}
		
		if(errors.length()>0){
			throw new GtcFileException(errors);
		}
	}
	
	/**
	 * Entra recursivamente en un directorio y obtiene todos los observing blocks.
	 * 
	 * @param dir
	 * @return
	 * @throws GtcFileException 
	 */
	public ObsBlock[] getObsBlocks() throws GtcFileException{
		Vector<ObsBlock> aux = new Vector<ObsBlock>();
		
		File[] ficheros = this.directorio.listFiles();
		Arrays.sort(ficheros);
		
		for(int i=0; i<ficheros.length; i++){
			ObsBlock oblock = new ObsBlock(ficheros[i]);
			oblock.setProgram(this);
			aux.add(new ObsBlock(ficheros[i]));
		}
		
		return (ObsBlock[])aux.toArray(new ObsBlock[0]);
		
		
	}


	//////////////////////////////////////////////////////////////////
	
	public File getDirectorio() {
		return directorio;
	}


	public void setDirectorio(File directorio) {
		this.directorio = directorio;
	}


	public String getProgId() {
		return progId.toUpperCase().replaceAll("\\s", "");
	}


	public void setProgId(String progId) {
		this.progId = progId;
	}

	public Integer getProgYear() {
		return progYear;
	}

	public void setProgYear(Integer progYear) {
		this.progYear = progYear;
	}

	public String getProgSemester() {
		return progSemester;
	}

	public void setProgSemester(String progSemester) {
		this.progSemester = progSemester;
	}


	
}
