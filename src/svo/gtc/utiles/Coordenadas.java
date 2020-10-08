/*
 * @(#)Coordenadas.java    2003-06-12
 *
 *
 * Ra�l Guti�rrez S�nchez. (raul@cab.inta-csic.es)	
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
 * @author Ra�l Guti�rrez S�nchez(raul@cab.inta-csic.es)
 * @version 0.0, 2003-06-12
 */

package svo.gtc.utiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Math;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

/**
 * <H2>Coordenadas</H2>
 *
 * <P>Herramientas para el manejo de coordenadas
 *    <UL>Consultas.
 *        <LI>gms2double(): Paso de numero en grados, minutos y segundos a su representacion
 *							en grados como un double.  
 *        <LI>hms2double(): Paso de numero en horas, minutos y segundos a su representacion
 *							en horas como un double.  
 *        <LI>double2gms(): Paso de numero en grados como un double a su representacion en 
 *							grados, minutos y segundos.  
 *        <LI>double2hms(): Paso de numero en grados como un double a su representacion en 
 *							horas, minutos y segundos.  
 *    </UL>
 *
 * @author Ra�l Guti�rrez S�nchez (raul@cab.inta-csic.es)
 * @version 0.0, 2003-06-12
 */
public class Coordenadas {
    
  //---Variables y areas de trabajo. ---------------------------------
  int contador = 0;
  
  static String patronCoo1 = "([\\+\\-]?[0-9]+(?:\\.[0-9]+)?)\\s+([\\+\\-]?[0-9]+(?:\\.[0-9]+)?)";
  
  static String patronCoo2 = "([\\+\\-]?[0-9]{1,2})[\\s\\:]+([0-9]{1,2})[\\s\\:]+([0-9]+(?:\\.[0-9]+)?)\\s+" +
  "([\\+\\-]?[0-9]{1,2})[\\s\\:]+([0-9]{1,2})[\\s\\:]+([0-9]+(?:\\.[0-9]+)?)";

 
  
  public Coordenadas() {
  }
  
  public static void main(String[] args){
	  
	  Double[] coords = Coordenadas.coordsInDeg("09:14:29.13 -00:07:41.5");
	  System.out.println(coords[0]+" // "+coords[1]);
	  
	  String[] coor = new String[9]; 
	  coor[0] = "12 45 43 13 43 54";
	  coor[1] = "10.2334 25.234";
	  coor[2] = "12  45  :  43.4234    13    43    54.24434";
	  coor[3] = "18:27:45.79 +34:36:18.77";
	  coor[4] = "18 34";
	  coor[5] = "12:15:18.6 45:28:59";
	  coor[6] = "242.6025 43.8169";
	  coor[7] = "25.82 36.45";
	  coor[8] = "25 36";
	  //for(int i=0; i<coor.length; i++)  System.out.println(checkCoordinatesFormat(coor[i])+": "+coor[i]);
	  
	  for(int i=0; i<coor.length; i++){
		  Double[] degrees = coordsInDeg(coor[i]);
		 // System.out.println(degrees[0]+" "+degrees[1]);
	  }
	  
  }


	/**
	* <P>Paso de numero en grados como un double a su representacion en 
    *	 grados, minutos y segundos
	* <UL>LLAMADA
	*     <LI>String[] salida = Coordenadas.double2gms(double gms);
	* </UL>
	*  
	* @author Ra�l Guti�rrez S�nchez (raul@cab.inta-csic.es)
	* @version 0.0, 2003-06-12
	*/
   
	public static String[] double2gms(double numero) {
		
		String[] salida = new String[3];

      double grados_sinsigno = Math.abs(numero);
      int resultado_grados = (int) Math.floor(grados_sinsigno);
      if(numero >=0){
      	salida[0]=StringUtils.lpad(""+resultado_grados,'0',2);    
      }
      else{
      	salida[0]= "-"+StringUtils.lpad(""+resultado_grados,'0',2);
      }

      double minutos = (grados_sinsigno-resultado_grados)*60;
      int resultado_minutos= (int) Math.floor(minutos);
      salida[1]= StringUtils.lpad(""+resultado_minutos,'0',2);

      double resultado_segundos = (minutos-resultado_minutos)*60;
      salida[2]= ""+StringUtils.formateaNumero(resultado_segundos,"00.###########################",0);

      return(salida);

    } 


	/**
	* <P>Paso de numero en grados como un double a su representacion en 
    *	 horas, minutos y segundos
	* <UL>LLAMADA
	*     <LI>String[] salida = Coordenadas.double2hms(double gms);
	* </UL>
	*  
	* @author Ra�l Guti�rrez S�nchez (raul@cab.inta-csic.es)
	* @version 0.0, 2003-06-12
	*/
   
