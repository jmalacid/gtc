package svo.gtc.ingestion;

import java.io.File;
import java.sql.Timestamp;
import java.util.Calendar;

import svo.varios.utiles.seguridad.Encriptado;

public class IngestionHipercamManual {

	public static void main(String[] args) throws Exception {
		
		//Definimos el programa
//COMPLETAR!!
		String prog_id = "GTC103-17B";
		String obl_id = "0010";
		
		
		
		
		String[] fichsQC = {"GTC01-19ADDT_0001a_qc.txt","GTC01-19ADDT_0001a_run0012.red","GTC01-19ADDT_0001a_run0011.red","GTC01-19ADDT_0001a_log.html"};
		
		
		String path = "/2000X/"+prog_id+"/OB"+obl_id+"/";
		
		//Ingestamos el OB
//		System.out.println("insert into obsblock (prog_id, obl_id, obl_pi) values ('"+prog_id+"','"+obl_id+"',(select prog_pi from programa where prog_id='"+prog_id+"'));");
		
		//Ingestar ficheros QC por OB
		for(int i=0; i<fichsQC.length; i++){
			String type = "";
			if(fichsQC[i].contains(".red")){
				type="RED";
			}else if(fichsQC[i].contains(".html")){
				type="HTML";
			}else{
				type="QC";
			}
//			System.out.println("insert into logfile (prog_id, obl_id, log_filename, bpath_id, log_path, log_type) values ('"+prog_id+"','"+obl_id+"','"+fichsQC[i]+"',1,'"+path+"','"+type+"');");
		}
		
		
		//Ingestar ficheros reducidos
//COMPLETAR
		File red= new File("/pcdisk/oort/jmalacid/data/gtc/Errores/hipercam/"+prog_id+"/OB"+obl_id+"/GTC01-19ADDT_0001a_run0012.log");
		Integer predId = 30128;//SELECT greatest(0,(select max(pred_id)+1 from prodreducido));
			
//		System.out.println("INSERT INTO prodreducido (pred_id, usr_id, col_id, bpath_id, pred_filename, pred_path, pred_filenameorig, pred_filesize, pred_md5sum, pred_ra, pred_de, pred_type) VALUES "
//					+ "('"+predId+"','jmalacid',5,1,'"+red.getName()+"','"+path+"','"+red.getName()+"','"+new Long(red.length())+"','"+Encriptado.md5(red)+"','"+0+"','"+0+"','HiPER');");
		
		
/*		//COMPLETAR
			File red2= new File("/pcdisk/oort/jmalacid/data/gtc/Errores/hipercam/"+prog_id+"/OB"+obl_id+"/GTC01-19ADDT_0001a_run0011.log");
			Integer predId2 =30128;//SELECT greatest(0,(select max(pred_id)+1 from prodreducido));
					
			System.out.println("INSERT INTO prodreducido (pred_id, usr_id, col_id, bpath_id, pred_filename, pred_path, pred_filenameorig, pred_filesize, pred_md5sum, pred_ra, pred_de, pred_type) VALUES "
							+ "('"+predId2+"','jmalacid',5,1,'"+red2.getName()+"','"+path+"','"+red2.getName()+"','"+new Long(red2.length())+"','"+Encriptado.md5(red2)+"','"+0+"','"+0+"','HiPER');");
*/
/*			
			//COMPLETAR
			File red3= new File("/pcdisk/oort/jmalacid/data/gtc/Errores/25oct19/"+prog_id+"/OB"+obl_id+"/GTC48-19A_0002_run0016.log");
			Integer predId3 =27297;//SELECT greatest(0,(select max(pred_id)+1 from prodreducido));
					
			System.out.println("INSERT INTO prodreducido (pred_id, usr_id, col_id, bpath_id, pred_filename, pred_path, pred_filenameorig, pred_filesize, pred_md5sum, pred_ra, pred_de, pred_type) VALUES "
							+ "('"+predId3+"','jmalacid',5,1,'"+red3.getName()+"','"+path+"','"+red3.getName()+"','"+new Long(red3.length())+"','"+Encriptado.md5(red3)+"','"+0+"','"+0+"','HiPER');");
*/	
		
		//Ingestar ficheros de ciencia (Tantos como tengamos en object)
//COMPLETAR!!
		String filename = "0002119333-20190607-HIPERCAM-Imaging.fits.gz";
		String filesize = "13050000";
		Double ra = 259.308136;//RADEG
		Double dec = 67.96995;//DECDEG
		Double airmass = 1.29;//AIRMASS
		Timestamp ini = Timestamp.valueOf("2019-06-08 01:45:53.5860");//DATE-OBS //quitar la T por un espacio
		Double exp = 0.39891;//EXPTIME
		String object = "GALEX J171708.5+675712";//OBJECT
		String observer ="vdhillon";//OBSERVER->PI
		
		
		Integer prod_id=new Integer(filename.substring(0, filename.indexOf("-")));
		String point = "spoint'("+ra+"d,"+dec+"d)'";
		
		//Calculamos el endtime
		Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ini.getTime());
        cal.add(Calendar.SECOND, exp.intValue());
        Timestamp end = new Timestamp(cal.getTime().getTime());
		
