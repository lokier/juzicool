package com.juzicool.webwalker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class WalkFlow {
    public static Logger LOG = LoggerFactory.getLogger(WalkFlow.class);

    private String name;

    Queue<CaseWrapper> caseQueue = new LinkedList<>();

    public WalkFlow addCase(WalkCase _case, long delay) {
        CaseWrapper wrapper = new CaseWrapper();
        wrapper._case= _case;
        wrapper.delay = delay;
        caseQueue.add(wrapper);
        return this;
    }

    public String getName() {
        return name;
    }

    public WalkFlow setName(String name) {
        this.name = name;
        return this;
    }

    /*pacage*/ void execute(WalkTask task,WalkClient client) {

        while(true){
            CaseWrapper wrapper =   caseQueue.poll();
            if(wrapper == null) {
                break;
            }
            if(wrapper.delay > 0 ){
                try {
                    Thread.sleep(wrapper.delay);
                } catch (InterruptedException e) {
                    LOG.warn("stop by InterruptedException!!");
                   break;
                }
            }

            long startTime = System.currentTimeMillis();
            LOG.info("flow[" +getName()+"]: start do case");
            TimeoutRunnable runnable = new TimeoutRunnable(wrapper);
            task.getWalkService().getHandler().postDelayed(runnable,wrapper._case.getTimeout());
            wrapper._case.doCase(client,wrapper);
            wrapper.waitingFinish();
            task.getWalkService().getHandler().removeCallbacks(runnable);
            runnable.dispose();
            try {
                wrapper._case.cancel();
            } catch (Throwable e) {

            }

            boolean doCaseOK = wrapper._walkOk;
            LOG.info("flow[" +getName()+"]: finish do case = " + doCaseOK +", spend time: " + (System.currentTimeMillis()- startTime));
            if(!doCaseOK){
                //中间有错误退出
                break;
            }
        }
    }




    /**
     * 执行完成
     * @param succes 是否执行成功
     * @param ex
     */
    /*pacage*/ void onFinished(boolean succes, Throwable ex){

    }

    private static class TimeoutRunnable implements Runnable{
        private CaseWrapper wrapper;
        TimeoutRunnable(CaseWrapper p){
            this.wrapper = p;
        }

        @Override
        public void run() {
            LOG.info("        timeout do case = " + wrapper._case.toString() );

            try {
                this.wrapper._case.cancel();
            }catch (Throwable ex){

            }
            this.wrapper.reject();
            dispose();
        }

        public void dispose(){
            this.wrapper = null;
        }
    }

    private static class CaseWrapper implements WalkPormise {
        WalkCase _case;
        long delay;

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
