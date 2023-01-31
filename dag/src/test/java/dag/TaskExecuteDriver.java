package dag;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import dag.bean.BusHandler;
import dag.handlers.AbstractHandler;
import dag.handlers.Context;
import dag.handlers.DeployTask;
import dag.handlers.Handler;
import dag.handlers.Node;
import dag.handlers.Ref;
import dag.handlers.Tools;
import dag.pool.EagerThreadPoolExecutor;
import dag.pool.TaskQueue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 主要用来驱动任务执行，可能串行或并行
 *
 * @author xijiu
 * @since 2022/3/29 下午4:24
 */
@Slf4j
@Component
public class TaskExecuteDriver {

    /** 存放每个实例下的每个节点是否处于重启的语境下 */
    private static final Map<String, Map<Integer, RebootBean>> REBOOT_CONTEXT_MAP = Maps.newConcurrentMap();

    private final static ThreadPoolExecutor THREAD_POOL_EXECUTOR = createThreadPool();

    private static EagerThreadPoolExecutor createThreadPool() {
        AtomicInteger num = new AtomicInteger();
        TaskQueue<Runnable> taskQueue = new TaskQueue<>(100);
        // 3节点实例创建过程，线程占用数量不会超过9，故设置30个常用线程绰绰有余；
        // 但考虑到并发创建大实例的场景，将最大线程数设置为2000，因为新建线程的并发度最大为20，可同时创建30个节点的实例
        // 1分钟后节线程会被回收，不会占用系统资源
        EagerThreadPoolExecutor poolExecutor = new EagerThreadPoolExecutor(
                30,
                2000,
                1,
                TimeUnit.MINUTES,
                taskQueue,
                r -> {
                    Thread thread = new Thread(r);
                    thread.setName(String.valueOf(num.incrementAndGet()));
                    return thread;
                },
                new ThreadPoolExecutor.AbortPolicy());
        taskQueue.setExecutor(poolExecutor);
        return poolExecutor;
    }


    public void doBusiness(Context context) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        BusHandler majorHandler = context.getMajorBusHandler();

