package com.juzicool.webwalker;

import com.juzicool.webwalker.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public abstract class WalkFlow {
    public static Logger LOG = LoggerFactory.getLogger(WalkFlow.class);

    Queue<CaseWrapper> caseQueue = new LinkedList<>();

    protected WalkFlow addCase(WalkCase _case, long delay) {
        CaseWrapper wrapper = new CaseWrapper();
        wrapper._case= _case;
        wrapper.delay = delay;
        caseQueue.add(wrapper);
        return this;
    }

    public abstract String getName();


    /*pacage*/ Promise.Builder createPromise(final WalkFlowTask task,final WalkClient client) {
        Promise.Builder promiseBuilder = new Promise.Builder();
        while(true){
            final CaseWrapper wrapper =   caseQueue.poll();
            if(wrapper == null) {
                break;
            }
            if(wrapper.delay > 0 ){
                promiseBuilder.delay(wrapper.delay);
            }

           // final long startTime = System.currentTimeMillis();
            promiseBuilder.then(new Runnable() {
                @Override
                public void run() {

                    LOG.debug("flow[" +getName()+"]: START do case");
                    wrapper._case.onCreate(client);
                }
            });

            promiseBuilder.then(new Promise.RunFunc() {
                @Override
                public void run(Promise promise) {
                    LOG.debug("flow[" +getName()+"]: on do case");

                    wrapper._case.doCase(client,promise);
                }
            },wrapper._case.getTimeout());

            promiseBuilder.then(new Runnable() {
                @Override
                public void run() {
                    LOG.info("flow[" +getName()+"]: END do case");
                    wrapper._case.onDestroy();
                }
            });
        }

        return  promiseBuilder;
    }







    private static class CaseWrapper {
        WalkCase _case;
        long delay;
    }
}
