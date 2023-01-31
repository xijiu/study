package com.lkn.dag;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lkn.dag.handlers.Action;
import com.lkn.dag.bean.BusHandler;
import com.lkn.dag.bean.NumHandler;
import com.lkn.dag.handlers.AbstractHandler;
import com.lkn.dag.handlers.Context;
import com.lkn.dag.handlers.Handler;
import com.lkn.dag.handlers.Node;
import com.lkn.dag.handlers.Ref;
import com.lkn.dag.handlers.TaskType;
import com.lkn.dag.handlers.Tools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * DAG大图的生成器，核心类
 *
 * @author xijiu
 * @since 2022/4/8 下午10:34
 */
@Slf4j
@Component
public class DAGGenerator {

    @Resource
    private DAGTransfer dagTransfer;

    public void generic(AbstractHandler majorHandler, Context context) {
        // 根据主执行器生成全部执行器
        genericByMajor(majorHandler, context);
        // 转换为一张大的DAG图
        dagTransfer.transfer(context);
        // 调整重启部分的节点，可能将reboot节点动态删除
        adjustRebootElement(context.getWholeHandlers());
        // 打印当前任务的DAG图
        DAGPrinter.print(context.getWholeHandlers());
    }

    /**
     * 调整重启节点，如果位于reboot begin与end之间的节点均不需要重启，那么自动将reboot节点移除
     *
     * @param wholeHandlers 全量节点
     */
    private void adjustRebootElement(LinkedList<BusHandler> wholeHandlers) {
        for (BusHandler busHandler : wholeHandlers) {
            if (busHandler.getHandler().handler() == Handler.REBOOT_BEGIN) {
                if (!isActualNeedReboot(wholeHandlers, busHandler)) {
                    removeRebootHandlerByNodeIndex(wholeHandlers, busHandler.getNode().getNodeIndex());
                    adjustRebootElement(wholeHandlers);
                    return;
                }
            }
        }
    }

    /**
     * 将目标节点的reboot的开始、结束节点移除
     *
     * @param allHandlers   全量的handler
     * @param nodeIndex 节点index
     */
    private void removeRebootHandlerByNodeIndex(LinkedList<BusHandler> allHandlers, int nodeIndex) {
        BusHandler rebootBegin = null;
        BusHandler rebootEnd = null;
        for (BusHandler busHandler : allHandlers) {
            Handler currHandler = busHandler.getHandler().handler();
            Node currNode = busHandler.getNode();
            if (currHandler == Handler.REBOOT_BEGIN && currNode.getNodeIndex() == nodeIndex) {
                rebootBegin = busHandler;
            }
            if (currHandler == Handler.REBOOT_END && currNode.getNodeIndex() == nodeIndex) {
                rebootEnd = busHandler;
            }
        }

        doRemoveHandlerFromDAG(allHandlers, rebootBegin);
        doRemoveHandlerFromDAG(allHandlers, rebootEnd);
    }

    /**
     * 执行删除动作
     *
     * @param allHandlers   全量handlers
     * @param busHandler    要删除的handler
     */
    private void doRemoveHandlerFromDAG(LinkedList<BusHandler> allHandlers, BusHandler busHandler) {
        DAG.removeHandler(allHandlers, busHandler);

        log.info("delete handler {}, instance id {}, node index is {}",
                busHandler.getHandler().handler(),
                busHandler.getNode().getInstanceId(),
                busHandler.getNode().getNodeIndex());
    }

