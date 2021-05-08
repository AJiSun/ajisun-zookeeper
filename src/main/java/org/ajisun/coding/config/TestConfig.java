package org.ajisun.coding.config;

import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @Copyright (c) 2021. ajisun. All right reserved.
 * @ProjectName: ajisunzookeeper
 * @PackageName: org.ajisun.coding.config
 * @Date: 2021/2/18
 * @author: ajisun
 * @Email: .....com
 */
public class TestConfig {

    ZooKeeper zk ;
    MyConfig config;

    @Before
    public void conn(){
     zk = ZKUtils.getZK("testConfig");
    }

    @After
    public void close(){
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getConf(){


        WatchCallBack watchCallBack = new WatchCallBack();
        watchCallBack.setZk(zk);
        MyConfig myConfig = new MyConfig();
        watchCallBack.setConfig(myConfig);

        watchCallBack.await();

        while (true){
            if (myConfig.getConf().equals("")){
                System.out.println("没有数据，停止");
                watchCallBack.await();
            }else {
                System.out.println(myConfig.getConf());
            }
        }




    }

}
