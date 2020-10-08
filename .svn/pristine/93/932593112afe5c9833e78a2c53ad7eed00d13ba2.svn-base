/*
 * @(#)LogIngestion.java    25/01/2012
 *
 *
 * Raúl Gutiérrez Sánchez. (raul@laeff.inta.es)	
 * LAEFF: 	Laboratorio de Astrofísica Espacial y Física Fundamental.
 *        	INTA
 *			Villafranca del Castillo Satellite Tracking Station
 *			Villafranca del Castillo, E-28691 Villanueva de la Cañada
 *			Madrid - España
 *			
 * Phone:  34 91-8131260
 * Fax..:  34 91-8131160   
 * -----------------------------------------------------------------------
 * 
 *	ESTE  PROGRAMA  ES  DE  USO EXCLUSIVO.  ES  PROPIEDAD  DE  SU AUTOR  Raúl 
 *  Gutiérrez Sánchez  Y SE PRESENTA "COMO ESTA" SIN NINGUN TIPO DE GARANTIAS
 *  DENTRO Y FUERA  DE LA FINALIDAD PARA  LA QUE EL  AUTOR LO  HIZO. EL AUTOR 
 *  PERMITE  SU USO SIEMPRE QUE  SE LE REFERENCIE. SE PERMITE LA MODIFICACION
 *  DE SU  FUENTE SIEMPRE QUE SE  HAGA REFERENCIA A SU AUTORIA Y JUNTO A ELLA
 *  SE  ADJUNTE LA IDENTIFICACION DEL  AUTOR DE LAS NUEVAS MODIFICACIONES. EL
 *  AUTOR  NUNCA SERA RESPONSABLE DEL USO DE ESTE  PROGRAMA NI DE LOS EFECTOS
 *  QUE ESTE PROGRAMA PUDIEDA CAUSAR EN DETERIOROS, ALTERACIONES Y/O PERDIDAS 
 *  DE INFORMACION.
 *	
 */

package svo.gtc.ingestion;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class LogIngestion {
	private String name;
	private HashMap<String, LogProgram> programas = new HashMap<String, LogProgram>();
	private BufferedInputStream stream;
	
	public static void main(String[] args) throws Exception{
		//LogIngestion log = new LogIngestion(new FileInputStream("/pcdisk/marconi/raul/proyectos/GTC/gtcData/logs/ingestion/logIngestionGTC_2011-10-26 16:40:28.079.log"));
	}
	
	
	public LogIngestion(BufferedInputStream stream) throws Exception{
		this.stream=stream;
		
		procesaFichero();
	}
	
	public LogProgram[] getPrograms(){
		//return Programs;
		return null;
	}

	private void procesaFichero() throws Exception{
		BufferedReader reader = new BufferedReader(new InputStreamReader(this.stream));
		
		String linea = "";
		
		LogProgram program = null;
		LogOblock oblock = null;
		
		while((linea=reader.readLine())!=null){
			if(linea.matches("^GTC\\w+.*$")){
				program = new LogProgram(linea);
				
				if(programas.get(program.getName())==null){
					programas.put(program.getName(), program);
				}else{
					program = (LogProgram)programas.get(program.getName());
				}
			}else if(linea.matches("^\\t\\w+.*$")){
				oblock = new LogOblock(linea);
				
				if(program.getOblock(oblock.getName())==null){
					program.addOblock(oblock);
				}else{
					oblock = program.getOblock(oblock.getName());
				}
			}else if(linea.matches("^\\t\\t\\w+.*$")){
				LogProduct prod = new LogProduct(linea);
				
				if(oblock.getProduct(prod.getName())==null){
					oblock.addProduct(prod);
				}
			}
			
			
		}
		
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public LogProgram[] getProgramas(){
		Iterator<LogProgram> it =  programas.values().iterator();
		Vector<LogProgram> aux = new Vector<LogProgram>();
		while(it.hasNext()){
			aux.add(it.next());
		}
		
		LogProgram[] salida = (LogProgram[])aux.toArray(new LogProgram[0]);
		Arrays.sort(salida);
		
		return salida;
	}
	
	
	
	
}
