package dag_junit_test.handlers;


import dag.handlers.AbstractNodeHandler;
import dag.handlers.Context;
import dag.handlers.Handler;
import dag.handlers.Node;
import dag.handlers.TaskType;
import dag.handlers.Tools;
import dag_junit_test.Record;
import dag.tools.EnumTools;
import dag.tools.Global;

/**
 * @author xijiu
 * @since 2022/4/11 上午9:57
 */
public abstract class TestAbstractNodeHandler extends AbstractNodeHandler {

    @Override
    public Handler handler() {
        return EnumTools.add(this.getClass().getSimpleName(), TaskType.class, TaskType.NODE_TYPE);
    }

    @Override
    public void exeBusiness(Context context, Node node) {
        Tools.sleep(200);
        Global.records.add(Record.of(handler().toString(), node.getNodeIndex(), System.currentTimeMillis()));
    }
}
