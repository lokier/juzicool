package com.juzicool.seo.plugin;

import com.jfinal.plugin.IPlugin;
import com.juzicool.seo.AppConstant;
import com.juzicool.seo.Services;
import com.juzicool.seo.flow.ZhifuFlow1;
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
        StartPoint startPoint = StartPoint.Bulider.bySeconds(System.currentTimeMillis() + 10*1000,20*60 * 1000);

       /* DefaultWalkFlowTask flowTask = new DefaultWalkFlowTask(1);
        flowTask.setTaskName("ATest-Task");
        flowTask.addWalkFlow(new ZhifuFlow1());*/

        ZhifuFlowTask task = new ZhifuFlowTask();

        WalkFlowSchedule schedule = new WalkFlowSchedule(1,startPoint,task);
        service.submit(schedule);


        service.setWalkFlowListener(new WalkFlowListener() {
            @Override
            public void onStartTask(WalkFlowTask task) {
                System.out.println("onStartTask:" + task.getTaskName()+","+task.getTaskId());
            }

            @Override
            public void onStartFlow(WalkFlowTask task, WalkFlow flow, WalkClient client) {
                System.out.println("onStartFlow:" + task.getTaskName()+","+task.getTaskId());

            }

            @Override
            public void onDoCase(WalkFlowTask task, WalkFlow flow, WalkClient client, WalkCase _case) {
                System.out.println("onDoCase:" + task.getTaskName()+","+task.getTaskId());

            }


            @Override
            public void onFinishFlow(WalkFlowTask task, WalkFlow flow, WalkClient client, boolean hasError) {
                System.out.println("onFinishFlow:" + task.getTaskName()+","+task.getTaskId());

            }

            @Override
            public void onFinishTask(WalkFlowTask task) {
                System.out.println("onFinishTask:" + task.getTaskName()+","+task.getTaskId());

            }
        });
    }

    @Override
    public boolean stop() {
        return false;
    }
}
