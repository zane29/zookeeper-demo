package com.goldmsg.curator.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.utils.ZKPaths;

/**
 * Created by zhouhaiming on 2017-12-14 11:29
 * Email: dg_chow@163.com
 *
 * @Description:路径监听类
 */

/**
 *
 * @描述：第三种监听器的添加方式: Cache 的三种实现 实践
 * Path Cache：监视一个路径下1）孩子结点的创建、2）删除，3）以及结点数据的更新。产生的事件会传递给注册的PathChildrenCacheListener。
 * Node Cache：监视一个结点的创建、更新、删除，并将结点的数据缓存在本地。
 * Tree Cache：Path Cache和Node Cache的“合体”，监视路径下的创建、更新、删除事件，并缓存路径下所有孩子结点的数据。
 */
//1.path Cache  连接  路径  是否获取数据
//能监听所有的子节点，且是无限监听的模式。但是，指定目录下节点的子节点不再监听。
public class PathChildrenCacheListener implements org.apache.curator.framework.recipes.cache.PathChildrenCacheListener {

    @Override
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
            throws Exception {
        switch (event.getType()) {
            // 增加子节点
            case CHILD_ADDED: {
                System.out.println("Node added: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                break;
            }
            // 子节点更新
            case CHILD_UPDATED: {
                System.out.println("Node changed: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                break;
            }
            // 子节点移除
            case CHILD_REMOVED: {
                System.out.println("Node removed: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                break;
            }
        }
    }
}
