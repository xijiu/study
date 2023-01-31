package com.lkn.dag;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lkn.dag.bean.NumHandler;
import com.lkn.dag.handlers.AbstractHandler;
import com.lkn.dag.bean.BusHandler;
import com.lkn.dag.handlers.Handler;
import com.lkn.dag.handlers.Node;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 打印有向无环图，对DAG的生成、执行不产生影响，且本类执行失败不影响后继逻辑
 *
 * @author xijiu
 * @since 2022/4/6 下午12:00
 */
@Slf4j
public class DAGPrinter {

    /** DAG图中，横向的2个节点间隔数量 */
    private static final int COL_GAP = 28;

    /** DAG图中，纵向的2个节点间隔数量 */
    private static final int LINE_GAP = 5;

    private static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(
            2, 50, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    /**
     * 打印一张DAG图，期间发生异常不影响程序的后续执行
     *
     * @param allHandlers   全部的DAG节点列表
     */
    public static void print(List<BusHandler> allHandlers) {
        try {
            // 输出全量handler的json形式
            printTotalHandlers(allHandlers);

            // 打印一张完整的DAG图
            Future<?> future = POOL.submit(() -> doPrint(allHandlers));
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("print DAG error ", e);
        }
    }

    /**
     * 打印最终生成的结果
     */
    private static void printTotalHandlers(List<BusHandler> allHandlers) {
        String instanceId = "";
        if (CollectionUtils.isNotEmpty(allHandlers)) {
            for (BusHandler busHandler : allHandlers) {
                Node node = busHandler.getNode();
                if (node != null && StringUtils.isNotEmpty(node.getInstanceId())) {
                    instanceId = node.getInstanceId();
                    break;
                }
            }
        }
        log.info("generated all handlers, instanceId is {}, total handlers are \n {}",
                instanceId,
                JSON.toJSONString(allHandlers, SerializerFeature.DisableCircularReferenceDetect));
    }

    private static void doPrint(List<BusHandler> handlerList) {
        handlerList = deepCopy(handlerList);
        printDAG(handlerList);
    }

    private static void printDAG(List<BusHandler> handlerList) {
        // 生成DAG的二维数组
        int[][] arr = tryGenericArr(handlerList);
        // 填充空行，即第一层与第二层之间的距离
        arr = fillEmptyLines(arr);
        // 在二维数组中画线，点与点直接的连线
        drawLine(arr, handlerList);
        // 添加左前缀空列
        arr = addPrevEmpty(arr);
        // 真正输出DAG图
        doPrintDAG(handlerList, arr);
    }

    /**
     * 添加左前缀空列
     */
    private static int[][] addPrevEmpty(int[][] arr) {
        int addWidth = 10;

        int newWidth = arr[0].length + addWidth;
        int[][] newArr = new int[arr.length][newWidth];
        for (int i = 0; i < arr.length; i++) {
            System.arraycopy(arr[i], 0, newArr[i], addWidth, arr[i].length);
        }
        return newArr;
    }

    /**
     * 画线，点与点之间填充-1
     */
    private static void drawLine(int[][] arr, List<BusHandler> handlerList) {
        Map<Integer, BusHandler> map = handlerList.stream()
                .collect(Collectors.toMap(b -> b.getNumHandler().getExeNum(), b -> b));
        Map<Integer, Pair<Integer, Integer>> positionMap = genericPositionMap(arr);
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (arr[i][j] > 0) {
                    drawForCurrNode(arr, arr[i][j], positionMap, map);
                }
            }
        }
    }

    /**
     * 为某个节点连线，可能这个节点连接了多个其他节点，一并画出
     *
     * @param arr   数组
     * @param num   当前节点的执行编号
     * @param positionMap   已经存储的节点在数组中的位置信息
     * @param numMap    num与BusHandler的映射关系
     */
    private static void drawForCurrNode(int[][] arr, int num, Map<Integer, Pair<Integer, Integer>> positionMap,
                                        Map<Integer, BusHandler> numMap) {
        List<BusHandler> allNextHandlers = numMap.values().stream()
                .filter(b -> b.getNumHandler().afterTargetHandler(num))
                .collect(Collectors.toList());
        Pair<Integer, Integer> startNode = positionMap.get(num);
        for (BusHandler nextHandler : allNextHandlers) {
            Pair<Integer, Integer> endNode = positionMap.get(nextHandler.getNumHandler().getExeNum());
            drawSingleLine(arr, startNode, endNode);
        }
    }

    /**
     * 将开始节点与结束节点的中间部分填充线条
     *
     * @param arr   载体数组
     * @param startNode 开始节点
     * @param endNode   结束节点
     */
    private static void drawSingleLine(int[][] arr, Pair<Integer, Integer> startNode, Pair<Integer, Integer> endNode) {
        Integer beginX = startNode.getKey();
        Integer beginY = startNode.getValue();
        Integer endX = endNode.getKey();
        Integer endY = endNode.getValue();
        double perSize = (double) (endY - beginY) / (Math.abs(endX - beginX));
        int colNum = 1;
        for (int i = beginX + 1; i < endX; i++) {
            int col = (int) (perSize * colNum + beginY);
            if (arr[i][col] == 0) {
                arr[i][col] = -1;
            }
            colNum++;
        }
    }

    /**
     * 填充空行，即2个level之间相隔的行数
     *
     * @param arr   载体数组
     * @return  新的载体数组
     */
    private static int[][] fillEmptyLines(int[][] arr) {
        int col = arr[0].length;
        int newLines = (arr.length - 1) * LINE_GAP + arr.length;
        int[][] newArr = new int[newLines][col];
        for (int i = 0; i < arr.length; i++) {
            System.arraycopy(arr[i], 0, newArr[i * (LINE_GAP + 1)], 0, col);
        }
        return newArr;
    }

    /**
     * 生成位置的映射map
     * key：exeNum   val: Pair<x, y>
     *
     * @param arr   载体数组
     * @return  映射map
     */
    private static Map<Integer, Pair<Integer, Integer>> genericPositionMap(int[][] arr) {
        Map<Integer, Pair<Integer, Integer>> positionMap = Maps.newHashMap();
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (arr[i][j] > 0) {
                    positionMap.put(arr[i][j], new Pair<>(i, j));
                }
            }
        }
        return positionMap;
    }

    /**
     * 输出DAG图
     *
     * @param handlerList   列表
     * @param arr   载体数组
     */
    private static void doPrintDAG(List<BusHandler> handlerList, int[][] arr) {
        Map<Integer, BusHandler> map = handlerList.stream()
                .collect(Collectors.toMap(b -> b.getNumHandler().getExeNum(), b -> b));
        String emptyNode = " ";
        String lineStr = "-";
        StringBuilder sb = new StringBuilder();
        for (int[] ints : arr) {
            int remain = 0;
            for (int exeNum : ints) {
                if (remain > 0) {
                    remain--;
                    continue;
                }
                if (exeNum == 0) {
                    sb.append(emptyNode);
                } else if (exeNum == -1) {
                    sb.append(lineStr);
                } else {
                    String content = toStr(map.get(exeNum));
                    remain = modifyStringBuilder(sb, content);
                }
            }
            sb.append("\n");
        }
        String DAGStr = sb.toString();
        double size = DAGStr.length() * 4D / 1024 / 1024;
        BigDecimal two = new BigDecimal(size);
        size = two.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        log.info("doPrintDAG size is {}M, result is \n{}", size, DAGStr);
    }

    /**
     * 如果字符串长度超过1（大部分都会超过），那么调整StringBuilder前后的内容
     *
     * @param sb    字符串
     * @param content   要拼接的内容
     * @return  剩余拼接的长度
     */
    private static int modifyStringBuilder(StringBuilder sb, String content) {
        int prevNum = content.length() / 2;
        sb.delete(sb.length() - prevNum, sb.length());
        sb.append(content);
        return content.length() - prevNum - 1;
    }

    /**
     * 将BusHandler转换为字符串，形式为：
     * KAFKA_SERVICE_START(101)
     *
     * @param busHandler    目标对象
     * @return  字符串形式
     */
    private static String toStr(BusHandler busHandler) {
        StringBuilder sb = new StringBuilder();
        Handler handler = busHandler.getNumHandler().getHandler();
        sb.append(handler.alias == null ? handler : handler.alias);
        if (AbstractHandler.get(handler).isNodeType()) {
            Node node = busHandler.getNode();
            String nodeIndexStr = node == null ? "" : String.valueOf(node.getNodeIndex());
            sb.append("(").append(nodeIndexStr).append(")");
        }
        if (busHandler.isPartTail()) {
            sb.append("_#T");
        }
        return sb.toString();
    }

    /**
     * 核心方法，生成DAG图的数组，分3个步骤：
     * 1、找到整个DAG图中最宽的位置，同时找到DAG图的高度，然后将最宽的一行填充
     * 2、向上依次填充
     * 3、向下依次填充
     *
     * @param handlerList   目标列表
     * @return  数组载体
     */
    private static int[][] tryGenericArr(List<BusHandler> handlerList) {
        int height = findDAGHeight(handlerList);
        Map<Integer, List<Integer>> levelExeNumMap = findDAGLevelAndNumMap(handlerList);
        Pair<Integer, Integer> levelAndWidth = calculateMaxWidth(levelExeNumMap);
        Integer maxWidthLevel = levelAndWidth.getKey();
        Integer maxWidth = levelAndWidth.getValue();
        Map<Integer, BusHandler> numMap = handlerList.stream()
                .collect(Collectors.toMap(b -> b.getNumHandler().getExeNum(), b -> b));

        Map<Integer, Pair<Integer, Integer>> positionMap = Maps.newHashMap();
        int[][] arr = new int[height][(maxWidth + 2) * COL_GAP];

        fillMaxWidth(maxWidthLevel, arr, levelExeNumMap.get(maxWidthLevel), positionMap);
        List<NumHandler> numHandlers = handlerList.stream().map(BusHandler::getNumHandler).collect(Collectors.toList());
        for (int level = maxWidthLevel - 1; level > 0; level--) {
            fillUpper(level, arr, levelExeNumMap.get(level), positionMap, numHandlers, levelExeNumMap);
        }
        for (int level = maxWidthLevel + 1; level < height + 1; level++) {
            fillLower(level, arr, levelExeNumMap.get(level), positionMap, numMap, numHandlers, levelExeNumMap);
        }
        return arr;
    }

    /**
     * 向下填充
     */
    private static void fillLower(int level, int[][] arr, List<Integer> list,
                                  Map<Integer, Pair<Integer, Integer>> positionMap,
                                  Map<Integer, BusHandler> numMap, List<NumHandler> numHandlers,
                                  Map<Integer, List<Integer>> levelExeNumMap) {
        int x = level - 1;
        for (Integer exeNum : list) {
            BusHandler busHandler = numMap.get(exeNum);
            int[] prevExeNum = busHandler.getNumHandler().getPrevExeNum();
            int y = calculateYForLower(prevExeNum, exeNum, positionMap, numHandlers, level, levelExeNumMap);
            if (x < 0 || y < 0) {
                System.out.println("x : " + x);
                System.out.println("y : " + y);
            }
            if (arr[x][y] == 0) {
                arr[x][y] = exeNum;
            } else {
                throw new RuntimeException("generic DAG error");
            }
            positionMap.put(exeNum, new Pair<>(x, y));
        }
    }

    private static int calculateYForLower(int[] prevExeNumArr, Integer exeNum,
                                          Map<Integer, Pair<Integer, Integer>> positionMap,
                                          List<NumHandler> numHandlers, int level,
                                          Map<Integer, List<Integer>> levelExeNumMap) {
        if (prevExeNumArr.length > 1) {
            // 多前置节点
            return calculateYForLowerForMultiFather(prevExeNumArr, positionMap);
        } else {
            int prevNum = prevExeNumArr[0];
            List<NumHandler> nextNumHandlers = DAG.findAllNextNumHandler(prevNum, numHandlers);
            if (nextNumHandlers.size() == 1) {
                return calculateYForLowerForMultiFather(prevExeNumArr, positionMap);
            } else {
                int totalNum = nextNumHandlers.size();
                int index = findIndexFromNextNumHandlers(nextNumHandlers, exeNum);
                Integer prevY = positionMap.get(prevNum).getValue();
                int tmpY = index - totalNum / 2;
                if (totalNum % 2 == 0) {
                    if (tmpY >= 0) {
                        tmpY++;
                    }
                }
                int maxWidth = computeNodeWidthForLower(exeNum, levelExeNumMap, level, numHandlers);
                maxWidth = maxWidth % 2 == 0 ? maxWidth + 1 : maxWidth;
                tmpY = prevY + tmpY * COL_GAP * maxWidth;
                return Math.max(tmpY, 0);
            }
        }
    }


    /**
     *
     *                        A1
     *                        -
     *                 -      -         -
     *          -             -              -
     *        A2              A3              A4
     *        -               -               -
     *     -  -  -         -  -  -         -  -  -
     *    -   -   -       -   -   -       -   -   -
     *   A5  A6   A7     A8   A9  A10    A11  A12  A13
     *
     * 假如现在A1的位置已经固定，要生成A2的位置
     * 1、如果A2没有子节点，那么直接将A2放在A1左边1个宽度即可
     * 2、如果A2现在有3个子节点，那么A2就不能举例A1太近，否则A5、A6、A7势必与A8、A9、A10位置有冲突，因此我们需要提前计算出A2
     * 可能的位置，从而为后继节点预留空间
     *
     *
     * @param exeNum    目标节点
     * @param levelExeNumMap    层级map
     * @param level 当前DAG图的LEVEL
     * @param numHandlers   所有的handler列表
     * @return  当前节点需要预留的宽度
     */
    private static int computeNodeWidthForLower(Integer exeNum, Map<Integer, List<Integer>> levelExeNumMap,
                                                int level, List<NumHandler> numHandlers) {
        Integer maxLevel = levelExeNumMap.keySet().stream().max(Integer::compareTo).orElse(null);
        assert maxLevel != null;
        int width = 1;
        for (int i = level + 1; i <= maxLevel; i++) {
            List<Integer> levelExeNumList = levelExeNumMap.get(i);
            if (levelExeNumList.size() == 1) {
                break;
            }
            int tmpCount = 0;
            for (Integer levelExeNum : levelExeNumList) {
                if (DAG.isTwoExeNumOrdered(numHandlers, exeNum, levelExeNum)) {
                    tmpCount++;
                }
            }
            width = Math.max(tmpCount, width);
        }
        return width;
    }

    private static int findIndexFromNextNumHandlers(List<NumHandler> nextNumHandlers, Integer exeNum) {
        for (int i = 0; i < nextNumHandlers.size(); i++) {
            if (nextNumHandlers.get(i).getExeNum() == exeNum) {
                return i;
            }
        }
        throw new RuntimeException("findIndexFromNextNumHandlers error");
    }

    private static int calculateYForLowerForMultiFather(int[] prevExeNum,
                                                        Map<Integer, Pair<Integer, Integer>> positionMap) {
        int minY = Integer.MAX_VALUE;
        int maxY = 0;
        for (int prevNum : prevExeNum) {
            Pair<Integer, Integer> pair = positionMap.get(prevNum);
            Integer y = pair.getValue();
            minY = Math.min(y, minY);
            maxY = Math.max(y, maxY);
        }
        return minY + (maxY - minY) / 2;
    }

    private static void fillUpper(int level, int[][] arr, List<Integer> list,
                                  Map<Integer, Pair<Integer, Integer>> positionMap, List<NumHandler> numHandlers,
                                  Map<Integer, List<Integer>> levelExeNumMap) {
        int x = level - 1;
        for (Integer exeNum : list) {
            int y = calculateYForUpper(exeNum, positionMap, numHandlers, levelExeNumMap, level);
            if (arr[x][y] == 0) {
                arr[x][y] = exeNum;
            } else {
                throw new RuntimeException("generic DAG error, position repeat");
            }
            positionMap.put(exeNum, new Pair<>(x, y));
        }
    }

    private static int calculateYForUpper(Integer exeNum, Map<Integer, Pair<Integer, Integer>> positionMap,
                                          List<NumHandler> numHandlers, Map<Integer, List<Integer>> levelExeNumMap, int level) {
        List<NumHandler> allNextNumHandler = DAG.findAllNextNumHandler(exeNum, numHandlers);
        if (allNextNumHandler.size() > 1) {
            return calculateYForUpper(allNextNumHandler, positionMap);
        } else {
            NumHandler nextNumHandler = allNextNumHandler.get(0);
            int[] prevExeNum = nextNumHandler.getPrevExeNum();
            if (prevExeNum.length == 1) {
                return positionMap.get(nextNumHandler.getExeNum()).getValue();
            }

            int totalNum = prevExeNum.length;
            int index = 0;
            for (int i = 0; i < totalNum; i++) {
                if (prevExeNum[i] == exeNum) {
                    index = i;
                }
            }
            Integer prevY = positionMap.get(nextNumHandler.getExeNum()).getValue();
            int tmpY = index - totalNum / 2;
            if (totalNum % 2 == 0) {
                if (tmpY >= 0) {
                    tmpY++;
                }
            }
            int maxWidth = computeNodeWidthForUpper(exeNum, levelExeNumMap, level, numHandlers);
            maxWidth = maxWidth % 2 == 0 ? maxWidth + 1 : maxWidth;
            tmpY = prevY + tmpY * COL_GAP * maxWidth;
            return Math.max(tmpY, 0);
        }
    }

    private static int computeNodeWidthForUpper(Integer exeNum, Map<Integer, List<Integer>> levelExeNumMap,
                                                int level, List<NumHandler> numHandlers) {
        int width = 1;
        for (int i = level - 1; i >= 0; i--) {
            List<Integer> levelExeNumList = levelExeNumMap.get(i);
            if (levelExeNumList.size() == 1) {
                break;
            }
            int tmpCount = 0;
            for (Integer levelExeNum : levelExeNumList) {
                if (DAG.isTwoExeNumOrdered(numHandlers, levelExeNum, exeNum)) {
                    tmpCount++;
                }
            }
            width = Math.max(tmpCount, width);
        }
        return width;
    }

    private static int calculateYForUpper(List<NumHandler> allNextNumHandler,
                                          Map<Integer, Pair<Integer, Integer>> positionMap) {
        int minY = Integer.MAX_VALUE;
        int maxY = 0;
        for (NumHandler numHandler : allNextNumHandler) {
            Pair<Integer, Integer> pair = positionMap.get(numHandler.getExeNum());
            if (pair == null) {
                throw new RuntimeException();
            }
            Integer y = pair.getValue();
            minY = Math.min(y, minY);
            maxY = Math.max(y, maxY);
        }
        return minY + (maxY - minY) / 2;
    }

    /**
     * 填充最宽的一列
     */
    private static void fillMaxWidth(Integer maxWidthLevel, int[][] arr,
                                     List<Integer> list, Map<Integer, Pair<Integer, Integer>> positionMap) {
        int x = maxWidthLevel - 1;
        for (int i = 0; i < list.size(); i++) {
            Integer exeNum = list.get(i);
            int y = (i + 1) * COL_GAP;
            arr[x][y] = exeNum;
            positionMap.put(exeNum, new Pair<>(x, y));
        }
    }

    /**
     * 计算最大宽度
     *
     * @param levelExeNumMap    level与每一层级的执行编号列表
     * @return  level与最大宽度
     */
    private static Pair<Integer, Integer> calculateMaxWidth(Map<Integer, List<Integer>> levelExeNumMap) {
        int level = 0;
        int maxWidth = 0;
        for (Map.Entry<Integer, List<Integer>> entry : levelExeNumMap.entrySet()) {
            List<Integer> set = entry.getValue();
            if (set.size() > maxWidth) {
                level = entry.getKey();
                maxWidth = set.size();
            }
        }
        return new Pair<>(level, maxWidth);
    }


    /**
     * 计算DAG图的高度
     *
     * @param handlerList   节点列表
     * @return  高度
     */
    private static int findDAGHeight(List<BusHandler> handlerList) {
        AtomicInteger height = new AtomicInteger(0);
        DAG.breadthFirstSearch(-1, handlerList,
                (busHandler, level) -> height.set(Math.max(height.get(), level)));
        return height.get();
    }

    /**
     * 找到DAG每一行的数量
     *
     * @param handlerList   所有节点列表
     * @return  每一行与数量的map
     */
    private static Map<Integer, List<Integer>> findDAGLevelAndNumMap(List<BusHandler> handlerList) {
        Map<Integer, List<Integer>> widthMap = Maps.newHashMap();
        DAG.breadthFirstSearch(-1, handlerList, (busHandler, level) -> {
            List<Integer> set = widthMap.computeIfAbsent(level, k -> Lists.newArrayList());
            set.add(busHandler.getNumHandler().getExeNum());
        });
        return widthMap;
    }

    /**
     * 深度拷贝
     *
     * @param handlerList   目标列表
     * @return  拷贝列表
     */
    private static List<BusHandler> deepCopy(List<BusHandler> handlerList) {
        String jsonStr = JSON.toJSONString(handlerList, SerializerFeature.DisableCircularReferenceDetect);
        return JSON.parseObject(jsonStr, new TypeReference<List<BusHandler>>() {});
    }

}
