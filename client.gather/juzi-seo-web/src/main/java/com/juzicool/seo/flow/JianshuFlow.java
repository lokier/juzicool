package com.juzicool.seo.flow;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.juzicool.core.Promise;
import com.juzicool.webwalker.WalkCase;
import com.juzicool.webwalker.WalkClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JianshuFlow extends BaseFlow {

    public JianshuFlow(){
        super.addCase(new EnterLink(),30*1000);
        super.addInterLinkCase(); // 添加内部link链接
    }

    @Override
    public String getName() {
        return "个人简书主页flow";
    }




    private static class EnterLink extends WalkCase {

        @Override
        public long getTimeout() {
            return 60*1000;
        }

        @Override
        public void doCase(WalkClient wclient, Promise pormise) {

            WebClient client = wclient.getWebClient();
            try {
                //Random r = new Random(System.currentTimeMillis());
                String url = "https://www.jianshu.com/u/7a3d1067aba0";
                pormise.sendProcessText(10,"开始打开简书入口：" + url);
                HtmlPage page =  client.getPage(url);


                List<HtmlAnchor> anchors =  page.getAnchors();
                if(anchors!= null){
                    Random r = new Random(System.currentTimeMillis());
                    List<HtmlAnchor> myAnchor = new ArrayList<>();
                    for(HtmlAnchor a : anchors) {
                        if(a.asText().contains("初创记")){
                            myAnchor.add(a);
                        }
                    }
                    if(myAnchor.size() > 0){
                        HtmlAnchor anchor = myAnchor.get(r.nextInt(myAnchor.size()));
                        pormise.sendProcessText(15,"开始个人简书文章页：" + anchor.asText());

                        HtmlPage nextPage =  anchor.click();
                        //查找句子酷的外链入口
                        pormise.sendProcessText(15,"分析句子酷入库链");

                        anchors =  nextPage.getAnchors();
                        if(anchors!= null){
                            myAnchor = new ArrayList<>();
                            for(HtmlAnchor a : anchors) {
                                if(a.getHrefAttribute().contains("juzicool.com")){
                                    myAnchor.add(a);
                                }
                            }
                            if(myAnchor.size() > 0) {
                                anchor = myAnchor.get(r.nextInt(myAnchor.size()));
                                pormise.sendProcessText(15,"开始进入句子酷：" + anchor.getHrefAttribute());
                                nextPage =  anchor.click();
                                pormise.accept(nextPage);
                                return;
                            }


                        }
                        pormise.sendProcessText(15,"该页面没有找到外链");


                    }
                }

                pormise.reject("没有找到简书的外链入口");
                return;
            } catch (Exception e) {
                pormise.reject(e);
                return;
            }

        }


    }
}
