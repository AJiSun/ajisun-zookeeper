package org.ajisun.coding;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 *
 */
public class App 
{


    public static void main(String[] args ) throws IOException, InterruptedException, KeeperException {
        System.out.println( "Hello World!" );



        // zk是有session概念的，没有连接池概念
        /**
         * watch : 观察，回调
         * watch的注册制只发生在读类型调用，get/exists
         * 第一类：new zookeeper 传入的watch 是session级别的，跟path，node没有关系
         * 使用CountDownLatch监控连接成功回掉
         */

        CountDownLatch downLatch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181,127.0.0.2:2181,127.0.0.3:2181,127.0.0.4:2181",3000,(WatchedEvent event)->{
            Watcher.Event.KeeperState state = event.getState();
            Watcher.Event.EventType type = event.getType();
            String path = event.getPath();
            System.out.println("new zk watch:"+event.toString());

            switch (state) {
                case Unknown:
                    break;
                case Disconnected:
                    break;
                case NoSyncConnected:
                    break;
                case SyncConnected:
                    downLatch.countDown();
                    System.out.println("连接成功");
                    break;
                case AuthFailed:
                    break;
                case ConnectedReadOnly:
                    break;
                case SaslAuthenticated:
                    break;
                case Expired:
                    break;
            }

            switch (type) {
                case None:
                    break;
                case NodeCreated:
                    break;
                case NodeDeleted:
                    break;
                case NodeDataChanged:
                    break;
                case NodeChildrenChanged:
                    break;
            }

        });

        downLatch.await();
        ZooKeeper.States states = zooKeeper.getState();

        switch (states) {
            case CONNECTING:
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
        }

        // 创建
        String pathName = zooKeeper.create("/ooxx", "oldData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        // 取数
        Stat stat = new Stat();
        byte[] getData = zooKeeper.getData("/ooxx", new Watcher() {
            @Override
            public void process(WatchedEvent event) {

                System.out.println(event.toString());
                try {
                    // true: 默认watch被重新注册，new zookeeper()的那个watch
                    zooKeeper.getData("/ooxx",true,stat);

//                    zooKeeper.getData("/ooxx",this,stat);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        },stat);

        //设置数据,触发上面的取数的回调
        Stat stat1 = zooKeeper.setData("/ooxx", "newData".getBytes(), 0);
        Stat stat2 = zooKeeper.setData("/ooxx", "newData01".getBytes(), stat1.getVersion());


        // 异步获取数据
        zooKeeper.getData("/ooxx", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                System.out.println(new String(data));
            }
        },"abc");


    }


}
