package com.juzicool.seo.flow;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.juzicool.webwalker.WalkCase;
import com.juzicool.webwalker.WalkClient;
import com.juzicool.webwalker.WalkFlow;
import com.juzicool.webwalker.core.Promise;

import java.io.IOException;

public class ZhifuFlow1 extends WalkFlow {

    public ZhifuFlow1(){
        addCase(new ClickLink(),0);
    }

    @Override
    public String getName() {
        return "知乎外链-1";
    }


    private static class ClickLink extends WalkCase{

        @Override
        public long getTimeout() {
            return 30*1000;
        }

        @Override
        public void doCase(WalkClient wclient, Promise pormise) {

            WebClient client = wclient.getWebClient();
            try {
                final HtmlPage page =  client.getPage("https://zhuanlan.zhihu.com/p/54059741");

                HtmlAnchor anchor =  page.getAnchorByText("句子酷：女人 男人");

                HtmlPage nextPage =  anchor.click();

               String text =  nextPage.asText();

               System.out.println("text ： " + text);

                pormise.accept(null);

            } catch (IOException e) {
                pormise.reject(null);
                return;
            }

        }


    }
}
