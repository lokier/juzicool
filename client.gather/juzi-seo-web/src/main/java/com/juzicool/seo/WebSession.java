package com.juzicool.seo;

import com.gargoylesoftware.htmlunit.util.Cookie;

import java.io.Serializable;
import java.util.ArrayList;

public class WebSession implements Serializable {
    private static final long serialVersionUID = 1L;

    public ArrayList<Cookie> cookies = null;
}
