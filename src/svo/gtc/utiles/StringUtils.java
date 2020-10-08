/*
 * @(#)StringUtils.java    2004-02-09
 *
 *
 * Ra?l Guti?rrez S?nchez. (raul@cab.inta-csic.es)	
 * LAEFF: 	Laboratorio de Astrof?sica Espacial y F?sica Fundamental.
 *        	INTA
 *			Villafranca del Castillo Satellite Tracking Station
 *			Villafranca del Castillo, E-28691 Villanueva de la Ca?ada
 *			Madrid - Espa?a
 *			
 * Phone:  34 91-8131260
 * Fax..:  34 91-8131160   
 * -----------------------------------------------------------------------
 * 
 *	ESTE  PROGRAMA  ES  DE  USO EXCLUSIVO.  ES  PROPIEDAD  DE  SU AUTOR  Ra?l 
 *  Guti?rrez S?nchez  Y SE PRESENTA "COMO ESTA" SIN NINGUN TIPO DE GARANTIAS
 *  DENTRO Y FUERA  DE LA FINALIDAD PARA  LA QUE EL  AUTOR LO  HIZO. EL AUTOR 
 *  PERMITE  SU USO SIEMPRE QUE  SE LE REFERENCIE. SE PERMITE LA MODIFICACION
 *  DE SU  FUENTE SIEMPRE QUE SE  HAGA REFERENCIA A SU AUTORIA Y JUNTO A ELLA
 *  SE  ADJUNTE LA IDENTIFICACION DEL  AUTOR DE LAS NUEVAS MODIFICACIONES. EL
 *  AUTOR  NUNCA SERA RESPONSABLE DEL USO DE ESTE  PROGRAMA NI DE LOS EFECTOS
 *  QUE ESTE PROGRAMA PUDIEDA CAUSAR EN DETERIOROS, ALTERACIONES Y/O PERDIDAS 
 *  DE INFORMACION.
 *	
 * @author Ra?l Guti?rrez S?nchez(raul@cab.inta-csic.es)
 * @version 0.0, 2004-02-09
 */

package svo.gtc.utiles;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Herramientas para manejo de cadenas de caracteres.
 *  
 * @author Raul Gutierrez Sanchez
 * @version 0.0, 2004-02-09
 *
 */

public class StringUtils {

	//---Variables y areas de trabajo. ---------------------------------
	int contador = 0;

	public StringUtils() {
	}
	
	/**
	 * <P>
	 * Rellenado de un String con ceros a la izquierda hasta el tama?o
	 * <UL>
	 * LLAMADA
	 * <LI>String salida = StringUtils.zerofill(String entrada, int
	 * longitud_final);
	 * </UL>
	 * 
	 * @deprecated Use lpad or rpad instead.
	 * @author Raul Gutierrez Sanchez (raul@cab.inta-csic.es)
	 * @version 0.0, 2004-02-09
	 */

	public static String zerofill(String entrada, int longitudFinal) {
		String salida = "";

		for (int i = 0; i < longitudFinal - entrada.trim().length(); i++) {
			salida += "0";
		}

		salida += entrada.trim();

		return salida;

	}

	/**
	 * <P>
	 * Rellenado de un String con blancos a la izquierda hasta el tamano
	 * especificado
	 * <UL>
	 * LLAMADA
	 * <LI>String salida = StringUtils.whitefill(String entrada, int
	 * longitud_final);
	 * </UL>
	 * 
	 * @deprecated Use lpad or rpad instead.
	 * @author Ral Gutirrez Snchez (raul@cab.inta-csic.es)
	 * @version 0.0, 2004-02-09
	 */

	public static String whitefill(String entrada, int longitudFinal) {
		String salida = "";

		for (int i = 0; i < longitudFinal - entrada.trim().length(); i++) {
			salida += " ";
		}

		salida += entrada.trim();

		return salida;

	}

