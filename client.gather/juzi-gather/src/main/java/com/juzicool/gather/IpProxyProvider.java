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

        putProxy("114.55.236.62",3128);//t= 553
        putProxy("125.40.238.181",56738);//t= 722
        putProxy("111.43.70.58",51547);//t= 1202
        putProxy("118.163.165.250",3128);//t= 941
        putProxy("39.137.20.87",8080);//t= 1000
        putProxy("118.81.69.249",9797);//t= 518
        putProxy("106.12.112.57",8888);//t= 549
        putProxy("39.137.20.84",8080);//t= 1078
        putProxy("39.108.222.178",8118);//t= 2328
        putProxy("45.221.72.58",31793);//t= 4423
        putProxy("120.7.245.124",9000);//t= 2706
        putProxy("39.137.20.75",80);//t= 819
        putProxy("27.155.83.182",8081);//t= 639
        putProxy("36.33.32.158",46817);//t= 558
        putProxy("47.104.179.65",8081);//t= 2898
        putProxy("223.244.252.58",60824);//t= 4224
        putProxy("39.137.20.76",80);//t= 937
        putProxy("202.104.113.35",53281);//t= 3874
        putProxy("202.104.113.35",53281);//t= 788

        putProxy("14.115.105.202",808); // 13, t= 4915
        putProxy("119.29.205.254",808); // 17, t= 56
        putProxy("111.230.211.23",1080); // 18, t= 319
        putProxy("120.84.146.194",9000); // 19, t= 1092
        putProxy("121.40.154.114",8118); // 30, t= 69
        putProxy("118.89.148.211",1080); // 21, t= 1118
        putProxy("222.186.34.241",60531); // 32, t= 107
        putProxy("182.88.91.87",8123); // 15, t= 2004
        putProxy("60.217.64.237",35091); // 22, t= 1154
        putProxy("219.238.186.188",8118); // 36, t= 105
        putProxy("163.125.28.214",8118); // 27, t= 1046
        putProxy("118.89.148.211",1080); // 25, t= 1127
        putProxy("180.118.242.65",61234); // 20, t= 2004
        putProxy("103.254.185.219",31065); // 24, t= 1445
        putProxy("14.115.104.143",808); // 28, t= 2004
        putProxy("113.116.182.219",9000); // 29, t= 2002
        putProxy("121.40.78.138",3128); // 31, t= 2003
        putProxy("163.125.70.204",8888); // 34, t= 2004
        putProxy("110.73.41.145",8123); // 37, t= 2003
        putProxy("47.104.201.136",53281); // 39, t= 2004
        putProxy("123.207.234.226",3128); // 42, t= 1064
        putProxy("223.145.212.41",8118); // 40, t= 2003
        putProxy("118.163.125.36",8080); // 41, t= 2003
        putProxy("119.179.60.117",8118); // 33, t= 3695
        putProxy("220.132.135.79",49997); // 43, t= 2004
        putProxy("222.210.158.157",8118); // 45, t= 2004


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
                        System.out.println(String.format("putProxy(\"%s\",%d); // %d, t= %d",proxy.getHost(),proxy.getPort(),index,time ));
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
