package com.juzicool.seo.flow;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.juzicool.core.Promise;
import com.juzicool.webwalker.WalkCase;
import com.juzicool.webwalker.WalkClient;

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
                pormise.sendProcessText(30,"开始访问知乎网站");
                final HtmlPage page =  client.getPage("https://zhuanlan.zhihu.com/p/54059741");
                pormise.sendProcessText(30,"打开知乎网站成功！！");

                HtmlAnchor anchor;
                try {
                    anchor = page.getAnchorByText("句子酷：女人 男人");
                }catch (ElementNotFoundException ex){

                    pormise.reject("不能找到外链入口：\n" + page.asText());
                    return;
                }
                pormise.sendProcessText(30,"开始打开句子酷网站");
                HtmlPage nextPage =  anchor.click();

                String text =  nextPage.asText();

                if(text.contains("句子酷")){
                    System.err.println("accept!!!!!!!!");
                    pormise.sendProcessText(30,"流量完成");

                    pormise.accept(nextPage);
                    return;
                }else{
                    pormise.sendProcessText(30,"流量失败");
                    System.out.println("text ： " + text);
                }

            } catch (Exception e) {
                pormise.reject(e);
                return;
            }

        }


    }
}
