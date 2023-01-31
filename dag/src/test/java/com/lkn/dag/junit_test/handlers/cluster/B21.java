package com.lkn.dag.junit_test.handlers.cluster;

import com.lkn.dag.bean.NumHandler;
import com.lkn.dag.handlers.Action;
import com.lkn.dag.junit_test.handlers.TestAbstractClusterHandler;
import com.lkn.dag.junit_test.handlers.node.A1;
import com.lkn.dag.junit_test.handlers.node.A2;
import com.lkn.dag.junit_test.handlers.node.A3;
import com.lkn.dag.junit_test.handlers.node.A4;
import com.lkn.dag.junit_test.handlers.node.A5;
import com.lkn.dag.handlers.Config;
import org.springframework.stereotype.Component;

/**
 * @author xijiu
 * @since 2022/4/11 下午4:24
 */
@Component
public class B21 extends TestAbstractClusterHandler {

    /**
     * 子任务是否根据节点粒度并行
     *
     * @return  true：需要并行执行子任务     false：串行执行
     */
    public boolean nodeParallel() {
        return true;
    }

    @Override
    public NumHandler[] subProcess(Config currConfig, Config newConfig, Action action) {
        return new NumHandler[] {
                num(1, new A1().handler(), -1),
                num(2, new A2().handler(), 1),
                num(3, new A3().handler(), 2),
                num(4, new A4().handler(), 3),
                num(5, new A5().handler(), 4)
        };
    }

}
