package svo.gtc.db.web;

import java.sql.Connection;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;

import svo.gtc.web.FormParameterException;
import svo.gtc.web.FormMain;

/**
 *@author Raul Gutierrez Sanchez (raul@cab.inta-csic.es)
*/
//Esta clase hace referencia al acceso a la tabla Object. 


public class WebStats{
	
	Connection conex = null;
	
	Timestamp initDate 		= null;
	Timestamp finalDate 	= null;
	
	Date[]	  dates = {null,null};
	
	private Integer		queWebExt			= new Integer(0);
	private Integer		queVoExt			= new Integer(0);
	private Integer		queHostsExt			= new Integer(0);
	private Integer		queHostsExtVO			= new Integer(0);

	private Integer		queWebInt			= new Integer(0);
	private Integer		queVoInt			= new Integer(0);
	private Integer		queHostsInt			= new Integer(0);

	private Integer		downWebInt			= new Integer(0);
	private Integer		downVoInt			= new Integer(0);
	private Integer		downHostsInt		= new Integer(0);
	private Integer		downFilesInt		= new Integer(0);
	private Float		downMbInt			= new Float(0);
	private Integer		downHostsIntVO		= new Integer(0);
	private Integer		downFilesIntVO		= new Integer(0);
	private Float		downMbIntVO			= new Float(0);

	private Integer		downWebExt			= new Integer(0);
	private Integer		downVoExt			= new Integer(0);
	private Integer		downHostsExt		= new Integer(0);
	private Integer		downFilesExt		= new Integer(0);
	private Integer		downFilesExtPriv	= new Integer(0);
	private Integer		downSciExt			= new Integer(0);
	private Integer		downSciExtPriv		= new Integer(0);
	private Float		downMbExt			= new Float(0);
	private Integer		downHostsExtVO		= new Integer(0);
	private Integer		downFilesExtVO		= new Integer(0);
	private Float		downMbExtVO			= new Float(0);

	private String[] 	queTopHostsExt		= new String[0];
	private Integer[] 	queCountTopHostsExt	= new Integer[0];

	private String[] 	queTopHostsInt		= new String[0];
	private Integer[] 	queCountTopHostsInt	= new Integer[0];

	private String[] 	prevTopHostsExt		= new String[0];
	private Integer[] 	prevCountTopHostsExt	= new Integer[0];

	private String[] 	prevTopHostsInt		= new String[0];
	private Integer[] 	prevCountTopHostsInt	= new Integer[0];


	private String[] 	downTopHostsExt		= new String[0];
	private Integer[] 	downTopHostsFilesExt	= new Integer[0];
	private Float[] 	downTopHostsMbExt	= new Float[0];

	private String[] 	downTopHostsInt		= new String[0];
	private Integer[] 	downTopHostsFilesInt	= new Integer[0];
	private Float[] 	downTopHostsMbInt	= new Float[0];

	//Casos predprod
	private Integer case1 = null;
	private Integer case2 = null;
	private Integer case3 = null;
	private Integer case4 = null;
	
	
	private String 		queCondInt = " AND (logq_host LIKE '127.0.%' OR logq_host LIKE '192.168.%' OR logq_host LIKE '%cab.inta-csic.es' OR logq_host LIKE '%localhost%') ";
	private String 		queCondExt = " AND NOT (logq_host LIKE '127.0.%' OR logq_host LIKE '192.168.%' OR logq_host LIKE '%cab.inta-csic.es' OR logq_host LIKE '%localhost%') ";
	private String		queCondDate = "";
	
	private String 		prodCondInt = " AND (logprod.logp_host LIKE '127.0.%' OR logprod.logp_host LIKE '192.168.%' OR logprod.logp_host LIKE '%cab.inta-csic.es' OR logprod.logp_host LIKE '%localhost%') ";
	private String 		prodCondExt = " AND NOT (logprod.logp_host LIKE '127.0.%' OR logprod.logp_host LIKE '192.168.%' OR logprod.logp_host LIKE '%cab.inta-csic.es' OR logprod.logp_host LIKE '%localhost%') ";
	private String		prodCondDate = "";
	
	public WebStats(Connection conex) throws SQLException{
		initVars(conex);

	}
	
