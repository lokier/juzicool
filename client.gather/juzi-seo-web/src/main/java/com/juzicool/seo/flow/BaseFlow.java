package com.juzicool.seo.flow;


import com.juzicoo.ipservcie.IPservcie;
import com.juzicool.core.Promise;
import com.juzicool.seo.plugin.WakFlowHander;
import com.juzicool.webwalker.*;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Random;

public abstract class BaseFlow extends WalkFlow {

    private static String currentTimeDesc(){
        return DateFormatUtils.format(System.currentTimeMillis(),"yyyy-MM-dd'T'HH:mm:ss");
    }
    public static void main(String[] args){


        WalkService service = new WalkService();
        service.prepare();

        DefaultWalkFlowTask task = new DefaultWalkFlowTask(89384);
        task.setTaskName("测试task");

        BaseFlow flow = new JianshuFlow();

        flow = new DirectFlow();

        task.addWalkFlow(flow);


        service.setMaxTaskThread(5);
        System.out.println("开始flow....");
        StartPoint startPoint = StartPoint.Bulider.bySeconds(System.currentTimeMillis() + 3*1000,30*60 * 1000);
        WalkFlowSchedule schedule = new WalkFlowSchedule(1,startPoint,task);
        service.setWalkFlowListener(new WalkFlowListener() {
            @Override
            public void onStartTask(WalkFlowTask task) {
                System.out.println("onStartTask....");

            }

            @Override
            public void onStartFlow(WalkFlowTask task, WalkFlow flow, WalkClient client) {
                System.out.println("onStartFlow....");

            }

            @Override
            public void onDoCase(WalkFlowTask task, WalkFlow flow, WalkClient client, WalkCase _case) {

            }

            @Override
            public void onFlowProgerssChanged(WalkFlowTask task, WalkFlow flow, int progress, String progressText) {
                System.err.println(String.format("[%s ,progress=%d%%]%s\n", currentTimeDesc(),progress,progressText));

            }

            @Override
            public void onFinishFlow(WalkFlowTask task, WalkFlow flow, WalkClient client, boolean hasError) {
                System.out.println("onFinishFlow....");

            }

            @Override
            public void onFinishTask(WalkFlowTask task) {
                System.out.println("onFinishTask....");

            }
        });
        service.submit(schedule);


    }



    /**
     * 内链
     */
    public void addInterLinkCase(){
        Random random = new Random(System.currentTimeMillis());
        super.addCase(new InterRandomCase.SearchCase(),random.nextInt(3000));
        int count = random.nextInt(4) + 1;
        while (count-->0){
            super.addCase(new InterRandomCase.ClickSearchResultCase(),random.nextInt(60* 1000));
        }

        super.addCase(new InterRandomCase.DumpCase(),random.nextInt(2 * 1000));

    }

}
