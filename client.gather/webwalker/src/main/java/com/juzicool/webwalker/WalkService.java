package com.juzicool.webwalker;


import com.juzicool.webwalker.core.Handler;
import com.juzicool.webwalker.core.Looper;
import com.juzicool.webwalker.core.PromiseExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class WalkService {
    public static Logger LOG = LoggerFactory.getLogger(WalkFlowTask.class);


    private static final long UPDATE_SECHEDULE_TIMER = 5000; //

    private int maxTaskThread = 5;
    private Handler mHandler = null;
    private HashMap<Integer,WalkFlowScheduleRunnable> mSchedulesMap = new HashMap<>();
    //private boolean isShutdown = false;
    private PromiseExecutor promiseExecutor;
     WalkFlowListener walkFlowListener;


    public WalkFlowListener getWalkFlowListener() {
        return walkFlowListener;
    }

    public void setWalkFlowListener(WalkFlowListener walkFlowListener) {
        this.walkFlowListener = walkFlowListener;
    }

    public void submit(final WalkFlowSchedule schedule){
        getHandler().post(new Runnable() {
            @Override
            public void run() {

                Integer sId = new Integer(schedule.getScheduleId());

                WalkFlowScheduleRunnable runnable = mSchedulesMap.get(sId);

                if(runnable!= null){
                    //已经存在任务，删除；
                    getHandler().removeCallbacks(runnable);
                    //mSchedulesMap.remove(schedule.id)
                }

                runnable = new WalkFlowScheduleRunnable(schedule);
                mSchedulesMap.put(sId,runnable);

                schedule(runnable);


            }
        });


    }

    /**
     * 设置最大的线程数据。
     * @param size
     */
    public void setMaxTaskThread(int size) {
        this.maxTaskThread = size;
    }

    public int getMaxTaskThread() {
        return maxTaskThread;
    }


    /**
     * 返回并处处理的WalkFlow限制个数。
     */
    /**pacake*/ int getRunningFlowLimit(){
        return (int)(maxTaskThread * 2.5f);
    }

    /***
     * 返回正在执行的WalkFlow个数
     * @return
     */
    public int getRunningWorkFlowCount(){
        int runningCount = 0;
        Collection<WalkFlowScheduleRunnable> runns =  mSchedulesMap.values();
       // ArrayList<WalkFlowSchedule> rets = new ArrayList<>(runns.size());
        for(WalkFlowScheduleRunnable r: runns){
           // rets.add(r.schedule);
            runningCount += r.schedule.getWalkFlowTask().getRunningCount();
        }
        return runningCount;
    }

    /**
     * 返回正在执行的线程个数。
     * @return
     */
    public int getRunningThreadCount(){
        return promiseExecutor.getCurrentThreadSize();
    }

    private void exitSchedule(WalkFlowScheduleRunnable runnable){
        //退出计划任务执行
        LOG.info("退出计划执行:" + runnable.schedule.toString());
        runnable.remove = true;
        getHandler().removeCallbacks(runnable);
    }


    private void schedule(WalkFlowScheduleRunnable runnable){

        //添加到计划任务里面；
        long runningTime = runnable.nextRunningTime();
        if(LOG.isDebugEnabled()){
            LOG.debug("schedule: " + runnable.schedule.toString() +"  at time: " + runningTime);
        }
        if(runningTime<=0){
            exitSchedule(runnable);
            return;
        }


        runnable.scheduleRunningTime = runningTime;
        long delayTime = runningTime - System.currentTimeMillis();
        if(delayTime < 0){
            delayTime = 0;
        }
        getHandler().removeCallbacks(runnable);
        getHandler().postDelayed(runnable,delayTime);


    }

    public List<WalkFlowSchedule> getWalkFlowScheduleList(){
        Collection<WalkFlowScheduleRunnable> runns =  mSchedulesMap.values();
        ArrayList<WalkFlowSchedule> rets = new ArrayList<>(runns.size());
        for(WalkFlowScheduleRunnable r: runns){
            rets.add(r.schedule);
        }
        return rets;
    }



    public Handler getHandler(){
        if(mHandler == null){
            throw new RuntimeException("must call prepare() first!!!!!");
        }
        return mHandler;
    }

    public void prepare() {
        if(mHandler == null) {
            synchronized (this) {
                if(mHandler == null) {
                    new Thread() {
                        public void run() {
                            Looper.prepare();
                            mHandler = new Handler();
                            Looper.loop();
                        }
                    }.start();

                    while (mHandler == null) {
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    promiseExecutor = new PromiseExecutor();
                    promiseExecutor.startup(mHandler);
                    mHandler.postDelayed(checkStatusTimerRunnable,600);
                }
            }
        }
    }


    public PromiseExecutor getPromiseExecutor() {
        return promiseExecutor;
    }


    /**
     * 定时检查当前服务状态
     */
    private Runnable checkStatusTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if(LOG.isDebugEnabled()){
                LOG.debug("定时更新计划任务状态");
            }
            boolean isIdle = true;

            ArrayList<WalkFlowScheduleRunnable> toRemoves = new ArrayList<>(3);
            for(WalkFlowScheduleRunnable schedule: mSchedulesMap.values()){
                if(schedule.remove){
                    toRemoves.add(schedule);
                    continue;
                }
                WalkFlowTask task = schedule.schedule.getWalkFlowTask();
                if(task.isRunning()){
                    isIdle = false;
                }

                //检查计划任务的时间也没有变
                if(schedule.nextRunningTime()!= schedule.scheduleRunningTime){
                    if(LOG.isDebugEnabled()){
                        LOG.debug("计划时间状态变化：" + schedule.schedule.toString());
                    }
                    //重新计划
                    schedule(schedule);
                }

            }

            for(WalkFlowScheduleRunnable runnable: toRemoves){
                WalkFlowTask task = runnable.schedule.getWalkFlowTask();
                if(!task.isRunning()){
                    mSchedulesMap.remove(runnable.schedule.getScheduleId());
                }
            }

            if(!isIdle ){
                getHandler().postDelayed(this,UPDATE_SECHEDULE_TIMER);
            }

        }
    };



      class WalkFlowScheduleRunnable implements Runnable{

        final WalkFlowSchedule schedule;
         boolean remove = false;
        private long lastedRunningTime = -1L;  //上一次执行时间
        private long scheduleRunningTime= -1L; //当前计划的执行时间。

        WalkFlowScheduleRunnable(WalkFlowSchedule schedule){
            this.schedule = schedule;
        }

        /**
         * 没有的返回0或者以上。
         * @return
         */
        public long nextRunningTime(){
            if(remove) {
                return -1L;
            }
            return schedule.nextRunTime();
        }

        @Override
        public void run() {
            if(remove){
                //已删除不执行。
                return;
            }
            WalkFlowTask task = schedule.getWalkFlowTask();

            if(!task.isRunning() && lastedRunningTime!=scheduleRunningTime ){
                //上一次的执行时间等于这一次的计划执行时间。
                lastedRunningTime = scheduleRunningTime;
                task.start(WalkService.this);
            }

            //规划下一个任务时间点。
            schedule(this);
        }
    }
}