    /**
     * 判断当前节点是否真的需要重启
     *
     * @param allHandlers   全量handler
     * @param rebootBeginHandler    重启开始handler
     * @return  是否需要重启
     */
    private boolean isActualNeedReboot(LinkedList<BusHandler> allHandlers, BusHandler rebootBeginHandler) {
        Ref<Boolean> overFlag = new Ref<>(false);
        Ref<AbstractHandler.RebootType> rebootTypeRef = new Ref<>(AbstractHandler.RebootType.NONE);

        DAG.breadthFirstSearch(rebootBeginHandler.getNumHandler().getExeNum(), allHandlers, (busHandler, level) -> {
            if (overFlag.get()) {
                return;
            }
            if (busHandler.getHandler().handler() == Handler.REBOOT_END) {
                overFlag.set(true);
                return;
            }

            AbstractHandler.RebootType currRebootType = busHandler.getHandler().rebootType();
            switch (currRebootType) {
                case NONE:
                    return;
                case BROKER:
                    if (rebootTypeRef.get() == AbstractHandler.RebootType.NONE) {
                        rebootTypeRef.set(AbstractHandler.RebootType.BROKER);
                    }
                    break;
                case ECS:
                    rebootTypeRef.set(AbstractHandler.RebootType.ECS);
                    break;
            }
        });
        return rebootTypeRef.get() != AbstractHandler.RebootType.NONE;
    }

//    /**
//     * 可能存在一些重复处理器，比如开启公网端口，只需要开启一次即可
//     *
//     * @param busHandlers   列表
//     */
//    private void removeDuplicateHandler(LinkedList<BusHandler> busHandlers) {
//        Map<Integer, BusHandler> map = busHandlers.stream().collect(Collectors.toMap(BusHandler::getSeq, b -> b));
//        List<BusHandler> deleteHandlers = new ArrayList<>();
//        for (BusHandler busHandler : busHandlers) {
//            if (busHandler.getHandler().isClusterType()) {
//                BusHandler fatherHandler = map.get(busHandler.getFSeq());
//                if (fatherHandler != null && fatherHandler.getHandler().isNodeType()) {
//                    deleteHandlers.add(busHandler);
//                }
//            }
//        }
//
//        List<BusHandler> actualDeleteHandlers = new ArrayList<>();
//        Map<Handler, Set<Handler>> markMap = Maps.newHashMap();
//        for (BusHandler deleteHandler : deleteHandlers) {
//            Handler fatherHandler = map.get(deleteHandler.getFSeq()).getHandler().handler();
//            Handler handler = deleteHandler.getHandler().handler();
//            Set<Handler> handlerSet = markMap.get(fatherHandler);
//            if (handlerSet != null && handlerSet.contains(handler)) {
//                actualDeleteHandlers.add(deleteHandler);
//            } else {
//                if (handlerSet == null) {
//                    markMap.put(fatherHandler, handlerSet = new HashSet<>());
//                }
//                handlerSet.add(handler);
//            }
//        }
//        log.info("actualDeleteHandlers is " + JSON.toJSONString(actualDeleteHandlers));
//        for (BusHandler deleteHandler : actualDeleteHandlers) {
//            DAG.removeHandler(busHandlers, deleteHandler);
//        }
//    }

    /**
     * 根据主handler迭代向下生成所有的子handler
     *
     * @param majorHandler 主业务handler
     * @param context  上下文
     */
    private void genericByMajor(AbstractHandler majorHandler, Context context) {
        NumHandler major = NumHandler.of(0, majorHandler.handler(), new int[] {-1});
        BusHandler majorBusHandler = BusHandler.of(major, null);
        context.setMajorBusHandler(majorBusHandler);
        addSubHandler(majorBusHandler, context, null);
    }

    /**
     * 递归函数，将当前handler及子handler都添加至列表中
     *
     * @param currBusHandler  当前执行器
     * @param context   上下文
     */
    private void addSubHandler(BusHandler currBusHandler, Context context, Node node) {
        context.getWholeHandlers().add(currBusHandler);
        Action currAction = computeAction(currBusHandler, context);
        AbstractHandler aHandler = currBusHandler.getHandler();
        NumHandler[] subNumHandlers = aHandler.subProcess(context.getCurrConfig(), context.getNewConfig(), currAction);
        if (isNeedDrillDown(currBusHandler, context)) {
            Action subAction = aHandler.handler() == Handler.EXPAND_CLUSTER ? Action.HORIZONTAL : currAction;
            // 过滤掉无效的handler
            subNumHandlers = filterIneffectiveHandler(subNumHandlers, context, subAction);
            log.info("filterIneffectiveHandler is {}", JSON.toJSONString(subNumHandlers));
            if (aHandler.isClusterType()) {
                // 父节点是集群任务，那么将按照节点顺序依次生成任务
                addSubHandlerForClusterType(currBusHandler, context, subNumHandlers);
            } else {
                // 父节点是节点任务，那么依次添加子任务
                addSubHandlerForNodeType(currBusHandler, context, subNumHandlers, node);
            }
        }
    }

    /**
     * 计算当前上下文的环境
     *
     * @param currBusHandler    当前bus handler
     * @param context   上下文
     * @return  结果
     */
    private Action computeAction(BusHandler currBusHandler, Context context) {
        if (context.getMajorHandler() == Handler.CLUSTER_CREATE) {
            return Action.NEW_CLUSTER;
        }

        Ref<Boolean> isExpand = new Ref<>(false);
        LinkedList<BusHandler> wholeHandlers = context.getWholeHandlers();
        DAG.findAboveHandlers(wholeHandlers, currBusHandler, (busHandler, level) -> {
            if (busHandler.getHandler().handler() == Handler.EXPAND_CLUSTER) {
                isExpand.set(true);
            }
        });
        return isExpand.get() ? Action.HORIZONTAL : Action.VERTICAL;
    }

