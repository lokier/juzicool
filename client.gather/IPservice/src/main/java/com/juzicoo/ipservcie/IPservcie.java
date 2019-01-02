package com.juzicoo.ipservcie;

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

       iPservcie.setIPTester(new IPTester.DefaultIPTester(new String[]{"https://www.juzimi.com/ju/469610"}));

       iPservcie.addIpSource(new www89ipcn());

       final IPPool pool =  iPservcie.createPool(20,10,0.6f);

       pool.ready();

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
                               System.out.println("request ip: " + ip.toString());
                           }else{
                               System.out.println("request ip: null");

                           }
                       }else{
                           if(ret.size() > 0){
                               ProxyIp ip = ret.remove(0);
                               pool.release(ip,true);
                               System.out.println("release ip: " + ip.toString());

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

    public IPTester getIPTester() {
        if(mIPTester == null){
            mIPTester = new IPTester.DefaultIPTester(new String[]{"https://www.baidu.com"});
        }
        return mIPTester;
    }

    public void setIPTester(IPTester mIPTester) {
        this.mIPTester = mIPTester;
    }

    public IPservcie addIpSource(IpSource source){
        ipSourceList.add(source);
        return this;
    }

     void doCollect(){
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        httpClientDownloader.setHttpUriRequestConverter(new HttpUriRequestConverter());

        Spider spider =  new Spider(new IpPageProcess());
        spider.setDownloader(httpClientDownloader);

        for(IpSource source : ipSourceList){
            for(String url: source.getEnterUrls()){
                Request r = new Request();
                r.getHeaders().put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6801.400 QQBrowser/10.3.2928.400");
                r.setUrl(url);
                spider.addRequest(r);
            }
        }

        spider.thread(15).run();
        spider.start();
    }


     class IpPageProcess implements PageProcessor, Closeable {


         IpPageProcess(){

         }


        @Override
        public void close() throws IOException {

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
                List<ProxyIp> ipList =  source.process(IPservcie.this,page);
                if(ipList!= null){
                    getDB().putIfNotExist(ipList);
                    IPservcie.LOG.info("DB: add proxyIpSize: " + ipList.size());
                }
            }


        }

         @Override
         public Site getSite() {
             return gSite;
         }

         private Site gSite = Site.me().setRetryTimes(2).setSleepTime(1200).setTimeOut(3000);
    }

}
