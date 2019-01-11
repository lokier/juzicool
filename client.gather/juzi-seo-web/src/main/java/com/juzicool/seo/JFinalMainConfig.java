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

package com.juzicool.seo;


import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.json.MixedJsonFactory;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.template.Engine;
import com.juzicool.seo.plugin.AppPlugin;
import com.juzicool.seo.plugin.IPServicePlugin;
import com.juzicool.seo.plugin.WebwalkPlugin;
import com.juzicool.seo.web.IndexController;


/**
 * JFinalClubConfig
 */
public class JFinalMainConfig extends JFinalConfig {
	
	// 先加载开发环境配置，再追加生产环境的少量配置覆盖掉开发环境配置
	private static Prop p = PropKit.use("config.properties");
	

	/**
	 * 启动入口，运行此 main 方法可以启动项目，此 main 方法可以放置在任意的 Class 类定义中，不一定要放于此
	 * 
	 * 使用本方法启动过第一次以后，会在开发工具的 debug、run configuration 中自动生成
	 * 一条启动配置项，可对该自动生成的配置再继续添加更多的配置项，例如 Program arguments
	 * 可配置为：src/main/webapp 80 / 5
	 * 
	 */
	public static void main(String[] args) {
		JFinal.start("juzi-seo-web/src/main/webapp", 8088, "/", 5);

	}
	
	public void configConstant(Constants me) {
		me.setDevMode(p.getBoolean("devMode", false));
		me.setJsonFactory(MixedJsonFactory.me());
		
		// 支持 Controller、Interceptor 之中使用 @Inject 注入业务层，并且自动实现 AOP
		me.setInjectDependency(true);
	}
	
    /**
     * 路由拆分到 FrontRutes 与 AdminRoutes 之中配置的好处：
     * 1：可分别配置不同的 baseViewPath 与 Interceptor
     * 2：避免多人协同开发时，频繁修改此文件带来的版本冲突
     * 3：避免本文件中内容过多，拆分后可读性增强
     * 4：便于分模块管理路由
     */
    public void configRoute(Routes me) {
	   // me.add(new FrontRoutes());
	   // me.add(new AdminRoutes());
		//me.add("/hello",HelloContorller.class);
		me.add("/", IndexController.class,"/_VIEW");

	}
    
    /**
     * 配置模板引擎，通常情况只需配置共享的模板函数
     */
	public void configEngine(Engine me) {
		me.setDevMode(p.getBoolean("devMode", false));
		me.addSharedFunction("/_VIEW/common/_layout.html");

		// 添加角色、权限指令
		//me.addDirective("role", RoleDirective.class);
		//me.addDirective("permission", PermissionDirective.class);
	//	me.addDirective("perm", PermissionDirective.class);		// 配置一个别名指令

		// 添加角色、权限 shared method
		//me.addSharedMethod(AdminAuthKit.class);

		//me.addSharedFunction("/_view/common/_layout.html");
		//me.addSharedFunction("/_view/common/_paginate.html");

	//	me.addSharedFunction("/_view/_admin/common/__admin_layout.html");
	//	me.addSharedFunction("/_view/_admin/common/_admin_paginate.html");
	}
	
    public void configPlugin(Plugins me) {
		me.add(new AppPlugin());
		me.add(new WebwalkPlugin());
		me.add(new IPServicePlugin());

	}
    
    public void configInterceptor(Interceptors me) {
	   // me.add(new LoginSessionInterceptor());
    }
    
    public void configHandler(Handlers me) {
	   // me.add(DruidKit.getDruidStatViewHandler()); // druid 统计页面功能
	  //  me.add(new UrlSeoHandler());             	// index、detail 两类 action 的 url seo
    }
    
    /**
     * 本方法会在 jfinal 启动过程完成之后被回调，详见 jfinal 手册
     */
	public void afterJFinalStart() {
		// 调用不带参的 renderJson() 时，排除对 loginAccount、remind 的 json 转换
/*		JsonRender.addExcludedAttrs(
                LoginService.loginAccountCacheName,
                LoginSessionInterceptor.remindKey,
                FriendInterceptor.followNum, FriendInterceptor.fansNum, FriendInterceptor.friendRelation
        );*/
		
		// 让 druid 允许在 sql 中使用 union
		// https://github.com/alibaba/druid/wiki/%E9%85%8D%E7%BD%AE-wallfilter
	}
}






