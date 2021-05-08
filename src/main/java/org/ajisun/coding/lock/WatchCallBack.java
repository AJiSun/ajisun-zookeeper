package org.ajisun.coding.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Copyright (c) 2021. ajisun. All right reserved.
 * @ProjectName: ajisunzookeeper
 * @PackageName: org.ajisun.coding.lock
 * @Date: 2021/2/20
 * @author: ajisun
 * @Email: .....com
 */
public class WatchCallBack implements Watcher, AsyncCallback.StringCallback, AsyncCallback.Children2Callback, AsyncCallback.StatCallback {


    ZooKeeper zk;
    String threadName;
    CountDownLatch cc;
    String pathName;

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public void tryLock() {

        try {
            //   创建临时有序的锁
            zk.create("/lock", threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this::processResult, "abc");
            cc.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void unLock() {

        try {
            zk.delete(pathName, -1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void process(WatchedEvent event) {

        // 如果第一个锁释放了，只有第二个收到回调事件
        // 如果是其他的挂了，对应的后一个也能收到通知
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                zk.getChildren("/", false, this, "bcd");
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                break;
        }

    }


    // create call back
    @Override
    public void processResult(int rc, String path, Object ctx, String name) {

        if (name != null) {
            System.out.println(threadName + "create node:" + name);
            pathName = name;
            // 获取所有创建的目录，即参与锁争夺的线程
            zk.getChildren("/", false, this, "bcd");
        }

    }

    /**
     * getChildren call back
     * pathName= /lock00000000003
     * children=[lock0000000002,lock0000000008,lock0000000005,lock0000000003]
     */

    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {

        Collections.sort(children);
        int i = children.indexOf(pathName.substring(1));

        // 判断是不是第一个
        if (i == 0) {
            System.out.println(threadName + " first");
            cc.countDown();
        } else {
            // 监控前一个是否存在
            zk.exists("/" + children.get(i - 1), this, this, "xyz");
        }


    }


    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {

    }
}
