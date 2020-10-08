package svo.sepub.buscador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;

import svo.gtc.db.DriverBD;
import svo.sepub.db.AccessDB;

public class Buscador2 {

	String result = null;
	private String bibcodes ="";
	private String bib_ok="";
	private String bib_no="";
	
	private String union ="";
	private String union2="";
	Connection conex = null;
	AccessDB adb = null;
	Integer total = 0;
	
	//proyecto siempre es gtc por ahora
	String proyecto = "gtc";
	
	public Buscador2() throws Exception {
		super();

	
			
		Context initContext;
		Connection con = null;

		
		try {
			
			initContext = new InitialContext();
			DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
			con = ds.getConnection();
			this.adb = new AccessDB(con);
			
			//Obtenemos el resultado
			this.result = getJSon();

			//Creamos un Objeto JSON
			JSONObject objjson = new JSONObject(result);
			
			//Creamos un Objeto JSON con la parte dentro de response
			JSONObject objres = new JSONObject(objjson.getString("response"));
			
			JSONArray jsonArray = new JSONArray(objres.getString("docs"));
	 
			for(int i=0 ; i< jsonArray.length(); i++){   // Entramos en todos los resultados 

				JSONObject jsonObject = jsonArray.getJSONObject(i);  // get jsonObject @ i position 
				
				String bib = jsonObject.getString("bibcode");
			
				if(bib.contains("MNRAS") || bib.contains("ApJ")|| bib.contains("A&A")|| bib.contains("AJ")|| bib.contains("PASP")|| bib.contains("Natur")|| bib.contains("Sci")){
					//Estudiamos los bibcodes
					viewBibs(bib, proyecto);
					total += 1;
				}
			}
			
			/*//Obtenemos los bibcodes	
			getBibcodes();
			
			//Estudiamos los bibcodes uno a uno
			String[] bibs = bibcodes.split(";");
			union="";
			union2="";
			
			for(int i=0; i<bibs.length;i++){
				//System.out.println(bibs[i]);
				viewBibs(bibs[i], proyecto);
			}*/
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception ex) {
					System.out.println("Error: " + ex.getMessage());
				}
			}
		}
		
	}
	
	public void abreBD() throws SQLException{
		DriverBD DDB = new DriverBD();
		conex = DDB.bdConexion();
		adb = new AccessDB(conex);
		//System.out.println("conex: "+conex);
	}
	
	public void cierraBD() throws SQLException{
		conex.close();
	}
	
	public void getBibcodes(){
		
		String[] temp1 = result.split("bibcode");
    	
		
		for(int i=0;i<temp1.length;i++){
			//System.out.println(temp1[i]);
			String valor = temp1[i].replaceAll("\\n","").replaceAll(":\"([0-9]{4}[^//]*)\"}.*", "$1").trim();
		    
		    String bib = valor.replaceAll("\"","").replaceAll("%26", "&").trim();
			//System.out.println("bib: "+bib);
			if ( Character.isDigit(bib.charAt(0)) ){
				
				//Comprobamos que es de uno de los journals que buscamos 
				if(bib.contains("MNRAS") || bib.contains("ApJ")|| bib.contains("A&A")|| bib.contains("AJ")|| bib.contains("PASP")|| bib.contains("Natur")|| bib.contains("Sci")){
					//System.out.println("bibcode ("+total+1+"): "+bib);
					bibcodes =  bibcodes + union + bib;
					union=";";
					total += 1;
				}
			}
		    
		}
		

	}
	
	public void viewBibs(String bib, String proyecto) throws SQLException{
		
		Integer hay = adb.existeBibBus(bib, proyecto);
		Integer hayBib = adb.existeBib(bib);
		
		if(hay>0){
			//System.out.println("revisión "+bib+" ya existe");
			bib_no=bib_no+union+bib;
			union=";";
		}else{
			//System.out.println("revisión "+bib+" no existe");
			if(!(hayBib>0)){
				adb.addBib(bib);
			}
			adb.addBus(bib, proyecto);
			bib_ok=bib_ok+union2+bib;
			union2=";";
		}
				
	}

	public String getJSon() throws MalformedURLException, IOException{
		
		String url="https://api.adsabs.harvard.edu/v1/search/query?"
				+ "q=full%3A%22gran+telescopio+canarias%22or%22gran+telescopio+de+canarias%22or%22gtc%22&database=astronomy&sort=date+desc&rows=1000&fl=bibcode";
		
		
		URLConnection conn = new URL(url).openConnection();
	   	conn.setRequestProperty("Authorization", "Bearer:WG8qpt5jwF4x1zX0LE4FCSbQjh7QOYz5TUHk7fHg");
	   	conn.connect();
	   	 
	   	BufferedReader br= new BufferedReader(new InputStreamReader(conn.getInputStream()));
	   	
	   	String linea="";
	    String line;
	   
	    while ((line = br.readLine()) != null) {
            //System.out.println(line);
            linea +=line;
        }
	   	 
	   	 br.close();
	   	 return linea; 	 
	}
	
	public String getBib_ok() {
		return bib_ok;
	}

	public void setBib_ok(String bib_ok) {
		this.bib_ok = bib_ok;
	}

	public String getBib_no() {
		return bib_no;
	}

	public void setBib_no(String bib_no) {
		this.bib_no = bib_no;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public void setBibcodes(String bibcodes) {
		this.bibcodes = bibcodes;
	}
	
	
}
