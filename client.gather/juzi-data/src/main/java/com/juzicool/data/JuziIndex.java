package com.juzicool.data;

/**
 * 句子索引结构
 */
public interface JuziIndex {

    public void setContent(String content);

	public String getContent();

	public void setLength(Integer length);

	public Integer getLength();

	public void setAuthor(String author);


	public String getAuthor();

	public void setFrom(String from) ;

	public String getFrom() ;


	public void setCategory(String category);

	public String getCategory() ;

	public void setRemark(String remark) ;

	public String getRemark() ;

	public void setTags(String tags);

	public String getTags() ;

	public void setApplyDesc(String applyDesc);

	public String getApplyDesc() ;

	public void setUpdateAt(java.util.Date updateAt) ;
	
	public java.util.Date getUpdateAt() ;

	


}
