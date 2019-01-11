package com.juzicool.core;


import jdk.internal.org.objectweb.asm.Handle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class PromiseExecutor {

    private Handler mHander = null;
   // private boolean shutdownWhileIdle = false;
    private LinkedList<Promise> promises = new LinkedList<>();
    private int maxThreadSize = 5;
    Queue<PromiseThead> threadList = new LinkedList<>();

    private ArrayList<Promise> mRunningPromise = new ArrayList<>(50);

    private PromiseListener promiseListener;

    public void startup(Handler handler) {
        if(handler == null){
            if(mHander == null) {
                new Thread() {
                    public void run() {
                        Looper.prepare();
                        mHander = new Handler();
                        Looper.loop();
                    }
                }.start();
                while(mHander == null){
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }else {
            mHander = new Handler(handler.getLooper());
        }
       // shutdownWhileIdle = false;
    }

    public Handler getHandler(){
        return mHander;
    }

    public PromiseListener getPromiseListener() {
        return promiseListener;
    }

    public void setPromiseListener(PromiseListener promiseListener) {
        this.promiseListener = promiseListener;
    }

    public void setMaxThreadSize(int maxThreadSize) {
        this.maxThreadSize = maxThreadSize;
        if(mHander!= null){
            mHander.post(new Runnable() {
                @Override
                public void run() {
                    dispatch();
                }
            });
        }
    }


    public int getRunningThreadSize(){
        return threadList.size();
    }

    public boolean isShutdown(){
        return mHander == null;
    }

    /**
     * 返回待处理的promise个数
     * @return
     */
    public int getPenddingPromiseSize(){
        return promises.size();
    }

    public Promise[] getRunningPromise(){
        return mRunningPromise.toArray(new Promise[mRunningPromise.size()]);
    }

    /**
     * 在looper线程
     * @param promise
     */
    void onPromiseStart(Promise promise){
        PromiseListener ls = promiseListener;
        mRunningPromise.add(promise);
        if(ls!= null){
            ls.onStart(promise);
        }
    }
    /**
     * 在looper线程
     * @param promise
     */
    void onPromiseFinished(Promise promise){
        PromiseListener ls = promiseListener;
        mRunningPromise.remove(promise);
        if(ls!= null){
            ls.onEnd(promise);
        }
    }

    public Promise submit(Runnable runnable){
        Promise promise =  new Promise().then(runnable);
        submit(promise);
        return promise;
    }


    public void submit(final Promise promise) {
        throwErrorIfShutdown();
        synchronized (this){
            if(promise.getStatus() != Promise.Status.PENDING){
                throw new IllegalArgumentException(" error promise status:" + promise.getStatus());
            }
            promise.setRunningStatus();
        }

        mHander.post(new Runnable() {
            @Override
            public void run() {
                if(promise.getStatus() != Promise.Status.RUNNING){
                    return;
                }
                enqueue(promise,false);

            }
        });

    }

    private void throwErrorIfShutdown(){
        if(isShutdown()){
            throw new IllegalStateException("is shutdown now.(use startup() !!!)");
        }
    }


    private   void onThreadCreate(PromiseThead thead){
        synchronized (this){
            threadList.add(thead);
        }
    }

    public int getCurrentThreadSize(){
        return threadList.size();
    }

    private  void onThreadFinishend(PromiseThead thead){
        synchronized (this) {
            threadList.remove(thead);
        }
        dispatch();
    }


    private void enqueue(Promise promise,boolean priority){
        synchronized (PromiseExecutor.this){
            //优先继续执行
            if(priority){
                promises.addLast(promise);
            }else{
                promises.addFirst(promise);
            }
        }
        dispatch();
    }


    private void dispatch(){
        synchronized (this) {
            int caseSize = promises.size();
            int threadSize = threadList.size();
            int idleThreadSize = maxThreadSize - threadSize;
            if (idleThreadSize > 0) {
                int createTheadSize = Math.min(idleThreadSize, caseSize);
                for(int i = 0;i < createTheadSize;i++){
                    PromiseThead thead = new PromiseThead();
                    onThreadCreate(thead);
                    thead.start();
                }
            }
        }
    }


    private class PromiseThead extends Thread{

        boolean isStop = false;

        public void run(){
            Promise nextPromise = null;
            while (!isStop){
                //WalkThreadManager.TaskData data = null;
                Promise promise = nextPromise;
                nextPromise = null;
                synchronized (PromiseExecutor.this){
                    if(promise == null) {
                        promise = promises.poll();
                    }
                }


                if(promise == null) {
                    //没有case可以执行,退出线程执行。
                    break;
                }

                if(promise.getStatus()!= Promise.Status.RUNNING){
                    finnlayPromise(promise);
                    continue;
                }

                final Promise _promise = promise;


                //判断时候首次执行
                if(promise.startTime == 0L){
                    promise.startTime = System.currentTimeMillis();
                    mHander.post(new Runnable() {
                        @Override
                        public void run() {
                            onPromiseStart(_promise);
                        }
                    });
                }

                //long startElapseRealTime = System.currentTimeMillis();


                Promise.Func func =  promise.nextFunc();

                //还有case要执行，
                boolean monitorTimout = false;
                if(func != null) {

                    //1，执行不需要检查timeou的情况
                    //2,空操作,等待一阵时间后
                    try {
                        long delayTime = func.delayFunc;
                        boolean checkTimeOut = func.needCheckTimeout;

                        //延迟执行Promoise
                        if (delayTime > 0) {
                            delayPromise(promise, delayTime);
                        } else if (!checkTimeOut) {
                            func.doFunc(promise);
                            nextPromise = promise;   //下一次优选执行。
                        } else {
                            //需要监听是否timeout
                            //TODO
                            monitorTimout = true;

                            Runnable timeoutRunnable =  new Runnable() {
                                @Override
                                public void run() {
                                    _promise.timeout();
                                }
                            };
                            mHander.postDelayed(timeoutRunnable,func.timeout);
                            promise.prepareAction(mHander, new Runnable() {
                                @Override
                                public void run() {
                                    //放在promise继续执行。
                                    enqueue(_promise,true);
                                }
                            },timeoutRunnable);
                            func.doFunc(promise);

                        }
                    }catch (Throwable th){
                        if(!monitorTimout){
                            promise.errorAndStop(th);

                        }else{
                            promise.reject(th);
                        }
                        //下一次优选执行。
                        nextPromise = promise;
                    }
                }else{
                    //执行成功。
                    promise.status = Promise.Status.RESOLVED;
                    //下一次优选执行。
                    nextPromise = promise;
                }
            }

            Handler  h = mHander;
            if(h == null){
                return;
            }
            h.post(new Runnable() {
                @Override
                public void run() {
                    onThreadFinishend(PromiseThead.this);
                }
            });
        }

        void finnlayPromise(final Promise promise) {
            Handler h = mHander;
            if (h == null) {
                return;
            }

            if (promise.status == Promise.Status.REJECT) {
                if (promise.rejectFunc != null) {
                    for (Promise.RunFunc runFunc : promise.rejectFunc) {
                        runWithoutExcpetion(runFunc, promise);
                    }

                }
            } else if (promise.status == Promise.Status.RESOLVED) {
                if (promise.resloveFunc != null) {
                    for (Promise.RunFunc runFunc : promise.resloveFunc) {
                        runWithoutExcpetion(runFunc, promise);
                    }
                }
            }

            if (promise.finalFunc != null) {
                for (Promise.RunFunc runFunc : promise.finalFunc) {
                    runWithoutExcpetion(runFunc, promise);
                }
            }



            promise.destroy();
            //标记结束时间
            promise.endTime = System.currentTimeMillis();
            mHander.post(new Runnable() {
                @Override
                public void run() {
                    onPromiseFinished(promise);
                }
            });
        }

        private void runWithoutExcpetion(Promise.RunFunc runFunc, Promise promise){
            try {
                if (runFunc != null) {
                    runFunc.run(promise);
                }
            }catch (Throwable th){
                th.printStackTrace();
                //LOG.warn(th.getMessage(),th);
            }
        }

        void delayPromise(final Promise promise,long delay){

            Handler  h = mHander;
            if(h == null){
                return;
            }

            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    enqueue(promise,true);
                }
            },delay);

        }


    }

}
