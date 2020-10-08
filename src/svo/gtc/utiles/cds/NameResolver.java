package svo.gtc.utiles.cds;

import java.util.Date;

import svo.gtc.utiles.cds.ResultadoSesameClient;
import svo.gtc.utiles.cds.SesameClient_new;

public class NameResolver extends Thread{

	private String nametoresolve = "";
	
	private boolean isFinished = false;
	private long timeInit = 0;
	private ResultadoSesameClient resultado = null;

	public NameResolver(String name){
		this.nametoresolve=name;
	}
	
	public void run(){
		//System.out.println("NameResolver: Empiezo...");
    	Date date = new Date();
    	this.timeInit  = date.getTime();
		try {
			this.resultado = SesameClient_new.sesameSearch(this.nametoresolve);
		} catch (Exception e) {
			e.printStackTrace();
			this.resultado=null;
		}

		//System.out.println("NameResolver: Termino..."+getElapsedTime());
		this.isFinished=true;
	}
	
    /**
     * Devuelve el tiempo trascurrido desde que se inici� el thread;
     * @return long <p>Tiempo transcurrido desde el inicio del proceso en milisegundos.</p>
     */
    public long getElapsedTime(){
    	Date date = new Date();
    	return (date.getTime()-this.timeInit);
    }
    
    public ResultadoSesameClient getResolution(){
    	return this.resultado;
    }
    
    public boolean isFinished(){
    	return this.isFinished;
    }



}
