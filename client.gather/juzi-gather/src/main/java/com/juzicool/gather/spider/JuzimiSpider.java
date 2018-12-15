package com.juzicool.gather.spider;

import com.juzicool.gather.*;
import com.juzicool.gather.processor.JuzimiProcessor;
import com.juzicool.gather.utils.RegexUtil;
import com.juzicool.gather.utils.SelectableUtls;
import com.juzicool.gather.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JuzimiSpider {

    private DB mDB = null;

/*    public JuzimiSpider(File dbFile){
        super(mProcessor);
        mDB = DB.request(dbFile);
    }*/




    public static void main(String[] args) {

        File file = new File("./juzimi.db");
        //JuzimiSpider spider = new JuzimiSpider(file);
        Gloabal.beforeMain();

        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("web-proxy.oa.com",8080)));


        JuzimiProcessor p = new JuzimiProcessor();
        SimpleDB db = new SimpleDB();
        db.openFile(file);
        SimpleDBSpider spider =  new SimpleDBSpider(db,p);

        spider.addUrl("https://www.juzimi.com/album/48574");
        spider.stopWhileExceutedSize(7);

        spider.thread(1).run();
    }


    public static class JuzimiProcessor extends BasePageProcessor {


        @Override
        public void process(Page page) {

            String url = page.getRequest().getUrl().toLowerCase();
            url = UrlUtils.getUrlWithoutQuery(url);
            Html html = page.getHtml();

            if(isAlbum(url)){

                String albumTitle = html.xpath("h1[@class='xqalbumusertitle']/span/text()").toString();
                String albumDesc = html.xpath("div[@class='contentalbum clear-block']/text()").toString();

                List list = html.xpath("a[@class='xlistju']").nodes();
                for(Object obj : list){
                    Selectable selct = (Selectable)obj;

                    String juziUrl = selct.links().toString();
                    System.out.println("add juzi:" + juziUrl +" , title  : " + albumTitle+",desc:"+albumDesc);


                    Request request = new Request();
                    request.setUrl(juziUrl);
                    request.putExtra("albumTitle",albumTitle);
                    request.putExtra("albumDesc",albumDesc);
                    request.setPriority(10);
                    page.addTargetRequest(request);

                }

                list = html.links().nodes();


                for(Object obj : list){
                    Selectable selct = (Selectable)obj;
                    String subUrl = selct.toString();
                    if(isAlbum(subUrl)){
                        Request request = new Request();
                        request.setPriority(5);
                        request.setUrl(subUrl);
                        page.addTargetRequest(request);
                    }
                }

            }else if(isJuzi(url)){
                processJuzi(page);
            }

        }


        public void processJuzi(Page page) {
            String albumTitle = (String)page.getRequest().getExtra("albumTitle");
            String albumDesc =  (String)page.getRequest().getExtra("albumDesc");


            Html html = page.getHtml();

            List tagsLink =  html.xpath("div[@class='xqlinks']/a").nodes();
            StringBuffer tagSb = new StringBuffer();
            for(Object obj : tagsLink){
                Selectable selct = (Selectable)obj;

                tagSb.append(SelectableUtls.toSimpleText(selct) +",");
                //System.out.println("add juzi:" + juziUrl);

            }
            String tags = tagSb.toString();

            String content = html.xpath("h1[@id='xqtitle']/text()").toString();

            System.out.println(String.format("gather juzi : [%s],[%s],[%s],%s",albumTitle,albumDesc,tags,content)  );

        }






/*        private boolean parseCase1(Html html, List<Juzi> result) {
            List lists = html.xpath("div[@class='views-field views-field-phpcode']").nodes();

            if(lists.size() > 0) {
                for(int i = 0 ;i < lists.size();i++) {
                    Selectable selct = (Selectable)lists.get(i);

                    Selectable juziE =selct.xpath("a[@class='xlistju']/text()");
                    Selectable fromE=selct.xpath("span[@class='views-field-field-oriarticle-value']/text()");
                    Selectable authorE =selct.xpath("a[@class='views-field-field-oriwriter-value']/text()");

                    Juzi juzi = new Juzi();
                    juzi.content = SelectableUtls.toSimpleText(juziE);
                    juzi.from = fromE.toString();
                    juzi.author = authorE.toString();

                    write(juzi);


                }
                return true;
            }
            return false;
        }

        private boolean parseCase2(Html html,List<Juzi> result) {
            List lists = html.xpath("div[@class='views-field-phpcode']").nodes();

            if(lists.size() > 0) {
                for(int i = 0 ;i < lists.size();i++) {
                    Selectable selct = (Selectable)lists.get(i);

                    Selectable juziE =selct.xpath("a[@class='xlistju']");

                    Selectable fromE=selct.xpath("span[@class='views-field-field-oriarticle-value']/text()");
                    Selectable authorE =selct.xpath("a[@class='views-field-field-oriwriter-value']/text()");

                    Juzi juzi = new Juzi();
                    juzi.content = SelectableUtls.toSimpleText(juziE);
                    juzi.from = fromE.toString();
                    juzi.author = authorE.toString();

                    write(juzi);


                }
                if(result.size()>0) {
                    return true;
                }
            }
            return false;
        }*/


    }


    private static boolean isJuzi(String url){
        url = UrlUtils.getUrlWithoutQuery(url);
        String path = UrlUtils.getPath(url);
        return RegexUtil.containText("ju/\\d+",path);
    }

    private static boolean isAlbum(String url){
        url = UrlUtils.getUrlWithoutQuery(url);
        String path = UrlUtils.getPath(url);
        return RegexUtil.containText("album/\\d+",path);
    }
}
