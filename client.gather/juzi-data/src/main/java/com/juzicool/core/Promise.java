package com.juzicool.core;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * pending: 初始状态，不是成功或失败状态。
 * running;运行状态，
 * Resolved: 意味着操作成功完成。
 * rejected: 意味着操作失败。
 *
 * 一旦状态改变，就不会再变，任何时候都可以得到这个结果。Promise 对象的状态改变，
 * 只有两种可能：从 Pending 变为 Resolved 和从 Pending 变为 Rejected。
 * 只要这两种情况发生，状态就凝固了，不会再变了，会一直保持这个结果。就算改变已经发生了，
 * 你再对 Promise 对象添加回调函数，也会立即得到这个结果。
 */
public class Promise {


    public  enum Status{
        PENDING,
        RUNNING,
        RESOLVED,
        REJECT;
    }

    public interface RunFunc{
        void run(Promise promise);
    }


    private static AtomicInteger IdGanerator = new AtomicInteger(0);

     Func[] funcList;
     private int funcIndex = 0;
     RunFunc[] rejectFunc;
     RunFunc[] resloveFunc;
     RunFunc[] finalFunc;

     Object error = null;
     Object success = null;
     final int id;
     Status status = Status.PENDING;
     boolean isAcitive = true;

     //long elaseTotalTime = 0L; //消耗总共时间（包含睡眠时间）
     long elaseRealTime = 0L;  //消耗真实时间（实在在的运行时间）
     long startTime = 0L;
     long endTime = 0L;
     private Builder builder = new Builder();

     public Promise(){
         id = IdGanerator.incrementAndGet();
     }

    public Promise then(Runnable runnable){
        builder.then(runnable);
        return this;
    }

    public Promise then(RunFunc runnable, long timeoutMillions){
        builder.then(runnable,timeoutMillions);
        return this;
    }

    public Promise delay(long timeMillions) {
        builder.delay(timeMillions);
        return this;
    }

    public Promise reject(RunFunc runFunc){
         builder.reject(runFunc);
        return this;
    }

    public Promise resolve(RunFunc runFunc){
        builder.resolve(runFunc);
        return this;
    }

    public Promise finall(RunFunc runFunc) {
        builder.finall(runFunc);
        return this;
    }

    /*pcakge*/synchronized void setRunningStatus(){
        builder.set(this);
        status = Status.RUNNING;
        builder =null;
    }



    public boolean isActive() {
        return isAcitive;
    }

    Handler mHandler = null;
    Runnable rejectOrResovlerCall = null;
    Runnable runnableTimeout = null;

    /*package*/ void destroy(){
        isAcitive = false;
        rejectFunc = null;
        resloveFunc = null;
        finalFunc = null;
        funcList = null;
        mHandler = null;
        rejectOrResovlerCall = null;
        runnableTimeout = null;
    }

    void prepareAction(Handler handler,Runnable rejectOrResovlerCall,Runnable runnableTimeout ){
        mHandler = handler;
        this.rejectOrResovlerCall = rejectOrResovlerCall;
        this.runnableTimeout = runnableTimeout;
    }

    /*pacage*/  synchronized void timeout() {
        if(mHandler == null){
            return;
        }
        Handler h = mHandler;
        mHandler = null;
        h.post(new Runnable() {
            @Override
            public void run() {
                if(rejectOrResovlerCall != null){
                    errorAndStop(new PromiseException("time out"));
                    rejectOrResovlerCall.run();
                }
                if(runnableTimeout != null){
                    h.removeCallbacks(runnableTimeout);
                    runnableTimeout = null;
                }
            }
        });

        return ;

    }

    public synchronized void reject(Object error){
        if(mHandler == null){
            return;
        }
        final Handler h = mHandler;
        mHandler = null;
        h.post(new Runnable() {
            @Override
            public void run() {
                if(rejectOrResovlerCall != null){
                    errorAndStop(error);
                    rejectOrResovlerCall.run();
                    rejectOrResovlerCall = null;
                }
                if(runnableTimeout != null){
                    h.removeCallbacks(runnableTimeout);
                    runnableTimeout = null;
                }
            }
        });

        return ;
    }

