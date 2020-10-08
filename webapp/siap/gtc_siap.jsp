<?xml version="1.0" encoding="ISO-8859-1" ?><%@ page contentType="text/xml;content=x-votable" %>
<%@page import="java.io.*, 	
				java.util.*,
				java.sql.*,
				java.net.URLEncoder, 
				java.text.DecimalFormat,
				svo.gtc.db.DriverBD,
				svo.gtc.siap.VOObject,
				java.util.Date,
				svo.gtc.siap.ConsultaSiap,
				svo.gtc.siap.ResultSiap,
				svo.gtc.db.logquery.LogQueryAccess,
				svo.gtc.db.logquery.LogQueryDb" %>
	  
	  <%
	  
	  Connection con = null;
try {

VOObject parametrosFormulario = null;
 
try{
	parametrosFormulario = new VOObject(request);
}catch(Exception e){
	//System.out.println("error"+e.getMessage());
	%> <jsp:forward page="error.jsp?cod=<%=e.getMessage()%>"/> 
	<%
}

if (parametrosFormulario.getFormat().equalsIgnoreCase("METADATA")){
	%> <jsp:forward page="metadata.jsp"/> 
	<% 
}
DriverBD drive = new  DriverBD();
con = drive.bdConexion();
//Valores de la sesi�n
String serverName= request.getServerName();
int port = request.getServerPort();
String path = request.getContextPath();

//*************************************************************
//*  ESTADISTICAS                                             *
//*************************************************************
	LogQueryDb logQuery = new LogQueryDb();
	logQuery.setLogqTime(new Timestamp((new Date()).getTime()));
	logQuery.setLogqHost(request.getRemoteHost());
	logQuery.setLogqType("SIAP");
	LogQueryAccess logqAccess = new LogQueryAccess(con);
	logqAccess.insert(logQuery);

	ConsultaSiap union = null;
	try{
		union = new ConsultaSiap(con, parametrosFormulario, "user");
	}catch(Exception e){
		System.out.println(e.getMessage());
	}
%>
	<VOTABLE version="1.1" xmlns="http://www.ivoa.net/xml/VOTable/v1.1"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://www.ivoa.net/xml/VOTable/v1.1 http://www.ivoa.net/xml/VOTable/v1.1" xmlns:sed="http://www.ivoa.net/xml/SedModel/v1.0">
	<RESOURCE id="gtc" type="results">
	<DESCRIPTION>GTC user reduced data Simple Image Access Service</DESCRIPTION>
	
	<INFO name="QUERY_STATUS" value="OK"/>
	
	<PARAM name="INPUT:POS" value="<%=parametrosFormulario.getRa()%>,<%=parametrosFormulario.getDec()%>" datatype="double">
		<DESCRIPTION> Search Position in the form "ra, dec" where ra and dec are given in decimal degrees in the ICRS coordinate system </DESCRIPTION>
	</PARAM>
	
	<PARAM name="INPUT:SIZE" value="<%=parametrosFormulario.getSr()%>" datatype="double">
		<DESCRIPTION>Size of search region in the RA and Dec. directions </DESCRIPTION>
	</PARAM>
	
	<PARAM name="INPUT:OBS_ID" value="" utype="sia:dataid.obsid" datatype="char" arraysize="*" ucd="OBS_ID">
	<DESCRIPTION>Image observation id</DESCRIPTION>
	</PARAM>
	
	<PARAM name="INPUT:FORMAT" value="ALL" datatype="char" arraysize="*">
		<DESCRIPTION>Request format of image</DESCRIPTION>
		<VALUES>
			<OPTION value="image/fits"/>
		</VALUES>
	</PARAM>
	
	<PARAM name="INPUT:INTERSECT" value="OVERLAPS" datatype="char" arraysize="*">
		<DESCRIPTION>Choice of overlap with requested region </DESCRIPTION>
	</PARAM>
	
	<PARAM name="INPUT:VERB" value="1" datatype="int">
		<DESCRIPTION>Verbosity level, controlling the number of columns returned </DESCRIPTION>
	</PARAM>
	
	<PARAM name="INPUT:TELESCOPE" datatype="char" arraysize="*" value="GTC">
		<DESCRIPTION>Telescope name</DESCRIPTION>
	</PARAM>
	
	<%if(parametrosFormulario.getInstrument().equalsIgnoreCase("OSIRIS")){
	%>
	<PARAM name="INPUT:INSTRUMENT" datatype="char" arraysize="*" value="OSIRIS">
		<DESCRIPTION>Instrument name</DESCRIPTION>

	</PARAM>
	<%
	}else if(parametrosFormulario.getInstrument().equalsIgnoreCase("CANARICAM")){
	%>
	<PARAM name="INPUT:INSTRUMENT" datatype="char" arraysize="*" value="CANARICAM">
		<DESCRIPTION>Instrument name</DESCRIPTION>

	</PARAM>
	<%
	}else if(parametrosFormulario.getInstrument().equalsIgnoreCase("EMIR")){
	%>
	<PARAM name="INPUT:INSTRUMENT" datatype="char" arraysize="*" value="EMIR">
		<DESCRIPTION>Instrument name</DESCRIPTION>

	</PARAM>
	<%
	}else if(parametrosFormulario.getInstrument().equalsIgnoreCase("CIRCE")){
	%>
	<PARAM name="INPUT:INSTRUMENT" datatype="char" arraysize="*" value="CIRCE">
		<DESCRIPTION>Instrument name</DESCRIPTION>

	</PARAM>
	<%
	}else if(parametrosFormulario.getInstrument().equalsIgnoreCase("HIPERCAM")){
		%>
		<PARAM name="INPUT:INSTRUMENT" datatype="char" arraysize="*" value="HIPERCAM">
			<DESCRIPTION>Instrument name</DESCRIPTION>

		</PARAM>
		<%
	}else{
	%>
	<PARAM name="INPUT:INSTRUMENT" datatype="char" arraysize="*" value="ALL">
		<DESCRIPTION>Instrument name</DESCRIPTION>
		<VALUES>
			<OPTION value="OSIRIS"/>
			<OPTION value="CANARICAM"/>
			<OPTION value="EMIR"/>
			<OPTION value="CIRCE"/>
			<OPTION value="HIPERCAM"/>
		</VALUES>
	</PARAM>
	<%}%>

		<TABLE>
		<DESCRIPTION>  
			Result showed in VOTable format.
		</DESCRIPTION>
	
		<FIELD name="Title" utype="sia:dataid.title" ucd="VOX:Image_Title" datatype="char" arraysize="*">
		<DESCRIPTION> Image Title</DESCRIPTION>
		</FIELD>

		<FIELD name="Observation_ID"  utype="sia:dataid.obsid" ucd="OBS_ID" datatype="char" arraysize="*">
		<DESCRIPTION> Observation Id</DESCRIPTION>
		</FIELD>

		<FIELD name="Reference" utype="sia:access.reference" ucd="VOX:Image_AccessReference" datatype="char" arraysize="*">
		<DESCRIPTION> Link of data </DESCRIPTION>
		</FIELD>
		
		<FIELD name="Format" utype="sia:access.format" datatype="char" arraysize="*" ucd="VOX:Image_Format">
		<DESCRIPTION>MIME type of the image</DESCRIPTION>
		</FIELD>
		
		<FIELD name="RA" utype="sia:char.spatialaxis.coverage.location.value.ra" ucd="POS_EQ_RA_MAIN" unit="deg" datatype="double">
		<DESCRIPTION> Image center Right Ascension </DESCRIPTION>
		</FIELD>
		
		<FIELD name="Dec" utype="sia:char.spatialaxis.coverage.location.value.dec" ucd="POS_EQ_DEC_MAIN" unit="deg" datatype="double">
		<DESCRIPTION> Image center Declination </DESCRIPTION>
		</FIELD>
		
		<FIELD name="Telescope" utype="sia:dataid.telescope" ucd="instr.tel" datatype="char" arraysize="*">
		<DESCRIPTION> Telescope name </DESCRIPTION>
		</FIELD>
		
		<FIELD name="Instrument" utype="sia:dataid.instrument" ucd="INST_ID" datatype="char" arraysize="*">
		<DESCRIPTION> Instrument name</DESCRIPTION>
		</FIELD>
		
		<FIELD name="Naxes" utype="sia:image.naxes" ucd="VOX:Image_Naxes" datatype="int">
		<DESCRIPTION> Number of image axes </DESCRIPTION>
		</FIELD>
		
		<FIELD name="Naxis" utype="sia:image.naxis" ucd="VOX:Image_Naxis" datatype="int" arraysize="*">
		<DESCRIPTION> Length of image axis </DESCRIPTION>
		</FIELD>
		
		<FIELD name="Scale" ucd="VOX:Image_Scale" datatype="double" arraysize="*">
		<DESCRIPTION> Scale </DESCRIPTION>
		</FIELD>
		
		<FIELD name="FileSize" utype="sia:access.size" datatype="int" unit="bytes" ucd="VOX:Image_FileSize">
		<DESCRIPTION>Image file size</DESCRIPTION>
		</FIELD>
		
		<DATA>
		<TABLEDATA>
		<%
		ResultSiap resultado=null;
		while( (resultado=union.getNext())!=null ){

	try{	
		
	%>		
				<TR>
					<TD>GTC Image <%= resultado.getPredId().intValue() %></TD>
					<TD><%= resultado.getPredId().intValue() %></TD>
					
					<%
					String linkred= "http://"+serverName+":"+port+path+"/servlet/FetchProd?pred_id="+resultado.getPredId().intValue()+"&fetch_type=VO_PRED";
					%><%
					String dl = "http://"+serverName+":"+port+path+"/siap/dataLinkGTC.jsp?uri="+resultado.getPredId().intValue();
					 %>
					<TD><![CDATA[<%=linkred%>]]></TD>
					<TD>application/xml+votable</TD>
					<TD><%= resultado.getFormatedPredRa() %></TD>
					<TD><%= resultado.getFormatedPredDe() %></TD>
					<TD>GTC</TD>
					<TD><%= resultado.getInsName() %></TD>
					
					
					<TD>N/A</TD>
					<TD>N/A</TD>
					<TD>N/A</TD>
					<TD>N/A</TD>
			
				</TR>
	<%

	}catch(Exception e){
		e.printStackTrace();
	}
	}
	
		con.close();
	%>
	</TABLEDATA>
	</DATA>	
	</TABLE>
	</RESOURCE>
	</VOTABLE>
	<%
	}catch(Exception e){
		con.close();
		%> <jsp:forward page="error.jsp"/> 
		<%
}%>