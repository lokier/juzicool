package com.juzicool.gather;

import com.juzicool.gather.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
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

    protected void onStartProcess(Request request){

    }

    protected void onError(Request request) {
        super.onError(request);
        //TODO 下载失败的url，要记录下来。
        System.out.println("ruquest error: " + request.getUrl());
    }

    protected void onSuccess(Request request) {
       super.onSuccess(request);

        //mErrorRequsetQueue.
        executedSize++;
        if(executedSize>= maxExecutedSize){
            mSchedule.stop();
        }
        String key = getUrlKey(request.getUrl());
        if(!StringUtils.isEmpty(key)){
            mErrorRequsetQueue.push(key,10000,request);
        }
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

        while(true){
            DB.QueueData data =  mErrorRequsetQueue.poll();
            if(data!= null){
                data.priority = 10000;//优先基本加高
                db.Queue().push(data);  //添加到待请求队列
                db.KV().remove(data.key);  //标志未访问过
                //Request request =  (Request)data.data;
            }else{
                break;
            }
        }

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


            DB.QueueData data =  db.Queue().poll();
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
