package com.juzicool.seo.plugin;

import com.jfinal.plugin.IPlugin;
import com.juzicool.seo.AppConstant;
import com.juzicool.seo.Services;
import com.juzicool.seo.flow.RandomFlowTask;
import com.juzicool.seo.flow.ZhifuFlowTask;
import com.juzicool.webwalker.*;


public class WebwalkPlugin implements IPlugin {


    public static WalkService me;

    @Override
    public boolean start() {
        me = new WalkService();

        Integer maxThreadSize = AppPlugin.me.getConfigDB().KV().get(AppConstant.Config.MAX_THREAD_NUMB,20);

        me.setMaxTaskThread(maxThreadSize);
        me.prepare(); // 准备工作
        Services.walkService = me;

        prepareTask(me);
        return true;
    }

    private void initConifg(){

    }

    private void prepareTask(WalkService service){
        StartPoint startPoint = StartPoint.Bulider.bySeconds(System.currentTimeMillis() + 15*60*1000,30*60 * 1000);

        ZhifuFlowTask task = new ZhifuFlowTask();

        RandomFlowTask rTask = new RandomFlowTask();

        WalkFlowSchedule schedule = new WalkFlowSchedule(1,startPoint,rTask);
        service.submit(schedule);


        service.setWalkFlowListener(new WakFlowHander());
    }

    @Override
    public boolean stop() {
        return false;
    }
}
