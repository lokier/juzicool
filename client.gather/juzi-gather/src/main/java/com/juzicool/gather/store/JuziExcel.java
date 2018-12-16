

package com.juzicool.gather.store;

import java.io.File;
import java.io.IOException;

import com.juzicool.gather.Juzi;
import org.apache.commons.lang3.StringUtils;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class JuziExcel {
	final File mOutput;

	//分类	点评	鉴赏标签	推荐使用场景

	private static final String[] TITLE = {"句子","出自","作者","分类","点评","鉴赏","推荐使用场景"};

	public JuziExcel(File file) {
		mOutput = file;
	}

	public void prepare() throws Exception {
		//2:创建工作簿
		mWorkBook=Workbook.createWorkbook(mOutput);
		//3:创建sheet,设置第二三四..个sheet，依次类推即可
		mSheet=mWorkBook.createSheet("juzi1", 0);
		mNextWriteRow = 0;
		writeColumn(mNextWriteRow++);
	}

	private WritableWorkbook mWorkBook = null;
	private WritableSheet mSheet = null;
	private int mNextWriteRow  = -1;

	private void writeColumn(int row) throws Exception  {
		for(int column = 1; column <= TITLE.length;column++) {
			Label label=new Label(column,row,TITLE[column-1]);
			mSheet.addCell(label);
		}
	}


	private void write(int row, Juzi juzi) throws Exception  {

		Label contentLabel=new Label(1,row,juzi.content);
		mSheet.addCell(contentLabel);

		String from = filterBookmark(juzi.from);
		if(!StringUtils.isEmpty(from)) {
			Label label=new Label(2,row,from);
			mSheet.addCell(label);
		}

		String author = filterBookmark(juzi.author);
		if(!StringUtils.isEmpty(author)) {
			Label label=new Label(3,row,author);
			mSheet.addCell(label);
		}
		
		String category = juzi.category;
		if(!StringUtils.isEmpty(category)) {
			Label label=new Label(4,row,category);
			mSheet.addCell(label);
		}
		
		String remark = juzi.remark;
		if(!StringUtils.isEmpty(remark)) {
			Label label=new Label(5,row,remark);
			mSheet.addCell(label);
		}
		
		String tags = juzi.tags;
		if(!StringUtils.isEmpty(tags)) {
			Label label=new Label(6,row,tags);
			mSheet.addCell(label);
		}
		
		
		String applyTags = juzi.applyTags;
		if(!StringUtils.isEmpty(applyTags)) {
			Label label=new Label(7,row,applyTags);
			mSheet.addCell(label);
		}

	}

	public void write(Juzi juzi) throws Exception  {
		write(mNextWriteRow++,juzi);
	}


	public void close() throws IOException {
		//写入数据，一定记得写入数据，不然你都开始怀疑世界了，excel里面啥都没有
		if(mWorkBook  == null) {
			return;
		}

		mWorkBook.write();
		//最后一步，关闭工作簿
		try {
			mWorkBook.close();
		}catch (WriteException ex){
			throw new IOException(ex);
		}
		mWorkBook =null;
		mSheet = null;
	}

	private String filterBookmark(String text ) {
		if(StringUtils.isEmpty(text)) {
			return text;
		}
		//	text = text.replace("《", "");
		//text = text.replace("》", "");
		return text.replaceAll("[《》「」—\\-]", "");
	}
}
