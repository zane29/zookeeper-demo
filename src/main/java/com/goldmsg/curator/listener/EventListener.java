package com.goldmsg.curator.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.goldmsg.curator.CuratorWatch.PATH;

/**
 * Created by zhouhaiming on 2017-12-14 17:23
 * Email: dg_chow@163.com
 *
 * @Description:多种监听器的使用
 */
public class EventListener {
    /**
     * @描述：第一种监听器的添加方式: Cache 的三种实现 实践
     * Path Cache：监视一个路径下1）孩子结点的创建、2）删除、3）以及结点数据的更新。产生的事件会传递给注册的PathChildrenCacheListener。
     * Node Cache：监视一个结点的创建、更新、删除，并将结点的数据缓存在本地。
     * Tree Cache：Path Cache和Node Cache的“合体”，监视路径下的创建、更新、删除事件，并缓存路径下所有孩子结点的数据。
     */

    // 1.path Cache  连接  路径  是否获取数据
    // 监听所有的子节点，且是无限监听的模式。但是指定目录下节点的子节点不再监听。
    public static void pathChildrenCacheListener(CuratorFramework client) throws Exception {
        PathChildrenCache childrenCache = new PathChildrenCache(client, PATH, true);
        PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                ChildData data = event.getData();
                switch (event.getType()) {
                    case CHILD_ADDED:
                        System.out.println("Node added: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                        System.out.println("Node added: " + data.getPath());
                        System.out.println("CHILD_ADDED : " + data.getPath() + "  数据:" + new String(data.getData(), "UTF-8"));
                        break;
                    case CHILD_REMOVED:
                        System.out.println("Node added: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                        System.out.println("Node added: " + data.getPath());
                        System.out.println("CHILD_REMOVED : " + data.getPath() + "  数据:" + new String(data.getData(), "UTF-8"));
                        break;
                    case CHILD_UPDATED:
                        System.out.println("Node added: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                        System.out.println("Node added: " + data.getPath());
                        System.out.println("CHILD_UPDATED: " + data.getPath() + "  数据:" + new String(data.getData(), "UTF-8"));
                        break;
                    default:
                        break;
                }
            }
        };
        childrenCache.getListenable().addListener(pathChildrenCacheListener);
        System.out.println("Register zk watcher successfully!");
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
    }

    // 2.Node Cache  监控本节点的变化情况  连接  目录  是否压缩
    // 监听本节点的变化，节点可以进行修改操作，删除节点后会再次创建(空节点)
    public static void nodeCacheListener(CuratorFramework client) throws Exception {
        //设置节点的cache
        final NodeCache nodeCache = new NodeCache(client, PATH, false);
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("the test node is change and result is :");
                System.out.println("path: " + nodeCache.getCurrentData().getPath());
                System.out.println("data: " + new String(nodeCache.getCurrentData().getData(), "UTF-8"));
                System.out.println("stat: " + nodeCache.getCurrentData().getStat());
            }
        });
        nodeCache.start();
    }

    // 3.Tree Cache
    // 监听 指定节点和节点下的所有的节点的变化--无限监听  可以进行本节点的删除(不在创建)
    public static void treeCacheListener(CuratorFramework client) throws Exception {
        //设置节点的cache
        TreeCache treeCache = new TreeCache(client, PATH);
        //设置监听器和处理过程
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                ChildData data = event.getData();
                if (data != null) {
                    switch (event.getType()) {
                        case NODE_ADDED:
                            System.out.println("NODE_ADDED: " + data.getPath() + "  数据:" + new String(data.getData(), "UTF-8"));
                            break;
                        case NODE_REMOVED:
                            System.out.println("NODE_REMOVED: " + data.getPath() + "  数据:" + new String(data.getData(), "UTF-8"));
                            break;
                        case NODE_UPDATED:
                            System.out.println("NODE_UPDATED: " + data.getPath() + "  数据:" + new String(data.getData(), "UTF-8"));
                            break;
                        default:
                            break;
                    }
                } else {
                    System.out.println("data is null: " + event.getType());
                }
            }
        });
        //开始监听
        treeCache.start();
    }


    /**
     * @描述：第二种监听器的添加方式: 对指定的节点进行添加操作
     * 仅仅能监控指定的本节点的数据修改,删除 操作 并且只能监听一次 --->不好
     */
    public static void setListenterOne(CuratorFramework client) throws Exception {
        // 注册观察者，当节点变动时触发
        byte[] data = client.getData().usingWatcher(new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("获取节点监听器 : " + event);
            }
        }).forPath("PATH");
        System.out.println("节点数据: " + new String(data, "UTF-8"));
    }


    /**
     * @描述：第三种监听器的添加方式: 也是一次性的监听操作, 使用后就无法在继续监听了
     */
    public static void setListenterTwo(CuratorFramework client) throws Exception {
        ExecutorService pool = Executors.newCachedThreadPool();
        CuratorListener listener = new CuratorListener() {
            @Override
            public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
                System.out.println("监听器  : " + event.toString());
            }
        };
        client.getCuratorListenable().addListener(listener, pool);
        client.getData().inBackground().forPath(PATH);
        client.getData().inBackground().forPath(PATH);
        client.getData().inBackground().forPath(PATH);
        client.getData().inBackground().forPath(PATH);
        Thread.sleep(Long.MAX_VALUE);
    }
}
