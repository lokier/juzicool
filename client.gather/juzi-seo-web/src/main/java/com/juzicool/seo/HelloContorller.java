package com.juzicool.seo;

import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;

public class HelloContorller extends Controller {

    public void index() {
        PathKit.getWebRootPath();
        renderText("WebRoot path : "  + PathKit.getWebRootPath());
    }
}
