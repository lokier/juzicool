package com.juzicool.webwalker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StartPoint {

    public static void main(String[] args) {

        StartPoint startPoint = StartPoint.Bulider.everyDay(11,20,20);

        long nextTime = startPoint.nextStartTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        System.out.println("day: " + sdf.format(new Date(nextTime)));

        Calendar c = Calendar.getInstance();

      //  c.set(Calendar.DAY_OF_MONTH, 5);
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 0);

         startPoint = StartPoint.Bulider.bySeconds(c.getTimeInMillis(),(int)(6*A_HOUR));

        System.out.println("day: " + sdf.format(new Date(startPoint.nextStartTime())));


    }
    private static final  long A_DAY = 24 * 60 * 60 *1000;
    private static final  long A_HOUR =  60 * 60 *1000;
    private static final  long A_MINUTE =  60 *1000;
    private static final  long A_SECOND=  1000;

    private static final int MODE_EVERY_DAY = 0; // 每天重复
    private static final int MODE_BY_SECONDS = 1; // 隔几s开始

    private long repeatMode = MODE_EVERY_DAY;

    private long startTime = 0L;
    private int hour = 0; // 几时开始
    private int minute = 0; //开始分钟
    private int second = 0; // 开始秒数

    public static class Bulider {

        /***
         * 每天指定时间段开始。
         * @param hour
         * @param miniute
         * @param second
         * @return
         */
        public static StartPoint everyDay(int hour,int miniute,int second){
            StartPoint startPoint = new StartPoint();
            startPoint.repeatMode = MODE_EVERY_DAY;
            startPoint.hour = hour;
            startPoint.minute = miniute;
            startPoint.second = second;
            return startPoint;
        }

        /**
         * 小时间隔。
         * @param startTime  开始计算时间
         * @param seconds   隔多少s
         * @return
         */
        public static StartPoint bySeconds(long startTime, int seconds){

            if(seconds <=0){
                throw new IllegalArgumentException("seconds must > 0");
            }

            StartPoint startPoint = new StartPoint();
            startPoint.repeatMode = MODE_BY_SECONDS;

            startPoint.startTime = startTime;
            startPoint.second = seconds;

            return startPoint;
        }
    }

    private StartPoint(){

    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public long getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(long repeatMode) {
        this.repeatMode = repeatMode;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public long nextStartTime(){


        if(this.repeatMode == MODE_EVERY_DAY) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR, this.hour);
            c.set(Calendar.MINUTE, this.minute);
            c.set(Calendar.SECOND, this.second);

           long nextTime =  c.getTimeInMillis();
           if(nextTime <= System.currentTimeMillis()){
               return nextTime + A_DAY;
           }

          return nextTime;


        }else if(this.repeatMode == MODE_BY_SECONDS){

            //Calendar c = Calendar.getInstance();
           // c.setTimeInMillis(startTime);
           // int tareget_minute =  c.get(Calendar.MINUTE);
            //int target_second = c.get(Calendar.SECOND);
            //int target_hour =  c.get(Calendar.HOUR);

            final long currentTime = System.currentTimeMillis();

            long runTime = currentTime;

            if(runTime > startTime) {

                //减去间隔时间
                long detal = (runTime - startTime) %  second;
                runTime = runTime - detal;
            }else{
                runTime  = startTime;
            }

            if(runTime <=currentTime) {
                runTime = runTime + second;
            }


            if(runTime < currentTime){
                throw  new RuntimeException("程序有错误");
            }


           return runTime;


        }

       throw  new UnsupportedOperationException();
    }
}
