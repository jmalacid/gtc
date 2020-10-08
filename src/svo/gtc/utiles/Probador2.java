package svo.gtc.utiles;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Data;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
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

public class Probador2 {
	
	public static void main(String args[]) throws IOException, ConfigException, GtcFileException, SQLException, FitsException{
		
		 Timestamp prueba = Timestamp.valueOf("2016-02-01 21:30:06.835");
		 
		 Timestamp hoy = new Timestamp(System.currentTimeMillis());
		 
		System.out.println("hoy: "+hoy+", "+prueba);
		
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(prueba.getTime());
        cal.add(Calendar.SECOND, (int)3.0000);
        Timestamp later = new Timestamp(cal.getTime().getTime());
        System.out.println(later);
		
		//Método para obtener los valores RAMAX,RMIN,DECMAX,DECMIN
		//DriverBD drive = new  DriverBD();
		//Connection con = drive.bdConexion();

		//DBPrivate union = new DBPrivate(con);

		//String[] progs = union.fileError("E-OSIRIS-0015");
		//con.close();
		
	//	for(int j=1; j<5;j++){
		//Obtenemos todos los ficheros de un programa como ejemplo
		/*try{
			
			String obl_id = null;
			if(j<10){
				obl_id= "000"+j;
		
			}else{
				obl_id= "00"+j;
			}
			File dir= new File("/pcdisk/oort6/raul/gtc/data/2014B/GTC83-14B/OB"+obl_id+"/object");
		File[] progs = dir.listFiles();
		
		
		for(int i=0; i<progs.length;i++){
			
			//Comprobamos el nombre
			if(progs[i].contains("object") && (progs[i].indexOf("LongSlitSpectroscopy")>0 || progs[i].indexOf("BroadBand")>0)){
				System.out.println(progs[i]);
			}else{
				//System.out.println(progs[i]);
			}
		
			
			try{

			boolean compressed = false;
			
			if(progs[i].getName().toUpperCase().endsWith(".GZ")){
				compressed = true;
			}
		    */
	/*	File dir = new File("/pcdisk/oort/jmalacid/data/gtc/reduced/pending/mgarcia/27381.fits");
			
			Fits fEntrada = new Fits(dir,false);	    
		    BasicHDU hdu1 = fEntrada.getHDU(0);  
		    //BasicHDU hdu2 = fEntrada.getHDU(2);
		    Header header1=hdu1.getHeader();
		    //Header header2=hdu2.getHeader();
		    
		    Long num = hdu1.getSize();
		    
		    System.out.println("tamaño: "+num);
		    
		    Double max = hdu1.getMaximumValue();
		    Double min = hdu1.getMinimumValue();
		    
		    Data dat1 =hdu1.getData();
		    
		   
		    
		    System.out.println("max: "+max+", min: "+min+"\n size= ");
		    */
		    
			
		   /* String prueba = header1.findCard("HIERARCH OBJECT_TYPE").getValue();
		    
		    System.out.println("prueba: "+prueba);
		    
		    String datasec = header1.findCard("DATASEC").getValue();
		    String[] ds = datasec.split(",");
		    Double datasec1 = Double.valueOf(ds[0].split(":")[1]);
		    Double datasec2 = Double.valueOf(ds[1].split(":")[1].replaceAll("]", ""));
		    
		    System.out.println("datasec1: "+datasec1+", datasec2: "+datasec2);
		    
		    Double crval1_1 = new Double(header1.findCard("CRVAL1").getValue());
		    Double crval2_1 = new Double(header1.findCard("CRVAL2").getValue());
		    Double crpix1_1 = new Double(header1.findCard("CRPIX1").getValue());
		    Double crpix2_1 = new Double(header1.findCard("CRPIX2").getValue());
		    Double cd1_1 = new Double(header1.findCard("CD1_1").getValue());
		    Double cd2_1 = new Double(header1.findCard("CD2_2").getValue());
		    
		    Double crval1_2 = new Double(header2.findCard("CRVAL1").getValue());
		    Double crval2_2 = new Double(header2.findCard("CRVAL2").getValue());
		    Double crpix1_2 = new Double(header2.findCard("CRPIX1").getValue());
		    Double crpix2_2 = new Double(header2.findCard("CRPIX2").getValue());
		    Double cd1_2 = new Double(header2.findCard("CD1_1").getValue());
		    Double cd2_2 = new Double(header2.findCard("CD2_2").getValue());
		    
		    //Obtenemos los valores
		    Double rmax1 = crval1_1+(0-crpix1_1)*cd1_1;
		    Double rmin1 = crval1_1+(datasec1-crpix1_1)*cd1_1;
		    Double decmax1 = crval2_1+(datasec2-crpix2_1)*cd2_1;
		    Double decmin1 = crval2_1+(0-crpix2_1)*cd2_1;
		    		
		    Double rmax2 = crval1_2+(0-crpix1_2)*cd1_2;
		    Double rmin2 = crval1_2+(2098-crpix1_2)*cd1_2;
		    Double decmax2 = crval2_2+(4102-crpix2_2)*cd2_2;
		    Double decmin2 = crval2_2+(0-crpix2_2)*cd2_2;
		    
		    Integer prod_id = new Integer(progs[i].getName().substring(0, progs[i].getName().indexOf("-")));*/
		    
		   // System.out.println("GTC83-14B , OB"+obl_id+" , "+prod_id +" , " +rmax1+" , "+rmin1+" , "+decmax1+" , "+decmin1+" , "+rmax2+" , "+rmin2+" , "+decmax2+" , "+decmin2+" , http://gtc.sdc.cab.inta-csic.es/gtc/jsp/viewprodheader.jsp?prod_id=GTC83-14B.."+obl_id+".."+prod_id);
		    
			/*}catch (Exception e1){
				e1.printStackTrace();
			}
		}
			}catch (Exception e){
				
				e.printStackTrace();
			}*/
		//}
		
	}
		

}
