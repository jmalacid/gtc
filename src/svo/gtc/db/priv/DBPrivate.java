package svo.gtc.db.priv;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import svo.gtc.db.usuario.UsuarioDb;
import svo.gtc.priv.objetos.AddProd;
import svo.gtc.priv.objetos.Author;
import svo.gtc.priv.objetos.BibChange;
import svo.gtc.priv.objetos.Bibcodes;
import svo.gtc.priv.objetos.ErrCount;
import svo.gtc.priv.objetos.ObsInfo;
import svo.gtc.priv.objetos.PredNoPub;
import svo.gtc.priv.objetos.ProdKey;
import svo.gtc.priv.objetos.RedInfo;
import svo.gtc.priv.objetos.RedObj;
import svo.gtc.priv.objetos.ReducInfo;
import svo.gtc.utiles.ObjMegara;
import svo.varios.utiles.seguridad.Encriptado;

public class DBPrivate {

	//---Conexiones a la B.D.-------------------------------------------
		static Connection conex;
		 
		static Logger logger = Logger.getLogger("svo.gtc");
		
		public DBPrivate(Connection con) {

			conex = con;

		}

		
		public Integer countErr(String tipo) throws SQLException{
			
			String select = "select count(distinct(prode_filename)) from prodError p join prode_error pe on p.prog_id=pe.prog_id and p.obl_id=pe.obl_id and p.prode_id=pe.prode_id";
				
			if(tipo.equals("sci")){
				select = select + " where prode_filename like '%Spectroscopy%' or prode_filename like '%Imaging%' or prode_filename like '%Polarimetry%' or prode_filename like '%MOS%'";
			}else if(tipo.equals("cal")){
				select = select + " where prode_filename not like '%Spectroscopy%' and prode_filename not like '%Imaging%' and prode_filename not like '%Polarimetry%' and prode_filename not like '%MOS%'";
			}else if(tipo.equals("sci_c")){
				select = select + " where (prode_filename like '%Spectroscopy%' or prode_filename like '%Imaging%' or prode_filename like '%Polarimetry%' or prode_filename like '%MOS%') and prode_rev=1";
			}else if(tipo.equals("cal_c")){
				select = select + " where (prode_filename not like '%Spectroscopy%' and prode_filename not like '%Imaging%' and prode_filename not like '%Polarimetry%' and prode_filename not like '%MOS%') and prode_rev=1";
			}
			
			
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			Integer Result= null;
			
			while (resselTemp.next()) {
				  Result = Integer.valueOf(resselTemp.getString(1));
			}
			
				
			stTemp.close();
			
			return  Result;
				
		}
		
		public String[] infoProgs() throws SQLException{
			
			String select = "select distinct(prog_id) from prodError";
				
			
			
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			int contador = 0;
			Vector aux = new Vector();
			while (resselTemp.next()) {
				
				 String Result = resselTemp.getString("prog_id").trim();

				aux.addElement(Result);
				contador++;
			}
			
				
			stTemp.close();
			
			return  (String[])aux.toArray(new String[0]);
				
		}
		
		public String[] fileError(String cod_error) throws SQLException{
			
			String select = "select prode_filename, prode_path from prodError p, prode_error pe where p.prog_id=pe.prog_id and p.prode_id=pe.prode_id and err_id='"+cod_error+"'";
			
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			int contador = 0;
			Vector aux = new Vector();
			while (resselTemp.next()) {
				
				 String Result = resselTemp.getString("prode_path").trim()+resselTemp.getString("prode_filename").trim();

				aux.addElement(Result);
				contador++;
			}
							
			stTemp.close();
			
			return  (String[])aux.toArray(new String[0]);
				
		}
		
		public String[] infoState() throws SQLException{
			
			String select = "select fin_id, fin_desc from final order by fin_id;";
				
			
			
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			int contador = 0;
			Vector aux = new Vector();
			while (resselTemp.next()) {
				
				 String Result = resselTemp.getString("fin_id").trim()+"--"+resselTemp.getString("fin_desc").trim();

				aux.addElement(Result);
				contador++;
			}
			
				
			stTemp.close();
			
			return  (String[])aux.toArray(new String[0]);
				
		}

		public String[] infoAutor() throws SQLException{
			
			String select = "select aut_id, aut_name from author order by aut_name;";
				
			
			
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			int contador = 0;
			Vector aux = new Vector();
			while (resselTemp.next()) {
				
				 String Result = resselTemp.getString("aut_id").trim()+"--"+resselTemp.getString("aut_name").trim();

				aux.addElement(Result);
				contador++;
			}
			
				
			stTemp.close();
			
			return  (String[])aux.toArray(new String[0]);
				
		}
		
		public String[] infoHist(String bib) throws SQLException{
			
			String select = "select com_id, com_description from comments where pub_bibcode='"+bib+"' order by com_id;";
				
			
			
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			int contador = 0;
			Vector aux = new Vector();
			while (resselTemp.next()) {
				
				 String Result = resselTemp.getString("com_id").trim()+"--"+resselTemp.getString("com_description").trim();

				aux.addElement(Result);
				contador++;
			}
			
				
			stTemp.close();
			
			return  (String[])aux.toArray(new String[0]);
				
		}

		public ErrCount[] countErr() throws SQLException{
		
			String select = "select err_id, (select count(prode_error.err_id) from prode_error where errors.err_id=prode_error.err_id) as cantidad, " +
					"(select count(prode_error.err_id) from prode_error where errors.err_id=prode_error.err_id and prode_rev='1') as rev," +
					"(select count(prode_error.err_id) from prode_error where errors.err_id=prode_error.err_id and prode_rev!='1') as pend" +
					", err_comment, err_desc from Errors where err_id like 'E%' order by err_id;";
		
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************
			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			int contador = 0;
			Vector aux = new Vector();
			while (resselTemp.next()) {
				
				 ErrCount Result = new ErrCount(resselTemp);

				aux.addElement(Result);
				contador++;
			}
			
				
			stTemp.close();
			
			return  (ErrCount[])aux.toArray(new ErrCount[0]);
		}
		