    /**
     * 过滤掉无效的handler，某些handler在PreCheck阶段就返回none，将这些handler过滤掉
     *
     * @param subNumHandlers    子DAG图
     * @param context   上下文
     * @param action   动作
     * @return  有效的数组
     */
    private NumHandler[] filterIneffectiveHandler(NumHandler[] subNumHandlers, Context context, Action action) {
        Set<Integer> validNumSet = Sets.newHashSet();
        for (NumHandler numHandler : subNumHandlers) {
            AbstractHandler abstractHandler = AbstractHandler.get(numHandler.getHandler());
            AbstractHandler.ParamState paramState = abstractHandler.preCheck(context, action);
            if (Objects.equals(paramState, AbstractHandler.ParamState.VALID)) {
                validNumSet.add(numHandler.getExeNum());
            }
        }
        return DAG.rebuildDAG(subNumHandlers, validNumSet);
    }

    /**
     * 当前节点是否需要下钻
     * 1、当前节点配置了 子NumHandler
     * 2、当前节点的targetHandlerList不为空
     *
     * @param busHandler    业务handler
     * @param context   上下文
     * @return  是否需要下钻
     */
    private boolean isNeedDrillDown(BusHandler busHandler, Context context) {
        AbstractHandler aHandler = busHandler.getHandler();
        Action currAction = computeAction(busHandler, context);
        NumHandler[] subNumHandlers = aHandler.subProcess(context.getCurrConfig(), context.getNewConfig(), currAction);
        // 将preCheck返回无效的handler过滤掉
        if (ArrayUtils.isNotEmpty(subNumHandlers)) {
            Action subAction = aHandler.handler() == Handler.EXPAND_CLUSTER ? Action.HORIZONTAL : currAction;
            subNumHandlers = filterIneffectiveHandler(subNumHandlers, context, subAction);
            if (ArrayUtils.isNotEmpty(subNumHandlers)) {
                List<Node> nodes = queryNodeListByBusHandler(busHandler, context);
                return CollectionUtils.isNotEmpty(nodes);
            }
        }
        return false;
    }

    /**
     * 添加节点类型的子任务
     */
    private void addSubHandlerForNodeType(BusHandler currBusHandler,
                                          Context context, NumHandler[] subNumHandlers, Node node) {

        subNumHandlers = rebuildNewBusHandler(subNumHandlers);
        for (NumHandler subNumHandler : subNumHandlers) {
            BusHandler subBusHandler = BusHandler.of(subNumHandler, currBusHandler, node);
            Action action = computeAction(subBusHandler, context);
            AbstractHandler.ParamState paramState = subBusHandler.getHandler().preCheck(context, action);
            if (Objects.equals(paramState, AbstractHandler.ParamState.VALID)) {
                addSubHandler(subBusHandler, context, node);
            }
        }
    }

    /**
     * 添加集群类型的子任务
     * 因为业务的特殊性，如果在同一个层级的DAG图中存在集群任务与节点任务的交替执行情况时，需要满足：
     * 节点任务后的集群任务，必须能够收敛至一个节点
     *
     * @param busHandler    目标处理器
     * @param context   上下文
     * @param numHandlers   当前层级的DAG图
     */
    private void addSubHandlerForClusterType(BusHandler busHandler, Context context, NumHandler[] numHandlers) {
        int prevNum = -1;
        Set<Integer> leafNumSet = Sets.newHashSet();

        while (true) {
            TaskType nextTaskType = searchNextTaskType(numHandlers, prevNum);
            if (nextTaskType == null) {
                break;
            } else if (nextTaskType == TaskType.CLUSTER_TYPE) {
                operateClusterHandler(busHandler, context, numHandlers, prevNum, Tools.setToArr(leafNumSet));
            } else if (nextTaskType == TaskType.NODE_TYPE) {
                // 优先使用处理器提供的节点列表
                List<Node> nodeList = queryNodeListByBusHandler(busHandler, context);
                operateMultiNodeHandler(busHandler, context, numHandlers, prevNum, nodeList, leafNumSet);
            }
            prevNum = findLastHandlerNum(numHandlers, prevNum, nextTaskType);
        }
    }

    /**
     * 根据执行器查询node列表
     *
     * @param busHandler    目标执行器
     * @param context   上下文
     * @return  要处理的节点列表
     */
    private List<Node> queryNodeListByBusHandler(BusHandler busHandler, Context context) {
        List<Node> udfNodes = busHandler.getHandler().targetNodeList(context, busHandler);
        return udfNodes == null ? context.getRunningNodeList() : udfNodes;
    }

