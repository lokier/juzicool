package com.juzicool.search.util;

public class HtmlUtils {
    public static String toHtml(String text) {
        if(text == null) {
            return null;
        }

        text = text.replaceAll("\r\n", "<br/>");
        text = text.replaceAll("\r", "<br/>");
        text = text.replaceAll("\n", "<br/>");

        return text;
    }
}
