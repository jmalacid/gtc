/*
 * @(#)MainApp.java    19/01/2012
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
import javax.swing.JButton;

import svo.gtc.ingestion.Config;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainApp {

	private JFrame frmGtcArchiveData;
	private Config config;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainApp window = new MainApp();
					window.frmGtcArchiveData.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainApp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmGtcArchiveData = new JFrame();
		frmGtcArchiveData.setIconImage(Toolkit.getDefaultToolkit().getImage(MainApp.class.getResource("/svo/gtc/ingestion/gui/images/iconoSVO.png")));
		frmGtcArchiveData.setTitle("Archivo GTC: Ingestión de datos");
		frmGtcArchiveData.setBounds(100, 100, 377, 213);
		frmGtcArchiveData.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{127, 0, 122, 0};
		gridBagLayout.rowHeights = new int[]{38, 0, 25, 25, 25, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frmGtcArchiveData.getContentPane().setLayout(gridBagLayout);
		
		JButton btnIngestion = new JButton("Ingestión");
		GridBagConstraints gbc_btnIngestion = new GridBagConstraints();
		gbc_btnIngestion.anchor = GridBagConstraints.NORTH;
		gbc_btnIngestion.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnIngestion.insets = new Insets(0, 0, 5, 5);
		gbc_btnIngestion.gridx = 1;
		gbc_btnIngestion.gridy = 1;
		frmGtcArchiveData.getContentPane().add(btnIngestion, gbc_btnIngestion);
		
		JButton btnOpenLog = new JButton("Abrir log");
		GridBagConstraints gbc_btnOpenLog = new GridBagConstraints();
		gbc_btnOpenLog.anchor = GridBagConstraints.NORTH;
		gbc_btnOpenLog.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnOpenLog.insets = new Insets(0, 0, 5, 5);
		gbc_btnOpenLog.gridx = 1;
		gbc_btnOpenLog.gridy = 2;
		frmGtcArchiveData.getContentPane().add(btnOpenLog, gbc_btnOpenLog);
		
		JButton btnConfig = new JButton("Configuración");
		btnConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				initConfig();
			}
		});
		GridBagConstraints gbc_btnConfig = new GridBagConstraints();
		gbc_btnConfig.insets = new Insets(0, 0, 5, 5);
		gbc_btnConfig.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnConfig.gridx = 1;
		gbc_btnConfig.gridy = 3;
		frmGtcArchiveData.getContentPane().add(btnConfig, gbc_btnConfig);
	}
	
	
	
	private void initConfig(){
		System.out.println("Entrando en configuración...");
		new Configuration(this.frmGtcArchiveData, config);
	}
	
}
