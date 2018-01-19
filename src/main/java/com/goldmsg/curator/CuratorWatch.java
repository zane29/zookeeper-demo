package com.goldmsg.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import static com.goldmsg.curator.listener.EventListener.nodeCacheListener;
import static com.goldmsg.curator.listener.EventListener.pathChildrenCacheListener;
import static com.goldmsg.curator.listener.EventListener.treeCacheListener;

/**
 * 使用curator监听zookeeper节点
 *
 * @author qindongliang
 **/
public class CuratorWatch {

    public static CuratorFramework zkclient = null;

    public static final String PATH = "/root/apple";

    public static final String ROOT_PATH = "/root";

    public static final String ZK_HOST = "10.10.20.191:2181";


    static {
        // 重试机制。baseSleepTimeMs:重试间隔时间基数，maxRetries:是重试次数
        RetryPolicy rp = new ExponentialBackoffRetry(1000, 3);
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
        zkclient = builder.connectString(ZK_HOST)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .canBeReadOnly(false)
                .retryPolicy(rp)
                .defaultData("testdata".getBytes())
                .build();
        zkclient.start();

    }

    public static void main(String[] args) throws Exception {
        if (null == zkclient.checkExists().forPath(ROOT_PATH)) {
            zkclient.create().creatingParentsIfNeeded().forPath(ROOT_PATH);
        }
        System.out.println("监听开始/zk........");
        watch();
        Thread.sleep(Long.MAX_VALUE);
    }

    /**
     * 监听节点变化
     */
    public static void watch() throws Exception {
        pathChildrenCacheListener(zkclient);
        nodeCacheListener(zkclient);
        treeCacheListener(zkclient);
    }
}