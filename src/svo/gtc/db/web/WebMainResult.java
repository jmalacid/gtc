package svo.gtc.db.web;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import utiles.Coordenadas;
import utiles.StringUtils;

/**
 *@author Raul Gutierrez Sanchez (raul@cab.inta-csic.es)
*/
//Esta clase hace referencia al acceso a la tabla Object. 


public class WebMainResult{
	//ResultSet 	resSet 		= null;
	//boolean   	hasPerm		= false;
	//Date		periodoProp	= null;
	
	Integer prodId 			=null;
	String 	progId			=null;
	String 	oblId			=null;
	String 	insId			=null;
	String 	modId			=null;
	String 	modShortname	=null;
	String 	modDesc			=null;
	String 	submId			=null;
	Double 	prodRa			=null;
	Double 	prodDe			=null;
	Timestamp prodInitime	=null;
	Timestamp prodPrivate	=null;
	Timestamp prodEndtime	=null;
	Double 	prodExposure	=null;
	Double 	prodAirmass		=null;
	String 	insName			=null;
	String 	fileName		=null;
	//String  bibcode			=null;
	String  object			=null;
	Integer	exptime			=null;
	int 	countWarn		=-1;
	int 	countCal		=-1;
	int 	countAC		=-1;
	int 	countRed		=-1;
	int		countEE			= 0;
	int 	countQC			=-1;
	
	
	//Nuevas columnas
	String progName = null;
	Integer pub = null;
	//int 	countHer		=-1;
	//int 	countMeg		=-1;
	

	public WebMainResult(ResultSet resSet) throws SQLException{

		prodId 	= new Integer(resSet.getInt("prod_id"));
		progId 	= resSet.getString("prog_id").trim();
		oblId 	= resSet.getString("obl_id").trim();
		insId 	= resSet.getString("ins_id").trim();
		modId 	= resSet.getString("mod_id").trim();
		modShortname 	= resSet.getString("mod_shortname").trim();
		modDesc 	= resSet.getString("mod_desc").trim();
		
		submId 	= resSet.getString("subm_id".trim());

		prodRa 	= new Double(resSet.getDouble("prod_ra"));
		if(resSet.wasNull()) prodRa=null;

		prodDe 	= new Double(resSet.getDouble("prod_de"));
		if(resSet.wasNull()) prodDe=null;

		prodInitime	= resSet.getTimestamp("prod_initime");
		if(resSet.wasNull()) prodInitime=null;
		
		prodEndtime	= resSet.getTimestamp("prod_endtime");
		if(resSet.wasNull()) prodEndtime=null;

		prodExposure = new Double(resSet.getDouble("prod_exposure"));
		if(resSet.wasNull()) prodExposure=null;

		prodAirmass = new Double(resSet.getDouble("prod_airmass"));
		if(resSet.wasNull()) prodAirmass=null;

		insName 	= resSet.getString("ins_name".trim());
		

		fileName 	= resSet.getString("prod_filename".trim());
		/*try{
			bibcode 	= resSet.getString("bibcode".trim());
		}catch(Exception e){
			bibcode = null;
		}*/
		
		object 	= resSet.getString("prod_object".trim());
		if(resSet.wasNull()) object = null;
		
		Double expdouble 	= new Double(resSet.getDouble("prod_exposure"));
		if(resSet.wasNull()){
			exptime=null;
		}else{
			exptime = expdouble.intValue();
		}
		
		
		
		try{
			countRed	= resSet.getInt("cuenta_red");
		}catch(Exception e){
			countRed = 0;
		}	
		
		//Nuevas columnas
		/*try{
			countMeg	= resSet.getInt("cuenta_meg");
		}catch(Exception e){
			countMeg = 0;
		}
		try{
			countHer	= resSet.getInt("cuenta_her");
		}catch(Exception e){
			countHer = 0;
		}*/
		try{
			progName 	= resSet.getString("progName".trim());
		}catch(Exception e){
			progName = null;
		}
		try{
			pub	= resSet.getInt("pub");
		}catch(Exception e){
			pub = 0;
		}
}

	
	public Timestamp getProdPrivate() {
		Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(prodInitime.getTime());
	    if(progId.contains("DDT")){
	    	calendar.add(calendar.MONTH, 6);
	    }else{
	    	calendar.add(calendar.MONTH, 12);
	    }
	    Timestamp priv = new Timestamp(calendar.getTimeInMillis());
		
		return priv;
	}


	public void setProdPrivate(Timestamp prodPrivate) {
		this.prodPrivate = prodPrivate;
	}


	public Integer getProdId() {
		return prodId;
	}

	public void setProdId(Integer prodId) {
		this.prodId = prodId;
	}

	public String getProgId() {
		return progId;
	}

	public void setProgId(String progId) {
		this.progId = progId;
	}

	public String getOblId() {
		return oblId;
	}

	public void setOblId(String oblId) {
		this.oblId = oblId;
	}

	public String getInsId() {
		return insId;
	}

	public void setInsId(String insId) {
		this.insId = insId;
	}

