package svo.gtc.ingestion;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.Header;
import nom.tam.fits.PaddingException;
import svo.gtc.proddat.ProdDatos;
import svo.varios.utiles.seguridad.Encriptado;

public class IngestionHipercamAutomatico {

	public static void main(String[] args) throws Exception {
		
		//Definimos el programa
		String prog_id = "GTC103-17B";
		String obl_id = "0010";
		Integer predId =30130;//SELECT greatest(0,(select max(pred_id)+1 from prodreducido));
		
		
		
		
		
		//String path = "/2000X/"+prog_id+"/"+obl_id+"/";
		String path = prog_id+"/OB"+obl_id+"/";
		String path_base = "/pcdisk/oort/jmalacid/data/gtc/Errores/hipercam/";
		Double ra_red = 0.0;
		Double dec_red = 0.0;
		System.out.println("------Prog: "+prog_id+"------OB: "+obl_id);
		File[] ficheros = listar(new File(path_base+path));
		
		//Ingestar ficheros QC por OB
		int red=0;
		
		//Ingestamos el OB
		System.out.println("insert into obsblock (prog_id, obl_id, obl_pi) values ('"+prog_id+"','"+obl_id+"',(select prog_pi from programa where prog_id='"+prog_id+"'));");
				
		
		for(int i=0; i<ficheros.length; i++){
			//System.out.println(i);
			if(ficheros[i].getName().contains("qc.txt")){
				System.out.println("insert into logfile (prog_id, obl_id, log_filename, bpath_id, log_path, log_type) values ('"+prog_id+"','"+obl_id+"','"+ficheros[i].getName()+"',1,'"+path+"','QC');");
			}else if(ficheros[i].getName().contains(".red")){
				System.out.println("insert into logfile (prog_id, obl_id, log_filename, bpath_id, log_path, log_type) values ('"+prog_id+"','"+obl_id+"','"+ficheros[i].getName()+"',1,'"+path+"','RED');");
			}else if(ficheros[i].getName().contains(".html")){
				System.out.println("insert into logfile (prog_id, obl_id, log_filename, bpath_id, log_path, log_type) values ('"+prog_id+"','"+obl_id+"','"+ficheros[i].getName()+"',1,'"+path+"','HTML');");
			}else if(ficheros[i].getName().contains(".log")){
				
				System.out.println("INSERT INTO prodreducido (pred_id, usr_id, col_id, bpath_id, pred_filename, pred_path, pred_filenameorig, pred_filesize, pred_md5sum, pred_ra, pred_de, pred_type) VALUES "
						+ "('"+(predId+red)+"','jmalacid',5,1,'"+ficheros[i].getName()+"','"+path+"','"+ficheros[i].getName()+"','"+new Long(ficheros[i].length())+"','"+Encriptado.md5(ficheros[i])+"','"+ra_red+"','"+dec_red+"','HiPER');");
				red +=1;
			}
		}
		File[] fichObj = listar(new File(path_base+path+"object/"));
		for(int i=0; i<fichObj.length; i++){
		
			Integer prod_id=new Integer(fichObj[i].getName().substring(0, fichObj[i].getName().indexOf("-")));
			String filesize = "13050000";
			System.out.println("-------"+fichObj[i].getName()+"-------------------");
			try{
				boolean compressed = false;
				
				if(fichObj[i].getName().toUpperCase().endsWith(".GZ")){
					compressed = true;
				}
			    Fits fEntrada = new Fits(fichObj[i],compressed);
			   
			    try{
			    	fEntrada.read();
			    }catch(PaddingException e){
			    	fEntrada.addHDU(e.getTruncatedHDU());
			    }
			    BasicHDU hdu = fEntrada.getHDU(0);   
			    
			    Header header=hdu.getHeader();
				
				
			   /*
			    BasicHDU[] hdus =  new BasicHDU[0];
				
				hdus = fEntrada.read();
				BasicHDU h = hdus[1];
					if( h != null) {*/
				
			
				Double ra =new Double(header.findCard("RADEG").getValue());
				Double de = new Double(header.findCard("DECDEG").getValue());
				//Timestamp ini = new Timestamp(header.findCard("DATE-OBS").getValue());//DATE-OBS
				//Timestamp end = "";
				Double exp = new Double(header.findCard("EXPTIME").getValue());//EXPTIME
				Double airmass = new Double(header.findCard("AIRMASS").getValue());//AIRMASS
				String object = header.findCard("OBJECT").getValue().trim();//OBJECT
				String observer = header.findCard("PI").getValue().trim();//OBJECT
				Timestamp ini = Timestamp.valueOf(header.findCard("DATE-OBS").getValue().replaceAll("T", " "));//DATE-OBS //quitar la T por un espacio
					
				String point = "spoint'("+ra+"d,"+de+"d)'";
				
				//Calculamos el endtime
				Calendar cal = Calendar.getInstance();
		        cal.setTimeInMillis(ini.getTime());
		        cal.add(Calendar.SECOND, exp.intValue());
		        Timestamp end = new Timestamp(cal.getTime().getTime());
				
				
				
				String insert1 = "INSERT INTO proddatos (prod_id, prog_id, obl_id, det_id, ins_id, mod_id, subm_id, conf_id, bpath_id,  " +
						"  prod_filename, prod_path, prod_filesize, prod_ra, prod_de, prod_initime , prod_endtime"
						+ ", prod_exposure, prod_airmass, prod_observer, prod_object, prod_point";
				String insert2 = ") VALUES ('"+prod_id+"','"+prog_id+"','"+obl_id+"',5,'HIP','IMG','IMG','45557',1,'"+fichObj[i].getName()+"','"+path+"object/','"+filesize+"'"
						+ ",'"+ra+"','"+de+"','"+ini+"','"+end+"','"+exp+"','"+airmass+"','"+observer+"','"+object+"',"+point+"";
				
				
				String insert = insert1 + insert2 + ");";
				System.out.println(insert);
				
				for(int k=0; k<red; k++){
					System.out.println("insert into pred_prod (pred_id, prog_id, obl_id, prod_id) values ('"+(predId+k)+"','"+prog_id+"','"+obl_id+"','"+prod_id+"');");
				}
			}catch(Exception e){
				String insert1 = "INSERT INTO proddatos (prod_id, prog_id, obl_id, det_id, ins_id, mod_id, subm_id, conf_id, bpath_id,  " +
						"  prod_filename, prod_path, prod_filesize, prod_ra, prod_de, prod_initime , prod_endtime"
						+ ", prod_exposure, prod_airmass, prod_observer, prod_object, prod_point";
				String insert2 = ") VALUES ('"+prod_id+"','"+prog_id+"','"+obl_id+"',5,'HIP','IMG','IMG','45557',1,'"+fichObj[i].getName()+"','"+path+"object/','"+filesize+"'"
						+ ",'','','','','','','','',";
				
				
				String insert = insert1 + insert2 + ");";
				System.out.println("-------ERROR: "+fichObj[i].getName()+"-------------------");
				System.out.println(insert);
				System.out.println("insert into pred_prod (pred_id, prog_id, obl_id, prod_id) values ('"+predId+"','"+prog_id+"','"+obl_id+"','"+prod_id+"');");
				System.out.println("---------------------------");
			}
		}
		
	}
	
	public static File[] listar(File dir) {
		 
		List<File> lista = new ArrayList<File>();
		
        File listFile[] = dir.listFiles();
        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    //File[] fichs2 = listar(listFile[i]);
                    //for(int j=0; j<fichs2.length; j++){
    				//	lista.add(fichs2[j]);
    				//}
                } else {
                    lista.add(listFile[i]);
                }
            }
        }
        File[] fin = lista.toArray(new File[lista.size()]);
        return fin;
    }
}