		public String[] infoErrors() throws SQLException{
			
			String select = "select err_id from Errors where err_id like 'E%' order by err_id;";
				
			
			
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			int contador = 0;
			Vector aux = new Vector();
			while (resselTemp.next()) {
				
				 String Result = resselTemp.getString("err_id").trim();

				aux.addElement(Result);
				contador++;
			}
			
				
			stTemp.close();
			
			return  (String[])aux.toArray(new String[0]);
				
		}
		
		public PredNoPub[] countPredNoPub() throws SQLException{
			
			String select = "select pp.prog_id, pp.obl_id, pp.prod_id, (select col_bibcode from colecciondatos where col_id=p.col_id and usr_id=p.usr_id) bibcode "
					+ "from pred_prod pp left join prodreducido p on pp.pred_id=p.pred_id left join proddatos d on d.prog_id=pp.prog_id and "
					+ "d.obl_id=pp.obl_id and d.prod_id=pp.prod_id where pred_type='USER' and mty_id='SCI' and (pp.prog_id, pp.obl_id, pp.prod_id) "
					+ "not in (select pub_prog, pub_obl, pub_prod from prod_pub) order by pp.prog_id, pp.obl_id";
		
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************
			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			Vector aux = new Vector();
			while (resselTemp.next()) {
				
				 PredNoPub Result = new PredNoPub(resselTemp);

				aux.addElement(Result);			
			}
			
				
			stTemp.close();
			
			return  (PredNoPub[])aux.toArray(new PredNoPub[0]);
		}
		
		public String[] infoEstado() throws SQLException{
			
			String select = "select final.fin_desc, (select count(publication.fin_id) from publication where publication.fin_id=final.fin_id) as cantidad from final where fin_id!=0 order by fin_id;";
			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			int contador = 0;
			Vector aux = new Vector();
			while (resselTemp.next()) {
				
				 String Result = resselTemp.getString("fin_desc").trim()+"-.-"+resselTemp.getString("cantidad").trim();

				aux.addElement(Result);
				contador++;
			}
			
				
			stTemp.close();
			
			return  (String[])aux.toArray(new String[0]);
				
		}
		
		public Integer countBib() throws SQLException{
			
			String select = "select count(*) from publication;";
			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			Integer count = null;
			
			while (resselTemp.next()) {
				
				 count = resselTemp.getInt(1);
			}
			
				
			stTemp.close();
			
			return  count;
				
		}
		
		public Author infoAuthor(Integer aut_id) throws SQLException{
			
			String select = "select aut_name, aut_email, aut_details from Author where aut_id='"+aut_id+"';";
				
			
			
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			Author Result = null;
			
			while (resselTemp.next()) {
				
				Result = new Author(resselTemp);
			}
			
				
			stTemp.close();
			
			return  Result;
				
		}
		
		public ObsInfo[] obsInfo(String prog, String err, String order) throws SQLException{
			
			String select = "select p.prode_id, p.prog_id, p.obl_id, prode_filename, pe.err_id, err_desc, prode_rev from prodError p " +
					"join prode_error pe on p.prog_id=pe.prog_id and p.obl_id=pe.obl_id and p.prode_id=pe.prode_id join errors on pe.err_id=errors.err_id ";
			String union = " where ";
			if(!prog.equals("-")){
				select = select +union+ " p.prog_id='"+prog+"'";
				union = " and ";
			}
			
			if(!err.equals("-")){
				select = select +union+ " errors.err_id='"+err+"'";
				union = " and ";
			}
			
			if(order.equals("rev")){
				select = select +union+ " prode_rev=1";
			}else if(order.equals("pend")){
				select = select +union+ " prode_rev!=1";
			}
			
			//select = select + ";";
				
			//System.out.println(select);
			
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			int contador = 0;
			Vector aux = new Vector();
			while (resselTemp.next()) {
				
				 ObsInfo Result = new ObsInfo(resselTemp);

				aux.addElement(Result);
				contador++;
			}
			
				
			stTemp.close();
			
			return  (ObsInfo[])aux.toArray(new ObsInfo[0]);
		
		}
		public ObsInfo[] obsInfo(String user) throws SQLException{
			String select = "select p.prode_id, p.prog_id, p.obl_id, prode_filename, pe.err_id, err_desc, prode_rev from proderror p join obsblock o on o.prog_id=p.prog_id "
					+ "and o.obl_id=p.obl_id join prode_error pe on p.prog_id=pe.prog_id and p.obl_id=pe.obl_id and p.prode_id=pe.prode_id join errors on pe.err_id=errors.err_id "
					+ "where obl_pi='"+user+"'";
			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			int contador = 0;
			Vector aux = new Vector();
			while (resselTemp.next()) {
				
				 ObsInfo Result = new ObsInfo(resselTemp);

				aux.addElement(Result);
				contador++;
			}
			
				
			stTemp.close();
			
			return  (ObsInfo[])aux.toArray(new ObsInfo[0]);
		}
		public Bibcodes[] BibInfo(String bib, String state, String order) throws SQLException{
			
			String select = "select pub_bibcode, author.aut_id, author.aut_name, fin_desc, aut_email from publication, final, author where publication.aut_id=author.aut_id and publication.fin_id=final.fin_id";
			
			if(bib != null && bib.length()>0){
				select = select + " and pub_bibcode like '%"+bib.trim()+"%'";
			}
			
			if(!state.equals("-")){
				select = select + " and publication.fin_id='"+state+"'";
			}
			
			if(order !=null && order.length()>0){
				if(order.contains("desc")){
					select = select + " order by "+order.replaceAll("_desc", "")+" desc;";
				}else{
					select = select + " order by "+order+";";
				}
			}
			
				
			//System.out.println("select: "+select);
			
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			int contador = 0;
			Vector aux = new Vector();
			while (resselTemp.next()) {
				
				 Bibcodes Result = new Bibcodes(resselTemp);

				aux.addElement(Result);
				contador++;
			}
			
				
			stTemp.close();
			
			return  (Bibcodes[])aux.toArray(new Bibcodes[0]);
		
		}
		
