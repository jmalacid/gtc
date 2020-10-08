package svo.gtc.ads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class TestADS {
	
	String bibcode = null;
	
	public TestADS(String bibcode) throws Exception {
		super();
		
		this.bibcode= bibcode;

		
		
	}

	public boolean bibcodeState() throws IOException, MalformedURLException {
		
		boolean state = false;
		
		URL pagina = new URL("http://adsabs.harvard.edu/cgi-bin/nph-data_query?bibcode="+bibcode.replaceAll("&", "%26")+"&link_type=DATA&db_key=AST&high=");
		BufferedReader in = new BufferedReader(new InputStreamReader(pagina.openStream()));
		
		
		
		String entrada;
		
		while ((entrada = in.readLine()) != null){
			 if(entrada.contains("gtc.sdc.cab.inta-csic.es") || entrada.contains("/gtc/images/cabGTC.png")|| entrada.contains("GTC Archive")){
				state = true;
			    break;
		    }
		    	
		}
		in.close();

		return state;
	}

	

}
