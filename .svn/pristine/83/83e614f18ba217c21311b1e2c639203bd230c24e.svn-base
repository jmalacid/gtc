/*
 * @(#)LogBrowser.java    30/01/2012
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.ProgressMonitorInputStream;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.JTree;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import svo.gtc.ingestion.Config;
import svo.gtc.ingestion.LogError;
import svo.gtc.ingestion.LogIngestion;
import svo.gtc.ingestion.LogOblock;
import svo.gtc.ingestion.LogProduct;
import svo.gtc.ingestion.LogProgram;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import java.awt.Color;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class LogBrowser extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField logFile;
	private JButton btnAbrir;
	
	private Config config;

	private LogIngestion logIngestion;
	private JScrollPane scrollPane;
	private JTree tree;
	private JPanel panel_1;
	private JCheckBox chkShowError;
	private JCheckBox chkShowWarn;
	private JCheckBox chkShowOk;
	private JLabel lblShowLevel;

	/**
	 * Clase Worker que realiza la lectura del fichero de log en segundo plano y muestra
	 * una barra de progreso.
	 * @author raul
	 *
	 */
	private class LogReader extends SwingWorker<LogIngestion, Void>{

		private File fichero;
		
		public LogReader(File fichero){
			this.fichero = fichero;
		}
		
		@Override
		protected LogIngestion doInBackground() throws Exception {
			BufferedInputStream stream = new BufferedInputStream(new ProgressMonitorInputStream(
	                LogBrowser.this,
	                "Reading " + fichero.getName(),
	                new FileInputStream(fichero) ));
			
			LogIngestion log = new LogIngestion(stream);
			log.setName(fichero.getName());
			
			stream.close();
			
			return log;
		}


		@Override
		public void done() {
			try {
				btnAbrir.setEnabled(true);
				logFile.setEnabled(true);
			    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				logIngestion = get();
				pintaArbol();
			} catch (InterruptedException ignore) {}
			catch (java.util.concurrent.ExecutionException e) {
				String why = null;
				Throwable cause = e.getCause();
				if (cause != null) {
					why = cause.getMessage();
				} else {
					why = e.getMessage();
				}
				System.err.println("Error retrieving file: " + why);
			}
		}
	}
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			LogBrowser dialog = new LogBrowser();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public LogBrowser() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Visor de ficheros de log");
		config=new Config();
		config.readFromDisk();
		
		
		setBounds(100, 100, 703, 686);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPanel.add(panel, BorderLayout.NORTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblFicheroDeLog = new JLabel("Fichero de log: ");
		panel.add(lblFicheroDeLog);
		
		logFile = new JTextField();
		panel.add(logFile);
		logFile.setColumns(25);
		
		btnAbrir = new JButton("Abrir");
		btnAbrir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser c = new JFileChooser();
				c.setCurrentDirectory(config.getPathLogs());
				int rVal= c.showOpenDialog(LogBrowser.this);
				
				if(rVal==JFileChooser.APPROVE_OPTION) {
					logFile.setText(c.getSelectedFile().toString());
				}
				
				logFile.setEnabled(false);
				btnAbrir.setEnabled(false);
			    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			    LogReader logReader= new LogReader(new File(logFile.getText()));
				logReader.execute();
			}
		});
		panel.add(btnAbrir);
		
		scrollPane = new JScrollPane();
		contentPanel.add(scrollPane, BorderLayout.CENTER);
		
		tree = new JTree();
		tree.setModel(new DefaultTreeModel(null));
		tree.setCellRenderer(new MyRenderer());
		scrollPane.setViewportView(tree);
		
		panel_1 = new JPanel();
		scrollPane.setColumnHeaderView(panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		lblShowLevel = new JLabel("Show: ");
		panel_1.add(lblShowLevel);
		
		chkShowOk = new JCheckBox("OK");
		chkShowOk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				pintaArbol();
			}
		});
		chkShowOk.setBackground(Color.GREEN);
		chkShowOk.setSelected(true);
		panel_1.add(chkShowOk);
		
		chkShowWarn = new JCheckBox("Warning");
		chkShowWarn.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				pintaArbol();
			}
		});
		chkShowWarn.setBackground(Color.YELLOW);
		chkShowWarn.setSelected(true);
		panel_1.add(chkShowWarn);
		
		chkShowError = new JCheckBox("Error");
		chkShowError.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				pintaArbol();
			}
		});
		chkShowError.setBackground(Color.RED);
		chkShowError.setSelected(true);
		panel_1.add(chkShowError);
	}
	
	/**
	 * Genera el arbol que representa el objeto LogIngestion.
	 */
	private void pintaArbol(){
		if(logIngestion==null) return;
		NodoArbol fileNode	 = new NodoArbol(logIngestion, logIngestion.getName());
		NodoArbol progNode  = null;
	    NodoArbol oblNode   = null;
	    NodoArbol prodNode  = null;
	    NodoArbol errorNode = null;
	    
		LogProgram[] programas = logIngestion.getProgramas();
		// Añadimos los programas
		for(int i=0; i<programas.length; i++){
		    progNode = new NodoArbol(programas[i], programas[i].getName());

	    	LogError[] errores = programas[i].getErrores();
	    	// Añadimos los errores de programa
	    	for(int l=0; l<errores.length; l++){
	    		errorNode = new NodoArbol(errores[l], errores[l].getErrId()+": "+errores[l].getErrDesc());
	    		if(errores[l].getErrId().startsWith("W")){
	    			progNode.setWarning();
	    			errorNode.setWarning();
	    		}else if(errores[l].getErrId().startsWith("E")){
	    			progNode.setError();
	    			errorNode.setError();
	    		}
	    		progNode.add(errorNode);
	    	}

		    
		    
		    LogOblock[] oblocks = programas[i].getOblocks();
		    // Añadimos los OBs
		    for(int j=0; j<oblocks.length; j++){
		    	oblNode = new NodoArbol(oblocks[j], oblocks[j].getName());

		    	errores = oblocks[j].getErrores();
		    	// Añadimos los errores de OB
		    	for(int l=0; l<errores.length; l++){
		    		errorNode = new NodoArbol(errores[l], errores[l].getErrId()+": "+errores[l].getErrDesc());
		    		if(errores[l].getErrId().startsWith("W")){
		    			progNode.setWarning();
		    			oblNode.setWarning();
		    			errorNode.setWarning();
		    		}else if(errores[l].getErrId().startsWith("E")){
		    			progNode.setError();
		    			oblNode.setError();
		    			errorNode.setError();
		    		}
		    		oblNode.add(errorNode);
		    	}

		    	
			    LogProduct[] prods = oblocks[j].getProducts();
			    // Añadimos los productos
			    for(int k=0; k<prods.length; k++){
			    	prodNode = new NodoArbol(prods[k], prods[k].getName());
			    	
			    	errores = prods[k].getErrores();
			    	// Añadimos los errores de producto
			    	for(int l=0; l<errores.length; l++){
			    		errorNode = new NodoArbol(errores[l], errores[l].getErrId()+": "+errores[l].getErrDesc());
			    		if(errores[l].getErrId().startsWith("W")){
			    			progNode.setWarning();
			    			oblNode.setWarning();
			    			prodNode.setWarning();
			    			errorNode.setWarning();
			    		}else if(errores[l].getErrId().startsWith("E")){
			    			progNode.setError();
			    			oblNode.setError();
			    			prodNode.setError();
			    			errorNode.setError();
			    		}
		    			prodNode.add(errorNode);
			    	}
			    	
		    		if( prodNode.getError()==2 && chkShowError.isSelected()
			    			||prodNode.getError()==1 && chkShowWarn.isSelected()
			    			||prodNode.getError()==0 && chkShowOk.isSelected()
			    			){
  		    			oblNode.add(prodNode);
		    		}
			    }
			    
		    	progNode.add(oblNode);
		    }
		    fileNode.add(progNode);
		}
		DefaultTreeModel model = new DefaultTreeModel(fileNode);
	    tree.setModel(model);
	    tree.setVisible(true);
	}
	
	private class NodoArbol extends DefaultMutableTreeNode{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private int error = 0;
		
		private String text;

		public NodoArbol(Object o, String text){
			super(o);
			this.text=text;
		}
		
		public void setWarning(){
			if(error<1){
				error=1;
			}
		}

		public void setError(){
			if(error<2){
				error=2;
			}
		}
		
		public int getError() {
			return error;
		}
		
		/**
		 * Override de toString.
		 */
		public String toString(){
			return text;
		}
		
	}

	/** 
	 * Establece un icono personalizado en función de si existen errores o no
	 * @author raul
	 *
	 */
	private class MyRenderer extends DefaultTreeCellRenderer {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		ImageIcon carpetaDefault;
		ImageIcon ficheroDefault;
		ImageIcon carpetaOk;
		ImageIcon carpetaWarn;
		ImageIcon carpetaErr;
		ImageIcon ficheroOk;
		ImageIcon ficheroWarn;
		ImageIcon ficheroErr;

	    public MyRenderer() {
	    	carpetaDefault   = createImageIcon("/svo/gtc/ingestion/gui/images/iconoCarpetaDefault.png");
	    	carpetaOk   = createImageIcon("/svo/gtc/ingestion/gui/images/iconoCarpetaVerde.png");
	    	carpetaWarn = createImageIcon("/svo/gtc/ingestion/gui/images/iconoCarpetaAmarilla.png");
	    	carpetaErr  = createImageIcon("/svo/gtc/ingestion/gui/images/iconoCarpetaRoja.png");
	    	ficheroDefault   = createImageIcon("/svo/gtc/ingestion/gui/images/iconoFicheroDefault.png");
	    	ficheroOk   = createImageIcon("/svo/gtc/ingestion/gui/images/iconoFicheroVerde.png");
	    	ficheroWarn = createImageIcon("/svo/gtc/ingestion/gui/images/iconoFicheroAmarillo.png");
	    	ficheroErr  = createImageIcon("/svo/gtc/ingestion/gui/images/iconoFicheroRojo.png");
	    }

	    public Component getTreeCellRendererComponent(
	                        JTree tree,
	                        Object value,
	                        boolean sel,
	                        boolean expanded,
	                        boolean leaf,
	                        int row,
	                        boolean hasFocus) {

	        super.getTreeCellRendererComponent(
	                        tree, value, sel,
	                        expanded, leaf, row,
	                        hasFocus);
	        	ImageIcon icon = getIcon(leaf,value); 
	            if(icon!=null) setIcon(icon);

	        return this;
	    }

	    protected ImageIcon getIcon(boolean leaf, Object value) {
	        NodoArbol nodo =
	                (NodoArbol)value;
	        
	        if(nodo.getParent()!=null){
	        	if(leaf){
	        		switch(nodo.getError()){
	        			case 0: return ficheroOk;
	        			case 1: return ficheroWarn;
	        			case 2: return ficheroErr;
	        		};
	        	}else{
	        		switch(nodo.getError()){
	        			case 0: return carpetaOk;
	        			case 1: return carpetaWarn;
	        			case 2: return carpetaErr;
	        		};
	        		
	        	}
	        }
	        return null;
	    }

	}

	
	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = LogBrowser.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
	
	

}