	public WebStats(Connection conex, String day1, String month1, String year1, String day2, String month2, String year2) throws SQLException, FormParameterException{
		// Fechas
		Date dateInit = null;
		Date dateEnd  = null;
		
		if(day1!=null && month1!=null && year1!=null
		   && day1.trim().length()>0 && month1.trim().length()>0 && year1.trim().length()>0){
			String auxDate = year1.trim()+"-"+month1.trim()+"-"+day1.trim();
			try{
				auxDate=FormMain.validaDate(auxDate);
				dateInit=Date.valueOf(auxDate);
			}catch(IllegalArgumentException e){
				throw new FormParameterException("Bad format for initial date: "+auxDate+". "+e.getMessage());
			}
		}
		if(day2!=null && month2!=null && year2!=null
		   && day2.trim().length()>0 && month2.trim().length()>0 && year2.trim().length()>0){
			String auxDate = year2.trim()+"-"+month2.trim()+"-"+day2.trim();
			try{
				auxDate=FormMain.validaDate(auxDate);
				dateEnd=Date.valueOf(auxDate);
			}catch(IllegalArgumentException e){
				throw new FormParameterException("Bad format for end date: "+auxDate+". "+e.getMessage());
			}
		}
		if(dateInit!=null || dateEnd!=null){
			dates=new Date[2];
			dates[0]=dateInit;
			dates[1]=dateEnd;
			
		}
		
		initVars(conex);
	}
	
	private void initVars(Connection conex) throws SQLException{
		this.conex=conex;
		
		setInitialDate();
		setFinalDate();
		
		setCondDate();
		
		setQueWeb();
		setQueVo();
		setQueHosts();
		setQueTopHosts();
		
		setDownWeb();
		setDownVo();
		setDownHosts();
		setDownFiles();
		setDownMb();
		setDownHostsVO();
		setDownFilesVO();
		setDownMbVO();
		setDownTopHosts();
		setDownSci();
		
		setPrevTopHosts();
		
		//Casos predprod
		setPredProd();
		
	}
	

	/** 
	 * Fija la fecha inicial cuando se han empezado a tomar las estadísticas.
	 * @return
	 * @throws SQLException
	 */
	public void setInitialDate() throws SQLException{
		Timestamp date1=null;
		Timestamp date2=null;
		
		String query = "SELECT min(logq_time) from logquery;";
		ResultSet resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			date1 = resset.getTimestamp(1);
		}
		resset.close();

