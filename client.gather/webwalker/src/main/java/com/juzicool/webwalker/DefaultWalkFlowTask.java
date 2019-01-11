package com.juzicool.webwalker;

import java.util.LinkedList;

public class DefaultWalkFlowTask extends WalkFlowTask{

    private static LinkedList<WalkFlow> walkFlows = new LinkedList<>();

    private static LinkedList<WalkFlow> walkFlowsQueue = null;

    private String taskName;

   final private int taksId;

    public DefaultWalkFlowTask(int taskId){
        this.taksId = taskId;
    }

    public void addWalkFlow(WalkFlow flow){
        walkFlows.add(flow);
    }

    @Override
    public int getTaskId() {
        return this.taksId;
    }

    @Override
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    protected WalkFlow next() {
        if(walkFlowsQueue!= null){
            return walkFlowsQueue.poll();
        }
        return null;
    }

    @Override
    protected WalkClient createWalkClient() {
        return WalkClient.build();
    }

    @Override
    protected void releaseWalkClient(WalkClient client) {

    }

    @Override
    protected void onStartInBackgound() {
        walkFlowsQueue = new LinkedList<>(walkFlows);

    }

    @Override
    protected void onStopInBackground() {
        walkFlowsQueue = null;

    }

}
