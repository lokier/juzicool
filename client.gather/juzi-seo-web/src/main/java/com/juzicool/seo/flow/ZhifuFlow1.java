package com.juzicool.seo.flow;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.juzicool.core.Promise;
import com.juzicool.webwalker.WalkCase;
import com.juzicool.webwalker.WalkClient;
import java.io.IOException;

public class ZhifuFlow1 extends BaseFlow {

    public ZhifuFlow1(){
        addCase(new ClickLink(),0);
        super.addInterLinkCase(); // 添加内部link链接
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
                pormise.setProcessText("开始访问知乎网站");
                final HtmlPage page =  client.getPage("https://zhuanlan.zhihu.com/p/54059741");

                HtmlAnchor anchor =  page.getAnchorByText("句子酷：女人 男人");
                pormise.setProcessText("开始打开句子酷网站");
                HtmlPage nextPage =  anchor.click();

                String text =  nextPage.asText();

                if(text.contains("句子酷")){
                    System.err.println("accept!!!!!!!!");
                    pormise.setProcessText("流量完成");

                    pormise.accept(nextPage);
                    return;
                }else{
                    pormise.setProcessText("流量失败");

                    System.out.println("text ： " + text);
                }

            } catch (IOException e) {
               // e.printStackTrace();
                pormise.setProcessText("流量失败，有异常");
                pormise.reject(null);
                return;
            }

        }


    }
}
