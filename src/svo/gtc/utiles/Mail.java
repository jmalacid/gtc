package svo.gtc.utiles;
	
	import java.util.Properties;

	import javax.mail.Message;
	import javax.mail.MessagingException;
	import javax.mail.Session;
	import javax.mail.Transport;
	import javax.mail.internet.InternetAddress;
	import javax.mail.internet.MimeMessage;

	public class Mail {

		/**
		    * Envia un correo electronico con el texto indicado a la direccion que se
		    * especifique.
		    *
		    * @param textoMensaje
		    *            texto que aparecera como cuerpo en el mensaje.
		    * @param direccionCorreo
		    *            direccion a la que se envia.
		    * @throws MessagingException
		    */

		   public Mail(String msn) throws MessagingException{

			   //Creamos el String qse mandará en el correo
			   String textoMensaje=msn.replaceAll("=", "' and prode_id=(select prode_id from proderror where prode_filename='").replaceAll("&", "');\nupdate prode_error set prod e_rev=1 where err_id='").replaceAll("\\+", "");
			   
			   
		       Properties props = new Properties();
		       props.setProperty("mail.transport.protocol", "smtp"); 
		       props.setProperty("mail.host", "localhost");
		      
		       Session mailSession = Session.getDefaultInstance(props, null);
		       mailSession.setDebug(false);
		       Transport transport = mailSession.getTransport();

		       MimeMessage message = new MimeMessage(mailSession);
		       message.setSubject("Corrección errores GTC ");
		      message.setFrom(new InternetAddress("gtc-support@cab.inta-csic.es"));
		       message.setContent(textoMensaje, "text/plain");
		       message.addRecipient(Message.RecipientType.TO, new InternetAddress(
		    		   "jmalacid@cab.inta-csic.es"));

		       transport.connect();
		       transport.sendMessage(message, message
		               .getRecipients(Message.RecipientType.TO));
		       transport.close();
		   } 
		   public Mail(String msn, String cabecera, String email) throws MessagingException{
			   
		       Properties props = new Properties();
		       props.setProperty("mail.transport.protocol", "smtp");
		       props.setProperty("mail.host", "localhost");
		      
		       Session mailSession = Session.getDefaultInstance(props, null);
		       mailSession.setDebug(false);
		       Transport transport = mailSession.getTransport();

		       MimeMessage message = new MimeMessage(mailSession);
		       message.setSubject(cabecera);
		       message.setFrom(new InternetAddress("gtc-support@cab.inta-csic.es"));
		       message.setContent(msn, "text/plain");
		       message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
		      
		       if(!cabecera.contains("access")){
		    	   message.addRecipient(Message.RecipientType.TO, new InternetAddress("gtc-support@cab.inta-csic.es"));
		       }
		       transport.connect();
		       transport.sendMessage(message, message
		               .getRecipients(Message.RecipientType.TO));
		       transport.close();
		   } 
		   
	}


