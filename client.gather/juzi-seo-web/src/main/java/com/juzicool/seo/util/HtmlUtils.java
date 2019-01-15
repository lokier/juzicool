package com.juzicool.seo.util;

public class HtmlUtils {
    public static String toHtml(String text) {
        return toHtml(text,null);
    }

    public static String toHtml(String text,Integer maxLength) {
        if(text == null) {
            return null;
        }

        if(maxLength!=null) {
            if (text.length() > maxLength) {
                text =  text.substring(0,maxLength - 3) +"... ...";
            }
        }


        text = text.replaceAll("\r\n", "<br/>");
        text = text.replaceAll("\r", "<br/>");
        text = text.replaceAll("\n", "<br/>");
        text = text.replaceAll(" ", "&nbsp;");

        return text;
    }
}
