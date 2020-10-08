package svo.gtc.utiles;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjMegara {

	private String PROG_ID = null;
	private String OBL_ID = null;
	private String PATH_DIR = null;
	private String FILENAME = null;
	private String INSMODE = null;
	private String VPH = null;
	private String BLCKUUID = null;
	private Double SENTEMP4 = null;
	private Double SPECLAMP = null;
	public String getPROG_ID() {
		return PROG_ID;
	}
	public void setPROG_ID(String pROG_ID) {
		PROG_ID = pROG_ID;
	}
	public String getOBL_ID() {
		return OBL_ID;
	}
	public void setOBL_ID(String oBL_ID) {
		OBL_ID = oBL_ID;
	}
	public String getPATH_DIR() {
		return PATH_DIR;
	}
	public void setPATH_DIR(String pATH_DIR) {
		PATH_DIR = pATH_DIR;
	}
	public String getFILENAME() {
		return FILENAME;
	}
	public void setFILENAME(String fILENAME) {
		FILENAME = fILENAME;
	}
	public String getINSMODE() {
		return INSMODE;
	}
	public void setINSMODE(String iNSMODE) {
		INSMODE = iNSMODE;
	}
	public String getVPH() {
		return VPH;
	}
	public void setVPH(String vPH) {
		VPH = vPH;
	}
	public String getBLCKUUID() {
		return BLCKUUID;
	}
	public void setBLCKUUID(String bLCKUUID) {
		BLCKUUID = bLCKUUID;
	}
	public Double getSENTEMP4() {
		return SENTEMP4;
	}
	public void setSENTEMP4(Double sENTEMP4) {
		SENTEMP4 = sENTEMP4;
	}
	public Double getSPECLAMP() {
		return SPECLAMP;
	}
	public void setSPECLAMP(Double sPECLAMP) {
		SPECLAMP = sPECLAMP;
	}
	
	public ObjMegara(String pROG_ID, String oBL_ID, String pATH_DIR, String fILENAME, String iNSMODE, String vPH,
			String bLCKUUID, Double sENTEMP4, Double sPECLAMP) {
		super();
		PROG_ID = pROG_ID;
		OBL_ID = oBL_ID;
		PATH_DIR = pATH_DIR;
		FILENAME = fILENAME;
		INSMODE = iNSMODE;
		VPH = vPH;
		BLCKUUID = bLCKUUID;
		SENTEMP4 = sENTEMP4;
		SPECLAMP = sPECLAMP;
	}
	public ObjMegara(ResultSet resset) throws SQLException{
		this.VPH = resset.getString("vph");
		this.INSMODE = resset.getString("insmode");
		this.BLCKUUID = resset.getString("blckuuid");
		this.PROG_ID = resset.getString("prog_id");
		this.OBL_ID = resset.getString("obl_id");
	}
	
}
