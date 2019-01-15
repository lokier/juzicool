package com.juzicool.seo;

import com.jfinal.kit.PathKit;
import com.juzicool.data.db.SimpleDB;

import java.io.File;

public class App {

    private static final String APP_DIR_NAME = "data";
    private static final String CONFIG_DB_FILE = "appconfig.db";

    private File appDir = null;
    private SimpleDB configDb = null;

    public File getAppDir(){
        if(appDir == null || !appDir.exists()){
            appDir = new File(PathKit.getWebRootPath(),APP_DIR_NAME);
            appDir.mkdirs();
        }
        return appDir;
    }

    public File getAppSubDir(String subDirName){
        File subDir = new File(getAppDir(),subDirName);
        if(!subDir.isDirectory() || !subDir.exists()){
            subDir.mkdirs();
        }
        return subDir;
    }

    public SimpleDB getConfigDB(){

        if(configDb == null){
            configDb = new SimpleDB();
            configDb.openFile(new File(getAppDir(),CONFIG_DB_FILE));
        }

        return configDb;
    }

    //public File
}
