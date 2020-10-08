<?xml version="1.0" encoding="ISO-8859-1" ?><%@ page contentType="text/xml;content=x-votable" %>
<%@page import="java.io.*, 	
				java.util.*,
				java.sql.*,
				java.net.URLEncoder, 
				java.text.DecimalFormat,
				svo.gtc.db.DriverBD,
				java.util.Date,
				svo.gtc.siap.Prod,
				svo.gtc.siap.ConsultaDL" %>
	  
	  <%
try {

String DID = null;
 
try{
	DID = request.getParameter("uri");
	
	if(DID.equalsIgnoreCase(null) || !(DID.length()>0)){
		%> <jsp:forward page="errordl.jsp"/> 
		<%
	}
}catch(Exception e){
	
	%> <jsp:forward page="errordl.jsp"/> 
	<%e.printStackTrace();
}

DriverBD drive = new  DriverBD();
Connection con = drive.bdConexion();
//Valores de la sesión
String serverName= request.getServerName();
int port = request.getServerPort();
String path = request.getContextPath();

	ConsultaDL union =new ConsultaDL(con);

	Prod[] raws = union.getCountRaw(Integer.valueOf(DID));
	Integer size = union.getSize(Integer.valueOf(DID));
	String bib = union.getBib(Integer.valueOf(DID)).trim();
	
	String pub_did= "http://"+serverName+":"+port+path+"/siap/gtc_siap.jsp?obs_id="+DID;
	%>	
	<VOTABLE version="1.2"
    xmlns="http://www.ivoa.net/xml/VOTable/v1.2"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.ivoa.net/xml/VOTable/v1.2 http://www.ivoa.net/xml/VOTable/v1.2">
	<RESOURCE type="results">
	<DESCRIPTION>GTC DataLink Service</DESCRIPTION>
	
	<INFO name="QUERY_STATUS" value="OK"/>
	
		<TABLE name="DataLinks">
		<DESCRIPTION>  
			Result showed in VOTable format.
		</DESCRIPTION>
		
		<FIELD name="publisher_did" utype="dl:Dataid.ObservationID" ucd="meta.ref.url;meta.curation" datatype="char" arraysize="*">
		<DESCRIPTION> Identifier of the observation </DESCRIPTION>
		</FIELD>
		
		<FIELD name="identifier" utype="dl:Semantics" ucd="" datatype="char" arraysize="*">
		<DESCRIPTION> Name of the link </DESCRIPTION>
		</FIELD>
		
		<FIELD name="servicetype" utype="dl:Votype" ucd="" datatype="char" arraysize="*">
		<DESCRIPTION> Type of the service </DESCRIPTION>
		</FIELD>
		
		<FIELD name="format" utype="dl:Access.Format" ucd="meta.id" datatype="char" arraysize="*">
		<DESCRIPTION> Format </DESCRIPTION>
		</FIELD>
		
		<FIELD name="size" utype="dl:Access.Size" ucd="phys.meta;meta.file" datatype="integer" arraysize="">
		<DESCRIPTION> Estimate size of the response </DESCRIPTION>
		</FIELD>
		
		<FIELD name="access" utype="dl:Access.Reference" ucd="meta.ref.url" datatype="char" arraysize="*">
		<DESCRIPTION> URL returning the content of the targeted link </DESCRIPTION>
		</FIELD>
		
		<FIELD name="description" utype="" ucd="meta.note" datatype="char" arraysize="*">
		<DESCRIPTION> Description of the service </DESCRIPTION>
		</FIELD>
	
		<DATA>
			<TABLEDATA>
		<%if(raws.length>0){%>
			<TR>
			<TD><![CDATA["<%=pub_did%>"]]></TD>
			<TD> Reduced Data <%=DID%></TD>
			<TD>DAL:SIA</TD>
			<TD>image/fits</TD>
			<TD><%=size%></TD>
			<%
			String linkred= "http://"+serverName+":"+port+path+"/servlet/FetchProd?pred_id="+DID;
			%>
			<TD><![CDATA["<%=linkred%>"]]></TD>
			<TD> Reduced Data </TD>
			</TR>
	
		<%if(bib!= null && !bib.equalsIgnoreCase("CANARICAM")){
			String linkADS = "http://adsabs.harvard.edu/abs/"+bib.trim();
			%>
				
			<TR>
			<TD><![CDATA["<%=pub_did%>"]]></TD>
			<TD> ADS Journal</TD>
			<TD>n/a</TD>
			<TD>webservice</TD>
			<TD>n/a</TD>
			<TD><![CDATA["<%=linkADS%>"]]></TD>
			<TD> Link to the related ADS Journal </TD>
			</TR>

		<%	}%>

	<%for(int i = 0; i<raws.length;i++){ 
		String[] val_raw = union.getIns_id(raws[i].getProg(),raws[i].getObl(), raws[i].getProd()).split(".__.");
	%>
			<TR>
			<TD><![CDATA["<%=pub_did%>"]]></TD>
			<TD> Raw Data </TD>
			<TD>DAL:SIA</TD>
			<TD>image/fits</TD>
			<TD><%=val_raw[1]%></TD>
		<%String linkraw= "http://"+serverName+":"+port+path+"/servlet/FetchProd?prod_id="+raws[i].getProg()+".."+raws[i].getObl()+".."+raws[i].getProd();%>
			<TD><![CDATA["<%=linkraw%>"]]></TD>
			<TD> Product <%=raws[i].getProd()%>, program: <%=raws[i].getProg()%>, observing block:<%=raws[i].getObl()%> </TD>
			</TR>
		<%
		
		Integer cal = union.getCountCal(raws[i].getProg(), raws[i].getObl());
		
		if(cal>0){//Averiguar el ins_id
			//String ins_id = union.getIns_id(raws[i].getProg(),raws[i].getObl(), raws[i].getProd());
			String linkcal= "http://"+serverName+":"+port+path+"/jsp/fetchDataBlock.jsp?datablock="+raws[i].getProg()+"__"+raws[i].getObl()+"__"+val_raw[0]+"__ALLCAL";
			%>
		
			<TR>
			<TD><![CDATA["<%=pub_did%>"]]></TD>
			<TD> Calibration Data </TD>
			<TD>DAL:SIA</TD>
			<TD>archive/tar</TD>
			<TD>n/a</TD>
			<TD><![CDATA["<%=linkcal%>"]]></TD>
			<TD> Calibration data related to the product <%=raws[i].getProd()%>, program: <%=raws[i].getProg()%>, observing block:<%=raws[i].getObl()%>  </TD>
			</TR>
		
	<%	}
	}
	}
	
	con.close();%>
	</TABLEDATA>
	</DATA>	
	</TABLE>
	</RESOURCE>
	</VOTABLE>
	<%
	}catch(Exception e){
		e.printStackTrace();
		%> <jsp:forward page="errordl.jsp"/> 
		<%
}%>