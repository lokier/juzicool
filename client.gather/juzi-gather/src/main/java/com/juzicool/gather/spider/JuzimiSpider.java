package com.juzicool.gather.spider;

import com.juzicool.gather.BasePageProcessor;
import com.juzicool.gather.DB;
import com.juzicool.gather.Gloabal;
import com.juzicool.gather.Juzi;
import com.juzicool.gather.processor.JuzimiProcessor;
import com.juzicool.gather.utils.RegexUtil;
import com.juzicool.gather.utils.SelectableUtls;
import com.juzicool.gather.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.io.File;
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
        //Gloabal.beforeMain();


       JuzimiProcessor p = new JuzimiProcessor();

        Spider spider =  Spider.create(p);

        spider.addUrl("https://www.juzimi.com/album/48576");
        //spider.addUrl("https://www.juzimi.com/album/2364?page=1");  //优美的句子,美好,难过，或暂，长久,难忘

        spider.thread(1).run();
    }


    public static class JuzimiProcessor extends BasePageProcessor {


        @Override
        public void process(Page page) {

            String url = page.getRequest().getUrl().toLowerCase();
            url = UrlUtils.getUrlWithoutQuery(url);
            Html html = page.getHtml();


            if(isAlbum(url)){

                List herfList = html.xpath("a[@class='xlistju']@href").nodes();
                for(Object selectable : herfList){
                    System.out.println("href: " + selectable);
                }

            }
            if(isJuzi(url)){
                processJuzi(page);
            }


        }

        private boolean isJuzi(String url){
            String path = UrlUtils.getPath(url);
            return RegexUtil.containText("/ju/\\d+",path);
        }

        private boolean isAlbum(String url){
            String path = UrlUtils.getPath(url);
            return RegexUtil.containText("/album/\\d+",path);
        }

        public void processJuzi(Page page) {

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
}
