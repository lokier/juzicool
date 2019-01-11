package com.juzicool.seo.web;


import com.juzicoo.ipservcie.IPservcie;
import com.juzicool.core.PromiseExecutor;
import com.juzicool.seo.Services;
import com.juzicool.webwalker.WalkFlowSchedule;
import com.juzicool.webwalker.WalkService;


public class IndexController extends BaseController {

    public void index(){

        WalkService walkService = Services.walkService;

      //  walkService.getMaxTaskThread()
        PromiseExecutor promiseExecutor = Services.walkService.getPromiseExecutor();
        IPservcie ipService = Services.iPservcie;

        setAttr("ip_proxy_file_path",Services.iPservcie.getFile().getAbsolutePath());
        setAttr("walkService",walkService);
        setAttr("ipService",ipService);
        setAttr("promiseExecutor",promiseExecutor);

        //Services.iPservcie.

        render("index.html");
    }
}