    private TaskType searchNextTaskType(NumHandler[] numHandlers, int prevNum) {
        for (NumHandler numHandler : numHandlers) {
            if (numHandler.afterTargetHandler(prevNum)) {
                return AbstractHandler.get(numHandler.getHandler()).taskType();
            }
        }
        return null;
    }

    /**
     * 处理多个节点的执行器
     *
     * @param busHandler    当前节点
     * @param context   上下文
     * @param numHandlers   DAG图
     * @param prevNum   DAG图中开始的节点编号
     * @param nodeList  node列表
     * @param leafNumSet    存储叶子节点编号
     */
    private void operateMultiNodeHandler(BusHandler busHandler, Context context, NumHandler[] numHandlers,
                                        int prevNum, List<Node> nodeList, Set<Integer> leafNumSet) {
        // 用来生成最终DAG图的节点编号
        AtomicInteger exeNum = new AtomicInteger(Math.max(DAG.findMaxExeNum(context.getWholeHandlers()), 100));
        // 暂存每个节点的任务信息，以便后续的调整、归纳
        List<List<BusHandler>> nodeResultList = initNodeResultList(nodeList);
        int index = 0;
        for (Node node : nodeList) {
            NumHandler[] newNumHandlers = rebuildNewBusHandler(numHandlers, exeNum);
            List<BusHandler> resultBusHandlers = nodeResultList.get(index++);
            operateSingleNodeHandler(prevNum, busHandler, context, newNumHandlers, node, resultBusHandlers);
        }

        // 记录当前操作执行完毕后的叶子节点
        recordLeafNode(busHandler, nodeResultList, leafNumSet);
        // 调整多节点任务之间的依赖关系，主要是顺序执行时需要调整
        adjustDependency(nodeResultList, busHandler);
    }

    /**
     * 记录叶子节点的信息
     * 1、如果所有节点时并行执行，那么叶子节点即为所有DAG图中的叶子节点总和
     * 2、如果节点之间是串行执行的话，那么叶子节点为最后一张DAG图的叶子节点
     *
     * @param busHandler    目标执行器
     * @param resultList    多张DAG图
     * @param leafNumSet    用来收集叶子节点的集合
     */
    private void recordLeafNode(BusHandler busHandler, List<List<BusHandler>> resultList, Set<Integer> leafNumSet) {
        leafNumSet.clear();
        if (busHandler.getHandler().nodeParallel()) {
            for (List<BusHandler> busHandlers : resultList) {
                List<BusHandler> leafNodeList = DAG.findLeafNode(busHandlers);
                for (BusHandler handler : leafNodeList) {
                    leafNumSet.add(handler.getNumHandler().getExeNum());
                }
            }
        } else {
            List<BusHandler> busHandlers = resultList.get(resultList.size() - 1);
            List<BusHandler> leafNodeList = DAG.findLeafNode(busHandlers);
            for (BusHandler handler : leafNodeList) {
                leafNumSet.add(handler.getNumHandler().getExeNum());
            }
        }
    }

    private int findLastHandlerNum(NumHandler[] numHandlers, int prevNum, TaskType taskType) {
        List<NumHandler> list = Lists.newArrayList();
        DAG.breadthFirstSearch(prevNum, numHandlers, new DAG.NumHandlerHook() {
            @Override
            public boolean isMatch(NumHandler numHandler) {
                return AbstractHandler.get(numHandler.getHandler()).taskType() == taskType;
            }

            @Override
            public void hook(NumHandler numHandler) {
                list.add(numHandler);
            }
        });
        List<NumHandler> leafs = DAG.findLeafNodeOfNumHandler(list);
        return leafs.get(0).getExeNum();
    }

    /**
     * 调整多节点任务的依赖关系，主要针对顺序执行的节点
     * 将前一个节点的DAG的叶子节点，设置为下一个节点DAG图中根节点的开始节点
     *
     * @param nodeResultList    所有节点的处理器列表
     * @param currBusHandler    目标节点
     */
    private void adjustDependency(List<List<BusHandler>> nodeResultList, BusHandler currBusHandler) {
        if (currBusHandler.getHandler().nodeParallel()) {
            return;
        }

        for (int i = 0; i < nodeResultList.size() - 1; i++) {
            List<BusHandler> currList = nodeResultList.get(i);
            List<BusHandler> nextList = nodeResultList.get(i + 1);

            List<BusHandler> prevNodes = DAG.findLeafNode(currList);
            List<BusHandler> rootNodes = DAG.findRootNode(nextList);

            Set<Integer> numSet = prevNodes.stream()
                    .map(h -> h.getNumHandler().getExeNum()).collect(Collectors.toSet());
            int[] arr = Tools.setToArr(numSet);

            for (BusHandler rootNode : rootNodes) {
                rootNode.getNumHandler().setPrevExeNum(arr);
            }
        }
    }

