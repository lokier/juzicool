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

        putProxy("111.230.211.23",1080); // 19, t= 355
        putProxy("118.89.148.211",1080); // 16, t= 1188
        putProxy("163.125.28.214",8118); // 15, t= 2009
        putProxy("60.217.64.237",35091); // 17, t= 1265
        putProxy("123.207.234.226",3128); // 21, t= 1056
        putProxy("182.88.91.87",8123); // 24, t= 1125
        putProxy("111.43.70.58",51547); // 18, t= 2005
        putProxy("120.84.146.194",9000); // 20, t= 2005
        putProxy("103.254.185.219",31065); // 22, t= 1502
        putProxy("220.132.135.79",49997); // 27, t= 1353
        putProxy("222.210.158.157",8118); // 32, t= 1160
        putProxy("163.125.70.204",8888); // 23, t= 2005
        putProxy("180.118.242.65",61234); // 25, t= 2005
        putProxy("14.115.104.143",808); // 26, t= 2006
        putProxy("110.73.41.145",8123); // 28, t= 2004
        putProxy("47.104.201.136",53281); // 29, t= 2022
        putProxy("223.145.212.41",8118); // 30, t= 2004
        putProxy("119.101.115.141",9999); // 35, t= 1213
        putProxy("119.179.60.117",8118); // 31, t= 2004
        putProxy("119.101.115.115",9999); // 40, t= 1190
        putProxy("157.0.210.242",53540); // 34, t= 2003
        putProxy("119.101.116.102",9999); // 36, t= 2004
        putProxy("119.101.115.151",9999); // 41, t= 1167
        putProxy("119.101.113.35",9999); // 42, t= 1193
        putProxy("119.101.115.241",9999); // 46, t= 1167
        putProxy("119.101.114.179",9999); // 44, t= 2005
        putProxy("59.78.2.140",1080); // 50, t= 2004
        putProxy("218.204.53.161",34967); // 43, t= 2786
        putProxy("119.101.114.7",9999); // 51, t= 2004
        putProxy("122.116.67.146",31195); // 52, t= 2004
        putProxy("118.250.68.99",1080); // 53, t= 2004
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
