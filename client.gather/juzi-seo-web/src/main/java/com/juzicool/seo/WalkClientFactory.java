package com.juzicool.seo;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.juzicoo.ipservcie.ProxyIp;
import com.juzicool.data.db.SimpleDB;
import com.juzicool.webwalker.WalkClient;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

public class WalkClientFactory {

/*
    private static WalkClientFactory g = new WalkClientFactory();

    public static WalkClientFactory getInstance(){
        return g;
    }

*/

    private Cache htmlCache = new Cache();
    private SimpleDB db;

    public WalkClientFactory(File file){
        htmlCache.setMaxSize(300);
        db = new SimpleDB();
        db.openFile(file);
    }

    public WalkClient create(ProxyIp proxyIp){

        WalkClient wclient = WalkClient.build();

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


        client.setCache(htmlCache);

        if(proxyIp!= null) {
            ProxyConfig proxyConfig = client.getOptions().getProxyConfig();
            proxyConfig.setProxyHost(proxyIp.getHost());
            proxyConfig.setProxyPort(proxyIp.getPort());

            final String ip = proxyIp.getHost();

            //设置cookies
            client.getCookieManager().setCookiesEnabled(true);

            WebSession session = db.KV().get(ip,null);
            if(session != null && session.cookies!= null) {
                for(Cookie cookie: session.cookies){
                    client.getCookieManager().addCookie(cookie);
                }
            }
        }

        //client.getOptions().set

        wclient.setWebClient(client);
        return wclient;
    }

    public void store(WalkClient wclient){

        WebClient client =  wclient.getWebClient();
        if(client!=null){
          ProxyConfig config =   client.getOptions().getProxyConfig();
          if(config!= null){
              String ip = config.getProxyHost();
              if(!StringUtils.isEmpty(ip)){
                  Set<Cookie> cookies =  client.getCookieManager().getCookies();
                  if(cookies!= null){

                      WebSession session = new WebSession();
                      session.cookies = new ArrayList<>(cookies);

                      db.KV().put(ip,session);

                  }

              }
          }

        }

    }
}
