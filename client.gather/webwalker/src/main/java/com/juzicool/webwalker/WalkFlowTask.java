package com.juzicool.webwalker;

import com.juzicool.webwalker.core.Looper;
import com.juzicool.webwalker.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public abstract class WalkFlowTask {


    public static Logger LOG = LoggerFactory.getLogger(WalkFlowTask.class);

    abstract public int getTaskId();

    abstract public String getTaskName();

    /**
     * 下一个要处理的WalkFlow；
     * @return  没有的话，返回null
     */
    abstract protected WalkFlow next();


    abstract protected WalkClient createWalkClient();

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
        Promise promise = mRunningPromise;
        return promise!=null && promise.isActive();
    }



    private Promise mRunningPromise;

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
                        dispathNextWalkFlowPromise();
                    }
                });
            }
        });


    }

    /*pacage*/ void stop() {

        mService.getPromiseExecutor().submit(new Runnable() {
            @Override
            public void run() {
                //在后台线程执行。
                onStopInBackground();
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
        });

    }

    /**
     * must run in Looper线程；
     */
    private void dispathNextWalkFlowPromise(){
        if(Looper.myLooper() == null){
            throw new RuntimeException("must run in looper thread");
        }

        if(isRunning()){
            return;
        }
        //在后台线程里面执行。
        Promise promise = createPromise();
        if(promise == null){
            LOG.debug("createPromise null, is Stop");
            stop();
            return ;
        }

        mRunningPromise = promise;
        mService.getPromiseExecutor().submit(promise);

    }




    private Promise createPromise( ){


        final WalkFlow walkFlow = next();

        if(walkFlow == null){
            LOG.debug("createPromise falil:  walkFlow is null");
            return null;
        }

        final WalkClient walkClient = createWalkClient();

        LOG.info("dispatch  walkFlow: " + walkFlow.getName());
        Promise.Builder builder = new Promise.Builder();


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
        builder.finall(new Promise.RunFunc() {
            @Override
            public void run(Promise promise) {

                final boolean hasError = promise.getStatus() == Promise.Status.REJECT;
                mService.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        releaseWalkClient(walkClient);
                        mRunningPromise = null;
                        if(mService.walkFlowListener!=null){
                            mService.walkFlowListener.onFinishFlow(WalkFlowTask.this,walkFlow,walkClient,hasError);
                        }
                        dispathNextWalkFlowPromise();
                    }
                });

            }
        });

        return builder.build();
    }


    @Override
    public String toString() {
        return "WalkFlowTask{" +
                "id='" + getTaskId() + '\'' +
                ",name='" + getTaskName() + '\'' +
                '}';
    }

}
