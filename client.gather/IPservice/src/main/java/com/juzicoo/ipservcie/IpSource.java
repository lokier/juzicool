package com.juzicoo.ipservcie;

import us.codecraft.webmagic.Page;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface IpSource {

    /**
     * 入口URL
     * @return
     */
    HashSet<String> getEnterUrls();


    void process(Page page, DataCommit commit);

    void absort();

    interface DataCommit{

        void submit(String ip,int port);
    }

}
