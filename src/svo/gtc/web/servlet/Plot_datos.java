package svo.gtc.web.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import svo.gtc.adm.DatFecha;
import svo.gtc.adm.DatHistogram;
import svo.gtc.adm.DatQuery;
import svo.gtc.db.DriverBD;
//import svo.pdd.tess.db.SearchDB;
//import svo.pdd.tess.obj.IngObj;

public class Plot_datos  extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		

		DriverBD drive = null;
		Connection cn = null;

		try{
			DatFecha fi = new DatFecha(request.getParameter("mi"),request.getParameter("yi"));
			DatFecha fe = new DatFecha(request.getParameter("me"),request.getParameter("ye"));
			
			drive = new  DriverBD();
			cn = drive.bdConexion();
			DatQuery query = new DatQuery(cn);
			DatHistogram[] datos = query.viewDat(fi.fechaini(), fe.fechaend());
			
			//Creamos el plot
			response.setContentType ("image/png");
			
			if(request.getParameter("format")!=null && request.getParameter("format").equals("dow")){
				response.setHeader("Content-Disposition", "attachment;filename=gtc_hist.png");
			}else{
				response.setHeader("Content-Disposition", "inline;filename=gtc_hist.png");
			}
			
			DefaultCategoryDataset dataset = new DefaultCategoryDataset( ); 
			
			
			for(int i=0; i<datos.length; i++){
				
				dataset.addValue( datos[i].getNum() , "ficheros" ,datos[i].getFecha() );
				
			}
			
			
			JFreeChart chart = ChartFactory.createBarChart(
			         "Histograma datos GTC",           
			         "meses",            
			         "nº ficheros",            
			         dataset,          
			         PlotOrientation.VERTICAL,           
			         true, true, false);
			CategoryPlot plot = chart.getCategoryPlot();
			CategoryAxis domainAxis = plot.getDomainAxis();
	        domainAxis.setCategoryLabelPositions(
	            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
	        );
			        ChartUtilities.writeChartAsPNG(response.getOutputStream(), chart, 640, 480);
			    	
					response.getOutputStream().flush();
					response.getOutputStream().close();
					
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}finally{
			if(cn != null){
				try{
					cn.close();
				}catch(SQLException ex){
					System.out.println("Error: "+ex.getMessage());
				}
			}
		}
	}
	
	/**
	 * @throws SQLException
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}


}
