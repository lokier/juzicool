package com.juzicool.data.main;

import com.juzicool.data.Juzi;
import com.juzicool.data.db.JuziDB;
import com.juzicool.data.main.es.ElasticSearch;

import java.io.File;
import java.util.List;

public class ImportJuziFromSqlite {

    public static void main(String[] args) {
        final String host = "localhost";
        final int port = 9200;
        final String name = "";
        final String password = "";
        final int es_timeout = 30000;

        final String sqliteFilePath = "./juzimi_ablum_output.db";

        JuziDB juziDB = new JuziDB(new File(sqliteFilePath));


        juziDB.prepare();

        int juziTotalSize = juziDB.size();

        System.out.println("juzi total size: " + juziTotalSize);

        ElasticSearch search = new ElasticSearch(host,port,name,password,es_timeout);


        JuziDB.Iterator it =  juziDB.createIterator();
        List<Juzi> batchList = null;
        int count  = 0 ;
        do{
            batchList =  it.next(300);
            if(batchList!= null){
                count += batchList.size();
                System.out.println(String.format("handle: %d/%d",count,juziTotalSize));

            }

        }while (batchList != null);


        juziDB.close();



    }
}
