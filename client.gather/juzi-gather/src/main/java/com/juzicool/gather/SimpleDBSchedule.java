package com.juzicool.gather;

import com.juzicool.gather.utils.UrlUtils;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.Scheduler;
import us.codecraft.webmagic.utils.HttpConstant;

public class SimpleDBSchedule implements Scheduler {
    private SimpleDB db;
    private boolean isStop = false;

    public SimpleDBSchedule(SimpleDB db){
        this.db = db;
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


        String key = getKey(request.getUrl());

        if (!db.KV().has(key) || shouldReserved(request) || noNeedToRemoveDuplicate(request)) {
            db.Queue().push(key,(int)request.getPriority(),request);
            db.KV().put(key,true);
        }
    }

    /***
     * TODO 要分离出来。
     * @param url
     * @return
     */
    private String getKey(String url) {
        //过滤哪些无用的数据。
        String path = UrlUtils.getPath(url);
        int index = path.indexOf("#");

        if(index > 0){
            path = path.substring(0,index);
        }

        return path;
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