	public static String[] double2hms(double numero) {
		
		String salida[] = new String[3];

      double horas = numero*12/180;
      int resultado_horas = (int)Math.floor(horas);
		salida[0]= StringUtils.lpad(""+resultado_horas,'0',2);
		
      double minutos = (horas-resultado_horas)*60;
      int resultado_minutos = (int) Math.floor(minutos);
		salida[1]= StringUtils.lpad(""+resultado_minutos,'0',2);

      double resultado_segundos = (minutos-resultado_minutos)*60;
		salida[2]= ""+StringUtils.formateaNumero(resultado_segundos,"00.###########################",0);

      return(salida);

    } 

	
	/**
	 * Calcula la distancia en grados entre dos pares de coordenadas, tambien 
	 * en grados.
	 * @param ra1
	 * 		Ascensi�n recta de la primara coordenada, en grados.
	 * @param de1
	 * 		Declinaci�n de la primara coordenada, en grados.
	 * @param ra2
	 * 		Ascensi�n recta de la segunda coordenada, en grados.
	 * @param de2
	 * 		Declinaci�n de la segunda coordenada, en grados.
	 * @return
	 */
	public static double distanciaRaDec(double ra1, double de1, double ra2, double de2){
		double coseno=Math.cos(Math.toRadians(de1))*Math.cos(Math.toRadians(de2))*Math.cos(Math.toRadians(ra1 - ra2)) + Math.sin(Math.toRadians(de1))*Math.sin(Math.toRadians(de2));
	    if(coseno >= 1) return 0;
	    return Math.toDegrees(Math.acos(coseno));
	}
	
	/**
	 * Separa en un array de strings la lista de coordenadas que viene de un 
	 * area de texto, por ejemplo.
	 * @param coordinatesList 
	 * 		String que representa la lista de coordenadas.
	 * @return
	 * 		Array de Strings conteniendo cada uno cada una de las coordenadas
	 * 		de la lista pasada.
	 */
	public static String[] addCoordinates(String coordinatesList) {

		String[] RaDecList = null;
		
		Vector aux = new Vector();

		if (coordinatesList != null && coordinatesList != "") {
			coordinatesList.trim().replaceAll("\\s", " ")
					.replaceAll("\\*", "%");
			RaDecList = coordinatesList.split("\n");

			for (int i = 0; i < RaDecList.length; i++) {
				if(RaDecList[i].trim().length()>0){
					aux.add(RaDecList[i]);
				}
			}
			RaDecList = (String[])aux.toArray(new String[0]);
		} else {
			RaDecList = new String[0];
		}

		
		
		return RaDecList;
	}
	
	
	/**
	* <P>Paso de numero en horas, minutos y segundos a su representacion
    *	 en grados como un double.  
	* <UL>LLAMADA
	*     <LI>double valor = Coordenadas.hms2double(int gg, int mm, double ss);
	* </UL>
	*  
	* @author Ra�l Guti�rrez S�nchez (raul@cab.inta-csic.es)
	* @version 0.0, 2003-06-12
	*/
   
	public static double hms2double(int hh, int mm, double ss) {
		double salida = ((double)Math.abs(hh) + ((double) mm)/60 + ((double) ss)/3600)*180/12;
        if(hh<0){
           salida = -1*salida;
        }
       return(salida);
    } 


	/**
	* <P>Paso de double en grados, minutos y segundos a su representacion
    *	 en grados como un double .  
	* <UL>LLAMADA
	*     <LI>double valor = Coordenadas.gms2double(double gg, double mm, double ss);
	* </UL>
	* 
	*/
	public static double gms2double(int gg, int mm, double ss) {

		double salida = (double)Math.abs(gg) + ((double) mm)/60 + ((double) ss)/3600;
         if(gg<0){
            salida = -1*salida;
         }
        return(salida);

  	}
	
	public static double gms2double(int signo, int gg, int mm, double ss) {

		double salida = (double)Math.abs(gg) + ((double) mm)/60 + ((double) ss)/3600;
         if(signo<0){
            salida = -1*salida;
         }
        return(salida);

  	}

