package com.juzicool.webwalker;

import com.juzicool.core.Looper;
import com.juzicool.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;


public abstract class WalkFlowTask {


    public static Logger LOG = LoggerFactory.getLogger(WalkFlowTask.class);

    abstract public int getTaskId();

    abstract public String getTaskName();

    /**
     * 下一个要处理的WalkFlow；
     * @return  没有的话，返回null
     */
    abstract protected WalkFlow next();

    abstract protected WalkClient createWalkClient(WalkFlow flow);

    abstract protected void releaseWalkClient(WalkClient client);


    /**
     * 这里执行完之后，保证next()下面有值。会在后台线程执行。
     */
    abstract protected void onStartInBackgound();


    /**
     * 这里执行完之后，会在后台线程执行。
     */
    abstract protected void onStopInBackground();


    public WalkFlowTask(){

    }

    public boolean isRunning(){
        //Promise promise = mRunningPromise;
        ArrayList<Promise> ps = new ArrayList<>(mRunningPromise);
        for(Promise p: ps){
            if(p.isActive()){
                return true;
            }
        }
        return false;
    }

    public int getRunningCount() {
        return mRunningPromise.size();
    }


    private LinkedList<Promise> mRunningPromise = new LinkedList<>();

    WalkService mService;


    /*pacage*/ void start(WalkService service) {
        mService = service;

        if(isRunning()){
            return;
        }

        if(mService.walkFlowListener!= null){
            mService.walkFlowListener.onStartTask(this);
        }

        mService.getPromiseExecutor().submit(new Runnable() {
            @Override
            public void run() {
                //在后台线程执行。
                onStartInBackgound();

                mService.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        //开始执行task
                        int size = mService.getRunningFlowLimit() - mService.getRunningWorkFlowCount();
                        if(size < 0){
                            size = 0;
                        }
                        for(int i= 0; i < size;i++){
                            dispathNextWalkFlowPromise();
                        }
                    }
                });
            }
        });


    }


    /**
     * must run in Looper线程；
     */
    private boolean dispathNextWalkFlowPromise(){
        if(Looper.myLooper() == null){
            throw new RuntimeException("must run in looper thread");
        }

        //在后台线程里面执行。
        final WalkFlow walkFlow = next();
        if(walkFlow == null){
            LOG.debug("createPromise null, not any more");
            return false;
        }
        Promise promise = createPromise(walkFlow);
        mRunningPromise.add(promise);
        mService.getPromiseExecutor().submit(promise);
        return true;
    }


    private Promise createPromise(WalkFlow walkFlow ){

        final WalkClient walkClient = createWalkClient(walkFlow);

        LOG.info("dispatch  walkFlow: " + walkFlow.getName());
        Promise builder = new Promise();


        // flow star:
        builder.then(new Runnable() {
            @Override
            public void run() {
                if(mService.walkFlowListener!=null){
                    mService.walkFlowListener.onStartFlow(WalkFlowTask.this,walkFlow,walkClient);
                }
            }
        });

        walkFlow.createPromise(builder,this,walkClient);

        //flow finish
        builder.finalFunc(new Promise.RunFunc() {
            @Override
            public void run(final Promise promise) {

                final boolean hasError = promise.getStatus() == Promise.Status.REJECT;
                mService.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        releaseWalkClient(walkClient);
                       // int oldSize =
                        mRunningPromise.remove(promise);
                        if(mService.walkFlowListener!=null){
                            mService.walkFlowListener.onFinishFlow(WalkFlowTask.this,walkFlow,walkClient,hasError);
                        }
                        boolean hasNew = dispathNextWalkFlowPromise();

                        //整個task 停止
                        if(!hasNew && mRunningPromise.size() == 0){
                            mService.getPromiseExecutor().submit(new Runnable() {
                                @Override
                                public void run() {
                                    //在后台线程执行。
                                    onStopInBackground();
                                }
                            });
                            mService.getHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    if(mService.walkFlowListener!= null){
                                        mService.walkFlowListener.onFinishTask(WalkFlowTask.this);
                                    }
                                    mService = null;
                                }
                            });
                        }
                    }
                });

            }
        });

        return builder;
    }


    @Override
    public String toString() {
        return "WalkFlowTask{" +
                "id='" + getTaskId() + '\'' +
                ",name='" + getTaskName() + '\'' +
                '}';
    }

}
