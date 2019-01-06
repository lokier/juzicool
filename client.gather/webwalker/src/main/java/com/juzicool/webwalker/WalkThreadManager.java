package com.juzicool.webwalker;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;

/***
 * 一个WalkTask就是一个Thread线程。
 */
public class WalkThreadManager {

    public static interface OnWalkCaseLisnter{

        /**
         * （在后台线程执行）
         * @param walkOk
         */
        void onResult(boolean walkOk);
        /**
         *  运行过程中出错（在后台线程执行）
         * @param th
         */
        void onError(Throwable th);
    }

    public static Logger LOG = LoggerFactory.getLogger(WalkThreadManager.class);

    Queue<WalkThreadManager.TaskData> queue = new LinkedList<>();
    Queue<WalkCaseThead> threadList = new LinkedList<>();

    private WalkService mWalkService;
    private int maxThreadSize = 5;

    WalkThreadManager(WalkService service){
        mWalkService = service;
    }

    public void setMaxThreadSize(int maxThreadSize) {
        this.maxThreadSize = maxThreadSize;
        dispatch();
    }

    public int getRunningThreadSize(){
        return threadList.size();
    }


    /**
     *  提交case到线程执行。
     * @param client
     * @param flow
     * @param lisnter
     */
    public void sumbit(WalkClient client,WalkFlow flow,WalkCase _case,OnWalkCaseLisnter lisnter) {
        if(client == null || flow == null){
            throw  new NullPointerException();
        }
        TaskData data = new TaskData();
        data.client = client;
        data.flow = flow;
        data.callback = lisnter;
        synchronized (this){
            queue.add(data);
        }
        dispatch();
    }

    public void sumbit(Runnable runnable) {
        TaskData data = new TaskData();
        data.runnable = runnable;
        synchronized (this){
            queue.add(data);
        }
        dispatch();
    }


    private void dispatch(){
        synchronized (this) {
            int caseSize = queue.size();
            int threadSize = threadList.size();
            int idleThreadSize = maxThreadSize - threadSize;
            if (idleThreadSize > 0) {
                int createTheadSize = Math.min(idleThreadSize, caseSize);

                for(int i = 0;i < createTheadSize;i++){
                    WalkCaseThead thead = new WalkCaseThead();
                    onThreadCreate(thead);
                    thead.start();
                }

            }
        }
    }

    protected  void onThreadCreate(WalkCaseThead thead){
        synchronized (this){
            threadList.add(thead);
        }
    }

    protected  void onThreadFinishend(WalkCaseThead thead){
        synchronized (this) {
            threadList.remove(thead);
        }
        dispatch();
    }

    private static class TaskData {
        WalkClient client;
        WalkFlow flow;
        WalkCase _case;
        Runnable runnable;
        OnWalkCaseLisnter callback;
    }

    private class WalkCaseThead extends Thread{

        boolean isStop = false;

        public void run(){
            while (!isStop){
                TaskData data = null;
                synchronized (WalkThreadManager.this){
                    data = queue.poll();
                }
                if(data == null) {
                    //没有case可以执行,退出线程执行。
                   break;
                }

                if(data.runnable != null){
                    try{
                        data.runnable.run();
                    }catch (Throwable ex){
                        LOG.error(ex.getMessage(),ex);
                    }

                    continue;
                }

                WalkFlow flow = data.flow;
                WalkCase _case = data._case;
                WalkClient client = data.client;
                Throwable onError = null;

                CaseWalkPormise pormise = new CaseWalkPormise();

                long startTime = System.currentTimeMillis();
                LOG.info("flow[" +flow.getName()+"]: start do case");

                _case.onCreate(client);
                TimeoutRunnable runnable = new TimeoutRunnable(data,pormise);

                try {
                    mWalkService.getHandler().postDelayed(runnable, _case.getTimeout());
                    _case.doCase(client, pormise);
                    pormise.waitingFinish();
                }catch (Throwable th){
                    onError = th;
                }finally {
                    mWalkService.getHandler().removeCallbacks(runnable);
                    runnable.dispose();
                    _case.onDestroy();
                }

                boolean doCaseOK = pormise._walkOk;
                if(data.callback!= null){
                    if(onError!= null) {
                        try{
                            data.callback.onError(onError);
                        }catch (Throwable th){
                            th.printStackTrace();
                            LOG.error(th.getMessage(),th);
                        }
                        doCaseOK = false;
                    }else{
                        try{
                            data.callback.onResult(doCaseOK);
                        }catch (Throwable th){
                            th.printStackTrace();
                            LOG.error(th.getMessage(),th);
                        }
                    }
                }

                LOG.info("flow[" +getName()+"]: finish do case = " + doCaseOK +", spend time: " + (System.currentTimeMillis()- startTime));

            }
            onThreadFinishend(this);
        }
    }

    private static class TimeoutRunnable implements Runnable{
        private TaskData data;
        private WalkPormise promise;
        TimeoutRunnable(TaskData data,WalkPormise p){
            this.data = data;
            promise = p;
        }

        @Override
        public void run() {
            LOG.info("        timeout do case = " + data._case.toString()  +" ,WalkFlow = " + data.flow.getName());

            try {
                data._case.onCancel();
            }catch (Throwable ex){

            }
            this.promise.reject();
            dispose();
        }

        public void dispose(){
            this.promise = null;
            data = null;
        }
    }

    private static class CaseWalkPormise implements WalkPormise {
       // WalkCase _case;
      //  long delay;

        Boolean _walkOk = null;

        @Override
        public void accept() {
            _walkOk = true;
        }

        @Override
        public void reject() {
            _walkOk = false;
        }

        public void waitingFinish() {
            while (_walkOk == null){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    LOG.warn(e.getMessage(),e);
                }
            }
        }
    }
}
