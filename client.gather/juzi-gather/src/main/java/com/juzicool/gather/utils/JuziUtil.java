package com.juzicool.gather.utils;

import org.apache.commons.lang3.StringUtils;

public class JuziUtil {

    public static String filterBookmark(String text ) {
        if(StringUtils.isEmpty(text)) {
            return text;
        }
        //	text = text.replace("《", "");
        //text = text.replace("》", "");
        return text.replaceAll("[《》「」—\\-]", "");
    }

}