		String insert1 = "INSERT INTO proddatos (prod_id, prog_id, obl_id, det_id, ins_id, mod_id, subm_id, conf_id, bpath_id,  " +
				"  prod_filename, prod_path, prod_filesize, prod_ra, prod_de, prod_initime , prod_endtime"
				+ ", prod_exposure, prod_airmass, prod_observer, prod_object, prod_point";
		String insert2 = ") VALUES ('"+prod_id+"','"+prog_id+"','"+obl_id+"',5,'HIP','IMG','IMG','45557',1,'"+filename+"','"+path+"object/','"+filesize+"'"
				+ ",'"+ra+"','"+dec+"','"+ini+"','"+end+"','"+exp+"','"+airmass+"','"+observer+"','"+object+"',"+point+"";
		
		
		String insert = insert1 + insert2 + ");";
		System.out.println(insert);

		System.out.println("insert into pred_prod (pred_id, prog_id, obl_id, prod_id) values ('"+predId+"','"+prog_id+"','"+obl_id+"','"+prod_id+"');");
		//System.out.println("insert into pred_prod (pred_id, prog_id, obl_id, prod_id) values ('"+predId2+"','"+prog_id+"','"+obl_id+"','"+prod_id+"');");
		//System.out.println("insert into pred_prod (pred_id, prog_id, obl_id, prod_id) values ('"+predId3+"','"+prog_id+"','"+obl_id+"','"+prod_id+"');");
		
/*		//Ingestar ficheros de ciencia (Tantos como tengamos en object)
	//COMPLETAR!!
				filename = "0002101963-20190604-HIPERCAM-Imaging.fits.gz";
				filesize = "13050000";
				ra = 272.532166;//RADEG
				dec = -15.29308;//DECDEG
				airmass = 1.45;//AIRMASS
				ini = Timestamp.valueOf("2019-06-05 01:27:26.2823");//DATE-OBS (quitar T)
				exp = 7.3137400;//EXPTIME
				object = "Quaoar";//OBJECT
				observer ="jlortiz";//OBSERVER
				
				
				prod_id=new Integer(filename.substring(0, filename.indexOf("-")));
				point = "spoint'("+ra+"d,"+dec+"d)'";
				
				//Calculamos el endtime
				cal = Calendar.getInstance();
		        cal.setTimeInMillis(ini.getTime());
		        cal.add(Calendar.SECOND, exp.intValue());
		        end = new Timestamp(cal.getTime().getTime());
				
				insert1 = "INSERT INTO proddatos (prod_id, prog_id, obl_id, det_id, ins_id, mod_id, subm_id, conf_id, bpath_id,  " +
						"  prod_filename, prod_path, prod_filesize, prod_ra, prod_de, prod_initime , prod_endtime"
						+ ", prod_exposure, prod_airmass, prod_observer, prod_object, prod_point";
				insert2 = ") VALUES ('"+prod_id+"','"+prog_id+"','"+obl_id+"',5,'HIP','IMG','IMG','45557',1,'"+filename+"','"+path+"object/','"+filesize+"'"
						+ ",'"+ra+"','"+dec+"','"+ini+"','"+end+"','"+exp+"','"+airmass+"','"+observer+"','"+object+"',"+point+"";
				
				
				insert = insert1 + insert2 + ");";
				System.out.println(insert);

				System.out.println("insert into pred_prod (pred_id, prog_id, obl_id, prod_id) values ('"+predId+"','"+prog_id+"','"+obl_id+"','"+prod_id+"');");
				System.out.println("insert into pred_prod (pred_id, prog_id, obl_id, prod_id) values ('"+predId2+"','"+prog_id+"','"+obl_id+"','"+prod_id+"');");
				//System.out.println("insert into pred_prod (pred_id, prog_id, obl_id, prod_id) values ('"+predId3+"','"+prog_id+"','"+obl_id+"','"+prod_id+"');");


				
				
		*/		
				
				
								
	}
	
	

}
