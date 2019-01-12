package com.juzicoo.ipservcie.source;

import com.juzicoo.ipservcie.IPservcie;
import com.juzicoo.ipservcie.IpSource;
import com.juzicoo.ipservcie.ProxyIp;
import com.juzicoo.ipservcie.util.SelectableUtls;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class www89ipcn implements IpSource {

    private HashSet set = new HashSet();

    public www89ipcn(){
        HashSet<String> ret = new HashSet<>();
        int pageSize = 1;
        for(int i = 1; i<=pageSize;i++){
            ret.add("http://www.89ip.cn/index_"+i+".html");
        }
        set = ret;
    }

    @Override
    public HashSet<String> getEnterUrls() {

        return set;
    }

    @Override
    public void process( Page page,DataCommit commit) {

        Html html =  page.getHtml();

        //ArrayList<ProxyIp> list = new ArrayList<>();

        List<Selectable> trNodes= html.xpath("tbody/tr").nodes();
        for(Selectable trNode: trNodes){
            String ip = SelectableUtls.toSimpleText(trNode.xpath("td[1]")).trim();
            String port =SelectableUtls.toSimpleText(trNode.xpath("td[2]")).trim();
            //long startTime =System.currentTimeMillis();

           // System.out.println("testing START, ip : " + ip);

            commit.submit(ip, Integer.parseInt(port));
          /*  if(iPservcie.getIPTester().checkProxyIp(ip,Integer.parseInt(port))){
                long end = System.currentTimeMillis() - startTime;
                if(end < 5000){
                   // ProxyIp proxy = new ProxyIp(ip,Integer.parseInt(port));

                }
            }*/
          //  System.out.println("testing END, ip : " + ip);

        }

        //return list;
    }

    @Override
    public void absort() {

    }
}
