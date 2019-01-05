package com.juzicool.webwalker;

import java.io.Serializable;

/***
 *
 */
public class ClientSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private String proxyIp;  //代理IP
    private int port; //端口

    private String coookes; // cookes
    private String headers; //
}
