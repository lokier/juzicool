package com.juzicool.seo.plugin;

import com.jfinal.plugin.IPlugin;
import com.juzicool.seo.App;
import com.juzicool.seo.Services;
import com.juzicool.seo.flow.ZhifuFlow1;
import com.juzicool.webwalker.DefaultWalkFlowTask;
import com.juzicool.webwalker.StartPoint;
import com.juzicool.webwalker.WalkFlowSchedule;
import com.juzicool.webwalker.WalkService;


public class AppPlugin implements IPlugin {


    public static App me;

    @Override
    public boolean start() {
        me = new App();
        Services.app = me;
        return true;
    }


    @Override
    public boolean stop() {
        return false;
    }
}
