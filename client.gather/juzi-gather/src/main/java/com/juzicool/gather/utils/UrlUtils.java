package com.juzicool.gather.utils;

public class UrlUtils {

    /**
     * 去掉url的末尾query参数
     * @param url
     * @return
     */
    public static String getUrlWithoutQuery(String url){
        int inndex = url.indexOf("?");
        if(inndex!=1){
            return url.substring(0,inndex);
        }
        return url;
    }

    public static String getUrlQuery(String url){
        int inndex = url.indexOf("?");
        if(inndex!=1){
            return url.substring(inndex+1,url.length());
        }
        return "";
    }

    public static String getPath(String url){
        int inndex = url.indexOf("/",10);
        if(inndex==-1){
            inndex = url.indexOf("\\",10);
        }
        if(inndex!=1){
            return url.substring(inndex+1,url.length());
        }
        return "";
    }

}
