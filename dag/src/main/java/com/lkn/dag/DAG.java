package com.lkn.dag;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lkn.dag.bean.NumHandler;
import com.lkn.dag.handlers.Tools;
import com.lkn.dag.bean.BusHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 封装DAG相关操作，均为static方法
 * 无状态，只提供DAG的计算服务
 *
 * @author xijiu
 * @since 2022/4/6 下午4:32
 */
@Slf4j
public class DAG {

    /**
     * 重新构造DAG图，目标handler中的内容可不能不是最终版
     *
     * @param totalHandler  全部handler
     * @param validNumSet   有效的num
     * @return  新构建的handlers
     */
    public static NumHandler[] rebuildDAG(NumHandler[] totalHandler, Set<Integer> validNumSet) {
        List<NumHandler> totalList = deepCopy(totalHandler);
        LinkedList<Integer> assistList = new LinkedList<>();
        assistList.add(-1);
        doRebuildDAG(totalList, assistList, validNumSet);
        return totalList.toArray(new NumHandler[validNumSet.size()]);
    }

    /**
     * 递归方法，通过广度遍历列表实现重新构建的目标
     *
     * @param totalList 全部的handlers
     * @param fatherList    父节点的列表，辅助类
     * @param validSet  有效节点的编号
     */
    private static void doRebuildDAG(List<NumHandler> totalList,
                                     LinkedList<Integer> fatherList, Set<Integer> validSet) {
        if (CollectionUtils.isEmpty(fatherList)) {
            return;
        }
        Integer fatherNum = fatherList.removeFirst();
        Iterator<NumHandler> iterator = totalList.iterator();
        while (iterator.hasNext()) {
            NumHandler numHandler = iterator.next();
            if (isSonHandler(numHandler, fatherNum)) {
                if (validSet.contains(numHandler.getExeNum())) {
                    fatherList.add(numHandler.getExeNum());
                } else {
                    List<NumHandler> sonList = findSonHandlers(totalList, numHandler);
                    removeNumHandler(numHandler, sonList, totalList);
                    if (CollectionUtils.isNotEmpty(sonList)) {
                        fatherList.addAll(sonList.stream().map(NumHandler::getExeNum).collect(Collectors.toList()));
                    }
                    iterator.remove();
                }
            }
        }
        doRebuildDAG(totalList, fatherList, validSet);
    }

    /**
     * 在依赖关系上删除某个节点，分两种情况
     * 情况一：
     *               A1
     *               -
     *               -
     *               A2
     *               -
     *               -
     *               A3
     * 如果删除A2，那么需要将A3的前置节点设置为A1
     *
     *
     * 情况二：
     *               B1
     *               -
     *           -   -   -
     *          -    -     -
     *        B2     B3     B4
     *          -    -     -
     *           -   -   -
     *               -
     *               B5
     *  如果要删除B4，因为B1可以通过B2、B3到达B5，所以此时不需要将B1添加进B5的前置节点池中
     *
     * @param toBeDeleteNumHandler    要删除的节点
     * @param sonList   儿子节点
     */
    private static void removeNumHandler(NumHandler toBeDeleteNumHandler, List<NumHandler> sonList,
                                         List<NumHandler> totalList) {

        NumHandler[] allNumHandlers = totalList.toArray(new NumHandler[0]);
        int toBeDeleteExeNum = toBeDeleteNumHandler.getExeNum();
        int[] fExeNumArr = toBeDeleteNumHandler.getPrevExeNum();

        for (NumHandler sonHandler : sonList) {
            Set<Integer> set = Tools.arrToSet(sonHandler.getPrevExeNum());
            set.remove(toBeDeleteNumHandler.getExeNum());
            for (int fExeNum : fExeNumArr) {
                if (!isExistRelationship(fExeNum, sonHandler.getExeNum(), toBeDeleteExeNum, allNumHandlers)) {
                    set.add(fExeNum);
                }
            }
            sonHandler.setPrevExeNum(Tools.setToArr(set));
        }
    }

