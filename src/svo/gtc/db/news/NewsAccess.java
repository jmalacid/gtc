/*
 * @(#)InstrumentAccess.java    Feb 17, 2011
 *
 *
 * Ra�l Guti�rrez S�nchez. (raul@cab.inta-csic.es)	
 * LAEFF: 	Laboratorio de Astrof�sica Espacial y F�sica Fundamental.
 *        	INTA
 *			Villafranca del Castillo Satellite Tracking Station
 *			Villafranca del Castillo, E-28691 Villanueva de la Ca�ada
 *			Madrid - Espa�a
 *			
 * Phone:  34 91-8131260
 * Fax..:  34 91-8131160   
 * -----------------------------------------------------------------------
 * 
 *	ESTE  PROGRAMA  ES  DE  USO EXCLUSIVO.  ES  PROPIEDAD  DE  SU AUTOR  Ra�l 
 *  Guti�rrez S�nchez  Y SE PRESENTA "COMO ESTA" SIN NINGUN TIPO DE GARANTIAS
 *  DENTRO Y FUERA  DE LA FINALIDAD PARA  LA QUE EL  AUTOR LO  HIZO. EL AUTOR 
 *  PERMITE  SU USO SIEMPRE QUE  SE LE REFERENCIE. SE PERMITE LA MODIFICACION
 *  DE SU  FUENTE SIEMPRE QUE SE  HAGA REFERENCIA A SU AUTORIA Y JUNTO A ELLA
 *  SE  ADJUNTE LA IDENTIFICACION DEL  AUTOR DE LAS NUEVAS MODIFICACIONES. EL
 *  AUTOR  NUNCA SERA RESPONSABLE DEL USO DE ESTE  PROGRAMA NI DE LOS EFECTOS
 *  QUE ESTE PROGRAMA PUDIEDA CAUSAR EN DETERIOROS, ALTERACIONES Y/O PERDIDAS 
 *  DE INFORMACION.
 *	
 */

package svo.gtc.db.news;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

public class NewsAccess {

	Connection con = null;
	//SELECT
	private PreparedStatement pstSel;
	private PreparedStatement pstSelRecent;
	private PreparedStatement pstInsert;
	
	
	
	private String select=
		" SELECT news_date, news_type, news_phrase, news_desc " +
		" 	FROM news ";

	private String insert=
		" INSERT INTO news (news_date, news_type, news_phrase, news_desc) " +
		" 			VALUES(?,?,?,?) ";

	public NewsAccess(Connection conex) throws SQLException{
		con = conex;
		//Ordenes Precompiladas --------------------------------------------------
		pstSel 		= conex.prepareStatement(select+" ORDER BY news_date DESC ");
		pstSelRecent = conex.prepareStatement(select+" WHERE age(news_date)<'30 days' ORDER BY news_date DESC ");
		pstInsert 	= conex.prepareStatement(insert);
	}
	
	public NewsDb[] select() throws SQLException{
		Vector<NewsDb> aux = new Vector<NewsDb>();
		
		ResultSet resset = pstSel.executeQuery();

		while(resset.next()){
			aux.add(new NewsDb(resset));
		}
		
		return (NewsDb[])aux.toArray(new NewsDb[0]);
	}

	public NewsDb[] selectRecent() throws SQLException{
		Vector<NewsDb> aux = new Vector<NewsDb>();
		
		ResultSet resset = pstSelRecent.executeQuery();

		while(resset.next()){
			aux.add(new NewsDb(resset));
		}
		
		return (NewsDb[])aux.toArray(new NewsDb[0]);
	}

	public void insert(NewsDb news) throws SQLException{
		pstInsert.setDate(1, news.getNewsDate());
		pstInsert.setString(2, news.getNewsType());
		pstInsert.setString(3, news.getNewsPhrase());
		
		// Desc
		if(news.getNewsDesc()!=null && news.getNewsDesc().trim().length()>0){
			pstInsert.setString(4, news.getNewsDesc().trim());
		}else{
			pstInsert.setNull(4, Types.CHAR);
		}

		pstInsert.execute();
	}
		
	
}
