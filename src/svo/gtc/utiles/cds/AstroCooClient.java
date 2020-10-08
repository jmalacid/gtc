/*
 * @(#)AstroCooClient.java    2005-09-16
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
 * @version 0.1, 16/09/2005
 */

package svo.gtc.utiles.cds;

/**
 * <H2>AstroCooClient</H2>
 * 
 * <P>
 * Gestiona las consultas al servicio web de transformaci�n de coordenadas del
 * CDS.
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import AstroCoo_pkg.*;
import svo.gtc.utiles.cds.MalformedSesameResponseException;
import svo.gtc.utiles.cds.NotSingleObjectDataException;
import svo.gtc.utiles.cds.SesameResolverException;

public class AstroCooClient {

	/**
	 * Definicion de una nueva clase para almacenar los resultados del
	 * c�lculo
	 * 
	 * @author Ra�l Guti�rrez S�nchez
	 */
	public static class ResultadoAstroCooClient{
		protected Double lon =null;
		protected Double lat =null;
		 
		ResultadoAstroCooClient(Double lon, Double lat){
		 	this.lon = lon;
		 	this.lat = lat;
		 }
		 
		 public double getLon(){
		 	if(lon!=null){
		 		return this.lon.doubleValue();
		 	}else{
		 		return 0;
		 	}
		 }
		 
		 public double getLat(){
		 	if(lat!=null){
		 		return this.lat.doubleValue();
		 	}else{
		 		return 0;
		 	}
		 }
	}

	/**
	 * Definicion de un nuevo tipo de excepcion que se lanza cuando el servicio
	 * devuelve los datos en un formato inesperado.
	 * 
	 * @author Ra�l Guti�rrez S�nchez
	 *  
	 */
	public static class UnexpectedFormatException extends Exception {
		UnexpectedFormatException() {
		}

		UnexpectedFormatException(String msg) {
			super(msg);
		}
	}
	
	/**
	 * Realiza una llamada al servicio AstroCoo CDS para convertir las coordenadas
	 * dadas a otro sistema.
	 *  
	 * @param frame1 Sistema de coordenadas de entrada (1=FK4, 2=GAL, 3=SGAL, 4=ECL, 5=FK5, 6=ICRS).
	 * @param frame2 Sistema de coordenadas de salida (1=FK4, 2=GAL, 3=SGAL, 4=ECL, 5=FK5, 6=ICRS).
	 * @param lon longitud
	 * @param lat latitud
	 * @param equinox1 Equinocio de entrada (Julian Years or Besselian, unused for GAL, SGAL, ICRS)
	 * @param equinox2 Equinocio de salida (Julian Years or Besselian, unused for GAL, SGAL, ICRS)
	 * @return
	 * @throws UnexpectedFormatException
	 * @throws ServiceException
	 */

	public static ResultadoAstroCooClient transformCoords(int frame1, int frame2, 
																		 double lon, double lat, 
																		 double equinox1, double equinox2) throws UnexpectedFormatException, ServiceException{
		
		String salidaSesame = callService(frame1, frame2, lon, lat, 8, equinox1, equinox2);

		Double lonSalida = null;
		Double latSalida = null;
		
		
		String[] partes = salidaSesame.split("\\s");
		
		if(partes.length==3){
			lonSalida = new Double(partes[0]);
			latSalida = new Double(partes[1]);
		}else if(partes.length==7){
			double a,b,c;
			try{
				a=Double.parseDouble(partes[0]);
				b=Double.parseDouble(partes[1]);
				c=Double.parseDouble(partes[2]);
			}catch(NumberFormatException e){
				throw new UnexpectedFormatException("Formato de salida del servicio inesperado: "+salidaSesame);
			}
			lonSalida = new Double( (a+b/60+c/3600)*360/24 );

			try{
				a=Double.parseDouble(partes[3]);
				b=Double.parseDouble(partes[4]);
				c=Double.parseDouble(partes[5]);
			}catch(NumberFormatException e){
				throw new UnexpectedFormatException("Formato de salida del servicio inesperado "+salidaSesame);
			}
			if(a<0){
				latSalida = new Double( a-b/60-c/3600 );
			}else{
				latSalida = new Double( a+b/60+c/3600 );
			}
		}else{
			throw new UnexpectedFormatException("Formato de salida del servicio inesperado "+salidaSesame);
		}
		
		
		
		return new ResultadoAstroCooClient(lonSalida, latSalida);
	}
	

	/**
	 * Consulta el servicio web AstroCoo (de CDS) y realiza una transformacion
	 * de coordenadas
	 * 
	 * @param frame1 Sistema de coordenadas de entrada (1=FK4, 2=GAL, 3=SGAL, 4=ECL, 5=FK5, 6=ICRS).
	 * @param frame2 Sistema de coordenadas de salida (1=FK4, 2=GAL, 3=SGAL, 4=ECL, 5=FK5, 6=ICRS).
	 * @param lon longitud
	 * @param lat latitud
	 * @param precision (0=NONE, 1=DEG, 3=ARCMIN, 5=ARCSEC, 8=MAS)
	 * @param equinox1 Equinocio de entrada (Julian Years or Besselian, unused for GAL, SGAL, ICRS)
	 * @param equinox2 Equinocio de salida (Julian Years or Besselian, unused for GAL, SGAL, ICRS)
	 * @return string de salida del servicio.
	 * 
	 * @throws ServiceException
	 * @throws RemoteException
	 * @throws NotSingleObjectDataException
	 * @throws MalformedSesameResponseException
	 * @throws SesameResolverException
	 */

	public static String callService( int frame1, int frame2, 
												 double lon, double lat, 
												 int precision,
												 double equinox1, double equinox2) throws ServiceException {

		URL[] serviceAddresses = new URL[4];
		try {
			serviceAddresses[0] = new URL("http://cdsws.u-strasbg.fr/axis/services/AstroCoo");
			serviceAddresses[1] = new URL("http://vizier.cfa.harvard.edu:8080/axis/services/AstroCoo");
			serviceAddresses[2] = new URL("http://vizier.nao.ac.jp:8080/axis/services/AstroCoo");
			serviceAddresses[3] = new URL("http://vizier.hia.nrc.ca:8080/axis/services/AstroCoo");
		}catch (MalformedURLException e1){
			e1.printStackTrace();
		}
		
		boolean servicioOK = false;
		int mirror = 0;
		String ret = "";
		
		while(servicioOK!=true && mirror < serviceAddresses.length){
			// locator creation
			AstroCooService locator = new AstroCooServiceLocator();
			try{
				// AstroCoo object
				AstroCoo mya = locator.getAstroCoo(serviceAddresses[mirror]);
		
				ret = mya.convert(frame1, frame2, lon, lat, precision, equinox1, equinox2);
		
				servicioOK=true;
			}catch(Exception e){
				servicioOK=false;
				mirror++;
			}
		}
		//System.out.println("Hemos salido del While.....");
		if(servicioOK != true){
			throw new ServiceException(
					"ERROR: None of the Sesame Services are working properly.");
		}
		
		return ret;

	}

}

