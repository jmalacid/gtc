/*
 * @(#)Sesame.java    2004-03-29
 *
 *
 * Ra�l Guti�rrez S�nchez. (raul@cab.inta-csic.es)	
 * LAEFF: 	Laboratorio de Astrof�sica Espacial y F�sica Fundamental.
 *        	INTA
 *			Villafranca del Castillo Satellite Tracking Station
 *			Villafranca del Castillo, E-28691 Villanueva de la Ca?ada
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
 * @version 0.1, 02/02/2005
 */

package svo.gtc.utiles.cds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;


import java.net.URL;

import javax.xml.rpc.ServiceException;

import svo.gtc.utiles.cds.MalformedSesameResponseException;
import svo.gtc.utiles.cds.NotSingleObjectDataException;
import svo.gtc.utiles.cds.ResultadoSesameClient;
import svo.gtc.utiles.cds.SesameResolverException;

public class SesameClient_new {
	
	/**
	 * Realiza una b�squda en el servicio Sesame de CDS dado el nombre de objeto 
	 * @param name nombre del objeto
	 * @return <p>objeto de tipo SesameClient.ResultadoSesameClient con los datos que
	 *         devuelve sesame</p>
	 * 
	 * @throws ServiceException
	 * @throws NotSingleObjectDataException
	 * @throws MalformedSesameResponseException
	 * @throws SesameResolverException
	 * @throws IOException 
	 */
	public static ResultadoSesameClient sesameSearch(String name) throws ServiceException, NotSingleObjectDataException, MalformedSesameResponseException, SesameResolverException, IOException{

		Double ra   = null;
		Double de   = null;
		
		final String USER_AGENT = "Mozilla/5.0";

	    URL obj;
	    
	    obj = new URL("http://cds.u-strasbg.fr/viz-bin/nph-sesame?" + name.replaceAll(" ", "%20"));
	    final HttpURLConnection con = (HttpURLConnection)
	   
	    obj.openConnection();
	    con.setRequestMethod("POST");
	    con.setRequestProperty("User-Agent", USER_AGENT);
	    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	    con.setDoOutput(true);

	   // final int responseCode = con.getResponseCode();
	    final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

	    String inputLine;

	    while ((inputLine = in.readLine()) != null) {
	    	//System.out.println(inputLine);
	    	  
	        if (inputLine.startsWith("%J ")) {
	          final String[] fields = inputLine.split(" ", -1);
	          ra = Double.valueOf(fields[1]);
	          de = Double.valueOf(fields[2]);
	        }
	      }

		return new ResultadoSesameClient(null,null,ra,de,null);
	}
	

}

