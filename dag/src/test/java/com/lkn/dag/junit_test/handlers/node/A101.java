package com.lkn.dag.junit_test.handlers.node;

import com.lkn.dag.bean.NumHandler;
import com.lkn.dag.handlers.AbstractBehaviorHandler;
import com.lkn.dag.handlers.Action;
import com.lkn.dag.junit_test.handlers.TestAbstractNodeHandler;
import com.lkn.dag.handlers.Config;
import org.springframework.stereotype.Component;

/**
 * 复合节点
 *
 * @author xijiu
 * @since 2022/4/11 上午8:46
 */
@Component
public class A101 extends TestAbstractNodeHandler {

    /*
           21
           -
           -
           22
           -
           -
           23
    */
    @Override
    public NumHandler[] subProcess(Config currConfig, Config newConfig, Action action) {
        return new NumHandler[] {
                AbstractBehaviorHandler.num(1, new A21().handler(), -1),
                AbstractBehaviorHandler.num(2, new A22().handler(), 1),
                AbstractBehaviorHandler.num(3, new A23().handler(), 2),
        };
    }
}
