package com.juzicool.gather;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyProvider;

import java.util.ArrayList;

public class IpProxyProvider implements ProxyProvider {

    private ArrayList<Proxy> list = new ArrayList<>();
    private int index = 0;
    public IpProxyProvider(){
        list.add(new Proxy("60.250.159.191",56599));
        list.add(new Proxy("163.125.28.154",8118));
        list.add(new Proxy("112.16.172.107",48399));
        list.add(new Proxy("182.146.203.217",8118));
        list.add(new Proxy("39.137.20.88",8080));
        list.add(new Proxy("221.9.152.154",80));
        list.add(new Proxy("47.99.61.236",9999));
        list.add(new Proxy("123.249.28.107",3128));
        list.add(new Proxy("163.125.28.156",8118));
        list.add(new Proxy("47.110.73.124",3128));
        list.add(new Proxy("223.223.187.195",80));
        list.add(new Proxy("103.38.178.114",55502));
        list.add(new Proxy("218.17.21.138",808));

        list.add(new Proxy("116.237.67.127",37422));
        list.add(new Proxy("134.196.244.120",46425));
        list.add(new Proxy("118.163.125.36",8080));

        list.add(new Proxy("120.83.49.176",9000));
        list.add(new Proxy("36.7.128.146",44473));
        list.add(new Proxy("123.244.148.4",30912));
        list.add(new Proxy("123.156.40.140",80));
        list.add(new Proxy("112.102.62.113",8888));
        list.add(new Proxy("47.107.245.94",3128));
        list.add(new Proxy("123.206.56.247",1080));
        list.add(new Proxy("120.234.138.101",53779));
        list.add(new Proxy("103.254.185.219",31065));
        list.add(new Proxy("175.17.154.157",8080));
        list.add(new Proxy("111.198.77.169",47891));
        list.add(new Proxy("39.108.225.10",8118));
        list.add(new Proxy("120.84.146.194",9000));
        list.add(new Proxy("119.28.181.167",3271));
        list.add(new Proxy("39.137.20.75",80));
        list.add(new Proxy("14.115.104.212",808));
        list.add(new Proxy("113.12.202.50",57761));
        list.add(new Proxy("58.240.232.126",45984));
        list.add(new Proxy("182.134.128.233",8118));
        list.add(new Proxy("183.237.206.92",53281));
        list.add(new Proxy("120.194.42.157",38185));
        list.add(new Proxy("36.255.234.243",30350));
        list.add(new Proxy("183.47.2.201",43174));
        list.add(new Proxy("58.58.213.55",8888));
        list.add(new Proxy("14.115.105.202",808));


        list.add(new Proxy("112.67.187.155",9797));
        list.add(new Proxy("210.61.216.66",60209));
        list.add(new Proxy("124.205.143.212",54755));
        list.add(new Proxy("222.128.9.235",33428));
        list.add(new Proxy("222.212.88.12",32142));
        list.add(new Proxy("180.164.24.165",53281));
        list.add(new Proxy("183.6.120.29",1080));
        list.add(new Proxy("59.127.55.215",56732));
        list.add(new Proxy("124.16.90.183",1080));
        list.add(new Proxy("58.63.112.186",30893));
        list.add(new Proxy("180.169.186.155",1080));
        list.add(new Proxy("183.129.207.88",11056));
        list.add(new Proxy("219.246.90.204",3128));
        list.add(new Proxy("112.95.18.123",8088));
        list.add(new Proxy("59.110.136.213",8080));

        list.add(new Proxy("175.148.79.43",1133));
        list.add(new Proxy("123.207.234.226",3128));
        list.add(new Proxy("58.45.223.13",10800));
        list.add(new Proxy("183.172.106.233",1080));
        list.add(new Proxy("111.230.211.23",1080));
        list.add(new Proxy("1.175.133.183",3128));
        list.add(new Proxy("211.141.111.114",61395));
        list.add(new Proxy("220.180.50.14",53281));
        list.add(new Proxy("222.128.9.235",33428));
        list.add(new Proxy("222.221.11.119",3128));
        list.add(new Proxy("118.89.148.211",1080));
        list.add(new Proxy("124.16.112.74",1080));

        list.add(new Proxy("61.183.233.6",54896));
        list.add(new Proxy("182.207.232.135",49166));
        list.add(new Proxy("116.192.175.93",41944));
        list.add(new Proxy("180.104.63.159",8118));
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
}
