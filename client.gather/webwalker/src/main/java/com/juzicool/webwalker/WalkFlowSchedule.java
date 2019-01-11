package com.juzicool.webwalker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class WalkFlowSchedule {

    public static Logger LOG = LoggerFactory.getLogger(WalkFlowSchedule.class);

    final private StartPoint startPoint;

    final private WalkFlowTask walkFlowTask;
    final private  int scheduleId;

    public WalkFlowSchedule(int id,StartPoint point,WalkFlowTask task){
        startPoint = point;
        walkFlowTask = task;
        scheduleId = id;
    }

    public long nextRunTime(){
        return startPoint.nextStartTime();
    }

    public Date nextRunDate(){
        return new Date(startPoint.nextStartTime());
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public StartPoint getStartPoint() {
        return startPoint;
    }

    public WalkFlowTask getWalkFlowTask() {
        return walkFlowTask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalkFlowSchedule that = (WalkFlowSchedule) o;
        return scheduleId == that.scheduleId;
    }

    @Override
    public String toString() {
        return "WalkFlowSchedule{" +
                "scheduleId=" + scheduleId +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheduleId);
    }

}
