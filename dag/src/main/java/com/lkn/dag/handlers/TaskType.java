package com.lkn.dag.handlers;

/**
 * 任务类型
 *
 * @author xijiu
 * @since 2022/3/14 上午10:46
 */
public enum TaskType {

    /** 面向节点的任务 */
    NODE_TYPE,

    /** 面向集群的任务，即整个过程中只会执行一次 */
    CLUSTER_TYPE
}