		public Bibcodes BibInfo(String bib) throws SQLException{
			
			String select = "select pub_bibcode, author.aut_id, author.aut_name, fin_desc, aut_email from publication, final, author where publication.aut_id=author.aut_id and " +
					"publication.fin_id=final.fin_id and pub_bibcode like '%"+bib.trim()+"%';";
				
			//System.out.println("select: "+select);
			
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			Bibcodes Result = null;
			
			while (resselTemp.next()) {
				
				 Result = new Bibcodes(resselTemp);

			}
			
				
			stTemp.close();
			
			return  Result;
		
		}
		
		public ReducInfo RedInfo(String prog, String obl, Integer prod) throws SQLException{
			
			String select = "select * from reds where prog_id='"+prog+"' and obl_id='"+obl+"' and prod_id='"+prod+"'";
				
			//System.out.println("select: "+select);
			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			ReducInfo Result = null;
			
			while (resselTemp.next()) {
				
				 Result = new ReducInfo(resselTemp);

			}
	
			stTemp.close();
			
			return  Result;
		
		}
		
		public String[] pubArchive() throws SQLException{
			
			String select = "select pa_bibcode, pa_comment from pubarchive order by pa_bibcode";
					
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			
			Vector aux = new Vector();
			
			while (resselTemp.next()) {
				
				String Result = resselTemp.getString("pa_bibcode")+".-."+resselTemp.getString("pa_comment");
				aux.addElement(Result);
			}
			
				
			stTemp.close();
			
			return  (String[])aux.toArray(new String[0]);
		
		}
		
