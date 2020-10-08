/*
 * @(#)FetchMoc.java    May 12, 2017
 *
 *
 * José Manuel Alacid. (jmalacid@cab.inta-csic.es)	
 * 
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

package svo.gtc.web.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * Servlet implementation class Entrada
 */
public class FetchMoc extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FetchMoc() { 
        super();       
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		
		ServletOutputStream outs = response.getOutputStream();
		
		
		
		String ins_id = request.getParameter("id");
		String name = "gtcMOC.fits";
		
		try{
			File fich= null;
			if(ins_id.equals("0")){
				fich = new File("/export/data-gtc/moc/gtcMOC.fits");
			}else if(ins_id.equals("1")){
				fich = new File("/export/data-gtc/moc/userMOC.fits");
				name = "userMOC.fits";
			}else if(ins_id.equals("2")){
				fich = new File("/export/data-gtc/moc/osirisMOC.fits");
				name = "osirisMOC.fits";
			}
			
			response.setContentType ("application/fits");
			response.setHeader("Content-Disposition", "attachment;filename="+name); 
			
			//File fich = new File("/pcdisk/oort/jmalacid/data/gtc/scripts/moc/gtcMOC.fits");
			//File fich = new File("/export/data-gtc/moc/gtcMOC.fits");
			InputStream ins = new FileInputStream(fich);
			
			byte[] buffer = new byte[1024];
			int numBytes = 0;
			while((numBytes=ins.read(buffer))>0){
				outs.write(buffer, 0, numBytes);
			}
			outs.flush();
			ins.close();
			outs.close();
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	
	
	//bi.cose();
	try {
		outs.flush();
	} catch (IOException e) {
		e.printStackTrace();
	}
					

	}	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
	}

}