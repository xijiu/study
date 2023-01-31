package dag_junit_test.handlers.cluster;

import dag.bean.NumHandler;
import dag.handlers.Action;
import dag.handlers.Config;
import dag_junit_test.handlers.TestAbstractClusterHandler;
import dag_junit_test.handlers.node.A1;
import dag_junit_test.handlers.node.A2;
import dag_junit_test.handlers.node.A3;
import dag_junit_test.handlers.node.A4;
import dag_junit_test.handlers.node.A5;
import dag_junit_test.handlers.node.A6;
import dag_junit_test.handlers.node.A7;
import dag_junit_test.handlers.node.A8;
import org.springframework.stereotype.Component;

/**
 * @author xijiu
 * @since 2022/4/11 下午4:24
 */
@Component
public class B23 extends TestAbstractClusterHandler {

    /**
     * 子任务是否根据节点粒度并行
     *
     * @return  true：需要并行执行子任务     false：串行执行
     */
    public boolean nodeParallel() {
        return true;
    }

    /*
                 1
                 -
                 -
                 2
            -  -  -   -
           -   -   -     -
          3    4    5        -
           -   -   -          7
             - - -            -
               6            -
                 -         -
                    -     -
                       8
     */
    @Override
    public NumHandler[] subProcess(Config currConfig, Config newConfig, Action action) {
        return new NumHandler[] {
                num(1, new A1().handler(), -1),
                num(2, new A2().handler(), 1),
                num(3, new A3().handler(), 2),
                num(4, new A4().handler(), 2),
                num(5, new A5().handler(), 2),
                num(6, new A6().handler(), 3, 4, 5),
                num(7, new A7().handler(), 2),
                num(8, new A8().handler(), 6, 7),
        };
    }

}
