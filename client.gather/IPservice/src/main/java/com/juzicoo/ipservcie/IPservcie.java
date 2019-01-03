package com.juzicoo.ipservcie;

import com.juzicoo.ipservcie.core.Handler;
import com.juzicoo.ipservcie.core.Looper;
import com.juzicoo.ipservcie.source.www89ipcn;
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

public class IPservcie {


    public static Logger LOG = LoggerFactory.getLogger(IPservcie.class);

    public static void main(String[] args) {
       IPservcie iPservcie = new IPservcie(new File("ipservide.db"));

       iPservcie.setIPTester(new IPTester.DefaultIPTester(iPservcie,new String[]{"https://www.juzimi.com/ju/469610"}));

       iPservcie.addIpSource(new www89ipcn());

       final IPPool pool =  iPservcie.createPool(20,10,0.6f);

       System.out.println("start collecting..");
       pool.ready();
       System.out.println("finish collecting..");

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



    private ProxyIpDB db;
    private ArrayList<IpSource> ipSourceList = new ArrayList();
    private IPTester mIPTester = null;
    private CollectIpRunnale mCollectRunnable = new CollectIpRunnale();

   // private Looper mLooper;
    private Handler mHander;

    public IPservcie(File file){
        db = new ProxyIpDB(file);
        db.prepare();
    }

    public IPPool createPool(int maxPoolSize,int minPoolSize,float minRate){
        return new IPPoolImpl(this,maxPoolSize,minPoolSize,minRate);
    }

    ProxyIpDB getDB(){
        return db;
    }

    public synchronized Handler getHandler(){
      if(mHander == null) {
          new Thread() {
              public void run() {
                  Looper.prepare();
                  mHander = new Handler();
                  Looper.loop();
              }
          }.start();

          while(mHander == null){
              try {
                  Thread.sleep(50);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
          }

      }
        return mHander;
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
         try {
                while (true){

                    synchronized (this){
                        if(mCollectRunnable == null || mCollectRunnable.isFinish){
                            break;
                        }
                        Thread.sleep(800);
                    }
                }
         } catch (InterruptedException e) {

         }

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
                List<ProxyIp> ipList =  source.process(IPservcie.this,page);
                if(ipList!= null){
                    getDB().putIfNotExist(ipList);
                    IPservcie.LOG.info("DB: add proxyIpSize: " + ipList.size());
                }
                mProccessSource = null;
            }


        }

         @Override
         public Site getSite() {
             return gSite;
         }

         private Site gSite = Site.me().setRetryTimes(2).setSleepTime(200).setTimeOut(3000);
    }

}
