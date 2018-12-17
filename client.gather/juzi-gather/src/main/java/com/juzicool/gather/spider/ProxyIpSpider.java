package com.juzicool.gather.spider;

import com.juzicool.gather.FileSpider;
import com.juzicool.gather.Gloabal;
import com.juzicool.gather.utils.UrlUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
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
        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("web-proxy.oa.com",8080)));


        Spider spider =  new Spider(new P());

        final int pageSize = 1;
        for(int i = 1; i<=pageSize;i++){
            spider.addUrl("http://www.89ip.cn/index_"+pageSize+".html");
        }


        spider.thread(1).run();
    }

    private static class P implements PageProcessor, Closeable {

        @Override
        public void close() throws IOException {

        }

        @Override
        public void process(Page page) {

            Html html = page.getHtml();

           List<Selectable> trNodes= html.xpath("tbody/tr").nodes();
           for(Selectable trNode: trNodes){
                String ip = trNode.xpath("td/[1]").smartContent().toString();
                String prot = trNode.xpath("td/[2]").smartContent().toString();

                System.out.println("");
           }
        }

        @Override
        public Site getSite() {
            return null;
        }

        private Site gSite = Site.me().setRetryTimes(2).setSleepTime(500).setTimeOut(3000);

    }


}
