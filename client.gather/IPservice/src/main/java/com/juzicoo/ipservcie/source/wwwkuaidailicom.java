package com.juzicoo.ipservcie.source;

import com.juzicoo.ipservcie.IpSource;
import com.juzicoo.ipservcie.util.SelectableUtls;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.HashSet;
import java.util.List;

public class wwwkuaidailicom implements IpSource {

    private HashSet set = new HashSet();

    {
        HashSet<String> ret = new HashSet<>();
        int pageSize = 1;
        for(int i = 1; i<=pageSize;i++){
            ret.add("https://www.kuaidaili.com/ops/proxylist/"+i+"/");
        }
        set = ret;
    }

    @Override
    public HashSet<String> getEnterUrls() {
        return set;
    }

    @Override
    public void process(Page page, DataCommit commit) {

       Html html = page.getHtml();

        // <div id="freelist";
        List<Selectable> rets =  html.xpath("div[@id='freelist']").nodes();

        if(rets.size() > 0){
            List<Selectable> trNodes= rets.get(0).xpath("tbody/tr").nodes();
            for(Selectable trNode: trNodes){
                String ip = SelectableUtls.toSimpleText(trNode.xpath("td[1]")).trim();
                String port =SelectableUtls.toSimpleText(trNode.xpath("td[2]")).trim();
                commit.submit(ip, Integer.parseInt(port));
            }
        }else{
            LOG.warn("www.kuaidaili.com 抓取IPc错误" );
        }

        // <tbody class="center">
    }

    @Override
    public void absort() {

    }
}
