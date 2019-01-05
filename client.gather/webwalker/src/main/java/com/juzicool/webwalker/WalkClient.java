package com.juzicool.webwalker;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;

public class WalkClient {

    public static WalkClient build() {
        return new WalkClient(null);
    }

    public static WalkClient build(ClientSession session){
        return new WalkClient(session);
    }

    private ClientSession session = null;

    private WebClient mWebClient = null;

    private WalkClient(ClientSession session){
        if(session == null){
            session  = new ClientSession();
        }
    }

    private WebClient createWebClient(){
        WebClient client = new WebClient(BrowserVersion.FIREFOX_60);
        client.setJavaScriptTimeout(5000);
        client.getOptions().setUseInsecureSSL(true);// 接受任何主机连接 无论是否有有效证书
        client.getOptions().setJavaScriptEnabled(true);// 设置支持javascript脚本
        client.getOptions().setCssEnabled(false);// 禁用css支持
        client.getOptions().setThrowExceptionOnScriptError(false);// js运行错误时不抛出异常
        client.getOptions().setTimeout(30000);// 设置连接超时时间
        client.getOptions().setDoNotTrackEnabled(false);
        client.setAjaxController(new NicelyResynchronizingAjaxController());// 设置Ajax异步
        client.waitForBackgroundJavaScript(20000);
        return client;
    }

    public WebClient getWebClient(){
        if(mWebClient == null){
            mWebClient = createWebClient();
        }
        return mWebClient;
    }

    public void despose(){

    }


    //public static void setIP
}
