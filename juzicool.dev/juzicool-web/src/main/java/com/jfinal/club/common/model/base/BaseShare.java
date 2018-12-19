package com.jfinal.club.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseShare<M extends BaseShare<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setAccountId(java.lang.Integer accountId) {
		set("accountId", accountId);
	}
	
	public java.lang.Integer getAccountId() {
		return getInt("accountId");
	}

	public void setProjectId(java.lang.Integer projectId) {
		set("projectId", projectId);
	}
	
	public java.lang.Integer getProjectId() {
		return getInt("projectId");
	}

	public void setTitle(java.lang.String title) {
		set("title", title);
	}
	
	public java.lang.String getTitle() {
		return getStr("title");
	}

	public void setContent(java.lang.String content) {
		set("content", content);
	}
	
	public java.lang.String getContent() {
		return getStr("content");
	}

	public void setCreateAt(java.util.Date createAt) {
		set("createAt", createAt);
	}
	
	public java.util.Date getCreateAt() {
		return get("createAt");
	}

	public void setClickCount(java.lang.Integer clickCount) {
		set("clickCount", clickCount);
	}
	
	public java.lang.Integer getClickCount() {
		return getInt("clickCount");
	}

	public void setReport(java.lang.Integer report) {
		set("report", report);
	}
	
	public java.lang.Integer getReport() {
		return getInt("report");
	}

	public void setLikeCount(java.lang.Integer likeCount) {
		set("likeCount", likeCount);
	}
	
	public java.lang.Integer getLikeCount() {
		return getInt("likeCount");
	}

	public void setFavoriteCount(java.lang.Integer favoriteCount) {
		set("favoriteCount", favoriteCount);
	}
	
	public java.lang.Integer getFavoriteCount() {
		return getInt("favoriteCount");
	}

}