package com.lkn.dag;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.lkn.dag.bean.NumHandler;
import com.lkn.dag.handlers.AbstractHandler;
import com.lkn.dag.handlers.Action;
import com.lkn.dag.handlers.DeployTask;
import com.lkn.dag.bean.BusHandler;
import com.lkn.dag.handlers.Config;
import com.lkn.dag.handlers.Context;
import com.lkn.dag.handlers.Handler;
import com.lkn.dag.handlers.Node;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 更新操作的业务流程
 *
 * @author xijiu
 * @since 2022/3/14 上午11:18
 */
@Slf4j
@Component
public class CoreDeployDriver {

    private static volatile AbstractHandler[] CONTROLLER_ABSTRACT_HANDLER = null;

    @Resource
    private TaskExecuteDriver taskExecuteDriver;
    @Resource
    private DAGGenerator dagGenerator;

    /**
     * 驱动的核心入口
     *
     * @param context   上下文
     */
    public void updateCluster(Context context) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        // 真正执行集群部署动作
        AbstractHandler.runInClusterDeploy(context, this::doUpdateCluster);
        log.info("instanceId {}, major handler {}, deploy time cost {}s",
                context.getInstanceId(),
                context.getMajorHandler(),
                stopwatch.elapsed(TimeUnit.SECONDS));
    }

    /**
     * 真正执行集群部署动作
     *
     * @param context   上下文
     */
    private void doUpdateCluster(Context context) {
        if (!context.isTaskRecover()) {
            // 生成DAG，首次执行某个部署任务时调用
            genericDAGDeployTask(context);
        } else {
            // 恢复DAG，重跑某个部署任务时调用
//            recoverDAGDeployTask(context);
        }
        // 执行真正的部署逻辑
        taskExecuteDriver.doBusiness(context);
    }

//    /**
//     * 恢复DAG的部署任务图
//     *
//     * @param context   上下文
//     */
//    private void recoverDAGDeployTask(Context context) {
//        List<DeployTask> deployTasks = deployTaskMapper.getDeployTasksByTaskId(context.getTaskId());
//        if (CollectionUtils.isEmpty(deployTasks)) {
//            throw new RuntimeException("recover failed");
//        }
//        Map<Integer, Node> allNodeMap = assembleAllNodes(context);
//        LinkedList<BusHandler> wholeHandlers = new LinkedList<>();
//        for (DeployTask deployTask : deployTasks) {
//            wholeHandlers.add(deployTaskToBusHandler(deployTask, allNodeMap));
//        }
//        context.setWholeHandlers(wholeHandlers);
//        BusHandler majorBusHandler = wholeHandlers.stream().filter(b -> b.getFSeq() == -1).findAny().orElse(null);
//        assert majorBusHandler != null;
//        context.setMajorBusHandler(majorBusHandler);
//        context.setMajorHandler(majorBusHandler.getHandler().handler());
//        DAGPrinter.print(wholeHandlers);
//        log.info("recover WholeHandlers are {}", JSON.toJSONString(wholeHandlers));
//    }

    /**
     * 将当前实例中所有的节点信息汇聚，包含已经在库中的节点以及待扩容的节点
     *
     * @param context   上下文
     * @return  所有节点的映射关系
     */
    private Map<Integer, Node> assembleAllNodes(Context context) {
        List<Node> allNodes = Lists.newArrayList();
        allNodes.addAll(context.getRunningNodeList());
        allNodes.addAll(context.getExpandNodeList());
        // 根据nodeIndex去重
        allNodes = allNodes.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Node::getNodeIndex))),
                ArrayList::new));
        return allNodes.stream().collect(Collectors.toMap(Node::getNodeIndex, n -> n));
    }

