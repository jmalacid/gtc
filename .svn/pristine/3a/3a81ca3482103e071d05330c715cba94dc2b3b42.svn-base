package svo.gtc.priv.objetos;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RedInfo {
	
	private String redtype = null;
	private boolean biassub = false;
	private boolean darksub = false;
	private boolean flatfield = false;
	private boolean photometric = false;
	private boolean astrometry = false;
	private String aststatus = null;
	private Double astprecision = null;
	
	//Info de la colección
	private Integer col_id = null;
	private String usr_id = null;

	
	
	public String getRedtype() {
		return redtype;
	}



	public void setRedtype(String redtype) {
		this.redtype = redtype;
	}



	public boolean isBiassub() {
		return biassub;
	}



	public void setBiassub(boolean biassub) {
		this.biassub = biassub;
	}



	public boolean isDarksub() {
		return darksub;
	}



	public void setDarksub(boolean darksub) {
		this.darksub = darksub;
	}



	public boolean isFlatfield() {
		return flatfield;
	}



	public void setFlatfield(boolean flatfield) {
		this.flatfield = flatfield;
	}



	public boolean isPhotometric() {
		return photometric;
	}



	public void setPhotometric(boolean photometric) {
		this.photometric = photometric;
	}



	public boolean isAstrometry() {
		return astrometry;
	}



	public void setAstrometry(boolean astrometry) {
		this.astrometry = astrometry;
	}



	public String getAststatus() {
		return aststatus;
	}



	public void setAststatus(String aststatus) {
		this.aststatus = aststatus;
	}



	public Double getAstprecision() {
		return astprecision;
	}



	public void setAstprecision(Double astprecision) {
		this.astprecision = astprecision;
	}

	public Integer getCol_id() {
		return col_id;
	}

	public void setCol_id(Integer col_id) {
		this.col_id = col_id;
	}

	public String getUsr_id() {
		return usr_id;
	}

	public void setUsr_id(String usr_id) {
		this.usr_id = usr_id;
	}

	public RedInfo(String redtype, boolean biassub, boolean darksub,
			boolean flatfield, boolean photometric, boolean astrometry,
			String aststatus, Double astprecision, Integer col_id, String usr_id) {
		super();
		this.redtype = redtype;
		this.biassub = biassub;
		this.darksub = darksub;
		this.flatfield = flatfield;
		this.photometric = photometric;
		this.astrometry = astrometry;
		this.aststatus = aststatus;
		this.astprecision = astprecision;
		this.col_id = col_id;
		this.usr_id = usr_id;
	}



	public RedInfo (ResultSet resselTemp){
		try {
			redtype = resselTemp.getString("col_redtype").trim();
			biassub = resselTemp.getBoolean("pred_biassub");
			darksub = resselTemp.getBoolean("pred_darksub");
			flatfield = resselTemp.getBoolean("pred_flatfield");
			photometric = resselTemp.getBoolean("pred_photometric");
			astrometry = resselTemp.getBoolean("pred_astrometry");
			try{
				aststatus = resselTemp.getString("pred_aststatus").trim();
			}catch(Exception e){
				aststatus = null;
			}
			astprecision = resselTemp.getDouble("pred_astprecision");
			
			col_id = resselTemp.getInt("col_id");
			usr_id = resselTemp.getString("usr_id");
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
