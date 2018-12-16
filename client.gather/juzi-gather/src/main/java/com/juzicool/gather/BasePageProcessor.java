package com.juzicool.gather;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import com.juzicool.gather.store.JuziExcel;
import org.apache.commons.lang3.StringUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public abstract class BasePageProcessor implements PageProcessor,Closeable {

	public static final int MIN_JUZI_LENGTH = 18;


	public static final String EXCEL_XLS = ".xls";
	// private static final String EXCEL_XLSX = "xlsx";
	private static final String OUT_PUT_DIR = "output";

	private Site gSite = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);


	private File mWriteFile = null;
	private JuziExcel mJuziExcel = null;

	public BasePageProcessor() {

	}

	public File getWriteFile() {
		if(mWriteFile== null) {
			synchronized (getClass()) {

				File dir = new File(OUT_PUT_DIR);
				if(!dir.exists()) {
					if(!dir.mkdirs()) {
						throw new RuntimeException("create dir error: " + dir.getPath());
					}
				}
				mWriteFile = new File(dir,getFileName());
			}
		}

		return mWriteFile;
	}

	protected String getFileName(){
		return this.getClass().getSimpleName()+EXCEL_XLS;
	}

	protected JuziExcel createJuziExcel(File file) {
		//删除之前的file
		File wFile = file;
		if(wFile.exists()) {
			if(!wFile.delete()) {
				throw new RuntimeException("init() , delete filer error: " + wFile.getPath());
			}
		}

		JuziExcel excel = new JuziExcel(file);
		return excel;
	}
	
	protected int minJuziLength() {
		return MIN_JUZI_LENGTH;
	}

	/**
	 *  写句子
	 * @param juzi
	 */
	public  boolean write(Juzi juzi) {

		if(StringUtils.isEmpty(juzi.content)) {
			System.err.println("write juzi error:  empty content.");
			return false;
		}

		if(juzi.content.length()<  minJuziLength() ) {
			System.err.println("write juzi error:  句子太短：" + juzi.content );
			return false;

		}

		synchronized (getClass()) {
			try {
			if(mJuziExcel == null) {
				mJuziExcel = createJuziExcel(getWriteFile());
				mJuziExcel.prepare();
			}

				mJuziExcel.write(juzi);
				//mJuziExcel.close();
				System.out.println("write juzi success：" + juzi.toString() );
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	@Override
	public void close() throws IOException{
		if(mJuziExcel!=null){
			mJuziExcel.close();
		}
	}


	@Override
	public Site getSite() {
		return gSite;
	}



	@Override
	abstract public void process(Page arg0) ;

}
