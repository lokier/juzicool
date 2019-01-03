package com.juzicoo.ipservcie;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ProxyIp {

    private  String host;
    private  int port;

    private float rate10 = 1f;  //近10次使用的成功率。
    private HashMap<String,Serializable> extra;

    public ProxyIp(){

    }

    public ProxyIp(String host,int port){
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public ProxyIp setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public ProxyIp setPort(int port) {
        this.port = port;
        return this;
    }

    public float getRate10() {
        return rate10;
    }

    public ProxyIp setRate10(float rate10) {
        this.rate10 = rate10;
        return this;
    }

    public HashMap<String, Serializable> getExtra() {
        return extra;
    }

    public ProxyIp setExtra(HashMap<String, Serializable> extra) {
        this.extra = extra;
        return this;
    }


    private static final String KEY_USE_OK_LIST = "USE_OK_LIST";
    private static final String KEY_USE_OK_LIST_INDEX = "USE_OK_LIST_INDEX";

    /***
     * 设置代理ip是否正常使用。
     * @param ip
     * @param userOk
     * @return
     */
    public static  void addIfUseOk(ProxyIp ip, boolean userOk){

       HashMap<String,Serializable> extra = ip.getExtra();
       if(extra == null){
           ip.extra = new HashMap<>();
           extra = ip.extra;
       }
       Serializable okListSe = extra.get(KEY_USE_OK_LIST);
        Boolean[] okList = null;
        int okListIndex = 0;
       if(okListSe instanceof Boolean[]){
           okList = (Boolean[])okListSe;
           if(okList.length !=10){
               okList = null;
           }else{
              Serializable serIndex =  extra.get(KEY_USE_OK_LIST_INDEX);
              if(serIndex instanceof Integer){
                  okListIndex = (Integer)serIndex;
              }
           }
       }
       if(okList == null) {
           okList = new Boolean[10];
       }
        okListIndex = okListIndex % 10;
       okList[okListIndex++] = userOk;
        extra.put(KEY_USE_OK_LIST_INDEX,okListIndex);
        extra.put(KEY_USE_OK_LIST,okList);

        return;
    }

    public static  void updateRate(ProxyIp ip){
        HashMap<String,Serializable> extra = ip.getExtra();

        if(extra == null){
            return;
        }

        Serializable okListSe = extra.get(KEY_USE_OK_LIST);
        Boolean[] okList = null;
        if(okListSe instanceof Boolean[]){
            okList = (Boolean[])okListSe;

        }

        if(okList != null) {
            float totalCount = okList.length;
            float okCount = 0;
            for(Boolean ok : okList){
                if(ok== null || ok){
                    okCount++;
                }
            }
            float rate = okCount / totalCount;
            ip.setRate10(rate);
        }
    }

    @Override
    public String toString() {
        return "ProxyIp{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", rate10=" + rate10 +
                ", extra=" + extra +
                '}';
    }
}
