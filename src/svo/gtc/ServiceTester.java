/*
 * @(#)ServiceTester.java    Oct 14, 2010
 *
 *
 * Ra�l Guti�rrez S�nchez. (raul@laeff.inta.es)	
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
 */

package svo.gtc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import svo.gtc.db.DriverBD;

public class ServiceTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.print(test());
	}

	/**
	 * Realiza todos los tests definidos para el servicio.
	 * 
	 * @return <p>"0000" en caso de funcionamiento correcto. Los c�digos de error correspondientes en caso contrario.</p>
	 */
	public static String test(){
		String codigo = "";
		
		codigo += testDB();
		
		if(codigo.length()==0){
			codigo ="0000";
		}
		
		return codigo;
	}

	
	/**
	 * Comprueba que la conexi�n a la base de datos se realiza correctamente y realiza una consulta.
	 * 
	 * @return <p>String vacio en caso de que el servicio funcione. El c�digo "1000 Error de conexi�n con la base de datos." en caso de problemas.</p>
	 * 
	 */
	private static String testDB(){
		/////////////////////////////////////////////////////
		////  Conexi�n con la Base de Datos                                    
		/////////////////////////////////////////////////////

		Connection conexOMC = null;

		DriverBD con = new  DriverBD();

		try {
			conexOMC = con.bdConexion();
			ResultSet res = conexOMC.createStatement().executeQuery("SELECT COUNT(*) FROM news");
			if(!res.next()){
				return "1000 Error de conexion/acceso a la base de datos.\n";
			}

			conexOMC.close();
		} catch (SQLException errconexion)  {
			errconexion.printStackTrace();
			return "1000 Error de conexion/acceso a la base de datos.\n";
		}

		return "";

	}
	

}
