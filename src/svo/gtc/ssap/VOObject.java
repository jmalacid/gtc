package svo.gtc.ssap;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

public class VOObject {
	
	//Información sobre la posición
	private String pos = null;
	private Double ra = null;
	private Double dec = null;
	private String size = null;
	private Double sr = null;
	//Info sobre el formato (metadata o null)
	private String format = null;
	//Info sobre la banda espectral (rando o id)
	private String band= null;
	private String band_id = null;
	private Double band_min = null;
	private Double band_max = null;
	//Info sobre Time (rango o valor individual)
	private String time = null;
	private Integer dd_i = null;
	private Integer mm_i = null;
	private Integer yy_i = null;
	private Integer dd_e = null;
	private Integer mm_e = null;
	private Integer yy_e = null;
	//Info sobre el id de un producto en concreto
	private String obs_id = null;
	private Integer id = null;

	public VOObject (HttpServletRequest request) throws Exception{
		
		Enumeration nombresParametros = request.getParameterNames();
		
		while (nombresParametros.hasMoreElements()) {
		    String aux = (String) nombresParametros.nextElement();
		    if(aux.equalsIgnoreCase("POS")){
		        pos = request.getParameter(aux);
		       // System.out.println(pos);
		    }else if(aux.equalsIgnoreCase("SIZE")){
		    	size = request.getParameter(aux);
		       // System.out.println(sr);
		    }else if(aux.equalsIgnoreCase("FORMAT")){
		        format = request.getParameter(aux);
		       // System.out.println(format);
		    }else if(aux.equalsIgnoreCase("BAND")){
		        band = request.getParameter(aux);
		       // System.out.println(instrument);
		    }else if(aux.equalsIgnoreCase("TIME")){
		        time = request.getParameter(aux);
		       // System.out.println(telescope);
			}else if(aux.equalsIgnoreCase("OBS_ID")){
				obs_id = request.getParameter(aux);
			}
		} 

		//Obtenemos los valores referentes a cada parametro
		
		if(pos!=null && pos.length()>0){
			if(size!=null && size.length()>0){
				pos();
			}else{
				//Error, no habría size para la posición
				throw new Exception("3");
			}
		}
		
		if(band!=null && band.length()>0){
			band();
		}
		
		if(time!=null && time.length()>0){
			try{
				time();
			}catch(Exception e){
				throw new Exception("5");
			}
		}
		
		if(obs_id!=null && obs_id.length()>0){
			try {
				id = Integer.valueOf(obs_id);
			} catch (Exception e) {
				throw new Exception("7");
			}
		}
		
			
			
			
		
		}

	public void test() throws Exception{
		//Tenemos que tener valor para pos, band, time o format=metadata
	
		if (pos!=null && pos.length()>0){
			//Si buscan por pos también tendría que tener radio
			if(size!=null && size.length()>0){
				throw new Exception("3");
			}
		}else if(time!=null && time.length()>0){
			//tenemos valor para time
		}else if(band!=null && band.length()>0){
			//tenemos valor para band
		}else if(format.equals("METADATA")){
			//Se trata de una búsqueda por metadatos
		}else if(obs_id!=null && obs_id.length()>0){
		
		}else{
			throw new Exception("1");
		}
	}
	
	public void band() throws Exception{
		//Si hay listado o valores en ; se quita y cogemos el primero
		if(band.contains(",")){
			band = band.split(",")[0];
		}
		if(band.contains(";")){
			band = band.split(";")[0];
		}
		
		//Vemos si se trata de un rango (contiene /)
		if(band.contains("/")){
			String[] band_range = band.split("/");
			
			try{
				if(band_range[0]!=null && band_range[0].length()>0){
					band_min = Double.valueOf(band_range[0]);
				}
				if(band_range[1]!=null && band_range[1].length()>0){
					band_max = Double.valueOf(band_range[1]);
				}
			}catch(Exception e){
			//Hay errores con el valor de band
				throw new Exception("4");
			}
				
		}else{
			//Correspondería al nombre de la banda espectral
			band_id = band;
		}
	}
	
