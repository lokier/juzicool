package com.juzicoo.ipservcie;

import java.util.Collection;

public interface IPPool {

    /**
     *
     * @param iplist  添加优先使用的ipList
     */
    void addPrioriyIplist(Collection<String> iplist);

    void ready();

    void destroy();

    ProxyIp request();

    void release(String ipHost, boolean userOk);

}
