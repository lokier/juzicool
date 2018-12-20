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

        putProxy("139.199.176.215",3128);//t= 927
        putProxy("111.8.150.52",54966);//t= 1747
        putProxy("106.14.162.110",8080);//t= 2029
        putProxy("140.207.50.246",34409);//t= 416
        putProxy("114.34.168.157",46160);//t= 3728
        putProxy("49.81.125.133",8118);//t= 387
        putProxy("112.98.126.100",33421);//t= 3257
        putProxy("120.194.42.157",38185);//t= 855
        putProxy("103.40.54.67",23500);//t= 4075
        putProxy("61.135.155.82",443);//t= 802
        putProxy("120.55.57.84",3128);//t= 228
        putProxy("118.89.244.146",1080);//t= 1830

        putProxy("27.155.83.182",8081); // 12, t= 5854
        putProxy("47.104.179.65",8081); // 14, t= 6144
        putProxy("118.81.69.249",9797); // 5, t= 6369
        putProxy("119.29.205.254",808); // 20, t= 46
        putProxy("118.163.165.250",3128); // 3, t= 6975
        putProxy("121.40.154.114",8118); // 23, t= 294
        putProxy("14.115.105.202",808); // 19, t= 1048
        putProxy("125.40.238.181",56738); // 1, t= 7621
        putProxy("39.108.222.178",8118); // 8, t= 7624
        putProxy("114.55.236.62",3128); // 0, t= 7627
        putProxy("111.43.70.58",51547); // 2, t= 7633
        putProxy("118.89.148.211",1080); // 24, t= 1109
        putProxy("222.186.34.241",60531); // 25, t= 1140
        putProxy("202.104.113.35",53281); // 18, t= 2004
        putProxy("111.230.211.23",1080); // 21, t= 1988
        putProxy("120.84.146.194",9000); // 22, t= 2005
        putProxy("163.125.28.214",8118); // 29, t= 1035
        putProxy("118.89.148.211",1080); // 30, t= 1134
        putProxy("60.217.64.237",35091); // 27, t= 1199
        putProxy("202.104.113.35",53281); // 17, t= 3071
        putProxy("182.88.91.87",8123); // 26, t= 2007
        putProxy("103.254.185.219",31065); // 32, t= 1661
        putProxy("180.118.242.65",61234); // 31, t= 2003
        putProxy("14.115.104.143",808); // 33, t= 2005
        putProxy("123.207.234.226",3128); // 39, t= 1063
        putProxy("121.40.78.138",3128); // 35, t= 2006
        putProxy("163.125.70.204",8888); // 36, t= 2005
        putProxy("110.73.41.145",8123); // 37, t= 2013
        putProxy("47.104.201.136",53281); // 38, t= 2006
        putProxy("223.145.212.41",8118); // 40, t= 2004
        putProxy("118.163.125.36",8080); // 41, t= 2004
        putProxy("119.179.60.117",8118); // 42, t= 2008
        putProxy("220.132.135.79",49997); // 43, t= 2003
        putProxy("222.210.158.157",8118); // 44, t= 2010


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
