/**
 * 请勿将俱乐部专享资源复制给其他人，保护知识产权即是保护我们所在的行业，进而保护我们自己的利益
 * 即便是公司的同事，也请尊重 JFinal 作者的努力与付出，不要复制给同事
 * 
 * 如果你尚未加入俱乐部，请立即删除该项目，或者现在加入俱乐部：http://jfinal.com/club
 * 
 * 俱乐部将提供 jfinal-club 项目文档与设计资源、专用 QQ 群，以及作者在俱乐部定期的分享与答疑，
 * 价值远比仅仅拥有 jfinal club 项目源代码要大得多
 * 
 * JFinal 俱乐部是五年以来首次寻求外部资源的尝试，以便于有资源创建更加
 * 高品质的产品与服务，为大家带来更大的价值，所以请大家多多支持，不要将
 * 首次的尝试扼杀在了摇篮之中
 */

package com.juzicool.search.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.club.common.controller.BaseController;
import com.jfinal.club.common.interceptor.AuthCacheClearInterceptor;
import com.jfinal.club.common.model.Juzi;
import com.jfinal.club.common.model.JuziGroup;
import com.jfinal.plugin.activerecord.Page;
import com.juzicool.search.JuziObject;
import com.juzicool.search.plugin.SearchService;
import com.juzicool.search.util.UrlUtils;


/**
 * 首页控制器
 */
public class ApiController extends BaseController {

	private static int MAX_PAGE_SIZE = 10;

	SearchService srv = SearchService.me;
	JuziGroup mGroupDao = new JuziGroup().dao();
	Juzi mJuziDao = new Juzi().dao();


	public void index() {

		Page<JuziGroup> grops = mGroupDao.paginate(1,10,"select *", "from juzi_group order by id asc");

		JSONObject object = new JSONObject();

		JSONArray groupList = new JSONArray();
		if(grops!= null){
			for(JuziGroup group: grops.getList()){
				JSONObject groupObj = new JSONObject();
				groupObj.put("name",group.getName());
				groupObj.put("id",group.getId());
				groupList.add(groupObj);
			}
		}
		object.put("groups",groupList);
		renderJson(object.toJSONString());
	}
	
	/***
	 * 分页查找从1开始找。
	 */
	public void search() {
		//setAttr("q", value);
		String query = super.getPara("q", "");
		int currentPage = super.getParaToInt("page", 1); //从1开始
		int pageSize = super.getParaToInt("size", 0);
		
		if(currentPage < 1) {
			currentPage = 1;
		}
		
		if(pageSize >MAX_PAGE_SIZE) {
			pageSize = MAX_PAGE_SIZE;
		}
		if(pageSize < 1) {
			pageSize = 10;
		}
		if(query.isEmpty()) {
			 index();
			 return;
		}
		
		Page<JuziObject> pageResult = srv.query(query,currentPage,pageSize);

		renderJson(getJsonString(pageResult));
		return;

	}

	private String getJsonString(Page<JuziObject> pageResult){
		if(pageResult == null) {
			JSONObject result = new JSONObject();
			result.put("success",false);
			return result.toJSONString();
		}

		int totalPage = pageResult.getTotalPage() > 100 ?100:pageResult.getTotalPage();

		JSONObject result = new JSONObject();

		result.put("success",true);
		result.put("totalPage",totalPage);

		JSONArray arrayObjc = new JSONArray();
		if(arrayObjc!= null){
			for(JuziObject juzi: pageResult.getList()){
				JSONObject juziObject = new JSONObject();
				juziObject.put("content",juzi.getContent());
				juziObject.put("author",juzi.getAuthor());
				juziObject.put("from",juzi.getFrom());
				juziObject.put("id",juzi.getId());
				arrayObjc.add(juziObject);
			}
		}
		result.put("data",arrayObjc);
		return result.toJSONString();
	}

/*	public void juzi(){
		Integer juziId = super.getParaToInt("id", -1);

		Juzi juzi = mJuziDao.findById(juziId);

		if(juzi == null){
			renderError(404);
			return;
		}
		setAttr("juzi",juzi);
		setAttr("juziEx",juzi.getExt());
		render("juzi_detail.html");
	}*/

	public void group(){

		Integer groupId = super.getParaToInt("id", -1);
		int currentPage = super.getParaToInt("page", 1); //从1开始
		int pageSize = super.getParaToInt("size", 0);

		if(currentPage < 1) {
			currentPage = 1;
		}

		if(pageSize >MAX_PAGE_SIZE) {
			pageSize = MAX_PAGE_SIZE;
		}
		if(pageSize < 1) {
			pageSize = 10;
		}


		JuziGroup group = mGroupDao.findById(groupId);

		if(group == null){
			renderError(404);
			return;
		}

		String query= group.getTags();
		if(query.isEmpty()) {
			index();
			return;
		}

		Page<JuziObject> pageResult = srv.query(query,currentPage,pageSize);

		renderJson(getJsonString(pageResult));
		return;
	}

/*	@Before(AuthCacheClearInterceptor.class)
	public void clear() {
		//srv.clearCache();
		redirect("/");
	}*/
}
