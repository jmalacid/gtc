<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page contentType="text/xml;content=x-votable" %>
<%@page import="java.io.*, 	
				java.util.*,
				java.sql.*,
				java.net.URLEncoder" %>
	<VOTABLE version="1.1" xmlns="http://www.ivoa.net/xml/VOTable/v1.1"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://www.ivoa.net/xml/VOTable/v1.1 http://www.ivoa.net/xml/VOTable/v1.1" xmlns:sed="http://www.ivoa.net/xml/SedModel/v1.0">
	
	<RESOURCE id="gtc" type="results">
	<INFO  name="QUERY_STATUS" value="OK"></INFO>
	<DESCRIPTION>GTC OSIRIS Simple Image Access Service</DESCRIPTION>
	
	<PARAM name="INPUT:POS" value="0,0" datatype="double">
		<DESCRIPTION> Search Position in the form "ra, dec" where ra and dec are given in decimal degrees in the ICRS coordinate system </DESCRIPTION>
	</PARAM>
	
	<PARAM name="INPUT:SIZE" value="0" datatype="double">
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
	
	<PARAM name="INPUT:TELESCOPE" datatype="char" arraysize="*" value="">
		<DESCRIPTION>Telescope name</DESCRIPTION>
		<VALUES>
			<OPTION value="GTC"/>
		</VALUES>
	</PARAM>

	<PARAM name="INPUT:INSTRUMENT" datatype="char" arraysize="*" value="">
		<DESCRIPTION>Instrument name</DESCRIPTION>
		<VALUES>
			<OPTION value="OSIRIS"/>
		</VALUES>
	</PARAM>
	<PARAM name="SCALE" ucd="VOX:Image_Scale" datatype="double" arraysize="*" value="">
		<DESCRIPTION>Image Scale</DESCRIPTION>

	</PARAM>
	<TABLE>
		<DESCRIPTION>  
			Result showed in VOTable format.
		</DESCRIPTION>
	
		<FIELD name="Title" utype="sia:dataid.title" ucd="VOX:Image_Title" datatype="char" arraysize="*">
		<DESCRIPTION> Image Title</DESCRIPTION>
		</FIELD>

		<FIELD name="Observation_ID"  utype="sia:dataid.obsid" ucd="OBS_ID" datatype="char" arraysize="*">
		<DESCRIPTION> Obsesrvation Id</DESCRIPTION>
		</FIELD>

		<FIELD name="Reference" utype="sia:access.reference" ucd="VOX:Image_AccessReference" datatype="char" arraysize="*">
		<DESCRIPTION> List of datalinks </DESCRIPTION>
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
		
		
		<FIELD name="FileSize" utype="sia:access.size" datatype="int" unit="bytes" ucd="VOX:Image_FileSize">
		<DESCRIPTION>Image file size</DESCRIPTION>
		</FIELD>
		
	</TABLE>
	</RESOURCE>
	</VOTABLE>