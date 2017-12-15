package com.goldmsg.curator; /**
 * Created with IntelliJ IDEA.
 * User: 周海明
 * Date: 2017/1/17
 * Time: 14:18
 */

import com.goldmsg.curator.listener.EventListener;
import com.goldmsg.curator.listener.PathChildrenCacheListener;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;

import static com.goldmsg.curator.listener.EventListener.pathChildrenCacheListener;
import static com.goldmsg.curator.listener.EventListener.setListenterOne;

/**
 * 使用curator监听zookeeper节点
 *
 * @author qindongliang
 **/
public class CuratorWatch {

    public static CuratorFramework zkclient = null;
    public static final String PATH = "/root/apple";


    static {
        String zkhost = "10.10.20.191:2181";
        RetryPolicy rp = new ExponentialBackoffRetry(1000, 3);//重试机制
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
        zkclient = builder.connectString(zkhost)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .canBeReadOnly(false)
                .retryPolicy(rp)/*baseSleepTimeMs 是重试间隔时间基数，maxRetries 是重试次数*/
                .defaultData("".getBytes())
                .build();
        zkclient.start();

    }

    public static void main(String[] args) throws Exception {
//        if (null == zkclient.checkExists().forPath("/root")) {
//            zkclient.create().creatingParentsIfNeeded().forPath("/root");
//        }
        System.out.println("监听开始/zk........");
        watch();
        Thread.sleep(Long.MAX_VALUE);
    }

    /**
     * 监听节点变化
     */
    public static void watch() throws Exception {
        // 注册监听sm
//        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkclient, PATH, true);
//        pathChildrenCache.start();
//        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener());

        pathChildrenCacheListener(zkclient);
    }
}