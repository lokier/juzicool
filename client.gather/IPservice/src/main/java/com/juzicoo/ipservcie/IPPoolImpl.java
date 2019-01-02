package com.juzicoo.ipservcie;

import java.util.*;

class IPPoolImpl implements IPPool {

    private IPservcie iPservcie;
    private int maxPoolSize;
    private int minPoolSize;
    private float minRate;
    private IPExList pooList = new IPExList();



    IPPoolImpl(IPservcie pservcie, int maxPoolSize, int minPoolSize, float minRate) {
        this.iPservcie = pservcie;
        this.maxPoolSize = maxPoolSize;
        this.minPoolSize = minPoolSize;
        this.minRate = minRate;
    }


    @Override
    public synchronized void addPrioriyIplist(Collection<String> iplist) {

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

        List<ProxyIp> ipList =  iPservcie.getDB().next(maxPoolSize,minRate);
        if(ipList == null || ipList.size() ==0){
            iPservcie.doCollect();
        }
        pooList.put(ipList);
    }

    @Override
    public synchronized void destroy() {
        pooList = null;
    }

    @Override
    public synchronized ProxyIp request() {
        return pooList.poll();
    }

    @Override
    public synchronized void release(ProxyIp ip, boolean userOk) {
        pooList.remove(ip);

        //TODO 更新数据
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
                    "isUse=" + isUse +
                    ", useCount=" + useCount +
                    ", lastUseTime=" + lastUseTime +
                    '}';
        }
    }


    private static class IPExList {

        private HashSet<String> hosts = new HashSet<>();
        private ArrayList<IPEx> unUseList = new ArrayList<IPEx>(1000);




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

        public void remove(ProxyIp ip){
            hosts.remove(ip.getHost());

        }

        public IPEx poll(){

            if(unUseList.size() > 0 ){
                IPEx ex =  unUseList.remove(0);
                if(ex.isUse){
                    throw new IllegalStateException("should not true");
                }
                ex.isUse = true;


                //long pollId = seqId.getAndIncrement();

                IPservcie.LOG.debug("POOL: pool=> "  + ex.toString());
                return ex;
            }
            return null;
        }



    }

}
