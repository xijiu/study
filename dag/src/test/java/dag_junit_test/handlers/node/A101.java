package dag_junit_test.handlers.node;

import dag.bean.NumHandler;
import dag.handlers.Action;
import dag.handlers.Config;
import dag_junit_test.handlers.TestAbstractNodeHandler;
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
                num(1, new A21().handler(), -1),
                num(2, new A22().handler(), 1),
                num(3, new A23().handler(), 2),
        };
    }
}
