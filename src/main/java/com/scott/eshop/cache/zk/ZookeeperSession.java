package com.scott.eshop.cache.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @ClassName ZookeeperSession
 * @Description
 * @Author 47980
 * @Date 2020/5/2 16:34
 * @Version V_1.0
 **/
public class ZookeeperSession {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    private ZooKeeper zooKeeper;

    /**
     * 建立zk session
     */
    public ZookeeperSession() {
        // 连接zookeeper server，创建会话的时候，时异步去进行的
        // 所以要给一个监听器，判断真正和zk server连接的时候
        try {
            this.zooKeeper = new ZooKeeper(
                    "192.168.52.115:2181,192.168.52.113:2181,192.168.52.107:2181",
                    50000,
                    new ZookeeperWatcher());
            // 给一个状态CONNECTING.连接中
            System.out.println(zooKeeper.getState());

            // CountDownLatch
            // java多线程并发同步的一个工具类
            // 会传递一些数字，比如说1，2，3
            // 然后await()，如果数字不是0，就会卡主，等待
            // 其他线程也可以调用countDown()，减 1
            // 如果数字减到0，那么之前所有的await的线程，都会逃出等待状态，继续往下执行。
            try {
                connectedSemaphore.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Zookeeper session establish");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分布式加锁
     * @param productId
     */
    public void acquireDistributeLock(Long productId) {
        String path = "/product-lock-" + productId;

        try {
            zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("success to acquire lock for product[id=" + productId + "]");

        } catch (Exception e) {
            // 如果node已经存在。即会报错
            // NodeExistsException
            int count = 0;
            while (true) {
                try {
                    Thread.sleep(1000);
                    zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                } catch (Exception ex) {
//                    ex.printStackTrace();
                    count++;
                    System.out.println("the " + count + " times try to acquire lock for product[id= "+productId+"]");
                    continue;
                }
                System.out.println("success to acquire lock for product[id="+productId+"], after: " + count);
                break;
            }
        }
    }

    /**
     * 分布式加锁
     * @param path
     */
    public void acquireDistributeLock(String path) {
        try {
            zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("success to acquire lock for "+path);

        } catch (Exception e) {
            // 如果node已经存在。即会报错
            // NodeExistsException
            int count = 0;
            while (true) {
                try {
                    Thread.sleep(1000);
                    zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                } catch (Exception ex) {
//                    ex.printStackTrace();
                    count++;
                    System.out.println("the " + count + " times try to acquire lock for "+path);
                    continue;
                }
                System.out.println("success to acquire lock for " + path + ", after: " + count);
                break;
            }
        }
    }

    /**
     * 分布式加锁-快速
     * @param path
     */
    public boolean acquireFastFailedDistributeLock(String path) {
        try {
            zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("success to acquire lock for " + path);
            return true;
        } catch (Exception e) {
            System.out.println("fail to acquire lock for " + path);
        }
        return false;
    }

    /**
     * 释放分布式锁
     */
    public void releaseDistributeLock(Long productId) {
        String path = "/product-lock-"+productId;
        try {
            zooKeeper.delete(path, -1);
            System.out.println("release the lock for product[id="+productId+"]");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放分布式锁
     */
    public void releaseDistributeLock(String path) {
        try {
            zooKeeper.delete(path, -1);
            System.out.println("release the lock for " + path);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    private class ZookeeperWatcher implements Watcher {
        @Override
        public void process(WatchedEvent watchedEvent) {
            System.out.println("Receive watched event: " + watchedEvent.getState());
            if (Event.KeeperState.SyncConnected.equals(watchedEvent.getState())) {
                connectedSemaphore.countDown();
            }
        }
    }

    public String getNodeData(String path) {
        try {
            return new  String(zooKeeper.getData(path, false, new Stat()));
        } catch (Exception e) {
        }
        return "";
    }

    public void setNodeData(String path, String data) {
        try {
            zooKeeper.setData(path, data.getBytes(), -1);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createNode(String path) {
        try {
            zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (Exception e) {

        }
    }

    private static class Singleton {

        private static ZookeeperSession intance;

        static {
            intance = new ZookeeperSession();
        }

        public static ZookeeperSession getIntance() {
            return intance;
        }
    }

    /**
     * 获取单例
     * @return
     */
    public static ZookeeperSession getInstance() {
        return Singleton.getIntance();
    }

    /**
     * 初始化单例的便捷方法
     */
    public static void init () {
        Singleton.getIntance();
    }
}
