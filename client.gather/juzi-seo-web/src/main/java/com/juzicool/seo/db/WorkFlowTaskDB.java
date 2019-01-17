package com.juzicool.seo.db;

import com.juzicool.data.db.SimpleDB;
import com.juzicool.seo.Services;
import com.juzicool.seo.model.WorkFlowLog;

import java.io.File;
import java.io.Serializable;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;

public class WorkFlowTaskDB {

    private static HashMap<Integer,WorkFlowTaskDB> gMap = new HashMap<>();


    public static WorkFlowTaskDB get(Integer taskId){
        WorkFlowTaskDB db = gMap.get(taskId);
        if(db == null) {
            synchronized (WorkFlowTaskDB.class){
                db = gMap.get(taskId);
                if(db == null){
                    db = new WorkFlowTaskDB(getFile(taskId));
                    gMap.put(taskId,db);
                }
            }
        }
        return db;
    }

    private static File getFile(Integer taskId){

      return  new File(Services.app.getAppSubDir("walkTask"),taskId+".db");
    }

    private final SimpleDB db;

    private WorkFlowTaskDB(File file){
        db = new SimpleDB();
        db.openFile(file);

    }


    public void insert(WorkFlowLog log){
        db.List().insert(new Serializable[]{log});
    }

    public List<SimpleDB.ListData<WorkFlowLog>> getLatests(){
        return db.List().getPage(0,20,true);
    }

    /***
     * 删除之间的日志。
     * @param time
     */
    public void deleyBefore(long time){
        db.List().delete(null,new Date(time));
    }

}
