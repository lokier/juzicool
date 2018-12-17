package com.juzicool.gather.spider;

import com.juzicool.gather.FileSpider;
import com.juzicool.gather.Gloabal;
import com.juzicool.gather.IpTest;
import com.juzicool.gather.utils.SelectableUtls;
import com.juzicool.gather.utils.UrlUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.downloader.HttpUriRequestConverter;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProxyIpSpider {

    public static void main(String[] args) {
        Gloabal.beforeMain();


       // File gatherFile = new File("./ip_gather.db");  //抓取状态保存在这个文件。
       // File outputFile = new File("./ip_output.db");  //句子结果保存到这个数据库。


        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        //httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("web-proxy.oa.com",8080)));

        httpClientDownloader.setHttpUriRequestConverter(new HttpUriRequestConverter());

        Spider spider =  new Spider(new P());

        final int pageSize = 12;
        for(int i = 1; i<=pageSize;i++){
            Request r = new Request();
            r.getHeaders().put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6801.400 QQBrowser/10.3.2928.400");
            r.setUrl("http://www.89ip.cn/index_"+i+".html");
            spider.addRequest(r);
        }

        spider.thread(10).run();

        System.out.println("\n==============ip list==========");
        System.out.println(gSb.toString());
    }

    private static  StringBuffer gSb = new StringBuffer();

    private static class P implements PageProcessor, Closeable {


        @Override
        public void close() throws IOException {

        }

        @Override
        public void process(Page page) {

            Html html = page.getHtml();

           List<Selectable> trNodes= html.xpath("tbody/tr").nodes();
           for(Selectable trNode: trNodes){
                String ip = SelectableUtls.toSimpleText(trNode.xpath("td[1]")).trim();
                String port =SelectableUtls.toSimpleText(trNode.xpath("td[2]")).trim();

                if(IpTest.checkProxyIp(ip,Integer.parseInt(port))){
                    String line = "list.add(new Proxy(\""+ip+"\","+port+"));";
                    System.out.println(line);
                    gSb.append(line +"\n");
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
