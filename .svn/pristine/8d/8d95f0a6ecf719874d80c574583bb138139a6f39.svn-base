package svo.gtc.siap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import utiles.StringUtils;

/**
 *@author J. Manuel Alacid (jmalacid@cab.inta-csic.es)
*/
//Esta clase hace referencia al acceso a la tabla Object. 


public class ResultSiap{
	
	Integer predId 			=null;
	String 	insId			=null;
	Double 	predRa			=null;
	Double 	predDe			=null;
	String 	insName			=null;
	Double 	fileSize		=null;

	public ResultSiap(ResultSet resSet) throws SQLException{

		predId 	= new Integer(resSet.getInt("pred_id"));
		insId 	= resSet.getString("ins_id").trim();

		predRa 	= new Double(resSet.getDouble("pred_ra"));
		if(resSet.wasNull()) predRa=null;

		predDe 	= new Double(resSet.getDouble("pred_de"));
		if(resSet.wasNull()) predDe=null;

		insName 	= resSet.getString("ins_id".trim());

		fileSize 	= resSet.getDouble("pred_filesize");
		
}



	public Integer getPredId() {
		return predId;
	}



	public void setPredId(Integer predId) {
		this.predId = predId;
	}



	public String getInsId() {
		return insId;
	}



	public void setInsId(String insId) {
		this.insId = insId;
	}



	
	public Double getPredRa() {
		return predRa;
	}



	public void setPredRa(Double predRa) {
		this.predRa = predRa;
	}



	public Double getPredDe() {
		return predDe;
	}



	public void setPredDe(Double predDe) {
		this.predDe = predDe;
	}



	public String getInsName() {
		return insName;
	}



	public void setInsName(String insName) {
		this.insName = insName;
	}

	public String getFormatedPredRa() {
		if(predRa==null) return "";
		return StringUtils.formateaNumero(predRa, "0.00000", 0);
	}

	public String getFormatedPredDe() {
		if(predDe==null) return "";
		return StringUtils.formateaNumero(predDe, "0.00000", 0);
	}



	public Double getFileSize() {
		return fileSize;
	}



	public void setFileSize(Double fileSize) {
		this.fileSize = fileSize;
	}

}