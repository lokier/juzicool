package com.juzicool.gather;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SpiderStopUI extends JFrame{

    private JButton jb;

    public SpiderStopUI(String title){
        this.setTitle(title);//设置窗体标题
        this.setBounds(350,350,350,300);//设置窗体尺寸
        this.setVisible(true);//显示窗体

        jb = new JButton("停止采集");
        add(jb);

        this.addWindowListener(new WindowAdapter(){//添加窗体关闭事件
            public void windowClosing(WindowEvent ev){
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            }
        });
    }

    public static void main(String[] args){
        doWhileCloase(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public static void doWhileCloase( ActionListener actionListener){

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SpiderStopUI ui = new SpiderStopUI("事件监听测试");
                ui.jb.addActionListener(actionListener);
            }
        });

    }
}
