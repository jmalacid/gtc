<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page contentType="text/xml;charset=ISO-8859-1" %>


<%
String node = null;
try{
	node = request.getParameter("uri");
}catch(Exception e){
	
}
%>
	<VOTABLE version="1.2"
    xmlns="http://www.ivoa.net/xml/VOTable/v1.2"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.ivoa.net/xml/VOTable/v1.2 http://www.ivoa.net/xml/VOTable/v1.2">

	  <RESOURCE type="result" ID="GTC">
		<DESCRIPTION>
			GTC VO Archives
		</DESCRIPTION>
		<INFO name="QUERY_STATUS" value="ERROR">
		<%if(node!=null){ %>
			<DESCRIPTION> No datalink available por node <%=node%></DESCRIPTION>
		<%} %>
		</INFO>
		<INFO name="SERVICE_PROTOCOL" value="1.0">DataLink</INFO>
		<INFO name="REQUEST" value="queryData"/>
		<INFO name="baseUrl" value="http://gtc.sdc.cab.inta-csic.es/gtc/siap/dataLinkGTC.jsp"/>
		<INFO name="serviceVersion" value="0.1"/>
	</RESOURCE>
	</VOTABLE>
