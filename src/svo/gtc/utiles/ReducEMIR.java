package svo.gtc.utiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.fits.PaddingException;

public class ReducEMIR {
	
	public static void main(String args[]) throws FitsException, IOException{
	File dir= new File("/pcdisk/oort/jmalacid/data/gtc/EMIR/data/");
	File[] progs = listar(dir);
	
	System.out.println("OBSBLOCK    - NOBSBLCK   -   IMGOBBL   - NIMGOBBL   - EXP   - NEXP    - FRSEC   -  NFRSEC");
	
	for(int i=0; i<progs.length;i++){
		if(progs[i].getName().endsWith("IMAGE.fits")){
			//System.out.println(progs[i].getName());
		Fits fEntrada = new Fits(progs[i]);	
		try{
			fEntrada.read();	    
	    }catch(PaddingException e){
	    	fEntrada.addHDU(e.getTruncatedHDU());
	    }
	    BasicHDU hdu = fEntrada.getHDU(0);   		    
	    Header header=hdu.getHeader();
	   
	    String OBSBLOCK = header.findCard("OBSBLOCK").getValue();
	    String NOBSBLCK = header.findCard("NOBSBLCK").getValue();
	    String IMGOBBL = header.findCard("IMGOBBL").getValue();
	    String NIMGOBBL = header.findCard("NIMGOBBL").getValue();
	    String EXP = header.findCard("EXP").getValue();
	    String NEXP = header.findCard("NEXP").getValue();
	    String FRSEC = header.findCard("FRSEC").getValue();
	    String NFRSEC = header.findCard("NFRSEC").getValue();
	    String FILTER = header.findCard("DATE-OBS").getValue();
	    
	    if(IMGOBBL.equals("1")){
	    	System.out.println(progs[i].getName()+"   -   "+OBSBLOCK+"   -   "+NOBSBLCK+"   -   "+IMGOBBL+"   -   "+NIMGOBBL+"   -   "+EXP+"   -   "+NEXP+"   -   "+FRSEC+"   -   "+NFRSEC +"   -   "+FILTER);
	    }
	    
	    
	}
	}
}

public static File[] listar(File dir){
	File[] progs = dir.listFiles();
	
	ArrayList fichs = new ArrayList();
	
	for(int i=0;i<progs.length;i++){
		if(progs[i].isDirectory()){
			File[] fichs_dentro= listar(progs[i]);
			for(int j =0;j<fichs_dentro.length;j++){
				fichs.add(fichs_dentro[j]);
			}
		}else if(progs[i].isFile() && progs[i].toString().contains("fits")){
			fichs.add(progs[i]);
		}
	}
	
	File[] result = (File[]) fichs.toArray(new File[fichs.size()]);
	
	return result;
}
}
