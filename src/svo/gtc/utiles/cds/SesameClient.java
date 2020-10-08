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

/**
 * <H2>Sesame</H2>
 * 
 * <P>
 * Gestiona las consultas al resolvedor de nombres del CDS
 * <UL>
 * Consultas.
 * <LI>(nombresEquivalentes()): Obtenci?n de la relaci?n de nombres
 * equivalentes al especificado
 * </UL>
 * 
 * @author 
 * @version 0.0, 2004-03-29
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Vector;

import javax.xml.rpc.ServiceException;

import Sesame_pkg.*;
import Sesame_pkg.SesameServiceLocator;
import svo.gtc.utiles.cds.MalformedSesameResponseException;
import svo.gtc.utiles.cds.NotSingleObjectDataException;
import svo.gtc.utiles.cds.ResultadoSesameClient;
import svo.gtc.utiles.cds.SesameResolverException;

public class SesameClient {
	
	/**
	 * Realiza una b�squda en el servicio Sesame de CDS dado el nombre de objeto 
	 * @param name nombre del objeto
	 * @return <p>objeto de tipo SesameClient.ResultadoSesameClient con los datos que
	 *         devuelve sesame</p>
	 * 
	 * @throws RemoteException
	 * @throws ServiceException
	 * @throws NotSingleObjectDataException
	 * @throws MalformedSesameResponseException
	 * @throws SesameResolverException
	 */
	public static ResultadoSesameClient sesameSearch(String name) throws RemoteException, ServiceException, NotSingleObjectDataException, MalformedSesameResponseException, SesameResolverException{

		String onam = null;
		String oty  = null;
		Double ra   = null;
		Double de   = null;
		String[] equivalentNames = null;
		Double[] v = null;
		
		String salidaSesame = null;
		String simbad = null;
		String[] partes = null;
		String aux = null;

		salidaSesame = getNameResolved(name);

		// Nombre de objeto
		partes = salidaSesame.split("<name>");
		if (partes.length > 1) {
			aux = partes[1];
			aux = aux.substring(0, aux.indexOf("</name")).trim();
			onam=aux;
		}

		
		// Nos quedamos solo con el resultado de SIMBAD (lo que est?
		// contenido
		// entre <Resolver name="Simbad"> y </Resolver>
		simbad = salidaSesame;
//CAMBIOS
//		simbad = simbad.substring(simbad.indexOf("Simbad"));
//		simbad = simbad.substring(0, simbad.indexOf("</Resolver>"));
		if (simbad.indexOf("Simbad") > -1) {
		      simbad = simbad.substring(simbad.indexOf("Simbad"));
		      // simbad = simbad.substring(0, simbad.indexOf("</Resolver>"));
		    }
//		
		
		// Tipo de objeto
		partes = simbad.split("<otype>");
		if (partes.length > 1) {
			aux = partes[1];
			aux = aux.substring(0, aux.indexOf("</otype")).trim();
			oty=aux;
		}

		// Coordenadas objeto
		partes = simbad.split("<jradeg>");
		if (partes.length > 1) {
			aux = partes[1];
			aux = aux.substring(0, aux.indexOf("</jradeg")).trim();
			ra = new Double(aux);
		}

		partes = simbad.split("<jdedeg>");
		if (partes.length > 1) {
			aux = partes[1];
			aux = aux.substring(0, aux.indexOf("</jdedeg")).trim();
			de  = new Double(aux);
		}
		
		// Nombres equivalentes objeto
		Vector nombresEquivalentesAux = new Vector();
		partes = simbad.split("<alias>");

		for (int i = 1; i < partes.length; i++) {
			aux = partes[i];
			aux = aux.substring(0, aux.indexOf("</alias")).trim();

			//Eliminamos los nombres que empiecen por "com "
			if (!aux.startsWith("com ")) {
				nombresEquivalentesAux.addElement(aux);
			}
		}

		
		
		equivalentNames = (String[]) nombresEquivalentesAux.toArray(new String[0]);

		return new ResultadoSesameClient(onam,oty,ra,de,equivalentNames);
	}
	
	/**
	 * <P>
	 * Obtenci�n del tipo de objeto y coordenadas de un objeto dado:
	 * 
	 * <UL>
	 * LLAMADA
	 * <LI>Vector datosObjeto = accesos.simbad.Sesame.datosObjeto("hd25204");
	 * <LI>String onam = (String) datosObjeto.elementAt(0);
	 * <LI>String oty = (String) datosObjeto.elementAt(1);
	 * <LI>Double ra = (Double) datosObjeto.elementAt(2);
	 * <LI>Double de = (Double) datosObjeto.elementAt(3);
	 * </UL>
	 * 
	 * @param name
	 *            Nombre del objeto a consultar.
	 * @return vector con los datos del objeto.
	 * @throws SesameResolverException
	 * @throws MalformedSesameResponseException
	 * @throws NotSingleObjectDataException
	 * @throws ServiceException
	 * @throws RemoteException
	 *  
	 */

	public static Vector datosObjeto(String name) throws RemoteException, ServiceException, NotSingleObjectDataException, MalformedSesameResponseException, SesameResolverException {

		ResultadoSesameClient resultado = sesameSearch(name);
		Vector salida = new Vector();

		// Nombre de objeto
		salida.addElement(resultado.getOnam());

		// Tipo de objeto
		salida.addElement(resultado.getOty());

		// Coordenadas objeto
		salida.addElement(resultado.getRa());
		salida.addElement(resultado.getDe());
		
		return salida;

	}

	/**
	 * <P>
	 * Obtenci?n de la relaci?n de nombres equivalentes de un objeto dado.
	 * 
	 * <UL>
	 * LLAMADA
	 * <LI>Vector nombresEquivalentes =
	 * accesos.simbad.Sesame.nombresEquivalentes("hd25204");
	 * <LI>for(int i=0; i <nombresEquivalentes.size(); i++) {
	 * <LI>String nombre = (String) nombresEquivalentes.elementAt(i);
	 * </UL>
	 * 
	 * @param name
	 *            Nombre del objeto a consultar.
	 * @return vector con los nombres equivalentes del objeto.
	 * @throws RemoteException
	 * @throws ServiceException
	 * @throws NotSingleObjectDataException
	 * @throws MalformedSesameResponseException
	 * @throws SesameResolverException
	 *  
	 */
	public static String[] nombresEquivalentes(String name)
			throws RemoteException, ServiceException,
			NotSingleObjectDataException, MalformedSesameResponseException,
			SesameResolverException {

		ResultadoSesameClient resultado = sesameSearch(name);

		return resultado.getEquivalentNames();

	}

	/**
	 * Consulta el servicio web Sesame (de CDS) y pide la informaci�n en formato
	 * XML.
	 * 
	 * @param name
	 *            Nombre del objeto que quiere consultarse.
	 * @return string con el resultado de la consulta en formato XML.
	 * 
	 * @throws ServiceException
	 * @throws RemoteException
	 * @throws NotSingleObjectDataException
	 * @throws MalformedSesameResponseException
	 * @throws SesameResolverException
	 */

	public static String getNameResolved(String name) throws ServiceException,
			RemoteException, NotSingleObjectDataException,
			MalformedSesameResponseException, SesameResolverException {

		URL[] sesameAddresses = new URL[4];
		try {
			sesameAddresses[0] = new URL("http://cdsws.u-strasbg.fr/axis/services/Sesame");
			sesameAddresses[1] = new URL("http://vizier.cfa.harvard.edu:8080/axis/services/Sesame");
			sesameAddresses[2] = new URL("http://vizier.nao.ac.jp:8080/axis/services/Sesame");
			sesameAddresses[3] = new URL("http://vizier.hia.nrc.ca:8080/axis/services/Sesame");
			//sesameAddresses[4] = new URL("http://cdsweb.u-strasbg.fr/cgi-bin/nph-sesame/SNVO");
		}catch (MalformedURLException e1){
			e1.printStackTrace();
		}
		
		boolean servicioSesameOK = false;
		int mirror = 0;
		String ret = "";
		
		while(!servicioSesameOK && mirror < sesameAddresses.length){
			//System.out.println("Mirror "+mirror);
			// locator creation
			SesameService locator = new SesameServiceLocator();
			try{
				//System.out.println("MIRROR  "+sesameAddresses[mirror].toString());
				// AstroCoo object
				Sesame mya = locator.getSesame(sesameAddresses[mirror]);
		
				ret = mya.sesameXML(name);
				//ret = mya.sesame(name, "x", true, "S");
				//ret = mya.sesame(name, "p", true, "NSVA");
				//System.out.println(ret);
				//ret = mya.sesame("m31", "x");
				servicioSesameOK=true;
			}catch(Exception e){
				e.printStackTrace();
				servicioSesameOK=false;
				mirror++;
			} 
			//System.out.println(servicioSesameOK);
		}
		//System.out.println("Hemos salido del While.....");
		if(servicioSesameOK != true){
			throw new ServiceException(
					"ERROR: None of the Sesame Services are working properly.");
		}
		
		if (ret != null) {
			if (ret.indexOf("<Sesame") < 0
					|| ret.matches("^<Resolver name=\".*Simbad.*$")) {
				throw new MalformedSesameResponseException(
						"ERROR: Formato XML inesperado devuelto por SIMBAD:"
								+ "\n ---------------------------------------------------\n"
								+ ret
								+ "\n ---------------------------------------------------\n");

			}

			// Si devuelve informacion de mas de 1 objeto devolvemos error
			/*if (ret.indexOf("<oname>") != ret.lastIndexOf("<oname>")) {
				throw new NotSingleObjectDataException(
						"Error: Simbad devolvio datos para mas de un objeto.");
			}*/

			// Excepcion si SIMBAD ha podido resolver el objeto y no avisa de
			// ello
			String simbad = ret;
			simbad = simbad.substring(simbad
					.indexOf("Simbad"));
			simbad = simbad.substring(0, simbad.indexOf("</Resolver>"));

			if ( (simbad.indexOf("<INFO>") < 0 && simbad.indexOf("<nrefs>0</nrefs>")<0)
					&& simbad.indexOf("<alias") < 0 && simbad.indexOf("<name>")<0 && 
					simbad.indexOf("<jradeg>")<0) {

				throw new SesameResolverException(
						"ERROR: SIMBAD no resuelve el nombre y no avisa de ello:"
								+ "\n ---------------------------------------------------\n"
								+ ret
								+ "\n ---------------------------------------------------\n");
			}

			return ret;
		} else {
			return "No result found.";
		}

	}

}

