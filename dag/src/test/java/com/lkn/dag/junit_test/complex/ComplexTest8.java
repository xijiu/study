package com.lkn.dag.junit_test.complex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSON;
import com.lkn.dag.bean.NumHandler;
import com.lkn.dag.handlers.Action;
import com.lkn.dag.handlers.Config;
import com.lkn.dag.handlers.Context;
import com.lkn.dag.handlers.Handler;
import com.lkn.dag.junit_test.BaseTest;
import com.lkn.dag.junit_test.TestController;
import com.lkn.dag.junit_test.handlers.cluster.B1;
import com.lkn.dag.junit_test.handlers.cluster.B2;
import com.lkn.dag.junit_test.handlers.cluster.B23;
import com.lkn.dag.junit_test.handlers.node.A1;
import com.lkn.dag.junit_test.handlers.node.A2;
import com.lkn.dag.junit_test.handlers.node.A3;
import com.lkn.dag.junit_test.handlers.node.A4;
import com.lkn.dag.junit_test.handlers.node.A5;
import com.lkn.dag.junit_test.handlers.node.A6;
import com.lkn.dag.junit_test.handlers.node.A7;
import com.lkn.dag.junit_test.handlers.node.A8;
import com.lkn.dag.tools.Global;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * 1、集群任务+节点任务
 * 2、次序为 节点任务 + 集群任务 + 节点任务 + 集群任务 + 节点任务
 * 3、集群任务并行度设置为TRUE
 *
 * @author xijiu
 * @since 2022/4/12 上午9:11
 */
public class ComplexTest8 extends BaseTest {

    private static class MyController extends TestController {
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
                    num(4, new A2().handler(), 1),
                    num(5, new B1().handler(), 4),
                    num(6, new A5().handler(), 5),
                    num(7, new A6().handler(), 5),
                    num(11, new A7().handler(), 7),
            };
        }
    }

    @Test
    public void test() {
        register(Handler.CLUSTER_UPDATE, new ComplexTest8.MyController());
        Global.reset();
        Context context = mockContext(1);
        coreDeployDriver.updateCluster(context);
        System.out.println("Global.recodes.toString is : " + JSON.toJSONString(Global.records));
    }

}
