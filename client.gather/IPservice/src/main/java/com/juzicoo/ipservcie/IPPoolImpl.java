package com.juzicoo.ipservcie;

import com.juzicoo.ipservcie.source.www89ipcn;

import java.io.File;
import java.util.*;

class IPPoolImpl implements IPPool {


    public static void main(String[] args) {

        IPservcie iPservcie = new IPservcie(new File("ipservide.db"));
        System.out.println("www");

        iPservcie.prepare();
        iPservcie.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("wwsdfewrr");

            }
        },2000);
        new Thread(){

            @Override
            public void run(){
                IPservcie iPservcie = new IPservcie(new File("ipservide.db"));

                iPservcie.setIPTester(new IPTester.DefaultIPTester(iPservcie,new String[]{"https://www.juzimi.com/ju/469610"}));

                long start = System.currentTimeMillis();
                System.out.println("start : " + start);

                boolean ok = iPservcie.getIPTester().checkProxyIp("119.101.113.141",9999);
                System.out.println("end : "  +System.currentTimeMillis() +", spend:"+(System.currentTimeMillis() - start) +", ok = " + ok );
            }
        }.start();


  /*      final IPPool pool =  iPservcie.createPool(20,10,0.6f);

        System.out.println("start collecting..");
        iPservcie.doCollect();
        System.out.println("finish collecting..");*/


    }

    private IPservcie iPservcie;
    private int maxPoolSize;
    private int minPoolSize;
    private float minRate;
    private boolean isDestroy = false;
    private IPExList pooList = new IPExList();

    private HashMap<String,ProxyIp> toUpadateMap = new HashMap<>();


    IPPoolImpl(IPservcie pservcie, int maxPoolSize, int minPoolSize, float minRate) {
        this.iPservcie = pservcie;
        this.maxPoolSize = maxPoolSize;
        this.minPoolSize = minPoolSize;
        this.minRate = minRate;
    }


    @Override
    public synchronized void addPrioriyIplist(Collection<String> iplist) {
        if(isDestroy){
            return;
        }

        ArrayList<String> hosts = new ArrayList<>(iplist);

        for(String host: iplist){
            hosts.add(host);
        }
        if(hosts.isEmpty()){
            return;
        }
        List<ProxyIp> proxyIps = iPservcie.getDB().get(hosts);
        if(proxyIps!= null){
            pooList.put(proxyIps);
        }
    }

    @Override
    public synchronized void ready() {
        if(isDestroy){
            return;
        }
        saveCache();
        List<ProxyIp> ipList =  iPservcie.getDB().next(maxPoolSize,minRate);
        if(ipList == null || ipList.size() <= minPoolSize){
            iPservcie.doCollect();
        }
        pooList.put(ipList);
    }

    @Override
    public synchronized void destroy() {

        saveCache();
        isDestroy = true;
    }

    @Override
    public synchronized ProxyIp request() {
        if(isDestroy || pooList == null){
            return null;
        }

        ProxyIp ip =  pooList.poll();

        int unUseSize = pooList.getUnUseSize();

        if(IPservcie.LOG.isDebugEnabled()){
            IPservcie.LOG.debug(String.format("++[Requst IP](unUse:%d,total:%d,minPool:%d,maxPool:%d):" + (ip!= null ? ip.toString():"null")
                    ,unUseSize,pooList.hosts.size(),minPoolSize,maxPoolSize));
        }

        if( unUseSize <= minPoolSize) {
            //再次收集新的IP
            saveCache();
            List<ProxyIp> iplist =  iPservcie.getDB().next( maxPoolSize- unUseSize,minRate);




            if(IPservcie.LOG.isDebugEnabled()){
                IPservcie.LOG.debug(String.format("   ==>read ip from db:(unUse:%d,total:%d,minPool:%d,maxPool:%d), new ip size:%d"
                        ,unUseSize,pooList.hosts.size(),minPoolSize,maxPoolSize,(iplist!= null ? iplist.size():0)));
            }

            if(iplist!= null) {
                pooList.put(iplist);
            }

            int newUnuseSize = pooList.getUnUseSize();
            if(newUnuseSize <= unUseSize){
                //请求收集新的IP。
                if(IPservcie.LOG.isDebugEnabled()) {
                    IPservcie.LOG.debug(String.format("   ==>request collect:(unUse:%d,total:%d,minPool:%d,maxPool:%d)"
                            , unUseSize, pooList.hosts.size(), minPoolSize, maxPoolSize));
                }
                iPservcie.requestCollect();

            }

            if(unUseSize == 0) {
                iplist =  iPservcie.getDB().next( minPoolSize);
                if(iplist!= null) {
                    pooList.put(iplist);
                }
                IPservcie.LOG.warn( "没有代理IP可用，拿劣质IP使用!!!!");
            }



        }


        return ip;
    }

    @Override
    public synchronized void release(String ipHost, boolean userOk) {
        ProxyIp proxy = pooList.remove(ipHost);

        if(proxy!= null){
            ProxyIp.addIfUseOk(proxy,userOk);
            ProxyIp.updateRate(proxy);
            toUpadateMap.put(proxy.getHost(),proxy);
            if(IPservcie.LOG.isDebugEnabled()) {
                IPservcie.LOG.debug(String.format("--[release ip](unUse:%d,total:%d,minPool:%d,maxPool:%d): %s, userOk=" + userOk
                        , pooList.unUseList.size(), pooList.hosts.size(), minPoolSize, maxPoolSize,proxy.toString()));
            }
        }else{
            IPservcie.LOG.warn(String.format("--[release ip] error:(unUse:%d,total:%d,minPool:%d,maxPool:%d) : %s"
                    , pooList.unUseList.size(), pooList.hosts.size(), minPoolSize, maxPoolSize,ipHost));
        }
    }

    private void saveCache(){
        if(iPservcie == null){
            return;
        }
        if(IPservcie.LOG.isDebugEnabled()) {
            IPservcie.LOG.debug(String.format("--[save cache]:toUpadateMap = %d"
                    ,toUpadateMap.size()));
        }
        if(toUpadateMap.size() > 0) {
            iPservcie.getDB().update(toUpadateMap.values());
        }
        toUpadateMap.clear();
    }


    //缺少资源。
    void onLowResoure(){

    }


    void onHighResource(){

    }


    private static class IPEx extends ProxyIp {

        public IPEx(ProxyIp ip){
            super.setExtra(ip.getExtra());
            super.setHost(ip.getHost());
            super.setPort(ip.getPort());
            super.setRate10(ip.getRate10());
        }

        public boolean isUse = false;
        public int useCount = 0;  //使用的次数。
        public long lastUseTime = 0;  //上一次使用的时间;


        @Override
        public String toString() {
            return "IPEx{" +
                    "host='" + getHost() + '\'' +
                    ", port=" + getPort() +
                    ", rate10=" + getRate10() +
                    ",isUse=" + isUse +
                    ", useCount=" + useCount +
                    ", lastUseTime=" + lastUseTime +
                    '}';
        }
    }


    private static class IPExList {

        private HashSet<String> hosts = new HashSet<>();
        private ArrayList<IPEx> unUseList = new ArrayList<IPEx>(1000);
        private HashMap<String,IPEx> usingMap = new HashMap<>();


        public int getUnUseSize(){
            return unUseList.size();
        }


        /***
         * 放入ProxyIp，如果已经包含就放弃放入
         * @param
         */
        public void put(Collection<ProxyIp> ips){
            if(ips == null){
                return;
            }
            for (ProxyIp ip : ips) {
                if(!hosts.contains(ip.getHost())){
                    hosts.add(ip.getHost());
                    IPEx ex = new IPEx(ip);
                    ex.isUse = false;
                    unUseList.add(ex);
                }
            }
        }

        public ProxyIp remove(String ipHost){
            hosts.remove(ipHost);
            return usingMap.remove(ipHost);
        }

        public IPEx poll(){

            if(unUseList.size() > 0 ){
                IPEx ex =  unUseList.remove(0);
                if(ex.isUse){
                    throw new IllegalStateException("should not true");
                }
                ex.isUse = true;
                usingMap.put(ex.getHost(),ex);

                //long pollId = seqId.getAndIncrement();

                IPservcie.LOG.debug("POOL: pool=> "  + ex.toString());
                return ex;
            }
            return null;
        }



    }

}
