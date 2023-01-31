package com.lkn.dag.handlers;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.lkn.dag.bean.BusHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * deploy2的工具类，封装无状态的工具方法
 *
 * @author xijiu
 * @since 2022/3/21 上午9:44
 */
@Slf4j
public class Tools {
    public static String json(Object o) {
        return JSON.toJSONString(o);
    }

    /**
     * 等待目标future执行完毕
     *
     * @param future    目标future
     * @param <T>   结果类型
     * @return  结果
     */
    public static <T> T sync(Future<T> future) {
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断数组中是否包含目标元素
     *
     * @param arr   数组
     * @param target    目标值
     * @return  是否包含
     */
    public static boolean contains(int[] arr, int target) {
        for (int ele : arr) {
            if (ele == target) {
                return true;
            }
        }
        return false;
    }

    /**
     * 当前线程睡眠一段时间，单位ms，不抛出编译器异常
     *
     * @param time  睡眠时间
     */
    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 数组转换为集合
     *
     * @param arr   数组
     * @return  集合
     */
    public static Set<Integer> arrToSet(int[] arr) {
        Set<Integer> result = Sets.newHashSet();
        for (int i : arr) {
            result.add(i);
        }
        return result;
    }

    /**
     * 集合转换为数组
     *
     * @param set   集合
     * @return  数组
     */
    public static int[] setToArr(Set<Integer> set) {
        int[] arr = new int[set.size()];
        int i = 0;
        for (Integer index : set) {
            arr[i++] = index;
        }
        return arr;
    }

    /**
     * 数组的深度拷贝
     *
     * @param targetArr 目标数组
     * @return  深度拷贝后的数组
     */
    public static int[] arrDeepCopy(int[] targetArr) {
        int[] resultArr = new int[targetArr.length];
        System.arraycopy(targetArr, 0, resultArr, 0, targetArr.length);
        return resultArr;
    }

    /**
     * 将数组添加至集合中
     *
     * @param target    集合
     * @param arr   数组
     */
    public static void addArrToCollection(Collection<Integer> target, int[] arr) {
        if (ArrayUtils.isNotEmpty(arr)) {
            for (int num : arr) {
                target.add(num);
            }
        }
    }

    /**
     * 修改数组元素，将原来是currVal的值替换为newVal
     *
     * @param targetArr 数组
     * @param currVal   原元素内容
     * @param newVal    要替换的新元素内容
     */
    public static void modifyArr(int[] targetArr, int currVal, int newVal) {
        for (int i = 0; i < targetArr.length; i++) {
            if (targetArr[i] == currVal) {
                targetArr[i] = newVal;
            }
        }
    }

    /**
     * 判断集合中是否完全包含数组的内容
     *
     * @param target    集合
     * @param arr   数组
     * @return  是否包含
     */
    public static boolean containsArr(Collection<Integer> target, int[] arr) {
        if (ArrayUtils.isNotEmpty(arr)) {
            for (int num : arr) {
                if (!target.contains(num)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断当前major处理器是否为新建集群操作
     *
     * @param handler    目标handler
     * @return  是否为新建操作
     */
    public static boolean isClusterCreate(Handler handler) {
        return Objects.equals(handler, Handler.CLUSTER_CREATE);
    }

    /**
     * 判断当前major处理器是否为修改集群操作
     *
     * @param busHandler    目标handler
     * @return  是否为修改操作
     */
    public static boolean isClusterUpdate(BusHandler busHandler) {
        Handler handler = busHandler.getHandler().handler();
        return Objects.equals(handler, Handler.CLUSTER_UPDATE);
    }

    /**
     * 判断当前major处理器是否为修改集群操作
     *
     * @param handler    目标handler
     * @return  是否为修改操作
     */
    public static boolean isClusterUpdate(Handler handler) {
        return Objects.equals(handler, Handler.CLUSTER_UPDATE);
    }

    /**
     * 将类似"4G"转换为数字"4"
     *
     * @param memoryStr 内存字符
     * @return  数字
     */
    public static int memoryToInt(String memoryStr) {
        if (Strings.isNullOrEmpty(memoryStr)) {
            throw new RuntimeException("memory is empty");
        }
        if (memoryStr.endsWith("G") || memoryStr.endsWith("g")) {
            memoryStr = memoryStr.substring(0, memoryStr.length() - 1);
        }
        return Integer.parseInt(memoryStr);
    }

    /**
     * 重试方法，默认每次重试休息1秒
     *
     * @param retry 重试次数
     * @param runner    承载器，用来传递逻辑方法
     * @return  最终是否成功
     */
    public static boolean retryExe(int retry, Runner runner) {
        return retryExe(retry, -1, runner);
    }

    /**
     * 重试方法
     *
     * @param retry 重试次数
     * @param sleepSeconds  失败后休息时长
     * @param runner    承载器，用来传递逻辑方法
     * @return  最终是否成功
     */
    public static boolean retryExe(int retry, int sleepSeconds, Runner runner) {
        return retryExe(retry, sleepSeconds, true, runner);
    }

    /**
     * 重试方法
     *
     * @param retry 重试次数
     * @param sleepSeconds  失败后休息时长
     * @param ignoreException   是否忽略执行过程遇到的异常
     * @param runner    承载器，用来传递逻辑方法
     * @return  最终是否成功
     */
    public static boolean retryExe(int retry, int sleepSeconds, boolean ignoreException, Runner runner) {
        return retryExe(retry, sleepSeconds, -1, ignoreException, runner);
    }

    /**
     * 重复执行器某个动作
     *
     * @param retry 重复次数，如果传入负数或0，则代表一直执行
     * @param sleepSeconds  每次睡眠时间，单位秒
     * @param maximumExeSeconds 最长时间时长，如果超过该时长，直接返回false
     * @param ignoreException   是否忽略异常
     * @param runner    载体，存放业务方法
     * @return  是否执行成功
     */
    public static boolean retryExe(int retry, int sleepSeconds, int maximumExeSeconds,
                                   boolean ignoreException, Runner runner) {
        long startTime = System.currentTimeMillis();
        long exeTime = retry <= 0 ? Long.MAX_VALUE : retry;
        for (long i = 0; i < exeTime; i++) {
            boolean result = false;
            try {
                result = runner.exe();
            } catch (Exception e) {
                if (!ignoreException) {
                    throw new RuntimeException(e);
                }
                runner.occurException(i + 1, e);
            }
            if (result) {
                return true;
            } else {
                if (maximumExeSeconds > 0) {
                    // 当前方法执行超时
                    if (System.currentTimeMillis() - startTime > maximumExeSeconds * 1000) {
                        return false;
                    }
                }
                int sleepTime = sleepSeconds > 0 ? sleepSeconds : runner.sleepSeconds(i);
                sleepTime = Math.max(1, sleepTime);
                sleepSecond(sleepTime);
            }
        }
        return false;
    }

    private static void sleepSecond(int sleepTime) {
        try {
            Thread.sleep(sleepTime * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void deleteLastChar(StringBuilder sb) {
        sb.deleteCharAt(sb.length() - 1);
    }

    /**
     * 接口，用来传递方法
     */
    @FunctionalInterface
    public interface Runner {
        boolean exe() throws Exception;
        default int sleepSeconds(long exeIndex) {
            return -1;
        }
        default void occurException(long retryTime, Exception e) {}
    }

    /**
     * 从上下文中提取实例id
     *
     * @param context   上下文
     * @return  实例id
     */
    public static String extractInstanceId(Context context) {
        String instanceId = context.getInstanceId();
        if (StringUtils.isNotEmpty(instanceId)) {
            return instanceId;
        }
        if (context.getNewConfig() != null) {
            instanceId = context.getNewConfig().getInstanceId();
            if (StringUtils.isNotEmpty(instanceId)) {
                return instanceId;
            }
        }
        if (context.getCurrConfig() != null) {
            instanceId = context.getCurrConfig().getInstanceId();
            if (StringUtils.isNotEmpty(instanceId)) {
                return instanceId;
            }
        }
        return null;
    }

    public static boolean isTrue(Boolean val) {
        return val != null && val;
    }

    /**
     * 判断入参中是否有null值
     *
     * @param obj   入参
     */
    public static boolean existNull(Object... obj) {
        if (obj == null) {
            return true;
        }
        for (Object o : obj) {
            if (o == null) {
                return true;
            }
        }
        return false;
    }
}
