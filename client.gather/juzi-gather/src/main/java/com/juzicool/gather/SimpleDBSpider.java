package com.juzicool.gather;

import org.apache.commons.collections.CollectionUtils;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;

public class SimpleDBSpider extends Spider {

   // private SimpleDBSchedule

    private SimpleDB db = null;
    private SimpleDBSchedule mSchedule;

    private long executedSize = 0;
    private long  maxExecutedSize = Long.MAX_VALUE;

    /**
     * SimpleDBSchedule
     *
     * @param pageProcessor pageProcessor
     */
    public SimpleDBSpider(SimpleDB db,PageProcessor pageProcessor) {
        super(pageProcessor);
        this.db = db;
        mSchedule = new SimpleDBSchedule(db);
        super.setScheduler(mSchedule);
    }
    public final Spider setScheduler(Scheduler scheduler) {
        throw new UnsupportedOperationException("must use SimpleDBSchedule");
    }

    public Spider stopWhileExceutedSize(int size){
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
        executedSize++;
        if(executedSize>= maxExecutedSize){
            mSchedule.stop();
        }
    }

    @Override
    public void close(){
        super.close();
        db.close();
    }

}
