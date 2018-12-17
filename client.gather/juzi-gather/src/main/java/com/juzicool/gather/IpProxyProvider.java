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
