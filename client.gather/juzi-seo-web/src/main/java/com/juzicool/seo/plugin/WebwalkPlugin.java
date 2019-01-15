package com.juzicool.seo.plugin;

import com.jfinal.plugin.IPlugin;
import com.juzicool.seo.AppConstant;
import com.juzicool.seo.Services;
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

        ZhifuFlowTask task = new ZhifuFlowTask();

        WalkFlowSchedule schedule = new WalkFlowSchedule(1,startPoint,task);
        service.submit(schedule);

       // final AtomicInteger tot = new AtomicInteger();

        service.setWalkFlowListener(new WalkFlowListener() {

            private int totalFlowCount = 0;
            private int totalFlowSuccess = 0;

            @Override
            public void onStartTask(WalkFlowTask task) {
                totalFlowCount = 0;
                totalFlowSuccess =0;
                System.out.println("onStartTask:" + task.getTaskName()+","+task.getTaskId());
            }

            @Override
            public void onStartFlow(WalkFlowTask task, WalkFlow flow, WalkClient client) {
                System.out.println("onStartFlow:" + task.getTaskName()+","+task.getTaskId());
                totalFlowCount++;
            }

            @Override
            public void onDoCase(WalkFlowTask task, WalkFlow flow, WalkClient client, WalkCase _case) {
                System.out.println("onDoCase:" + task.getTaskName()+","+task.getTaskId());

            }


            @Override
            public void onFinishFlow(WalkFlowTask task, WalkFlow flow, WalkClient client, boolean hasError) {
                System.out.println("onFinishFlow:" + task.getTaskName()+","+task.getTaskId());

                if(!hasError){
                    totalFlowSuccess++;
                }

                task.setProcessText(String.format("已處理%d個flow，成功%d個",totalFlowCount,totalFlowSuccess));

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
