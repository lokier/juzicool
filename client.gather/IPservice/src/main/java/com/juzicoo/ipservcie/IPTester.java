package com.juzicoo.ipservcie;

import com.juzicool.core.Promise;
import com.juzicool.core.PromiseException;
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
import java.util.concurrent.atomic.AtomicInteger;

public interface IPTester {


    Promise checkProxyIp(String host, int port);



    public static class DefaultIPTester implements IPTester {

         private String[] mUrls;
         private IPservcie iPservcie;



         public DefaultIPTester(IPservcie iPservcie,String[] urls) {
             this.iPservcie = iPservcie;
             mUrls = urls;
         }

         @Override
         public Promise checkProxyIp(String host, int port) {
             try {
                 return check(host, port, 5000);
             }catch (Throwable ex){
                return null;
             }
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


         public Promise check(final String proxyIp, int proxyPort,final long timeoutMillion) {

             if(mUrls == null || mUrls.length == 0){
                 return null;
             }

             Promise builder = new Promise();

             for(String reqUrl : mUrls){
                 final CloseableHttpClient client = createClient();
                 final RequestConfig config =  RequestConfig.custom()
                         .setSocketTimeout(2000)
                         .setConnectTimeout(2000)
                         .setConnectionRequestTimeout(1000)
                         .setProxy(new HttpHost(proxyIp,proxyPort))
                         .build();

                 final HttpGet httpGet = new HttpGet(reqUrl);
                 builder.then(new Promise.RunFunc() {
                     @Override
                     public void run(Promise promise) {
                         httpGet.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
                         httpGet.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
                         httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                         httpGet.setHeader("Accept-Encoding", "gzip, deflate");
                         httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6801.400 QQBrowser/10.3.2928.400");
                         httpGet.setConfig(config);
                         try {

                             final HttpResponse response = client.execute(httpGet);
                             int statuCode = response.getStatusLine().getStatusCode();
                             if (statuCode == 200)
                                 promise.reject(null);
                             else
                                 promise.accept(null);
                         }catch (Exception ex){
                             promise.reject(null);
                         }
                     }
                 },timeoutMillion);

               final Runnable closeRunnable =  new Runnable() {
                     @Override
                     public void run() {
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

                 builder.then(closeRunnable).reject(new Promise.RunFunc() {
                     @Override
                     public void run(Promise promise) {
                         if(promise.getRejectError() instanceof PromiseException){
                             if(IPservcie.LOG.isDebugEnabled()){
                                 IPservcie.LOG.debug(String.format(" =====> close test for timeout(%d): %s",timeoutMillion,proxyIp));
                             }
                         }
                         closeRunnable.run();
                     }
                 });
             }
             return  builder;

         }
     }
}
