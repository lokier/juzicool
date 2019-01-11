package com.juzicool.seo.web;


import com.juzicool.seo.Services;
import com.juzicool.webwalker.WalkFlowSchedule;
import com.juzicool.webwalker.WalkService;

public class IndexController extends BaseController {

    public void index(){

        WalkService walkService = Services.walkService;

      //  walkService.getMaxTaskThread()
        WalkFlowSchedule schedule;
       // schedule.getWalkFlowTask().getTaskName();
       //schedul
        setAttr("walkService",walkService);
        render("index.html");
    }
}
