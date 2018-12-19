package com.juzicool.gather;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class IpProxyProvider implements ProxyProvider {

    private ArrayList<Proxy> list = new ArrayList<>();
    private HashSet<String> ipMap = new HashSet<>();
    private int index = 0;
    public IpProxyProvider(){
       putProxy("163.125.28.156",8118);
       putProxy("163.125.28.154",8118);
       putProxy("182.146.203.217",8118);
       putProxy("123.156.40.140",80);
       putProxy("103.38.178.114",55502);
       putProxy("47.110.73.124",3128);
       putProxy("221.9.152.154",80);
       putProxy("47.99.61.236",9999);
       putProxy("120.83.49.176",9000);
       putProxy("118.163.125.36",8080);
       putProxy("36.7.128.146",44473);
       putProxy("123.244.148.4",30912);
       putProxy("39.108.225.10",8118);
       putProxy("120.84.146.194",9000);
       putProxy("120.234.138.101",53779);
       putProxy("103.254.185.219",31065);
       putProxy("111.198.77.169",47891);
       putProxy("14.115.104.212",808);
       putProxy("113.12.202.50",57761);
       putProxy("182.134.128.233",8118);
       putProxy("120.194.42.157",38185);
       putProxy("112.67.187.155",9797);
       putProxy("183.47.2.201",43174);
       putProxy("14.115.105.202",808);
       putProxy("222.212.88.12",32142);
       putProxy("180.164.24.165",53281);
       putProxy("183.6.120.29",1080);
       putProxy("124.16.90.183",1080);
       putProxy("58.63.112.186",30893);
       putProxy("112.95.18.123",8088);
       putProxy("59.110.136.213",8080);
       putProxy("58.45.223.13",10800);
       putProxy("118.89.148.211",1080);
       putProxy("124.16.112.74",1080);
       putProxy("180.104.63.159",8118);
       putProxy("182.207.232.135",49166);
       putProxy("111.230.211.23",1080);
       putProxy("180.169.186.155",1080);
       putProxy("219.246.90.204",3128);
       putProxy("183.129.207.88",11056);

        putProxy("114.83.101.130",9797);
        putProxy("125.40.238.181",56738);
        putProxy("211.152.33.24",49603);
        putProxy("222.191.243.187",61114);
        putProxy("116.7.176.75",8118);
        putProxy("111.225.11.87",9999);
        putProxy("218.26.227.108",80);
        putProxy("222.248.243.103",8118);
        putProxy("1.196.160.165",9999);
        putProxy("39.137.20.84",8080);
        putProxy("47.100.112.183",8088);
        putProxy("61.145.69.27",48275);
        putProxy("140.143.96.108",1080);
        putProxy("60.216.101.46",32868);
        putProxy("47.99.61.236",9999);
        putProxy("183.15.122.250",808);
        putProxy("58.63.112.186",30893);
        putProxy("219.142.132.146",34666);
        putProxy("222.223.115.30",37936);

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
        for(int i = 0;i < provider.list.size();i++){
            final Proxy proxy = provider.list.get(i);
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    boolean isOk = IpTest.checkProxyIp(proxy.getHost(),proxy.getPort());
                    if(!isOk){
                        System.out.println(String.format("putProxy(\"%s\",%d);",proxy.getHost(),proxy.getPort() ));
                    }
                }
            });
        }

    }
}
