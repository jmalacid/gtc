/*
 * @(#)NewCollection.java    30/08/2012
 *
 *
 * Raúl Gutiérrez Sánchez. (raul@laeff.inta.es)	
 * LAEFF: 	Laboratorio de Astrofísica Espacial y Física Fundamental.
 *        	INTA
 *			Villafranca del Castillo Satellite Tracking Station
 *			Villafranca del Castillo, E-28691 Villanueva de la Cañada
 *			Madrid - España
 *			
 * Phone:  34 91-8131260
 * Fax..:  34 91-8131160   
 * -----------------------------------------------------------------------
 * 
 *	ESTE  PROGRAMA  ES  DE  USO EXCLUSIVO.  ES  PROPIEDAD  DE  SU AUTOR  Raúl 
 *  Gutiérrez Sánchez  Y SE PRESENTA "COMO ESTA" SIN NINGUN TIPO DE GARANTIAS
 *  DENTRO Y FUERA  DE LA FINALIDAD PARA  LA QUE EL  AUTOR LO  HIZO. EL AUTOR 
 *  PERMITE  SU USO SIEMPRE QUE  SE LE REFERENCIE. SE PERMITE LA MODIFICACION
 *  DE SU  FUENTE SIEMPRE QUE SE  HAGA REFERENCIA A SU AUTORIA Y JUNTO A ELLA
 *  SE  ADJUNTE LA IDENTIFICACION DEL  AUTOR DE LAS NUEVAS MODIFICACIONES. EL
 *  AUTOR  NUNCA SERA RESPONSABLE DEL USO DE ESTE  PROGRAMA NI DE LOS EFECTOS
 *  QUE ESTE PROGRAMA PUDIEDA CAUSAR EN DETERIOROS, ALTERACIONES Y/O PERDIDAS 
 *  DE INFORMACION.
 *	
 */

package svo.gtc.struts.reduced.model;

public class NewCollection {
	
	public static String SPECTRUM="SPEC";
	public static String IMAGE="IMG";
	
	private String name;
	private String desc;
	private String bibcode;
	private String dataType;
	private String[] redTypeImg;
	private String redTypeImgOther;
	private String[] redTypeSpec; 
	private String redTypeSpecOther;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getBibcode() {
		return bibcode;
	}
	
	public void setBibcode(String bibcode) {
		this.bibcode = bibcode;
	}
	
	public String getDataType() {
		return dataType;
	}
	
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String[] getRedTypeImg() {
		return redTypeImg;
	}
	public void setRedTypeImg(String[] redTypeImg) {
		setDataType(IMAGE);
		this.redTypeImg = redTypeImg;
	}
	
	public String[] getRedTypeSpec() {
		return redTypeSpec;
	}
	
	public void setRedTypeSpec(String[] redTypeSpec) {
		setDataType(SPECTRUM);
		this.redTypeSpec = redTypeSpec;
	}
	public String getRedTypeImgOther() {
		return redTypeImgOther;
	}
	public void setRedTypeImgOther(String redTypeImgOther) {
		setDataType(IMAGE);
		this.redTypeImgOther = redTypeImgOther;
	}
	public String getRedTypeSpecOther() {
		return redTypeSpecOther;
	}
	public void setRedTypeSpecOther(String redTypeSpecOther) {
		setDataType(SPECTRUM);
		this.redTypeSpecOther = redTypeSpecOther;
	}
	
	
	/**
	 * Devuelve un array con todos los tipos de reducción especificados, incluyendo
	 * el campo "other".
	 * @return
	 */
	public String[] getRedType(){
		String[] salida = new String[0];
		if(this.redTypeImg.length>0){
			salida=redTypeImg;
		}else if(this.redTypeSpec.length>0){
			salida=redTypeSpec;
		}
		
		if(this.redTypeImgOther.trim().length()>0){
			String[] aux = new String[salida.length+1];
			System.arraycopy(salida, 0, aux, 0, salida.length);
			salida=aux;
			salida[salida.length-1]=this.redTypeImgOther.trim();
		}else if(this.redTypeSpecOther.trim().length()>0){
			String[] aux = new String[salida.length+1];
			System.arraycopy(salida, 0, aux, 0, salida.length);
			salida=aux;
			salida[salida.length-1]=this.redTypeSpecOther.trim();
		}
		
		return salida;
		
	}
	
	
	
	
	

}


