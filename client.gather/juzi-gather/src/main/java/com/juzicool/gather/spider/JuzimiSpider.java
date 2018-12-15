package com.juzicool.gather.spider;

import com.juzicool.gather.*;
import com.juzicool.gather.utils.RegexUtil;
import com.juzicool.gather.utils.SelectableUtls;
import com.juzicool.gather.utils.UrlUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.io.File;
import java.util.List;

public class JuzimiSpider {

    private DB mDB = null;

/*    public JuzimiSpider(File dbFile){
        super(mProcessor);
        mDB = DB.request(dbFile);
    }*/




    public static void main(String[] args) {
        Gloabal.beforeMain();


        File file = new File("./juzimi.db");  //抓取状态保存在这个文件。
        //JuzimiSpider spider = new JuzimiSpider(file);

        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("web-proxy.oa.com",8080)));


        JuzimiProcessor p = new JuzimiProcessor();

        FileSpider spider =  new FileSpider(file,p);

        spider.setKeyGetter(new FileSpider.KeyGetter() {
            //避免对同一个url抓取，减少抓取次数。
            @Override
            public String getKey(String url) {
                //过滤哪些无用的数据。
                String path = UrlUtils.getPath(url);
                int index = path.indexOf("#");

                if(index > 0){
                    path = path.substring(0,index);
                }
                return path;
            }
        });

        //重新开始上次请求失败的url请求
        spider.restoreErrorRequest();

        spider.addUrl("https://www.juzimi.com/album/48574");
        spider.stopWhileExceutedSize(3);

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
                   // System.out.println("add juzi:" + juziUrl +" , title  : " + albumTitle+",desc:"+albumDesc);


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
