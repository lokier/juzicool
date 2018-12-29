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


        putProxy("119.101.113.246",9999);//t= 702
        putProxy("59.126.222.125",60785);//t= 1141
        putProxy("58.210.136.83",30498);//t= 1393
        putProxy("183.33.129.79",808);//t= 578
        putProxy("119.101.116.2",9999);//t= 680
        putProxy("111.177.183.42",9999);//t= 423
        putProxy("119.101.113.103",9999);//t= 1525
        putProxy("211.21.120.163",8080);//t= 2386
        putProxy("218.24.16.198",50044);//t= 768
        putProxy("119.101.114.220",9999);//t= 1903
        putProxy("124.205.143.212",36674);//t= 2615
        putProxy("119.101.115.241",9999);//t= 383
        putProxy("218.14.115.211",3128);//t= 621
        putProxy("58.55.145.175",9999);//t= 375
        putProxy("119.101.112.136",9999);//t= 362
        putProxy("119.101.112.135",9999);//t= 2169
        putProxy("111.177.174.87",9999);//t= 387
        putProxy("119.101.117.248",9999);//t= 382
        putProxy("119.101.112.107",9999);//t= 767
        putProxy("221.10.159.234",1337);//t= 892
        putProxy("111.177.172.172",9999);//t= 349
        putProxy("140.207.155.94",39354);//t= 548
        putProxy("119.101.116.217",9999);//t= 358
        putProxy("119.101.112.133",9999);//t= 686
        putProxy("116.192.175.93",41944);//t= 3105
        putProxy("117.21.191.154",32340);//t= 333
        putProxy("119.101.112.28",9999);//t= 4381
        putProxy("114.33.59.156",30274);//t= 3705
        putProxy("119.101.118.44",9999);//t= 3078
        putProxy("59.37.33.62",50686);//t= 2845
        putProxy("27.24.215.49",42164);//t= 433
        putProxy("119.101.116.240",9999);//t= 409
        putProxy("112.12.37.196",53281);//t= 751
        putProxy("119.101.117.147",9999);//t= 367
        putProxy("118.250.68.99",1080);//t= 888

        putProxy("36.33.32.158",46817);//t= 476
        putProxy("119.101.116.160",9999);//t= 542

        putProxy("183.239.174.77",53281);//t= 1546
        putProxy("120.198.61.126",55293);//t= 1727
        putProxy("58.17.125.215",53281);//t= 516
        putProxy("119.39.238.27",9999);//t= 1163


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