    /**
     * 传递给下一个值，最终
     * @param data
     */
    public final synchronized void accept(final Object data){
        if(mHandler == null){
            return;
        }
        final Handler h = mHandler;
        mHandler = null;
        h.post(new Runnable() {
            @Override
            public void run() {
                if(rejectOrResovlerCall != null){
                    success = data;

                   // status = Status.RESOLVED;
                    rejectOrResovlerCall.run();
                }
                if(runnableTimeout != null){
                    h.removeCallbacks(runnableTimeout);
                    runnableTimeout = null;
                }
            }
        });
        return ;
    }




    public final Object getResolveData(){
        return success;
    }

    public final Object getRejectError(){
        return error;
    }

    public final Status getStatus(){
        return status;
    }

    public void waitToFinished() {

        while (isAcitive){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public long getStartTime(){
        return startTime;
    }

    void errorAndStop(Object error){
       // System.out.println("errorAndStop");
       // hasError = true;

        this.error = error;
        status = Status.REJECT;
    }

    Func nextFunc(){
        if(funcList == null){
            return null;
        }
        if(funcIndex <  funcList.length){
            return funcList[funcIndex++];
        }
        return null;
    }



    /*pacage*/ static class Func{
         Runnable runnable;
        boolean needCheckTimeout = false;
        long delayFunc = 0L;
        long timeout = 0L;

        RunFunc runFunc;

        void doFunc(Promise promise){
            if(runnable != null) {
                runnable.run();
            }else if(runFunc!= null){
                runFunc.run(promise);
            }

        }
    }

    public static class Builder{

        private ArrayList<Func> funcList = new ArrayList<>(10);

        private ArrayList<RunFunc> rejectFunc = new ArrayList<>(3);
        private ArrayList<RunFunc> resolveFunc = new ArrayList<>(3);
        private ArrayList<RunFunc> finalRunc = new ArrayList<>(3);

        public Builder(){

        }

        public Builder(Runnable runnable){
            this.then(runnable);
        }

        public Builder then(Runnable runnable){
            Func func = new Func();
            func.needCheckTimeout = false;
            func.delayFunc = 0L;
            func.runnable = runnable;
            funcList.add(func);
            return this;
        }

        public Builder then(RunFunc runnable, long timeoutMillions){
            Func func = new Func();
            func.needCheckTimeout = true;
            func.timeout = timeoutMillions;
            func.runFunc = runnable;
            funcList.add(func);
            return this;
        }

        public Builder delay(long timeMillions) {
            Func func = new Func();
            func.needCheckTimeout = false;
            if(timeMillions > 0){
                func.delayFunc = timeMillions;
            }
            funcList.add(func);
            return this;
        }

        public Builder reject(RunFunc runFunc){
            rejectFunc.add(runFunc);
            return this;
        }

        public Builder resolve(RunFunc runFunc){
            resolveFunc.add(runFunc);
            return this;
        }

        public Builder finall(RunFunc runFunc) {
            finalRunc.add(runFunc);
            return this;
        }

        private void set(Promise promise){
            Func[] funcs = funcList.toArray(new Func[funcList.size()]);
            RunFunc[] resolveFuncs = resolveFunc.toArray(new RunFunc[resolveFunc.size()]);
            RunFunc[] rejectFuncs = rejectFunc.toArray(new RunFunc[rejectFunc.size()]);
            RunFunc[] finalRuncs = finalRunc.toArray(new RunFunc[finalRunc.size()]);

            promise.funcList = funcs;
            promise.rejectFunc = rejectFuncs;
            promise.resloveFunc = resolveFuncs;
            promise.finalFunc = finalRuncs;
           // promise.set(funcs,resolveFuncs,rejectFuncs,finalRuncs);
        }

        public Promise build(){
            Promise promise = new Promise();
            set(promise);
            return promise;
        }
    }

}
