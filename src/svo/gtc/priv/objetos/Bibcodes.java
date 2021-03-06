package svo.gtc.priv.objetos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import svo.gtc.db.DriverBD;
import svo.gtc.db.priv.DBPrivate;

public class Bibcodes {

	private String bibcode = null;
	private Integer autor_id = null;
	private String autor = null;
	private Integer estado = null;
	private String estado_desc = null;
	private String details = null;
	private String email = null;
	private String prog = null; 
	private String obl = null;
	private Integer prod = null;
	private String prods = null;
	private String producto = null;
	private String comment = null;
	
	public String getBibcode() {
		return bibcode;
	}
	public void setBibcode(String bibcode) {
		this.bibcode = bibcode;
	}
	public Integer getAutor_id() {
		return autor_id;
	}
	public void setAutor_id(Integer autor_id) {
		this.autor_id = autor_id;
	}
	public String getAutor() {
		return autor;
	}
	public void setAutor(String autor) {
		this.autor = autor;
	}
	public Integer getEstado() {
		return estado;
	}
	public void setEstado(Integer estado) {
		this.estado = estado;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	
	public String getEstado_desc() {
		return estado_desc;
	}
	public void setEstado_desc(String estado_desc) {
		this.estado_desc = estado_desc;
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
	
	public String getProducto() {
		return producto;
	}
	public void setProducto(String producto) {
		this.producto = producto;
	}
	
	public String getProds() {
		return prods;
	}
	public void setProds(String prods) {
		this.prods = prods;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public Bibcodes(ResultSet resselTemp){
		try {

			bibcode = resselTemp.getString("pub_bibcode").trim();
			autor_id = Integer.valueOf(resselTemp.getString("aut_id"));
			autor = resselTemp.getString("aut_name").trim();
			estado_desc = resselTemp.getString("fin_desc").trim();
			email = resselTemp.getString("aut_email").trim();
			
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public Bibcodes(HttpServletRequest request){
		
		bibcode = request.getParameter("bib").trim();
		if(!request.getParameter("aut_id").equals("-")){
			autor_id = Integer.valueOf(request.getParameter("aut_id"));
		}
		autor = request.getParameter("autor").trim();
		email = request.getParameter("email").trim();
		details = request.getParameter("det").trim();
		prog = request.getParameter("prog").trim().toUpperCase();
		obl = request.getParameter("obl").trim().toUpperCase();
		prods = request.getParameter("prod").trim();
		comment = request.getParameter("comment").trim();
		
	}
	public Bibcodes(String bibcode, Integer autor_id, String autor,
			String details, String email) {
		super();
		this.bibcode = bibcode;
		this.autor_id = autor_id;
		this.autor = autor;
		this.details = details;
		this.email = email;
	}
	public void comprueba() throws Exception{
		if(bibcode.length()==0 || bibcode==null){
			throw new Exception("Falta información del bibcode");
		}
		
		
		DriverBD driver = new  DriverBD();
		Connection con = driver.bdConexion();

		DBPrivate conex = new DBPrivate(con);
		Integer count = conex.countBib(this.getBibcode());
		con.close();
		
		if(count>0){
			throw new Exception("Este bibcode ya está agregado en el sistema");
		}

		if(autor_id==null){
			if(autor.length()==0 || autor==null){
				throw new Exception("Falta información del autor");
			}
		}
	}
	
	public String insertDB() throws SQLException{
		
		String mensaje = "";
		//Creamos el ins_id apartir del número de datos guardados en la bbdd
		DriverBD driver = new  DriverBD();
		Connection con = driver.bdConexion();

		DBPrivate conex = new DBPrivate(con);
		
		if(autor_id == null){
			autor_id = conex.addAut(this);
		}
		
		//Agregamos la publicación
		conex.addPub(this);
		
		//Agregamos los comentarios
		if(!(comment.length()==0 ||comment == null)){
			conex.addCom(bibcode, comment);
		}
		
		if((prog.length()==0 || prog==null) || (obl.length()==0 || obl==null) || (prods.length()==0 || prods==null)){
			mensaje = "No ha completado los valores de la publicación (prog/obl/prod).";
		}else{
			
			//estudiamos cuantos productos diferentes hay
			String[] productos = prods.split(",");
			for(int i = 0;i<productos.length;i++){
				//Vemos si hay un rango
				System.out.println("productos: "+productos[i]);
				try{
					String[] prodRango = productos[i].split("-");
					if(prodRango.length>1){
						
						for(int j = Integer.valueOf(prodRango[0]); j<=Integer.valueOf(prodRango[1]); j++){
							//Agregamos a la base de datos
							prod=j;
							System.out.println("prodRango j : "+prod);
						
							Integer hayProd = conex.countProd(this);
							
							if(hayProd==0){
								mensaje = mensaje + "- "+prog+"/"+obl+"/"+prod+" : No está en el sistema<br>";
							}else{
								conex.addProdPub(this);
							}
						}
					}else{
						//Agregamos esta individual a la base de datos
						prod=Integer.valueOf(productos[i]);
						Integer hayProd = conex.countProd(this);
						
						
						if(hayProd==0){
							mensaje = mensaje + "- "+prog+"/"+obl+"/"+prod+" : No está en el sistema<br>";
						}else{
							conex.addProdPub(this);
						}
						
						
					}
				}catch(Exception e){
					//problema al trnasformar en enteros
					mensaje = mensaje + "Fallo en el valor del producto, no es un entero <br>";
				}
			}

		}
		
		con.close();
		
		return mensaje;
	}
	
}
