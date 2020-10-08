<%@ page import="svo.gtc.db.priv.DBPrivate,
				java.sql.*,
				
				javax.sql.*,
				javax.naming.Context,
				javax.naming.InitialContext,
				javax.naming.NamingException" %>
<%
response.setContentType ("text/plain");

//Valores de la sesión
String serverName= request.getServerName();
int port = request.getServerPort();
String path = request.getContextPath();

//Abrimos la conexión a la base de datos
Context initContext;
Connection cn=null;

try{
initContext = new InitialContext();
DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/gtc");
cn = ds.getConnection();

DBPrivate union = new DBPrivate(cn);
String[] info = union.coleccion(); 

for(int i =0; i<info.length; i++){
	
	out.print(info[i]+"	http://"+serverName+":"+port+path+"/jsp/BibResult.jsp?bib="+info[i].replaceAll("&", "%26")+"\n");
	
}
}catch(SQLException ex){
	//fallo sql
	System.out.println("Error : "+ex.getMessage());
}catch(NamingException ex){
	System.out.println("Error al intentar obtener el DataSource:"+ex.getMessage());
}finally{
	if(cn != null){
		try{
			cn.close();
		}catch(SQLException ex){
			System.out.println("Error: "+ex.getMessage());
		}
	}
}
%>