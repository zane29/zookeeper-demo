package com.goldmsg; /**
 * Created with IntelliJ IDEA.
 * User: 周海明
 * Date: 2017/1/17
 * Time: 14:18
 */

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;

/**
 *
 * 使用curator监听zookeeper节点
 * @author qindongliang
 * **/
public class CuratorWatch {

    static CuratorFramework zkclient=null;
//    static String nameSpace="java";
    static {
    String zkhost="10.10.6.153:2181";//zk的host
    RetryPolicy rp=new ExponentialBackoffRetry(1000, 3);//重试机制
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

    public static void main(String[] args) throws Exception{
        if(null == zkclient.checkExists().forPath("/root/apple")){
            zkclient.create().creatingParentsIfNeeded().forPath("/root/apple");
        }
        watch();
        Thread.sleep(Long.MAX_VALUE);

    }

    /**
     *
     * 监听节点变化
     *
     * */
    public static void watch()throws Exception{
        PathChildrenCache cache = new PathChildrenCache(zkclient, "/java/apple", true);
        cache.start();

        System.out.println("监听开始/zk........");
        PathChildrenCacheListener plis=new PathChildrenCacheListener() {

            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
                    throws Exception {
                switch ( event.getType() )
                {
                    case CHILD_ADDED:
                    {
                        System.out.println("Node added: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                        break;
                    }

                    case CHILD_UPDATED:
                    {
                        System.out.println("Node changed: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                        break;
                    }

                    case CHILD_REMOVED:
                    {
                        System.out.println("Node removed: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                        break;
                    }
                }
            }
        };
//注册监听
        cache.getListenable().addListener(plis);
    }

}