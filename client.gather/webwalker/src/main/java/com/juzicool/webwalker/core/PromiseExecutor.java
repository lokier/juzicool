package com.juzicool.webwalker.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.LinkedList;
import java.util.Queue;

public class PromiseExecutor {

    public static Logger LOG = LoggerFactory.getLogger(PromiseExecutor.class);


    public static void main(String[] args) {

        HandlerThread thread = new HandlerThread();

        thread.start();

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Handler handler = new Handler(thread.getLooper());

        Promise promise = new Promise.Builder().then(new Runnable() {
            @Override
            public void run() {

            }
        }).then(new Runnable() {
            @Override
            public void run() {

            }
        }).delay(10000).reject(new Promise.RunFunc() {
            @Override
            public void run(Promise promise) {

                Object error = promise.getRejectError();

            }
        }).resolve(new Promise.RunFunc() {
            @Override
            public void run(Promise promise) {
                Object data = promise.getResolveData();

            }
        })
         .build();


        PromiseExecutor executor = new PromiseExecutor();
        executor.startup(handler);

        executor.submit(promise);


        //空闲时关闭。
        //executor.shutdownWhileIdle(true);

        //等待关闭

    }

    private Handler mHander = null;
   // private boolean shutdownWhileIdle = false;
    private LinkedList<Promise> promises = new LinkedList<>();
    private int maxThreadSize = 5;
    Queue<PromiseThead> threadList = new LinkedList<>();


    public void startup(Handler handler) {
        mHander = new Handler(handler.getLooper());
       // shutdownWhileIdle = false;
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



/*    public void shutdownWhileIdle(boolean shutdownWhileIdle) {

        this.shutdownWhileIdle = shutdownWhileIdle;


    }*/


    public Promise submit(Runnable runnable){
        Promise promise =  new Promise.Builder(runnable).build();
        submit(promise);
        return promise;
    }


    public void submit(final Promise promise) {
        throwErrorIfShutdown();
        synchronized (this){
            if(promise.getStatus() != Promise.Status.PENDING){
                throw new IllegalArgumentException(" error promise status:" + promise.getStatus());
            }
            promise.status = Promise.Status.RUNNING;
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
                            nextPromise = promise;
                        } else {
                            //需要监听是否timeout
                            //TODO
                            monitorTimout = true;
                            final Promise _promise = promise;

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
                            //下一次优选执行。
                            nextPromise = promise;
                        }else{
                            promise.reject(th);
                        }

                    }
                }else{
                    //执行成功。
                    promise.status = Promise.Status.RESOLVED;
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

        void finnlayPromise(Promise promise) {
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

        }

        private void runWithoutExcpetion(Promise.RunFunc runFunc, Promise promise){
            try {
                if (runFunc != null) {
                    runFunc.run(promise);
                }
            }catch (Throwable th){
                LOG.warn(th.getMessage(),th);
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
