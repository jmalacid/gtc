package svo.gtc.utiles;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.fits.PaddingException;
import svo.gtc.db.DriverBD;
import svo.gtc.db.priv.DBPrivate;

public class ReducMEGARA {
	
	public static void main(String args[]) throws FitsException, IOException, SQLException{
	

	//Aquí definimos los valores necesarios, path, programa y ob
	String prog_id= "GTC6-19BGMEG";
	String obl_id = "OB0008";
	String path ="/pcdisk/oort/jmalacid/data/gtc/MEGARA/"+prog_id+"/"+obl_id;
	
	
	File dir= new File(path);
	File[] files = listar(dir);
	
	//Método para comprobar keywords de ficheros por un tipo de error
			DriverBD drive = new  DriverBD();
			Connection con = drive.bdConexion();

			DBPrivate union = new DBPrivate(con);

	for(int i=0; i<files.length;i++){
		
	
		
		
		//Obtenemos el directorio padre, que nos interesa
		String path_dir = files[i].getParent();
		if(path_dir.contains("object")){
			path_dir="object";
		}else if(path_dir.contains("arc")){
			path_dir="arc";
		}else if(path_dir.contains("bias")){
			path_dir="bias";
		}else if(path_dir.contains("flat")){
			path_dir="flat";
		}else if(path_dir.contains("stds")){
			path_dir="stds";
		}
		
		String filename = files[i].getName();
		
		Fits fEntrada = new Fits(files[i]);	
		try{
			fEntrada.read();	    
	    }catch(PaddingException e){
	    	fEntrada.addHDU(e.getTruncatedHDU());
	    }
	    BasicHDU hdu = fEntrada.getHDU(0);   		    
	    Header header=hdu.getHeader();
		
	    String INSMODE = header.findCard("INSMODE").getValue();
	    String VPH = header.findCard("VPH").getValue();
	    String BLCKUUID = header.findCard("BLCKUUID").getValue();
	    Double SPECLAMP = null;
	    try{
	    	SPECLAMP = Double.valueOf(header.findCard("SPECLAMP").getValue());
	    }catch(Exception e){
	    	//No es numérico
	    }
	    Double SENTEMP4 = null;
	    try{
	    	SENTEMP4 = Double.valueOf(header.findCard("SENTEMP4").getValue());
	    }catch(Exception e){ 	
	    }
	    
	    String OBJECT = header.findCard("OBJECT").getValue();
	    
		
	    System.out.println(files[i].getName()+"   -   "+path_dir+"   -   "+VPH+"   -   "+BLCKUUID+"   -   "+OBJECT+"   -   "+SPECLAMP+"   -   "+INSMODE+"   -   "+SENTEMP4);
	    
	    //Creamos el objeto ObjMegara
	    ObjMegara obj = new ObjMegara(prog_id, obl_id, path_dir, filename, INSMODE, VPH, BLCKUUID, SPECLAMP, SENTEMP4);
		
		//Introducimos info a la BD
		union.addMegara(obj);
	}

	
	//Seleccionamos los bloques de ciencia que se van a reducir
	//Select por blckuuid, insmode y vph
	ObjMegara[] grupo = union.SelectGrupo(prog_id, obl_id);
		
	for(int j=0; j<grupo.length; j++){
		System.out.println("grupo: "+grupo[j].getINSMODE()+" - "+ grupo[j].getVPH());
		//Si hay bias creamos bias (todos los bias)
		String[] bias = union.SelectBias(grupo[j]);
		File archbias = new File(path+"/0_bias_"+j+".yaml");
	       BufferedWriter bwbias;
	       if(archbias.exists()) {
	    	   bwbias = new BufferedWriter(new FileWriter(archbias));
	    	   bwbias.write("id: 0_bias\nmode: MegaraBiasImage\ninstrument: MEGARA\nframes:\n");
	           for(int k=0; k<bias.length;k++){
	        	   bwbias.write("   - "+bias[k]+"\n");
	           }
	        } else {
	        	bwbias = new BufferedWriter(new FileWriter(archbias));
		    	   bwbias.write("id: 0_bias\nmode: MegaraBiasImage\ninstrument: MEGARA\nframes:\n");
		           for(int k=0; k<bias.length;k++){
		        	   bwbias.write("   - "+bias[k]+"\n");
		           }
	        }
	       bwbias.close();
		
		//Creamos fichero tracemap, modelmap y flatfield del directorio flat a partir de VPH y INSMODE
		String[] flat = union.SelectFlat(grupo[j]);
		File archtrace = new File(path+"/1_tracemap_"+j+".yaml");
	       BufferedWriter bwtm;
	       if(archtrace.exists()) {
	    	   bwtm = new BufferedWriter(new FileWriter(archtrace));
	    	   bwtm.write("id: 1_"+grupo[j].getVPH()+"\nmode: MegaraTraceMap\ninstrument: MEGARA\nframes:\n");
	           for(int k=0; k<flat.length;k++){
	        	   bwtm.write("   - "+flat[k]+"\n");
	           }
	          
	        } else {
	        	bwtm = new BufferedWriter(new FileWriter(archtrace));
	        	bwtm.write("id: 1_"+grupo[j].getVPH()+"\nmode: MegaraTraceMap\ninstrument: MEGARA\nframes:\n");
		           for(int k=0; k<flat.length;k++){
		        	   bwtm.write("   - "+flat[k]+"\n");
		           }
	        }
	       bwtm.close();
	       
	     File archmodel = new File(path+"/2_modelmap_"+j+".yaml");
	       BufferedWriter bwm;
	       if(archtrace.exists()) {
	    	   bwm = new BufferedWriter(new FileWriter(archmodel));
	    	   bwm.write("id: 2_"+grupo[j].getVPH()+"\nmode: MegaraModelMap\ninstrument: MEGARA\nframes:\n");
	           for(int k=0; k<flat.length;k++){
	        	   bwm.write("   - "+flat[k]+"\n");
	           }
	          
	        } else {
	        	bwm = new BufferedWriter(new FileWriter(archmodel));
	        	bwm.write("id: 2_"+grupo[j].getVPH()+"\nmode: MegaraModelMap\ninstrument: MEGARA\nframes:\n");
		           for(int k=0; k<flat.length;k++){
		        	   bwm.write("   - "+flat[k]+"\n");
		           }
	        }
	       bwm.close();
	       
	       File archfib = new File(path+"/4_fiberflat_"+j+".yaml");
	       BufferedWriter bwf;
	       if(archtrace.exists()) {
	    	   bwf = new BufferedWriter(new FileWriter(archfib));
	    	   bwf.write("id: 4_"+grupo[j].getVPH()+"\nmode: MegaraFiberFlatImage\ninstrument: MEGARA\nframes:\n");
	           for(int k=0; k<flat.length;k++){
	        	   bwf.write("   - "+flat[k]+"\n");
	           }
	           bwf.write("requirements:\n extraction_offset: [0.0]");
	        } else {
	        	bwf = new BufferedWriter(new FileWriter(archfib));
		    	bwf.write("id: 4_"+grupo[j].getVPH()+"\nmode: MegaraFiberFlatImage\ninstrument: MEGARA\nframes:\n");
		        for(int k=0; k<flat.length;k++){
		         bwf.write("   - "+flat[k]+"\n");
		        }
		        bwf.write("requirements:\n extraction_offset: [0.0]");
	        }
	       bwf.close();
		
		
		//creamos el fichero de calibracion de ondas
		String[] arc = union.SelectArc(grupo[j]);
		File archarc = new File(path+"/3_wavecalib_"+j+".yaml");
	       BufferedWriter bwa;
	       if(archarc.exists()) {
	    	   bwa = new BufferedWriter(new FileWriter(archarc));
	    	   bwa.write("id: 3_"+grupo[j].getVPH()+"\nmode: MegaraArcCalibration\ninstrument: MEGARA\nframes:\n");
	           for(int k=0; k<arc.length;k++){
	        	   bwa.write("   - "+arc[k]+"\n");
	           }
	           bwa.write("requirements:\n extraction_offset: [0.0]\n store_pdf_with_refined_fits: 1");
	        } else {
	        	bwa = new BufferedWriter(new FileWriter(archarc));
		    	bwa.write("id: 3_"+grupo[j].getVPH()+"\nmode: MegaraArcCalibration\ninstrument: MEGARA\nframes:\n");
		        for(int k=0; k<arc.length;k++){
		        	 bwa.write("   - "+arc[k]+"\n");
		        }
		        bwa.write("requirements:\n extraction_offset: [0.0]\n store_pdf_with_refined_fits: 1");
	        }
	        bwa.close();
		
		
		//creamos el fichero de flujo
		String[] stds = union.SelectStds(grupo[j]);
		File archadq = new File(path+"/6_Lcbacquisition_"+j+".yaml");
	       BufferedWriter bwad;
	       if(archadq.exists()) {
	    	   bwad = new BufferedWriter(new FileWriter(archadq));
	    	   bwad.write("id: 6_"+grupo[j].getVPH()+"\nmode: MegaraLcbAcquisition\ninstrument: MEGARA\nframes:\n");
	           for(int k=0; k<stds.length;k++){
	        	   bwad.write("   - "+stds[k]+"\n");
	           }
	           bwad.write("requirements:\n extraction_offset: [+1.5]\n");
	        } else {
	        	bwad = new BufferedWriter(new FileWriter(archadq));
		    	bwad.write("id: 6_"+grupo[j].getVPH()+"\nmode: MegaraLcbAcquisition\ninstrument: MEGARA\nframes:\n");
		        for(int k=0; k<stds.length;k++){
		        	bwad.write("   - "+stds[k]+"\n");
		        }
		        bwad.write("requirements:\n extraction_offset: [+1.5]\n");
	        }
	       bwad.close();
	       
	       File archss = new File(path+"/7_Standardstar_"+j+".yaml");
	       BufferedWriter bwss;
	       if(archss.exists()) {
	    	   bwss = new BufferedWriter(new FileWriter(archss));
	    	   bwss.write("id: 7_"+grupo[j].getVPH()+"\nmode: MegaraLcbStdStar\ninstrument: MEGARA\nframes:\n");
	           for(int k=0; k<stds.length;k++){
	        	   bwss.write("   - "+stds[k]+"\n");
	           }
	           bwss.write("requirements:\n extraction_offset: [+1.5]\n reference_spectrum: mbd284211_stis.dat\n reference_extinction: extinction_LP.txt\n position:  [0.3212740713935877, 0.04620708253153888]\n sigma_resolution: 100");
	        } else {
	        	bwss = new BufferedWriter(new FileWriter(archss));
		    	   bwss.write("id: 7_"+grupo[j].getVPH()+"\nmode: MegaraLcbStdStar\ninstrument: MEGARA\nframes:\n");
		           for(int k=0; k<stds.length;k++){
		        	   bwss.write("   - "+stds[k]+"\n");
		           }
		           bwss.write("requirements:\n extraction_offset: [+1.5]\n reference_spectrum: mbd284211_stis.dat\n reference_extinction: extinction_LP.txt\n position:  [0.3212740713935877, 0.04620708253153888]\n sigma_resolution: 100");
		        
	        }
	       bwss.close();
		
		
		//Creamos el fichero de reducción de datos científicos
		String[] sci = union.SelectSci(grupo[j]);
		File archivo = new File(path+"/8_reduce_LCB_"+j+".yaml");
	       BufferedWriter bw;
	       if(archivo.exists()) {
	           bw = new BufferedWriter(new FileWriter(archivo));
	           bw.write("id: 8_"+grupo[j].getVPH()+"\nmode: MegaraLcbImage\ninstrument: MEGARA\nframes:\n");
	           for(int k=0; k<sci.length;k++){
	        	   bw.write("   - "+sci[k]+"\n");
	           }
	           bw.write("requirements:\n extraction_offset: [+1.5]\n reference_extinction: extinction_LP.txt");
	        } else {
	        	bw = new BufferedWriter(new FileWriter(archivo));
		        bw.write("id: 8_"+grupo[j].getVPH()+"\nmode: MegaraLcbImage\ninstrument: MEGARA\nframes:\n");
		        for(int k=0; k<sci.length;k++){
		        	bw.write("   - "+sci[k]+"\n");
		        }
		        bw.write("requirements:\n extraction_offset: [+1.5]\n reference_extinction: extinction_LP.txt");
	        }
	        bw.close();
	   System.out.println("Ficheros creados");
	}
	    
	    
	    
	    
	    
	//}
	
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