        try {
            log.info("THREAD_POOL before active num {}", THREAD_POOL_EXECUTOR.getActiveCount());
            exeBusinessHandler(context, majorHandler, new DAGContext());
            log.info("THREAD_POOL after active num {}", THREAD_POOL_EXECUTOR.getActiveCount());
        } finally {
            String instanceId = Tools.extractInstanceId(context);
            REBOOT_CONTEXT_MAP.remove(instanceId);
            // 记录耗时总时长
            long elapsed = stopwatch.elapsed(TimeUnit.SECONDS);
            printTimeCostLog(instanceId, majorHandler.getHandler().handler(), "ALL", elapsed);
        }
    }

    /**
     * 开始执行一个业务 handler
     *
     * @param context   上下文
     * @param busHandler    当前封装的handler
     * @param dagContext    DAG图的上下文
     */
    private void exeBusinessHandler(Context context, BusHandler busHandler, DAGContext dagContext) {
        // 驱动当前执行器及其所有子执行器
        BusHandler redirectHandler = driveCurrHandler(context, busHandler, dagContext);
        // 没有发生重定向，那么驱动后继节点继续执行
        if (redirectHandler.getSeq() == busHandler.getSeq()) {
            // 驱动DAG后的执行器继续执行
            driveNextHandlerInDAG(context, redirectHandler, dagContext);
        } else {
            // 发生重定向，那么重新进入当前方法
            exeBusinessHandler(context, redirectHandler, dagContext);
        }
    }

    /**
     * 驱动DAG图的后续节点继续执行
     *
     * @param context   上下文
     * @param currBusHandler    当前执行器
     * @param dagContext    DAG上下文
     */
    private void driveNextHandlerInDAG(Context context, BusHandler currBusHandler, DAGContext dagContext) {
        // 找到当前节点后的所有DAG节点
        List<BusHandler> nextDAGHandlers = findNextDAGPoints(currBusHandler, context.getWholeHandlers());
        for (BusHandler nextHandler : nextDAGHandlers) {
            boolean needExe = dagContext.put(nextHandler, currBusHandler.getNumHandler().getExeNum());
            if (needExe) {
                Future<?> future = THREAD_POOL_EXECUTOR.submit(() -> {
                    Thread.currentThread().setName("Driver_" + context.getTaskId());
                    // 启动新线程，将context放入上下文中
                    AbstractHandler.runInContext(context, () -> exeBusinessHandler(context, nextHandler, dagContext));
                });
                dagContext.taskQueue.add(future);
            }
        }

        // 根节点需要等待全部任务都执行完毕
        if (currBusHandler.getNumHandler().isRoot()) {
            LinkedBlockingQueue<Future<?>> queue = dagContext.taskQueue;
            while (!queue.isEmpty()) {
                Future<?> future = queue.poll();
                if (future != null) {
                    Tools.sync(future);
                }
            }
        }
    }

    /**
     * 当前执行器开始执行，或者驱动其子执行器
     *
     * @param context   上下文
     * @param busHandler    当前处理器
     * @param dagContext    dag的上下文
     * @return 重定向的DAG节点，这块主要处理需要依赖broker或者ECS重启的handler
     */
    private BusHandler driveCurrHandler(Context context, BusHandler busHandler, DAGContext dagContext) {
        DeployTask deployTask = busHandler.getDeployTask();
        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            // 执行handler调用
            invokeBusinessHandler(context, busHandler, deployTask);
            // 处理重启场景
            BusHandler redirectHandler = operateRebootContext(context, busHandler, dagContext);
            // 打印耗时日志
            printTimeCostLog(context, busHandler, stopwatch.elapsed(TimeUnit.SECONDS));
            return redirectHandler;
        } catch (Exception e) {
            log.error("handler {} occur exception ", busHandler.getHandler().handler(), e);
//            modifyTaskStatus(deployTask, DeployTaskStatus.FAILED);
            throw new RuntimeException(e);
        }
    }

    /**
     * 输出耗时日志
     *
     * @param context   上下文
     * @param busHandler    业务handler
     * @param cost  耗时
     */
    private void printTimeCostLog(Context context, BusHandler busHandler, long cost) {
        String instanceId = Tools.extractInstanceId(context);
        Handler handler = busHandler.getHandler().handler();
        if (isUnderRebootEnv(context, busHandler)) {
            int nodeIndex = busHandler.getNode().getNodeIndex();
            Map<Integer, RebootBean> rebootBeanMap = REBOOT_CONTEXT_MAP.get(instanceId);
            RebootBean rebootBean = rebootBeanMap.get(nodeIndex);
            String step = rebootBean == null ? "finished" : rebootBean.currRebootMode().name();
            printTimeCostLog(instanceId, handler, String.valueOf(nodeIndex), cost, step);
        } else {
            AbstractHandler aHandler = busHandler.getHandler();
            String nodeIndex = aHandler.isClusterType() ? "" : String.valueOf(busHandler.getNode().getNodeIndex());
            printTimeCostLog(instanceId, handler, nodeIndex, cost);
        }
    }

    /**
     * 判断当前执行器是否处于重启的大环境下
     *
     * @param context   上下文
     * @param currBusHandler    当前业务执行器
     * @return  是否处于重启环境下
     */
    private boolean isUnderRebootEnv(Context context, BusHandler currBusHandler) {
        Handler handler = currBusHandler.getHandler().handler();
        if (handler == Handler.REBOOT_BEGIN || handler == Handler.REBOOT_END) {
            return true;
        }
        String instanceId = Tools.extractInstanceId(context);
        if (currBusHandler.getHandler().isNodeType()) {
            int nodeIndex = currBusHandler.getNode().getNodeIndex();
            Map<Integer, RebootBean> rebootBeanMap = REBOOT_CONTEXT_MAP.get(instanceId);
            if (MapUtils.isNotEmpty(rebootBeanMap)) {
                return rebootBeanMap.containsKey(nodeIndex);
            }
        }
        return false;
    }

    /**
     * 维护重启状态的上下文
     *
     * @param context    上下文
     * @param busHandler    业务handler
     * @param dagContext    dag的上下文
     * @return 重定向后的handler
     */
    private BusHandler operateRebootContext(Context context, BusHandler busHandler, DAGContext dagContext) {
        Handler currHandler = busHandler.getHandler().handler();

        if (currHandler == Handler.REBOOT_BEGIN) {
            return operateRebootBegin(context, busHandler, dagContext);
        } else if (currHandler == Handler.REBOOT_END) {
            return operateRebootEnd(context, busHandler);
        }
        return busHandler;
    }

    /**
     * 处理重启的end节点
     *
     * @param context   上下文
     * @param busHandler    业务handler
     * @return  重定向后的节点
     */
    private BusHandler operateRebootEnd(Context context, BusHandler busHandler) {
        String instanceId = Tools.extractInstanceId(context);
        int nodeIndex = busHandler.getNode().getNodeIndex();
        Map<Integer, RebootBean> nodeIndexRebootBeanMap = REBOOT_CONTEXT_MAP.get(instanceId);
        RebootBean rebootBean = nodeIndexRebootBeanMap.get(nodeIndex);
        RebootMode rebootMode = rebootBean.currRebootMode();
        // 如果当前已经是broker重启后的状态，那么整个重启过程结束，不需要重定向了
        if (rebootMode == RebootMode.AFTER_BROKER_STARTED) {
            // 将上下文信息清除
            nodeIndexRebootBeanMap.remove(nodeIndex);
            return busHandler;
        } else {
            return rebootBean.beginBusHandler;
        }
    }

    /**
     * 重启开始的处理逻辑
     *
     * @param context   上下文
     * @param busHandler    业务handler
     * @param dagContext    标识dag执行的上下文
     */
    private BusHandler operateRebootBegin(Context context, BusHandler busHandler, DAGContext dagContext) {
        String instanceId = Tools.extractInstanceId(context);
        int nodeIndex = busHandler.getNode().getNodeIndex();
        Map<Integer, RebootBean> nodeIndexRebootMap
                = REBOOT_CONTEXT_MAP.computeIfAbsent(instanceId, k -> Maps.newConcurrentMap());
        RebootBean rebootBean = nodeIndexRebootMap.computeIfAbsent(nodeIndex, k -> {
            // 计算当前DAG图下，重启类型
            AbstractHandler.RebootType rebootType = computeRebootType(context, busHandler);
            return new RebootBean(rebootType, busHandler);
        });
        rebootBean.roll();
        // 需要重置一下重启环境的DAG上下文
        cleanDAGContextWhenRebootBegin(context, busHandler, dagContext);
        // reboot开始节点不会发生重定向
        return busHandler;
    }

    /**
     * 在reboot节点执行时，需要将REBOOT_BEGIN与REBOOT_END之间的节点的DAG上下文清除
     *
     * @param context   上下文
     * @param busHandler    业务handler
     * @param dagContext    标识dag执行的上下文
     */
    private void cleanDAGContextWhenRebootBegin(Context context, BusHandler busHandler, DAGContext dagContext) {
        Ref<Boolean> flag = new Ref<>(false);
        int currExeNum = busHandler.getNumHandler().getExeNum();
        DAG.breadthFirstSearch(currExeNum, context.getWholeHandlers(), (tmpBusHandler, level) -> {
            if (flag.get()) {
                return;
            }
            dagContext.cleanExeRecords(tmpBusHandler.getNumHandler().getExeNum());
            if (tmpBusHandler.getHandler().handler() == Handler.REBOOT_END) {
                flag.set(true);
            }
        });
    }

    /**
     * 计算当前DAG下是ECS重启还是Broker重启
     *
     * @param context   上下文
     * @param busHandler    业务handler
     * @return  重启类型
     */
    private AbstractHandler.RebootType computeRebootType(Context context, BusHandler busHandler) {
        LinkedList<BusHandler> wholeHandlers = context.getWholeHandlers();
        Ref<Boolean> overFlag = new Ref<>(false);
        Ref<AbstractHandler.RebootType> rebootTypeRef = new Ref<>(AbstractHandler.RebootType.NONE);

        DAG.breadthFirstSearch(busHandler.getNumHandler().getExeNum(), wholeHandlers, (busHandlerTmp, level) -> {
            if (overFlag.get() || rebootTypeRef.get() == AbstractHandler.RebootType.ECS) {
                return;
            }
            if (busHandlerTmp.getHandler().handler() == Handler.REBOOT_END) {
                overFlag.set(true);
                return;
            }
            AbstractHandler.RebootType rebootType = busHandlerTmp.getHandler().rebootType();
            if (rebootType == AbstractHandler.RebootType.ECS || rebootType == AbstractHandler.RebootType.BROKER) {
                rebootTypeRef.set(rebootType);
            }
        });
        return rebootTypeRef.get();
    }

    /**
     * 记录某个handler的耗时总时长
     *
     * @param instanceId    实例id
     * @param handler   handler
     * @param nodeIndex 节点index
     * @param elapsedTime   耗时
     */
    private void printTimeCostLog(String instanceId, Handler handler, String nodeIndex, long elapsedTime) {
        printTimeCostLog(instanceId, handler, nodeIndex, elapsedTime, "finished");
    }

    private void printTimeCostLog(String instanceId, Handler handler, String nodeIndex, long elapsedTime, String step) {
        log.info("instance {} execute {}, handler is {}, node index is {}, time cost {}s",
                instanceId, step, handler, nodeIndex, elapsedTime);
    }

    private void invokeBusinessHandler(Context context, BusHandler busHandler, DeployTask deployTask) {
        // 如果当前任务已经标记为完成状态，那么直接返回
        if (!busHandler.getHandler().rerunAnyway()
                && Objects.equals(deployTask.getStatus(), 1)) {
            return;
        }
        if (isRebootEnv(context, busHandler)) {
            invokeInRebootEnv(context, busHandler, deployTask);
        } else {
            invokeInNormalEnv(context, busHandler, deployTask);
        }
    }

    /**
     * 重启环境的调用
     *
     * @param context   上下文
     * @param busHandler    业务handler
     * @param deployTask    部署任务对象
     */
    private void invokeInRebootEnv(Context context, BusHandler busHandler, DeployTask deployTask) {
        String instanceId = Tools.extractInstanceId(context);
        Node node = busHandler.getNode();
        int nodeIndex = node.getNodeIndex();
        Map<Integer, RebootBean> nodeIndexRebootBeanMap = REBOOT_CONTEXT_MAP.get(instanceId);
        RebootBean rebootBean = nodeIndexRebootBeanMap.get(nodeIndex);
        RebootMode rebootMode = rebootBean.currRebootMode();
        // 如果当前的reboot步骤已经执行过了，那么直接跳过
        if (!isNeedRebootExe(rebootMode, deployTask)) {
            return;
        }

        AbstractHandler abstractHandler = busHandler.getHandler();
        Runnable runnable = null;
        switch (rebootMode) {
            case BEFORE_BROKER_STOP:
                runnable = () -> abstractHandler.beforeBrokerStop(context, node);
                break;
            case ON_BROKER_STOPPED:
                runnable = () -> abstractHandler.onBrokerStopped(context, node);
                break;
            case BEFORE_ECS_STOP:
                runnable = () -> abstractHandler.beforeEcsStop(context, node);
                break;
            case ON_ECS_STOPPED:
                runnable = () -> abstractHandler.onEcsStopped(context, node);
                break;
            case AFTER_ECS_STARTED:
                runnable = () -> abstractHandler.afterEcsStart(context, node);
                break;
            case AFTER_BROKER_STARTED:
                runnable = () -> abstractHandler.afterBrokerStart(context, node);
                break;
        }
        doExeRebootHandler(busHandler, runnable);
    }

    /**
     * 是否需要执行后续的重启动作
     *
     * @param rebootMode    重启步骤
     * @param deployTask    部署任务
     * @return  是否需要执行
     */
    private boolean isNeedRebootExe(RebootMode rebootMode, DeployTask deployTask) {
        int status = deployTask.getStatus();
        if (status == 2) {
            return true;
        }
        // 处在部署的步骤中
        if (status >= 20
                && status <= 29) {
            return status < rebootMode.status;
        }
        return true;
    }

    /**
     * 常规环境调用
     *
     * @param context   上下文
     * @param busHandler    业务handler
     * @param deployTask    部署任务对象
     */
    private void invokeInNormalEnv(Context context, BusHandler busHandler, DeployTask deployTask) {
        // 执行当前处理器
        doExeBusinessHandler(busHandler, context);
    }

    /**
     * 当前运行的环境是否处在重启上下文中
     *
     *
     * @param context    上下文
     * @param busHandler    目标handler
     * @return  是否处在重启环境
     */
    private boolean isRebootEnv(Context context, BusHandler busHandler) {
        // 如果是集群任务，那么直接返回false
        if (busHandler.getHandler().isClusterType()) {
            return false;
        }
        String instanceId = Tools.extractInstanceId(context);
        if (StringUtils.isEmpty(instanceId)) {
            return false;
        }
        int nodeIndex = busHandler.getNode().getNodeIndex();
        if (REBOOT_CONTEXT_MAP.containsKey(instanceId)) {
            Map<Integer, RebootBean> nodeIndexRebootBeanMap = REBOOT_CONTEXT_MAP.get(instanceId);
            return nodeIndexRebootBeanMap.containsKey(nodeIndex);
        }
        return false;
    }

    /**
     * 找到目标节点在DAG图的后续节点集合
     *
     * @param currBusHandler    当前节点
     * @param wholeHandlers 全部节点
     * @return  后续节点
     */
    private List<BusHandler> findNextDAGPoints(BusHandler currBusHandler, List<BusHandler> wholeHandlers) {
        List<BusHandler> resultList = Lists.newArrayList();
        int currExeNum = currBusHandler.getNumHandler().getExeNum();
        for (BusHandler busHandler : wholeHandlers) {
            int[] fExeNum = busHandler.getNumHandler().getPrevExeNum();
            if (Tools.contains(fExeNum, currExeNum)) {
                resultList.add(busHandler);
            }
        }
        return resultList;
    }

    /**
     * 真正执行一个业务handler
     *
     * @param busHandler    当前封装的handler
     */
    private void doExeRebootHandler(BusHandler busHandler, Runnable runnable) {
        AbstractHandler abstractHandler = busHandler.getHandler();
        Handler handler = abstractHandler.handler();
        try {
            if (!busHandler.isPartTail()) {
                autoRetryBusiness(handler, runnable);
            }
        } catch (Exception e) {
            log.error("exeBusiness error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 真正执行一个业务handler
     *
     * @param busHandler    当前封装的handler
     * @param context   上下文
     */
    private void doExeBusinessHandler(BusHandler busHandler, Context context) {
        AbstractHandler abstractHandler = busHandler.getHandler();
        Handler handler = abstractHandler.handler();
        try {
            if (!busHandler.isPartTail()) {
                if (busHandler.getHandler().isClusterType()) {
                    if (!abstractHandler.isExecuted(context)) {
                        autoRetryBusiness(handler, () -> {
                            maybeInvokeExactlyOnce(abstractHandler, context);
                            abstractHandler.exeBusiness(context);
                        });
                    }
                } else {
                    if (!abstractHandler.isExecuted(context, busHandler.getNode())) {
                        autoRetryBusiness(handler, () -> {
                            maybeInvokeExactlyOnce(abstractHandler, context);
                            abstractHandler.exeBusiness(context, busHandler.getNode());
                        });
                    }
                }
            }
        } catch (Exception e) {
            log.error("exeBusiness error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 调用 exeBusiness 的前置方法，控制执行且只执行一次
     *
     * @param abstractHandler   目标handler
     * @param context   上下文
     */
    private void maybeInvokeExactlyOnce(AbstractHandler abstractHandler, Context context) {
        // 方法没有覆写，直接返回
        if (!isExactlyOnceOverridden(abstractHandler)) {
            return;
        }
        String instanceId = context.getInstanceId();
        Handler handler = abstractHandler.handler();
        Map<String, Map<Handler, Boolean>> exactlyOnceMap = AbstractHandler.getExactlyOnceMap();
        Map<Handler, Boolean> map = exactlyOnceMap.computeIfAbsent(instanceId, k -> {
            Map<Handler, Boolean> valueMap = Maps.newConcurrentMap();
            valueMap.put(handler, false);
            return valueMap;
        });
        Boolean isExecuted = map.get(handler);
        if (isExecuted) {
            return;
        }
        synchronized (map.get(handler)) {
            isExecuted = map.get(handler);
            if (isExecuted) {
                return;
            }
            abstractHandler.exactlyOnce(context);
            map.put(handler, true);
        }
    }

    /**
     * 判断目标方法是否被重写
     *
     * @param abstractHandler   目标handler
     * @return  是否重写
     */
    private boolean isExactlyOnceOverridden(AbstractHandler abstractHandler) {
        try {
            Class<? extends AbstractHandler> subClass = abstractHandler.getClass();
            return subClass.getDeclaredMethod("exactlyOnce", Context.class) != null;
        } catch (NoSuchMethodException | SecurityException e) {
            return false;
        }
    }

    /**
     * 自动进行业务重试操作
     *
     * @param runnable  承载无入参、无出参函数
     */
    private void autoRetryBusiness(Handler handler, Runnable runnable) {
        int retryTimes = 3;
        Ref<Exception> ref = new Ref<>(null);
        boolean result = Tools.retryExe(retryTimes, new Tools.Runner() {
            @Override
            public boolean exe() {
                runnable.run();
                return true;
            }

            @Override
            public int sleepSeconds(long exeIndex) {
                // 如果业务执行失败后，等待一个随机时间后重试
                return RandomUtils.nextInt(5, 10);
            }

            @Override
            public void occurException(long retryTime, Exception e) {
                ref.set(e);
                // 如果发生异常，那么打印异常信息
                log.warn("execute business failed, but deploy2 will try again, curr retry time is {}", retryTime, e);
            }
        });

        // 执行3次后依旧失败，那么抛出异常
        if (!result) {
            String msg = String.format("execute business failed after %d times, handler is %s", retryTimes, handler);
            log.error(msg);
            Exception exception = ref.get();
            if (exception == null) {
                throw new RuntimeException(msg);
            } else {
                throw new RuntimeException(exception);
            }
        }
    }

//    /**
//     * 修改任务的执行状态
//     *
//     * @param deployTask    任务对象
//     * @param taskStatus    部署状态
//     */
//    private void modifyTaskStatus(DeployTask deployTask, DeployTaskStatus taskStatus) {
//        String taskId = deployTask.getTaskId();
//        int seq = deployTask.getSeq();
//        String action = deployTask.getAction();
//        deployTaskDao.updateDeployTaskStatus(taskId, seq, action, taskStatus.code);
//    }

    /**
     * DAG上下文
     */
    private static class DAGContext {
        /**
         * 存放某个节点已经完成的父节点
         * key：当前节点的num
         * val：所有完成的父节点的num
         */
        private final Map<Integer, List<Integer>> pointExeResultMap = Maps.newConcurrentMap();

        /** 存放线程执行的任务结果 */
        private final LinkedBlockingQueue<Future<?>> taskQueue = new LinkedBlockingQueue<>();

        /**
         * 将完成情况存入上下文中
         *
         * @param busHandler    目标处理器
         * @param prevNum  前置节点num
         * @return  true：父节点内容与配置已经严格匹配    false：父节点还未收集完毕
         */
        public boolean put(BusHandler busHandler, int prevNum) {
            int exeNum = busHandler.getNumHandler().getExeNum();
            int[] fExeNum = busHandler.getNumHandler().getPrevExeNum();
            synchronized (busHandler) {
                List<Integer> list = pointExeResultMap.computeIfAbsent(exeNum, k -> Lists.newArrayList());
                list.add(prevNum);
                if (isContentMatch(fExeNum, list)) {
                    return true;
                }
            }
            return false;
        }

        public void cleanExeRecords(int exeNum) {
            pointExeResultMap.remove(exeNum);
        }

        /**
         * 数组的内容与列表中的是否一致
         *
         * @param arr  数组
         * @param list  列表
         * @return  true 相等   false 不等
         */
        public boolean isContentMatch(int[] arr, List<Integer> list) {
            if (arr == null || list == null) {
                return false;
            }
            if (arr.length != list.size()) {
                return false;
            }
            Set<Integer> set1 = Tools.arrToSet(arr);
            Set<Integer> set2 = Sets.newHashSet(list);
            return set1.containsAll(set2);
        }
    }

    /**
     * 重启语境下的辅助类
     */
    private static class RebootBean {
        private final RebootMode[] rebootModeArr;
        private int arrIndex = -1;
        private final BusHandler beginBusHandler;

        private RebootBean(AbstractHandler.RebootType rebootType, BusHandler beginBusHandler) {
            this.rebootModeArr = rebootType == AbstractHandler.RebootType.BROKER
                    ? RebootMode.BROKER_REBOOT : RebootMode.ECS_REBOOT;
            this.beginBusHandler = beginBusHandler;
        }

        private void roll() {
            arrIndex++;
        }

        private RebootMode currRebootMode() {
            return rebootModeArr[arrIndex];
        }
    }

    private enum RebootMode {
        BEFORE_BROKER_STOP(21),
        ON_BROKER_STOPPED(22),
        AFTER_BROKER_STARTED(23),

        BEFORE_ECS_STOP(24),
        ON_ECS_STOPPED(25),
        AFTER_ECS_STARTED(26),
        ;

        public final int status;

        RebootMode(int status) {
            this.status = status;
        }

        private static final RebootMode[] ECS_REBOOT = {
                BEFORE_BROKER_STOP, ON_BROKER_STOPPED, BEFORE_ECS_STOP,
                ON_ECS_STOPPED, AFTER_ECS_STARTED, AFTER_BROKER_STARTED };

        private static final RebootMode[] BROKER_REBOOT = {
                BEFORE_BROKER_STOP, ON_BROKER_STOPPED, AFTER_BROKER_STARTED };
    }
}
