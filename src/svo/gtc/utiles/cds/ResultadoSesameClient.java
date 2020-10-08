package svo.gtc.utiles.cds;


	/**
	 * Definicion de una nueva clase para almacenar los resultados de la b�squeda
	 * en Sesame
	 * 
	 * @author 
	 */
	public class ResultadoSesameClient{
		 protected String onam = null;
		 protected String oty  = null;
		 protected Double ra   = null;
		 protected Double de   = null;
		 
		 protected String[] equivalentNames = null;
		 
		 ResultadoSesameClient(String onam, String oty, 
		 							  Double ra, Double de, String[] equivalentNames){
		 	
		 	this.onam = onam;
		 	this.oty  = oty;
		 	this.ra   = ra;
		 	this.de   = de;
		 	this.equivalentNames = equivalentNames;
		 }

		 public String getOnam(){
		 	return this.onam;
		 }
		 
		 public String getOty(){
		 	return this.oty;
		 }
		 
		 public Double getRa(){
	 		return this.ra;
		 }
		 
		 public Double getDe(){
	 		return this.de;
		 }
		 
		 public String[] getEquivalentNames(){
		 	return this.equivalentNames;
		 }
		 
	}
