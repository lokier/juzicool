package com.juzicool.webwalker;

import com.juzicool.webwalker.core.Promise;
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


    abstract WalkClient createWalkClient();

    abstract void releaseWalkClient();



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


    private Promise mRunningPromise;

    WalkService mService;

    /*pacage*/ void start(WalkService service) {
        mService = service;

        if(mRunningPromise!=null){

            if(mRunningPromise.isActive()){
                return;
            }
            mRunningPromise = null;
            return;
        }
        //在后台线程里面执行。
    /*    Promise promise = createPromise();
        if(promise == null){
            LOG.info("createPromise == null");
            return;
        }*/
       // mService.getPromiseExecutor().submit();


    }


    private Promise createPromise(){
        return null;
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
