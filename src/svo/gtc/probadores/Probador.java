/*
 * @(#)Probador.java    16/03/2011
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

package svo.gtc.probadores;

import java.io.File;
import java.util.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.hibernate.Session;

import svo.gtc.db.HibernateUtil;
import svo.gtc.db.target.TargetDb;
import svo.gtc.utiles.cds.ResultadoSesameClient;
import svo.gtc.utiles.cds.SesameClient;
import utiles.Coordenadas;

public class Probador {

    public static void main(String[] args) throws ParseException {
        
		//File path = new File("/a/b/c/d/");
		//System.out.println(path.getAbsolutePath());
		//System.out.println(path.getParentFile().getParentFile().getAbsolutePath());

        System.out.println("prueba");
        
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date fech_i = (Date) formatter.parse("2014-10-1");
        Timestamp time_ini = new Timestamp(fech_i.getTime());
        
        System.out.println(time_ini);
        
        
       // Probador prueba = new Probador();
        //prueba.verdoc("tomate"); 
        
        
        //Probador mgr = new Probador();

        //mgr.createAndStoreEvent();

        //HibernateUtil.getSessionFactory().close();
    }

    private void createAndStoreEvent() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        TargetDb target = new TargetDb();
        
        target.setObjName("hola");
        target.setObjRa(1.0);
        target.setObjDe(2.0);
        session.save(target);

        session.getTransaction().commit();
    }
    
    private void verdoc(String lineas){
		if(lineas.trim().matches("^[0-9]{1,10}$")){
			Integer aux = new Integer(lineas.trim());
			System.out.println("paso1: "+aux);
		}else if(Coordenadas.checkCoordinatesFormat(lineas.trim())){
			Double[] coords = Coordenadas.coordsInDeg(lineas.trim());
			if(coords!=null){
				for(int i =0; i<coords.length;i++){
				System.out.println("coorsee("+i+"): "+coords[i]);
				}
			}
		}else{
			try {
				ResultadoSesameClient resultado = SesameClient.sesameSearch(lineas.trim());
				
				if(resultado.getRa()!=null && resultado.getDe()!=null){
					System.out.println("se considera null");
				
				Double[] coords = new Double[]{resultado.getRa(),resultado.getDe()};
					for(int i =0; i<coords.length;i++){
						System.out.println("coorsSesame("+i+"): "+coords[i]);
					}
				}
				//auxCoordsList.add(coords);
			} catch (Exception e) {
				
				System.out.println("error");
				//this.addError("Unable to resolve object: \""+lineas.trim()+"\"");
			}
			
		}
	}

}