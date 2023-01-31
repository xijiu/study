package dag_junit_test.complex;

import com.alibaba.fastjson.JSON;
import dag.CoreDeployDriver;
import dag.bean.NumHandler;
import dag.handlers.Action;
import dag.handlers.Config;
import dag.handlers.Context;
import dag.handlers.Handler;
import dag.tools.Global;
import dag_junit_test.BaseTest;
import dag_junit_test.TestController;
import dag_junit_test.handlers.cluster.B20;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * 1、全局只有一个集群任务
 * 2、集群任务下所有节点都是node节点，节点顺序执行
 * 3、集群任务并行度设置为false
 *
 * @author xijiu
 * @since 2022/4/12 上午9:11
 */
public class ComplexTest1 extends BaseTest {
    @Resource
    private CoreDeployDriver coreDeployDriver;

    private static class MyController extends TestController {
        @Override
        public NumHandler[] subProcess(Config currConfig, Config newConfig, Action action) {
            return new NumHandler[] {
                    num(1, new B20().handler(), -1)
            };
        }
    }

    @Test
    public void test() {
        register(Handler.CLUSTER_UPDATE, new MyController());
        Global.reset();
        Context context = mockContext(2);
        coreDeployDriver.updateCluster(context);
        System.out.println("Global.recodes.toString is : " + JSON.toJSONString(Global.records));
        Assert.assertTrue(fixedSeqWithNode(Global.records, "B20-0,A1-10001,A2-10001,A3-10001,A4-10001,A5-10001,A1-10002,A2-10002,A3-10002,A4-10002,A5-10002"));
    }

}
