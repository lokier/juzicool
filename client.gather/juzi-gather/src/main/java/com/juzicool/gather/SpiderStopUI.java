package com.juzicool.gather;

import javax.swing.*;
import java.awt.event.ActionListener;

public class SpiderStopUI {

    public static void main(String[] args){

    }

    public static void doWhileCloase( ActionListener actionListener){
        final JFrame jf = new JFrame("事件监听测试");
        jf.setVisible(true);
        jf.setSize(100, 200);

        JButton jb = new JButton("停止采集");
        jf.add(jb);
        jb.addActionListener(actionListener);
    }
}
