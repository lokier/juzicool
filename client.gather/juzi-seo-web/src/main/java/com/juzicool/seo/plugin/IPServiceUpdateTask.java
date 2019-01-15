package com.juzicool.seo.plugin;

import com.juzicoo.ipservcie.IPservcie;
import com.juzicool.core.Promise;
import com.juzicool.seo.Services;
import com.juzicool.webwalker.WalkCase;
import com.juzicool.webwalker.WalkClient;
import com.juzicool.webwalker.WalkFlow;
import com.juzicool.webwalker.WalkFlowTask;

public class IPServiceUpdateTask extends WalkFlowTask {

    WalkFlow nextFlow = null;

    @Override
    public int getTaskId() {
        return 100000;
    }

    @Override
    public String getTaskName() {
        return "定時抓取IP任務";
    }

    @Override
    protected WalkFlow next() {
        WalkFlow flow = nextFlow;
        nextFlow = null;
        return flow;
    }

    @Override
    protected WalkClient createWalkClient(WalkFlow flow) {
        return null;
    }

    @Override
    protected void releaseWalkClient(WalkClient client) {

    }

    @Override
    protected void onStartInBackgound() {
        nextFlow = new MyWalkFlow();
    }

    @Override
    protected void onStopInBackground() {

    }

    private static class MyWalkFlow extends WalkFlow{

        public MyWalkFlow(){
            addCase(new WalkCase() {
                @Override
                public long getTimeout() {
                    return 1 * 60 *60 *1000;
                }

                @Override
                protected void doCase(WalkClient wclient, Promise pormise) {
                    IPservcie ipService = Services.iPservcie;

                    if(ipService.isCollectFinish()){
                        ipService.requestCollect();
                    }

                    pormise.accept(null);
                }
            },0);
        }

        @Override
        public String getName() {
            return "抓取代理IP";
        }
    }
 }
