package svo.gtc.priv.objetos;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Vector;

import org.apache.log4j.Logger;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import svo.gtc.db.DriverBD;
import svo.gtc.db.priv.DBPrivate;

public class TempRed {

	static String path = "/export/data-gtc/reduced/pending";
	//static String path = "/pcdisk/oort2/jmalacid/data/proyectos/GTC/reducidos";

	RedObj[] ficheros_ok = null;
	RedObj[] ficheros_no = null;

	static Logger logger = Logger.getLogger("svo.gtc");

	public TempRed() throws SQLException {

		// Extraemos todos los ficheros que tenemos en pending
		File dir = new File(path);
		File[] ficheros = extraeFits(dir);

		Connection con = null;

		try {
			// Abrimos la BD
			DriverBD drive = new DriverBD();
			con = drive.bdConexion();

			DBPrivate union = new DBPrivate(con);

			Vector<RedObj> aux = new Vector<RedObj>();
			Vector<RedObj> aux_no = new Vector<RedObj>();
			// Para cada fichero vemos si está en la base de datos
			for (int i = 0; i < ficheros.length; i++) {

				// Buscamos los valores en su header
				RedObj ro = rellenaCampos(ficheros[i]);

				// Buscamos su OpenTime en la BD
				Integer ok = union.tempRed(ro);

				if (ok == 0) {
					ro.setMensaje("No se ha encontrado");
					logger.info("No se ha encontrado el correspondiente valor");
					aux_no.add(ro);
				} else if (ok > 1) {
					ro.setMensaje("Encontrado más de un valor");
					logger.info("Encontramos más de un valor");
					aux.add(ro);
				} else {
					ro.setMensaje("Valor encontrado");
					// guardamos el valor
					logger.info("Sí hay correspondencia");
					aux.add(ro);
				}

			}

			ficheros_ok = (RedObj[]) aux.toArray(new RedObj[0]);
			ficheros_no = (RedObj[]) aux_no.toArray(new RedObj[0]);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
	}

	public static RedObj rellenaCampos(File fich) throws IOException,
			FitsException {

		boolean compressed = false;

		String prog = null;
		String obl = null;
		String date = null;
		String openTime = null;
		String closeTime = null;
		String tipo = null;


		if (fich.getName().toUpperCase().endsWith(".GZ")) {
			compressed = true;
		}

		Fits fEntrada = new Fits(fich, compressed);

		BasicHDU hdu = null;
		while ((hdu = fEntrada.readHDU()) != null) {

			Header header = hdu.getHeader();

			// PROGID
			try {
				if (prog == null)
					prog = header.findCard("GTCPRGID").getValue();
			} catch (NullPointerException e) {
			}

			if (prog == null) {
				try {
					prog = header.findCard("GTCPROGI").getValue();
				} catch (NullPointerException e) {
				}
			}

			// OBLOCK
			try {
				if (obl == null)
					obl = header.findCard("GTCOBID").getValue();
				String aux = obl.replaceAll("_", "-");
				if (aux.indexOf("-") != aux.lastIndexOf("-")) {
					obl = aux.substring(aux.lastIndexOf("-") + 1);
				}
			} catch (NullPointerException e) {
			}

			
			//Obtenemos date de DATE-OBS o DATE
			try{
				date = header.findCard("DATE-OBS").getValue();
			}catch(Exception e){
				try{
					date = header.findCard("DATE").getValue();
				}catch(Exception e2){
					
				}
			}
			
			// Podremos encontrar OPENTIME o UTSTART según el instrumento
			try {
				openTime = header.findCard("OPENTIME").getValue();
				tipo = "otro";
			} catch (NullPointerException e1) {
				try{
					openTime = header.findCard("UTSTART").getValue();
					tipo="S";
				}catch (Exception e2){
					logger.info("error, no hay opentime");
				}
			}

			// Podremos encontrar CLOSTIME o UTEND según el instrumento
			try {
				closeTime = header.findCard("CLOSTIME").getValue();
				tipo="otro";
			} catch (NullPointerException e1) {
				try {
					closeTime = header.findCard("UTEND").getValue();
					tipo="S";
				} catch (Exception e2) {
					logger.info("error, no hay closetime");
				}
			}

			try {
				if(closeTime == null || closeTime.trim().length() ==0){
					openTime = header.findCard("READTIME").getValue();
				}
			} catch (NullPointerException e1) {
			}
		}

		fEntrada.getStream().close();

		RedObj red = new RedObj(fich, prog, obl, date, openTime, closeTime, tipo);

		return red;

	}

	/**
	 * Entra recursivamente en un directorio y obtiene todos los ficheros .FITS
	 * que encuentra.
	 * 
	 * @param dir
	 * @return
	 */
	public static File[] extraeFits(File dir) {

		Vector<File> aux = new Vector<File>();

		if (!dir.isDirectory())
			return new File[0];

		File[] ficheros = dir.listFiles();
		for (int i = 0; i < ficheros.length; i++) {
			if (ficheros[i].isFile()
					&& (ficheros[i].getName().toUpperCase().endsWith(".FITS") || ficheros[i]
							.getName().toUpperCase().endsWith(".FIT"))) {
				aux.add(ficheros[i]);
			} else if (ficheros[i].isDirectory()) {
				File[] ficherosAux = extraeFits(ficheros[i]);
				for (int j = 0; j < ficherosAux.length; j++) {
					aux.add(ficherosAux[j]);
				}
			}
		}

		return (File[]) aux.toArray(new File[0]);
	}

	public RedObj[] getFicheros_ok() {
		return ficheros_ok;
	}

	public void setFicheros_ok(RedObj[] ficheros_ok) {
		this.ficheros_ok = ficheros_ok;
	}

	public RedObj[] getFicheros_no() {
		return ficheros_no;
	}

	public void setFicheros_no(RedObj[] ficheros_no) {
		this.ficheros_no = ficheros_no;
	}

}