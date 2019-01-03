package com.juzicool.gather.spider;

import com.juzicoo.ipservcie.IPPool;
import com.juzicoo.ipservcie.IPTester;
import com.juzicoo.ipservcie.IPservcie;
import com.juzicoo.ipservcie.WebMagicIpProxy;
import com.juzicoo.ipservcie.source.www89ipcn;
import com.juzicool.data.Juzi;
import com.juzicool.data.db.JuziDB;
import com.juzicool.gather.*;
import com.juzicool.gather.utils.JuziUtil;
import com.juzicool.gather.utils.RegexUtil;
import com.juzicool.gather.utils.SelectableUtls;
import com.juzicool.gather.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class JuzimiSpider {

/*    public JuzimiSpider(File dbFile){
        super(mProcessor);
        mDB = DB.request(dbFile);
    }*/


    /**
     * 只抓取句子迷album下的句子。
     * @param args
     */
    public static void main(String[] args) {
        Gloabal.beforeMain();


        File gatherFile = new File("./juzimi_ablum_gather.db");  //抓取状态保存在这个文件。
        File outputFile = new File("./juzimi_ablum_output.db");  //句子结果保存到这个数据库。
        File ipProxyFile = new File("./juzi_proxy.db");  //代理ip库

        final int threadSize = 20;

        //初始化IP代理组件
        System.out.println("初始化IPservcie。。。");
        IPservcie iPservcie = new IPservcie(ipProxyFile);
        iPservcie.setIPTester(new IPTester.DefaultIPTester(iPservcie,new String[]{"https://www.juzimi.com/ju/469610"}));
        iPservcie.setCollectInterval(1f); //至少每隔1小時要收集新的ip。
        iPservcie.addIpSource(new www89ipcn());
        final IPPool pool =  iPservcie.createPool(threadSize * 5,threadSize + 1,0.3f);
        pool.ready();
        System.out.println("初始化成功。。。");


        long startTime = System.currentTimeMillis();
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        httpClientDownloader.setProxyProvider(new WebMagicIpProxy(pool));


        //不使用Webmgic的Pipline来处理结果，直接在Processor保存；
        JuzimiProcessor p = new JuzimiProcessor(outputFile);

        final FileSpider spider =  new FileSpider(gatherFile,p);
        spider.setDownloader(httpClientDownloader);

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

      //  spider.addUrl("https://www.juzimi.com/album/48576?page=3");
      // spider.addUrl("https://www.juzimi.com/albums");

        spider.stopWhileExceutedSize(2000000); // 执行超过指定次数请求时停止
        spider.stopWhileProcessSucessRateSmallerThan(0.5f); // 最近请求成功率低于50%时停止抓取

        SpiderStopUI.doWhileCloase(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spider.stop();
            }
        });

        spider.thread(threadSize).run();

        System.out.println("totalItme: " + (System.currentTimeMillis() - startTime));
    }

    public static class JuzimiProcessor extends BasePageProcessor {

        JuziDB juziDB;

        public JuzimiProcessor(File output){
            juziDB = new JuziDB(output);
            juziDB.prepare();
        }

        @Override
        public void process(Page page) {

            if(page.getStatusCode()!=200){
                //采集失败，可能ip被封了
                page.setProcessOK(false);
                return;
            }

            String url = page.getRequest().getUrl().toLowerCase();
            url = UrlUtils.getUrlWithoutQuery(url);
            Html html = page.getHtml();

            boolean isGatherOk = false;
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
                    isGatherOk = true;
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

                if(!isGatherOk){
                    String simpleText = SelectableUtls.toSimpleText(html);
                    if(simpleText.contains("没有收录任何句子")){
                        isGatherOk = true;
                    }
                }

            }else if(isJuzi(url)){
                processJuzi(page);
                return;
            }else {
                List list =  html.links().nodes();
                for(Object obj : list) {
                    //Selectable selct = (Selectable) obj;
                    String abumnUrl = obj.toString();
                    if(isAlbum(abumnUrl)){
                        Request request = new Request();
                        request.setPriority(5);
                        request.setUrl(abumnUrl);
                        page.addTargetRequest(request);
                    }

                }
                isGatherOk = true;
            }
            //是否抓取成功，不成功的话保存到失败列表，下一次再试。
            page.setProcessOK(isGatherOk);

        }


        public void processJuzi(Page page) {
            Juzi juzi = new Juzi();

            String albumTitle = (String)page.getRequest().getExtra("albumTitle");
            String albumDesc =  (String)page.getRequest().getExtra("albumDesc");

            juzi.remark = albumTitle;
            juzi.applyDesc = albumDesc;

            Html html = page.getHtml();

            List tagsLink =  html.xpath("div[@class='xqlinks']/a").nodes();
            StringBuffer tagSb = new StringBuffer();
            for(Object obj : tagsLink){
                Selectable selct = (Selectable)obj;

                tagSb.append(SelectableUtls.toSimpleText(selct) +",");
                //System.out.println("add juzi:" + juziUrl);

            }
            String tags = tagSb.toString();
            juzi.tags = tags;

           // String content = html.xpath("h1[@id='xqtitle']/text()").toString();
            Selectable juziE =html.xpath("h1[@id='xqtitle']");

            Selectable fromE=html.xpath("span[contains(@class,'field-field-oriarticle')]");
            Selectable authorE =html.xpath("span[contains(@class,'field-field-oriwriter')]");

            juzi.content = SelectableUtls.toSimpleText(juziE);
            juzi.from = SelectableUtls.toSimpleText(fromE);
            juzi.author =  SelectableUtls.toSimpleText(authorE);
            if(!StringUtils.isEmpty(juzi.content)){

                juzi.from = JuziUtil.filterBookmark(juzi.from);
                juzi.author = JuziUtil.filterBookmark(juzi.author);

                long start = System.currentTimeMillis();
                juziDB.put(juzi);
                long time =System.currentTimeMillis() - start;

                System.out.println(String.format("put[%d] juzi : %s" ,time, juzi.toString()));
                page.setProcessOK(true);
                return;
            }

            //不成功的话保存到失败列表，下一次再试。
            page.setProcessOK(false);

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
