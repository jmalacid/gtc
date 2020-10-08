package svo.gtc.proddat;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import svo.gtc.db.basepath.BasepathAccess;
import svo.gtc.db.basepath.BasepathDb;
import svo.gtc.db.prodat.ProdDatosAccess;
import svo.gtc.db.prodat.ProdDatosDb;
import svo.gtc.db.prodred.PredProdAccess;
import svo.gtc.db.prodred.PredProdDb;
import svo.gtc.db.prodred.ProdRedAccess;
import svo.gtc.db.prodred.ProdRedDb;

public class ProdDatosRedCanaricam extends ProdDatosRed{

	ObsBlock oblock = null;
	Program prog = null;
	String ob = null;
	String p = null;
	String errores = "";
	
	
	public ProdDatosRedCanaricam(File fichero, Connection con) throws GtcFileException, SQLException {
		super(fichero, con);
		
		oblock = new ObsBlock(this);
		ob = oblock.getOblId();
		prog = new Program(this);
		p = prog.getProgId();
		

		ProdDatosAccess prodDatosAccess = new ProdDatosAccess(con);
		ProdDatosDb[] prodDatosRaw = prodDatosAccess.selectByReducedProductInfo( p, ob , this.opentimeFits, this.clostimeFits, "CC");
		//logger.debug("Número de datos reducidos encontrados= "+prodDatosRaw.length);
		if(prodDatosRaw.length==0){
			this.errores = "E-REDUCED-0001: No existe el producto raw relacionado";
		}else if(prodDatosRaw.length>1){
			this.errores = "E-REDUCED-0002: More than one RAW product matching this reduced product.";
		}else{
			this.setProdDatosRaw(prodDatosRaw);
	}
		
	}
	
	public ProdDatosRedCanaricam(ProdDatosRed prodDatosRed, Connection con) throws GtcFileException, SQLException{
		this(prodDatosRed.getFile(), con);
	}
	
	/**
	 * Añade a las pruebas generales las específicas de CanariCam
	 */
	public void test() throws GtcFileException{
		String err = "";
		try{
			super.test();
			
		}catch(GtcFileException e){
			err += e.getMessage();
		}
		
		try{
			this.testOP();
		}catch(GtcFileException e){
			err += e.getMessage();
		}
		if(err.length()>0){
			throw new GtcFileException(err);
		}

	}
	
	/**
	 * Comprobamos los valores del obsblock y del program
	 * @throws GtcFileException 
	 */
	public void testOP() throws GtcFileException{
		String error = "";
		if(!this.oblockFits.equalsIgnoreCase(ob)){
			error +="No coinciden los valores del ObservingBlock; ";
		}
		if(!this.programFits.equalsIgnoreCase(p)){
			error +="No coinciden los valores del Programa; ";
		}
		
		if(error.length()>0){
			throw new GtcFileException(error);
		}
	}
	
	public void ingest(String usrId, Integer colId, Integer basepath) throws Exception{

		// Determinamos el path del fichero
			BasepathAccess basepathAccess = new BasepathAccess(con);
			BasepathDb basepathDb = basepathAccess.selectBpathById(9000);//lo dejamos el 9000 para obtener el path del fichero
			String path = this.getFile().getAbsolutePath().replaceAll(this.getFile().getName(), "").replaceAll(basepathDb.getBpathPath(), "");
		
		
		boolean autocommit = this.con.getAutoCommit();

		try{
			
			
			BasepathAccess bpathAccess = new BasepathAccess(this.con);
			BasepathDb bpathDb = bpathAccess.selectBpathById(9000);


			/// Insertamos en la tabla ProdReducido
			ProdRedAccess prodRedAccess = new ProdRedAccess(this.con);
			ProdRedDb prodRedDb = new ProdRedDb();


			int ind=0;
			String nameFichDestino;
			boolean existe=false;
			do{
				nameFichDestino = this.getProdDatosRaw()[0].getProdFilename().replaceAll("\\.gz", "").replaceAll("\\.fits", "")+"_RED_"+ind;
				existe = prodRedAccess.existsName(nameFichDestino+"%");
				ind++;
			}while(existe);
			

			this.con.setAutoCommit(false);

			
			predId = prodRedAccess.getNewPredId(usrId, colId);

			prodRedDb.setPredId(predId);
			prodRedDb.setUsrId(usrId);
			prodRedDb.setColId(colId);
			prodRedDb.setBpathId(basepath);
			prodRedDb.setBpathPath(bpathDb.getBpathPath());
			prodRedDb.setPredPath(path);
			prodRedDb.setPredFilenameOrig(this.getFilenameorig());
			prodRedDb.setPredFilesize(this.getFileSize());
			prodRedDb.setPredMd5sum(this.getMd5sum());
			prodRedDb.setPredRa(this.getRa());
			prodRedDb.setPredDe(this.getDe());
			prodRedDb.setPredFilename(this.getFilenameorig());
			prodRedDb.setPredType("CC");
			prodRedAccess.insProdDatos(prodRedDb);
			
			
			/// Insertamos en la tabla PRed_Prod
			PredProdAccess predProdAccess = new PredProdAccess(this.con);
			PredProdDb predProdDb = new PredProdDb();
			
			predProdDb.setPredId(prodRedDb.getPredId());
			predProdDb.setProgId(this.p);
			predProdDb.setOblId(this.ob);
			predProdDb.setProdId(this.getProdDatosRaw()[0].getProdId());

			predProdAccess.insPredProd(predProdDb);
			
			this.con.commit();
			this.con.setAutoCommit(autocommit);
		}catch(Exception e){
			this.con.rollback();
			this.con.setAutoCommit(autocommit);
			throw e;
		}
		
	
	}

	public String getOb() {
		return ob;
	}

	public void setOb(String ob) {
		this.ob = ob;
	}

	public String getP() {
		return p;
	}

	public void setP(String p) {
		this.p = p;
	}

	
	
}
