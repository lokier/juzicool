package com.juzicool.webwalker;
import com.juzicool.webwalker.core.Handler;
import com.juzicool.webwalker.core.HandlerThread;
import com.juzicool.webwalker.core.Promise;
import com.juzicool.webwalker.core.PromiseExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class PromiseTester {

    private static class Obj{
        Object args = null;
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


        testResoveCaes(executor);
        testTimeout(executor);

        testReject1(executor);
        testReject2(executor);

    }



    private void testResoveCaes( PromiseExecutor executor){
        final ArrayList rets = new ArrayList();
        final Obj c1 = new Obj();
        final Obj c2 = new Obj();
        final Obj c3 = new Obj();
        final Obj c4 = new Obj();
        final Obj c5 = new Obj();

        Promise promise = new Promise.Builder().then(new Runnable() {
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
                promise.reslove(new Object());
            }
        },3000).delay(1000)
         .reject(new Promise.RunFunc() {
            @Override
            public void run(Promise promise) {
                c3.args = new Long(System.currentTimeMillis());
                rets.add(c3);
            }
        }).resolve(new Promise.RunFunc() {
            @Override
            public void run(Promise promise) {
                Object data = promise.getResolveData();
                c4.args = new Long(System.currentTimeMillis());
                rets.add(c4);
            }
        }) .build();


        Assert.assertTrue(promise.getStatus() == Promise.Status.PENDING);
        executor.submit(promise);
        Assert.assertTrue(promise.getStatus() != Promise.Status.PENDING);
        wait_(2000);
        Assert.assertTrue(promise.getStatus() == Promise.Status.RESOLVED);
        Assert.assertTrue(rets.get(0)==c1);
        Assert.assertTrue(rets.get(1)==c2);
        Assert.assertTrue(rets.get(2)==c4);
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

        Promise promise = new Promise.Builder().then(new Runnable() {
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
                //promise.reslove(new Object());
            }
        },2000)
                .delay(900)
                .reject(new Promise.RunFunc() {
                    @Override
                    public void run(Promise promise) {
                        c3.args = new Long(System.currentTimeMillis());
                        rets.add(c3);

                    }
                }).resolve(new Promise.RunFunc() {
                    @Override
                    public void run(Promise promise) {
                        Object data = promise.getResolveData();
                        c4.args = new Long(System.currentTimeMillis());
                        rets.add(c4);
                    }
                }) .build();


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

        Promise promise = new Promise.Builder().then(new Runnable() {
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
                .reject(new Promise.RunFunc() {
                    @Override
                    public void run(Promise promise) {
                        c3.args = new Long(System.currentTimeMillis());
                        rets.add(c3);

                    }
                }).resolve(new Promise.RunFunc() {
                    @Override
                    public void run(Promise promise) {
                        Object data = promise.getResolveData();
                        c4.args = new Long(System.currentTimeMillis());
                        rets.add(c4);
                    }
                }) .build();


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

        Promise promise = new Promise.Builder().then(new Runnable() {
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
               // promise.reject(new Object());
            }
        },2000)
                .delay(900)
                .reject(new Promise.RunFunc() {
                    @Override
                    public void run(Promise promise) {
                        c3.args = new Long(System.currentTimeMillis());
                        rets.add(c3);

                    }
                }).resolve(new Promise.RunFunc() {
                    @Override
                    public void run(Promise promise) {
                        Object data = promise.getResolveData();
                        c4.args = new Long(System.currentTimeMillis());
                        rets.add(c4);
                    }
                }) .build();


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
            if(promise.getStatus()== Promise.Status.RESOLVED || promise.getStatus() == Promise.Status.REJECT){
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

    private class PCase {

        Promise promise;

        Runnable assertRunnable;


    }

}
