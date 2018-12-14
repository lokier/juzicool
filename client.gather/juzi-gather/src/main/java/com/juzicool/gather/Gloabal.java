package com.juzicool.gather;

import org.apache.log4j.PropertyConfigurator;

public class Gloabal {

    public static void beforeMain(){
    	try {
    		//在我的mac机器上运行
    		PropertyConfigurator.configure("/Users/raodongming/Desktop/my/gather/WebMagic-demo/src/log4j.properties");
    	}catch (Exception e) {
			
		}
    }
}
