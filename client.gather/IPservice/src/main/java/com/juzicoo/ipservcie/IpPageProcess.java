package com.juzicoo.ipservcie;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.Closeable;
import java.io.IOException;

public class IpPageProcess implements PageProcessor, Closeable {




    @Override
    public void close() throws IOException {

    }

    @Override
    public void process(Page page) {

    }

    @Override
    public Site getSite() {
        return null;
    }
}
