package com.juzicoo.ipservcie;

import com.juzicoo.ipservcie.source.www89ipcn;
import com.juzicoo.ipservcie.source.wwwkuaidailicom;
import com.juzicool.core.Handler;
import com.juzicool.core.Promise;
import com.juzicool.core.PromiseExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.downloader.HttpUriRequestConverter;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class IPservcie {


    public static Logger LOG = LoggerFactory.getLogger(IPservcie.class);

    public static void main(String[] args) {
       IPservcie iPservcie = new IPservcie(new File("ipservide.db"));

       iPservcie.prepare();

       iPservcie.setIPTester(new IPTester.DefaultIPTester(iPservcie,new String[]{"https://www.juzimi.com/ju/469610"}));

       iPservcie.addIpSource(new wwwkuaidailicom());
       //iPservcie.addIpSource(new www89ipcn());

       final IPPool pool =  iPservcie.createPool(20,10,0.6f);

       System.out.println("start collecting..");
       iPservcie.doCollect();
       System.out.println("finish collecting..");


      // iPservcie.destoryWhileFinish();

       new Thread(){

           @Override
           public void run(){
               Random r = new Random(System.currentTimeMillis());
               try {
                   ArrayList<ProxyIp> ret = new ArrayList<>();
                   while (true) {
                       if(r.nextBoolean()){
                           ProxyIp ip = pool.request();
                           if(ip!= null){
                               ret.add(ip);
                               System.out.println("++request ip: " + ip.toString());
                           }else{
                               System.out.println("++request ip: null");

                           }
                       }else{
                           if(ret.size() > 0){
                               ProxyIp ip = ret.remove(0);
                               pool.release(ip.getHost(),true);
                               System.out.println("--release ip: " + ip.toString());

                           }
                       }
                       Thread.sleep(200);

                   }
               }catch (Exception ex){

               }


           }
       }.start();

    }

    public static void loadSystemSources(IPservcie iPservcie){
        iPservcie.addIpSource(new wwwkuaidailicom());
        iPservcie.addIpSource(new www89ipcn());
    }

    private boolean isPreared = false;



    public void prepare(){
        this.prepare(this.executor);
    }

    public void prepare(PromiseExecutor executor){
        if(!isPreared) {
            if(executor == null){
                executor = new PromiseExecutor();
                executor.startup(null);
            }
            this.executor = executor;
            isPreared = true;
        }
    }

    public void destoryWhileFinish() {
        waitWhileCollectFinished();
        Handler handler = getHandler();
        if(handler!= null){
            handler.getLooper().quit();
        }

       // ddw
    }


    private ProxyIpDB db;
    private ArrayList<IpSource> ipSourceList = new ArrayList();
    private IPTester mIPTester = null;
    private CollectIpRunnale mCollectRunnable = new CollectIpRunnale();
    //private Handler mHander;
    private PromiseExecutor executor = null;// new PromiseExecutor();


    public IPservcie(File file){
        db = new ProxyIpDB(file);
        db.prepare();
    }

    public File getFile(){
        return db.getFile();
    }

    public IPPool createPool(int maxPoolSize,int minPoolSize,float minRate){
        prepare();
        return new IPPoolImpl(this,maxPoolSize,minPoolSize,minRate);
    }

    public ProxyIpDB getDB(){
        return db;
    }

    public  Handler getHandler(){

        return executor.getHandler();
    }

    public PromiseExecutor getPromiseExecutor(){
        return executor;
    }

    public IPTester getIPTester() {
        if(mIPTester == null){
            mIPTester = new IPTester.DefaultIPTester(this,new String[]{"https://www.baidu.com"});
        }
        return mIPTester;
    }

    public void setIPTester(IPTester mIPTester) {
        this.mIPTester = mIPTester;
    }

    private long checkCollectIntervalTime = 6 * 60 * 60 * 1000L;
    /***
     * 設置每隔多少小時檢查多少量的IP代理
     * @param hour
     * @param percent
     */
    public void setCheckCollectInterval(float hour,float percent){
        checkCollectIntervalTime = (long)hour * 60L * 60L * 1000L;
        getHandler().removeCallbacks(mCollectRunnable);
        getHandler().postDelayed(mCollectRunnable,checkCollectIntervalTime);

    }

    /****
     * 設置每隔多少時間需要收集IP。
     * @param haour
     */
    public void setCollectInterval(float haour){

    }

    public IPservcie addIpSource(IpSource source){
        ipSourceList.add(source);
        return this;
    }

    /**
     * 请求收集IP。
     */
    public synchronized void requestCollect(){
        CollectIpRunnale collectIpThread = mCollectRunnable;

        if(collectIpThread!= null && !collectIpThread.isFinish) {
            if(IPservcie.LOG.isDebugEnabled()){
                IPservcie.LOG.debug(String.format("collecting is running,spend time: %ds", collectIpThread.getSpendTime()/ 1000));
            }
            return;
        }
        getHandler().removeCallbacks(mCollectRunnable);
        collectIpThread.reset();
        getHandler().post(mCollectRunnable);

    }

     void  doCollect(){
         requestCollect();
         waitWhileCollectFinished();
     }

     public  void waitWhileCollectFinished(){
         try {
             while (true){
                 synchronized (this){
                     if(isCollectFinish()){
                         break;
                     }
                     Thread.sleep(800);
                 }
             }
         } catch (InterruptedException e) {

         }
     }

     public boolean isCollectFinish(){
        return mCollectRunnable == null || mCollectRunnable.isFinish;
     }


     private class CollectIpRunnale implements Runnable{

        private boolean isFinish = true;
        private long startTime = 0L;
        private long endTime = -1;

        public CollectIpRunnale(){
            isFinish = true;
        }

        public long getSpendTime(){
            if(endTime < 0){
                return System.currentTimeMillis() - startTime;
            }
            return endTime - startTime;
        }

        public void reset(){
            isFinish = false;
            startTime = System.currentTimeMillis();
            endTime = -1L;
        }

        @Override
        public void run(){
            reset();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
                        httpClientDownloader.setHttpUriRequestConverter(new HttpUriRequestConverter());
                        Spider spider = new Spider(new IpPageProcess());
                        spider.setDownloader(httpClientDownloader);
                        Spider mSpider = spider;
                        for (IpSource source : ipSourceList) {
                            for (String url : source.getEnterUrls()) {
                                Request r = new Request();
                                r.getHeaders().put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6801.400 QQBrowser/10.3.2928.400");
                                r.setUrl(url);
                                mSpider.addRequest(r);
                            }
                        }
                        IPservcie.LOG.info("CollectIpThread.start:");
                        //mSpider.s
                        mSpider.setExitWhenComplete(true);
                        mSpider.thread(15).run();
                        mCommit.saveCache();
                        endTime = System.currentTimeMillis();
                    }catch (Exception ex){
                        IPservcie.LOG.error(ex.getMessage(),ex);
                    }
                    isFinish = true;
                    getHandler().postDelayed(this,checkCollectIntervalTime);
                    IPservcie.LOG.info("CollectIpThread.end: time = " + (endTime - startTime) +",next check time:" + new Date(System.currentTimeMillis()+checkCollectIntervalTime));
                }
            }).start();

        }

     }


     class IpPageProcess implements PageProcessor, Closeable {

        private IpSource mProccessSource;


         IpPageProcess(){

         }


        @Override
        public void close() throws IOException {
             try {
                 if(mProccessSource!= null) {
                     mProccessSource.absort();
                 }
             }catch (Exception ex){

             }
        }

        @Override
        public void process(Page page) {


            String url = page.getRequest().getUrl();
            IpSource source = null;
            for(IpSource s: ipSourceList){
                if(s.getEnterUrls().contains(url)){
                    source = s;
                }
            }

            if(source!= null){
                mProccessSource = source;
                source.process(page,mCommit);
                mProccessSource = null;
            }


        }

         @Override
         public Site getSite() {
             return gSite;
         }

         private Site gSite = Site.me().setRetryTimes(2).setSleepTime(200).setTimeOut(3000);
    }

    private CacheIpCommit mCommit = new CacheIpCommit();

    private static AtomicInteger mCounter = new AtomicInteger(0);

    private  class CacheIpCommit implements IpSource.DataCommit{

        HashMap<String,ProxyIp> mCahceList = new HashMap<>();



        @Override
        public  void submit(final String ip,final int port) {

            if(mCahceList.containsKey(ip)){
                return;
            }
            Promise promise =  getIPTester().checkProxyIp(ip,port);

            if(promise!=null){

        /*        final ArrayList<Long> rets = new ArrayList<>();

                promise.first(new Runnable() {
                    @Override
                    public void run() {
                        mCounter.incrementAndGet();

                        System.out.println("[start test ip:]"+ ip);
                        rets.add(System.currentTimeMillis());
                    }
                });

                promise.finalFunc(new Promise.RunFunc() {
                    @Override
                    public void run(Promise promise) {
                        long start = rets.get(0);
                        System.out.println("[end test ip:]"+ ip +" ,port" + port +", spendTime: " + (System.currentTimeMillis() - start) +", 还剩下：" + mCounter.decrementAndGet());
                    }
                });*/


                promise.resolveFunc(new Promise.RunFunc() {
                    @Override
                    public void run(Promise promise) {
                        ProxyIp proxy = new ProxyIp(ip,port);
                        addValidProxy(proxy);
                    }
                });
                executor.submit(promise);
            }


        }

        private synchronized void addValidProxy(ProxyIp ip){
            mCahceList.put(ip.getHost(),ip);
            if(mCahceList.size() > 10) {
                saveCache();
            }
        }

        public synchronized void saveCache(){
            if(mCahceList.size() > 0) {
                getDB().putIfNotExist(mCahceList.values());
                mCahceList.clear();
            }
        }
    }

}
