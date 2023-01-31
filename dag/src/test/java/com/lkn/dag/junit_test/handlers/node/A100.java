package com.lkn.dag.junit_test.handlers.node;

import com.lkn.dag.bean.NumHandler;
import com.lkn.dag.handlers.AbstractBehaviorHandler;
import com.lkn.dag.handlers.Action;
import com.lkn.dag.junit_test.handlers.TestAbstractNodeHandler;
import com.lkn.dag.handlers.Config;
import com.lkn.dag.junit_test.handlers.cluster.B9;
import org.springframework.stereotype.Component;

/**
 * 复合节点
 *
 * @author xijiu
 * @since 2022/4/11 上午8:46
 */
@Component
public class A100 extends TestAbstractNodeHandler {

    /*
           1
        -  -   -
       -   -       -
      2    3         4
      -    -         -
      -    -         -
       -   -         5
       -   -       -
        -  -    -
           6
           -
           -
           7
           -
           -
           8
    */
    @Override
    public NumHandler[] subProcess(Config currConfig, Config newConfig, Action action) {
        return new NumHandler[] {
                AbstractBehaviorHandler.num(1, new A21().handler(), -1),
                AbstractBehaviorHandler.num(2, new A22().handler(), 1),
                AbstractBehaviorHandler.num(3, new A23().handler(), 1),
                AbstractBehaviorHandler.num(4, new A24().handler(), 1),
                AbstractBehaviorHandler.num(5, new A25().handler(), 4),
                AbstractBehaviorHandler.num(6, new B9().handler(), 2, 3, 5),
                AbstractBehaviorHandler.num(7, new A26().handler(), 6),
                AbstractBehaviorHandler.num(8, new A27().handler(), 7),
                AbstractBehaviorHandler.num(9, new A28().handler(), 8),
        };
    }
}
