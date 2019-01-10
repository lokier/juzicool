package com.juzicool.seo;

import com.jfinal.core.Controller;

public class HelloContorller extends Controller {

    public void index() {
        renderText("Hello JFinal World.");
    }
}
