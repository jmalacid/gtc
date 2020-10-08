/*
 * @(#)Configuration.java    19/01/2012
 *
 *
 * Raúl Gutiérrez Sánchez. (raul@laeff.inta.es)	
 * LAEFF: 	Laboratorio de Astrofísica Espacial y Física Fundamental.
 *        	INTA
 *			Villafranca del Castillo Satellite Tracking Station
 *			Villafranca del Castillo, E-28691 Villanueva de la Cañada
 *			Madrid - España
 *			
 * Phone:  34 91-8131260
 * Fax..:  34 91-8131160   
 * -----------------------------------------------------------------------
 * 
 *	ESTE  PROGRAMA  ES  DE  USO EXCLUSIVO.  ES  PROPIEDAD  DE  SU AUTOR  Raúl 
 *  Gutiérrez Sánchez  Y SE PRESENTA "COMO ESTA" SIN NINGUN TIPO DE GARANTIAS
 *  DENTRO Y FUERA  DE LA FINALIDAD PARA  LA QUE EL  AUTOR LO  HIZO. EL AUTOR 
 *  PERMITE  SU USO SIEMPRE QUE  SE LE REFERENCIE. SE PERMITE LA MODIFICACION
 *  DE SU  FUENTE SIEMPRE QUE SE  HAGA REFERENCIA A SU AUTORIA Y JUNTO A ELLA
 *  SE  ADJUNTE LA IDENTIFICACION DEL  AUTOR DE LAS NUEVAS MODIFICACIONES. EL
 *  AUTOR  NUNCA SERA RESPONSABLE DEL USO DE ESTE  PROGRAMA NI DE LOS EFECTOS
 *  QUE ESTE PROGRAMA PUDIEDA CAUSAR EN DETERIOROS, ALTERACIONES Y/O PERDIDAS 
 *  DE INFORMACION.
 *	
 */

