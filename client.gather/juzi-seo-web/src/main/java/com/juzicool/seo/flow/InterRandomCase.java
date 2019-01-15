package com.juzicool.seo.flow;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.juzicool.core.Promise;
import com.juzicool.webwalker.WalkCase;
import com.juzicool.webwalker.WalkClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            return 30*1000;
        }

        @Override
        protected void doCase(WalkClient wclient, Promise pormise) {



            HtmlPage page =  (HtmlPage)pormise.getResolveData();


           // wclient.getWebClient().setCache();

           // page.

            pormise.setProcessText("在句子酷：输入关键词搜索");
            HtmlInput input = page.getHtmlElementById("aw-search-query");
            if(input!= null) {
                input.setTextContent(nextSearchText());


                HtmlElement  element =  page.getHtmlElementById("global_search_btns");
                if(element == null){
                    element =   page.getFirstByXPath("span[class='main-search-btn']");
                }


                if(element!= null){
                    try {
                        pormise.setProcessText("在句子酷：输入关键词搜索");

                        HtmlPage nextPage =  element.click();

                        pormise.accept(null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                pormise.setProcessText("在句子酷找不到提交按钮");

                return;
            }
            pormise.setProcessText("在句子酷无法输入关键词搜索");

            pormise.reject(null);


        }
    }
}
