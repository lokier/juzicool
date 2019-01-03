package com.juzicoo.ipservcie;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public interface IPTester {


    boolean checkProxyIp(String host,int port);


    public static class DefaultIPTester implements IPTester {

         private String[] mUrls;
         private IPservcie iPservcie;

         public DefaultIPTester(IPservcie iPservcie,String[] urls) {
             this.iPservcie = iPservcie;
             mUrls = urls;
         }

         @Override
         public boolean checkProxyIp(String host, int port) {
             return check(host,port,5000);
         }


         private static CloseableHttpClient createClient(){
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

         public Boolean check(final String proxyIp, int proxyPort,final int timeoutMillion) {

             if(mUrls == null || mUrls.length == 0){
                 return false;
             }
             for(String reqUrl : mUrls){
                 final CloseableHttpClient client = createClient();
                 final RequestConfig config =  RequestConfig.custom()
                         .setSocketTimeout(2000)
                         .setConnectTimeout(2000)
                         .setConnectionRequestTimeout(1000)
                         .setProxy(new HttpHost(proxyIp,proxyPort))
                         .build();

                 final HttpGet httpGet = new HttpGet(reqUrl);
                 httpGet.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
                 httpGet.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
                 httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                 httpGet.setHeader("Accept-Encoding", "gzip, deflate");
                 httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6801.400 QQBrowser/10.3.2928.400");
                 httpGet.setConfig(config);
                 try {
                     final  HttpResponse response = client.execute(httpGet);

                     Runnable closeRunnable = new Runnable() {
                         @Override
                         public void run() {

                             if(IPservcie.LOG.isDebugEnabled()){
                                 IPservcie.LOG.debug(String.format(" =====> close test for timeout(%d): %s",timeoutMillion,proxyIp));
                             }
                             try{
                                 httpGet.abort();
                             }catch (Exception ex){

                             }
                            try{
                                client.close();
                            }catch (Exception ex){

                            }

                         }
                     };

                     iPservcie.getHandler().postDelayed(closeRunnable,timeoutMillion);
                     int statuCode = response.getStatusLine().getStatusCode();
                     iPservcie.getHandler().removeCallbacks(closeRunnable);

                     if(statuCode == 200)
                         continue;
                     else
                         return false;

                 } catch (Exception e) {
                    // IPservcie.LOG.warn(e.getMessage(),e);
                     return false;
                 } finally {
                     try {
                         if (httpGet != null) {
                             httpGet.abort();
                         }
                     }catch (Exception ex){

                     }

                     try {
                         if (client != null) {
                             client.close();
                         }
                     }catch (Exception ex){

                     }
                 }
             }
             return  true;

         }
     }
}