//    /**
//     * 将 DeployTask 还原转换为 BusHandler
//     *
//     * @param deployTask    部署任务
//     * @param allNodeMap    全部节点信息
//     * @return  BusHandler对象
//     */
//    private BusHandler deployTaskToBusHandler(DeployTask deployTask, Map<Integer, Node> allNodeMap) {
//        BusHandler busHandler = new BusHandler();
//        Handler handler = Handler.of(deployTask.getAction());
//        DeployData deployData = JSON.parseObject(deployTask.getData(), DeployData.class);
//        busHandler.setSeq(deployTask.getSeq());
//        busHandler.setFSeq((int) deployTask.getExec());
//        busHandler.setDeployTask(deployTask);
//        busHandler.setHandler(AbstractHandler.get(handler));
//        busHandler.setNode(allNodeMap.get(deployData.getNodeIndex()));
//        busHandler.setNumHandler(NumHandler.of(deployData.getExeNum(), handler, deployData.getPrevExeNum()));
//        busHandler.setPartTail(deployData.isPartTail());
//        return busHandler;
//    }

    /**
     * 生成部署任务的DAG大图
     *
     * @param context   上下文
     */
    private void genericDAGDeployTask(Context context) {
        // 初始化操作
        init();
        // 寻找对应的主处理器
        AbstractHandler majorHandler = findMajorHandler(context);
        // 生成要执行的handler的列表
        dagGenerator.generic(majorHandler, context);
        // 检查参数有效性
        checkParamsValid(majorHandler, context);
        // 生成部署任务
        genericDeployTask(context);
    }

    /**
     * 寻找主处理器
     *
     * @param context   上下文
     * @return  主处理器，如果没有找到则返回null
     */
    private AbstractHandler findMajorHandler(Context context) {
        initMajorHandler();
        for (AbstractHandler abstractHandler : CONTROLLER_ABSTRACT_HANDLER) {
            AbstractHandler.ParamState state = abstractHandler.preCheck(context, null);
            if (Objects.equals(state, AbstractHandler.ParamState.VALID)) {
                context.setMajorHandler(abstractHandler.handler());
                return abstractHandler;
            }
        }

        log.warn("not find major handler, context is {}", JSON.toJSONString(context));
        throw new RuntimeException("not find major handler");
    }

    /**
     * 初始化主处理器
     */
    private void initMajorHandler() {
        if (CONTROLLER_ABSTRACT_HANDLER == null) {
            synchronized (CoreDeployDriver.class) {
                if (CONTROLLER_ABSTRACT_HANDLER == null) {
                    CONTROLLER_ABSTRACT_HANDLER = buildHandlers();
                }
            }
        }
    }

    private void init() {
        BusHandler.reset();
    }

    /**
     * 生成部署任务的上下文并存储在数据库中
     *
     * @param context   上下文
     */
    private void genericDeployTask(Context context) {
        // 生成部署任务的基本信息
        genericBasicDeployTask(context);
        // "部署任务"落盘
        storeDeployTask(context);
    }

    /**
     * 存储部署任务
     *
     * @param context   上下文
     */
    private void storeDeployTask(Context context) {
        List<BusHandler> handlerList = context.getWholeHandlers();
        List<DeployTask> deployTasks = handlerList.stream().map(BusHandler::getDeployTask).collect(Collectors.toList());
        // deployTaskDao.insertDeployTasks(deployTasks);
        log.info("deploy task content is {}", JSON.toJSONString(deployTasks));
    }

    /**
     * 生成部署任务的基本信息
     *
     * @param context   上下文
     */
    private void genericBasicDeployTask(Context context) {
        List<BusHandler> handlerList = context.getWholeHandlers();
        String instanceId = context.getInstanceId();
        String taskId = context.getTaskId();
        for (BusHandler busHandler : handlerList) {
            AbstractHandler tmpHandler = busHandler.getHandler();
            DeployTask deployTask = busHandler.getDeployTask();
            Node node = busHandler.getNode();
            deployTask.setTaskId(taskId);
            deployTask.setInstanceId(instanceId);
            deployTask.setTaskLevel(tmpHandler.isClusterType() ? 0 : 1);
            deployTask.setAction(String.valueOf(tmpHandler.handler()));
            deployTask.setTargetId(tmpHandler.isClusterType() ? "-1" : String.valueOf(node.getNodeIndex()));
            deployTask.setStatus(0);
            deployTask.setModifyBy("");
            deployTask.setData(transToDeployData(busHandler));
            deployTask.setCreateTime(new Timestamp(System.currentTimeMillis()));
            deployTask.setModifyTime(new Timestamp(System.currentTimeMillis()));
        }
        // 将描述信息存入主handler中，持久化，方便当前任务中断后重试
        DeployTask majorDeployTask = context.getMajorBusHandler().getDeployTask();
        majorDeployTask.setData(JSON.toJSONString(majorDeployTask));
    }

    /**
     * 将当前的DAG对象转换为 DeployData，以便于后续的持久化
     *
     * @param busHandler    DAG节点大对象
     * @return  json形式
     */
    private String transToDeployData(BusHandler busHandler) {
        return JSON.toJSONString(busHandler);
    }

    /**
     * 检查参数的有效性，如果参数异常，快速失败
     *
     * @param majorHandler  主handler
     * @param context 上下文
     */
    private void checkParamsValid(AbstractHandler majorHandler, Context context) {
        Action action = majorHandler.handler() == Handler.CLUSTER_CREATE ? Action.NEW_CLUSTER : Action.VERTICAL;
        checkParamsValid(majorHandler, context, action);
    }

    /**
     * 检查参数的有效性，如果参数异常，快速失败
     *
     * @param handler  处理器
     * @param context 上下文
     * @param action 动作
     */
    private void checkParamsValid(AbstractHandler handler, Context context, Action action) {
        action = handler.handler() == Handler.EXPAND_CLUSTER ? Action.HORIZONTAL : action;
        Config currConfig = context.getCurrConfig();
        Config newConfig = context.getNewConfig();

        AbstractHandler.ParamState state = handler.preCheck(context, action);
        if (Objects.equals(state, AbstractHandler.ParamState.NONE)) {
            log.info("handler checked, but can not fit, handler is {}, curr config {}, new config {}",
                    handler.handler(), JSON.toJSONString(currConfig), JSON.toJSONString(newConfig));
            return;
        } else if (Objects.equals(state, AbstractHandler.ParamState.INVALID)) {
            log.error("pre check failed, handler {}, curr config {}, new config {}",
                    handler.handler(), JSON.toJSONString(currConfig), JSON.toJSONString(newConfig));
            throw new RuntimeException("pre check failed, handler is " + handler.getClass().getName());
        }
        NumHandler[] numHandlers = handler.subProcess(currConfig, newConfig, action);
        log.info("checkParamsValid curr handler is {}, sub handler is {}", handler.handler(), JSON.toJSONString(numHandlers));
        if (ArrayUtils.isNotEmpty(numHandlers)) {
            checkParamsValidForSeqHandlers(numHandlers, context, action);
        }
    }

    /**
     * 检查子执行器中的参数是否合法
     *
     * @param numHandlers   子执行器
     * @param context   上下文
     * @param action    动作
     */
    private void checkParamsValidForSeqHandlers(NumHandler[] numHandlers, Context context, Action action) {
        if (numHandlers == null || numHandlers.length == 0) {
            return;
        }
        for (NumHandler numHandler : numHandlers) {
            checkParamsValid(AbstractHandler.get(numHandler.getHandler()), context, action);
        }
    }


    /**
     * 根据handler的enum获取其执行bean
     *
     * @return  执行器bean
     */
    private AbstractHandler[] buildHandlers() {
        List<AbstractHandler> majorHandlers = Lists.newArrayList();
        Handler[] handlers = Handler.values();
        for (Handler handler : handlers) {
            AbstractHandler abstractHandler = AbstractHandler.get(handler);
            if (abstractHandler != null && abstractHandler.isMajor()) {
                majorHandlers.add(abstractHandler);
            }
        }
        return majorHandlers.toArray(new AbstractHandler[0]);
    }
}
