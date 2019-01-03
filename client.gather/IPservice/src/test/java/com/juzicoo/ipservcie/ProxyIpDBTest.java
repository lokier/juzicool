package com.juzicoo.ipservcie;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ProxyIpDBTest {

    @Test
    public void testPutAndNext() {
        File sqlFile = new File("proxyIp-test.db");

        ProxyIpDB db = new ProxyIpDB(sqlFile);
        db.prepare();

        db.delateAll();
        Assert.assertTrue( db.size() == 0);

        ArrayList<ProxyIp> ipList = new ArrayList<>();

        ProxyIp ip = new ProxyIp().setHost("w1").setPort(23).setRate10(0.3f);
        ProxyIp.addIfUseOk(ip,true);
        ProxyIp.updateRate(ip);
        ip.getExtra().put("s",100);
        Assert.assertTrue(ip.getRate10() == 1.f);
        ipList.add(ip);

        db.putIfNotExist(ipList);

        {
            List<ProxyIp> retlist = db.next(10);
            Assert.assertTrue(retlist.size() == 1);
            Assert.assertTrue(retlist.get(0).getHost().equals("w1"));
            Assert.assertTrue(retlist.get(0).getPort() == 23);
            Assert.assertTrue(retlist.get(0).getRate10() == 1f);

            Assert.assertTrue(retlist.get(0).getExtra().get("s").equals(new Integer(100)));
        }

        //再next一次，是一样的结果。
        {
            List<ProxyIp> retlist = db.next(10);
            Assert.assertTrue(retlist.size() == 1);
            Assert.assertTrue(retlist.get(0).getHost().equals("w1"));
            Assert.assertTrue(retlist.get(0).getPort() == 23);
            Assert.assertTrue(retlist.get(0).getExtra().get("s").equals(new Integer(100)));
        }
        Assert.assertTrue( db.size() == 1);



        //插入一个相同key值的ip，正常应该是忽略此次插入
        ipList.clear();
        ipList.add(new ProxyIp().setHost("w1").setPort(20).setRate10(0.5f));
        db.putIfNotExist(ipList);
        Assert.assertTrue( db.size() == 1);
        {
            //上一次的值
            List<ProxyIp> retlist = db.next(10);
            Assert.assertTrue(retlist.size() == 1);
            Assert.assertTrue(retlist.get(0).getHost().equals("w1"));
            Assert.assertTrue(retlist.get(0).getPort() == 23);
            Assert.assertTrue(retlist.get(0).getExtra().get("s").equals(new Integer(100)));
        }


        //插入一个相同key值的ip，正常应该是忽略此次插入
        ipList.clear();
        ipList.add(new ProxyIp().setHost("w1").setPort(20).setRate10(0.5f));
        db.update(ipList);
        Assert.assertTrue( db.size() == 1);
        {
            //上一次的值
            List<ProxyIp> retlist = db.next(10);
            Assert.assertTrue(retlist.size() == 1);
            Assert.assertTrue(retlist.get(0).getHost().equals("w1"));
            Assert.assertTrue(retlist.get(0).getPort() == 20);
            Assert.assertTrue(retlist.get(0).getRate10() == 0.5);
            Assert.assertTrue( retlist.get(0).getExtra() == null);
        }


        ipList.add(new ProxyIp().setHost("w2").setPort(20).setRate10(0.5f));
        db.putIfNotExist(ipList);
        Assert.assertTrue( db.size() == 2);
        List<ProxyIp> retlist = db.next(10);
        Assert.assertTrue( retlist.size() == 2);

        db.delateAll();

        ipList.clear();
        ipList.add(new ProxyIp().setHost("w1").setPort(20).setRate10(0.1f));
        ipList.add(new ProxyIp().setHost("w2").setPort(20).setRate10(0.2f));
        ipList.add(new ProxyIp().setHost("w3").setPort(20).setRate10(0.3f));
        ipList.add(new ProxyIp().setHost("w4").setPort(20).setRate10(0.4f));
        ipList.add(new ProxyIp().setHost("w5").setPort(20).setRate10(0.5f));
        ipList.add(new ProxyIp().setHost("w6").setPort(20).setRate10(0.6f));
        ipList.add(new ProxyIp().setHost("w7").setPort(20).setRate10(0.7f));
        ipList.add(new ProxyIp().setHost("w8").setPort(20).setRate10(0.8f));
        ipList.add(new ProxyIp().setHost("w9").setPort(20).setRate10(0.9f));
        ipList.add(new ProxyIp().setHost("w10").setPort(20).setRate10(1f));
        ipList.add(new ProxyIp().setHost("w1").setPort(20).setRate10(0.5f));
        db.putIfNotExist(ipList);



        ProxyIp ip1 =  db.next(1).get(0);
        ProxyIp ip2 =  db.next(1).get(0);
        ProxyIp ip3 =  db.next(1).get(0);
        Assert.assertTrue(!ip1.getHost().equals(ip2.getHost()));
        Assert.assertTrue(!ip2.getHost().equals(ip3.getHost()));



        retlist = db.next(12,0.9f);
        Assert.assertTrue(retlist.size() == 2);
        Assert.assertTrue(retlist.get(0).getHost().equals("w9") || retlist.get(0).getHost().equals("w10"));
        Assert.assertTrue(retlist.get(1).getHost().equals("w9") || retlist.get(1).getHost().equals("w10"));




        Assert.assertTrue(db.next(12).size() == 10);



        db.close();
    }

    @Test
    public void testUpdateRate() {
        File sqlFile = new File("proxyIp-test.db");

        ProxyIpDB db = new ProxyIpDB(sqlFile);
        db.prepare();

        db.delateAll();
        Assert.assertTrue( db.size() == 0);

        ArrayList<ProxyIp> ipList = new ArrayList<>();

        ipList.add(new ProxyIp().setHost("w1").setPort(20));
        ipList.add(new ProxyIp().setHost("w2").setPort(20));
        ipList.add(new ProxyIp().setHost("w3").setPort(23));
        ipList.add(new ProxyIp().setHost("w4").setPort(20));
        db.putIfNotExist(ipList);

        ArrayList<String> w3 = new ArrayList<>();
        w3.add("w3");

        Assert.assertTrue( db.get(w3).get(0).getRate10() == 1.0f);

        ProxyIp proxy = db.get(w3).get(0);
        ProxyIp.addIfUseOk(proxy,false);
        ProxyIp.updateRate(proxy);

        ipList.clear();
        ipList.add(proxy);
        db.update(ipList);

        Assert.assertTrue( db.get(w3).get(0).getRate10() == 0.9f);

        ipList.clear();




        db.delateAll();
        db.close();
    }


     @Test
      public void testGetAndDelate() {
         File sqlFile = new File("proxyIp-test.db");

         ProxyIpDB db = new ProxyIpDB(sqlFile);
         db.prepare();

         db.delateAll();
         Assert.assertTrue( db.size() == 0);

         ArrayList<ProxyIp> ipList = new ArrayList<>();
         ipList.add(new ProxyIp().setHost("w1").setPort(20).setRate10(0.5f));
         ipList.add(new ProxyIp().setHost("w2").setPort(20).setRate10(0.5f));
         ipList.add(new ProxyIp().setHost("w3").setPort(20).setRate10(0.5f));
         ipList.add(new ProxyIp().setHost("w4").setPort(20).setRate10(0.5f));
         ipList.add(new ProxyIp().setHost("w5").setPort(20).setRate10(0.5f));
         ipList.add(new ProxyIp().setHost("w6").setPort(20).setRate10(0.5f));
         ipList.add(new ProxyIp().setHost("w7").setPort(20).setRate10(0.5f));
         ipList.add(new ProxyIp().setHost("w8").setPort(20).setRate10(0.5f));
         ipList.add(new ProxyIp().setHost("w9").setPort(20).setRate10(0.5f));
         ipList.add(new ProxyIp().setHost("w10").setPort(20).setRate10(0.5f));
         ipList.add(new ProxyIp().setHost("w1").setPort(20).setRate10(0.5f));

         db.putIfNotExist(ipList);
         Assert.assertTrue( db.size() == 10);

         db.deletes(new String[]{"w1","w2","w3","w4","w5","w6","w7","w8","w9"});
         Assert.assertTrue( db.size() == 1);
         List<ProxyIp> retlist = db.next(10);
         Assert.assertTrue( retlist.get(0).getHost().equals("w10"));


         db.delateAll();
         Assert.assertTrue( db.next(1) == null);

         Assert.assertTrue( db.size() == 0);

         ipList.clear();
         ipList.add(new ProxyIp().setHost("w1").setPort(20).setRate10(0.5f));
         ipList.add(new ProxyIp().setHost("w2").setPort(20).setRate10(0.5f));
         ipList.add(new ProxyIp().setHost("w3").setPort(20).setRate10(0.5f));
         ipList.add(new ProxyIp().setHost("w4").setPort(20).setRate10(0.5f));
         ipList.add(new ProxyIp().setHost("w5").setPort(20).setRate10(0.5f));
         ipList.add(new ProxyIp().setHost("w6").setPort(20).setRate10(0.5f));
         ipList.add(new ProxyIp().setHost("w7").setPort(20).setRate10(0.5f));
         ipList.add(new ProxyIp().setHost("w8").setPort(20).setRate10(0.5f));
         ipList.add(new ProxyIp().setHost("w9").setPort(20).setRate10(0.5f));
         ipList.add(new ProxyIp().setHost("w10").setPort(20).setRate10(0.5f));
         ipList.add(new ProxyIp().setHost("w1").setPort(20).setRate10(0.5f));
         db.putIfNotExist(ipList);

         ArrayList<String> host = new ArrayList();
         host.add("w3");
         host.add("w4");
         host.add("w9");

         retlist = db.get(host);
         Assert.assertTrue( retlist.get(0).getHost().equals("w3"));
         Assert.assertTrue( retlist.get(1).getHost().equals("w4"));
         Assert.assertTrue( retlist.get(2).getHost().equals("w9"));
         //Assert.assertTrue( retlist.get(3).getHost().equals("w4"));

         db.delateAll();
         db.close();
      }
}
