package svo.sepub.buscador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import svo.sepub.db.AccessDB;
import svo.gtc.db.DriverBD;
import svo.sepub.results.Bibcode;

public class Buscador {

	private String words = null;
	private String bibcodes ="";
	private String bib_ok="";
	private String bib_no="";
	
	private String union ="";
	private String union2="";
	Connection conex = null;
	AccessDB adb = null;
	private Integer total = 0;
	
	
	public Buscador(String proyecto) throws Exception {
		super();

		//Abrimos la BD
		abreBD();
		try {
			//Obtenemos las palabras relacionadas con el proyecto
			words(proyecto);
			//System.out.println("tenemos las palabras: "+words);
			
			//Obtenemos los bibcodes
			boolean total = true;
			Integer page = 1;
				
			while(total){
				//System.out.println("Entramos en la página "+page);
				total = bibcodesfromURL(page);
				page = page +1;
			}
			
			//Estudiamos los bibcodes uno a uno
			String[] bibs = bibcodes.split(";");
			union="";
			union2="";
			
			for(int i=0; i<bibs.length;i++){
				//System.out.println(bibs[i]);
				viewBibs(bibs[i], proyecto);
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
		//Cerramos la BD
		cierraBD();	
		
	}
	
	public void abreBD() throws SQLException{
		DriverBD DDB = new DriverBD();
		conex = DDB.bdConexion();
		adb = new AccessDB(conex);
		//System.out.println("conex: "+conex);
	}
	

	public void words(String proyecto) throws SQLException {
		words = adb.viewWords(proyecto);
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
	
	public boolean bibcodesfromURL(Integer page) throws IOException, MalformedURLException {
		
		boolean seguir = true;
		Integer num = 1;
		
		//URL pagina = new URL("http://labs.adsabs.harvard.edu/adsabs/search/?q=full%3A"+words+"&db_f=astronomy&bib_f=" +
		URL pagina = new URL("https://ui.adsabs.harvard.edu/#search/?q=full%3A"+words+"&db_f=astronomy&bib_f=" +
				"(\"MNRAS\"+OR+\"ApJ\"+OR+\"A%26A\"+OR+\"AJ\"+OR+\"ApJS\"+OR+\"PASP\"+OR+\"Natur\"+OR+\"Sci\"+OR+\"ApJL\")&page="+page);
		//URL pagina = new URL("http://labs.adsabs.harvard.edu/fulltext/catalog?per_page=200&page="+page+"&q="+words+"&commit=search&bibstem=MNRAS+ApJ+A%26A+AJ+ApJS+PASP+Natur+Sci+ApJL");
		BufferedReader in = new BufferedReader(new InputStreamReader(pagina.openStream()));
		String entrada;
		while ((entrada = in.readLine()) != null){
			System.out.println(entrada);
			//if(entrada.contains("http://labs.adsabs.harvard.edu/ui/abs/")){
			 if(entrada.contains("<div class='span3 bibcode'><a href=\"/adsabs/abs/")){
				String valor = entrada.replaceAll("\\n","").replaceAll("<div class='span3 bibcode'><a href=\"/adsabs/abs/([0-9]{4}[^//]*)/\">.*", "$1").trim();
			    //System.out.println("bibcode ("+num+"): "+valor.replaceAll("%26", "&"));
			    bibcodes =  bibcodes.replaceAll("%26", "&") + union + valor;
			    union=";";
			    num = num + 1;
			    
		    }
		    	
		}
		in.close();

		if(num<20){
			//No es necesario ir a la siguiente página
			seguir = false;
			//System.out.println("false");
		}
		
		//Sumamos al total:
		total = total + num -1;
		//System.out.println(bibcodes);
		return seguir;
	}

	
	public void setWords(String words) {
		this.words = words;
	}
	
	public String getWords() {
		return words;
	}

	public String getBibcodes() {
		return bibcodes;
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

	public String getUnion() {
		return union;
	}

	public void setUnion(String union) {
		this.union = union;
	}

	public Connection getConex() {
		return conex;
	}

	public void setConex(Connection conex) {
		this.conex = conex;
	}

	public AccessDB getAdb() {
		return adb;
	}

	public void setAdb(AccessDB adb) {
		this.adb = adb;
	}

	public String getUnion2() {
		return union2;
	}

	public void setUnion2(String union2) {
		this.union2 = union2;
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
	
	public void cierraBD() throws SQLException{
		conex.close();
	}
}