	public void time(){
		//Si hay listado nos quedamos con el primer valor
		if(time.contains(",")){
			time = time.split(",")[0];
		}

		if(time.contains("/")){
			//Vemos si se trata de un rango (contiene /)
			String[] time_range = time.split("/");

			//Primera parte del rango
			if(time_range[0].contains("T")){
				String range = time_range[0].split("T")[0];
				yy_i = Integer.valueOf(range.split("-")[0]);
				mm_i = Integer.valueOf(range.split("-")[1]);
				dd_i = Integer.valueOf(range.split("-")[2]);

			}else{
				//Separamos en valores entre "-", y vemos cuales están completos yyyy-mm-dd
				String[] values = time_range[0].split("-");
				//Vemos el número de valores
				
				yy_i = Integer.valueOf(values[0]);
				if(values.length>1){
					mm_i = Integer.valueOf(values[1]);
				}else{
					//Creamos el mes ini por defecto, el 1
					mm_i = 1;
				}
				if(values.length>2){
					dd_i = Integer.valueOf(values[2]);
				}else{
					//Creamos el día ini por defecto, el 1
					dd_i = 1;
				}

			}


			//Segunda parte del rango
			if(time_range.length>1){
				if(time_range[0].contains("T")){
					String range = time_range[0].split("T")[0];
					yy_e = Integer.valueOf(range.split("-")[0]);
					mm_e = Integer.valueOf(range.split("-")[1]);
					dd_e = Integer.valueOf(range.split("-")[2]);
	
				}else{
					//Separamos en valores entre "-", y vemos cuales están completos yyyy-mm-dd
					String[] values = time_range[0].split("-");
					//Vemos el número de valores
	
					yy_e = Integer.valueOf(values[0]);
					if(values.length>1){
						mm_e = Integer.valueOf(values[1]);
					}else{
						//Creamos el mes "end" por defecto, el 12
						mm_e = 12;
					}
					if(values.length>2){
						dd_e = Integer.valueOf(values[2]);
					}else {
						//Creamos el día "end" por defecto, el 31
						dd_e = 31;
					}
	
				}
			}
		}else{
			//Separamos en valores entre "-", y vemos cuales están completos yyyy-mm-dd
			String[] values = time.split("-");
			//Vemos el número de valores

			yy_i = Integer.valueOf(values[0]);
			yy_e = Integer.valueOf(values[0]);
			if(values.length>1){
				mm_i = Integer.valueOf(values[1]);
				mm_e = Integer.valueOf(values[1]);
			}else{
				//Creamos el rango de meses del año
				mm_i = 1;
				mm_e = 12;
			}
			if(values.length>2){
				dd_i = Integer.valueOf(values[2]);
				dd_e = Integer.valueOf(values[2]);
			}else {
				//Creamos el rango de días del mes
				dd_e = 1;
				dd_e = 31;
			}
		}


	}
	
	public void pos() throws Exception{
		
		if(pos.contains(";")){
		//Podría contener información sobre el tipo de coordenadas	
			pos = pos.split(";")[0];
		
		}
		
		try{

			String[] aux = pos.split(",");
			ra= Double.valueOf(aux[0]);
			dec= Double.valueOf(aux[1]);
		}catch(Exception e){
			//Valor de POS erroneo
			throw new Exception("2");
			
		}
		try{
			sr = Double.valueOf(size);
		}catch(Exception e){
			try{
				sr = Double.valueOf(size.split(",")[0]);
			}catch(Exception e2){
				throw new Exception("3");	
			}
				
		}
		
		//Limitamos la búsqueda a 10 grados
		if(sr>10){
			sr = 10.0;
		}
	}
	
	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public Double getRa() {
		return ra;
	}

	public void setRa(Double ra) {
		this.ra = ra;
	}

	public Double getDec() {
		return dec;
	}

	public void setDec(Double dec) {
		this.dec = dec;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public Double getSr() {
		return sr;
	}

	public void setSr(Double sr) {
		this.sr = sr;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getObs_id() {
		return obs_id;
	}

	public void setObs_id(String obs_id) {
		this.obs_id = obs_id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBand() {
		return band;
	}

	public void setBand(String band) {
		this.band = band;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getBand_id() {
		return band_id;
	}

	public void setBand_id(String band_id) {
		this.band_id = band_id;
	}

	public Double getBand_min() {
		return band_min;
	}

	public void setBand_min(Double band_min) {
		this.band_min = band_min;
	}

	public Double getBand_max() {
		return band_max;
	}

	public void setBand_max(Double band_max) {
		this.band_max = band_max;
	}

	public Integer getDd_i() {
		return dd_i;
	}

	public void setDd_i(Integer dd_i) {
		this.dd_i = dd_i;
	}

	public Integer getMm_i() {
		return mm_i;
	}

	public void setMm_i(Integer mm_i) {
		this.mm_i = mm_i;
	}

	public Integer getYy_i() {
		return yy_i;
	}

	public void setYy_i(Integer yy_i) {
		this.yy_i = yy_i;
	}

	public Integer getDd_e() {
		return dd_e;
	}

	public void setDd_e(Integer dd_e) {
		this.dd_e = dd_e;
	}

	public Integer getMm_e() {
		return mm_e;
	}

	public void setMm_e(Integer mm_e) {
		this.mm_e = mm_e;
	}

	public Integer getYy_e() {
		return yy_e;
	}

	public void setYy_e(Integer yy_e) {
		this.yy_e = yy_e;
	}
			
 


}