	public String getModId() {
		return modId;
	}

	public void setModId(String modId) {
		this.modId = modId;
	}

	public String getSubmId() {
		return submId;
	}

	public void setSubmId(String submId) {
		this.submId = submId;
	}

	public Double getProdRa() {
		return prodRa;
	}

	public void setProdRa(Double prodRa) {
		this.prodRa = prodRa;
	}

	public Double getProdDe() {
		return prodDe;
	}

	public void setProdDe(Double prodDe) {
		this.prodDe = prodDe;
	}

	public Timestamp getProdInitime() {
		return prodInitime;
	}

	public void setProdInitime(Timestamp prodInitime) {
		this.prodInitime = prodInitime;
	}

	public Timestamp getProdEndtime() {
		return prodEndtime;
	}

	public void setProdEndtime(Timestamp prodEndtime) {
		this.prodEndtime = prodEndtime;
	}

	public Double getProdExposure() {
		return prodExposure;
	}

	public void setProdExposure(Double prodExposure) {
		this.prodExposure = prodExposure;
	}

	public Double getProdAirmass() {
		return prodAirmass;
	}

	public void setProdAirmass(Double prodAirmass) {
		this.prodAirmass = prodAirmass;
	}


	public String getInsName() {
		return insName;
	}


	public void setInsName(String insName) {
		this.insName = insName;
	}

	public String getModShortname() {
		return modShortname;
	}


	public void setModShortname(String modShortname) {
		this.modShortname = modShortname;
	}
	
	

	////////////////   VALORES FORMATEADOS   ///////////////


	public String getModDesc() {
		return modDesc;
	}


	public void setModDesc(String modDesc) {
		this.modDesc = modDesc;
	}


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public int getCountWarn() {
		return countWarn;
	}


	public void setCountWarn(int countWarn) {
		this.countWarn = countWarn;
	}


	public int getCountQC() {
		return countQC;
	}


	public void setCountQC(int countQC) {
		this.countQC = countQC;
	}


	public int getCountCal() {
		return countCal;
	}


	public void setCountCal(int countCal) {
		this.countCal = countCal;
	}
	
	public int getCountAC() {
		return countAC;
	}


	public void setCountAC(int countAC) {
		this.countAC = countAC;
	}

	
	/*
	public String getBibcode() {
		return bibcode;
	}


	public void setBibcode(String bibcode) {
		this.bibcode = bibcode;
	}
*/

	public int getCountRed() {
		return countRed;
	}


	public void setCountRed(int countRed) {
		this.countRed = countRed;
	}

	public int getCountEE() {
		return countEE;
	}


	public void setCountEE(int countEE) {
		this.countEE = countEE;
	}

	public String getObject() {
		return object;
	}


	public void setObject(String object) {
		this.object = object;
	}


	public Integer getExptime() {
		return exptime;
	}


	public void setExptime(Integer exptime) {
		this.exptime = exptime;
	}


	public String getFormatedProdRa() {
		if(prodRa==null) return "";
		return StringUtils.formateaNumero(prodRa, "0.00000", 0);
	}

	public String getFormatedProdDe() {
		if(prodDe==null) return "";
		return StringUtils.formateaNumero(prodDe, "0.00000", 0);
	}
	
	public String getSexaProdRa() {
		if(prodRa==null) return "";
		String salida = null;
		String[] coor = Coordenadas.double2hms(prodRa);
		salida = coor[0]+":"+coor[1]+":"+StringUtils.formateaNumero(Double.valueOf(coor[2]), "0.00", 0);
		return salida;
	}

	public String getSexaProdDe() {
		if(prodDe==null) return "";
		String salida = null;
		String[] coor = Coordenadas.double2gms(prodDe);
		salida = coor[0]+":"+coor[1]+":"+StringUtils.formateaNumero(Double.valueOf(coor[2]), "0.0", 0);;
		return salida;
	}

	public String getFormatedProdInitime() {
		if(prodInitime==null) return "";
		return prodInitime.toString().substring(0, 21);
	}

	public String getFormatedProdEndtime() {
		if(prodEndtime==null) return "";
		return prodEndtime.toString().substring(0, 21);
	}

	public String getFormatedProdExposure() {
		if(prodExposure==null) return "";
		return StringUtils.formateaNumero(prodExposure, "0.0", 0);
	}

	public String getFormatedProdAirmass() {
		if(prodAirmass==null) return "";
		return StringUtils.formateaNumero(prodAirmass, "0.00", 0);
	}


	public String getProgName() {
		return progName;
	}


	public void setProgName(String progName) {
		this.progName = progName;
	}


	public Integer getPub() {
		return pub;
	}


	public void setPub(Integer pub) {
		this.pub = pub;
	}


	/*public int getCountHer() {
		return countHer;
	}


	public void setCountHer(int countHer) {
		this.countHer = countHer;
	}


	public int getCountMeg() {
		return countMeg;
	}


	public void setCountMeg(int countMeg) {
		this.countMeg = countMeg;
	}*/



	
}