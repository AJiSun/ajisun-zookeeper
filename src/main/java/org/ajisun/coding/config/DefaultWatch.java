package org.ajisun.coding.config;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

/**
 * @Copyright (c) 2021. ajisun. All right reserved.
 * @ProjectName: ajisunzookeeper
 * @PackageName: org.ajisun.coding.config
 * @Date: 2021/2/18
 * @author: ajisun
 * @Email: .....com
 */
public class DefaultWatch implements Watcher {

    CountDownLatch countDownLatch;


    public DefaultWatch setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
        return this;
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event.toString());
        switch (event.getState()) {
            case Unknown:
                break;
            case Disconnected:
                break;
            case NoSyncConnected:
                break;
            case SyncConnected:
                countDownLatch.countDown();
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
    }
}
