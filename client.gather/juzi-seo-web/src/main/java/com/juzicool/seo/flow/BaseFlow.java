package com.juzicool.seo.flow;


import com.juzicool.webwalker.WalkFlow;
import java.util.Random;

public abstract class BaseFlow extends WalkFlow {


    /**
     * 内链
     */
    public void addInterLinkCase(){
        Random random = new Random(System.currentTimeMillis());
        super.addCase(new InterRandomCase.SearchCase(),random.nextInt(3000));
        super.addCase(new InterRandomCase.DumpCase(),random.nextInt(60 * 2 * 1000));

    }

}
