package com.juzicool.gather;

import com.juzicool.gather.store.SimpleDB;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.File;


public class FileSpider extends Spider {

    public interface KeyGetter{
        /**
         * url映射到key，避免重复冲抓取
         * @param url
         * @return
         */
        String getKey(String url);
    }

   // private SimpleDBSchedule
    private static final String ERROR_QUEUE_NAME = "error_reqeust_queues";

    private SimpleDB db = null;
    private SimpleDB.Queue mErrorRequsetQueue;
    private SimpleDBSchedule mSchedule;

    private long executedSize = 0;
    private long  maxExecutedSize = Long.MAX_VALUE;
    private KeyGetter keyGetter = null;
    private Boolean[] least20ProocessReuslt = new Boolean[20];
    private int prccessReusltIndex = 0;
    private float prccessMaxSuccessRate = 0.4f;

    /**
     * SimpleDBSchedule
     *
     * @param pageProcessor pageProcessor
     */
    public FileSpider(File file, PageProcessor pageProcessor) {
        super(pageProcessor);
        this.db = new SimpleDB();
        this.db.openFile(file);
        mErrorRequsetQueue = this.db.crateQueue(ERROR_QUEUE_NAME);
        mSchedule = new FileSpider.SimpleDBSchedule(db);
        super.setScheduler(mSchedule);
    }
    public final Spider setScheduler(Scheduler scheduler) {
        throw new UnsupportedOperationException("must use SimpleDBSchedule");
    }

    public FileSpider setKeyGetter(KeyGetter keyGetter){
        this.keyGetter = keyGetter;
        return this;
    }

    public FileSpider stopWhileExceutedSize(int size){
        maxExecutedSize = size;
        return this;
    }

    /**
     * 当成功率等于指定值时，停止。默认是0.4;
     * @param rate
     * @return
     */
    public FileSpider stopWhileProcessSucessRateSmallerThan(float rate){
        prccessMaxSuccessRate = rate;
        return this;
    }

    /**
     * 抓取目标结果页面结果。失败的话，可能是网路原因，也可能是抓取规则原因。
     * @param request
     * @param ok
     */
    protected void onProcessResult(Request request, boolean ok){

        //计算成功率
        synchronized (this){
            this.least20ProocessReuslt[this.prccessReusltIndex] = ok;
            prccessReusltIndex = (prccessReusltIndex+1)% this.least20ProocessReuslt.length;
        }

        if(!ok) {
            System.out.println("gather error: " + request.getUrl());
            String key = getUrlKey(request.getUrl());
            if (!StringUtils.isEmpty(key)) {
                mErrorRequsetQueue.push(key, 10000, request);
            }
        }

        //是否超过最大执行次数
        executedSize++;
        if(executedSize>= maxExecutedSize){
            System.out.println("Spider stop ：超过最大执行次数：" + maxExecutedSize);

            mSchedule.stop();
            return;
        }

        //成功率小于指定次数。
        float successRate = getProcessSuccessRate();
        if(successRate < prccessMaxSuccessRate){
            System.out.println("Spider stop ：采集成功率低于指定数值%：" + prccessMaxSuccessRate);
            mSchedule.stop();
            return;
        }
    }

    /**
     * 返回近期处理成功率
     * @return
     */
    public float getProcessSuccessRate(){
        float okCount = 0;
        for(Boolean flag: this.least20ProocessReuslt){
            if(flag == null || flag == true){
                okCount++;
            }
        }
        return  okCount / this.least20ProocessReuslt.length;
    }

    @Override
    public void close(){
        super.close();
        db.close();
    }

    public String getUrlKey(String url){
        if(this.keyGetter!= null){
            return this.keyGetter.getKey(url);
        }
        return url;
    }

    /***
     * 将上次请求失败的request
     */
    public void restoreErrorRequest() {
        System.out.println("Spider restoreErrorRequest.....");

        while(true){
            SimpleDB.QueueData data =  mErrorRequsetQueue.poll();
            if(data!= null){
                db.Queue().push(data);  //添加到待请求队列
                db.KV().remove(data.key);  //标志未访问过
                //Request request =  (Request)data.data;
            }else{
                break;
            }
        }

        System.out.println("Spider restoreErrorRequest finish");


    }


    public class SimpleDBSchedule implements Scheduler {
        //private SimpleDB db;
        private boolean isStop = false;

        public SimpleDBSchedule(SimpleDB db){
            //this.db = db;
        }
        @Override
        public Request poll(Task task) {
            if(isStop) {
                return null;
            }


            SimpleDB.QueueData data =  db.Queue().poll();
            if(data!= null){
                return (Request)data.data;
            }

            return null;
        }

        @Override
        public void push(Request request, Task task) {


            String key = FileSpider.this.getUrlKey(request.getUrl());

            if (!db.KV().has(key) || shouldReserved(request) || noNeedToRemoveDuplicate(request)) {
                db.Queue().push(key,(int)request.getPriority(),request);
                db.KV().put(key,true);
            }
        }


        protected boolean shouldReserved(Request request) {
            return request.getExtra(Request.CYCLE_TRIED_TIMES) != null;
        }

        protected boolean noNeedToRemoveDuplicate(Request request) {
            return HttpConstant.Method.POST.equalsIgnoreCase(request.getMethod());
        }


        public void stop() {
            isStop = true;
        }
    }
}
