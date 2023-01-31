package com.lkn.dag.junit_test.complex;

import com.alibaba.fastjson.JSON;
import com.lkn.dag.bean.NumHandler;
import com.lkn.dag.handlers.Action;
import com.lkn.dag.handlers.Config;
import com.lkn.dag.handlers.Context;
import com.lkn.dag.handlers.Handler;
import com.lkn.dag.junit_test.BaseTest;
import com.lkn.dag.junit_test.TestController;
import com.lkn.dag.junit_test.handlers.cluster.B23;
import com.lkn.dag.tools.Global;
import org.junit.Assert;
import org.junit.Test;


/**
 * 1、全局只有一个集群任务
 * 2、集群任务下所有节点都是node节点，节点为一张DAG
 * 3、集群任务并行度设置为true
 *
 * @author xijiu
 * @since 2022/4/12 上午9:11
 */
public class ComplexTest4 extends BaseTest {

    private static class MyController extends TestController {
        @Override
        public NumHandler[] subProcess(Config currConfig, Config newConfig, Action action) {
            return new NumHandler[] {
                    num(1, new B23().handler(), -1)
            };
        }
    }

    /*
             1
             -
             -
             2
          - - -  -
       -   -   -     -
      3    4    5        -
       -   -   -          7
         - - -            -
           6            -
             -         -
                -     -
                   8
    */
    @Test
    public void test() {
        register(Handler.CLUSTER_UPDATE, new MyController());
        Global.reset();
        Context context = mockContext(1);
        coreDeployDriver.updateCluster(context);
        System.out.println("Global.recodes.toString is : " + JSON.toJSONString(Global.records));

        Assert.assertTrue(fixedSeqWithNode(Global.records, "A1-10001,A2-10001,A3-10001,A6-10001,A8-10001"));
        Assert.assertTrue(fixedSeqWithNode(Global.records, "A1-10001,A2-10001,A4-10001,A6-10001,A8-10001"));
        Assert.assertTrue(fixedSeqWithNode(Global.records, "A1-10001,A2-10001,A5-10001,A6-10001,A8-10001"));
        Assert.assertTrue(fixedSeqWithNode(Global.records, "A1-10001,A2-10001,A7-10001,A8-10001"));
        Assert.assertTrue(isSameTime(Global.records, "A3-10001", "A4-10002", "A5-10002", "A7-10002"));
    }

    @Test
    public void test2() {
        register(Handler.CLUSTER_UPDATE, new MyController());
        Global.reset();
        Context context = mockContext(3);
        coreDeployDriver.updateCluster(context);
        System.out.println("Global.recodes.toString is : " + JSON.toJSONString(Global.records));

        for (int i = 1; i <= 3; i++) {
            int nodeId = 10000 + i;
            Assert.assertTrue(fixedSeqWithNode(Global.records, "A1-" + nodeId + ",A2-" + nodeId + ",A3-" + nodeId + ",A6-" + nodeId + ",A8-" + nodeId + ""));
            Assert.assertTrue(fixedSeqWithNode(Global.records, "A1-" + nodeId + ",A2-" + nodeId + ",A4-" + nodeId + ",A6-" + nodeId + ",A8-" + nodeId + ""));
            Assert.assertTrue(fixedSeqWithNode(Global.records, "A1-" + nodeId + ",A2-" + nodeId + ",A5-" + nodeId + ",A6-" + nodeId + ",A8-" + nodeId + ""));
            Assert.assertTrue(fixedSeqWithNode(Global.records, "A1-" + nodeId + ",A2-" + nodeId + ",A7-" + nodeId + ",A8-" + nodeId + ""));
            Assert.assertTrue(isSameTime(Global.records, "A3-" + nodeId + "", "A4-" + nodeId + "", "A5-" + nodeId + "", "A7-" + nodeId + ""));
        }

        for (int i = 1; i <= 3; i++) {
            int nodeId = 10000 + i;
            Assert.assertTrue(isSameTime(Global.records, "A3-" + nodeId + "", "A4-" + nodeId + "", "A5-" + nodeId + "", "A7-" + nodeId + ""));
        }

        for (int i = 1; i <= 8; i++) {
            String prev = "A" + i;
            Assert.assertTrue(isSameTime(Global.records, prev + "-10001", prev + "-10002", prev + "-10003"));
        }
    }
}
