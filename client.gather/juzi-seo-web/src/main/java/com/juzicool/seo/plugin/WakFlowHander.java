package com.juzicool.seo.plugin;

import com.juzicool.seo.Services;
import com.juzicool.seo.db.WorkFlowTaskDB;
import com.juzicool.seo.model.WorkFlowLog;
import com.juzicool.webwalker.*;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import javax.xml.ws.Service;
import java.util.HashMap;

public class WakFlowHander implements WalkFlowListener {

    private int totalFlowCount = 0;
    private int totalFlowSuccess = 0;

    private HashMap<WalkFlow,StringBuffer> walkFlowLogMap = new HashMap<>();

    private static String currentTimeDesc(){
        return DateFormatUtils.format(System.currentTimeMillis(),"yyyy-MM-dd'T'HH:mm:ss");
    }

    @Override
    public void onStartTask(WalkFlowTask task) {
        totalFlowCount = 0;
        totalFlowSuccess =0;
        System.out.println("onStartTask:" + task.getTaskName()+","+task.getTaskId());

    }

    @Override
    public void onStartFlow(WalkFlowTask task, WalkFlow flow, WalkClient client) {
        System.out.println("onStartFlow:" + task.getTaskName()+","+task.getTaskId());
        totalFlowCount++;
        StringBuffer sb = new StringBuffer( String.format("[%s]开始flow......\n", currentTimeDesc()));
        walkFlowLogMap.put(flow,sb);
    }

    @Override
    public void onDoCase(WalkFlowTask task, WalkFlow flow, WalkClient client, WalkCase _case) {
        System.out.println("onDoCase:" + task.getTaskName()+","+task.getTaskId());

    }

    @Override
    public void onFlowProgerssChanged(WalkFlowTask task, WalkFlow flow, int progress, String progressText) {
        StringBuffer sb = walkFlowLogMap.get(flow);
        if(sb!= null){
            sb.append(String.format("[%s ,progress=%d%%]%s\n", currentTimeDesc(),progress,progressText));
        }
    }


    @Override
    public void onFinishFlow(WalkFlowTask task, WalkFlow flow, WalkClient client, boolean hasError) {
        System.out.println("onFinishFlow:" + task.getTaskName()+","+task.getTaskId());

        if(!hasError){
            totalFlowSuccess++;
        }

        walkFlowLogMap.remove(flow);
        final StringBuffer sb = walkFlowLogMap.get(flow);
        sb.append(String.format("[%s]结束flow......\n", currentTimeDesc()));
        final Integer taskId = task.getTaskId();
        final WorkFlowLog log = new WorkFlowLog();
        log.detailLog = sb.toString();

        Services.walkService.getPromiseExecutor().submit(new Runnable() {
            @Override
            public void run() {

                WorkFlowTaskDB db = WorkFlowTaskDB.get(taskId);
                db.insert(log);

            }
        });


        task.setProcessText(String.format("已處理%d個flow，成功%d個",totalFlowCount,totalFlowSuccess));

    }

    @Override
    public void onFinishTask(WalkFlowTask task) {
        System.out.println("onFinishTask:" + task.getTaskName()+","+task.getTaskId());

    }
}
