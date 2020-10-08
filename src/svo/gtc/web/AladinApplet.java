package svo.gtc.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AladinApplet {

		// CDS (Strasbourg - France)
		static String mirror = "http://aladin.u-strasbg.fr/java/nph-aladin.pl?script=";
		// UKDC (Cambridge - UK)
		//static String mirror = "http://archive.ast.cam.ac.uk/viz-bin/nph-aladin.pl?script=";
		// CFA (Harvard - USA)
		//static String mirror = "http://vizier.cfa.harvard.edu/viz-bin/nph-aladin.pl?script=";
		// ADAC (Tokio - Japan)
		//static String mirror = "http://vizier.nao.ac.jp/viz-bin/nph-aladin.pl?script=";
		// IUCAA (Pune - India)
		//static String mirror = "http://vizier.iucaa.ernet.in/viz-bin/nph-aladin.pl?script=";
		// CADC (Victoria - Canada)
		//static String mirror = "http://vizier.hia.nrc.ca/viz-bin/nph-aladin.pl?script=";
		
		public static String getAladinAppletURL(String[] imageURL) {
			String url = "";
			String script = "reticle off;";
			for (int i=0; i<imageURL.length; i++) {
				script += "get Local("+imageURL[i]+");";
			}
			try { 
				url = mirror+ URLEncoder.encode(script, "UTF-8");
				//System.out.println(url);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return url;
		}
		public static String getAladinAppletURL(String imageURL) {
			String url = "";
			String script = "reticle off;";
			script += "get Local("+imageURL+");";
			
			try {
				url = mirror+ URLEncoder.encode(script, "UTF-8");
				//System.out.println(url);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return url;
		}


	
	
	
}
