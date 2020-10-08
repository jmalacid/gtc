package svo.gtc.utiles.cds;

	/**
	 * Definicion de un nuevo tipo de excepcion que se lanza cuando, por alguna
	 * razon, SIMBAD no devuelve nombres equivalentes y sin embargo no avisa de
	 * ello con el correspondiente " <INFO>*** This identifier is not present in
	 * the database".
	 * 
	 * @author Ra�l Guti�rrez S�nchez
	 *  
	 */
	public class SesameResolverException extends Exception {
		SesameResolverException() {
		}

		SesameResolverException(String msg) {
			super(msg);
		}
	}
