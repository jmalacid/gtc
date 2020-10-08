/*
 * @(#)GtcWebUtils.java    14/02/2012
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

package svo.gtc.web;

public class WebUtils {
	
	/**
	 * Genera un identificador de producto usado en la web para pasar de unas páginas a otras.
	 * @param progId
	 * @param oblId
	 * @param prodId
	 * @return
	 */
	public static String generaWebProdId(String progId, String oblId, Integer prodId){
		if(progId!=null && oblId!=null && progId.trim().length()>0 && oblId.trim().length()>0){
			return progId+".."+oblId+".."+prodId.intValue();
		}else{
			return "";
		} 
	}
	
	
	/**
	 * Extrae la parte de progId de un identificador web de producto.
	 * @param webProdId
	 * @return
	 */
	public static String getWebProgId(String webProdId){
		String[] aux = webProdId.split("\\.\\.");
		try{
			if(aux[0].trim().length()>0) return aux[0];
			else return "";
		}catch(Exception e){
			return "";
		}
	}
	/**
	 * Extrae la parte de oblId de un identificador web de producto.
	 * @param webProdId
	 * @return
	 */
	public static String getWebOblId(String webProdId){
		String[] aux = webProdId.split("\\.\\.");
		try{
			if(aux[1].trim().length()>0) return aux[1];
			else return "";
		}catch(Exception e){
			return "";
		}
	}
	/**
	 * Extrae la parte de prodId de un identificador web de producto.
	 * @param webProdId
	 * @return
	 */
	public static Integer getWebProdId(String webProdId){
		String[] aux = webProdId.split("\\.\\.");
		try{
			if(aux[2].trim().length()>0) return new Integer(aux[2]);
			else return null;
		}catch(Exception e){
			return null;
		}
	}

}
