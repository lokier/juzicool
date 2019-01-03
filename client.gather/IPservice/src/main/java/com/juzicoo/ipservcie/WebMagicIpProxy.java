package com.juzicoo.ipservcie;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyProvider;

public class WebMagicIpProxy implements ProxyProvider {


    private IPPool pool ;

    public WebMagicIpProxy(IPPool pool){
        this.pool = pool;
    }

    @Override
    public void returnProxy(Proxy proxy, Page page, Task task) {
        this.pool.release(proxy.getHost(),page.isDoProcess());
    }

    @Override
    public Proxy getProxy(Task task) {
        ProxyIp ip =  this.pool.request();
        if(ip == null) {
            return null;
        }
        return new Proxy(ip.getHost(),ip.getPort());
    }
}
