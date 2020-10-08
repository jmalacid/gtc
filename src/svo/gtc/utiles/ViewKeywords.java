package svo.gtc.utiles;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import nom.tam.fits.*;
import svo.gtc.db.DriverBD;
import svo.gtc.proddat.ProdDatos2;
import svo.gtc.proddat.ProdDatosEmir2;

public class ViewKeywords {

	public static void main(String args[]) throws FitsException, IOException, SQLException{
		
		DriverBD driver = new  DriverBD();
		Connection con = null;
		
		con = driver.bdConexion();
		
		File dir= new File("/pcdisk/oort6/raul/gtc/Errores/GTC40-18B/OB0014c/");
		File[] fits = extraeFits(dir);
		
		for(int i=0; i<fits.length;i++){
			
			ProdDatos2 prod = new ProdDatos2(fits[i]);
			
			/*Fits fEntrada = new Fits(progs[i]);	
			try{
				fEntrada.read();	    
		    }catch(PaddingException e){
		    	fEntrada.addHDU(e.getTruncatedHDU());
		    }
		    BasicHDU hdu = fEntrada.getHDU(0);   		    
		    Header header=hdu.getHeader();*/
			
			prod = new ProdDatosEmir2(prod, con);
		    
		    
		    System.out.println(i+" : "+fits[i].getName()+" , test:"+prod.getInstrument());
		}
		
		/*String[] programa ={"GTC35-19B","GTC60-19B","GTC100-19B","GTC01-TEC","GTC122-19B","GTC131-19B","GTC2-19BCNT","GTC130-19B","GTC5-19BFLO","GTC4-19BFLO"};
		
		for(int j=0; j<programa.length;j++){
		String path = "/pcdisk/oort6/raul/gtc/data/2000X/"+programa[j]+"/";
		File dir= new File(path);
		//File dir= new File("/pcdisk/oort/jmalacid/data/gtc/data/2000X/GTC2-19BCNT/OB0004/object/");
		File[] progs = listar(dir);
		
		//System.out.println("PATH     -   NAME   -    VPH    -    BLCKUUID");
		int TotalObj = 0;
		int TotalStds = 0;
		int TotalBias = 0;
		int TotalFlat = 0;
		int TotalArc = 0;
		int FileNoObj = 0;
		int FileNoStds =0;
		int SpecSMal = 0;
		int OtherSMal = 0;
		int CalSMal = 0;
		int SpecOMal = 0;
		int OtherOMal = 0;
		int CalOMal = 0;
		int FlatMal = 0;
		int FileNoFlat = 0;
		int OtherFMal = 0;
		int CalFMal = 0;
		int BiasMal = 0;
		int FileNoBias = 0;
		int ArcMal = 0;
		int FileNoArc = 0;
		
		for(int i=0; i<progs.length;i++){
			
			Fits fEntrada = new Fits(progs[i]);	
			try{
				fEntrada.read();	    
		    }catch(PaddingException e){
		    	fEntrada.addHDU(e.getTruncatedHDU());
		    }
		    BasicHDU hdu = fEntrada.getHDU(0);   		    
		    Header header=hdu.getHeader();
		   
		    String img = null;
		    try{
		    	 img = header.findCard("IMAGETYP").getValue();
		    }catch(Exception e){
		    	img="NO HAY";
		    }
		    
		    String obs = header.findCard("OBSTYPE").getValue();
		    String mirror = null;
		    try{
		    	 mirror = header.findCard("MIRROR").getValue();
		    }catch(Exception e){
		    	mirror="NO HAY MIRROR";
		    }
		    String flat = null;
		    try{
		    	 flat = header.findCard("FLATLAMP").getValue();
		    }catch(Exception e){
		    	flat="NO HAY FLAT";
		    }
		    String cal = null;
		    try{
		    	 cal = header.findCard("CALLAMP").getValue();
		    }catch(Exception e){
		    	cal="NO HAY CAL";
		    }
		    //System.out.println("");
		    if(progs[i].getParent().contains("object")){
		    	TotalObj++;
		    	if(progs[i].getName().contains("Spectroscopy")){
		    		if(!(img.equalsIgnoreCase("Spectroscopy") && mirror.equalsIgnoreCase("Sky") && cal.equalsIgnoreCase("off")&& flat.equalsIgnoreCase("off") && obs.equalsIgnoreCase("Spectroscopy"))){
		    			SpecOMal++;
		    		}
		    	}else if(progs[i].getName().contains("Other_type")){
		    		if(!(img.equalsIgnoreCase("Other_type") && mirror.equalsIgnoreCase("Sky") && cal.equalsIgnoreCase("off")&& flat.equalsIgnoreCase("off") && obs.equalsIgnoreCase("Other_type"))){
		    			OtherOMal++;
		    		}
		    	}else if(progs[i].getName().contains("Cal")){
		    		if(!(img.equalsIgnoreCase("cal") && mirror.equalsIgnoreCase("Calibration") && cal.equalsIgnoreCase("on")&& flat.equalsIgnoreCase("off") && obs.equalsIgnoreCase("cal"))){
		    			CalOMal++;
		    		}
		    	}else if(progs[i].getName().contains("Osiris")){
		    		
		    	}else{
		    		FileNoObj++;
		    	}
		    }else if(progs[i].getParent().contains("stds")){
		    	TotalStds++;
			    if(progs[i].getName().contains("Spectroscopy")){
			    	if(!(img.equalsIgnoreCase("Spectroscopy") && mirror.equalsIgnoreCase("Sky") && cal.equalsIgnoreCase("off")&& flat.equalsIgnoreCase("off") && obs.equalsIgnoreCase("Spectroscopy"))){
			    		SpecSMal++;
			    	}
			    }else if(progs[i].getName().contains("Other_type")){
			    	if(!(img.equalsIgnoreCase("Other_type") && mirror.equalsIgnoreCase("Sky") && cal.equalsIgnoreCase("off")&& flat.equalsIgnoreCase("off") && obs.equalsIgnoreCase("Other_type"))){
			    		OtherSMal++;
			    	}
			    }else if(progs[i].getName().contains("Cal")){
			    	if(!(img.equalsIgnoreCase("cal") && mirror.equalsIgnoreCase("Calibration") && cal.equalsIgnoreCase("on")&& flat.equalsIgnoreCase("off") && obs.equalsIgnoreCase("cal"))){
			    		CalSMal++;
			    	}
			    }else if(progs[i].getName().contains("Osiris")){
			    	
			    }else{
			    	FileNoStds++;
			    }
		    }else if(progs[i].getParent().contains("flat")){
		    	TotalFlat++;
		    	if(progs[i].getName().contains("Flat")){
		    		if(!(img.equalsIgnoreCase("Flat") && mirror.equalsIgnoreCase("Calibration") && cal.equalsIgnoreCase("off")&& flat.equalsIgnoreCase("on") && obs.equalsIgnoreCase("Flat"))){
		    			FlatMal++;
		    		}
		    	}else if(progs[i].getName().contains("Other_type")){
		    		if(!(img.equalsIgnoreCase("Other_type") && mirror.equalsIgnoreCase("Sky") && cal.equalsIgnoreCase("off")&& flat.equalsIgnoreCase("off") && obs.equalsIgnoreCase("Other_type"))){
		    			OtherFMal++;
		    		}
		    	}else if(progs[i].getName().contains("Cal")){
		    		if(!(img.equalsIgnoreCase("cal") && mirror.equalsIgnoreCase("Calibration") && cal.equalsIgnoreCase("on")&& flat.equalsIgnoreCase("off") && obs.equalsIgnoreCase("cal"))){
		    			CalFMal++;
		    		}
		    	}else{
		    		FileNoFlat++;
		    	}
		    	
		    }else if(progs[i].getParent().contains("bias")){
		    	TotalBias++;
		    	if(progs[i].getName().contains("Bias")){
		    		if(!(img.equalsIgnoreCase("Bias") && mirror.equalsIgnoreCase("Sky") && cal.equalsIgnoreCase("off")&& flat.equalsIgnoreCase("on") && obs.equalsIgnoreCase("Bias"))){
		    			BiasMal++;
		    		}
		    	}else{
		    		FileNoBias++;
		    	}
		    	
		    }else if(progs[i].getParent().contains("arc")){
		    	TotalArc++;
		    	if(progs[i].getName().contains("Cal")){
		    		if(!(img.equalsIgnoreCase("Cal") && mirror.equalsIgnoreCase("Calibration") && cal.equalsIgnoreCase("on")&& flat.equalsIgnoreCase("off") && obs.equalsIgnoreCase("Cal"))){
		    			ArcMal++;
		    		}
		    	}else{
		    		FileNoArc++;
		    	}
		    	
		    }
		}
		    //System.out.println(progs[i].getName()+"   -   "+det+"   -   "+obsmode+"   -   "+filter );
		    //System.out.println(progs[i].getAbsolutePath().replaceAll(path, "")+" - "+img+" - "+obs+" - "+mirror+" - "+flat+" - "+cal);
		    System.out.println(programa[j]);
		    
		    if(FileNoObj>0 || (SpecOMal+OtherOMal+CalOMal)>0){
			System.out.println("Object: "+TotalObj+", ficheros no spec,other_type, osiris o cal: "+FileNoObj);
			System.out.println("Errores configuración: Spec("+SpecOMal+"), Other_type("+OtherOMal+"), Cal("+CalOMal+")");
		    }
		    if(FileNoStds>0 || (SpecSMal+OtherSMal+CalSMal)>0){
			System.out.println("Stds: "+TotalStds+", ficheros no spec,other_type, osiris o cal: "+FileNoStds);
			System.out.println("Errores configuración: Spec("+SpecSMal+"), Other_type("+OtherSMal+"), Cal("+CalSMal+")");
		    }
		    if(FileNoFlat>0 || (FlatMal+OtherFMal+CalFMal)>0){
			System.out.println("Flat: "+TotalFlat+", ficheros no flat: "+FileNoFlat);
			System.out.println("Errores configuración: Flat("+FlatMal+"), Other_type("+OtherFMal+"), Cal("+CalFMal+")");
		    }
		    if(FileNoBias>0 || BiasMal>0){
			System.out.println("Bias: "+TotalBias+", ficheros no bias: "+FileNoBias);
			System.out.println("Errores configuración: Bias("+BiasMal+")");
		    }
		    if(FileNoArc>0 || ArcMal>0){
			System.out.println("Arc: "+TotalArc+", ficheros no arc: "+FileNoArc);
			System.out.println("Errores configuración: Arc("+ArcMal+")");
		    }
		}*/
		    
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
	
	public static File[] extraeFits(File dir){
		Vector<File> aux = new Vector<File>();
		
		if(!dir.isDirectory()) return new File[0];
		//System.out.println("dir: "+dir);
		File[] ficheros = dir.listFiles();		
		//System.out.println("ObsBlock-229, ficheros en OB: "+ficheros.length);
		for(int i=0; i<ficheros.length; i++){
			if(ficheros[i].isFile() && ficheros[i].getName().toUpperCase().endsWith(".FITS") || 
				ficheros[i].isFile() && ficheros[i].getName().toUpperCase().endsWith(".FITS.GZ")){
				aux.add(ficheros[i]);
			}else if(ficheros[i].isDirectory()){
				File[] ficherosAux = extraeFits(ficheros[i]);
				for(int j=0; j<ficherosAux.length; j++){
					aux.add(ficherosAux[j]);
				}
			}
		}
		
		return (File[])aux.toArray(new File[0]);
	}
	
}
