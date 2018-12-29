package com.juzicool.gather;

import com.juzicool.data.db.SimpleDB;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.File;
import java.util.*;


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

    private CacheDB db = null;

  //  private SimpleDB.Queue mErrorRequsetQueue;
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
        SimpleDB  sDb = new SimpleDB();
        sDb.openFile(file);
        this.db = new CacheDB(sDb);
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
     * @param
     */
    protected void onProcessResult(Request request,boolean isDoProcess ,boolean isProcessOk){
        String key = getUrlKey(request.getUrl());

        if(!isDoProcess){
            //没下载成功
            //放进错误列表，下次继续采集。
            //mErrorRequsetQueue.push(key, 10000, request);
            db.pushError(key,10000,request);
            return;
        }

        //计算成功率
        synchronized (this){
            this.least20ProocessReuslt[this.prccessReusltIndex] = isProcessOk;
            prccessReusltIndex = (prccessReusltIndex+1)% this.least20ProocessReuslt.length;
        }

        if(!isProcessOk) {
            System.err.println("采集错误: " + request.getUrl());
            if (!StringUtils.isEmpty(key)) {
                //放进错误列表，下次继续采集。
               // mErrorRequsetQueue.push(key, 10000, request);
                db.pushError(key,10000,request);

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

    @Override
    public void stop() {
        mSchedule.stop();
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

        final int batchSize = 1000;
        SimpleDB.Queue mErrorRequsetQueue = db.mErrorRequsetQueue;
        SimpleDB simpleDB = db.db;


        int totalSize = mErrorRequsetQueue.size();
        int count = 0;
        while(true){
            List<SimpleDB.QueueData> dataList =  mErrorRequsetQueue.poll(batchSize);
            if(dataList!= null && dataList.size() > 0){
                simpleDB.Queue().push(dataList);  //添加到待请求队列
                ArrayList<String> keys = new ArrayList<>(dataList.size());
                for(SimpleDB.QueueData data : dataList){
                    keys.add(data.key);
                }
                simpleDB.KV().remove(keys);  //标志未访问过
                //Request request =  (Request)data.data;
                count += dataList.size();
                System.out.println("progress:" + count +"/" + totalSize);

            }else{
                break;
            }
        }

        System.out.println("Spider restoreErrorRequest finish");


    }

    public static class CacheDB{

        private SimpleDB db;
        private SimpleDB.Queue mErrorRequsetQueue;

        private HashMap<String,SimpleDB.QueueData> toVisitMap = new HashMap();
        private HashSet<String> visitedMap = new HashSet();
        private HashMap<String,SimpleDB.QueueData> errorMap = new HashMap();
        private List<SimpleDB.QueueData> pollList = null;


        public CacheDB(SimpleDB db){
            this.db = db;
            mErrorRequsetQueue = db.crateQueue(ERROR_QUEUE_NAME);

        }

        public synchronized SimpleDB.QueueData pollNext(){
            check();
            if(pollList!= null && pollList.size() > 0){
                SimpleDB.QueueData data = pollList.remove(0);
                return data;
            }

            return null;
        }

        public synchronized boolean hasVisit(String key){

           /* if(visitedMap.contains(key)){
                return true;
            }*/
            if(toVisitMap.containsKey(key)){
                return true;
            }
            if(errorMap.containsKey(key)){
                return true;
            }
            if(pollList!= null) {
                for(SimpleDB.QueueData data : pollList){
                    if(key.equals(data.key)){
                        return true;
                    }
                }
            }

            return db.KV().has(key);
        }

        public synchronized void putToVisit(String key,Request request){
            //check();
            SimpleDB.QueueData data =  new SimpleDB.QueueData();
            data.key = key;
            data.priority = (int)request.getPriority();
            data.data = request;
            toVisitMap.put(key,data);
            visitedMap.add(key);
            //db.Queue().push(key,(int)request.getPriority(),request);
           // db.KV().put(key,true);
        }

        public synchronized void pushError(String key, int priority, Request request) {
            SimpleDB.QueueData data =  new SimpleDB.QueueData();
            data.key = key;
            data.priority = priority;
            data.data = request;
            errorMap.put(key,data);

            //mErrorRequsetQueue.push(key,priority,request);
        }

        private synchronized void check(){
            if (pollList == null || pollList.size()== 0){
                //
                save();
                pollList = db.Queue().poll(300);
            }
        }

        public synchronized void save() {
            long start = System.currentTimeMillis();
            ArrayList<SimpleDB.QueueData> toVlist = new ArrayList<>();
            ArrayList<SimpleDB.QueueData> toErroList = new ArrayList<>();
            HashSet<String> hasVistedList = visitedMap;

            if(pollList!=null){
                toVlist.addAll(pollList);
            }
            toVlist.addAll(toVisitMap.values());
            toErroList.addAll(errorMap.values());

            System.out.println("save cache, toVisit: " + toVlist.size()+",hasVisit:" + hasVistedList.size() +",erro visit:" + toErroList.size());

            int batchSize = 1000;
            if(toVlist.size() > 0){
                saveByBatch(db.Queue(),toVlist,batchSize);
            }
            if(toErroList.size() > 0) {
                saveByBatch(mErrorRequsetQueue, toErroList, batchSize);
            }
            if(hasVistedList.size() > 0){
                saveByBatch(db.KV(),hasVistedList,batchSize);
            }

            toVisitMap.clear();
            visitedMap.clear();
            errorMap.clear();
            if(pollList!= null) {
                pollList.clear();
            }
            System.out.println("save end..... time: " + (System.currentTimeMillis() - start));

            //db.KV().put

        }

        private void saveByBatch(SimpleDB.Queue queue,ArrayList<SimpleDB.QueueData> list, int batchSize){
            ArrayList<SimpleDB.QueueData> batch = new ArrayList<>(batchSize);
            for(SimpleDB.QueueData data:list){
                batch.add(data);
                if(batch.size() >= batchSize){
                    queue.push(batch);
                    System.out.println("save batch queue : " + batch.size());
                    batch.clear();
                }
            }
            if(batch.size()>0){
                queue.push(batch);
                System.out.println("save batch queue : " + batch.size());
                batch.clear();

            }
        }

        private void saveByBatch(SimpleDB.KV kv,Collection<String> list, int batchSize){
            ArrayList<String> batch = new ArrayList<>(batchSize);
            for(String key:list){
                batch.add(key);
                if(batch.size() >= batchSize){
                    kv.put(batch,true);
                    System.out.println("save batch kv : " + batch.size());
                    batch.clear();

                }
            }
            if(batch.size()>0){
                kv.put(batch,true);
                System.out.println("save batch kv : " + batch.size());
                batch.clear();

            }
        }

        public void close(){
            save();
            db.close();

        }

    }


    /***
     * 弄个缓存。
     */
    public  class SimpleDBSchedule implements Scheduler {
        //private SimpleDB db;
        private boolean isStop = false;
        private CacheDB mCacheDB;

        public SimpleDBSchedule(CacheDB db){
            //this.db = db;
            mCacheDB = db;
        }
        @Override
        public Request poll(Task task) {
            if(mCacheDB == null || isStop){
                return null;
            }

            long start = System.currentTimeMillis();
            SimpleDB.QueueData data =  mCacheDB.pollNext();
            long time = System.currentTimeMillis() - start;

            System.out.println("poll queue time: " + time);

            if(data!= null){
                return (Request)data.data;
            }

            return null;
        }

        @Override
        public void push(Request request, Task task) {

            String key = FileSpider.this.getUrlKey(request.getUrl());
            if(mCacheDB == null){
                return;
            }
            if (!mCacheDB.hasVisit(key) || shouldReserved(request) || noNeedToRemoveDuplicate(request)) {
                mCacheDB.putToVisit(key,request);
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
            if(mCacheDB!=null){
                mCacheDB.save();
            }
        }
    }
}
