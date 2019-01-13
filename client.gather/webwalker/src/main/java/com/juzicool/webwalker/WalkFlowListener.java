package com.juzicool.webwalker;

/**
 * 全部在Looper线程执行。
 */
public interface WalkFlowListener {

    void onStartTask(WalkFlowTask task);

    void onStartFlow(WalkFlowTask task,WalkFlow flow,WalkClient client);

    void onDoCase(WalkFlowTask task,WalkFlow flow,WalkClient client, WalkCase _case);

    void onFinishFlow(WalkFlowTask task,WalkFlow flow,WalkClient client,boolean hasError);

    void onFinishTask(WalkFlowTask task);

}
