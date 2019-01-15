package com.juzicool.seo.web;


import com.juzicoo.ipservcie.IPservcie;
import com.juzicool.core.PromiseExecutor;
import com.juzicool.data.db.SimpleDB;
import com.juzicool.seo.Services;
import com.juzicool.seo.db.WorkFlowTaskDB;
import com.juzicool.seo.model.WorkFlowLog;
import com.juzicool.webwalker.WalkFlowSchedule;
import com.juzicool.webwalker.WalkService;

import java.util.List;


public class IndexController extends BaseController {


    public void ipservice(){
       boolean start =  getParaToBoolean("start",true);

        IPservcie ipService = Services.iPservcie;

        if(start){
            if(ipService.isCollectFinish()){
                ipService.requestCollect();
            }
        }else{
            if(!ipService.isCollectFinish()){

            }
        }


        redirect("/index");
    }

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

    public void walkTask(){
        int taskID =  getParaToInt("id",-1);
        if(taskID == -1){
            renderError(404);
            return;
        }


        WorkFlowTaskDB db = WorkFlowTaskDB.get(taskID);

        List<SimpleDB.ListData<WorkFlowLog>> logList = db.getLatests();

        setAttr("logList",logList);
        render("walktask.html");
    }
}
