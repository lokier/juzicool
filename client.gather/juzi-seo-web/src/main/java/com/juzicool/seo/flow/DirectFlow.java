package com.juzicool.seo.flow;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.juzicool.core.Promise;
import com.juzicool.webwalker.WalkCase;
import com.juzicool.webwalker.WalkClient;

import java.util.Random;

public class DirectFlow extends BaseFlow {

    public DirectFlow(){
        super.addCase(new EnterLink(),30*1000);
        super.addInterLinkCase(); // 添加内部link链接
    }

    @Override
    public String getName() {
        return "直链flow-1";
    }

    private static final String[] URLS = new String[]{
            "https://www.juzicool.com/group?id=1",
            "https://www.juzicool.com/group?id=2",
            "https://www.juzicool.com/group?id=3",
            "https://www.juzicool.com/group?id=4",
            "https://www.juzicool.com/group?id=5",
            "https://www.juzicool.com/group?id=6",
            "https://www.juzicool.com/group?id=7",
            "https://www.juzicool.com/coopration",
            "https://www.juzicool.com/",
            "https://www.juzicool.com/",
            "https://www.juzicool.com/",
            "https://www.juzicool.com/group?id=8"
    };


    private static class EnterLink extends WalkCase {

        @Override
        public long getTimeout() {
            return 30*1000;
        }

        @Override
        public void doCase(WalkClient wclient, Promise pormise) {

            WebClient client = wclient.getWebClient();
            try {
                Random r = new Random(System.currentTimeMillis());
                String url = URLS[r.nextInt(URLS.length)];
                pormise.sendProcessText(10,"开始打开直链入口：" + url);

                Object obj =  client.getPage(url);

                if(obj instanceof  HtmlPage){
                    pormise.sendProcessText(10,"打开直链入口成功");
                    pormise.accept(obj);
                    return;
                }
                pormise.reject("未知打开内容：" + obj.toString());


            } catch (Exception e) {
                pormise.reject(e);
                return;
            }

        }


    }
}
