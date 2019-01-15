package com.juzicool.seo.flow;

import com.gargoylesoftware.htmlunit.*;
import com.juzicoo.ipservcie.IPPool;
import com.juzicoo.ipservcie.ProxyIp;
import com.juzicool.webwalker.WalkClient;
import com.juzicool.webwalker.WalkFlow;
import com.juzicool.webwalker.WalkFlowTask;

import java.util.LinkedList;
import java.util.List;

import static com.juzicool.seo.Services.iPservcie;

public class ZhifuFlowTask extends WalkFlowTask {


    private LinkedList<ProxyIp> ipQueues = null;

    @Override
    public int getTaskId() {
        return 2;
    }

    @Override
    public String getTaskName() {
        return "知乎外链-代理IP测试-[1000以下]";
    }

    @Override
    protected WalkFlow next() {
        LinkedList<ProxyIp> queues = ipQueues;
        if(queues!=null){
            ProxyIp ip  =queues.poll();
            if(ip!= null){
                ZhifuFlow1 flow1 = new ZhifuFlow1();
                flow1.args.put("IpProxy",ip);
                return flow1;
            }
        }
        return null;
    }

    private Cache htmlCache = new Cache();

    @Override
    protected WalkClient createWalkClient(WalkFlow flow) {
        ProxyIp proxyIp = (ProxyIp) flow.args.get("IpProxy");

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

        ProxyConfig proxyConfig = client.getOptions().getProxyConfig();
        proxyConfig.setProxyHost(proxyIp.getHost());
        proxyConfig.setProxyPort(proxyIp.getPort());

        //client.getOptions().set

        wclient.setWebClient(client);



        return wclient;
    }

    @Override
    protected void releaseWalkClient(WalkClient wclient) {
        try{
           WebClient client =  wclient.getWebClient();
           client.close();
        }catch (Exception ex){

        }
    }

    @Override
    protected void onStartInBackgound() {
        //准备代理IP跑一边
       // IPPool pool =  iPservcie.createPool(200,20,0.1f);
        //pool.ready();

        List<ProxyIp> iplist =  iPservcie.getDB().next( 50,01.f);

        ipQueues = null;
        if(iplist!=null){
            ipQueues = new LinkedList<ProxyIp>(iplist);
        }

    }

    @Override
    protected void onStopInBackground() {

    }
}
