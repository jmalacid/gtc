package svo.gtc.utiles;

import java.io.IOException;
import java.io.OutputStream;

public class Descargar {

	public static void descargar(OutputStream outs, String log) throws IOException{

		// Meter el texto al fichero
		try{

			outs.write(log.getBytes());

		}catch(IOException e){
			try {
				outs.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			throw e;
		}

		try {
			outs.flush();
			outs.close();
		} catch (IOException e) {
			throw e;
		}
	} 
	
}
