package com.juzicool.gather;

public class Juzi {
	//public long _sqliteId;
	public long id;
	public String content;
	public String from;
	public String author;
	public String category; //分类
	public String remark; //点评
	public String tags; //鉴赏标签
	public String applyTags; //应用标签


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
				"id='" + id + '\'' +
				", content='" + content + '\'' +
				", from='" + from + '\'' +
				", author='" + author + '\'' +
				", category='" + category + '\'' +
				", remark='" + remark + '\'' +
				", tags='" + tags + '\'' +
				", applyTags='" + applyTags + '\'' +
				'}';
	}
}
