package com.lkn.dag.junit_test.complex;

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
import org.junit.Assert;
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
public class ComplexTest6 extends BaseTest {

    /*
              A1
              -
          -   -   -
         -    -     -
       A2     A3     A4
         -    -     -
          -   -   -
              -
              B1
            -    -
         -          -
       A5            A6
         -          -
            -    -
              B2
              -
              -
              A7
              -
              -
              A8
    */
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
                    num(2, new A2().handler(), 1),
                    num(3, new A3().handler(), 1),
                    num(4, new A4().handler(), 1),
                    num(5, new B1().handler(), 2, 3, 4),
                    num(6, new A5().handler(), 5),
                    num(7, new A6().handler(), 5),
                    num(8, new B2().handler(), 6, 7),
                    num(9, new A7().handler(), 8),
                    num(10, new A8().handler(), 9),
            };
        }
    }

    /**
     * 单节点case
     */
    @Test
    public void test() {
        register(Handler.CLUSTER_UPDATE, new MyController());
        Global.reset();
        Context context = mockContext(1);
        coreDeployDriver.updateCluster(context);
        System.out.println("Global.recodes.toString is : " + JSON.toJSONString(Global.records));

        Assert.assertTrue(fixedSeqWithNode(Global.records, "A1,A2,B1-0,A5,B2-0,A7,A8"));
        Assert.assertTrue(fixedSeqWithNode(Global.records, "A1,A3,B1-0,A6,B2-0,A7,A8"));
        Assert.assertTrue(fixedSeqWithNode(Global.records, "A1,A4,B1-0,A5,B2-0,A7,A8"));
        Assert.assertTrue(isSameTime(Global.records, "A2-10001", "A3-10001", "A4-10001"));
        Assert.assertTrue(isSameTime(Global.records, "A5-10001", "A6-10001"));
        Assert.assertTrue(isAfter(Global.records, "A1-10001,A2-10001,A3-10001,A4-10001", "B1-0"));
        Assert.assertTrue(isAfter(Global.records, "A5-10001,A6-10001", "B2-0"));
        Assert.assertTrue(isAfter(Global.records, "B1-0", "B2-0"));
    }

    /**
     * 多节点case：
     * 检查每个node的执行顺序是否正常
     */
    @Test
    public void test2() {
        register(Handler.CLUSTER_UPDATE, new MyController());
        Global.reset();
        Context context = mockContext(3);
        coreDeployDriver.updateCluster(context);
        System.out.println("Global.recodes.toString is : " + JSON.toJSONString(Global.records));

        for (int i = 1; i <= 3; i++) {
            int nodeId = 10000 + i;
            Assert.assertTrue(fixedSeqWithNode(Global.records, "A1-" + nodeId + ",A2-" + nodeId + ",B1-0,A5-" + nodeId + ",B2-0,A7-" + nodeId + ",A8-" + nodeId + ""));
            Assert.assertTrue(fixedSeqWithNode(Global.records, "A1-" + nodeId + ",A3-" + nodeId + ",B1-0,A6-" + nodeId + ",B2-0,A7-" + nodeId + ",A8-" + nodeId + ""));
            Assert.assertTrue(fixedSeqWithNode(Global.records, "A1-" + nodeId + ",A4-" + nodeId + ",B1-0,A5-" + nodeId + ",B2-0,A7-" + nodeId + ",A8-" + nodeId + ""));
            Assert.assertTrue(isSameTime(Global.records, "A2-" + nodeId + "", "A3-" + nodeId + "", "A4-" + nodeId + ""));
            Assert.assertTrue(isSameTime(Global.records, "A5-" + nodeId + "", "A6-" + nodeId + ""));
            Assert.assertTrue(isAfter(Global.records, "A1-" + nodeId + ",A2-" + nodeId + ",A3-" + nodeId + ",A4-" + nodeId + "", "B1-0"));
            Assert.assertTrue(isAfter(Global.records, "A5-" + nodeId + ",A6-" + nodeId + "", "B2-0"));
        }
        Assert.assertTrue(isAfter(Global.records, "B1-0", "B2-0"));
        Assert.assertTrue(isAfter(Global.records, "A1-10002,A2-10002,A3-10002,A4-10002,A1-10002,A2-10002,A3-10002,A4-10002,A1-10003,A2-10003,A3-10003,A4-10003", "B1-0"));
        Assert.assertTrue(isAfter(Global.records, "A5-10001,A6-10001,A5-10002,A6-10002,A5-10003,A6-10003", "B2-0"));
    }

    /**
     * 多节点case：
     * 检查多个node并行执行的情况
     */
    @Test
    public void test3() {
        register(Handler.CLUSTER_UPDATE, new MyController());
        Global.reset();
        Context context = mockContext(3);
        coreDeployDriver.updateCluster(context);
        System.out.println("Global.recodes.toString is : " + JSON.toJSONString(Global.records));

        for (int i = 1; i <= 8; i++) {
            Assert.assertTrue(isSameTime(Global.records, "A"+ i + "-10001", "A"+ i + "-10002", "A"+ i + "-10003"));
        }

        Assert.assertTrue(isSameTime(Global.records, "A2-10001", "A3-10001", "A4-10001",
                "A2-10002", "A3-10002", "A4-10002",
                "A2-10003", "A3-10003", "A4-10003"));

        Assert.assertTrue(isSameTime(Global.records, "A5-10001", "A6-10001",
                "A5-10002", "A6-10002",
                "A5-10003", "A6-10003"));
    }
}