    /**
     * 两个节点是否存在前后关系，即from节点是否可以不通过delete节点到达to节点，不论是直接还是间接
     *
     * @param fromExeNum  前节点编号
     * @param toExeNum  后节点编号
     * @param deleteExeNum  不经过该节点
     * @param numHandlers    全部节点
     * @return  是否存在前后关系
     */
    private static boolean isExistRelationship(int fromExeNum, int toExeNum, int deleteExeNum,
                                               NumHandler[] numHandlers) {
        Set<Integer> fatherSet = Sets.newHashSet(fromExeNum);
        DAG.breadthFirstSearch(-1, numHandlers, new NumHandlerHook() {
            @Override
            public boolean isMatch(NumHandler numHandler) {
                return deleteExeNum != numHandler.getExeNum();
            }

            @Override
            public void hook(NumHandler numHandler) {
                int[] prevExeNum = numHandler.getPrevExeNum();
                for (int i : prevExeNum) {
                    if (fatherSet.contains(i)) {
                        fatherSet.add(numHandler.getExeNum());
                        return;
                    }
                }
            }
        });
        return fatherSet.contains(toExeNum);
    }

    /**
     * 寻找目标节点的所有儿子节点
     *
     * @param totalList 全部节点
     * @param fatherHandler 父节点
     * @return  所有儿子节点
     */
    private static List<NumHandler> findSonHandlers(List<NumHandler> totalList, NumHandler fatherHandler) {
        List<NumHandler> sonList = Lists.newArrayList();
        for (NumHandler numHandler : totalList) {
            if (isSonHandler(numHandler, fatherHandler.getExeNum())) {
                sonList.add(numHandler);
            }
        }
        return sonList;
    }

    /**
     * 当前handler是否为目标handler的儿子节点
     *
     * @param numHandler    当前handler
     * @param fatherNum 父节点的num
     * @return  是否为儿子节点
     */
    private static boolean isSonHandler(NumHandler numHandler, Integer fatherNum) {
        int[] fExeNum = numHandler.getPrevExeNum();
        for (int num : fExeNum) {
            if (num == fatherNum) {
                return true;
            }
        }
        return false;
    }

    /**
     * 深度拷贝，不污染入参
     *
     * @param totalHandler  全部handlers
     * @return  深度拷贝后的列表
     */
    private static List<NumHandler> deepCopy(NumHandler[] totalHandler) {
        List<NumHandler> totalList = Lists.newArrayList();
        for (NumHandler numHandler : totalHandler) {
            int[] copyArr = Tools.arrDeepCopy(numHandler.getPrevExeNum());
            totalList.add(NumHandler.of(numHandler.getExeNum(), numHandler.getHandler(), copyArr));
        }
        return totalList;
    }


    /**
     * 寻找DAG中的叶子节点
     *
     * @param busHandlers   所有数据
     * @return  叶子节点
     */
    public static List<BusHandler> findLeafNode(LinkedList<BusHandler> busHandlers, int fSeq) {
        List<BusHandler> leafList = Lists.newArrayList();
        Set<Integer> prevNumSet = findAllPrevNum(busHandlers, fSeq);
        for (BusHandler busHandler : busHandlers) {
            if (busHandler.getFSeq() != fSeq) {
                continue;
            }
            int exeNum = busHandler.getNumHandler().getExeNum();
            if (!prevNumSet.contains(exeNum)) {
                leafList.add(busHandler);
            }
        }
        return leafList;
    }

    private static Set<Integer> findAllPrevNum(LinkedList<BusHandler> busHandlers, int fSeq) {
        Set<Integer> resultSet = Sets.newHashSet();
        for (BusHandler busHandler : busHandlers) {
            if (busHandler.getFSeq() != fSeq) {
                continue;
            }
            int[] prevExeNum = busHandler.getNumHandler().getPrevExeNum();
            for (int num : prevExeNum) {
                resultSet.add(num);
            }
        }
        return resultSet;
    }

