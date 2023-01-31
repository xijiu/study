package dag;


import dag.bean.BusHandler;
import dag.bean.NumHandler;
import dag.handlers.Context;
import dag.handlers.Handler;
import dag.handlers.Tools;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DAG图的转换器，主要做两件事儿：
 * 1、原DAG图为一张嵌套图，即大DAG中嵌套了小DAG，本类将其转换为一张统一的大DAG图
 * 2、原DAG图中的复合节点（例如 Handler#EIP）在转换为大DAG图的时候，会缺少结束节点，统一补齐
 *
 * @author xijiu
 * @since 2022/4/15 上午8:13
 */
@Component
public class DAGTransfer {

    public void transfer(Context context) {
        // 转换为一张大DAG图
        makeExeNumUnique(context.getWholeHandlers(), -1);
        // 处理嵌套DAG
        operateCompositeNode(context.getWholeHandlers(), context.getMajorBusHandler());
    }


    /**
     * 处理复合节点
     * 1、在复合节点尾部添加结束节点
     * 2、修改复合节点的root节点，使其融合入大DAG图中
     *
     * @param handlerList   全部列表
     * @param targetHandler 目标执行器
     */
    private void operateCompositeNode(List<BusHandler> handlerList, BusHandler targetHandler) {
        List<BusHandler> sonHandlers = findSonHandlers(handlerList, targetHandler.getSeq());
        if (CollectionUtils.isEmpty(sonHandlers)) {
            return;
        }
        // 处理嵌套DAG的尾部
        operateTail(handlerList, sonHandlers, targetHandler);
        // 处理嵌套DAG的头部
        updateHead(sonHandlers, targetHandler);
        for (BusHandler sonHandler : sonHandlers) {
            operateCompositeNode(handlerList, sonHandler);
        }
    }

    /**
     * 处理头部，主要将曾经prev num为-1的节点修改为父节点真正的exeNum
     *
     * @param sonHandlers   所有的儿子节点
     * @param fatherHandler 父节点
     */
    private void updateHead(List<BusHandler> sonHandlers, BusHandler fatherHandler) {
        List<BusHandler> busHandlers = sonHandlers.stream().filter(this::isRoot).collect(Collectors.toList());
        for (BusHandler busHandler : busHandlers) {
            NumHandler numHandler = busHandler.getNumHandler();
            numHandler.setPrevExeNum(new int[] {fatherHandler.getNumHandler().getExeNum()});
        }
    }

    private boolean isRoot(BusHandler busHandler) {
        int[] prevExeNum = busHandler.getNumHandler().getPrevExeNum();
        return prevExeNum.length == 1 && prevExeNum[0] == -1;
    }

    /**
     * 处理嵌套DAG的尾部节点
     *
     * @param wholeList 全部list
     * @param sonHandlers   当前DAG的所有处理器
     * @param fatherHandler 父处理器
     */
    private void operateTail(List<BusHandler> wholeList, List<BusHandler> sonHandlers, BusHandler fatherHandler) {
        // 新建一个新的节点
        BusHandler tailHandler = createNewTailBusHandler(wholeList, fatherHandler, sonHandlers);
        // 将新节点放入全局handler list中
        addToWholeHandlerList(wholeList, sonHandlers, tailHandler);
        // 将新节点放入DAG图中
        addToWholeDAG(wholeList, fatherHandler, tailHandler);
    }

    /**
     * 将新节点加入大DAG图
     *
     * @param wholeList 全部list
     * @param fatherHandler 父节点
     * @param newHandler    新加入的节点
     */
    private void addToWholeDAG(List<BusHandler> wholeList, BusHandler fatherHandler, BusHandler newHandler) {
        int newExeNum = newHandler.getNumHandler().getExeNum();
        int fatherNum = fatherHandler.getNumHandler().getExeNum();
        List<NumHandler> numHandlers = wholeList.stream().map(BusHandler::getNumHandler).collect(Collectors.toList());
        List<NumHandler> allNextNumHandler = DAG.findAllNextNumHandler(fatherNum, numHandlers);
        for (NumHandler nextHandler : allNextNumHandler) {
            int[] prevExeNum = nextHandler.getPrevExeNum();
            for (int i = 0; i < prevExeNum.length; i++) {
                if (prevExeNum[i] == fatherNum) {
                    prevExeNum[i] = newExeNum;
                }
            }
        }
    }

    /**
     * 将新建的执行器加入整个whole列表对应的位置
     *
     * @param wholeList 整体执行器列表
     * @param sonHandlers   同层级执行器列表
     * @param newHandler    新建的执行器
     */
    private void addToWholeHandlerList(List<BusHandler> wholeList, List<BusHandler> sonHandlers,
                                       BusHandler newHandler) {

        BusHandler busHandler = sonHandlers.get(sonHandlers.size() - 1);
        int index = wholeList.indexOf(busHandler) + 1;
        wholeList.add(index, newHandler);
    }

    /**
     * 创建一个新的节点执行器
     *
     * @param handlerList   所有列表
     * @param fatherHandler 父执行器
     * @param sonHandlers   子执行器
     * @return  新建的执行器
     */
    private BusHandler createNewTailBusHandler(List<BusHandler> handlerList, BusHandler fatherHandler,
                                               List<BusHandler> sonHandlers) {
        BusHandler tailHandler;
        List<BusHandler> leafNodes = DAG.findLeafNode(sonHandlers);
        Set<Integer> numSet = leafNodes.stream().map(b -> b.getNumHandler().getExeNum()).collect(Collectors.toSet());
        int newExeNum = DAG.findMaxExeNum(handlerList) + 1;
        Handler handler = fatherHandler.getNumHandler().getHandler();
        NumHandler numHandler = NumHandler.of(newExeNum, handler, Tools.setToArr(numSet));
        if (fatherHandler.getHandler().isNodeType()) {
            tailHandler = BusHandler.of(numHandler, fatherHandler, fatherHandler.getNode());
        } else {
            tailHandler = BusHandler.of(numHandler, fatherHandler);
        }
        tailHandler.setPartTail(true);
        return tailHandler;
    }


    /**
     * 使大DAG图的执行编号唯一，原图中为嵌套结构，故存在可能不唯一的exeNum
     *
     * @param handlerList   全部的执行列表
     * @param fSeq  父节点的seq
     */
    private void makeExeNumUnique(List<BusHandler> handlerList, int fSeq) {
        List<BusHandler> sonHandlers = findSonHandlers(handlerList, fSeq);
        if (CollectionUtils.isEmpty(sonHandlers)) {
            return;
        }
        List<NumHandler> numHandlers = sonHandlers.stream()
                .map(BusHandler::getNumHandler).collect(Collectors.toList());
        int maxExeNum = DAG.findMaxExeNum(handlerList);
        // 改变所有节点的执行编号
        changeAllExeNum(numHandlers, maxExeNum + 1);
        for (BusHandler sonHandler : sonHandlers) {
            makeExeNumUnique(handlerList, sonHandler.getSeq());
        }
    }

    /**
     * 改变所有节点的执行编号
     */
    private static void changeAllExeNum(List<NumHandler> numHandlers, int beginNum) {
        for (NumHandler numHandler : numHandlers) {
            DAG.changeExeNum(numHandlers, numHandler, beginNum++);
        }
    }

    /**
     * 寻找儿子节点
     */
    private static List<BusHandler> findSonHandlers(List<BusHandler> allHandlerList, int fSeq) {
        return allHandlerList.stream().filter(h -> h.getFSeq() == fSeq).collect(Collectors.toList());
    }
}
