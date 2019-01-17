package com.juzicool.seo.flow;

import com.gargoylesoftware.htmlunit.*;
import com.juzicoo.ipservcie.ProxyIp;
import com.juzicool.seo.db.WorkFlowTaskDB;
import com.juzicool.webwalker.WalkClient;
import com.juzicool.webwalker.WalkFlow;
import com.juzicool.webwalker.WalkFlowTask;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.juzicool.seo.Services.iPservcie;

public class RandomFlowTask extends WalkFlowTask {


    private LinkedList<ProxyIp> ipQueues = null;
    private Random random = new Random(System.currentTimeMillis());

    @Override
    public int getTaskId() {
        return 3;
    }

    @Override
    public String getTaskName() {
        return "随机Flow任务，外链和直接链:size=" + getCurrentIpSize();
    }

    @Override
    protected WalkFlow next() {
        LinkedList<ProxyIp> queues = ipQueues;
        if(queues!=null){
            ProxyIp ip  =queues.poll();
            if(ip!= null){
                WalkFlow flow = __next();

                flow.args.put("IpProxy",ip);
                return flow;
            }
        }
        return null;
    }

    private WalkFlow __next(){
        Class clss = FLOW_CLASSES[random.nextInt(FLOW_CLASSES.length)];
        try {
            return (WalkFlow)clss.newInstance();
        }catch (Exception ex){
           LOG.warn(ex.getMessage(),ex);
        }
        return null;
    }


    private static Class[] FLOW_CLASSES = new Class[]{
           /* ZhifuFlow1.class,
            DirectFlow.class,*/
            JianshuFlow.class,
    };


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

    private int getCurrentIpSize(){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        int ipSize = 5;
        if(hour >=0 && hour<=2){
            ipSize = 40;
        }else if(hour>2 && hour <= 6){
            ipSize = 30;
        }else if(hour > 6 && hour<=8){
            ipSize = 80;
        }else if( hour > 8 && hour <=11){
            ipSize = 160;
        }else if( hour > 11 && hour <=13){
            ipSize = 320;
        }else if(hour > 13 && hour <= 16){
            ipSize = 250;
        }else if(hour > 16 && hour <=18){
            ipSize = 220;
        }else if(hour >18 && hour <= 20){
            ipSize = 320;
        }else if(hour>20 && hour <=22){
            ipSize = 200;
        }else if(hour>22 && hour <=23){
            ipSize = 80;
        }else {
            ipSize = 30;
        }

        return ipSize;
    }

    @Override
    protected void onStartInBackgound() {
        //准备代理IP跑一边
       // IPPool pool =  iPservcie.createPool(200,20,0.1f);
        //pool.ready();

      // Calendar calendar =  Calendar.getInstance();
       //calendar.setTimeInMillis(System.currentTimeMillis());

        WorkFlowTaskDB db = WorkFlowTaskDB.get(getTaskId());

        long one_day = 24 * 60 * 60 * 1000;

        //删除一天前的日志。
        db.deleyBefore(System.currentTimeMillis() - one_day);

        int ipSize = getCurrentIpSize();

        List<ProxyIp> iplist =  iPservcie.getDB().next( ipSize,01.f);

        ipQueues = null;
        if(iplist!=null){
            ipQueues = new LinkedList<ProxyIp>(iplist);
        }

    }

    @Override
    protected void onStopInBackground() {

    }
}
