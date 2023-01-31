package com.lkn.dag.junit_test;

import com.lkn.dag.bean.NumHandler;
import com.lkn.dag.handlers.Action;
import com.lkn.dag.handlers.AbstractClusterHandler;
import com.lkn.dag.handlers.Config;
import com.lkn.dag.handlers.Context;
import com.lkn.dag.handlers.Handler;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author xijiu
 * @since 2022/4/11 上午8:36
 */
@Component
public class TestController extends AbstractClusterHandler {

    @Override
    public Handler handler() {
        return Handler.CLUSTER_UPDATE;
    }

    @Override
    public boolean isMajor() {
        return true;
    }

    @Override
    public ParamState preCheck(Context context, Action action) {
        Config currConfig = context.getCurrConfig();
        Config newConfig = context.getNewConfig();
        if (Objects.equals(currConfig.getState(), "ItemStatus.RUNNING")
                && Objects.equals(newConfig.getState(), "ItemStatus.RUNNING")) {
            return ParamState.VALID;
        }
        return ParamState.NONE;
    }

    @Override
    public NumHandler[] subProcess(Config currConfig, Config newConfig, Action action) {
        return new NumHandler[] {
                num(1, Handler.CREATE_ECS, -1),
                num(2, Handler.START_ECS, 1),
                num(3, Handler.REBOOT_ECS, 2),
                num(4, Handler.DISK, 3),
                num(5, Handler.EIP_ENABLE, 4)
        };
    }
}