		public Integer addAut(Bibcodes bib) throws SQLException{
			
			Integer aut_id= 1;
			Statement stm = null;
			try{
				conex.setAutoCommit(false);
				stm = conex.createStatement();
				ResultSet resset = stm.executeQuery("SELECT max(aut_id)+1 as aut_id FROM author");
				if(resset.next()) aut_id=new Integer(resset.getInt("aut_id"));
			
			String insert = "INSERT INTO AUTHOR (aut_id, aut_name, aut_email, aut_details) VALUES ('"+aut_id+"','"+bib.getAutor()+"','"+bib.getEmail()+"','"+bib.getDetails()+"')";
			
			//System.out.println(insert);
				
			stm.executeUpdate(insert);
			conex.commit();
			
			}catch(SQLException e){
				
				//System.out.println("Error:"+e);
				conex.setAutoCommit(true);
				throw e;
				
			}finally{
				if(stm!=null){
					try {
						stm.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}		
				conex.setAutoCommit(true);
			return aut_id;
		}
		
		public void addPub(Bibcodes bib) throws SQLException{
			
			String insert = "INSERT INTO PUBLICATION (PUB_BIBCODE, FIN_ID, AUT_ID) VALUES ('"+bib.getBibcode()+"','0','"+bib.getAutor_id()+"')";
			
			//System.out.println(insert);

			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

				Statement stm = conex.createStatement();
				//ResultSet resselTemp = stm.executeUpdate(insert);
				stm.executeUpdate(insert);
					
				stm.close();
			
			
		}
		
		public void addProdPub(Bibcodes bib) throws SQLException{
			
			Integer pub_id= 1;
			Statement stm = null;
			
			Timestamp pdate = new Timestamp((new java.util.Date()).getTime());
			
			try{
				conex.setAutoCommit(false);
				stm = conex.createStatement();
				ResultSet resset = stm.executeQuery("SELECT max(pub_id)+1 as pub_id FROM prod_pub;");
				if(resset.next()) pub_id=new Integer(resset.getInt("pub_id"));
			
			String insertProd_pub = "INSERT INTO prod_pub (pub_id, pub_prog, pub_obl, pub_prod) VALUES ('"+pub_id+"','"+bib.getProg()+"','"+bib.getObl()+"','"+bib.getProd()+"')";
			String insertProdpub = "Insert into prodpub (pub_id, pub_bibcode, pub_date) values ('"+pub_id+"','"+bib.getBibcode()+"','"+pdate+"')";
			
			//System.out.println(insert);
				
			stm.executeUpdate(insertProd_pub);
			stm.executeUpdate(insertProdpub);
			conex.commit();
			
			}catch(SQLException e){
				
				//System.out.println("Error:"+e);
				conex.setAutoCommit(true);
				throw e;
				
			}finally{
				if(stm!=null){
					try {
						stm.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}		
				conex.setAutoCommit(true);
		}
		
		public void addProdPub(AddProd bib) throws SQLException{
			
			Integer pub_id= 1;
			Statement stm = null;
			Timestamp pdate = new Timestamp((new java.util.Date()).getTime());
			try{
				conex.setAutoCommit(false);
				stm = conex.createStatement();
				ResultSet resset = stm.executeQuery("SELECT max(pub_id)+1 as pub_id FROM prod_pub;");
				if(resset.next()) pub_id=new Integer(resset.getInt("pub_id"));
			
			String insertProd_pub = "INSERT INTO prod_pub (pub_id, pub_prog, pub_obl, pub_prod) VALUES ('"+pub_id+"','"+bib.getProg()+"','"+bib.getObl()+"','"+bib.getProd()+"')";
			String insertProdpub = "Insert into prodpub (pub_id, pub_bibcode, pub_date) values ('"+pub_id+"','"+bib.getBibcode()+"','"+pdate+"')";
			
			//System.out.println(insert);
				
			stm.executeUpdate(insertProd_pub);
			stm.executeUpdate(insertProdpub);
			conex.commit();
			
			}catch(SQLException e){
				
				//System.out.println("Error:"+e);
				conex.setAutoCommit(true);
				throw e;
				
			}finally{
				if(stm!=null){
					try {
						stm.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}		
				conex.setAutoCommit(true);
		}
		
		public void addProdPub(BibChange bib) throws SQLException{
			
			Integer pub_id= 1;
			Statement stm = null;
			Timestamp pdate = new Timestamp((new java.util.Date()).getTime());
			try{
				conex.setAutoCommit(false);
				stm = conex.createStatement();
				ResultSet resset = stm.executeQuery("SELECT max(pub_id)+1 as pub_id FROM prod_pub");
				if(resset.next()) pub_id=new Integer(resset.getInt("pub_id"));
			
			String insertProd_pub = "INSERT INTO prod_pub (pub_id, pub_prog, pub_obl, pub_prod) VALUES ('"+pub_id+"','"+bib.getProg()+"','"+bib.getObl()+"','"+bib.getProd()+"')";
			String insertProdpub = "Insert into prodpub (pub_id, pub_bibcode, pub_date) values ('"+pub_id+"','"+bib.getBibcode()+"','"+pdate+"')";
			
			//System.out.println(insert);
				
			stm.executeUpdate(insertProd_pub);
			stm.executeUpdate(insertProdpub);
			conex.commit();
			
			}catch(SQLException e){
				
				//System.out.println("Error:"+e);
				conex.setAutoCommit(true);
				throw e;
				
			}finally{
				if(stm!=null){
					try {
						stm.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}		
				conex.setAutoCommit(true);
		}
		
		public void addCom(String bib, String comment) throws SQLException{
			String bib_sql = StringEscapeUtils.escapeSql(bib);
			String com = StringEscapeUtils.escapeSql(comment);
			Timestamp com_id = new Timestamp((new java.util.Date()).getTime());
			
			String insert = "INSERT INTO comments (com_id, pub_bibcode, com_description) VALUES ('"+com_id+"','"+bib_sql+"','"+com+"');";
			
			//System.out.println(insert);

			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

				Statement stm = conex.createStatement();
				//ResultSet resselTemp = stm.executeUpdate(insert);
				stm.executeUpdate(insert);
					
				stm.close();
			
		}
		
		public void updateState (String bibcode, String fin_id) throws SQLException{
			String update = "update publication set fin_id='"+fin_id+"' where pub_bibcode='"+bibcode+"'" ;
			
			Statement stm = conex.createStatement();
			stm.executeUpdate(update);
			
			stm.close();
		}
		
		public void updateProd (BibChange bib) throws SQLException{
			String update = "update publication set pub_prog='"+bib.getProg()+"', pub_obl='"+bib.getObl()+"', pub_prod='"+bib.getProd()+"' where pub_bibcode='"+bib.getBibcode()+"'" ;
			
			Statement stm = conex.createStatement();
			stm.executeUpdate(update);
			
			stm.close();
		}
		
		public void updateAut (Author aut) throws SQLException{
			String update = "update author set" ;
			String union = " ";
			
			if(!(aut.getAut_name().length()==0 || aut.getAut_name()==null)){
				update = update + union + "aut_name='"+aut.getAut_name()+"'";
				union= ", ";
			}
			if(!(aut.getAut_email().length()==0 || aut.getAut_email()==null)){
				update = update + union + "aut_email='"+aut.getAut_email()+"'";
				union= ", ";
			}
			if(!(aut.getAut_details().length()==0 || aut.getAut_details()==null)){
				update = update + union + "aut_details='"+aut.getAut_details()+"' ";
			}
			
			update = update + "where aut_id='"+aut.getAut_id()+"';";
			
			Statement stm = conex.createStatement();
			stm.executeUpdate(update);
			
			stm.close();
		}
		
		public Integer countProd(Bibcodes bib) throws SQLException{
			
			String select = "select count(*) from prodDatos where prog_id='"+bib.getProg()+"' and obl_id='"+bib.getObl()+"' and prod_id='"+bib.getProd()+"'";
			
			//System.out.println(select);
			
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			Integer Result= null;
			
			while (resselTemp.next()) {
				  Result = Integer.valueOf(resselTemp.getString(1));
			}
			
				
			stTemp.close();
			
			return  Result;
				
		}
		
		public Integer countProd(AddProd bib) throws SQLException{
			
			String select = "select count(*) from prodDatos where prog_id='"+bib.getProg()+"' and obl_id='"+bib.getObl()+"' and prod_id='"+bib.getProd()+"'";
			
			//System.out.println(select);
			
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			Integer Result= null;
			
			while (resselTemp.next()) {
				  Result = Integer.valueOf(resselTemp.getString(1));
			}
			
				
			stTemp.close();
			
			return  Result;
				
		}
		
		public Integer countProd(BibChange bib) throws SQLException{
			
			String select = "select count(*) from prodDatos where prog_id='"+bib.getProg()+"' and obl_id='"+bib.getObl()+"' and prod_id='"+bib.getProd()+"';";
			
			
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			Integer Result= null;
			
			while (resselTemp.next()) {
				  Result = Integer.valueOf(resselTemp.getString(1));
			}
			
				
			stTemp.close();
			
			return  Result;
				
		}
		
		public Integer countProdPub(BibChange bib) throws SQLException{
			
			String select = "select count(*) from prodpub, prod_pub where pub_prog='"+bib.getProg()+"' and pub_obl='"+bib.getObl()+"' and pub_prod='"+bib.getProd()+"'" +
					" and pub_bibcode='"+bib.getBibcode()+"' and prodpub.pub_id=prod_pub.pub_id;";
			//System.out.println(select);
			
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			Integer Result= null;
			
			while (resselTemp.next()) {
				  Result = Integer.valueOf(resselTemp.getString(1));
			}
			
				
			stTemp.close();
			
			return  Result;
				
		}
		
		public Integer countProdPub(AddProd bib) throws SQLException{
			
			String select = "select count(*) from prodpub, prod_pub where pub_prog='"+bib.getProg()+"' and pub_obl='"+bib.getObl()+"' and pub_prod='"+bib.getProd()+"'" +
					" and pub_bibcode='"+bib.getBibcode()+"' and prodpub.pub_id=prod_pub.pub_id;";
			//System.out.println(select);
			
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			Integer Result= null;
			
			while (resselTemp.next()) {
				  Result = Integer.valueOf(resselTemp.getString(1));
			}
			
				
			stTemp.close();
			
			return  Result;
				
		}

		public Integer countBib(String bibcode) throws SQLException{
			
			String select = "select count(*) from publication where pub_bibcode='"+bibcode+"';";
			
			
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			Integer Result= null;
			
			while (resselTemp.next()) {
				  Result = Integer.valueOf(resselTemp.getString(1));
			}
			
				
			stTemp.close();
			
			return  Result;
				
		}
		public String[] selectPub(String prog, String obl, Integer prod) throws SQLException{
			
			String select = "select pub_bibcode from prodpub, prod_pub where pub_prog='"+prog+"' and pub_obl='"+obl+"' and pub_prod='"+prod+"' and prodpub.pub_id=prod_pub.pub_id;";
			
			//System.out.println(select);
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			int contador = 0;
			Vector aux = new Vector();
			while (resselTemp.next()) {
				
				 String Result = resselTemp.getString("pub_bibcode").trim();

				aux.addElement(Result);
				contador++;
			}
			
				
			stTemp.close();
			
			return  (String[])aux.toArray(new String[0]);
				
		}
		
		public RedInfo selectInfo(String bibcode) throws SQLException{
			
			String select = "select pred_biassub, pred_darksub, pred_flatfield, pred_photometric, pred_astrometry, pred_aststatus, pred_astprecision, col_redtype, pred.col_id, pred.usr_id " +
					"from prodreducido as pred, colecciondatos as cd where pred.usr_id=cd.usr_id and pred.col_id=cd.col_id and col_bibcode='"+bibcode+"' and pred.pred_type='USER';";
			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select); 

			RedInfo Result = null;
			
			while (resselTemp.next()) {
				
				 Result = new RedInfo(resselTemp);

			}
			
				
			stTemp.close();
			
			return  Result;
			
		}
		
		public String[] selectpubsNo() throws SQLException{
			
			//String select = "select pp.prog_id, pp.obl_id, pp.prod_id, col_bibcode from pred_prod as pp, prodreducido as pred, colecciondatos as cd where pp.pred_id=pred.pred_id and pred.usr_id=cd.usr_id " +
			//		"and pred.col_id=cd.col_id where cd.col_bibcode not in(select col_bibcode from colecciondatos)";
			String select = "select pp.prog_id, pp.obl_id, pp.prod_id, col_bibcode from pred_prod as pp left join prodreducido as pred on pp.pred_id=pred.pred_id left join colecciondatos as "
					+ "cd on pred.usr_id=cd.usr_id and pred.col_id=cd.col_id where cd.col_bibcode not in(select col_bibcode from colecciondatos)";
			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select); 

			int contador = 0;
			Vector aux = new Vector();
			while (resselTemp.next()) {
				
				 String Result = resselTemp.getString("prog_id").trim()+"/"+resselTemp.getString("obl_id").trim()+"/"+resselTemp.getInt("prod_id")+".--."+resselTemp.getString("col_bibcode").trim();

				aux.addElement(Result);
				contador++;
			}
			
				
			stTemp.close();
			
			return  (String[])aux.toArray(new String[0]);
			
		}
		
		public String selectLastCom(String bibcode) throws SQLException{
			
			String select = "select com_id, com_description from comments where pub_bibcode='"+bibcode+"' order by com_id desc limit 1;";
			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select); 

			String Result= null;
			
			while (resselTemp.next()) {
				  Result = resselTemp.getString("com_id")+" - "+resselTemp.getString("com_description");
			}
				
			stTemp.close();
			
			return  Result;
			
		}	
		
		public void updateInfoCol (String bib, RedInfo ri) throws SQLException{
			
			//Metemos información redtype
			if(ri.getRedtype()!=null && ri.getRedtype().length()>0){
				String updateCD = "update colecciondatos set col_redtype='"+ri.getRedtype()+"' where col_bibcode='"+bib+"';";
						
				Statement stm = conex.createStatement();
				stm.executeUpdate(updateCD);
				
				stm.close();
			}
			//if(ri.isBiassub() || ri.isDarksub() || ri.isFlatfield() || ri.isPhotometric() || ri.isAstrometry()){
				//Metemos informacion al producto
				String update = "update prodreducido set";
				String union = " ";
				
				if(ri.isBiassub()){
					update += union + "pred_biassub='true'";
					union = ", ";
				}else{
					update += union + "pred_biassub='false'";
					union = ", ";	
				}
				if(ri.isDarksub()){
					update += union + "pred_darksub='true'";
					union = ", ";
				}else{
					update += union + "pred_darksub='false'";
					union = ", ";
				}
				if(ri.isFlatfield()){
					update += union + "pred_flatfield='true'";
					union = ", ";
				}else{
					update += union + "pred_flatfield='false'";
					union = ", ";
				}
				if(ri.isPhotometric()){
					update += union + "pred_photometric='true'";
					union = ", ";
				}else{
					update += union + "pred_photometric='false'";
					union = ", ";
				}
				if(ri.isAstrometry()){
					update += union + "pred_astrometry='true'";
					union = ", ";
					if(ri.getAststatus()!=null && ri.getAststatus().length()>0){
						update += union + "pred_aststatus='"+ri.getAststatus()+"'";
					}
					if(ri.getAstprecision()!=null){
						update += union + "pred_astprecision='"+ri.getAstprecision()+"'";
					}
				}else{
					update += union + "pred_astrometry='false'";
					union = ", ";
				}
				
				//update += " where usr_id=(select usr_id from colecciondatos where col_bibcode='"+bib+"') and col_id=(select col_id from colecciondatos where col_bibcode='"+bib+"');" ;
				update += " where usr_id='"+ri.getUsr_id()+"' and col_id='"+ri.getCol_id()+"';" ;
				
				//System.out.println(update);
				Statement stm1 = conex.createStatement();
				stm1.executeUpdate(update);
				
				stm1.close();
			//}
		}
		
		public String[] infoProd(String bib) throws SQLException{
			
			String select = "select pub_prog, pub_obl, pub_prod from prod_pub, prodpub where prodpub.pub_id=prod_pub.pub_id and pub_bibcode='"+bib+"';";
				
			
			
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			int contador = 0;
			Vector aux = new Vector();
			while (resselTemp.next()) {
				
				 String Result = resselTemp.getString("pub_prog").trim()+"/"+resselTemp.getString("pub_obl").trim()+"/"+resselTemp.getInt("pub_prod");

				aux.addElement(Result);
				contador++;
			}
			
				
			stTemp.close();
			
			return  (String[])aux.toArray(new String[0]);
				
		}
		
		public String[] coleccion() throws SQLException{
			
			String select = "select distinct(pub_bibcode) from prodpub;";
				
			
			
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			int contador = 0;
			Vector aux = new Vector();
			while (resselTemp.next()) {
				
				 String Result = resselTemp.getString("pub_bibcode").trim();

				aux.addElement(Result);
				contador++;
			}
			
				
			stTemp.close();
			
			return  (String[])aux.toArray(new String[0]);
				
		}
		
		
		public String[] coleccionFecha(String ads) throws SQLException{
			
			String select = "select distinct(pub_bibcode),pub_date from prodpub";
				
			if(ads.equalsIgnoreCase("SI")){
				select = select + " where pub_ads = 'SI';";
			}else if(ads.equalsIgnoreCase("NO")){
				select = select + " where pub_ads != 'SI' or pub_ads is NULL;";
			}else{
				select = select + " ;";
			}
			
			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			Vector aux = new Vector();
			while (resselTemp.next()) {
				
				 String Result = resselTemp.getString("pub_bibcode").trim()+"-.-"+resselTemp.getDate("pub_date");

				aux.addElement(Result);
				
			}
			
				
			stTemp.close();
			
			return  (String[])aux.toArray(new String[0]);
				
		}
		
		//Obtenemos las fechas de agregación del bibcode
		public Date selectFecha(String bibcode) throws SQLException{
			
			String select = "select pub_date from prodpub where pub_bibcode='"+bibcode.trim()+"' order by pub_date desc limit 1;";
			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select); 

			Date Result= null;
			
			while (resselTemp.next()) {
				  Result = resselTemp.getDate("pub_date");
			}
				
			stTemp.close();
			
			return  Result;
			
		}
		
		//Cambiamos el estado del bibcode a si
		public void updateADS (String bibcode) throws SQLException{
			String update = "update prodpub set pub_ads='SI' where pub_bibcode='"+bibcode+"';" ;
			
			Statement stm = conex.createStatement();
			stm.executeUpdate(update);
			
			stm.close();
		}
		
		public void updateProg (String prog, String title, String user_id) throws SQLException{
			String update = "update programa set prog_title='"+title+"', prog_pi='"+user_id+"' where prog_id='"+prog+"';" ;
			
			Statement stm = conex.createStatement();
			stm.executeUpdate(update);
			
			stm.close();
		}
		public void insertProg (String prog, String title, String user_id) throws SQLException{
			
			String semester = null;
			String year = null;
			
			if(prog.endsWith("A")){
				semester = "A";
			}else if(prog.endsWith("B")){
				semester = "B";
			}
			
			if(prog.contains("-09")){
				year = "2009";
			}else if(prog.contains("-10")){
				year = "2010";
			}else if(prog.contains("-11")){
				year = "2011";
			}else if(prog.contains("-12")){
				year = "2012";
			}else if(prog.contains("-13")){
				year = "2013";
			}else if(prog.contains("-14")){
				year = "2014";
			}else if(prog.contains("-15")){
				year = "2015";
			}else if(prog.contains("-16")){
				year = "2016";
			}else if(prog.contains("-17")){
				year = "2017";
			}else if(prog.contains("-18")){
				year = "2018";
			}
			
			String insert1 = "insert into programa (prog_id, prog_title, prog_pi" ;
			String insert2 = ") values ('"+prog+"','"+title+"','"+user_id+"'";
			
			if(semester!=null){
				insert1 = insert1 + ", prog_semester";
				insert2 = insert2 + ",'"+semester+"'";
			}
			if(year!=null){
				insert1 = insert1 + ", prog_year";
				insert2 = insert2 + ",'"+year+"'";
			}
			
			String insert = insert1 + insert2 + ");";
			
			//System.out.println(insert);
			
			Statement stm = conex.createStatement();
			stm.executeUpdate(insert);
			
			stm.close();
		}
		
		public Integer isProg(String prog) throws SQLException{
			
			String select = "select count(*) from programa where prog_id='"+prog+"';";
				

			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			Integer Result= null;
			
			while (resselTemp.next()) {
				  Result = Integer.valueOf(resselTemp.getString(1));
			}
			
				
			stTemp.close();
			
			return  Result;
				
		}
		
		public String progName(String prog) throws SQLException{
			
			String select = "select prog_title from programa where prog_id='"+prog+"';";
				

			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			String Result= null;
			
			while (resselTemp.next()) {
				  Result = resselTemp.getString(1);
			}
			
				
			stTemp.close();
			
			return  Result;
				
		}
		public Integer tempRed(RedObj red) throws SQLException{

			String query = "select count(*) FROM proddatos prod WHERE ";
			
			String join = "";
			
			if(red.getProg()!=null && red.getProg().trim().length()>0){
				query += join+" prog_id = '"+StringEscapeUtils.escapeSql(red.getProg().trim())+"' "; 
				join="AND ";
			}
			
			if(red.getObl()!=null && red.getObl().trim().length()>0){
				query += join+" obl_id = '"+StringEscapeUtils.escapeSql(red.getObl().trim())+"' "; 
				join="AND ";
			}
			
			SimpleDateFormat sdf;
			if(red.getTipo()!= null && red.getTipo().equals("S")){
				sdf = new java.text.SimpleDateFormat("HH:mm:ss.S");
			}else{
				sdf = new java.text.SimpleDateFormat("HH:mm:ss.SSS");
			}
			
			
			if(red.getOpen()!=null){
				query += join+" date_trunc('milliseconds',cast(prod_initime as time))=date_trunc('milliseconds', time '"+sdf.format(red.getOpen())+"') ";
				join="AND ";
				//logger.debug("Buscando producto RAW: Opentime= "+sdf.format(opentime));
			}else{ 
				query += join+" 1=0 ";
				join="AND ";
			}
			if(red.getClose()!=null){
				query += join+" date_trunc('milliseconds',cast(prod_endtime as time))=date_trunc('milliseconds', time '"+sdf.format(red.getClose())+"') ";
				join="AND ";
				//logger.debug("Buscando producto RAW: Clostime= "+sdf.format(clostime));
			}else{ 
				query += join+" 1=0 ";
				join="AND ";
			}

			logger.info(query);

			ResultSet resset = conex.createStatement().executeQuery(query);

			Integer Result= null;
			
			while (resset.next()) {
				  Result = resset.getInt("count");
			}
			
				
			resset.close();
			
			return  Result;		
		}
		
		public ProdKey[] allProd(String prog) throws SQLException{
			
			String select = "select bpath_path,prod_path,prod_filename, prog_id, obl_id, prod_id from proddatos p, basepath b where p.bpath_id=b.bpath_id and (mod_id='BBI' or mod_id='TF') and subm_id='IMG'";
			
			
			if(prog.equals("a")){
				select += " order by prog_id, obl_id;";
			}else if(prog.equals("d")){
				select += " order by prog_id desc, obl_id desc;";
			}else{
				select += " and prog_id='"+prog+"';";
			}
				
			
			//System.out.println(select);
			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			int contador = 0;
			Vector aux = new Vector();
			while (resselTemp.next()) {
				
				 ProdKey Result = new ProdKey(resselTemp);

				aux.addElement(Result);
				contador++;
			}
			
				
			stTemp.close();
			
			return  (ProdKey[])aux.toArray(new ProdKey[0]);
				
		}
		
		public ProdKey[] allProdSpe() throws SQLException{
			
			String select = "select bpath_path,prod_path,prod_filename, prog_id, obl_id, prod_id, prod_ra, prod_de from proddatos p, basepath b where p.bpath_id=b.bpath_id and (mod_id!='BBI' and mod_id!='TF') and prod_point is null"
					+ " and (subm_id like '%SPE%' or subm_id like '%POL%' or subm_id like '%IMG%');";
				
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			int contador = 0;
			Vector aux = new Vector();
			while (resselTemp.next()) {
				
				 ProdKey Result = new ProdKey(resselTemp);

				aux.addElement(Result);
				contador++;
			}
			
				
			stTemp.close();
			
			return  (ProdKey[])aux.toArray(new ProdKey[0]);
				
		}
		
		//Cambiamos el keyword
		public void updateKey (String key, ProdKey prod) throws SQLException{
			String update = "update proddatos set prod_object='"+key+"' where prog_id='"+prod.getProg()+"' and obl_id='"+prod.getObl()+"'and prod_id='"+prod.getProd()+"';" ;
					
			Statement stm = conex.createStatement();
			stm.executeUpdate(update);
					
			stm.close();
		}
		
		//Cambiamos el keyword
		public void updatePol (Integer num, String key, ProdKey prod) throws SQLException{
			String word = null;
			if(num==1){
				word="prod_polig";
			}else if (num==2){
				word="prod_polig2";
			}else if (num==3){
				word="prod_point";
			}
			
			
			String update = "update proddatos set "+word+"="+key+" where prog_id='"+prod.getProg()+"' and obl_id='"+prod.getObl()+"'and prod_id='"+prod.getProd()+"'" ;
				
			//System.out.println(update);
			Statement stm = conex.createStatement();
			stm.executeUpdate(update);
					
			stm.close();
		}
		
		public void updateRev(String prog, String ob, String prode, String err_id) throws SQLException{
			String update = "update prode_error set prode_rev=1 where prog_id='"+prog+"' and obl_id='"+ob+"' and prode_id='"+prode+"' and err_id='"+err_id+"'" ;
			
			//System.out.println(update);
			Statement stm = conex.createStatement();
			stm.executeUpdate(update);
			
			stm.close();
		}
		
		public Integer numRed(String prog_id, String obl_id, Integer prod_id, String type) throws SQLException{
			
			String select = "select count(*) from pred_prod pp left join prodreducido p on p.pred_id=pp.pred_id where prog_id='"+prog_id+"' and obl_id='"+obl_id+"' and prod_id='"+prod_id+"'";
					
			if(type.equalsIgnoreCase("HERVE")){
				select += " and pred_type ='HERVE' ";
			}else if(type.equalsIgnoreCase("HIPER")){
				select += " and pred_type ='HiPER' ";
			}else if(type.equalsIgnoreCase("MEG")){
				select += " and pred_type ='MEG' ";
			}else{
				select += " and pred_type ='USER'";
			}
			

			//*********************************************************** 
			//*  Ejecución de la orden para extraer los valores         *
			//***********************************************************

			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			Integer Result= null;
			
			while (resselTemp.next()) {
				  Result = Integer.valueOf(resselTemp.getString(1));
			}
			
				
			stTemp.close();
			
			return  Result;
				
		}
		public String getPredS(String prog_id, String obl_id, Integer prod_id) throws SQLException{
			
			String select = "select p.pred_id from pred_prod pp left join prodreducido p on p.pred_id=pp.pred_id where prog_id='"+prog_id+"' and obl_id='"+obl_id+"' and prod_id='"+prod_id+"' and (pred_type ='HERVE' or pred_type='HiPER')";
			
			
			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			String Result= null;
			
			while (resselTemp.next()) {
				  Result = resselTemp.getString(1);
			}
			
				
			stTemp.close();
			
			return  Result;
				
		}
		
		public void insertUser (String name, String surname, String user_id, String user_mail) throws SQLException{
			
			String insert = "insert into usuario (usr_id, usr_name, usr_surname, usr_email, usr_passwd, usr_est) values ('"+user_id+"','"+name+"','"+surname+"','"+user_mail+"','e10adc3949ba59abbe56e057f20f883e',1)";
			
			//System.out.println(insert);
			
			Statement stm = conex.createStatement();
			stm.executeUpdate(insert);
			
			stm.close();
			
			String insert2= "insert into usuario_grupo (usr_id, grp_id) values ('"+user_id+"','IP')";
			Statement stm2 = conex.createStatement();
			stm2.executeUpdate(insert2);

			stm2.close();
		}
		
		public Integer isUser(String user_id) throws SQLException{
			
			String select = "select count(*) from usuario where usr_id='"+user_id+"'";
			
			
			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			Integer Result= null;
			
			while (resselTemp.next()) {
				  Result = resselTemp.getInt(1);
			}
			
				
			stTemp.close();
			
			return  Result;
				
		}
		public UsuarioDb[] infoPI() throws SQLException{
			
			//String select = "select * from usuario where usr_est=1";
			String select ="select * from usuario where usr_est=1 and (select (max(prod_initime)>now()-('1 year'::interval)) from proddatos where prog_id in (select prog_id from programa where prog_pi=usuario.usr_id));";
			
			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			ArrayList<UsuarioDb> al = new ArrayList<UsuarioDb>();
			
			while (resselTemp.next()) {
				 al.add(new UsuarioDb(resselTemp));
			}
	
			stTemp.close();
			
			UsuarioDb[] Result = new UsuarioDb[al.size()];
			Result = al.toArray(Result);
			
			return  Result;
				
		}
		public UsuarioDb infoPI(String id) throws SQLException{
			
			String select = "select * from usuario where usr_id='"+id+"'";
			
			
			
			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);
			
			UsuarioDb Result=null;
			
			while (resselTemp.next()) {
				 Result = new UsuarioDb(resselTemp);
			}
	
			stTemp.close();
			
			return  Result;
				
		}
		
		public void updateUsuario (String id, String pw) throws Exception{
			String update = "update usuario set usr_passwd='"+Encriptado.md5(pw)+"', usr_est=0 where usr_id='"+id+"'" ;
			
			Statement stm = conex.createStatement();
			stm.executeUpdate(update);
			
			stm.close();
		}
		
		public void addMegara(ObjMegara obj) throws SQLException{
			
			String insert1 = "INSERT INTO PIPMEGARA (PROG_ID, OBL_ID, PATH_DIR, FILENAME, INSMODE, VPH, BLCKUUID";
			String insert2 = ") VALUES ('"+obj.getPROG_ID()+"','"+obj.getOBL_ID()+"',"
					+ "'"+obj.getPATH_DIR()+"','"+obj.getFILENAME()+"','"+obj.getINSMODE()+"','"+obj.getVPH()+"','"+obj.getBLCKUUID()+"'";
			
			if(obj.getSENTEMP4()!=null){
				insert1 += ", SENTEMP4";
				insert2 += "','"+obj.getSENTEMP4();
			}
			
			if(obj.getSPECLAMP()!=null){
				insert1 += ", SPECLAMP";
				insert2 += ",'"+obj.getSENTEMP4()+"'";
			}
			
			String insert = insert1+insert2+")";
			//System.out.println(insert);

				Statement stm = conex.createStatement();
				//ResultSet resselTemp = stm.executeUpdate(insert);
				stm.executeUpdate(insert);
					
				stm.close();

		}
		
		public ObjMegara[] SelectGrupo(String prog_id, String obl_id) throws SQLException{
			
			String select = "select distinct(insmode), vph, blckuuid, prog_id, obl_id from pipmegara where prog_id='"+prog_id+"' and obl_id='"+obl_id+"' and path_dir='object'";

			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			ArrayList<ObjMegara> al = new ArrayList<ObjMegara>();
			
			while (resselTemp.next()) {
				 al.add(new ObjMegara(resselTemp));
			}
	
			stTemp.close();
			
			ObjMegara[] Result = new ObjMegara[al.size()];
			Result = al.toArray(Result);
			
			return  Result;
			
		}
		
		public String[] SelectBias(ObjMegara obj) throws SQLException{
			
			String select = "select filename from pipmegara where prog_id='"+obj.getPROG_ID()+"' and obl_id='"+obj.getOBL_ID()+"' and path_dir='bias'";

			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			ArrayList<String> al = new ArrayList<String>();
			
			while (resselTemp.next()) {
				 al.add(resselTemp.getString("filename"));
			}
	
			stTemp.close();
			
			String[] Result = new String[al.size()];
			Result = al.toArray(Result);
			
			return  Result;
			
		}
		
		public String[] SelectFlat(ObjMegara obj) throws SQLException{
			
			String select = "select filename from pipmegara where prog_id='"+obj.getPROG_ID()+"' and obl_id='"+obj.getOBL_ID()+"' and path_dir='flat' and vph='"+obj.getVPH()+"' and insmode='"+obj.getINSMODE()+"'";

			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			ArrayList<String> al = new ArrayList<String>();
			
			while (resselTemp.next()) {
				 al.add(resselTemp.getString("filename"));
			}
	
			stTemp.close();
			
			String[] Result = new String[al.size()];
			Result = al.toArray(Result);
			
			return  Result;
			
		}
		
		public String[] SelectArc(ObjMegara obj) throws SQLException{
			
			String select = "select filename from pipmegara where prog_id='"+obj.getPROG_ID()+"' and obl_id='"+obj.getOBL_ID()+"' and path_dir='arc' and vph='"+obj.getVPH()+"' and insmode='"+obj.getINSMODE()+"'";

			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			ArrayList<String> al = new ArrayList<String>();
			
			while (resselTemp.next()) {
				 al.add(resselTemp.getString("filename"));
			}
	
			stTemp.close();
			
			String[] Result = new String[al.size()];
			Result = al.toArray(Result);
			
			return  Result;
			
		}
		public String[] SelectStds(ObjMegara obj) throws SQLException{
			
			String select = "select filename from pipmegara where prog_id='"+obj.getPROG_ID()+"' and obl_id='"+obj.getOBL_ID()+"' and path_dir='stds' and vph='"+obj.getVPH()+"' and insmode='"+obj.getINSMODE()+"'";

			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			ArrayList<String> al = new ArrayList<String>();
			
			while (resselTemp.next()) {
				 al.add(resselTemp.getString("filename"));
			}
	
			stTemp.close();
			
			String[] Result = new String[al.size()];
			Result = al.toArray(Result);
			
			return  Result;
			
		}
		
		public String[] SelectSci(ObjMegara obj) throws SQLException{
			
			String select = "select filename from pipmegara where prog_id='"+obj.getPROG_ID()+"' and obl_id='"+obj.getOBL_ID()+"' and path_dir='object' and vph='"+obj.getVPH()+"' and insmode='"+obj.getINSMODE()+"' and blckuuid='"+obj.getBLCKUUID()+"'";

			Statement stTemp = conex.createStatement();
			ResultSet resselTemp = stTemp.executeQuery(select);

			ArrayList<String> al = new ArrayList<String>();
			
			while (resselTemp.next()) {
				 al.add(resselTemp.getString("filename"));
			}
	
			stTemp.close();
			
			String[] Result = new String[al.size()];
			Result = al.toArray(Result);
			
			return  Result;
			
		}
}


