package svo.gtc.priv.objetos;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RedObj {

	File fich = null;
	String prog = null;
	String obl = null;
	String date = null;
	String openTime = null;
	String closeTime = null;
	Date open = null;
	Date close = null;
	String mensaje = null;
	String tipo = null;
	
	public RedObj(File fich, String prog, String obl, String date, String openTime,
			String closeTime, String tipo) {
		super();
		this.fich = fich;
		this.prog = prog;
		this.obl = obl;
		this.date = date;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.tipo = tipo;
		
		//Aquí obtenemos el date
		// DATE, OPENTIME, CLOSETIME (READTIME)
					if(this.open==null){
						try{
							if(tipo!=null && tipo.equals("S")){
								SimpleDateFormat sdf=new java.text.SimpleDateFormat("hh:mm:ss.S");
								this.open= sdf.parse(openTime);
							}else{
								SimpleDateFormat sdf=new java.text.SimpleDateFormat("hh:mm:ss.SSS");
								this.open = sdf.parse(openTime);
							}
							
							/*try{
								SimpleDateFormat sdf=new java.text.SimpleDateFormat("hh:mm:ss.SSS");
								this.open = sdf.parse(openTime);
								this.tipo="otro";
							}catch(NullPointerException e1){
								try{
									SimpleDateFormat sdf=new java.text.SimpleDateFormat("hh:mm:ss.S");
									this.open= sdf.parse(openTime);
									this.tipo="S";
								}catch(Exception e2){
									//e2.printStackTrace(); 
								}
							}catch(Exception e2){
								e2.printStackTrace(); 
							}*/
							
							
						
						}catch(NullPointerException e){}
						catch(Exception e){
							e.printStackTrace(); 
						}
					}
					
					if(this.close==null){
						try{
							if(tipo!=null && tipo.equals("S")){
								SimpleDateFormat sdf=new java.text.SimpleDateFormat("hh:mm:ss.S");
								this.close = sdf.parse(closeTime);
							}else{
								SimpleDateFormat sdf=new java.text.SimpleDateFormat("hh:mm:ss.SSS");
								this.close = sdf.parse(closeTime);
							}
							
							/*try{
								SimpleDateFormat sdf=new java.text.SimpleDateFormat("hh:mm:ss.SSS");
								this.close = sdf.parse(closeTime);
								this.tipo="otro";
							}catch(NullPointerException e1){
								try{
									SimpleDateFormat sdf=new java.text.SimpleDateFormat("hh:mm:ss.S");
									this.close = sdf.parse(closeTime);
									this.tipo="S";
								}catch(Exception e2){
									//e2.printStackTrace(); 
								}
							}catch(Exception e2){
								e2.printStackTrace(); 
							}*/
							
						}catch(NullPointerException e){}
						catch(Exception e){
							e.printStackTrace(); 
						}
					}
	}
	public File getFich() {
		return fich;
	}
	public void setName(File fich) {
		this.fich = fich;
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
	public Date getOpen() {
		return open;
	}
	public void setOpen(Timestamp open) {
		this.open = open;
	}
	public Date getClose() {
		return close;
	}
	public void setClose(Timestamp close) {
		this.close = close;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getOpenTime() {
		return openTime;
	}
	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}
	public String getCloseTime() {
		return closeTime;
	}
	public void setCloseTime(String closeTime) {
		this.closeTime = closeTime;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	
}