package com.juzicool.seo;

import com.jfinal.core.JFinal;
import com.juzicool.seo.flow.ZhifuFlow1;
import com.juzicool.webwalker.*;

public class ATest {

    public static void main(String[] args){


        JFinal.start("src/main/webapp", 8088, "/", 5);

     /*   WalkService service = new WalkService();
        service.setMaxTaskThread(5); //设置最大的启动task线程个数
        service.prepare(); // 准备工作

        StartPoint startPoint = StartPoint.Bulider.bySeconds(System.currentTimeMillis() + 1000,1000000000);

        DefaultWalkFlowTask flowTask = new DefaultWalkFlowTask(1);
        flowTask.setTaskName("ATest-Task");
        flowTask.addWalkFlow(new ZhifuFlow1());

        WalkFlowSchedule schedule = new WalkFlowSchedule(1,startPoint,flowTask);
        service.submit(schedule);*/

       // service.shutdownWhileIdle(true);
       // service.waitUntilShutdown();
      //  System.out.println("finished service");
    }
}
