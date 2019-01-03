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
        putProxy("110.52.235.190",9999);//t= 631
        putProxy("112.6.133.89",31939);//t= 852
        putProxy("119.101.117.163",9999);//t= 892
        putProxy("119.101.113.209",9999);//t= 896
        putProxy("119.101.112.122",9999);//t= 409
        putProxy("119.39.238.110",9999);//t= 1721
        putProxy("110.52.234.167",9999);//t= 686
        putProxy("219.139.175.50",9999);//t= 1216
        putProxy("139.199.117.41",8118);//t= 529
        putProxy("121.225.229.234",9999);//t= 301
        putProxy("115.46.86.30",8123);//t= 703
        putProxy("111.177.185.101",9999);//t= 337
        putProxy("59.110.48.236",3128);//t= 4553
        putProxy("119.101.113.66",9999);//t= 1324
        putProxy("119.101.115.182",9999);//t= 366
        putProxy("119.101.117.190",9999);//t= 936
        putProxy("111.177.169.33",9999);//t= 361
        putProxy("218.76.253.201",61310);//t= 2450
        putProxy("119.39.238.101",9999);//t= 2703
        putProxy("119.101.114.125",9999);//t= 343
        putProxy("110.52.235.194",9999);//t= 1012
        putProxy("110.52.235.144",9999);//t= 465

        putProxy("119.39.238.211",9999);//t= 378
        putProxy("110.52.235.225",9999);//t= 712
        putProxy("119.101.116.8",9999);//t= 388

        putProxy("118.25.222.122",1080); // 14, t= 4164
        putProxy("119.101.116.56",9999); // 2, t= 4599
        putProxy("119.101.113.50",9999); // 3, t= 4899
        putProxy("119.101.115.42",9999); // 8, t= 4907
        putProxy("222.95.174.181",9999); // 11, t= 4907
        putProxy("112.95.21.187",8888); // 15, t= 2007
        putProxy("119.101.112.61",9999); // 16, t= 2004
        putProxy("119.101.112.221",9999); // 17, t= 2004
        putProxy("171.80.139.212",9999); // 18, t= 2005
        putProxy("112.85.128.113",9999); // 19, t= 2009
        putProxy("121.61.47.240",9999); // 20, t= 1116
        putProxy("110.52.234.244",9999); // 25, t= 1111
        putProxy("119.101.115.245",9999); // 27, t= 1117
        putProxy("219.142.132.146",52782); // 24, t= 1146
        putProxy("119.101.115.236",9999); // 29, t= 1108
        putProxy("119.101.117.97",9999); // 32, t= 383
        putProxy("110.52.234.73",9999); // 30, t= 1149
        putProxy("119.101.113.212",9999); // 31, t= 1133
        putProxy("111.181.37.150",9999); // 21, t= 2006
        putProxy("112.95.21.206",8888); // 22, t= 2016
        putProxy("119.101.112.86",9999); // 26, t= 2005
        putProxy("119.101.116.214",9999); // 28, t= 2007
        putProxy("110.52.234.237",9999); // 33, t= 1164
        putProxy("119.101.112.66",9999); // 34, t= 1163
        putProxy("116.113.27.170",30683); // 45, t= 577
        putProxy("220.133.218.213",34982); // 40, t= 1133
        putProxy("119.101.115.199",9999); // 42, t= 1108
        putProxy("119.101.113.155",9999); // 44, t= 1099
        putProxy("117.139.126.236",53281); // 35, t= 2010
        putProxy("61.142.72.150",33270); // 36, t= 2007
        putProxy("58.208.16.117",53281); // 43, t= 1126
        putProxy("119.101.115.221",9999); // 37, t= 2006
        putProxy("119.101.112.72",9999); // 38, t= 2004
        putProxy("119.101.116.2",9999); // 39, t= 2004
        putProxy("119.101.113.72",9999); // 41, t= 2004
        putProxy("119.101.116.136",9999); // 46, t= 2006
        putProxy("119.101.115.5",9999); // 47, t= 2006
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
