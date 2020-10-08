package svo.gtc.utiles;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;

public class ModificaFits {

	
	private static Fits f = null;
	private static File fich = null;
	
	public static void main(String args[]) throws FitsException, IOException{
		//fich = new File("/pcdisk/oort/jmalacid/data/proyectos/GTC/SpectSSAP/0000074298-20100920-OSIRIS-OsirisLongSlitSpectroscopy_RED_0.fits");
		fich = new File("/pcdisk/oort/jmalacid/data/proyectos/GTC/SpectSSAP/0000245119-20120810-OSIRIS-OsirisLongSlitSpectroscopy_RED_0.fits");
		f = new Fits(fich, false);
		
		//BasicHDU[] hdus = f.read();
		
		BasicHDU hdu1 = f.getHDU(0);  
	    //BasicHDU hdu2 = fEntrada.getHDU(2);
	    Header header1=hdu1.getHeader();
	    header1.deleteKey("CTYPE1");
	    header1.addValue("CTYPE1","Pepito","Prueba");
	    header1.deleteKey("CTYPE2");
	    header1.addValue("CTYPE2","Pepito2","Prueba");
	    header1.deleteKey("OBJECT");
	    header1.addValue("OBJECT","Juanito","Prueba");
	    header1.deleteKey("BUNIT");
	    header1.addValue("BUNIT","Garbanzo","Prueba");
	    
	    DataOutputStream os = new DataOutputStream(new FileOutputStream("/pcdisk/oort/jmalacid/data/proyectos/GTC/SpectSSAP/prueba.fits"));
			f.write(os);
			
			os.flush();
			os.close();
			
		
	    
		//f.modifyKeyword("IMAGETYP", "FLAT");

		//f.writeFile();
	}
	
	
	public void createF(){
		
	}
	
	public void modifyKeyword(String name, String value){
		
		
		String comment;
		try {
			
			comment = f.getHDU(0).getHeader().findCard(name).getComment();
			f.getHDU(0).getHeader().deleteKey(name);
	        f.getHDU(0).getHeader().addValue(name,value,comment);
	        
	        
	        
	        
		} catch (FitsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	public void writeFile(){
		
		DataOutputStream os;
		try {
			os = new DataOutputStream(new FileOutputStream(fich.getAbsolutePath()));
			f.write(os);
			
			os.flush();
			os.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FitsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
