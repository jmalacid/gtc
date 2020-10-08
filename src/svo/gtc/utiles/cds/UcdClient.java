/*
 * @(#)UcdClient.java    2004-03-29
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

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.rpc.ServiceException;

import UCD_pkg.*;

public class UcdClient {
	private static String[] serviceAddresses = 	{"http://cdsws.u-strasbg.fr/axis/services/UCD",
												"http://vizier.cfa.harvard.edu:8080/axis/services/UCD",
												"http://vizier.nao.ac.jp:8080/axis/services/UCD",
												"http://vizier.hia.nrc.ca:8080/axis/services/UCD"};

	
	/**
	 * Devuelve una descripci�n del UCD.
	 * @param ucd
	 * @return Descripcion del ucd.
	 * @throws ServiceException
	 */
	public static String explain(String ucd) throws ServiceException {
		
		boolean servicioSesameOK = false;
		int mirror = 0;
		String ret = "";
		
		while(servicioSesameOK!=true && mirror < serviceAddresses.length){
			UCDService locator = new UCDServiceLocator();
			try{
				UCD service = locator.getUCD(new URL(serviceAddresses[mirror]));
				ret = service.explain(ucd);
				if(ret.toLowerCase().startsWith("**** non-standard")){
					String aux = service.translate(ucd);
					if(aux!=null && !aux.startsWith("*")){
						aux = service.explain(aux);
					}else{
						String aux2 = service.upgrade(ucd);
						if(!aux2.equalsIgnoreCase(ucd)){
							aux = service.explain(aux2);
						}
					}
					if(aux!=null && !aux.startsWith("*")){
						ret = aux;
					}
				}
				servicioSesameOK=true;
			}catch(Exception e){
				servicioSesameOK=false;
				System.out.println(serviceAddresses[mirror].toString()+" not working.");
				e.printStackTrace();
				mirror++;
			}
		}
		//System.out.println("Hemos salido del While.....");
		if(servicioSesameOK != true){
			throw new ServiceException(
					"ERROR: None of the Sesame Services are working properly.");
		}
		
		if (ret != null) {
			return ret;
		} else {
			return "";
		}

	}

	/**
	 * Traduce un UCD1 a UCD1+
	 * @param ucd UCD en formato UCD1.
	 * @return UCD1+ correspondiente.
	 * @throws ServiceException
	 */
	public static String translate(String ucd1) throws ServiceException {
		
		boolean servicioSesameOK = false;
		int mirror = 0;
		String ret = "";
		
		while(servicioSesameOK!=true && mirror < serviceAddresses.length){
			UCDService locator = new UCDServiceLocator();
			try{
				UCD service = locator.getUCD(new URL(serviceAddresses[mirror]));
				ret = service.translate(ucd1);
				servicioSesameOK=true;
			}catch(Exception e){
				servicioSesameOK=false;
				System.out.println(serviceAddresses[mirror].toString()+" not working.");
				e.printStackTrace();
				mirror++;
			}
		}
		//System.out.println("Hemos salido del While.....");
		if(servicioSesameOK != true){
			throw new ServiceException(
					"ERROR: None of the Sesame Services are working properly.");
		}
		
		if (ret != null) {
			return ret;
		} else {
			return "";
		}

	}

	

	/**
	 * The official UCD1+ words have sometimes undergone changes. 
	 * This function will upgrade deprecated words within a UCD1+ 
	 * (causing error-code 2 in validate) to their currently valid expression;
	 * 
	 * @param ucd UCD.
	 * @return UCD1+ correspondiente o la ucd de entrada si ha habido cualquier error.
	 * @throws ServiceException
	 */
	public static String upgrade(String ucd1) throws ServiceException {
		
		boolean servicioSesameOK = false;
		int mirror = 0;
		String ret = "";
		
		while(servicioSesameOK!=true && mirror < serviceAddresses.length){
			UCDService locator = new UCDServiceLocator();
			try{
				UCD service = locator.getUCD(new URL(serviceAddresses[mirror]));
				ret = service.upgrade(ucd1);
				servicioSesameOK=true;
			}catch(Exception e){
				servicioSesameOK=false;
				System.out.println(serviceAddresses[mirror].toString()+" not working.");
				e.printStackTrace();
				mirror++;
			}
		}
		//System.out.println("Hemos salido del While.....");
		if(servicioSesameOK != true){
			throw new ServiceException(
					"ERROR: None of the Sesame Services are working properly.");
		}
		
		if (ret != null) {
			return ret;
		} else {
			return ucd1;
		}
	
	}
	
	

}
