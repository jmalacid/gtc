package svo.gtc.siap;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

public class VOObject {
	
	private String pos = "pos";
	private Double ra = 0.0;
	private Double dec = 0.0;
	private String size = "size";
	private Double sr = 0.0;
	private String format = "format";
	private String instrument= "instrument";
	private String telescope = "telescope";
	private String obs_id = "obs_id";
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
		    }else if(aux.equalsIgnoreCase("INSTRUMENT")){
		        instrument = request.getParameter(aux);
		       // System.out.println(instrument);
		    }else if(aux.equalsIgnoreCase("TELESCOPE")){
		        telescope = request.getParameter(aux);
		       // System.out.println(telescope);
			}else if(aux.equalsIgnoreCase("OBS_ID")){
				obs_id = request.getParameter(aux);
			}
		} 

		
		if (pos.equals("pos") && obs_id.equalsIgnoreCase("obs_id")){

			if(!format.equalsIgnoreCase("METADATA")){
				//Si el campo está vacio no tendremos ni objID ni coordenadas
				throw new Exception("1");
			}


		}else{
			
			if(!pos.equals("pos")){
				if(size.equalsIgnoreCase("size")){
					//Tenemos POS pero no SIZE
					throw new Exception("3");
				}
			}else{
				if(!size.equalsIgnoreCase("size")){
					//Tenemos SIZE pero no POS
					throw new Exception("1");
				}
			}
			
			try{

			String[] aux = pos.split(",");

			//if(Coordenadas.checkCoordinatesFormat(pos.trim().replaceAll(","," ").replaceAll("[\\n\\r]",""))){
			//	Double[] aux = Coordenadas.coordsInDeg(pos.trim().replaceAll("[\\n\\r]",""));

				ra= Double.valueOf(aux[0]);
				dec= Double.valueOf(aux[1]);
			}catch(Exception e){
				//No se puede obtener el valor de las coordenadas
				if(obs_id.equalsIgnoreCase("obs_id")){
					throw new Exception("2");
				}else{
					pos=null;
				}
			}
		
			try{
				sr = Double.valueOf(size);
			}catch(Exception e){
				try{
					sr = Double.valueOf(size.split(",")[0]);
				}catch(Exception e2){
					if(obs_id.equalsIgnoreCase("obs_id")){
						throw new Exception("3");
					}
				}
				
			}
			
			//Limitamos la búsqueda a 10 grados
			if(sr>10){
				sr = 10.0;
			}
			
		if ( sr > 0){
				
			if(!instrument.equals("instrument")){
				
			
				if(instrument.equalsIgnoreCase("OSIRIS") || instrument.equalsIgnoreCase("CANARICAM") || instrument.equalsIgnoreCase("EMIR")|| instrument.equalsIgnoreCase("CIRCE")|| instrument.equalsIgnoreCase("HIPERCAM")|| instrument.equalsIgnoreCase("ALL")){
					//Cumple con los valores posibles de los instrumentos
				}else{	
				
					throw new Exception("4");
				}
			}
	
	
			if(!telescope.equals("telescope")){
				if(!telescope.equalsIgnoreCase("GTC")){
					throw new Exception("5");
				}
			}
			
			if(!obs_id.equals("obs_id")){
				try {
					id = Integer.valueOf(obs_id);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("7");
					//e.printStackTrace();
				}
			}
	
		}else{
			if(!obs_id.equals("obs_id")){
				try {
					id = Integer.valueOf(obs_id);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("7");
					//e.printStackTrace();
				}
			}else{
		    	throw new Exception("3");
			}
		}
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

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}

	public String getTelescope() {
		return telescope;
	}

	public void setTelescope(String telescope) {
		this.telescope = telescope;
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
			
 
}
