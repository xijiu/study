package com.lkn.dag.handlers;


/**
 * major handler的抽象类
 *
 * @author xijiu
 * @since 2022/3/21 上午9:06
 */
public abstract class AbstractMajorHandler extends AbstractClusterHandler {

    public boolean isMajor() {
        return true;
    }

    @Override
    public abstract Context fillContext(Config newConfig, String taskId);
}