    /**
     * 初始化列表
     */
    private List<List<BusHandler>> initNodeResultList(List<Node> nodeList) {
        List<List<BusHandler>> nodeResultList = Lists.newArrayList();
        for (int i = 0; i < nodeList.size(); i++) {
            nodeResultList.add(Lists.newArrayList());
        }
        return nodeResultList;
    }

    private NumHandler[] rebuildNewBusHandler(NumHandler[] allNumHandlers) {
        return rebuildNewBusHandler(allNumHandlers, null);
    }

    /**
     * 重构DAG，不改变DAG结构，只变更执行编号
     *
     * @param numHandlers   原始DAG图
     * @param exeNum    编号生成器
     * @return  新的DAG图
     */
    private NumHandler[] rebuildNewBusHandler(NumHandler[] numHandlers, AtomicInteger exeNum) {
        NumHandler[] newNumHandlers = new NumHandler[numHandlers.length];
        for (int i = 0; i < numHandlers.length; i++) {
            NumHandler numHandler = numHandlers[i];
            int[] copy = Tools.arrDeepCopy(numHandler.getPrevExeNum());
            newNumHandlers[i] = NumHandler.of(numHandler.getExeNum(), numHandler.getHandler(), copy);
        }
        if (exeNum != null) {
            changeNodeHandlerNum(newNumHandlers, exeNum);
        }
        return newNumHandlers;
    }

    /**
     * 改变DAG图中的节点编号，只适用于节点任务类型
     */
    private void changeNodeHandlerNum(NumHandler[] allNumHandlers, AtomicInteger exeNum) {
        List<NumHandler> change = Lists.newArrayList();
        DAG.breadthFirstSearch(-1, allNumHandlers, new DAG.NumHandlerHook() {
            @Override
            public boolean isMatch(NumHandler numHandler) {
                return true;
            }

            @Override
            public void hook(NumHandler numHandler) {
                if (AbstractHandler.get(numHandler.getHandler()).isNodeType()) {
                    change.add(numHandler);
                }
            }
        });
        for (NumHandler numHandler : change) {
            int newVal = exeNum.incrementAndGet();
            DAG.changeExeNum(allNumHandlers, numHandler, newVal);
        }
    }

    /**
     * 生成某节点的执行器
     */
    private void operateSingleNodeHandler(int prevNum, BusHandler currBusHandler, Context context,
                                          NumHandler[] allNumHandlers, Node node, List<BusHandler> resultHandlers) {

        DAG.breadthFirstSearch(prevNum, allNumHandlers, new DAG.NumHandlerHook() {
            @Override
            public boolean isMatch(NumHandler numHandler) {
                AbstractHandler abstractHandler = AbstractHandler.get(numHandler.getHandler());
                return Objects.equals(abstractHandler.taskType(), TaskType.NODE_TYPE);
            }

            @Override
            public void hook(NumHandler numHandler) {
                BusHandler nextBusHandler = BusHandler.of(numHandler, currBusHandler, node);
                resultHandlers.add(nextBusHandler);
                addSubHandler(nextBusHandler, context, node);
            }
        });
    }

    /**
     * 生成集群任务的执行器
     */
    private void operateClusterHandler(BusHandler currBusHandler, Context context,
                                       NumHandler[] numHandlers, int prevNum, int[] firstNodePrev) {

        NumHandler[] rebuildNumHandlers = rebuildNewBusHandler(numHandlers);
        AtomicBoolean fistNodeFlag = new AtomicBoolean(false);
        DAG.breadthFirstSearch(prevNum, rebuildNumHandlers, new DAG.NumHandlerHook() {
            @Override
            public boolean isMatch(NumHandler numHandler) {
                AbstractHandler abstractHandler = AbstractHandler.get(numHandler.getHandler());
                return Objects.equals(abstractHandler.taskType(), TaskType.CLUSTER_TYPE);
            }

            @Override
            public void hook(NumHandler numHandler) {
                if (!fistNodeFlag.get()) {
                    fistNodeFlag.set(true);
                    int[] arr = ArrayUtils.isEmpty(firstNodePrev) ? new int[]{prevNum} : firstNodePrev;
                    numHandler.setPrevExeNum(arr);
                }
                BusHandler nextBusHandler = BusHandler.of(numHandler, currBusHandler);
                addSubHandler(nextBusHandler, context, null);
            }
        });
    }

}
