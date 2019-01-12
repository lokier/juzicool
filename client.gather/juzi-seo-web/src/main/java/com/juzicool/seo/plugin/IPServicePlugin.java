package com.juzicool.seo.plugin;

import com.jfinal.plugin.IPlugin;
import com.juzicoo.ipservcie.IPTester;
import com.juzicoo.ipservcie.IPservcie;
import com.juzicoo.ipservcie.source.www89ipcn;
import com.juzicool.seo.Services;

import java.io.File;


public class IPServicePlugin implements IPlugin {

    public static void main(String[] args){
        File file = new File("C:\\Users\\rao\\Desktop\\dev-workspace\\juzicool\\client.gather\\juzi-seo-web\\src\\main\\webapp\\data\\ip_proxiy.db");
        IPservcie me = new IPservcie(file);
        me.prepare();

        init(me);

        me.requestCollect();
        me.waitWhileCollectFinished();
        while(true){

            System.out.println(String.format("线程:%d,run promise:%d,pendding promise:%d"
                    ,me.getPromiseExecutor().getCurrentThreadSize()
                    ,me.getPromiseExecutor().getRunningPromise().length
                    ,me.getPromiseExecutor().getPenddingPromiseSize()
            ));

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }

    public static IPservcie me;

    @Override
    public boolean start() {
        File file = new File(AppPlugin.me.getAppDir(),"ip_proxiy.db");
        me = new IPservcie(file);
        me.prepare(Services.walkService.getPromiseExecutor());
        Services.iPservcie = me;
        init(me);
        return true;
    }

    private static void init(IPservcie iPservcie){
        iPservcie.setIPTester(new IPTester.DefaultIPTester(iPservcie,new String[]{"https://www.juzimi.com/ju/469610"}));
        iPservcie.addIpSource(new www89ipcn());
    }


    @Override
    public boolean stop() {
        return false;
    }
}
