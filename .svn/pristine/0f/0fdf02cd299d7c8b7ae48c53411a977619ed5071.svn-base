/*
 * @(#)Email.java    25/09/2012
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

package svo.gtc.tools;

import java.io.OutputStream;
import java.util.ArrayList;

public class Email {

	public static void sendMail(String subject, String body, String[] destinatarios) throws Exception{
		ArrayList<String>  argumentos = new ArrayList<String>();

		argumentos.add("mail");
		argumentos.add("-s");
		argumentos.add(subject);

		String dest = "";
		String coma = "";
		for(int i=0; i<destinatarios.length; i++){
			dest+=coma+destinatarios[i]; 
			coma=",";
		}
		
		argumentos.add(dest);

		Runtime rt=Runtime.getRuntime(); 
		Process p=null;

		String[] args = argumentos.toArray(new String[0]);
		p = rt.exec(args);
		OutputStream outs = p.getOutputStream();
		outs.write(body.getBytes());
		outs.close();
		p.waitFor(); 
		
		if(p.exitValue()!=0){
			throw new Exception("ERROR al enviar correo electrónico.");
		}
	}

}
