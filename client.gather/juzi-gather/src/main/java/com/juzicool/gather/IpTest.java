package com.juzicool.gather;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class IpTest {

    private static HttpClient createClient(){
        HttpRequestRetryHandler handler = new HttpRequestRetryHandler(){

            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                return false;
            }
        };
        CloseableHttpClient httpClient = HttpClientBuilder.create().setRetryHandler(handler).build();
        //httpClient.getParams().
        return httpClient;
    }

    public static Boolean checkProxyIp(String proxyIp, int proxyPort) {
        String reqUrl="https://www.juzimi.com/ju/469610";
        HttpClient client = createClient();
        RequestConfig config =  RequestConfig.custom()
                .setSocketTimeout(2000)
                .setConnectTimeout(2000)
                .setConnectionRequestTimeout(1000)
                .setProxy(new HttpHost(proxyIp,proxyPort))
                .build();



        HttpGet httpGet = new HttpGet(reqUrl);
        httpGet.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
        httpGet.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6801.400 QQBrowser/10.3.2928.400");
        httpGet.setConfig(config);
        try {

            HttpResponse response = client.execute(httpGet);
            int statuCode = response.getStatusLine().getStatusCode();

            if(statuCode == 200)
                return true;
            else
                return false;

        } catch (IOException e) {
            // TODO Auto-generated catch block
           // e.printStackTrace();
        } finally {

            if(httpGet != null){
                httpGet.abort();
            }

        }
        return false;
    }

    public static void main(String[] args) {

       // String url = "http://www.baidu.com";
        String proxyIp="60.190.66.131";
        int proxyPort=31056;

        long time = System.currentTimeMillis();
        boolean isOk = checkProxyIp(proxyIp,proxyPort);
        System.out.println("proxy ok :" + isOk +", time: " + (System.currentTimeMillis() - time));

    }


}
