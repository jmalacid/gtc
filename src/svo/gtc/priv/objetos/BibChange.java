package svo.gtc.priv.objetos;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import svo.gtc.db.DriverBD;
import svo.gtc.db.priv.DBPrivate;
import svo.gtc.priv.objetos.RedInfo;

public class BibChange {
	
	private String bibcode = null;
	private String comment = null;
	private String state = null;
	private String prog = null;
	private String obl = null;
	private Integer prod = null;
	private String prods = null;
	private String redtype = null;
	private boolean biassub = false;
	private boolean darksub = false;
	private boolean flatfield = false;
	private boolean photometric = false;
	private boolean astrometry = false;
	private String aststatus = null;
	private Double astprecision = null;
	private String red=null;
	
	//Info de la colección
	private Integer col_id = null;
	private String usr_id = null;
	
	public String getBibcode() {
		return bibcode; 
	}
	public void setBibcode(String bibcode) {
		this.bibcode = bibcode;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getProg() {
		return prog;
	}
	public void setProg(String prog) {
		this.prog = prog;
	}
	public String getObl() {
		return obl;
	}
	public void setObl(String obl) {
		this.obl = obl;
	}
	public Integer getProd() {
		return prod;
	}
	public void setProd(Integer prod) {
		this.prod = prod;
	}
	
	public String getProds() {
		return prods;
	}
	public void setProds(String prods) {
		this.prods = prods;
	}
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
	public String getRed() {
		return red;
	}
	public void setRed(String red) {
		this.red = red;
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
	
	public BibChange(HttpServletRequest request){
		
		bibcode = request.getParameter("bib");
		try{
			comment="";
			comment = request.getParameter("com");
		}catch(Exception e){
			
		}
		try {
			state = request.getParameter("state");
		} catch (NumberFormatException e) {
			state="";
			//e.printStackTrace();
		}
		prog = request.getParameter("prog").trim().toUpperCase();
		obl = request.getParameter("obl").trim().toUpperCase();
		prods = request.getParameter("prod").trim();
		try{
			redtype = request.getParameter("redtype").trim();
			biassub = Boolean.valueOf(request.getParameter("bias"));
			darksub = Boolean.valueOf(request.getParameter("dark"));
			flatfield = Boolean.valueOf(request.getParameter("flatfield"));
			photometric = Boolean.valueOf(request.getParameter("photometric"));
			astrometry = Boolean.valueOf(request.getParameter("astrometry"));
			
			//Info de la colección
			col_id = Integer.valueOf(request.getParameter("col_id").trim());
			usr_id = request.getParameter("usr_id").trim();
			
		}catch(Exception e){
			//e.printStackTrace();
		}
		try{
			aststatus = request.getParameter("aststatus").trim();
		}catch(Exception e){
			//e.printStackTrace();
		}
		try{
			astprecision = Double.valueOf(request.getParameter("astprecision"));
		}catch(Exception e){
			//e.printStackTrace();
		}
		try{
			red=request.getParameter("red").trim();
		}catch(Exception e){
			//e.printStackTrace();
		}
	}
	
	public BibChange(String bib, String state){
		this.bibcode = bib;
		this.state = state;
	}
	
	public void ChangeState() throws SQLException{
		//Abrimos la base de datos
		DriverBD driver = new  DriverBD();
		Connection con = driver.bdConexion();

		DBPrivate conex = new DBPrivate(con);
				
		if(!state.equals("-")){
			conex.updateState(bibcode, state);
		}
	}
	
	public void AddChange() throws Exception{


/*		//primero comprobamos que no todos los campos son nulos
		if(((prog.length()==0 || prog==null) || (obl.length()==0 || obl==null) || (prods.length()==0 || prods==null)) && state.equals("-") && (comment.length()==0 ||comment == null)
				&& (redtype.length()==0 || redtype == null) && biassub && darksub && flatfield && photometric && astrometry){
			throw new Exception("No ha introducido cambios en la publicación");
		}*/
		
		//Abrimos la base de datos
		DriverBD driver = new  DriverBD();
		Connection con = driver.bdConexion();

		DBPrivate conex = new DBPrivate(con);
		//Si alguno de los valores de la publicación son nulos, saldría un error, hay que dar los tres valores
		if(!((prog.length()==0 || prog==null) && (obl.length()==0 || obl==null) && (prods.length()==0 || prods==null))){

			//Estaríamos aquí si no son nulos los tres
			if((prog.length()==0 || prog==null) || (obl.length()==0 || obl==null) || (prods.length()==0 || prods==null)){
				con.close();
				throw new Exception("Para cambiar los valores del producto relacionado debe especificar los valores del programa, observing block y producto");
			}else{
				String mensaje = "";
				//estudiamos cuantos productos diferentes hay
				String[] productos = prods.split(",");
				for(int i = 0;i<productos.length;i++){
					//Vemos si hay un rango
					try{
						String[] prodRango = productos[i].split("-");
						if(prodRango.length>1){

							for(int j = Integer.valueOf(prodRango[0]); j<=Integer.valueOf(prodRango[1]); j++){
								//Agregamos a la base de datos
								prod=j;
								//System.out.println("prodRango j : "+prod);

								Integer hayProd = conex.countProd(this);

								if(hayProd==0){
									mensaje = mensaje + "- "+prog+"/"+obl+"/"+prod+" : No está en el sistema<br>";
								}else{
									Integer pp = conex.countProdPub(this);
									if(pp>0){
										mensaje = mensaje + "- "+prog+"/"+obl+"/"+prod+" : Ya está en esta publicación<br>";
									}else{
										conex.addProdPub(this);
									}
									
								}
							}
						}else{
							//Agregamos esta individual a la base de datos
							prod=Integer.valueOf(productos[i]);
							Integer hayProd = conex.countProd(this);


							if(hayProd==0){
								mensaje = mensaje + "- "+prog+"/"+obl+"/"+prod+" : No está en el sistema<br>";
							}else{
								Integer pp = conex.countProdPub(this);
								if(pp>0){
									mensaje = mensaje + "- "+prog+"/"+obl+"/"+prod+" : Ya está en esta publicación<br>";
								}else{
									conex.addProdPub(this);
								}
							}


						}
					}catch(Exception e){
						//problema al trnasformar en enteros
						//System.out.println(e.getMessage());
						mensaje = mensaje + "Fallo en el valor del producto, no es un entero <br>";
					}
				}
				
				if(mensaje.length()>0){
					con.close();
					throw new Exception(mensaje);
				}
			}			
		}
		
		if(!(comment.length()==0 ||comment == null)){
			conex.addCom(bibcode, comment);
		}
		
		if(!state.equals("-")){
			conex.updateState(bibcode, state);
		}
		//if(!(redtype.length()==0 ||redtype == null) || biassub || darksub || flatfield || photometric || astrometry){
		if(red==null){
			RedInfo ri = new RedInfo(redtype, biassub, darksub, flatfield, photometric, astrometry, aststatus, astprecision, col_id, usr_id);
			conex.updateInfoCol(bibcode, ri);
		}
				
		con.close();
	}
	

}
