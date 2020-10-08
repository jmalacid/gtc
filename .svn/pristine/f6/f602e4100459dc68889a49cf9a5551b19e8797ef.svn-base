package svo.gtc.db.object;

import java.sql.SQLException;
import java.sql.ResultSet;

/**
 * 
 * Objeto que representa un registro de la tabla Name.
 *  
 * @author avelasco
 * @version 0.0, 2008-02-20
 *
 */

//Esta clase hace referencia únicamente a la tabla Name.

//Esta clase define la estructura o el esqueleto de la tabla Name, con cada uno de sus campos.
//Al método constructor se le pasan como argumentos los campos de los que consta esta tabla.
//Se accede al valor de estos campos empleando los métodos 'get'.

public class Object {
	
    private Integer		corId          = null;
	private Integer     namId          = null;
	private Double		objra          = null;
	private Double		objde          = null;
	private Double      objabsv        = null;
	private Double      objmagv        = null;
	private Double      objmagb        = null;
	private Double      objmagr        = null;
	private Double      objmagi        = null;
	private Double      objbv          = null;
	private String      objsptype      = null;
	private String      objsptsubclass = null;
	private Double      objcoltemp     = null;
	private String      objlumclass    = null;
	private Double      objteff        = null;
	private Double      objgravity     = null;
	private Double      objmetal       = null;
	private String      objobs         = null;
	
	
	public Object(	Integer corId,
					Integer namId,
					Double  objra,
					Double  objde,
					Double  objabsv,
					Double  objmagv,
					Double  objmagb,
					Double  objmagr,
					Double  objmagi,
					Double  objbv,
					String  objsptype,
					String  objsptsubclass,
					Double  objcoltemp,
					String  objlumclass,
					Double  objteff,
					Double  objgravity,
					Double  objmetal,
					String  objobs){
		
		
	    this.corId	         = corId;
		this.namId           = namId;
		this.objra           = objra;
		this.objde           = objde;
		this.objabsv	     = objabsv;
		this.objmagv         = objmagv;
		this.objmagb         = objmagb;
		this.objmagr         = objmagr;
		this.objmagi         = objmagi;
		this.objbv           = objbv;
		this.objsptype       = objsptype;
		this.objsptsubclass  = objsptsubclass;
		this.objcoltemp      = objcoltemp;
		this.objlumclass     = objlumclass;
		this.objteff         = objteff;
		this.objgravity      = objgravity;
		this.objmetal        = objmetal;
		this.objobs          = objobs;
	}
	public Object(){
		
	}
	
	public Object(ResultSet resset, int tipo) throws SQLException{

		//Parámetros comunes
		this.corId		      = 	new Integer(resset.getInt("obj_corid"));
		if(resset.wasNull()) this.corId=null;
		
		this.objra	          = 	new Double(resset.getDouble("obj_ra"));
		this.objde 	      	  =	    new Double(resset.getDouble("obj_de"));

		this.objsptype        =     resset.getString("obj_sptype");
		
		this.objsptsubclass   =     resset.getString("obj_sptsubclass");
		
		this.objlumclass      =     resset.getString("obj_lumclass");

		this.objmagv          =     new Double(resset.getDouble("obj_magv"));
		if(resset.wasNull()) this.objmagv=null;

		this.objmagb          =     new Double(resset.getDouble("obj_magb"));
		if(resset.wasNull()) this.objmagb=null;

		this.objbv            =     new Double(resset.getDouble("obj_bv"));
		if(resset.wasNull()) this.objbv=null;

		this.objteff          =     new Double(resset.getDouble("obj_teff"));
		if(resset.wasNull()) this.objteff=null;
	
	}
	
	public Integer getCorId() {
		return corId;
		
	}

	public Integer getNamId() {
		return namId;
	}

	public Double getObjabsv() {
		return objabsv;
	}

	public Double getObjbv() {
		return objbv;
	}

	public Double getObjcoltemp() {
		return objcoltemp;
	}

	public Double getObjde() {
		return objde;
	}

	public Double getObjgravity() {
		return objgravity;
	}

	public String getObjlumclass() {
		return objlumclass;
	}

	public Double getObjmagb() {
		return objmagb;
	}

	public Double getObjmagi() {
		return objmagi;
	}

	public Double getObjmagr() {
		return objmagr;
	}

	public Double getObjmagv() {
		return objmagv;
	}

	public Double getObjmetal() {
		return objmetal;
	}

	public Double getObjra() {
		return objra;
	}

	public String getObjsptsubclass() {
		return objsptsubclass;
	}

	public String getObjsptype() {
		return objsptype;
	}

	public Double getObjteff() {
		return objteff;
	}

	public String getObjobs() {
		return objobs;
	}
}




