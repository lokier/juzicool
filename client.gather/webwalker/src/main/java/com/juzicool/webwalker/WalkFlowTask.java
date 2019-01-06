package com.juzicool.webwalker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public abstract class WalkFlowTask {


    public static Logger LOG = LoggerFactory.getLogger(WalkFlowTask.class);

    abstract int getTaskId();

    abstract String getTaskName();

    /**
     * 下一个要处理的WalkFlow；
     * @return  没有的话，返回null
     */
    abstract WalkFlow next();



    public WalkFlowTask(){

    }

    public boolean isRunning(){
        return false;
    }

    /**
     * 这里要处理下，保证next()下面有值。
     */
    abstract void onStart();

    abstract void onStop();

    //abstract void onCancel();

    WalkService mService;

    /*pacage*/ void start(WalkService service) {
        mService = service;

        //在后台线程里面执行。
        mService.getWalkThreadManager().sumbit(new Runnable() {
            @Override
            public void run() {
                //拿到client和wlakFlow，执行。

            }
        });

    }

    private void flow(WalkFlow flow,WalkClient client){

       // flow.execute();

    }

    @Override
    public String toString() {
        return "WalkFlowTask{" +
                "id='" + getTaskId() + '\'' +
                ",name='" + getTaskName() + '\'' +
                '}';
    }

}
