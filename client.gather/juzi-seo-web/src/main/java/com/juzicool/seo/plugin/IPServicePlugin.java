package com.juzicool.seo.plugin;

import com.jfinal.plugin.IPlugin;
import com.juzicoo.ipservcie.IPservcie;
import com.juzicool.seo.Services;

import java.io.File;


public class IPServicePlugin implements IPlugin {


    public static IPservcie me;

    @Override
    public boolean start() {
        File file = new File(AppPlugin.me.getAppDir(),"ip_proxiy.db");
        me = new IPservcie(file,WebwalkPlugin.me.getHandler());
        me.prepare();
        Services.iPservcie = me;
        return true;
    }


    @Override
    public boolean stop() {
        return false;
    }
}
