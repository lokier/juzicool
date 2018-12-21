package com.juzicool.search;

import java.util.Date;

/***
 *
 */
public class Juzi implements JuziObject{
	//public long _sqliteId;
	public long id;
	public String content;
	public String from;
	public String author;
	public String category; //分类
	public String remark; //点评
	public String tags; //鉴赏标签
	public String applyDesc; //应用标签

	private Date updateAt;


/*	content	VARCHAR(1024)	句子内容
	length	SMALLINT	句子长度
	author	VARCHAR(45)	作者
	from	VARCHAR(45)	句子出自
	category	VARCHAR(45)	目录
	tags
	applyDesc	VARCHAR(300)	使用场景描述
	remark
	*/

	@Override
	public String toString() {
		return "Juzi{" +
				"id=" + id +
				", content='" + content + '\'' +
				", from='" + from + '\'' +
				", author='" + author + '\'' +
				", category='" + category + '\'' +
				", remark='" + remark + '\'' +
				", tags='" + tags + '\'' +
				", applyDesc='" + applyDesc + '\'' +
				", updateAt=" + updateAt +
				'}';
	}

	public String getContent() {
		return content;
	}

	public void setLength(Integer length) {

	}

	public Integer getLength() {
		return this.content!= null ? this.content.length():0;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getApplyDesc() {
		return applyDesc;
	}

	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}

	public Date getUpdateAt() {
		return updateAt;
	}

	public void setApplyDesc(String applyDesc) {
		this.applyDesc = applyDesc;
	}
}
