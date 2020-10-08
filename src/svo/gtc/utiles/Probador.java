package svo.gtc.utiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.util.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.fits.ImageHDU;
import nom.tam.fits.PaddingException;
import svo.gtc.db.DriverBD;
import svo.gtc.db.basepath.BasepathAccess;
import svo.gtc.db.priv.DBPrivate;
import svo.gtc.db.prodat.ProdDatosAccess;
import svo.gtc.db.prodat.WarningDb;
import svo.gtc.db.proderr.ErrorDb;
import svo.gtc.db.proderr.ProdErrorAccess;
import svo.gtc.ingestion.Config;
import svo.gtc.ingestion.ConfigException;
import svo.gtc.proddat.GtcFileException;
import svo.gtc.proddat.ObsBlock;
import svo.gtc.proddat.ProdDatos;
import svo.gtc.proddat.ProdDatosCanaricam;
import svo.gtc.proddat.ProdDatosOsiris;
import svo.gtc.proddat.Program;
import svo.gtc.proddat.QcFile;
import svo.gtc.utiles.cds.ResultadoSesameClient;
import svo.gtc.utiles.cds.SesameClient;
import svo.varios.utiles.seguridad.Encriptado;
import utiles.Coordenadas;

public class Probador {
	
	private static File fits = null;
	public static void main(String args[]) throws Exception{
        	
	
        	
   //     	URL pagina = new URL("http://api.adsabs.harvard.edu/v1/search/query?q=full%3Agtc&fl=bibcode,author");
    		//URL pagina = new URL("curl -H 'Authorization: Bearer:WG8qpt5jwF4x1zX0LE4FCSbQjh7QOYz5TUHk7fHg' 'http://api.adsabs.harvard.edu/v1/search/query?q=full%3Agtc&fl=bibcode,author'");
   // 		BufferedReader in = new BufferedReader(new InputStreamReader(pagina.openStream()));
   // 		String entrada;
   // 		while ((entrada = in.readLine()) != null){
   // 			System.out.println(entrada);
    			//if(entrada.contains("http://labs.adsabs.harvard.edu/ui/abs/")){
    			
    			    
    //		    }
    
		
		
		File fichLog = new File("/pcdisk/oort/jmalacid/data/oort6/HORuS/horus_files.log");
		FileWriter fw = new FileWriter(fichLog);
		
		writeLog(fw,"GTC4-19BFLO;0007");
		/*writeLog(fw,"GTC4-19BFLO;0004");
		writeLog(fw,"GTC4-19BFLO;0005");
		writeLog(fw,"GTC4-19BFLO;0005a");
		writeLog(fw,"GTC4-19BFLO;0006");
		writeLog(fw,"GTC4-19BFLO;0007");*/
		fw.close();
		
		
/*		//String name = "J165315.06-234943.0";
		String name = "NGC 4321";
		try {
			System.out.println("11");
			
			//final String name = id;
		    final String oty = "";
		    Double ra = Double.NaN;
		    Double dec = Double.NaN;
		    final String USER_AGENT = "Mozilla/5.0";

		    URL obj;
		    //try {
		     obj = new URL("http://cds.u-strasbg.fr/viz-bin/nph-sesame?" + name.replaceAll(" ", "%20"));
		    //obj = new URL("http://cdsweb.u-strasbg.fr/cgi-bin/nph-sesame/-oxp/NSV?"+name);
		    //obj = new URL("http://cds.u-strasbg.fr/cgi-bin/nph-sesame?" + name.replaceAll(" ", "%20"));
		      final HttpURLConnection con = (HttpURLConnection)
		   obj.openConnection();
		      con.setRequestMethod("POST");
		      con.setRequestProperty("User-Agent", USER_AGENT);
		      con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		      con.setDoOutput(true);

		      final int responseCode = con.getResponseCode();
		      System.out.println("\nSending 'POST' request to URL : " +
		obj.toString());
		      System.out.println("Response Code : " + responseCode);

		      final BufferedReader in = new BufferedReader(new
		InputStreamReader(con.getInputStream()));

		      String inputLine;

		     

		      while ((inputLine = in.readLine()) != null) {
		    	  System.out.println(inputLine);
		    	  
		        if (inputLine.startsWith("%J ")) {
		          final String[] fields = inputLine.split(" ", -1);
		          ra = Double.valueOf(fields[1]);
		          dec = Double.valueOf(fields[2]);
		        }
		      }
		      
			
			
			//ResultadoSesameClient resultado = SesameClient.sesameSearch(name);
			
			System.out.println("22");
			if(ra!=null && dec!=null){	
				Double[] coords = new Double[]{ra,dec};
				System.out.println("ra: "+coords[0]+", dec: "+coords[1]);
			}else{
				System.out.println("ra y dec null");
			}
		} catch (Exception e) {
			//this.addWarning("Unable to resolve object: \""+lineas[i].trim().replaceAll("%", "").replaceAll("<", "").replaceAll(">", "").replaceAll("script", "")+"\"");
			System.out.println(e.toString());
			System.out.println("ERROR");
			
			//Probamos si es un J...
			if(name.startsWith("J")){
				//Componemos las coordenadas
				String[] partes=null;
				String coor = null;
				
				if(name.contains("+")){
					partes=name.split("\\+");
					coor = partes[0].substring(1, 3)+":"+partes[0].substring(3, 5)+":"+partes[0].substring(5)+" +"+partes[1].substring(0, 2)+":"+partes[1].substring(2, 4)+":"+partes[1].substring(4);
					
				}else if(name.contains("-")){
					partes=name.split("\\-");
					coor = partes[0].substring(1, 3)+":"+partes[0].substring(3, 5)+":"+partes[0].substring(5)+" -"+partes[1].substring(0, 2)+":"+partes[1].substring(2, 4)+":"+partes[1].substring(4);
					
				}
				
				if(coor!=null && Coordenadas.checkCoordinatesFormat(coor)){
					Double[] coords = Coordenadas.coordsInDeg(coor);
					System.out.println("ra: "+coords[0]+", dec: "+coords[1]);
					if(coords!=null){
						
					}
					
				}else{
					System.out.println("no hay coords");
				}
				
				
				
				
			}else{
				System.out.println("ni J ni na");
			}
		}
		
		*/
/*		Timestamp initime= new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		System.out.println("El initime es: "+ initime);
		
		Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(initime.getTime());
	   // calendar.add(calendar.MONTH, -6);
	    calendar.add(calendar.YEAR, -1);
	    Timestamp priv = new Timestamp(calendar.getTimeInMillis());
	    Timestamp fecha = new Timestamp(118, 0, 1, 1, 1, 1, 1);
	    System.out.println("Private 6 meses: "+priv+", fecha: "+fecha);
	    if(priv.before(fecha)){
	    	System.out.println("es privado");
	    }else{
	    	System.out.println("es publico");
	    }
*/	   
	    
		//String nombre="Luis Julian Goicoechea Santamaria";
		
		/*String name[] = nombre.split(" ");
		System.out.println(name[0]);
		String surname = nombre.replaceAll(name[0], "").trim();
		System.out.println("surname: "+surname);
		
		System.out.println("pwd: '"+Encriptado.md5("123456")+"'");*/
		
		
		/*fits= new File("/pcdisk/oort/jmalacid/data/proyectos/GTC/prueba/object/test.fits.gz");
		
		System.out.println("path:"+fits.getAbsolutePath());
		
		Probador p = new Probador();
		boolean hecho = p.moveBias(fits);
		
		System.out.println(hecho);
		System.out.println("path:"+fits.getAbsolutePath());*/
		
		
		
		
		/*//Fits fEntrada = new Fits(fits,true);	
		try{
			System.out.println("1: ");
			
			//Vamos a moverlo
			File otro = new File("/pcdisk/oort/jmalacid/data/proyectos/GTC/prueba/mover/test.fits.gz");
			Probador p = new Probador();
			//p.moveFile("/pcdisk/oort/jmalacid/data/proyectos/GTC/prueba/test.fits.gz","/pcdisk/oort/jmalacid/data/proyectos/GTC/prueba/mover/test.fits.gz");
			
			File dirBias = new File("/pcdisk/oort/jmalacid/data/proyectos/GTC/prueba/mover");
			if(dirBias.isDirectory() && dirBias.exists()){
				
			}else{
				dirBias.mkdir();
				
			}
			
			
			
			System.out.println(fits.renameTo(otro));
			fits = otro;
			System.out.println("2: "+fits.getParent());
			System.out.println("3: "+fits.getAbsolutePath());
			
			
			
	    }catch(Exception e){
	    	e.printStackTrace();
	    }*/
	    
		/*//Método para comprobar keywords de ficheros por un tipo de error
		DriverBD drive = new  DriverBD();
		Connection con = drive.bdConexion();

		DBPrivate union = new DBPrivate(con);

		String[] progs = union.fileError("E-0019");
		con.close();
		int sp=0;
		int im=0;
		
		for(int i=0; i<progs.length;i++){
			
			//Comprobamos el nombre
			if(progs[i].contains("object") && (progs[i].indexOf("LongSlitSpectroscopy")>0 || progs[i].indexOf("BroadBand")>0)){
				System.out.println(progs[i]);
			}else{
				//System.out.println(progs[i]);
			}
			
			try{
			File fits= new File("/pcdisk/oort6/raul/gtc/dataNew"+progs[i]);
			
			Fits fEntrada = new Fits(fits);	
			try{
				fEntrada.read();	    
		    }catch(PaddingException e){
		    	fEntrada.addHDU(e.getTruncatedHDU());
		    }
		    BasicHDU hdu = fEntrada.getHDU(0);   		    
		    Header header=hdu.getHeader();
		   
		    String obsmode = null;
		    try{
		    	System.out.println("AA");
		    	obsmode = header.findCard("GTCPROGI").getValue();
		    }catch(Exception e){
		    	e.printStackTrace();
		    }
		    //String obsmode2 = header.findCard("GTCPRGI").getValue();
		    System.out.println("BB");
		    //String obsclass = header.findCard("OBSCLASS").getValue();
		    //String obstype = header.findCard("OBSTYPE").getValue();
		    //String slit = header.findCard("SLIT").getValue();
		    //String grating = header.findCard("GRATING").getValue();
		    
		    
		    if(obsmode!=null){
		    	System.out.println(progs[i]+" | "+obsmode+" | ");
		    	im=im+1;
		    }else if(obsmode.equalsIgnoreCase("Imaging")){
		    	//System.out.println(progs[i]+" | "+slit+" | "+grating+" | "+obstype);
		    	//im = im+1;
		    }
		    //Comprobar el submodo de LSS
			String grism = header.findCard("GRISM").getValue();
			String maskname = header.findCard("MASKNAME").getValue();
			Double slitw = new Double(header.findCard("SLITW").getValue());
			
			System.out.println(progs[i]+": "+grism+", "+maskname+", "+slitw );
			
			if( grism!=null && !grism.equalsIgnoreCase("OPEN") &&
					maskname!=null && !maskname.equalsIgnoreCase("NOMASK") && !maskname.equalsIgnoreCase("NULL") &&
							slitw!=null && slitw.doubleValue()!=0 ){
				System.out.println("SI CUMPLE!!!!!!!!!!!!!!!!!!!!!!");
			}
		//	if(!(obsmode.equalsIgnoreCase("osirisbroadbandimage") || obsmode.equalsIgnoreCase("osirisbias"))){
			
				
				
			//}
			}catch (Exception e){
				//System.out.println("error: "+progs[i]);
				//e.printStackTrace();
			}
		}
		

		System.out.println("sp: "+im);
*/
	}

	private static void writeLog(FileWriter fw, String texto) throws IOException{
		//System.out.println(texto);
		if(fw!=null) fw.write(texto+"\n");
	}
	private boolean moveBias(File fichBias){

		//Fichero destino
		System.out.println(fichBias.getAbsolutePath().replaceAll("object", "bias"));
		File dest = new File(fichBias.getAbsolutePath().replaceAll("object", "bias"));
		File dirBias = new File(fichBias.getParent().replaceAll("object", "bias"));
			if(!(dirBias.isDirectory() && dirBias.exists())){
				dirBias.mkdir();

			}
		boolean move = fichBias.renameTo(dest);
		setFits(dest);
		
		return move;
	}

	public static File getFits() {
		return fits;
	}

	public static void setFits(File fits) {
		Probador.fits = fits;
	}
	
}
