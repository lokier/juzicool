package com.juzicool.webwalker;


import com.gargoylesoftware.htmlunit.WebClient;
import com.juzicool.webwalker.core.Handler;
import com.juzicool.webwalker.core.Looper;

public class WalkService {

    public static void main(String[] args) {

        WalkService service = new WalkService();
        service.setMaxTaskThread(5); //设置最大的启动task线程个数
        service.prepare(); // 准备工作


        //开启一个WaklTask
        WalkTask task = new  WalkTask();
        service.startTaskNow(task);  //一个WalkTask就是一个Thread线程。



        //在task上面执行一个WalkFlow
        WalkFlow flow = new WalkFlow();
        flow.setName("测试flow").addCase(new WalkCase.DumpCase(),100);

        WalkClient client = WalkClient.build();

        task.sumbit(client,flow);  //执行一个流量操作；

    }



    private int maxTaskThread = 5;
    private Handler mHandler = null;

    public void startTaskNow(WalkTask task) {
        task.attachService(this);
        task.startup();
        //task.detachService();
    }

    public void setMaxTaskThread(int size) {
        this.maxTaskThread = size;
    }

    public Handler getHandler(){
        if(mHandler == null){
            throw new RuntimeException("must call prepare() first!!!!!");
        }
        return mHandler;
    }

    public void prepare() {
        if(mHandler == null) {
            synchronized (this) {
                if(mHandler == null) {
                    new Thread() {
                        public void run() {
                            Looper.prepare();
                            mHandler = new Handler();
                            Looper.loop();
                        }
                    }.start();

                    while (mHandler == null) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
