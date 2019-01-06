package com.juzicool.webwalker.core;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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


    public static class Builder{

        private ArrayList<Func> funcList = new ArrayList<>();

        private RunFunc rejectFunc;
        private RunFunc resolveFunc;
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
            rejectFunc = runFunc;
            return this;
        }

        public Builder resolve(RunFunc runFunc){
            resolveFunc = runFunc;
            return this;
        }

        public Promise build(){
            Func[] funcs = funcList.toArray(new Func[funcList.size()]);
            return new Promise(funcs,resolveFunc,rejectFunc);
        }
    }

    public interface RunFunc{
        void run(Promise promise);
    }


    private static AtomicInteger IdGanerator = new AtomicInteger(0);

     Func[] funcList;
     private int funcIndex = 0;
    RunFunc rejectFunc;
    RunFunc resloveFunc;

     Object error = null;
     Object success = null;
     final int id;
     Status status = Status.PENDING;
     //boolean hasError = false;
     //boolean activeRejectOrResovle = true;

    private  Promise(Func[] funcs,RunFunc resolve,RunFunc reject){
        funcList = funcs;
        this.rejectFunc = reject;
        this.resloveFunc = resolve;
        id = IdGanerator.incrementAndGet();
        status = Status.PENDING;
    }


    Handler mHandler = null;
    Runnable rejectOrResovlerCall = null;
    Runnable runnableTimeout = null;

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
    public final synchronized void reslove(final Object data){
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

    private static class TimeountRunable implements Runnable{

        Handler h;
        Promise promise;

        public void run(){

        }
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

}