	/**
	 * Comprueba el formato de coordenadas que se reciben como parametro 
	 * en un String.
	 * @param coo 
	 * 		String que representa las coordenadas de las que se quiere 
	 * 		comprobar si tienen el formato correcto. 
	 * @return
	 * 		true Si el formato de coordenadas es aceptado.
	 * 		false En caso contrario.
	 */
	public static boolean checkCoordinatesFormat(String coo) {
		
		// Formato adecuado de coordenadas
		if (existePatronEnLinea(patronCoo1, coo)){
			// 2 componentes
			// deg deg
			String ra = coo.replaceAll(patronCoo1, "$1");
			String de = coo.replaceAll(patronCoo1, "$2");

			//System.out.println("ra  = "+ra);
			//System.out.println("de  = "+de);
		
			try{
				return (Double.parseDouble(ra)>= 0 
				 && Double.parseDouble(ra)< 360
				 && Double.parseDouble(de)>= -90 
				 && Double.parseDouble(de)<= 90);
			}catch(NumberFormatException e){
				return false;
			}
		}
		else if (existePatronEnLinea(patronCoo2, coo)) {
				
			// 6 componentes
			// hh mm ss deg mm ss
			String ra_hh  = coo.replaceAll(patronCoo2, "$1").replace("+", "");
			String ra_mm  = coo.replaceAll(patronCoo2, "$2");
			String ra_ss  = coo.replaceAll(patronCoo2, "$3");
			String de_deg = coo.replaceAll(patronCoo2, "$4").replace("+", "");
			String de_mm  = coo.replaceAll(patronCoo2, "$5");
			String de_ss  = coo.replaceAll(patronCoo2, "$6");
			
			//System.out.println("ra_hh  = "+ra_hh);
			//System.out.println("ra_mm  = "+ra_mm);
			//System.out.println("ra_ss  = "+ra_ss);
			//System.out.println("de_deg = "+de_deg);
			//System.out.println("de_mm  = "+de_mm);
			//System.out.println("de_ss  = "+de_ss);
			
			try{
				return (Double.parseDouble(ra_hh)>=0 
					&& Double.parseDouble(ra_hh)< 24
					&& Double.parseDouble(ra_mm)>= 0 
					&& Double.parseDouble(ra_mm)< 60
					&& Double.parseDouble(ra_ss)>= 0 
					&& Double.parseDouble(ra_ss)< 60
					&& Double.parseDouble(de_deg)>= -90 
					&& Double.parseDouble(de_deg)<= 90
					&& Double.parseDouble(de_mm)>= 0 
					&& Double.parseDouble(de_mm)< 60
					&& Double.parseDouble(de_ss)>= 0 
					&& Double.parseDouble(de_ss)< 60);
			}catch(NumberFormatException e){
				return false;
			}
		}else{
			return false;
		}
		
	}