		query = "SELECT min(logp_time) from logprod;";
		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			date2 = resset.getTimestamp(1);
		}
		resset.close();
		
		if(date1!=null && date2!=null){
			if(date1.before(date2)){ 
				this.initDate=date1;
			}else{
				this.initDate=date2;
			}
		}else if(date1!=null){
			this.initDate=date1;
		}else if(date2!=null){
			this.initDate=date2;
		}
	}

	/** 
	 * Fija la fecha final cuando se han terminado de tomar las estadísticas.
	 * @return
	 * @throws SQLException
	 */
	public void setFinalDate() throws SQLException{
		Timestamp date1=null;
		Timestamp date2=null;
		
		String query = "SELECT max(logq_time) from logquery;";
		ResultSet resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			date1 = resset.getTimestamp(1);
		}
		resset.close();

		query = "SELECT max(logp_time) from logprod;";
		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			date2 = resset.getTimestamp(1);
		}
		resset.close();
		
		if(date1!=null && date2!=null){
			if(date1.after(date2)){ 
				this.finalDate=date1;
			}else{
				this.finalDate=date2;
			}
		}else if(date1!=null){
			this.finalDate=date1;
		}else if(date2!=null){
			this.finalDate=date2;
		}else{
			this.finalDate=null;
		}
		
	}

	/**
	 * Fija la condicion SQL de restricción de búsqueda por fechas.
	 */
	private void setCondDate(){
		if(dates[0]!=null){
			this.queCondDate+= " AND logq_time>='"+dates[0].toString()+" 00:00:00' ";
			this.prodCondDate+= " AND logprod.logp_time>='"+dates[0].toString()+" 00:00:00' ";
		}
		if(dates[1]!=null){
			this.queCondDate+= " AND logq_time<='"+dates[1].toString()+" 23:59:59.999' ";
			this.prodCondDate+= " AND logprod.logp_time<='"+dates[1].toString()+" 23:59:59.999' ";
		}
	}
	
	/** 
	 * Obtiene el número de consultas web.
	 * 
	 * @throws SQLException
	 */
	private void setQueWeb() throws SQLException {
		
		/// EXTERNO
		String query = "SELECT count(*) FROM logquery WHERE logq_type='WEB' " + queCondExt + queCondDate;

		ResultSet resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.queWebExt = new Integer(resset.getInt(1));
		}
		resset.close();

		/// INTERNO
		query = "SELECT count(*) FROM logquery WHERE logq_type='WEB' " +queCondInt + queCondDate;
		
		resset = conex.createStatement().executeQuery(query);
		
		while(resset.next()){
			this.queWebInt = new Integer(resset.getInt(1));
		}
		resset.close();
	}

	
	/**
	 * Obtiene el numero de queries realizadas a través de SSAP.
	 * @throws SQLException
	 */
	private void setQueVo() throws SQLException {

		/// EXTERNO
		String query = "SELECT count(*) FROM logquery WHERE logq_type='SIAP' " + queCondExt + this.queCondDate;
		
		//System.out.println("quevo: "+query);
		
		ResultSet resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.queVoExt = new Integer(resset.getInt(1));
		}
		resset.close();

		/// INTERNO
		query = "SELECT count(*) FROM logquery WHERE logq_type='SIAP' " + queCondInt + this.queCondDate;
		
		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.queVoInt = new Integer(resset.getInt(1));
		}
		resset.close();
	}

	/** 
	 * Obtiene el numero de hosts que han realizado queries.
	 * 
	 * @throws SQLException
	 */
	private void setQueHosts() throws SQLException {
		
		/// EXTERNO
		String query = "SELECT count(distinct(logq_host)) FROM logquery WHERE logq_type='WEB' " + queCondExt + queCondDate;
		
		ResultSet resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.queHostsExt = new Integer(resset.getInt(1));
		}
		resset.close();
		
		/// EXTERNO
		query = "SELECT count(distinct(logq_host)) FROM logquery WHERE (logq_type='SIAP' or logq_type='SSAP') " + queCondExt + queCondDate;
				
		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.queHostsExtVO = new Integer(resset.getInt(1));
		}
		resset.close();

		/// INTERNO
		query = "SELECT count(distinct(logq_host)) FROM logquery WHERE 1=1 " + queCondInt + queCondDate;
		
		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.queHostsInt = new Integer(resset.getInt(1));
		}
		resset.close();

	}

	/**
	 * Obtiene el número de downloads realizados a través de la web.
	 * @throws SQLException
	 */
	private void setDownWeb() throws SQLException {
		
		/// EXTERNO
		String query = "SELECT count(*) FROM logprod WHERE logp_type NOT LIKE 'VO%'  " + prodCondExt + prodCondDate;

		//System.out.println(query);
		ResultSet resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.downWebExt = new Integer(resset.getInt(1));
		}
		resset.close();

		/// INTERNO
		query = "SELECT count(*) FROM logprod WHERE logp_type NOT LIKE 'VO%'  " + prodCondInt + prodCondDate;
		
		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.downWebInt = new Integer(resset.getInt(1));
		}
		resset.close();

	}

	/**
	 * Obtiene el número de downloads realizados a través del interfaz VO.
	 * @throws SQLException
	 */
	private void setDownVo() throws SQLException {
		
		/// EXTERNO
		String query = "SELECT count(*) FROM logprod WHERE logp_type LIKE 'VO%'  " + prodCondExt + prodCondDate;

		ResultSet resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.downVoExt = new Integer(resset.getInt(1));
		}
		resset.close();

		/// INTERNO
		query = "SELECT count(*) FROM logprod WHERE logp_type LIKE 'VO%'  " + prodCondInt + prodCondDate;
		
		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.downVoInt = new Integer(resset.getInt(1));
		}
		resset.close();

	}

	/**
	 * Obtiene el número de hosts que han realizado una descarga.
	 * @throws SQLException
	 */
	private void setDownHosts() throws SQLException {
		
		/// EXTERNO
		String query = "SELECT count(distinct(logp_host)) FROM logprod WHERE logp_type!='PREVIEW'  AND logp_type NOT LIKE 'VO%'   " + prodCondExt + prodCondDate;

		ResultSet resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.downHostsExt = new Integer(resset.getInt(1));
		}
		resset.close();

		/// INTERNO
		query = "SELECT count(distinct(logp_host)) FROM logprod WHERE logp_type!='PREVIEW'  AND logp_type NOT LIKE 'VO%'   " + prodCondInt + prodCondDate;
		
		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.downHostsInt = new Integer(resset.getInt(1));
		}
		resset.close();

	}
	
	/**
	 * Obtiene el número de hosts que han realizado una descarga.
	 * @throws SQLException
	 */
	private void setDownHostsVO() throws SQLException {
		
		/// EXTERNO
		String query = "SELECT count(distinct(logp_host)) FROM logprod WHERE logp_type!='PREVIEW'  AND logp_type LIKE 'VO%' " + prodCondExt + prodCondDate;

		ResultSet resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.downHostsExtVO = new Integer(resset.getInt(1));
		}
		resset.close();

		/// INTERNO
		query = "SELECT count(distinct(logp_host)) FROM logprod WHERE logp_type!='PREVIEW'  AND logp_type LIKE 'VO%' " + prodCondInt + prodCondDate;
		
		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.downHostsIntVO = new Integer(resset.getInt(1));
		}
		resset.close();

	}

	/**
	 * Obtiene el número de ficheros que se han descargado.
	 * @throws SQLException
	 */
	private void setDownFiles() throws SQLException {
		
		/// EXTERNO
		//String query = "SELECT count(*) FROM logprod, logprodsingle s WHERE logprod.logp_time=s.logp_time  AND logp_type NOT LIKE 'VO%'  " + prodCondExt + prodCondDate;
		String query = "SELECT count(*) FROM logprod right join logprodsingle s on logprod.logp_time=s.logp_time  where logp_type NOT LIKE 'VO%' " +prodCondExt + prodCondDate;
		
		ResultSet resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.downFilesExt = new Integer(resset.getInt(1));
		}
		resset.close();
		
		/// EXTERNO
		query = "SELECT count(*) FROM logprod right join logprodsingle s on logprod.logp_time=s.logp_time  where logp_type NOT LIKE 'VO%' and s.logp_priv=1 " +prodCondExt + prodCondDate;
				
		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.downFilesExtPriv = new Integer(resset.getInt(1));
		}
		resset.close();

		/// INTERNO
		query = "SELECT count(*) FROM logprod right join logprodsingle s on logprod.logp_time=s.logp_time  where logp_type NOT LIKE 'VO%' " + prodCondInt + prodCondDate;
		
		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.downFilesInt = new Integer(resset.getInt(1));
		}
		resset.close();

	}

	/**
	 * Obtiene el número de ficheros de ciencia que se han descargado.
	 * @throws SQLException
	 */
	private void setDownSci() throws SQLException {
		
		/// EXTERNO
		/*String query = "select count(*) from logprod right join logprodsingle s on logprod.logp_time=s.logp_time left join productos d on d.prod_id=s.lprod_id and d.prog_id=s.lprog_id and d.obl_id=s.lobl_id where "
				+ "logprod.logp_type!='PREVIEW' and (mty_id='SCI' or mty_id='SCI_R') and logprod.logp_type NOT LIKE 'VO%' " + prodCondExt + prodCondDate;*/
		String query = "select count(*) from logprod right join logprodsingle s on logprod.logp_time=s.logp_time where "
				+ "logprod.logp_type!='PREVIEW' and logp_sci=1 and logprod.logp_type NOT LIKE 'VO%' " + prodCondExt + prodCondDate;
		
		//System.out.println(query);
		ResultSet resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.downSciExt = new Integer(resset.getInt(1));
		}
		resset.close();

		// PRIVADO
		/*query = "select count(*) from logprod right join logprodsingle s on logprod.logp_time=s.logp_time left join productos d on d.prod_id=s.lprod_id and d.prog_id=s.lprog_id and d.obl_id=s.lobl_id where "
				+ "logprod.logp_type!='PREVIEW' and (mty_id='SCI' or mty_id='SCI_R') and logprod.logp_type NOT LIKE 'VO%' and s.logp_priv=1 " + prodCondExt + prodCondDate;*/
		query = "select count(*) from logprod right join logprodsingle s on logprod.logp_time=s.logp_time where "
				+ "logprod.logp_type!='PREVIEW' and logp_sci=1 and logprod.logp_type NOT LIKE 'VO%' and s.logp_priv=1 " + prodCondExt + prodCondDate;
		
		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.downSciExtPriv = new Integer(resset.getInt(1));
		}
		resset.close();
		
		/*/// INTERNO
		query = "SELECT count(*) FROM logprod, logprodsingle s WHERE logprod.logp_time=s.logp_time  AND logp_type NOT LIKE 'VO%'  " + prodCondInt + prodCondDate;
		
		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.downFilesInt = new Integer(resset.getInt(1));
		}
		resset.close();*/

	}
	
	/**
	 * Obtiene el número de ficheros que se han descargado.
	 * @throws SQLException
	 */
	private void setDownFilesVO() throws SQLException {
		
		/// EXTERNO
		String query = "SELECT count(*) FROM logprod, logprodsingle s WHERE logprod.logp_time=s.logp_time  AND logp_type LIKE 'VO%' " + prodCondExt + prodCondDate;

		ResultSet resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.downFilesExtVO = new Integer(resset.getInt(1));
		}
		resset.close();

		/// INTERNO
		query = "SELECT count(*) FROM logprod, logprodsingle s WHERE logprod.logp_time=s.logp_time  AND logp_type LIKE 'VO%' " + prodCondInt + prodCondDate;
		
		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.downFilesIntVO = new Integer(resset.getInt(1));
		}
		resset.close();

	}
	
	/**
	 * Obtiene el número de MB que se han descargado.
	 * @throws SQLException
	 */
	private void setDownMb() throws SQLException {
		/// EXTERNO
		String query = "SELECT sum(cast(logp_size as float)/1024/1024) FROM logprod WHERE 1=1  AND logp_type NOT LIKE 'VO%' " + prodCondExt + prodCondDate;

		//System.out.println(query);
		ResultSet resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.downMbExt = new Float(resset.getFloat(1));
		}
		resset.close();

		/// INTERNO
		query = "SELECT sum(cast(logp_size as float)/1024/1024) from logprod WHERE 1=1  AND logp_type NOT LIKE 'VO%' " + prodCondInt + prodCondDate;
		
		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.downMbInt = new Float(resset.getFloat(1));
		}
		resset.close();

	}
	
	/**
	 * Obtiene el número de MB que se han descargado.
	 * @throws SQLException
	 */
	private void setDownMbVO() throws SQLException {
		/// EXTERNO
		String query = "SELECT sum(cast(logp_size as float)/1024/1024) FROM logprod WHERE 1=1  AND logp_type LIKE 'VO%' " + prodCondExt + prodCondDate;

		//System.out.println(query);
		ResultSet resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.downMbExtVO = new Float(resset.getFloat(1));
		}
		resset.close();

		/// INTERNO
		query = "SELECT sum(cast(logp_size as float)/1024/1024) from logprod WHERE 1=1  AND logp_type LIKE 'VO%' " + prodCondInt + prodCondDate;
		
		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.downMbIntVO = new Float(resset.getFloat(1));
		}
		resset.close();

	}

	/**
	 * Busca los 10 hosts que más queries han realizado.
	 * 
	 * @throws SQLException
	 */
	private void setQueTopHosts() throws SQLException {
		
		/// EXTERNO
		String query = "SELECT logq_host, count(*) AS cuenta FROM logquery WHERE 1=1 " + queCondExt + queCondDate;
		
		query+=		" GROUP BY logq_host ORDER BY cuenta DESC LIMIT 10 ;";

		Vector<String> hostnames = new Vector<String>();
		Vector<Integer> hostcount = new Vector<Integer>();
		
		ResultSet resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			hostnames.add(resset.getString(1));
			hostcount.add(new Integer(resset.getInt(2)));
		}
		resset.close();
		
		this.queTopHostsExt 		= (String[])hostnames.toArray(new String[0]);
		this.queCountTopHostsExt	= (Integer[])hostcount.toArray(new Integer[0]);

	
		/// INTERNO
		query = "SELECT logq_host, count(*) AS cuenta FROM logquery WHERE 1=1 " + queCondInt + queCondDate;
		
		query+=	" GROUP BY logq_host ORDER BY cuenta DESC LIMIT 10 ";

		hostnames = new Vector<String>();
		hostcount = new Vector<Integer>();
		
		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			hostnames.add(resset.getString(1));
			hostcount.add(new Integer(resset.getInt(2)));
		}
		resset.close();
		
		this.queTopHostsInt 		= (String[])hostnames.toArray(new String[0]);
		this.queCountTopHostsInt	= (Integer[])hostcount.toArray(new Integer[0]);

	}

	/**
	 * Busca los 10 hosts que más previews han realizado.
	 * 
	 * @throws SQLException
	 */
	private void setPrevTopHosts() throws SQLException {
		
		/// EXTERNO
		String query = "SELECT logp_host, count(*) AS cuenta FROM logprod WHERE logp_type='PREVIEW' " + prodCondExt + prodCondDate;
		
		query+=		" GROUP BY logp_host ORDER BY cuenta DESC LIMIT 10 ;";

		Vector<String> hostnames = new Vector<String>();
		Vector<Integer> hostcount = new Vector<Integer>();
		
		ResultSet resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			hostnames.add(resset.getString(1));
			hostcount.add(new Integer(resset.getInt(2)));
		}
		resset.close();
		
		this.prevTopHostsExt 		= (String[])hostnames.toArray(new String[0]);
		this.prevCountTopHostsExt	= (Integer[])hostcount.toArray(new Integer[0]);

	
		/// INTERNO
		query = "SELECT logp_host, count(*) AS cuenta FROM logprod WHERE logp_type='PREVIEW' " + prodCondInt + prodCondDate;
		
		query+=		" GROUP BY logp_host ORDER BY cuenta DESC LIMIT 10 ;";

		hostnames = new Vector<String>();
		hostcount = new Vector<Integer>();
		
		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			hostnames.add(resset.getString(1));
			hostcount.add(new Integer(resset.getInt(2)));
		}
		resset.close();
		
		this.prevTopHostsInt 		= (String[])hostnames.toArray(new String[0]);
		this.prevCountTopHostsInt	= (Integer[])hostcount.toArray(new Integer[0]);

	}

	
	/**
	 * Busca los 10 hosts que más descargas han realizado.
	 * 
	 * @throws SQLException
	 */
	private void setDownTopHosts() throws SQLException {
		
		/// EXTERNO
		String query = 	"SELECT logp_host, count(*) AS cuenta, sum(cast(logp_size as float)/1024/1024) AS mb " +
						" FROM logprod " +
						" WHERE logp_type!='PREVIEW' " + prodCondExt + prodCondDate;
		
		query+=		" GROUP BY logp_host ORDER BY mb DESC LIMIT 10 ;";

		
		Vector<String> hostnames = new Vector<String>();
		Vector<Integer> filecount = new Vector<Integer>();
		Vector<Float> mb = new Vector<Float>();
		
		ResultSet resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			hostnames.add(resset.getString(1));
			filecount.add(new Integer(resset.getInt(2)));
			mb.add(new Float(resset.getFloat(3)));
		}
		resset.close();
		
		this.downTopHostsExt 		= (String[])hostnames.toArray(new String[0]);
		this.downTopHostsFilesExt	= (Integer[])filecount.toArray(new Integer[0]);
		this.downTopHostsMbExt		= (Float[])mb.toArray(new Float[0]);

		/// INTERNO
		query = "SELECT logp_host, count(*) AS cuenta, sum(cast(logp_size as float)/1024/1024) AS mb " +
				" FROM logprod " +
				" WHERE logp_type!='PREVIEW' " + prodCondInt + prodCondDate;

		
		query+=		" GROUP BY logp_host ORDER BY mb DESC LIMIT 10 ;";

		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			hostnames.add(resset.getString(1));
			filecount.add(new Integer(resset.getInt(2)));
			mb.add(new Float(resset.getFloat(3)));
		}
		resset.close();
		
		this.downTopHostsInt 		= (String[])hostnames.toArray(new String[0]);
		this.downTopHostsFilesInt	= (Integer[])filecount.toArray(new Integer[0]);
		this.downTopHostsMbInt		= (Float[])mb.toArray(new Float[0]);


	}
	
	
	/**
	 * Obtiene el número de reducions con o sin crudos y viceversa.
	 * @throws SQLException
	 */
	private void setPredProd() throws SQLException {
		/// Caso1
		/*String query= "select sum((select count(*) from pred_prod where (prod_id, obl_id, prog_id) in (select lprod_id, lobl_id, lprog_id from logprodsingle ls where ls.logp_time=l.logp_time) "
				+ "and pred_id=lprod_id)) from logprod right join logprodsingle l on "
				+ "logprod.logp_time=l.logp_time left join pred_prod p on l.lprod_id=p.prod_id and l.lprog_id=p.prog_id and l.lobl_id=p.obl_id where lprog_id='pred' AND logp_type NOT LIKE 'VO%'"
				+ prodCondExt + prodCondDate;*/
		String query = "select count(*) from logprod right join logprodsingle l on "
				+"logprod.logp_time=l.logp_time where l.lprog_id='pred' AND logp_type NOT LIKE 'VO%' "
				+prodCondExt + prodCondDate;

		ResultSet resset = conex.createStatement().executeQuery(query);
		Integer totalRed = null;
		while(resset.next()){
			totalRed = new Integer(resset.getInt(1));
		}
		resset.close();
				
		
		/// Caso2
		query = "select count(*) from logprod right join logprodsingle l on "
				+ "logprod.logp_time=l.logp_time right join pred_prod p on l.lprod_id=p.prod_id and l.lprog_id=p.prog_id and l.lobl_id=p.obl_id where "
				+ "logp_type NOT LIKE 'VO%' and p.pred_id in (select lprod_id from logprodsingle ls where ls.logp_time=l.logp_time and lprog_id='pred')"
				+  prodCondExt + prodCondDate;
		
		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.case2 = new Integer(resset.getInt(1));
		}
		resset.close();
		
		//Caso1, sería el total red, menos los que se han descargado con sus raw
		this.case1 = totalRed-this.case2;
		
		//Caso3
		query = "select count(*) from logprod right join logprodsingle l on "
				+ "logprod.logp_time=l.logp_time right join pred_prod p on l.lprod_id=p.prod_id and l.lprog_id=p.prog_id and l.lobl_id=p.obl_id where "
				+ "logp_type NOT LIKE 'VO%' and p.pred_id not in (select lprod_id from logprodsingle ls where ls.logp_time=l.logp_time and lprog_id='pred') "
				+  prodCondExt + prodCondDate;

		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.case3 = new Integer(resset.getInt(1));
		}
		resset.close();

		/// Caso4
		query = "select count(*) from logprod right join logprodsingle l on "
				+ "logprod.logp_time=l.logp_time left join pred_prod p on l.lprod_id=p.prod_id and l.lprog_id=p.prog_id and l.lobl_id=p.obl_id where "
				+ "p.prod_id is null AND l.lprog_id!='pred' and logp_type NOT LIKE 'VO%' and logp_sci=1 " 
				+ prodCondExt + prodCondDate;

		resset = conex.createStatement().executeQuery(query);
		while(resset.next()){
			this.case4 = new Integer(resset.getInt(1));
		}
		resset.close();
		
	}

	
	///////////////////////////////////////////////////////////////////////////////
	
	public Timestamp getInitDate() {
		return initDate;
	}

	public Timestamp getFinalDate() {
		return finalDate;
	}


	public Integer getQueWebExt() {
		return queWebExt;
	}

	public Integer getQueVoExt() {
		return queVoExt;
	}

	public Integer getQueHostsExt() {
		return queHostsExt;
	}
	
	public Integer getQueHostsExtVO() {
		return queHostsExtVO;
	}

	public String[] getQueTopHostsExt() {
		return queTopHostsExt;
	}

	public Integer[] getQueCountTopHostsExt() {
		return queCountTopHostsExt;
	}

	public Integer getQueWebInt() {
		return queWebInt;
	}

	public Integer getQueVoInt() {
		return queVoInt;
	}

	public Integer getQueHostsInt() {
		return queHostsInt;
	}

	public String[] getQueTopHostsInt() {
		// Realizamos una resolución de nombres:
		return queTopHostsInt;
	}

	public Integer[] getQueCountTopHostsInt() {
		return queCountTopHostsInt;
	}
	
	public Integer getDownWebInt() {
		return downWebInt;
	}

	public Integer getDownVoInt() {
		return downVoInt;
	}

	public Integer getDownHostsInt() {
		return downHostsInt;
	}

	public Integer getDownFilesInt() {
		return downFilesInt;
	}

	public Float getDownMbInt() {
		return downMbInt;
	}

	public Integer getDownHostsIntVO() {
		return downHostsIntVO;
	}

	public Integer getDownFilesIntVO() {
		return downFilesIntVO;
	}

	public Float getDownMbIntVO() {
		return downMbIntVO;
	}
	public Integer getDownWebExt() {
		return downWebExt;
	}

	public Integer getDownVoExt() {
		return downVoExt;
	}

	public Integer getDownHostsExt() {
		return downHostsExt;
	}

	public Integer getDownFilesExt() {
		return downFilesExt;
	}
	
	public Integer getDownSciExt() {
		return downSciExt;
	}
	public Integer getDownFilesExtPriv() {
		return downFilesExtPriv;
	}
	
	public Integer getDownSciExtPriv() {
		return downSciExtPriv;
	}

	public Float getDownMbExt() {
		return downMbExt;
	}
	
	public Integer getDownHostsExtVO() {
		return downHostsExtVO;
	}

	public Integer getDownFilesExtVO() {
		return downFilesExtVO;
	}

	public Float getDownMbExtVO() {
		return downMbExtVO;
	}

	public String[] getPrevTopHostsExt() {
		return prevTopHostsExt;
	}

	public Integer[] getPrevCountTopHostsExt() {
		return prevCountTopHostsExt;
	}

	public String[] getPrevTopHostsInt() {
		return prevTopHostsInt;
	}

	public Integer[] getPrevCountTopHostsInt() {
		return prevCountTopHostsInt;
	}

	public String[] getDownTopHostsExt() {
		return downTopHostsExt;
	}

	public Integer[] getDownTopHostsFilesExt() {
		return downTopHostsFilesExt;
	}

	public Float[] getDownTopHostsMbExt() {
		return downTopHostsMbExt;
	}

	public String[] getDownTopHostsInt() {
		return downTopHostsInt;
	}

	public Integer[] getDownTopHostsFilesInt() {
		return downTopHostsFilesInt;
	}

	public Float[] getDownTopHostsMbInt() {
		return downTopHostsMbInt;
	}

	public String getProdCondInt() {
		return prodCondInt;
	}

	public String getProdCondExt() {
		return prodCondExt;
	}

	public String getProdCondDate() {
		return prodCondDate;
	}

	public Integer getCase1() {
		return case1;
	}

	public void setCase1(Integer case1) {
		this.case1 = case1;
	}

	public Integer getCase2() {
		return case2;
	}

	public void setCase2(Integer case2) {
		this.case2 = case2;
	}

	public Integer getCase3() {
		return case3;
	}

	public void setCase3(Integer case3) {
		this.case3 = case3;
	}

	public Integer getCase4() {
		return case4;
	}

	public void setCase4(Integer case4) {
		this.case4 = case4;
	}

	
	
	/**
	 * Resuelve los nombres de host de la lista proporcionada.
	 * 
	 * @param hosts
	 * @return
	 */
	public String[] resolveHostNames(String[] hosts){
		String[] salida = new String[hosts.length];
		for(int i=0; i<hosts.length; i++){
			try{
				salida[i] = java.net.InetAddress.getByName(hosts[i]).getHostName();
			}catch(Exception e){
				salida[i] = hosts[i];
			}
		}
		
		return salida;
	}
	
}
