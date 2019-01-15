package com.juzicool.core;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class PromiseTester {

    private static class Obj{
        Object args = null;

        Obj set(Object args){
            this.args = args;
            return Obj.this;
        }
    }

    @Test
    public void testBasic(){

        HandlerThread thread = new HandlerThread();

        thread.start();

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Handler handler = new Handler(thread.getLooper());

        PromiseExecutor executor = new PromiseExecutor();
        executor.startup(handler);

        testOKEcCase(executor);
        testEmpytCase(executor);
        testfinanlyCaes(executor);

       testResoveCaes(executor);
        testTimeout(executor);

        testReject1(executor);
        testReject2(executor);

        Assert.assertTrue(executor.getPenddingPromiseSize()==0);
        Assert.assertTrue(executor.getRunningPromise().length==0);


    }


    private void testOKEcCase(PromiseExecutor executor){

        final Promise promise = new Promise();
        final Obj o1 = new Obj();
        final Obj o2 = new Obj();
        final Obj o3 = new Obj();
        final Obj o4 = new Obj();
        final Obj o5 = new Obj();
        final Obj o6 = new Obj();

        final ArrayList rets = new ArrayList();
        promise.then(new Promise.RunFunc() {
            @Override
            public void run(Promise promise) {
                //promise.resolveFunc()
                rets.add(o1.set(new Long(System.currentTimeMillis())));
                System.out.println("r0:" + o1.args.toString());

                promise.accept(o1);


                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                rets.add(o2.set(new Long(System.currentTimeMillis())));
                System.out.println("r0-1");

                promise.reject(null);
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                rets.add(o3.set(new Long(System.currentTimeMillis())));
                promise.accept(o1);
                System.out.println("r1");

            }
        },5000).delay(1500).then(new Runnable() {
            @Override
            public void run() {
                rets.add(o4.set(new Long(System.currentTimeMillis())));
                System.out.println("r2:" +o4.args.toString());

            }
        }).resolveFunc(new Promise.RunFunc() {
            @Override
            public void run(Promise promise) {
                rets.add(o5.set(new Long(System.currentTimeMillis())));
                System.out.println("r3");

            }
        }).rejectFunc(new Promise.RunFunc() {
            @Override
            public void run(Promise promise) {
                rets.add(o6.set(new Long(System.currentTimeMillis())));

            }
        });

        PCase pCase = new PCase();
        pCase.promise = promise;
        pCase.assertRunnable = new Runnable() {
            @Override
            public void run() {
                promise.waitToFinished();

                Assert.assertTrue(promise.getStatus() == Promise.Status.RESOLVED);
                System.out.println("szie : " + rets.size());

                long spendTime1 = (Long)o4.args  - (Long)o1.args;
                System.out.println("spendTime : " + spendTime1);
                long spendTime2 = (Long)o5.args  - (Long)o4.args;

                Assert.assertTrue(spendTime1>= 1500);

                Assert.assertTrue(rets.size() == 5);
                Assert.assertTrue(rets.get(0) ==o1);
                Assert.assertTrue(rets.get(1) ==o2);
                Assert.assertTrue(rets.get(2) ==o3);
                Assert.assertTrue(rets.get(3) ==o4);
                Assert.assertTrue(rets.get(4) ==o5);


                //Assert.assertTrue(spendTime2>= 0 && spendTime2 <=100);

                // Assert.assertTrue(rets.size() == 4);
            }
        };


        executor.submit(promise);
        pCase.assertRunnable.run();

    }

    private void testEmpytCase(PromiseExecutor executor){
        Promise promiseBuilder = new Promise();
        Promise promise = promiseBuilder;
        Assert.assertTrue(promise.getStatus() == Promise.Status.PENDING);
        Assert.assertTrue(promise.isActive());
        executor.submit(promise);
        wait_(promise);
        Assert.assertTrue(promise.getStatus() == Promise.Status.RESOLVED);
        Assert.assertTrue(promise.isActive() == false);

        promiseBuilder = new Promise();

        final Obj o1 = new Obj();
        final Obj o2 = new Obj();
        final Obj o3 = new Obj();
        final ArrayList rets = new ArrayList();

        promiseBuilder.resolveFunc(new Promise.RunFunc() {
            @Override
            public void run(Promise promise) {
                o1.args = new Long(System.currentTimeMillis());
                rets.add(o1);
            }
        });

        promiseBuilder.finalFunc(new Promise.RunFunc() {
            @Override
            public void run(Promise promise) {
                o2.args = new Long(System.currentTimeMillis());
                rets.add(o2);
            }
        });

         promise = promiseBuilder;
        Assert.assertTrue(promise.getStatus() == Promise.Status.PENDING);
        Assert.assertTrue(promise.isActive());
        executor.submit(promise);
        wait_(promise);
        Assert.assertTrue(promise.getStatus() == Promise.Status.RESOLVED);
        Assert.assertTrue(promise.isActive() == false);
        Assert.assertTrue(rets.get(0) == o1);
        Assert.assertTrue(rets.get(1) == o2);
       // Assert.assertTrue(promise.getResolveData() == null);

    }

    private void testfinanlyCaes( PromiseExecutor executor){
        final ArrayList rets = new ArrayList();
        final Obj c1 = new Obj();
        final Obj c2 = new Obj();
        final Obj c3 = new Obj();
        final Obj c4 = new Obj();
        final Obj c5 = new Obj();

        Promise promise = new Promise().then(new Runnable() {
            @Override
            public void run() {
                c1.args = new Long(System.currentTimeMillis());
                rets.add(c1);
            }
        }).then(new Promise.RunFunc() {
            @Override
            public void run(Promise promise) {
                c2.args = new Long(System.currentTimeMillis());
                rets.add(c2);
                promise.accept(new Object());
            }
        },3000).delay(1000)
                .rejectFunc(new Promise.RunFunc() {
                    @Override
                    public void run(Promise promise) {
                        c3.args = new Long(System.currentTimeMillis());
                        rets.add(c3);
                    }
                }).resolveFunc(new Promise.RunFunc() {
                    @Override
                    public void run(Promise promise) {
                      //  Object data = promise.getResolveData();
                        c4.args = new Long(System.currentTimeMillis());
                        rets.add(c4);
                    }
                }).finalFunc(new Promise.RunFunc() {
            @Override
            public void run(Promise promise) {
                c5.args = new Long(System.currentTimeMillis());
                rets.add(c5);
            }
        })
               ;


        Assert.assertTrue(promise.getStatus() == Promise.Status.PENDING);
        executor.submit(promise);
        Assert.assertTrue(promise.getStatus() != Promise.Status.PENDING);
        wait_(2000);
        Assert.assertTrue(promise.getStatus() == Promise.Status.RESOLVED);
        Assert.assertTrue(rets.get(0)==c1);
        Assert.assertTrue(rets.get(1)==c2);
        Assert.assertTrue(rets.get(2)==c4);
        Assert.assertTrue(rets.get(3)==c5);

        long spendTime = (Long)c4.args  - (Long)c2.args;
        System.out.println("spendTime : " + spendTime);

        Assert.assertTrue(spendTime>= 1000 && spendTime <=1100);
    }


    private void testResoveCaes( PromiseExecutor executor){
        final ArrayList rets = new ArrayList();
        final Obj c1 = new Obj();
        final Obj c2 = new Obj();
        final Obj c3 = new Obj();
        final Obj c4 = new Obj();
        final Obj c5 = new Obj();
        final Obj finalResolveObj = new Obj();

        Promise promise = new Promise().then(new Runnable() {
            @Override
            public void run() {
                c1.args = new Long(System.currentTimeMillis());
                rets.add(c1);
            }
        }).then(new Promise.RunFunc() {
            @Override
            public void run(Promise promise) {
                c2.args = new Long(System.currentTimeMillis());
                rets.add(c2);
                promise.accept(c5);
            }
        },3000).delay(1000)
         .rejectFunc(new Promise.RunFunc() {
            @Override
            public void run(Promise promise) {
                c3.args = new Long(System.currentTimeMillis());
                rets.add(c3);
            }
        }).resolveFunc(new Promise.RunFunc() {
            @Override
            public void run(Promise promise) {
                //Object data = promise.getResolveData();
                c4.args = new Long(System.currentTimeMillis());
                rets.add(c4);
                promise.accept(finalResolveObj);

            }
        });


        Assert.assertTrue(promise.getStatus() == Promise.Status.PENDING);
        executor.submit(promise);
        Assert.assertTrue(promise.getStatus() != Promise.Status.PENDING);
        wait_(2000);
        Assert.assertTrue(promise.getStatus() == Promise.Status.RESOLVED);
        Assert.assertTrue(rets.get(0)==c1);
        Assert.assertTrue(rets.get(1)==c2);
        Assert.assertTrue(rets.get(2)==c4);
       // Assert.assertTrue(promise.getResolveData()==c5);

        long spendTime = (Long)c4.args  - (Long)c2.args;
        System.out.println("spendTime : " + spendTime);

        Assert.assertTrue(spendTime>= 1000 && spendTime <=1100);
    }


    private void testTimeout( PromiseExecutor executor){
        final ArrayList rets = new ArrayList();
        final Obj c1 = new Obj();
        final Obj c2 = new Obj();
        final Obj c3 = new Obj();
        final Obj c4 = new Obj();
        final Obj c5 = new Obj();

        Promise promise = new Promise().then(new Runnable() {
            @Override
            public void run() {
                c1.args = new Long(System.currentTimeMillis());
                rets.add(c1);
            }
        }).then(new Promise.RunFunc() {
            @Override
            public void run(Promise promise) {
                c2.args = new Long(System.currentTimeMillis());
                rets.add(c2);
                //promise.accept(new Object());
            }
        },2000)
                .delay(900)
                .rejectFunc(new Promise.RunFunc() {
                    @Override
                    public void run(Promise promise) {
                        c3.args = new Long(System.currentTimeMillis());
                        rets.add(c3);

                    }
                }).resolveFunc(new Promise.RunFunc() {
                    @Override
                    public void run(Promise promise) {
                      //  Object data = promise.getResolveData();
                        c4.args = new Long(System.currentTimeMillis());
                        rets.add(c4);
                    }
                });


        Assert.assertTrue(promise.getStatus() == Promise.Status.PENDING);
        executor.submit(promise);
        Assert.assertTrue(promise.getStatus() != Promise.Status.PENDING);
        wait_(3200);
        Assert.assertTrue(promise.getStatus() == Promise.Status.REJECT);
        Assert.assertTrue(rets.get(0)==c1);
        Assert.assertTrue(rets.get(1)==c2);
        Assert.assertTrue(rets.get(2)==c3);
        long spendTime = (Long)c3.args  - (Long)c2.args;
        System.out.println("spendTime : " + spendTime);

        Assert.assertTrue(spendTime>= 2000 && spendTime <=2100);
    }


    private void testReject1( PromiseExecutor executor){
        final ArrayList rets = new ArrayList();
        final Obj c1 = new Obj();
        final Obj c2 = new Obj();
        final Obj c3 = new Obj();
        final Obj c4 = new Obj();
        final Obj c5 = new Obj();

        Promise promise = new Promise().then(new Runnable() {
            @Override
            public void run() {
                c1.args = new Long(System.currentTimeMillis());
                rets.add(c1);
            }
        }).then(new Promise.RunFunc() {
            @Override
            public void run(Promise promise) {
                c2.args = new Long(System.currentTimeMillis());
                rets.add(c2);
                promise.reject(new Object());
            }
        },2000)
                .delay(900)
                .rejectFunc(new Promise.RunFunc() {
                    @Override
                    public void run(Promise promise) {
                        c3.args = new Long(System.currentTimeMillis());
                        rets.add(c3);

                    }
                }).resolveFunc(new Promise.RunFunc() {
                    @Override
                    public void run(Promise promise) {
                        //Object data = promise.getResolveData();
                        c4.args = new Long(System.currentTimeMillis());
                        rets.add(c4);
                    }
                });


        Assert.assertTrue(promise.getStatus() == Promise.Status.PENDING);
        executor.submit(promise);
        Assert.assertTrue(promise.getStatus() != Promise.Status.PENDING);
        wait_(1500);
        Assert.assertTrue(promise.getStatus() == Promise.Status.REJECT);
        Assert.assertTrue(rets.get(0)==c1);
        Assert.assertTrue(rets.get(1)==c2);
        Assert.assertTrue(rets.get(2)==c3);
        long spendTime = (Long)c3.args  - (Long)c2.args;
        System.out.println("spendTime : " + spendTime);
        Assert.assertTrue(spendTime>= 0 && spendTime <=100);
    }

    private void testReject2( PromiseExecutor executor){
        final ArrayList rets = new ArrayList();
        final Obj c1 = new Obj();
        final Obj c2 = new Obj();
        final Obj c3 = new Obj();
        final Obj c4 = new Obj();
        final Obj c5 = new Obj();

        Promise promise = new Promise().then(new Runnable() {
            @Override
            public void run() {
                c1.args = new Long(System.currentTimeMillis());
                rets.add(c1);
            }
        }).then(new Promise.RunFunc() {
            @Override
            public void run(Promise promise) {
                c2.args = new Long(System.currentTimeMillis());
                rets.add(c2);

                throw new RuntimeException();
               // promise.rejectFunc(new Object());
            }
        },2000)
                .delay(900)
                .rejectFunc(new Promise.RunFunc() {
                    @Override
                    public void run(Promise promise) {
                        c3.args = new Long(System.currentTimeMillis());
                        rets.add(c3);

                    }
                }).resolveFunc(new Promise.RunFunc() {
                    @Override
                    public void run(Promise promise) {
                       // Object data = promise.getResolveData();
                        c4.args = new Long(System.currentTimeMillis());
                        rets.add(c4);
                    }
                }) ;//.build();


        Assert.assertTrue(promise.getStatus() == Promise.Status.PENDING);
        executor.submit(promise);
        Assert.assertTrue(promise.getStatus() != Promise.Status.PENDING);
        wait_(1500);
        Assert.assertTrue(promise.getStatus() == Promise.Status.REJECT);
        Assert.assertTrue(rets.get(0)==c1);
        Assert.assertTrue(rets.get(1)==c2);
        Assert.assertTrue(rets.get(2)==c3);
        long spendTime = (Long)c3.args  - (Long)c2.args;
        System.out.println("spendTime : " + spendTime);
        Assert.assertTrue(spendTime>= 0 && spendTime <=100);
    }

    private static void wait_(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private static void wait_(Promise promise){
        while(true){
            if(!promise.isActive()){
                break;
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private class PCase {

        Promise promise;

        Runnable assertRunnable;


    }

}
