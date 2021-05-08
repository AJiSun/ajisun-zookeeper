package org.ajisun.coding.config;

import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * @Copyright (c) 2021. ajisun. All right reserved.
 * @ProjectName: ajisunzookeeper
 * @PackageName: org.ajisun.coding.config
 * @Date: 2021/2/18
 * @author: ajisun
 * @Email: .....com
 */
public class ZKUtils {

    private static ZooKeeper zk;
    private static String address = "127.0.0.1:2181,127.0.0.2:2181,127.0.0.3:2181,127.0.0.4:2181/";
    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    private static DefaultWatch defaultWatch= new DefaultWatch();

    public static ZooKeeper getZK(String mk) {


        defaultWatch.setCountDownLatch(countDownLatch);
        try {
            zk = new ZooKeeper(address+mk,1000,defaultWatch);
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return zk;
    }


}
