package com.juzicool.seo.util;

import org.apache.commons.lang3.time.DateFormatUtils;

public class SeoUtils {

    public static final long A_MINUTE =  60 * 1000;
    public static final long A_HOUR =  60 * A_MINUTE;
    public static final long A_DAY =  24 * A_HOUR;

    public static String getNextTimeDesc(long nextTime){

        long detaTime = nextTime - System.currentTimeMillis();
        if(detaTime <0){
            return "已过期";
        }
        if(detaTime < A_MINUTE){
            return String.format("%d秒",(detaTime/1000));
        }else  if(detaTime < A_MINUTE) {
            long totalSecond = detaTime / 1000;
            long minitue = totalSecond / 60;
            long second = totalSecond % 60;
            return String.format("%d分钟%d秒",minitue,second);
        }else if(detaTime < A_DAY){
            //long totalSecond = detaTime / 1000;
            long hour = detaTime / A_HOUR;
            long totalSecond = detaTime / 1000;
            long minitue = totalSecond / 60;
            long second = totalSecond % 60;
            return String.format("%d小时%d分钟%d秒",hour,minitue,second);
        }

        return  DateFormatUtils.format(nextTime,"yyyy-MM-dd HH:mm:ss");
    }
}
