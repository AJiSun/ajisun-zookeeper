package org.ajisun.coding.config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * @Copyright (c) 2021. ajisun. All right reserved.
 * @ProjectName: ajisunzookeeper
 * @PackageName: org.ajisun.coding.config
 * @Date: 2021/2/18
 * @author: ajisun
 * @Email: .....com
 */
public class WatchCallBack implements Watcher,AsyncCallback.StatCallback,AsyncCallback.DataCallback {

    ZooKeeper zk;
    MyConfig config;
    CountDownLatch cc = new CountDownLatch(1);

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public void setConfig(MyConfig config) {
        this.config = config;
    }

    public void await(){

        zk.exists("/AppConf",this::process,this::processResult,"ABC");
        try {
            cc.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent event) {

        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                zk.getData("/AppConf",this,this,"data");
                break;
            case NodeDeleted:
                config.setConf("");
                cc = new CountDownLatch(1);
                break;
            case NodeDataChanged:
                zk.getData("/AppConf",this,this,"data");
                break;
            case NodeChildrenChanged:
                break;
        }

    }

    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {

        if (data!=null){
            config.setConf(new String(data));
            cc.countDown();
        }


    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        if (stat!=null){
            zk.getData("/AppConf",this,this,"data");
        }
    }
}
