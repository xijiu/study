package com.lkn.dag.handlers;


/**
 * 集群粒度的任务
 *
 * @author xijiu
 * @since 2022/3/21 上午9:06
 */
public abstract class AbstractClusterHandler extends AbstractBehaviorHandler {

    public TaskType taskType() {
        return TaskType.CLUSTER_TYPE;
    }

    @Override
    public void exeBusiness(Context context, Node node) {
    }

    @Override
    public void exeBusiness(Context context) {
    }
}
