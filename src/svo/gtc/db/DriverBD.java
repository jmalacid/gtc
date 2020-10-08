package svo.gtc.db;

import java.sql.*;

/**
 *  
 * Devuelve los drivers y los componentes de conexion correspondientes a las 
 * diferentes marcas (o motores) de Bases de Datos. Y permite establecer la 
 * conexi�n con cualquiera de ellas.
 *  
 *  <UL>Los driver soportado mas usuales son: 
 *      <LI>[MySQL]
 *      <LI>[PostgreSQL]
 *      <LI>[MsAccess]
 *      <LI>[Oracle]
 *      <LI>[Sybase]
 *  </UL>
 *  <BR>
 *  <UL>Los componentes son:
 *      <LI>"host"   - [localhost], o dominio asignado (laeff.cab.inta-csic.es).
 *      <LI>"puerto" - Numero de puerto por el que se comunica la base de
 *                     datos. (MySql-3306, PostgreSQL-5432, ORACLE-1521, ..etc.).
 *  </UL> 
 *  
 * <BR>
 * <STRONG>NOTA:<STRONG> Al cambiar de entorno solamente es necesario modificar 
 * estos parametros adecuandolos al nuevo sistema o al nuevo motor de la base de
 * datos, y COMPILAR esra clase. Co esto EL RESTO DE LA APLICACION FUNCIONARA 
 * SIN TENER QUE SER RECOMPILADA O MODIFICADA.
 *
 *  &copy; 
 *  2000-19-03 Driver: MySQL.
 * @author 
 * @version 0.0, 19/03/2000
 */		
public class DriverBD {
	
  //public static String marcaBd = "org.gjt.mm.mysql.Driver";            // MySQL
  public static String marcaBd = "org.postgresql.Driver";            // PostgreSQL
  //public static String marcaBd = "sun.jdbc.odbc.JdbcOdbcDriver";     // Microsoft Access
  //public static String marcaBd = "oracle.jdbc.driver.OracleDriver";  // ORACLE    
  //public static String marcaBd = "com.sybase.jdbc.SybDriver";        // SYBASE    
	
  //public static String host  = "localhost";  
  //public static String host  = "heisenberg";  
  public static String host  = "heisenberg-dev";
  //public static String host  = "eric";

  
  //public static String port  = "3306";  //Puerto standard utilizado por MySQL
  public static String port  = "5432";  //Puerto standard utilizado por PostgreSQL
  //public static String port  = "1521";  //Puerto standard utilizado por ORACLE
  //public static String port  = "1521";  //Puerto standard utilizado por SYBASE

  String dbname = "gtc";
  String userid = "jdbc";//heisenberg-dev
  //String userid = "jdbcgtcweb";//eric
  String passwd = "h$ZUzY$1"; //misma contraseña heisenberg-dev y eric
  
  Connection connexion;
  
  /**
   * Devuelve el Driver especifico del motor o Marca de la Base de Datos Actual.
   */		
  public String getDriver() {
     
    return marcaBd;
  }
  
    
  /**
   * Devuelve el nombre del HOST ACTIVO en el momento actual.
   */		
  public String getHost() {
     
    return host;
  }
  
  /**
   * Devuelve el n�mero del PUERTO ACTIVO por el que se comunica la Base de 
   * Datos.
   */		
  public String getPort() {
     
    return port;
  }
  
  	
	