    /**
     * 寻找DAG中的根节点
     *
     * @param busHandlers   所有数据
     */
    public static List<BusHandler> findRootNode(Collection<BusHandler> busHandlers) {
        List<BusHandler> roots = Lists.newArrayList();
        Set<Integer> numSet = busHandlers.stream().map(h -> h.getNumHandler().getExeNum()).collect(Collectors.toSet());
        for (BusHandler busHandler : busHandlers) {
            int[] prevExeNum = busHandler.getNumHandler().getPrevExeNum();
            boolean flag = true;
            for (int prevNum : prevExeNum) {
                if (numSet.contains(prevNum)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                roots.add(busHandler);
            }
        }
        return roots;
    }

    /**
     * 寻找DAG中的叶子节点
     *
     * @param busHandlers   所有数据
     */
    public static List<BusHandler> findLeafNode(Collection<BusHandler> busHandlers) {
        return findLeafNode(busHandlers, null);
    }

    /**
     * 寻找DAG中的叶子节点
     *
     * @param busHandlers   所有数据
     * @param filter   过滤器
     */
    public static List<BusHandler> findLeafNode(Collection<BusHandler> busHandlers, BusHandlerFilter filter) {
        List<BusHandler> leafList = Lists.newArrayList();
        Set<Integer> prevNumSet = findAllPrevNum(busHandlers);
        for (BusHandler busHandler : busHandlers) {
            int exeNum = busHandler.getNumHandler().getExeNum();
            if (!prevNumSet.contains(exeNum)) {
                if (filter == null || filter.isMatch(busHandler)) {
                    leafList.add(busHandler);
                }
            }
        }
        return leafList;
    }

    /**
     * 将所有节点的前置节点收集起来
     *
     * @param busHandlers   所有节点
     * @return  前置节点集合
     */
    private static Set<Integer> findAllPrevNum(Collection<BusHandler> busHandlers) {
        Set<Integer> resultSet = Sets.newHashSet();
        for (BusHandler busHandler : busHandlers) {
            int[] prevExeNum = busHandler.getNumHandler().getPrevExeNum();
            Tools.addArrToCollection(resultSet, prevExeNum);
        }
        return resultSet;
    }

    /**
     * 寻找DAG中的叶子节点
     *
     * @param numHandlers   所有数据
     */
    public static List<NumHandler> findLeafNodeOfNumHandler(Collection<NumHandler> numHandlers) {
        List<NumHandler> leafList = Lists.newArrayList();
        Set<Integer> prevNumSet = Sets.newHashSet();
        for (NumHandler numHandler : numHandlers) {
            Tools.addArrToCollection(prevNumSet, numHandler.getPrevExeNum());
        }
        for (NumHandler numHandler : numHandlers) {
            if (!prevNumSet.contains(numHandler.getExeNum())) {
                leafList.add(numHandler);
            }
        }
        return leafList;
    }

    /**
     * 寻找当前DAG最大的编号
     *
     * @param busHandlers   节点集合
     * @return  最大执行号
     */
    public static int findMaxExeNum(Collection<BusHandler> busHandlers) {
        int maxNum = -1;
        for (BusHandler busHandler : busHandlers) {
            maxNum = Math.max(maxNum, busHandler.getNumHandler().getExeNum());
        }
        return maxNum;
    }

    /**
     * 寻找当前DAG最大的编号
     *
     * @param numHandlers   节点集合
     * @return  最大执行号
     */
    public static int findMaxExeNum(NumHandler[] numHandlers) {
        int maxNum = -1;
        for (NumHandler numHandler : numHandlers) {
            maxNum = Math.max(maxNum, numHandler.getExeNum());
        }
        return maxNum;
    }

    /**
     * 广度优先遍历DAG图
     *
     * @param prevNum   前置节点编号
     * @param busHandlers   全部节点信息
     * @param hook  回调类
     */
    public static void breadthFirstSearch(int prevNum, List<BusHandler> busHandlers, BusHandlerHook hook) {
        LinkedList<Pair<Integer, Integer>> assistList = Lists.newLinkedList();
        // 其中key是exeNum, val为当前节点在DAG图中的level层数
        assistList.add(Pair.of(prevNum, 0));
        Map<Integer, Integer> maxLevelMap = Maps.newHashMap();
        maxLevelMap.put(prevNum, -1);
        Map<Integer, BusHandler> exeNumAndHandlerMap = busHandlers.stream()
                .collect(Collectors.toMap(h -> h.getNumHandler().getExeNum(), h -> h));
        doBreadthFirstSearch(assistList, Sets.newHashSet(prevNum), maxLevelMap, exeNumAndHandlerMap, hook);
    }

    /**
     * 递归方法，广度优先遍历DAG图
     *
     * @param assistList    辅助列表
     * @param ensureUniqueSet   辅助集合，用来判重，避免DAG图收敛时，增加重复节点
     * @param exeNumAndHandlerMap   全部节点
     * @param hook  回调类
     */
    private static void doBreadthFirstSearch(LinkedList<Pair<Integer, Integer>> assistList,
                                             Set<Integer> ensureUniqueSet,
                                             Map<Integer, Integer> maxLevelMap,
                                             Map<Integer, BusHandler> exeNumAndHandlerMap, BusHandlerHook hook) {
        if (CollectionUtils.isEmpty(assistList)) {
            return;
        }
        Pair<Integer, Integer> prevPair = assistList.removeFirst();
        Integer currExeNum = prevPair.getKey();
        Integer level = prevPair.getValue();
        if (exeNumAndHandlerMap.containsKey(currExeNum)) {
            BusHandler busHandler = exeNumAndHandlerMap.get(currExeNum);
            hook.hook(busHandler, level);
        }
        ensureUniqueSet.add(currExeNum);
        List<BusHandler> nextHandlers = findNextBusHandlers(exeNumAndHandlerMap.values(), currExeNum);
        if (CollectionUtils.isEmpty(nextHandlers)) {
            return;
        }
        for (BusHandler nextHandler : nextHandlers) {
            int nextExeNum = nextHandler.getNumHandler().getExeNum();
            putIntoMap(maxLevelMap, nextExeNum, level + 1);
            int[] prevExeNumArr = nextHandler.getNumHandler().getPrevExeNum();
            if (ensureUniqueSet.contains(nextExeNum)) {
                continue;
            }
            // 说明当前节点的部分前置节点还未被遍历，所以暂时还不能添加当前节点
            if (!Tools.containsArr(ensureUniqueSet, prevExeNumArr)) {
                continue;
            }
            int maxLevel = Math.max(level + 1, maxLevelMap.getOrDefault(nextExeNum, -1));
            assistList.add(Pair.of(nextExeNum, maxLevel));
        }
        doBreadthFirstSearch(assistList, ensureUniqueSet, maxLevelMap, exeNumAndHandlerMap, hook);
    }

    private static void putIntoMap(Map<Integer, Integer> maxLevelMap, int nextExeNum, int newLevel) {
        if (maxLevelMap.containsKey(nextExeNum)) {
            int max = Math.max(maxLevelMap.get(nextExeNum), newLevel);
            maxLevelMap.put(nextExeNum, max);
        } else {
            maxLevelMap.put(nextExeNum, newLevel);
        }
    }

    /**
     * 广度优先遍历DAG图
     *
     * @param prevNum   前置节点编号
     * @param numHandlers   全部节点信息
     * @param hook  回调类
     */
    public static void breadthFirstSearch(int prevNum, NumHandler[] numHandlers, NumHandlerHook hook) {
        LinkedList<Integer> assistList = Lists.newLinkedList();
        assistList.add(prevNum);
        Set<Integer> ensureUniqueSet = Sets.newHashSet();
        doBreadthFirstSearch(assistList, ensureUniqueSet, numHandlers, hook);
    }

    /**
     * 递归方法，广度优先遍历DAG图
     *
     * @param assistList    辅助列表
     * @param ensureUniqueSet   辅助集合，用来判重，避免DAG图收敛时，增加重复节点
     * @param numHandlers   全部节点
     * @param hook  回调类
     */
    private static void doBreadthFirstSearch(LinkedList<Integer> assistList, Set<Integer> ensureUniqueSet,
                                             NumHandler[] numHandlers, NumHandlerHook hook) {
        if (CollectionUtils.isEmpty(assistList)) {
            return;
        }
        Integer prevNum = assistList.removeFirst();
        List<NumHandler> nextHandlers = findAllNextNumHandler(prevNum, numHandlers);
        if (CollectionUtils.isEmpty(nextHandlers)) {
            doBreadthFirstSearch(assistList, ensureUniqueSet, numHandlers, hook);
            return;
        }
        for (NumHandler nextHandler : nextHandlers) {
            if (!hook.isMatch(nextHandler)) {
                continue;
            }
            int nextExeNum = nextHandler.getExeNum();
            if (ensureUniqueSet.contains(nextExeNum)) {
                continue;
            }
            ensureUniqueSet.add(nextExeNum);
            assistList.add(nextExeNum);
            hook.hook(nextHandler);
        }
        doBreadthFirstSearch(assistList, ensureUniqueSet, numHandlers, hook);
    }

    /**
     * 寻找目标节点的所有后继节点
     *
     * @param prevNum   节点编号
     * @param numHandlers   全部节点
     * @return  全部后继节点
     */
    public static List<NumHandler> findAllNextNumHandler(Integer prevNum, NumHandler[] numHandlers) {
        return findAllNextNumHandler(prevNum, Arrays.asList(numHandlers));
    }

    /**
     * 寻找目标节点的所有后继节点
     *
     * @param exeNum   节点编号
     * @param numHandlers   全部节点
     * @return  全部后继节点
     */
    public static List<NumHandler> findAllNextNumHandler(Integer exeNum, Collection<NumHandler> numHandlers) {
        List<NumHandler> resultList = Lists.newArrayList();
        for (NumHandler numHandler : numHandlers) {
            if (numHandler.afterTargetHandler(exeNum)) {
                resultList.add(numHandler);
            }
        }
        return resultList;
    }

    /**
     * 修改指定节点的编号
     *
     * @param numHandlers   全部处理器
     * @param targetHandler 目标处理器
     * @param newNum    新值
     */
    public static void changeExeNum(NumHandler[] numHandlers, NumHandler targetHandler, int newNum) {
        changeExeNum(Arrays.asList(numHandlers), targetHandler, newNum);
    }

    /**
     * 修改指定节点的编号
     *
     * @param numHandlers   全部处理器
     * @param targetHandler 目标处理器
     * @param newNum    新值
     */
    public static void changeExeNum(List<NumHandler> numHandlers, NumHandler targetHandler, int newNum) {
        int currNum = targetHandler.getExeNum();
        List<NumHandler> nextHandlers = Lists.newArrayList();
        for (NumHandler numHandler : numHandlers) {
            if (numHandler.afterTargetHandler(currNum)) {
                nextHandlers.add(numHandler);
            }
        }
        targetHandler.setExeNum(newNum);
        for (NumHandler nextHandler : nextHandlers) {
            int[] prevExeNum = nextHandler.getPrevExeNum();
            Tools.modifyArr(prevExeNum, currNum, newNum);
        }
    }

    /**
     * 删除目标节点
     *
     * @param busHandlers   所有节点列表
     * @param deleteHandler 要删除的节点
     */
    public static void removeHandler(LinkedList<BusHandler> busHandlers, BusHandler deleteHandler) {
        if (deleteHandler == null) {
            return;
        }
        List<BusHandler> brothers = findAllBrothers(busHandlers, deleteHandler);
        int[] prevNum = deleteHandler.getNumHandler().getPrevExeNum();
        List<BusHandler> nextHandlers = findNextBusHandlers(brothers, deleteHandler);
        // 将目标节点从列表中剔除
        busHandlers.remove(deleteHandler);
        if (nextHandlers.size() == 0) {
            return;
        }
        for (BusHandler nextHandler : nextHandlers) {
            // 后继节点将已经删除的节点的prevNum添加进自己的数组中
            int[] prevExeNum = nextHandler.getNumHandler().getPrevExeNum();
            Set<Integer> resultSet = Tools.arrToSet(prevExeNum);
            resultSet.remove(deleteHandler.getNumHandler().getExeNum());
            Tools.addArrToCollection(resultSet, prevNum);
            nextHandler.getNumHandler().setPrevExeNum(Tools.setToArr(resultSet));
        }
    }

    /**
     * 删除目标节点
     *
     * @param deleteHandler 要删除的节点
     */
    public static NumHandler[] removeHandler(NumHandler[] allHandlers, NumHandler deleteHandler) {
        List<NumHandler> numHandlers = new ArrayList<>(Arrays.asList(allHandlers));
        int[] prevNum = deleteHandler.getPrevExeNum();
        List<NumHandler> nextHandlers = findAllNextNumHandler(deleteHandler.getExeNum(), allHandlers);
        // 将目标节点从列表中剔除
        numHandlers.removeIf(a -> a.getExeNum() == deleteHandler.getExeNum());
        if (nextHandlers.size() == 0) {
            NumHandler[] result = new NumHandler[numHandlers.size()];
            return numHandlers.toArray(result);
        }
        if (nextHandlers.size() > 1) {
            throw new RuntimeException("inner error");
        }
        // 后继节点将已经删除的节点的prevNum添加进自己的数组中
        NumHandler nextHandler = nextHandlers.get(0);
        int[] prevExeNum = nextHandler.getPrevExeNum();
        Set<Integer> resultSet = Tools.arrToSet(prevExeNum);
        Tools.addArrToCollection(resultSet, prevNum);
        nextHandler.setPrevExeNum(Tools.setToArr(resultSet));
        NumHandler[] result = new NumHandler[numHandlers.size()];
        return numHandlers.toArray(result);
    }

    /**
     * 寻找目标节点的所有后继节点
     *
     * @param handlers  列表
     * @param handler   目标节点
     * @return  所有后继节点
     */
    private static List<BusHandler> findNextBusHandlers(List<BusHandler> handlers, BusHandler handler) {
        return findNextBusHandlers(handlers, handler.getNumHandler().getExeNum());
    }

    /**
     * 寻找目标节点的所有后继节点
     *
     * @param handlers  列表
     * @param prevExeNum   执行编号
     * @return  所有后继节点
     */
    private static List<BusHandler> findNextBusHandlers(Collection<BusHandler> handlers, int prevExeNum) {
        List<BusHandler> nextHandlers = Lists.newArrayList();
        for (BusHandler busHandler : handlers) {
            if (busHandler.getNumHandler().afterTargetHandler(prevExeNum)) {
                nextHandlers.add(busHandler);
            }
        }
        return nextHandlers;
    }

    /**
     * 找到目标节点所有的兄弟节点
     *
     * @param busHandlers   所有节点
     * @param targetHandler 目标节点
     * @return  所有兄弟节点
     */
    private static List<BusHandler> findAllBrothers(List<BusHandler> busHandlers, BusHandler targetHandler) {
        List<BusHandler> resultList = Lists.newArrayList();
        int fSeq = targetHandler.getFSeq();
        for (BusHandler busHandler : busHandlers) {
            if (busHandler.getFSeq() == fSeq) {
                resultList.add(busHandler);
            }
        }
        return resultList;
    }

    /**
     * 判断两个节点是否有序，即 prevNum 的某个后续节点是 nextNum
     *
     * @param numHandlers   全量的handlers
     * @param prevNum   前置节点
     * @param nextNum   后置节点
     * @return  是否有序
     */
    public static boolean isTwoExeNumOrdered(List<NumHandler> numHandlers, int prevNum, int nextNum) {
        Map<Integer, NumHandler> map = numHandlers.stream().collect(Collectors.toMap(NumHandler::getExeNum, h -> h));

        LinkedList<Integer> queue = new LinkedList<>();
        queue.add(nextNum);
        while (queue.size() > 0) {
            Integer targetNum = queue.poll();
            NumHandler targetNumHandler = map.get(targetNum);
            int[] prevExeNum = targetNumHandler.getPrevExeNum();
            // 上溯到了根节点
            if (prevExeNum == null || (prevExeNum.length == 1 && prevExeNum[0] == -1)) {
                continue;
            }
            for (int exeNum : prevExeNum) {
                if (exeNum == prevNum) {
                    return true;
                } else {
                    queue.add(exeNum);
                }
            }
        }
        return false;
    }

    public static void findAboveHandlers(List<BusHandler> busHandlers, BusHandler targetHandler,
                                         BusHandlerHook busHandlerHook) {

        Map<Integer, BusHandler> map = busHandlers.stream().collect(Collectors.toMap(BusHandler::getSeq, b -> b));
        findAboveHandlers(map, targetHandler, busHandlerHook);
    }

    public static void findAboveHandlers(Map<Integer, BusHandler> busMap, BusHandler targetHandler,
                                         BusHandlerHook busHandlerHook) {

        while (targetHandler != null) {
            busHandlerHook.hook(targetHandler, -1);
            targetHandler = busMap.get(targetHandler.getFSeq());
        }
    }

    @FunctionalInterface
    public interface BusHandlerFilter {
        boolean isMatch(BusHandler busHandler);
    }


    public interface NumHandlerHook {
        boolean isMatch(NumHandler numHandler);
        void hook(NumHandler numHandler);
    }

    @FunctionalInterface
    public interface BusHandlerHook {
        void hook(BusHandler busHandler, int level);
    }
}
