package com.juzicool.gather;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class IpProxyProvider implements ProxyProvider {

    private ArrayList<Proxy> list = new ArrayList<>();
    private HashSet<String> ipMap = new HashSet<>();
    private int index = 0;
    public IpProxyProvider(){

        putProxy("175.17.154.157",8080);//t= 1633
        putProxy("119.39.238.222",9999);//t= 1706
        putProxy("218.90.174.37",34749);//t= 2549
        putProxy("119.176.51.135",53281);//t= 2687
        putProxy("59.37.33.62",50686);//t= 677
        putProxy("119.101.112.246",9999);//t= 1568
        putProxy("117.159.23.115",53385);//t= 3974
        putProxy("119.101.112.132",9999);//t= 559
        putProxy("121.61.0.197",9999);//t= 368
        putProxy("218.249.45.162",52316);//t= 3347
        putProxy("119.101.112.193",9999);//t= 1482
        putProxy("183.195.145.174",53281);//t= 743
        putProxy("119.101.114.143",9999);//t= 1627
        putProxy("103.228.246.220",36651);//t= 3009
        putProxy("60.205.213.172",8118);//t= 453
        putProxy("119.101.112.86",9999);//t= 345
        putProxy("219.142.132.146",52782);//t= 2229
        putProxy("110.52.234.244",9999);//t= 4634
        putProxy("119.101.115.236",9999);//t= 2813
        putProxy("61.142.72.150",33270);//t= 312
        putProxy("119.101.115.221",9999);//t= 399
        putProxy("119.101.113.212",9999);//t= 776
        putProxy("119.101.112.72",9999);//t= 417
        putProxy("110.52.234.73",9999);//t= 4162
        putProxy("117.139.126.236",53281);//t= 2742
        putProxy("119.101.115.245",9999);//t= 4435
        putProxy("119.101.116.2",9999);//t= 1631
        putProxy("119.101.112.66",9999);//t= 701
        putProxy("119.101.117.97",9999);//t= 2097
        putProxy("118.190.149.36",8080);//t= 2879
        putProxy("110.52.234.237",9999);//t= 3232
        putProxy("119.101.113.72",9999);//t= 881
        putProxy("119.101.116.214",9999);//t= 391
        putProxy("220.133.218.213",34982);//t= 652
        putProxy("116.113.27.170",30683);//t= 3881
        putProxy("139.159.7.150",52908);//t= 599
        putProxy("119.101.115.199",9999);//t= 1284
        putProxy("58.208.16.117",53281);//t= 416
        putProxy("58.254.220.116",53579);//t= 691
        putProxy("119.101.116.136",9999);//t= 401

        putProxy("119.101.113.155",9999);//t= 350
        putProxy("117.186.214.74",9999);//t= 919
        putProxy("119.101.115.5",9999);//t= 510
    }

    public void putProxy(String ip,int port){
        if(!ipMap.contains(ip)){
            list.add(new Proxy(ip,port));
        }
    }

    @Override
    public void returnProxy(Proxy proxy, Page page, Task task) {

    }

    @Override
    public Proxy getProxy(Task task) {
        index = index % list.size();
        Proxy p = list.get(index);
        index++;
        return p;
    }


    public static void main(String[] args) {

        // String url = "http://www.baidu.com";
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(15);

        IpProxyProvider provider = new IpProxyProvider();
        final AtomicInteger count = new AtomicInteger(0);
        final CountDownLatch countDownload = new CountDownLatch(provider.list.size());
        for(int i = 0;i < provider.list.size();i++){
            final Proxy proxy = provider.list.get(i);
            final int index = i;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                   // System.out.println(String.format("start check ip(\"%s\",%d);",proxy.getHost(),proxy.getPort() ));
                    long startTime =System.currentTimeMillis();
                    boolean isOk = IpTest.checkProxyIp(proxy.getHost(),proxy.getPort());
                    long time = System.currentTimeMillis()- startTime;

                    if(!isOk){
                        if(time < 5000){
                            System.out.println(String.format("putProxy(\"%s\",%d); // %d, t= %d",proxy.getHost(),proxy.getPort(),index,time ));
                        }
                    }else{
                        //System.out.println(String.format("error Proxy(\"%s\",%d);",proxy.getHost(),proxy.getPort() ));

                    }
                    countDownload.countDown();
                }
            });
        }
        //System.out.println("end 2");

        try {
            countDownload.await();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end");

        System.exit(0);

    }
}
