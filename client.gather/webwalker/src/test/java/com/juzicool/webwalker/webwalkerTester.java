package com.juzicool.webwalker;
import org.junit.Assert;
import org.junit.Test;

public class webwalkerTester {

    @Test
    public void testCaseTimeout(){

        WalkService service = new WalkService();
        service.setMaxTaskThread(5); //设置最大的启动task线程个数
        service.prepare(); // 准备工作




        testBasic(service);


    }

    private void testWalkThreadManager(WalkService service){

        WalkThreadManager manager = new WalkThreadManager(service);

        //在task上面执行一个WalkFlow
        DefaultWalkFlow flow = new DefaultWalkFlow();

       // manager.sumbit();

        for(int i = 0; i< 100;i++){

        }

       // manager.s
    }

    private void testBasic(WalkService service){
        //开启一个WaklTask
        WalkTask task = new  WalkTask();
        service.startTaskNow(task);  //一个WalkTask就是一个Thread线程。



        //在task上面执行一个WalkFlow
        DefaultWalkFlow flow = new DefaultWalkFlow();

        final long start = System.currentTimeMillis();
        WalkCaseTest _case = new WalkCaseTest() {
            @Override
            public void onCancel() {
                super.onCancel();
                long time =System.currentTimeMillis() - start;
                Assert.assertTrue( time >= 1000 && time <=2000);
            }
        };

        flow.setName("测试flow").addCase(_case,0);

        WalkClient client = WalkClient.build();

        MyCallback myCallback = new MyCallback();
        task.sumbit(client,flow,myCallback);  //执行一个流量操作；

        myCallback.waintCallabck();

        Assert.assertTrue(_case.hasCallCancel);
        Assert.assertTrue(_case.onCreateTime < _case.onDoCaseTime && _case.onCreateTime > 0);
        Assert.assertTrue(_case.onDoCaseTime < _case.onDestroyTime);
    }



    private static class MyCallback implements Callback {

        boolean hasCallback = false;

        @Override
        public void onCallback(Object data) {
            hasCallback = true;
        }

        public void waintCallabck(){
            while(!hasCallback) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

        private static class WalkCaseTest extends WalkCase{

        long timeout = 1000L;
        boolean hasCallCancel = false;
        long onCreateTime = 0;
        long onDoCaseTime = 0;
        long onDestroyTime = 0;

        public WalkCaseTest(){

        }

        @Override
        public long getTimeout() {
            return timeout;
        }

        @Override
        public void doCase(WalkClient client, WalkPormise pormise) {
            onDoCaseTime = System.currentTimeMillis();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

            @Override
            public void onCancel() {
                super.onCancel();
                hasCallCancel = true;
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCreate(WalkClient client) {
            super.onCreate(client);
                onCreateTime = System.currentTimeMillis();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDestroy() {
            super.onDestroy();
                onDestroyTime = System.currentTimeMillis();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


    }
}