package svo.gtc.ingestion.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.Toolkit;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import svo.gtc.ingestion.Config;
import svo.gtc.ingestion.ConfigException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class ConfigurationOld extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFrame frmConfiguracion;
	private JTextField txtPathBase;
	private JTextField txtPathData;
	private JTextField txtPathLog;
	
	private String pathBaseOld = "";
	
	private Config config;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConfigurationOld window = new ConfigurationOld();
					window.frmConfiguracion.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ConfigurationOld() {
		//setIconImage(Toolkit.getDefaultToolkit().getImage(Configuration.class.getResource("/svo/gtc/ingestion/gui/iconoSVO.png")));
		this.config=new Config();
		this.config.readFromDisk();
		initialize();
	}

	/**
	 * Create the application.
	 */
	public ConfigurationOld(Config config) {
		this.config=config;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmConfiguracion = new JFrame();
		frmConfiguracion.setIconImage(Toolkit.getDefaultToolkit().getImage(ConfigurationOld.class.getResource("/svo/gtc/ingestion/gui/iconoSVO.png")));
		frmConfiguracion.setTitle("Configuración");
		frmConfiguracion.setBounds(100, 100, 504, 236);
		frmConfiguracion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{39, 86, 208, 119, 0, 0};
		gridBagLayout.rowHeights = new int[]{31, 0, 0, 0, 34, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frmConfiguracion.getContentPane().setLayout(gridBagLayout);
		
		JLabel lblPathBase = new JLabel("Directorio Base:");
		GridBagConstraints gbc_lblPathBase = new GridBagConstraints();
		gbc_lblPathBase.anchor = GridBagConstraints.WEST;
		gbc_lblPathBase.insets = new Insets(0, 0, 5, 5);
		gbc_lblPathBase.gridx = 1;
		gbc_lblPathBase.gridy = 1;
		frmConfiguracion.getContentPane().add(lblPathBase, gbc_lblPathBase);
		
		txtPathBase = new JTextField();
		txtPathBase.setText("");
		GridBagConstraints gbc_txtPathBase = new GridBagConstraints();
		gbc_txtPathBase.insets = new Insets(0, 0, 5, 5);
		gbc_txtPathBase.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPathBase.gridx = 2;
		gbc_txtPathBase.gridy = 1;
		frmConfiguracion.getContentPane().add(txtPathBase, gbc_txtPathBase);
		txtPathBase.setColumns(10);
		
		if(config.getPathBase()!=null) txtPathBase.setText(this.config.getPathBase().toString());
		pathBaseOld = txtPathBase.getText();
		
		JButton btnOpenPathBase = new JButton("Abrir");
		btnOpenPathBase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser c = new JFileChooser();
				int rVal= c.showOpenDialog(ConfigurationOld.this);
				
				if(rVal==JFileChooser.APPROVE_OPTION) {
					txtPathBase.setText(c.getCurrentDirectory().toString());
				}
			}
		});
		GridBagConstraints gbc_btnOpenPathBase = new GridBagConstraints();
		gbc_btnOpenPathBase.anchor = GridBagConstraints.WEST;
		gbc_btnOpenPathBase.insets = new Insets(0, 0, 5, 5);
		gbc_btnOpenPathBase.gridx = 3;
		gbc_btnOpenPathBase.gridy = 1;
		frmConfiguracion.getContentPane().add(btnOpenPathBase, gbc_btnOpenPathBase);
		
		JLabel lblPathData = new JLabel("Directorio de datos:");
		GridBagConstraints gbc_lblPathData = new GridBagConstraints();
		gbc_lblPathData.anchor = GridBagConstraints.WEST;
		gbc_lblPathData.insets = new Insets(0, 0, 5, 5);
		gbc_lblPathData.gridx = 1;
		gbc_lblPathData.gridy = 2;
		frmConfiguracion.getContentPane().add(lblPathData, gbc_lblPathData);
		
		txtPathData = new JTextField();
		txtPathData.setText("");
		GridBagConstraints gbc_txtPathData = new GridBagConstraints();
		gbc_txtPathData.insets = new Insets(0, 0, 5, 5);
		gbc_txtPathData.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPathData.gridx = 2;
		gbc_txtPathData.gridy = 2;
		frmConfiguracion.getContentPane().add(txtPathData, gbc_txtPathData);
		txtPathData.setColumns(10);

		if(config.getPathData()!=null) txtPathData.setText(config.getPathData().toString());

		
		JButton btnNewButton = new JButton("Abrir");
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.anchor = GridBagConstraints.WEST;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 3;
		gbc_btnNewButton.gridy = 2;
		frmConfiguracion.getContentPane().add(btnNewButton, gbc_btnNewButton);
		
		JLabel lblPathLogs = new JLabel("Directorio de log:");
		GridBagConstraints gbc_lblPathLogs = new GridBagConstraints();
		gbc_lblPathLogs.insets = new Insets(0, 0, 5, 5);
		gbc_lblPathLogs.anchor = GridBagConstraints.WEST;
		gbc_lblPathLogs.gridx = 1;
		gbc_lblPathLogs.gridy = 3;
		frmConfiguracion.getContentPane().add(lblPathLogs, gbc_lblPathLogs);
		
		txtPathLog = new JTextField();
		txtPathLog.setText("");
		GridBagConstraints gbc_txtPathLog = new GridBagConstraints();
		gbc_txtPathLog.insets = new Insets(0, 0, 5, 5);
		gbc_txtPathLog.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPathLog.gridx = 2;
		gbc_txtPathLog.gridy = 3;
		frmConfiguracion.getContentPane().add(txtPathLog, gbc_txtPathLog);
		txtPathLog.setColumns(10);

		if(config.getPathLogs()!=null) txtPathLog.setText(config.getPathLogs().toString());

		
		JButton btnNewButton_1 = new JButton("Abrir");
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.anchor = GridBagConstraints.WEST;
		gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_1.gridx = 3;
		gbc_btnNewButton_1.gridy = 3;
		frmConfiguracion.getContentPane().add(btnNewButton_1, gbc_btnNewButton_1);
		
		JButton btnSave = new JButton("Guardar");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					guardaDatos();
				} catch (ConfigException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.anchor = GridBagConstraints.EAST;
		gbc_btnSave.insets = new Insets(0, 0, 0, 5);
		gbc_btnSave.gridx = 2;
		gbc_btnSave.gridy = 5;
		frmConfiguracion.getContentPane().add(btnSave, gbc_btnSave);
		
		JButton btnCancel = new JButton("Cancelar");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmConfiguracion.dispose();
			}
		});
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.anchor = GridBagConstraints.WEST;
		gbc_btnCancel.insets = new Insets(0, 0, 0, 5);
		gbc_btnCancel.gridx = 3;
		gbc_btnCancel.gridy = 5;
		frmConfiguracion.getContentPane().add(btnCancel, gbc_btnCancel);
		
		frmConfiguracion.setVisible(true);
		
		
		
		//// Listeners
		txtPathBase.getDocument().addDocumentListener(new PathBaseL());

	}

	
	/**
	 * Listener para detectar cambios en el campo de texto de path.
	 * @author raul
	 *
	 */
	class PathBaseL implements DocumentListener {
		public void insertUpdate(DocumentEvent e) {
			completaCampos();
		}
		public void removeUpdate(DocumentEvent e) {
			completaCampos();
		}
		public void changedUpdate(DocumentEvent e) {
			completaCampos();
		}
	}
	
	/**
	 * Rellena automáticamente los campos pathData y pathLog cuando cambia pathBase.
	 */
	private void completaCampos(){
			String texto = txtPathBase.getText().trim();
		
			if(texto.length()>0 && (txtPathData.getText().trim().length()==0 || txtPathData.getText().trim().startsWith(pathBaseOld))){
				txtPathData.setText(texto+"/data");
			}
			if(texto.length()>0 && (txtPathLog.getText().trim().length()==0 || txtPathLog.getText().trim().startsWith(pathBaseOld))){
				txtPathLog.setText(texto+"/logs/ingestion");
			}
			
			pathBaseOld = texto;
	}
	
	private void guardaDatos() throws ConfigException{
		Config conf = new Config();

		if(txtPathBase.getText().trim().length()==0 &&
				txtPathData.getText().trim().length()==0 &&
				txtPathLog.getText().trim().length()==0 ){
			throw new ConfigException("Deben especificarse todos los directorios.");
		}
		
		conf.setPathBase(new File(this.txtPathBase.getText()));
		conf.setPathData(new File(this.txtPathData.getText()));
		conf.setPathLogs(new File(this.txtPathLog.getText()));

		conf.saveOnDisk();
	}
	
	/*
	public void setVisible(boolean b){
		frmConfiguracion.setVisible(b);
		this.setVisible(b);
	}
	*/
	
}
