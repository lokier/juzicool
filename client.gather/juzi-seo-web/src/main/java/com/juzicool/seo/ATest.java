package com.juzicool.seo;

import com.juzicool.seo.flow.ZhifuFlow1;
import com.juzicool.webwalker.*;

public class ATest {

    public static void main(String[] args){
        WalkService service = new WalkService();
        service.setMaxTaskThread(5); //设置最大的启动task线程个数
        service.prepare(); // 准备工作


        //开启一个WaklTask
        WalkTask task = new  WalkTask();
        service.startTaskNow(task);  //一个WalkTask就是一个Thread线程。



        //在task上面执行一个WalkFlow
        ZhifuFlow1 flow = new ZhifuFlow1();

        WalkClient client = WalkClient.build();

        task.sumbit(client,flow);  //执行一个流量操作；
    }
}
