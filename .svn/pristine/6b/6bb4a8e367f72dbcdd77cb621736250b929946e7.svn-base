package svo.gtc.priv.objetos;

	import java.sql.Connection;
	import java.sql.SQLException;

	import javax.servlet.http.HttpServletRequest;

	import svo.gtc.db.DriverBD;
	import svo.gtc.db.priv.DBPrivate;
	import svo.gtc.priv.objetos.RedInfo;

	public class AddProd {
		
		private String bibcode = null;
		private String [] prods = null;

		private String prog = null;
		private String obl = null;
		private Integer prod = null;
		
		
		public String getBibcode() {
			return bibcode; 
		}
		public void setBibcode(String bibcode) {
			this.bibcode = bibcode;
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
		
		public String[] getProds() {
			return prods;
		}
		public void setProds(String[] prods) {
			this.prods = prods;
		}
		public AddProd(HttpServletRequest request){
			
			bibcode = request.getParameter("bib");
			System.out.println("aa: "+bibcode);
			prods = request.getParameterValues("add");
			
			
		}

		
		public void AddChange() throws Exception{

			
			//Abrimos la base de datos
			DriverBD driver = new  DriverBD();
			Connection con = driver.bdConexion();

			DBPrivate conex = new DBPrivate(con);
			
			//Si alguno de los valores de la publicación son nulos, saldría un error, hay que dar los tres valores
			if(!(prods.length==0 || prods==null)){

					String mensaje = "";
					
					for(int i = 0;i<prods.length;i++){
						//Obtenemos los valores prod/obl/prog del producto
						
							String[] prodValues = prods[i].split("_");
							this.prod=Integer.valueOf(prodValues[0]);
							this.obl=prodValues[1];
							this.prog=prodValues[2];
							
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
					
						
					
					
					if(mensaje.length()>0){
						con.close();
						throw new Exception(mensaje);
					}
							
			}else{
				con.close();
				throw new Exception("No ha seleccionado ningún producto");
			}
			
					
			con.close();
		}
		



	
}
