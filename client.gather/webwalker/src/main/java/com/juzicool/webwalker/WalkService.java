package com.juzicool.webwalker;


import com.juzicool.webwalker.core.Handler;
import com.juzicool.webwalker.core.Looper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class WalkService {
    public static Logger LOG = LoggerFactory.getLogger(WalkFlowTask.class);

    public static void main(String[] args) {

        WalkService service = new WalkService();
        service.setMaxTaskThread(5); //设置最大的启动task线程个数
        service.prepare(); // 准备工作


        //开启一个WaklTask
        WalkFlowTask task = new WalkFlowTask() {
            @Override
            int getTaskId() {
                return 1;
            }

            @Override
            String getTaskName() {
                return "WalkService.main_task";
            }

            @Override
            WalkFlow next() {
                return null;
            }

            @Override
            void onStart() {
                DefaultWalkFlow flow = new DefaultWalkFlow();
                flow.setName("测试flow").addCase(new WalkCase.DumpCase(),100);
            }

            @Override
            void onStop() {

            }
        };

       // service.startTaskNow(task);  //一个WalkTask就是调度工作任务。


        //在task上面执行一个WalkFlow


        WalkClient client = WalkClient.build();



        service.shutdownWhileIdle(true);
        service.waitUntilShutdown();
        System.out.println("finished service");

    }


    private static final long UPDATE_SECHEDULE_TIMER = 5000; //

    private int maxTaskThread = 5;
    private Handler mHandler = null;
    private HashMap<Integer,WalkFlowScheduleRunnable> mSchedulesMap = new HashMap<>();
    private WalkThreadManager mWalkThreadManager = null;
    private boolean shutdownWhileIdle = false;
    private boolean isShutdown = false;

    public void shutdownWhileIdle(boolean shutdownWhileIdle) {
        this.shutdownWhileIdle = shutdownWhileIdle;
    }

    public void waitUntilShutdown(){

        while (!isShutdown){
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void submit(final WalkFlowSchedule schedule){
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                if(isShutdown){
                    return;
                }

                WalkFlowScheduleRunnable runnable = mSchedulesMap.get(new Integer(schedule.getScheduleId()));

                if(runnable!= null){
                    //已经存在任务，删除；
                    getHandler().removeCallbacks(runnable);
                    //mSchedulesMap.remove(schedule.id)
                }
                runnable = new WalkFlowScheduleRunnable(schedule);
                schedule(runnable);


            }
        });


    }

    public void setMaxTaskThread(int size) {
        this.maxTaskThread = size;
    }

    private void exitSchedule(WalkFlowScheduleRunnable runnable){
        //退出计划任务执行
        LOG.info("退出计划执行:" + runnable.schedule.toString());
        runnable.remove = true;
        getHandler().removeCallbacks(runnable);
    }


    private void schedule(WalkFlowScheduleRunnable runnable){
        if(isShutdown){
            return;
        }
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



    public Handler getHandler(){
        if(mHandler == null){
            throw new RuntimeException("must call prepare() first!!!!!");
        }
        return mHandler;
    }

    public void prepare() {
        if(isShutdown){
            throw  new IllegalStateException("the service has desctroy!!!");
        }
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

                    mHandler.postDelayed(checkStatusTimerRunnable,600);
                }
            }
        }

        mWalkThreadManager = new WalkThreadManager(this);
    }

    public WalkThreadManager getWalkThreadManager(){
        return mWalkThreadManager;
    }

    private void shutdown(){
        //删除所有的计划任务
        for(WalkFlowScheduleRunnable schedule: mSchedulesMap.values()){
            getHandler().removeCallbacks(schedule);
        }
        mSchedulesMap.clear();
        getHandler().removeCallbacks(checkStatusTimerRunnable);
        getHandler().getLooper().quit();
        mHandler = null;
        isShutdown = true;
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

            if(isIdle && shutdownWhileIdle){
                shutdown();
            }else{
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
