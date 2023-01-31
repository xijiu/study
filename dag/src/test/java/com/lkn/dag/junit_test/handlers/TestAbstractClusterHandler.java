package com.lkn.dag.junit_test.handlers;


import com.lkn.dag.handlers.Tools;
import com.lkn.dag.junit_test.Record;
import com.lkn.dag.handlers.AbstractClusterHandler;
import com.lkn.dag.handlers.Context;
import com.lkn.dag.handlers.Handler;
import com.lkn.dag.handlers.TaskType;
import com.lkn.dag.tools.EnumTools;
import com.lkn.dag.tools.Global;

/**
 * @author xijiu
 * @since 2022/4/11 上午9:57
 */
public abstract class TestAbstractClusterHandler extends AbstractClusterHandler {

    @Override
    public Handler handler() {
        return EnumTools.add(this.getClass().getSimpleName(), TaskType.class, TaskType.CLUSTER_TYPE);
    }


    @Override
    public void exeBusiness(Context context) {
        Tools.sleep(200);
        Global.records.add(Record.of(handler().toString(), 0L, System.currentTimeMillis()));
    }
}
