package svo.gtc.utiles;

import java.io.File;
import svo.varios.utiles.seguridad.Encriptado;

public class FichRedManual {

	public static void main(String[] args) throws Exception {
		
		//Habría que copiar los ficheros en el correspondiente path de cornell PROG/OB/USER/COLECCION
		//POR CADA CASO HABRÍA QUE HACES ESTO:
		Integer predId =30420;//SELECT greatest(0,(select max(pred_id)+1 from prodreducido));
		String filename ="J03463425+2350036.fits";
		String obl = "0025";
		String prod = "292941";
		Double ra =56.70485;
		Double de=23.84961;
		
		
		//Completar esta info para cada caso
		String user = "emanjavacas";
		int col_id = 0;
		String prog = "GTC66-12B";		
		File red= new File("/pcdisk/oort/jmalacid/data/proyectos/GTC/reducidos/emanjavacas/"+filename);	
		//File red2= new File("/pcdisk/oort/jmalacid/data/gtc/reduced/mvillar/"+filename.replaceAll(".fits", "_SUM.fits"));	
		
		
		
		String path = "/"+prog+"/OB"+obl+"/"+user+"/"+col_id+"/";
			
			//pr.insProdDatos(pred);
			
			System.out.println("INSERT INTO prodreducido (pred_id, usr_id, col_id, bpath_id, pred_filename, pred_path, pred_filenameorig, pred_filesize, pred_md5sum, pred_ra, pred_de, pred_type) VALUES "
					+ "('"+predId+"','"+user+"','"+col_id+"',1000,'"+red.getName()+"','"+path+"','"+red.getName()+"','"+new Long(red.length())+"','"+Encriptado.md5(red)+"','"+ra+"','"+de+"','USER');");
			//System.out.println("insert into pred_prod (pred_id, prog_id, obl_id, prod_id) select '"+predId+"',prog_id, obl_id,prod_id from proddatos where prog_id='"+prog+"' and obl_id='"+obl+"' and mty_id='SCI';");

			//System.out.println("insert into pred_prod (pred_id, prog_id, obl_id, prod_id) select '"+predId+"',prog_id, obl_id,prod_id from proddatos where prog_id='"+prog+"' and obl_id='"+obl+"C' and mty_id='SCI';");
			
	/*		System.out.println("INSERT INTO prodreducido (pred_id, usr_id, col_id, bpath_id, pred_filename, pred_path, pred_filenameorig, pred_filesize, pred_md5sum, pred_ra, pred_de, pred_type) VALUES "
					+ "('"+(predId+1)+"','"+user+"','"+col_id+"',1000,'"+red2.getName()+"','"+path+"','"+red2.getName()+"','"+new Long(red.length())+"','"+Encriptado.md5(red2)+"','"+ra+"','"+de+"','USER');");
			System.out.println("insert into pred_prod (pred_id, prog_id, obl_id, prod_id) select '"+(predId+1)+"',prog_id, obl_id,prod_id from proddatos where prog_id='"+prog+"' and obl_id='"+obl+"' and mty_id='SCI';");

			System.out.println("insert into pred_prod (pred_id, prog_id, obl_id, prod_id) select '"+(predId+1)+"',prog_id, obl_id,prod_id from proddatos where prog_id='"+prog+"' and obl_id='"+obl+"C' and mty_id='SCI';");
			*/
			
			
			System.out.println("insert into pred_prod (pred_id, prog_id, obl_id, prod_id) values ('"+predId+"','"+prog+"','"+obl+"','"+prod+"');");
	}

}