	/**
	 * @param cooList
	 * @return
	 */
	public static Double[][] addCoo2RaDecArr(String[] cooList) {

		Vector aux = addCoo2RaDecVector(cooList);
		
		Double[][] salida = new Double[aux.size()][2];
		
		for(int i=0; i<aux.size(); i++){
			salida[i][0] = new Double(((String[])aux.elementAt(i))[0]);
			salida[i][1] = new Double(((String[])aux.elementAt(i))[1]);
 		}
		
		return salida;
	}	
	
	
	/**
	 * @param cooList
	 * @return
	 */
	public static Vector addCoo2RaDecVector(String[] cooList) {

		// Vector al que se le van a ir añadiendo las coordenadas que estén en
		// el formato adecuado.
		Vector aux = new Vector();
		
		// Formato adecuado de coordenadas
		for (int i=0; i<cooList.length; i++) { 
			if (checkCoordinatesFormat(cooList[i]) && existePatronEnLinea(patronCoo1, cooList[i])){
				
				// 2 componentes
				// deg deg
				String[] comps = new String[]{cooList[i].replaceAll(patronCoo1, "$1"),
										      cooList[i].replaceAll(patronCoo1, "$2")};
				// Ya está en grados. Añadimos al vector
				aux.add(comps);
			}else if (checkCoordinatesFormat(cooList[i]) && existePatronEnLinea(patronCoo2, cooList[i])) {
				
				// 6 componentes
				// hh mm ss deg mm ss
				String ra_hh  = cooList[i].replaceAll(patronCoo2, "$1").replace("+", "");
				String ra_mm  = cooList[i].replaceAll(patronCoo2, "$2");
				String ra_ss  = cooList[i].replaceAll(patronCoo2, "$3");
				String de_deg = cooList[i].replaceAll(patronCoo2, "$4").replace("+", "");
				String de_mm  = cooList[i].replaceAll(patronCoo2, "$5");
				String de_ss  = cooList[i].replaceAll(patronCoo2, "$6");

				// Convertir a grados RA
				double ra;
				ra = Coordenadas.hms2double(Integer.parseInt(ra_hh), 
											Integer.parseInt(ra_mm), 
											Double.parseDouble(ra_ss));
				
				// Convertir a grados Dec
				double dec;
				dec = gms2double(Integer.parseInt(de_deg), 
								 Integer.parseInt(de_mm), 
								 Double.parseDouble(de_ss));
				
				// Añadimos al vector de salida
				String[] raDecDouble = new String[2];
				raDecDouble[0] = String.valueOf(ra);
				raDecDouble[1] = String.valueOf(dec);
				aux.add(raDecDouble);
			}
		}
		return aux;
		
	}	
	
	
	/**
	 * Analiza el string proporcionado y, en caso de que se identifique
	 * como coordenadas, las devuelve en grados.
	 * 
	 * @param coords
	 * @return
	 */
	public static Double[] coordsInDeg(String coords) {

		try{
			if (checkCoordinatesFormat(coords) && existePatronEnLinea(patronCoo1, coords)){
				
				// 2 componentes
				// deg deg
				String[] comps = new String[]{coords.replaceAll(patronCoo1, "$1"),
										      coords.replaceAll(patronCoo1, "$2")};
				// Ya está en grados.
				return new Double[]{new Double(comps[0]),new Double(comps[1])};
			}else if (checkCoordinatesFormat(coords) && existePatronEnLinea(patronCoo2, coords)) {
				
				// 6 componentes
				// hh mm ss deg mm ss
				String ra_hh  = coords.replaceAll(patronCoo2, "$1").replace("+", "");
				String ra_mm  = coords.replaceAll(patronCoo2, "$2");
				String ra_ss  = coords.replaceAll(patronCoo2, "$3");
				String de_deg = coords.replaceAll(patronCoo2, "$4").replace("+", "");
				String de_mm  = coords.replaceAll(patronCoo2, "$5");
				String de_ss  = coords.replaceAll(patronCoo2, "$6");
	
				int de_sign = 1;
				if(de_deg.contains("-")){
					de_sign=-1;
				}
				
				// Convertir a grados RA
				double ra;
				ra = Coordenadas.hms2double(Integer.parseInt(ra_hh), 
											Integer.parseInt(ra_mm), 
											Double.parseDouble(ra_ss));
				
				// Convertir a grados Dec
				double dec;
				dec = gms2double(de_sign, Integer.parseInt(de_deg), 
								 Integer.parseInt(de_mm), 
								 Double.parseDouble(de_ss));
				
				// Añadimos al vector de salida
				String[] raDecDouble = new String[2];
				raDecDouble[0] = String.valueOf(ra);
				raDecDouble[1] = String.valueOf(dec);
				return new Double[]{new Double(raDecDouble[0]),new Double(raDecDouble[1])};
			}else{
				return null;
			}
		}catch(NumberFormatException e){
			return null;
		}
		
	}	
	
	/**
	 * Devuelve el identificador principal del objeto que se encuentra mas cerca 
	 * de las coordenadas proporcionadas dentro de un radio, 
	 * mediante una cosnulta a Simbad. 
	 * @param ra
	 * @param de
	 * @param radius
	 * @param radUnit
	 * @return
	 */
	public static String coo2name (Double ra, Double de, int radius, String radUnit) {
		String name = null;
		String url = "http://simbad.u-strasbg.fr/simbad/sim-script?submit=submit+script&script=output" +
				"+script%3Doff%0D%0Aoutput+console%3Doff%0D%0Aoutput+error%3Doff%0D%0Aformat" +
				"+object+f1+%22%25IDLIST%281%29%22%0D%0Aset+" +
				"radius+" + radius + radUnit + "%0D%0Aquery+coo+" + ra + "+" + de;
		
		URL miUrl=null;
		try {
			miUrl = new java.net.URL(url);
			
			//System.out.println("URL: " + miUrl.toString());
	
			String urlContent;
	
			int linea = 0;
			
			BufferedReader in = new BufferedReader(new InputStreamReader(miUrl
					.openStream()));
	
			while ((urlContent = in.readLine()) != null) {
				linea++;
				if (urlContent != "") {
					name = urlContent;
					//System.out.println("Primera linea del url: "+name);
					break;
				}
				//System.out.println("linea: "+name);
			}
		
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		
		return name;
	}

	
	
	/**
	 * @param patron
	 *            Patron que se quiere encontrar
	 * @param linea
	 *            String que representa la linea donde buscar el patron
	 * @return true si existe el patron en la linea false si no existe
	 */
	public static boolean existePatronEnLinea(String patron, String linea) {
		return linea.matches(patron);
	}
} 	
  
