package com.juzicool.core;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

public class PromiseTester2 {

    private static class Obj{
        Object args = null;

        Obj set(Object args){
            this.args = args;
            return this;
        }
    }

    @Test
    public void testMutilThread(){

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
        executor.setMaxThreadSize(20);

        ArrayList<PCase> cases = new ArrayList<>();
        try {
            Random r = new Random(System.currentTimeMillis());
            for(int i = 0; i< 200;i++){
                PCase pCase;
                int v = r.nextInt(5);
                if(v == 0){
                    pCase = testResoveCaes(executor);
                }else if(v==1){
                    pCase = testTimeout(executor);

                }else if(v==2){
                    pCase = testReject1(executor);

                }else if(v==4){
                    pCase = testOKEcCase(executor);

                }else{
                    pCase = testReject2(executor);

                }
                executor.submit(pCase.promise);
                cases.add(pCase);

            }
        }catch (Exception ex){
            throw new  RuntimeException(ex);
        }

        for(PCase pCase: cases){
            pCase.assertRunnable.run();
        }

        Assert.assertTrue(executor.getPenddingPromiseSize()==0);
        Assert.assertTrue(executor.getRunningPromise().length==0);


    }


    private PCase testOKEcCase(PromiseExecutor executor){

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
                promise.accept(o1);


                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                rets.add(o2.set(new Long(System.currentTimeMillis())));
                promise.reject(null);
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                rets.add(o3.set(new Long(System.currentTimeMillis())));
                promise.accept(o1);
            }
        },5000).delay(1500).then(new Runnable() {
            @Override
            public void run() {
                rets.add(o4.set(new Long(System.currentTimeMillis())));

            }
        }).resolveFunc(new Promise.RunFunc() {
            @Override
            public void run(Promise promise) {
                rets.add(o5.set(new Long(System.currentTimeMillis())));

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
                Assert.assertTrue(rets.size() == 5);
                Assert.assertTrue(rets.get(0) ==o1);
                Assert.assertTrue(rets.get(1) ==o2);
                Assert.assertTrue(rets.get(2) ==o3);
                Assert.assertTrue(rets.get(3) ==o4);
                Assert.assertTrue(rets.get(4) ==o5);

                long spendTime1 = (Long)o4.args  - (Long)o1.args;
                System.out.println("spendTime : " + spendTime1);
                long spendTime2 = (Long)o5.args  - (Long)o4.args;

                Assert.assertTrue(spendTime1>= 1500);
                //Assert.assertTrue(spendTime2>= 0 && spendTime2 <=100);

                // Assert.assertTrue(rets.size() == 4);
            }
        };


      return pCase;

    }


    private PCase testResoveCaes( PromiseExecutor executor){
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
               // Object data = promise.getResolveData();
                c4.args = new Long(System.currentTimeMillis());
                rets.add(c4);
            }
        });// .build();




        PCase pCase = new PCase();
        pCase.promise = promise;
        pCase.assertRunnable = new Runnable() {
            @Override
            public void run() {
                wait_(promise);
                Assert.assertTrue(promise.getStatus() == Promise.Status.RESOLVED);
                Assert.assertTrue(rets.get(0)==c1);
                Assert.assertTrue(rets.get(1)==c2);
                Assert.assertTrue(rets.get(2)==c4);
                long spendTime = (Long)c4.args  - (Long)c2.args;
                System.out.println("spendTime : " + spendTime);

                Assert.assertTrue(spendTime>= 1000);
            }
        };

        return pCase;

    }


    private PCase testTimeout( PromiseExecutor executor){
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
                       // Object data = promise.getResolveData();
                        c4.args = new Long(System.currentTimeMillis());
                        rets.add(c4);
                    }
                });// .build();



        PCase pCase = new PCase();
        pCase.promise = promise;
        pCase.assertRunnable = new Runnable() {
            @Override
            public void run() {
                wait_(promise);
                Assert.assertTrue(promise.getStatus() == Promise.Status.REJECT);
                Assert.assertTrue(rets.get(0)==c1);
                Assert.assertTrue(rets.get(1)==c2);
                Assert.assertTrue(rets.get(2)==c3);
                long spendTime = (Long)c3.args  - (Long)c2.args;
                System.out.println("spendTime : " + spendTime);

                Assert.assertTrue(spendTime>= 2000 );
            }
        };

        return pCase;
    }


    private PCase testReject1( PromiseExecutor executor){
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
                });// .build();


        PCase pCase = new PCase();
        pCase.promise = promise;
        pCase.assertRunnable = new Runnable() {
            @Override
            public void run() {
                wait_(promise);
                Assert.assertTrue(promise.getStatus() == Promise.Status.REJECT);
                Assert.assertTrue(rets.get(0)==c1);
                Assert.assertTrue(rets.get(1)==c2);
                Assert.assertTrue(rets.get(2)==c3);
                long spendTime = (Long)c3.args  - (Long)c2.args;
                System.out.println("spendTime : " + spendTime);
                Assert.assertTrue(spendTime>= 0);
            }
        };

        return pCase;
    }

    private PCase testReject2( PromiseExecutor executor){
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
                });// .build();




        PCase pCase = new PCase();
        pCase.promise = promise;
        pCase.assertRunnable = new Runnable() {
            @Override
            public void run() {
                wait_(promise);
                Assert.assertTrue(promise.getStatus() == Promise.Status.REJECT);
                Assert.assertTrue(rets.get(0)==c1);
                Assert.assertTrue(rets.get(1)==c2);
                Assert.assertTrue(rets.get(2)==c3);
                long spendTime = (Long)c3.args  - (Long)c2.args;
                System.out.println("spendTime : " + spendTime);
                Assert.assertTrue(spendTime>= 0 );
            }
        };

        return pCase;
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
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
