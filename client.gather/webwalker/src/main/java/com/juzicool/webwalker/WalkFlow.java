package com.juzicool.webwalker;

import com.juzicool.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public abstract class WalkFlow {
    public static Logger LOG = LoggerFactory.getLogger(WalkFlow.class);

    Queue<CaseWrapper> caseQueue = new LinkedList<>();

    final public HashMap<String,Object> args = new HashMap();

    protected WalkFlow addCase(WalkCase _case, long delay) {
        CaseWrapper wrapper = new CaseWrapper();
        wrapper._case= _case;
        wrapper.delay = delay;
        caseQueue.add(wrapper);
        return this;
    }

    public abstract String getName();


    /*pacage*/ Promise createPromise(Promise promiseBuilder, final WalkFlowTask task, final WalkClient client) {
        while(true){
            final CaseWrapper wrapper =   caseQueue.poll();
            if(wrapper == null) {
                break;
            }
            if(wrapper.delay > 0 ){
                promiseBuilder.delay(wrapper.delay);
            }

           // final long startTime = System.currentTimeMillis();

            final WalkCase _case = wrapper._case;

            promiseBuilder.then(new Promise.RunFunc() {
                @Override
                public void run(Promise promise) {
                    LOG.debug("flow[" +getName()+"]: on do case");
                    task.mService.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            WalkService service = task.mService;
                            WalkFlow flow = WalkFlow.this;
                            if(service!= null && service.walkFlowListener!=null){
                                service.walkFlowListener.onDoCase(task,flow,client,_case);
                            }
                        }
                    });
                    wrapper._case.doCase(client,promise);
                }
            },wrapper._case.getTimeout());

        }

        return  promiseBuilder;
    }


    private static class CaseWrapper {
        WalkCase _case;
        long delay;
    }
}