  /**
   * Genera la URL ACTIVA o URL de conexion para la base de datos adecuandolo a 
   * los formatos exigidos por cada una de las marcas o motores de bases de 
   * datos.  Como argumentos comunes se requieren el nombre del HOST, el nombre 
   * de la BASE DE DATOS a la que se pretende acceder y por la identificacion 
   * del motor de base de datos (DBRMS) del que se trate (MySQL, ORACLE, ..etc).
   *
   * <UL>Las URL son:
   *    <LI><B>MySQL ------ [jdbc:mysql://host:puerto/Base_de_Datos]</B>
   *                        [jdbc:mysql://localhost:3306/WDCSIC"]
   *    <LI><B>PostgreSQL - [jdbc:postgresql://host:puerto/Base_de_Datos]</B>
   *                        [jdbc:postgresql://inesdev:5432/omc"]
   *    <LI><B>MsAccess --- [jdbc:odbc:Base_de_Datos]</B>
   *		                [jdbc:odbc:WDCSIC"]
   *    <LI><B>Oracle ----- [jdbc:oracle:thin:@host:port:Base_de_Datos]</B>
   *		                [jdbc:oracle:thin:@localhost:1521:WDCSIC]
   *    <LI><B>Sybase ----- [jdbc:sybase:Tds:host:port?SERVICENAME=Base_de_Datos]</B>
   *                        [jdbc:sybase:Tds:localhost:1521?SERVICENAME=WDCSIC]
   * </UL>
   *	
   */	
  public String bdURL(String dbName) {
  	
  	if (dbName == null || dbName.length() == 0) return null;
  	
    if (marcaBd.equals("org.gjt.mm.mysql.Driver") ) {
        return ("jdbc:mysql://" + host + ":" + port + "/" + dbName);
    } else {
		if (marcaBd.equals("org.postgresql.Driver") ) {
			return ("jdbc:postgresql://" + host + ":" + port + "/" + dbName);
		}else{
	       if (marcaBd.equals("sun.jdbc.odbc.JdbcOdbcDriver") ) {
	    	  return ("jdbc:odbc:" + dbName);
	       } else {
	          if (marcaBd.equals("oracle.jdbc.driver.OracleDriver") ) {
	             return("jdbc:oracle:thin:@" + host + ":" + port + ":" + dbName);
	          } else {
	             if (marcaBd.equals("com.sybase.jdbc.SybDriver") ) {
	                return("jdbc:sybase:Tds:" + host  + ":" + port + "?SERVICENAME=" + dbName);
	             }
	          }
	       }
	   }
    }            
    return null;
  }  
    
    
  /**
   * <P>Establece una conexion con un tipo de marca o motor (DBRMS) de base de 
   * datos y con una Base de Datos especificada. Para ello solo se necesita 
   * especificar en NOMBRE DE LA BASE DE DATOS, el USUARIO de conexi�n y su
   * CONTRASE�A. El formato de lladada ser�:
   * <BR>
   *       String nombre_bd = "WDCSIC";
   *       String usuario   = "Usuario";
   *       String password  = "contrase�a";
   *       Connection conexion;
   *
   *       DriverBD con = new  jmpservlets.DriverBD();
   *       boolean driverNotLoaded = true;
   *       try {
   *           conexion = con.bdConexion(nombre_bd, usuario, password);
   *           driverNotLoaded = true;
   *       } catch (SQLException errconexion)  {
   *           driverNotLoaded = false;
   *           errconexion.printStackTrace();
   *       }
   *     
   *       if (driverNotLoaded) { 
   *           ... ejecutar sentencia ....
   * <BR>
   */
  public Connection bdConexion(String dbname, String userid, String passwd) 
                                      throws SQLException {
    if(dbname!=null)
    	this.dbname = dbname;
    if(userid!=null)
    	this.userid = userid;
    if(passwd!=null)
    	this.passwd = passwd;
    boolean swt = true;
    
  
    Connection conexion = null; //interface para la connexion con las BD.

  	String marcabd = getDriver();
  	//System.out.println("diver = " + marcabd );
  	
	try {
	    Class.forName(marcabd);
	} catch(ClassNotFoundException mydriver)  {	
	   mydriver.printStackTrace();
	   swt = false;	
    }     
	
	String laurl = bdURL(this.dbname) ;
	//System.out.println(laurl);
	if (swt) {
	   try {
	      conexion = DriverManager.getConnection(laurl, this.userid, this.passwd );
          //System.out.println("DriverBD CONEXION GUENAAAA");
       } catch (SQLException errorconexion)  {
          //System.out.println("DriverBD ERRORR de CONEXION");
          errorconexion.printStackTrace();
	   }
    }
  	return (conexion);
  }
  
    
  /**
   * <P>Establece una conexion con un tipo de marca o motor (DBRMS) de base de 
   * datos y con una Base de Datos especificada. Para ello solo se necesita 
   * especificar en NOMBRE DE LA BASE DE DATOS y el USUARIO de conexi�n. El 
   * formato de lladada ser�:
   * <BR>
   *       String nombre_bd = "WDCSIC";
   *       String usuario   = "Usuario";
   *       Connection conexion;
   *
   *       DriverBD con = new  jmpservlets.DriverBD();
   *       boolean driverNotLoaded = true;
   *       try {
   *           conexion = con.bdConexion(nombre_bd, usuario);
   *           driverNotLoaded = true;
   *       } catch (SQLException errconexion)  {
   *           driverNotLoaded = false;
   *           errconexion.printStackTrace();
   *       }
   *     
   *       if (driverNotLoaded) { 
   *           ... ejecutar sentencia ....
   * <BR>
   */

  public Connection bdConexion(String dbname, String userid) 
                                      throws SQLException {
  	return (bdConexion(dbname, userid, ""));
  }


  public Connection bdConexion() throws SQLException {
  	return (bdConexion(dbname, userid, passwd));
  }


  /* ---- juanmp  2o-Junio-2001  -------  */
}
