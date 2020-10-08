<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page contentType="text/xml;charset=ISO-8859-1" %>
<%@page import="java.io.*, 	
				java.util.*,
				java.sql.*,
				svo.gtc.ssap.VOObject, 
				java.net.URLEncoder" %>
				
<%
VOObject parametrosFormulario = null;

try{

	parametrosFormulario = new VOObject(request);

}catch(Exception e){

	String cod=null;
	
	try{
	switch(Integer.valueOf(e.getMessage())){
	
	case 1:
		cod="There are not values";
		break;
	case 2:
		cod="It is a wrong value for position (POS)";
	break;
	case 3:
		cod="It is no value for size";
	break;
	case 4:
		cod="Incorret value for BAND";
	break;
	case 5:
		cod="Incorret value for TIME";
	break;
	case 6:
		cod="It is a wrong value for size";
	break;
	case 7:
		cod="Gtc_id is not an integer value";
	break;
	default:
		cod="";
	break;
	}
	}catch(Exception e1){
		e.printStackTrace();
		cod = "No value for some element";
	}	
%>
	<VOTABLE version="1.1" xmlns="http://www.ivoa.net/xml/VOTable/v1.1"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://www.ivoa.net/xml/VOTable/v1.1 http://www.ivoa.net/xml/VOTable/v1.1" xmlns:sed="http://www.ivoa.net/xml/SedModel/v1.0">
	<DEFINITIONS>
	  <COOSYS equinox="2000" />
	  </DEFINITIONS>
	  <RESOURCE type="results" ID="GTC">
		<DESCRIPTION>
			GTC VO Archives
		</DESCRIPTION>
		<INFO name="QUERY_STATUS" value="ERROR">
		<DESCRIPTION> Please, set search conditions: <%=cod%></DESCRIPTION>
		</INFO>
	</RESOURCE>
	</VOTABLE>
<%}
%>