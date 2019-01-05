package com.juzicool.webwalker;

import com.gargoylesoftware.htmlunit.WebClient;

public class WalkClient {

    public static WalkClient build() {
        return new WalkClient(null);
    }

    public static WalkClient build(ClientSession session){
        return new WalkClient(session);
    }

    private ClientSession session = null;

    private WebClient mWebClient = null;

    private WalkClient(ClientSession session){
        if(session == null){
            session  = new ClientSession();
        }
    }

    private WebClient createWebClient(){
        return new WebClient();
    }

    public WebClient getWebClient(){
        if(mWebClient == null){
            mWebClient = createWebClient();
        }
        return mWebClient;
    }

    public void despose(){

    }


    //public static void setIP
}
