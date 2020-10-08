/*
 * @(#)LogProduct.java    25/01/2012
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

import java.util.Vector;

public class LogProduct implements Comparable<LogProduct>{
	private String name;
	private LogError[] errores=new LogError[0];
	
	public LogProduct(String logLine) throws Exception{
		String pattern = "^\\t\\t([^:]+)\\.fits\\.gz[^:]+:\\s(.+)*$";
		
		if(!logLine.matches(pattern)) throw new Exception("La línea del fichero de log no tiene formato adecuado para producto: "+logLine);
		
		this.name=logLine.replaceAll(pattern, "$1");
		setErrores(logLine.replaceAll(pattern, "$2"));
	}

	/**
	 * Extrae los mensajes de error y sus identificadores de un mensaje con una lista de errores.
	 * @param mensaje
	 */
	private void setErrores(String mensaje){
		if(mensaje==null || mensaje.trim().length()==0) return;
		
		String pattern = "^.*([EW]-.*[0-9]{4}):(.+)$";
		Vector<LogError> aux = new Vector<LogError>();
		
		String[] bloques = mensaje.split(";");
		
		for(int i=0; i<bloques.length; i++){
			if(bloques[i].matches(pattern)){
				LogError error = new LogError();
				
				error.setErrId(bloques[i].replaceAll(pattern, "$1"));
				error.setErrDesc(bloques[i].replaceAll(pattern, "$2").trim());
				
				aux.add(error);
			}
		}
		
		errores=(LogError[])aux.toArray(new LogError[0]);
	}


	
	public String getName() {
		return name;
	}

	public LogError[] getErrores() {
		return errores;
	}

	public int compareTo(LogProduct o) {
		return this.getName().compareTo(o.getName());
	}
}
