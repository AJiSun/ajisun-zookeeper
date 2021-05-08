package org.ajisun.coding.lock;

import org.ajisun.coding.config.ZKUtils;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @Copyright (c) 2021. ajisun. All right reserved.
 * @ProjectName: ajisunzookeeper
 * @PackageName: org.ajisun.coding.lock
 * @Date: 2021/2/20
 * @author: ajisun
 * @Email: .....com
 */
public class TestLock {


    ZooKeeper zk;

    @Before
    public void conn(){
        zk = ZKUtils.getZK("testLock");
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
    public void lock(){


        for (int i = 0; i <10 ; i++) {

            new Thread() {
                @Override
                public void run() {

                    WatchCallBack watchCallBack = new WatchCallBack();
                    watchCallBack.setZk(zk);
                    String threadName = Thread.currentThread().getName();
                    watchCallBack.setThreadName(threadName);
                    // 抢锁

                    watchCallBack.tryLock();

                    //干活
                    System.out.println(threadName+" working......");

                    //释放锁
                    watchCallBack.unLock();

                }
            }.start();
        }
    }



}
