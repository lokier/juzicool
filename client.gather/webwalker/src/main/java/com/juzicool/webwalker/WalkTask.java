package com.juzicool.webwalker;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;

/***
 * 一个WalkTask就是一个Thread线程。
 */
public class WalkTask {

    public static Logger LOG = LoggerFactory.getLogger(WalkTask.class);

    private WalkService mService = null;
    private static final long IDLE_INTERVAL_TIME = 800;

    private WalkTaskThead mRunningThread = null;

    Queue<TaskData> queue = new LinkedList<>();

    public void startup() {
        if(mService == null) {
             throw new RuntimeException("service should not null");
        }

        if(mRunningThread != null) {
            LOG.info("walktsk is running");
            return ;
        }

        synchronized (this){
            if(mRunningThread != null) {
                return ;
            }
            mRunningThread = new WalkTaskThead();
            mRunningThread.start();
        }
    }

    public WalkService getWalkService(){
        return mService;
    }

    public boolean isRunning(){
        WalkTaskThead w = mRunningThread;
        return w != null;
    }


    void attachService(WalkService walkService) {
        mService = walkService;
    }

    public void sumbit(WalkClient client,WalkFlow flow) {
        sumbit(client,flow,null);
    }

        /**
         *
         * @param client
         * @param flow
         * @param finishCallback
         */
    public void sumbit(WalkClient client,WalkFlow flow,Callback<Boolean> finishCallback) {
        if(client == null || flow == null){
            throw  new NullPointerException();
        }
        TaskData data = new TaskData();
        data.client = client;
        data.flow = flow;
        data.callback = finishCallback;
        synchronized (this){
            queue.add(data);
        }
    }

    private static class TaskData {
        WalkClient client;
        WalkFlow flow;
        Callback<Boolean> callback;
    }

    private class WalkTaskThead extends Thread{

        boolean isStop = false;

        public void run(){
            LOG.debug("START running task....");

            while (!isStop){
                TaskData data = null;
                synchronized (WalkTask.this){
                    data = queue.poll();
                }
                if(data == null) {
                    LOG.debug("queue is empty. sleep");
                    try {
                        Thread.sleep(IDLE_INTERVAL_TIME);
                    } catch (InterruptedException e) {
                       LOG.warn(e.getMessage(),e);
                    }
                    continue;
                }

                LOG.info("    execute WalkFlow: " + data.flow.getName() +", by WalkClient : " + data.client.toString());

                try{
                    data.flow.execute(WalkTask.this,data.client);
                    data.flow.onFinished(true,null);

                    if(data.callback!= null){
                        data.callback.onCallback(true);
                    }
                    LOG.info("    finished WalkFlow: " + data.flow.getName());

                }catch (Throwable ex){
                    ex.printStackTrace();
                    LOG.error(ex.getMessage(),ex);
                    data.flow.onFinished(false,ex);

                    if(data.callback!= null){
                        data.callback.onCallback(false);
                    }
                }

            }
            mRunningThread = null;
            LOG.debug("STOP running task....");

        }
    }
}
