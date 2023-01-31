package com.lkn.dag.handlers;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.lkn.dag.bean.NumHandler;
import com.lkn.dag.bean.BusHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * 封装一些常用的方法，以方便诸多handler调用；主要目的是将Handler的行为与描述分开，符合单一原则
 *
 * @author xijiu
 * @since 2022/8/24 上午10:57
 */
@Slf4j
public abstract class AbstractBehaviorHandler extends AbstractHandler {

    /**
     * num注册
     *
     * @param n 编号
     * @param handler   处理器
     * @param f 父编号
     * @return  NumHandler
     */
    protected static NumHandler num(int n, Handler handler, int... f) {
        return NumHandler.of(n, handler, f);
    }

    protected List<Node> getAllNodes(Context context) {
        List<Node> allNodes = Lists.newArrayList();
        allNodes.addAll(context.getRunningNodeList());
        allNodes.addAll(context.getExpandNodeList());
        return allNodes;
    }

    /**
     * 判断当前上下文是否为实例新建
     *
     * @return  true 新建动作
     */
    protected boolean isCreate() {
        Context context = getContext();
        if (context == null) {
            throw new RuntimeException("context miss");
        }
        return Tools.isClusterCreate(context.getMajorHandler());
    }

    /**
     * 判断当前上下文是否为修改实例
     *
     * @return  true 修改动作
     */
    protected boolean isUpdate() {
        Context context = getContext();
        if (context == null) {
            throw new RuntimeException("context miss");
        }
        return Tools.isClusterUpdate(context.getMajorHandler());
    }

    /**
     * 当前的环境是否是serverless扩容
     *
     * @return  是否为serverless扩容操作
     */
    protected boolean isServerlessExpand() {
        Context context = getContext();
        if (context == null) {
            throw new RuntimeException("context miss");
        }
        LinkedList<BusHandler> wholeHandlers = context.getWholeHandlers();
        if (CollectionUtils.isNotEmpty(wholeHandlers)) {
            for (BusHandler busHandler : wholeHandlers) {
                if (busHandler.getHandler().handler() == Handler.EXPAND_CREATE_NODE) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 持久化所有node中的数据
     *
     * @param context   上下文
     */
    protected void persistenceAllNodes(Context context) {
        if (CollectionUtils.isNotEmpty(context.getRunningNodeList())) {
            for (Node node : context.getRunningNodeList()) {
//                clusterInstanceDao.saveOrUpdate(node);
            }
        }
        if (CollectionUtils.isNotEmpty(context.getExpandNodeList())) {
            for (Node node : context.getExpandNodeList()) {
//                clusterInstanceDao.saveOrUpdate(node);
            }
        }
    }

    /**
     * 持久化目标集合
     *
     * @param nodes 节点集合
     */
    protected void persistenceNodes(Collection<Node> nodes) {
        if (CollectionUtils.isNotEmpty(nodes)) {
            for (Node node : nodes) {
//                clusterInstanceDao.saveOrUpdate(node);
            }
        }
    }

    /**
     * 返回新集群的node节点个数
     *
     * @return  新集群中节点个数
     */
    protected int currClusterSize() {
        Context context = getContext();
        return 3;
    }

    /**
     * 返回新集群的node节点个数
     *
     * @return  新集群中节点个数
     */
    protected int newClusterSize() {
        Context context = getContext();
        return 4;
    }

    /**
     * 当前是否多盘部署
     *
     * @return  是否多盘部署
     */
    protected boolean isMultiDisk() {
        Context context = getContext();
        return true;
    }

    /**
     * 计算磁盘个数，计算新描述的
     *
     * @return  磁盘个数
     */
    protected int diskNum() {
        Context context = getContext();
        return diskNum(context.getNewConfig());
    }

    /**
     * 计算磁盘个数
     *
     * @param config    描述
     * @return  磁盘个数
     */
    protected int diskNum(Config config) {
        return 4;
    }

    /** kafka配置的前缀 */
    private static final String KAFKA_PREV = "kafka.";
    /** rmq配置前缀 */
    private static final String MQ_PREV = "cloud.";
    /** 用来控制config修改时候的并发锁 */
    private static final Map<String, Boolean> CONFIG_LOCK_MAP = new ConcurrentHashMap<>();

    /**
     * 解锁
     *
     * @param node  节点
     */
    private void unlockConfigForNode(Node node) {
        CONFIG_LOCK_MAP.remove(node.getNodeId());
    }


    /**
     * 为当前节点加锁，如果存在竞争，那么执行自旋；当然竞争的概率比较低
     *
     * @param node  节点
     */
    private void lockConfigForNode(Node node) {
        Boolean result = CONFIG_LOCK_MAP.putIfAbsent(node.getNodeId(), true);
        if (result != null) {
            // 说明有锁在竞争，休息1秒后重试
            Tools.sleep(1000);
            lockConfigForNode(node);
        }
    }


    /**
     * 解锁
     *
     * @param node  节点
     */
    private void unlockForInstance(Node node) {
        Semaphore semaphore = INSTANCE_LOCK_MAP.get(node.getInstanceId());
        if (semaphore != null) {
            semaphore.release();
        }
    }


    /**
     * 为当前节点加锁，如果存在竞争，那么执行自旋；当然竞争的概率比较低
     *
     * @param node  节点
     */
    private void lockForInstance(Node node) {
        Semaphore semaphore = INSTANCE_LOCK_MAP.computeIfAbsent(node.getInstanceId(), (k) -> new Semaphore(1));
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            log.error("lockForInstance error, node {}", JSON.toJSONString(node), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将参数的前缀去掉
     *
     * @param totalParamName    参数名字
     * @return  实际名字
     */
    private String actual(String totalParamName) {
        if (totalParamName.startsWith(KAFKA_PREV)) {
            return totalParamName.substring(KAFKA_PREV.length());
        } else if (totalParamName.startsWith(MQ_PREV)) {
            return totalParamName.substring(MQ_PREV.length());
        } else {
            log.error("invalid param {}", totalParamName);
            throw new RuntimeException("invalid param " + totalParamName);
        }
    }



}
