package com.juzicool.seo.flow;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.juzicool.core.Promise;
import com.juzicool.webwalker.WalkCase;
import com.juzicool.webwalker.WalkClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/***
 * 内部的case。
 */
public class InterRandomCase {

    public static List<WalkCase> create(final WebClient client){

        ArrayList<WalkCase> rets = new ArrayList<>();

        return rets;
    }

    public static class DumpCase extends WalkCase{


        @Override
        public long getTimeout() {
            return 1000;
        }

        @Override
        protected void doCase(WalkClient wclient, Promise pormise) {
            pormise.accept(null);
        }
    }

    public static class SearchCase extends WalkCase{

        private static final String[] searchText = new String[]{
          "唯美句子"
          ,"女人 男人"
          ,"高傲的句子"
          ,"悲伤的句子"
          ,"可爱之极"
         ,"骂人"
           ,"搞笑","表白","打斗","恐怖","古风","古文"
        };

        private static int _index = 0;

        private static synchronized  String nextSearchText(){
            int index = _index++;
            index =  index % searchText.length;
            return searchText[index];
        }

        @Override
        public long getTimeout() {
            return 50*1000;
        }

        @Override
        protected void doCase(WalkClient wclient, Promise pormise) {

            HtmlPage page =  (HtmlPage)pormise.getResolveData();

            pormise.sendProcessText(30,"在句子酷：开始输入关键词搜索");
            HtmlInput input = null;


            try {
                input = page.getHtmlElementById("aw-search-query");
            }catch (ElementNotFoundException ex){

            }

            if(input!= null) {
                input.setTextContent(nextSearchText());


                HtmlElement  element =  null;
                try {
                    element = page.getHtmlElementById("global_search_btns");
                }catch (ElementNotFoundException ex){

                }
                if(element == null){
                    element =   page.getFirstByXPath("//span[@class='main-search-btn']");
                }


                if(element!= null){
                    try {
                        pormise.sendProcessText(30,"已经输入关键词，开始提交");

                        HtmlPage nextPage =  element.click();
                        pormise.accept(nextPage);
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                pormise.sendProcessText(30,"在句子酷找不到提交按钮");
                pormise.accept(null);
                return;
            }
            pormise.sendProcessText(30,"在句子酷无法输入关键词搜索");

            pormise.accept("");


        }
    }

    public static class ClickSearchResultCase extends WalkCase{


        @Override
        public long getTimeout() {
            return 20*1000;
        }

        @Override
        protected void doCase(WalkClient wclient, Promise pormise) {

            HtmlPage page =  (HtmlPage)pormise.getResolveData();

            List<HtmlAnchor> anchors =  page.getAnchors();
            if(anchors!= null){
                pormise.sendProcessText(20,"分析搜索页面");
                List<HtmlAnchor> myAnchor = new ArrayList<>();
                for(HtmlAnchor a : anchors) {
                    String hef = a.getHrefAttribute();
                    if(hef.contains("www.juzicool.com") || hef.startsWith("./") || hef.startsWith("/")){
                        myAnchor.add(a);
                    }
                    //System.out.println("href:"+a.getHrefAttribute());
                }
                if(myAnchor.size() > 0){
                    Random r = new Random(System.currentTimeMillis());
                    HtmlAnchor  anchor = myAnchor.get(r.nextInt(myAnchor.size()));
                    try {
                        pormise.sendProcessText(20,"打开搜索页面：" + anchor.getHrefAttribute());
                        anchor.click();
                        pormise.accept(page);
                        return;
                    } catch (Exception e) {
                    }

                }
            }

            pormise.sendProcessText(20,"没有找到搜索页面");

            pormise.accept(page);

        }
    }
}