	/**
	 * <P>
	 * Marca una cadena con barras invertidas frente a los caracteres que
	 * necesitan marcarse en consultas de bases de datos, etc. Estos son la
	 * comilla simple ('), la comilla doble(") y la barra invertida (\)
	 * <UL>
	 * LLAMADA
	 * <LI>String salida = StringUtils.addSlashes(String entrada);
	 * </UL>
	 * 
	 * @author Ra?l Guti?rrez S?nchez (raul@cab.inta-csic.es)
	 * @version 0.0, 2004-03-08
	 */

	public static String addSlashes(String entrada) {
		String salida = entrada;

		salida = salida.replaceAll("\\\\", "\\\\\\\\");
		salida = salida.replaceAll("'", "\\\\'");
		salida = salida.replaceAll("\"", "\\\\\\\"");

		return salida;
	}

	/**
	 * Devuelve un string que representa el nmero con el formato especificado.
	 * 
	 * @param numero
	 *            numero que quiere ser formateado
	 * @param format
	 *            formato a aplicar al numero
	 * @param anchura
	 *            ancura del string total (se anaden tantos espacios en blanco
	 *            al inicio del numero como sean necesarios.
	 * @return string con la representacion del numero con el formato
	 *         especificado.
	 */

	public static String formateaNumero(double numero, String format,
			int anchura) {

		String salida = "";

		NumberFormat nf = NumberFormat
				.getNumberInstance(new Locale("en", "US"));
		DecimalFormat df = (DecimalFormat) nf;
		df.applyPattern(format);

		salida = df.format(numero);
		int longNumero = salida.length();

		salida = StringUtils.lpad(salida, ' ', anchura);

		return salida;
	}
	
	/**
	 * Limita el numero de decimales.
	 * 
	 * @param numero
	 *            numero que quiere ser formateado
	 * @param numDecimales
	 *            numero m�ximo de decimales.
	 * @return string con la representacion del numero con el formato
	 *         especificado.
	 */

	public static String recortaDecimales(double numero, int numDecimales) {

		String salida = "";
		String pattern = "0.";
		for(int i=0; i<numDecimales; i++) pattern+="#";
		pattern+="E0";
		
		
		NumberFormat nf = NumberFormat
				.getNumberInstance(new Locale("en", "US"));
		DecimalFormat df = (DecimalFormat) nf;
		df.applyPattern(pattern);

		salida = df.format(numero);

		return salida;
	}
	
	

	/**
	 * Rellenado de las posiciones a la izquierda de un String con un caracter
	 * determinado.
	 * 
	 * @param entrada
	 *            string que quiere rellenarse.
	 * @param caracter
	 *            caracter de relleno.
	 * @param longitudFinal
	 *            longitud final del string.
	 * @return string modificado.
	 */

	public static String lpad(String entrada, char caracter, int longitudFinal) {
		String salida = "";

		for (int i = 0; i < longitudFinal - entrada.trim().length(); i++) {
			salida = caracter + salida;
		}

		salida += entrada.trim();

		return salida;
	}

	/**
	 * Rellenado de las posiciones a la derecha de un String con un caracter
	 * determinado.
	 * 
	 * @param entrada
	 *            string que quiere rellenarse.
	 * @param caracter
	 *            caracter de relleno.
	 * @param longitudFinal
	 *            longitud final del string.
	 * @return string modificado.
	 */

	public static String rpad(String entrada, char caracter, int longitudFinal) {
		String salida = "";

		salida += entrada.trim();

		for (int i = 0; i < longitudFinal - entrada.trim().length(); i++) {
			salida += caracter;
		}

		return salida;
	}

	/**
	 * Devuelve el n�mero de veces que aparece strbusqueda dentro de string.
	 * 
	 * @param string
	 *            string sobre el que ha de buscarse.
	 * @param strbusqueda
	 *            string del que quiere saberse el numero de veces que aparece.
	 * @return int con el numero de apariciones.
	 */

	public static int cuentaOcurrencias(String string, String regex) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(string); // get a matcher object
		int count = 0;
		while (m.find()) {
			count++;
			//System.out.println("Match number "+count);
			//System.out.println("start(): "+m.start());
			//System.out.println("end(): "+m.end());
		}
		return count;
	}

}

