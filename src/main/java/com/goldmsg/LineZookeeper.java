package com.goldmsg;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 * User: 周海明
 * Date: 2017/1/13
 * Time: 15:09
 */
public class LineZookeeper implements Watcher {
    static CountDownLatch countDownLatch = null;

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        LineZookeeper lineZookeeper = new LineZookeeper();
        countDownLatch = new CountDownLatch(1);//同步计数器
        ZooKeeper zooKeeper = new ZooKeeper("10.10.6.151:2181", 500000, lineZookeeper);
        countDownLatch.await();//等待所有工人完成工作

        Stat a1 = zooKeeper.exists("/root", false);
        if (a1 == null) {
            System.out.println("/root节点不存在");
            //创建一个节点root，数据是mydata,不进行ACL权限控制，节点为永久性的(即客户端shutdown了也不会消失)
            String a = zooKeeper.create("/root", "mydata".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } else {
            System.out.println("/root节点存在");
        }

        Stat a2 = zooKeeper.exists("/root/childone", false);
        if (a1 == null) {
            System.out.println("/root/childone节点不存在");
            //在root下面创建一个childone znode,数据为childone,不进行ACL权限控制，节点为永久性的
            String a = zooKeeper.create("/root/childone", "mydata".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } else {
            System.out.println("/root/childone节点存在");
        }

        //取得/root节点下的子节点名称,返回List<String>
        List<String> c = zooKeeper.getChildren("/root", true);
        System.out.print("/root节点下的子节点名称:\t");
        for (String str : c)
            System.out.print(str + "\t");


        //取得/root/childone节点下的数据,返回byte[]
        byte[] d = zooKeeper.getData("/root/childone", true, null);
        String f = new String(d, "utf-8");
        System.out.println("root/childone节点下的数据：\t" + f);

//修改节点/root/childone下的数据，第三个参数为版本，如果是-1，那会无视被修改的数据版本，直接改掉
        Stat g = zooKeeper.setData("/root/childone", "childonemodify".getBytes(), -1);
//删除/root/childone这个节点，第二个参数为版本，－1的话直接删除，无视版本
//        zooKeeper.delete("/root/childone", -1);
//        try {
//            zooKeeper.close();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event.getState());
        if (event.getState() == Event.KeeperState.SyncConnected) {
            System.out.println("我连接上了");
        }
        countDownLatch.countDown();//计数器减一
    }
}
