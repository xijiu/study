package dag.handlers;

import com.google.common.collect.Maps;
import dag.DAG;
import dag.bean.BusHandler;
import dag.bean.NumHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

/**
 * 抽象处理器，用来约束与规范每个子处理器的行为
 *
 * @author xijiu
 * @since 2022/3/14 上午9:06
 */
@Slf4j
public abstract class AbstractHandler implements ApplicationContextAware {

    /** 操作spring上下文类 */
    private static ApplicationContext APPLICATION_CONTEXT;

    /** 存储所有的处理器 */
    private static final Map<Handler, AbstractHandler> HANDLER_MAP = Maps.newHashMap();

    /** 存储上下文 */
    private static final ThreadLocal<Context> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    /** 实例并发锁，主要用来控制高并发修改node表时，只能有一个线程在运行 */
    protected static final Map<String, Semaphore> INSTANCE_LOCK_MAP = new ConcurrentHashMap<>();

    /** 控制方法 {@link this#exactlyOnce(Context)} 在目标handler中只会被执行一次 */
    private static final Map<String, Map<Handler, Boolean>> EXACTLY_ONCE_MAP = new ConcurrentHashMap<>();

    @PostConstruct
    protected void init() {
        // 注：此处不可直接用 HANDLER_MAP.put(this)
        // 虽然这样目标类已经交由spring容器管理，且目标类中的属性已被增强后依赖注入
        // 但当前类在 @PostConstruct 环节只完成控制反转，未进行增强，故需要通过 ApplicationContext 获取其增强后的代理类
        HANDLER_MAP.put(handler(), getHandlerFromSpringContext());
    }

    /**
     * 拿到spring的引用对象
     * 因为handler之间可能存在继承的关系，单纯使用 {@code APPLICATION_CONTEXT.getBean()} 会报异常
     *
     * @return  当前handler在spring中的引用
     */
    private AbstractHandler getHandlerFromSpringContext() {
        try {
            return APPLICATION_CONTEXT.getBean(this.getClass());
        } catch (Exception e) {
            Map<String, ? extends AbstractHandler> map = APPLICATION_CONTEXT.getBeansOfType(this.getClass());
            Collection<? extends AbstractHandler> values = map.values();
            for (AbstractHandler value : values) {
                if (Objects.equals(value.getClass().getName(), this.getClass().getName())) {
                    return value;
                }
            }
        }
        throw new RuntimeException("getHandlerFromSpringContext exception");
    }

    /**
     * 根据enum获取业务处理器
     *
     * @param handler   统一enum配置
     * @return  处理器
     */
    public static AbstractHandler get(Handler handler) {
        return HANDLER_MAP.get(handler);
    }

    /**
     * 对应的enum
     *
     * @return  处理器的具体值
     */
    public abstract Handler handler();

    /**
     * 任务类型，参看 {@link TaskType}
     *
     * @return  任务类型
     */
    public abstract TaskType taskType();

    /**
     * 是否为集群执行器
     * @return  true：集群类型   false：node类型
     */
    public boolean isClusterType() {
        return Objects.equals(taskType(), TaskType.CLUSTER_TYPE);
    }

    /**
     * 当前处理器是否为节点类型
     *
     * @return  true：node类型   false：集群类型
     */
    public boolean isNodeType() {
        return Objects.equals(taskType(), TaskType.NODE_TYPE);
    }

    /**
     * 分区当前handler是否为主handler
     *
     * @return  true:主handler
     */
    public boolean isMajor() {
        return false;
    }

    /**
     * 每个major handler需要重写此方法，填充自己需要的上下文信息
     *
     * @param newConfig 传过来的配置信息
     * @param taskId 已经存在的任务ID，如果taskId不为空，那么走recover流程
     * @return  填充后的完整上下文
     */
    public Context fillContext(Config newConfig, String taskId) {
        return null;
    }

    /**
     * 前置检查，主要针对参数的检查，如果发现异常，快速失败
     *
     * @param context    上下文
     * @param action    动作
     * @return  前置检查是否通过
     */
    public ParamState preCheck(Context context, Action action) {
        return ParamState.VALID;
    }

    /**
     * 子任务是否根据节点粒度并行
     *
     * @return  true：需要并行执行子任务     false：串行执行
     */
    public boolean nodeParallel() {
        return false;
    }

    /**
     * 生成当前处理器的子流程
     * 优先执行子任务中的集群任务，等集群任务执行完毕后，再开始执行节点任务
     *
     * @return  子流程数组，返回null代表没有子流程
     */
    public NumHandler[] subProcess(Config currConfig, Config newConfig, Action action) {
        return null;
    }

    /**
     * 当前任务是否已执行
     *
     * @param context   上下文
     * @param node  当前节点
     * @return  true：已成功执行完毕    false：未执行或待重试
     */
    public boolean isExecuted(Context context, Node node) {
        return false;
    }

    /**
     * 核心方法，执行业务逻辑，节点任务类型时调用
     *
     * @param context  上下文
     * @param node  节点
     */
    public abstract void exeBusiness(Context context, Node node);

    /**
     * 当前任务是否已执行
     *
     * @param context   上下文
     * @return  true：已成功执行完毕    false：未执行或待重试
     */
    public boolean isExecuted(Context context) {
        return false;
    }

    /**
     * 标记handler无论在新建还是重试，都要重新再跑一遍。即便是deploy_task的状态是完成状态
     *
     * @return  true: 无论什么场景，都需要重跑    false：如果当前任务标记为已完成，那么直接跳过
     */
    public boolean rerunAnyway() {
        return false;
    }

    /**
     * 某个Handler在某实例部署任务时，精准执行一次。执行时机：
     * 1、常规handler会在 {@link this#exeBusiness(Context)}、{@link this#exeBusiness(Context, Node)} 之前触发
     * 2、需要重启的handler会在 {@link this#beforeBrokerStop(Context, Node)}} 之前触发
     *
     * @param context   上下文
     */
    public void exactlyOnce(Context context) {}

    /**
     * 核心方法，执行业务逻辑，集群任务类型时调用
     *
     * @param context   上下文
     */
    public abstract void exeBusiness(Context context);

    /**
     * 在broker服务马上就要停止前调用
     *
     * @param context   上下文
     * @param node  节点
     */
    public void beforeBrokerStop(Context context, Node node) {}

    /**
     * 在broker服务已经停止后调用
     *
     * @param context   上下文
     * @param node  节点
     */
    public void onBrokerStopped(Context context, Node node) {}

    /**
     * 在broker服务启动后调用
     *
     * @param context   上下文
     * @param node  节点
     */
    public void afterBrokerStart(Context context, Node node) {}

    /**
     * 在ECS机器马上就要关机前调用
     *
     * @param context   上下文
     * @param node  节点
     */
    public void beforeEcsStop(Context context, Node node) {}

    /**
     * ECS关机后调用
     *
     * @param context   上下文
     * @param node  节点
     */
    public void onEcsStopped(Context context, Node node) {}

    /**
     * ECS启动完毕后调用
     *
     * @param context   上下文
     * @param node  节点
     */
    public void afterEcsStart(Context context, Node node) {}

    /**
     * 重启类型，默认不需要重启
     *
     * @return  重启类型
     */
    public RebootType rebootType() {
        return RebootType.NONE;
    }

    /**
     * 清理动作
     *
     * @param instanceId    实例id
     */
    protected void clean(String instanceId) {}

    /**
     * 获取目标节点列表
     *
     * @param context   上下文
     * @param busHandler    DAG业务handler
     * @return  目标节点
     */
    public List<Node> targetNodeList(Context context, BusHandler busHandler) {
        Handler majorHandler = context.getMajorHandler();
//        if (Tools.isClusterCreate(majorHandler)) {
//            return context.getExpandNodeList();
//        } else if (Tools.isClusterUpdate(majorHandler)) {
//            if (isUnderExpand(context.getWholeHandlers(), busHandler)) {
//                return context.getExpandNodeList();
//            } else {
//                return context.getRunningNodeList();
//            }
//        }
        return context.getRunningNodeList();
    }

    /**
     * 目标节点的所有父节点中，是否存在扩容节点
     *
     * @param busHandlers   所有DAG节点
     * @param busHandler    目标节点
     * @return  是否存在扩容父节点
     */
    private boolean isUnderExpand(List<BusHandler> busHandlers, BusHandler busHandler) {
        Ref<Boolean> ref = new Ref<>(false);
        DAG.findAboveHandlers(busHandlers, busHandler, (tmp, level) -> {
            if (Objects.equals(tmp.getHandler().handler(), Handler.EXPAND_CLUSTER)) {
                ref.set(true);
            }
        });
        return ref.get();
    }

    /**
     * 实现ApplicationContextAware接口的回调方法，设置上下文环境
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        AbstractHandler.APPLICATION_CONTEXT = applicationContext;
    }

    public static Context getContext() {
        return CONTEXT_THREAD_LOCAL.get();
    }

    public static void putContext(Context context) {
        CONTEXT_THREAD_LOCAL.set(context);
    }

    public static void cleanContext() {
        CONTEXT_THREAD_LOCAL.remove();
    }

    public static Map<String, Map<Handler, Boolean>> getExactlyOnceMap() {
        return EXACTLY_ONCE_MAP;
    }

    /**
     * 使目标方法运行在context的上下文中
     *
     * @param context   上下文
     * @param runnable  承载方法的Runnable接口
     */
    public static void runInContext(Context context, Runnable runnable) {
        try {
            putContext(context);
            runnable.run();
        } finally {
            cleanContext();
        }
    }

    /**
     * 运行在整个大部署环境上，在整个集群部署完毕后，需要做一些清理动作
     *
     * @param context   上下文
     * @param consumer  传递方法使用
     */
    public static void runInClusterDeploy(Context context, Consumer<Context> consumer) {
        try {
            putContext(context);
            consumer.accept(context);
        } finally {
            // 清理战场
            cleanClusterDeploy(context.getInstanceId());
        }
    }

    /**
     * 清理部署战场
     *
     * @param instanceId    实例id
     */
    private static void cleanClusterDeploy(String instanceId) {
        cleanContext();
        if (StringUtils.isNotEmpty(instanceId)) {
            INSTANCE_LOCK_MAP.remove(instanceId);
        }
        // 结束部署的时候，需要将所有handler中关于当前实例的遗留信息进行清理
        for (AbstractHandler abstractHandler : HANDLER_MAP.values()) {
            abstractHandler.clean(instanceId);
        }
        EXACTLY_ONCE_MAP.remove(instanceId);
    }

    /**
     * 对参数的校验结果
     */
    public enum ParamState {
        /** 参数有效，且需要当前处理器进行处理，例如磁盘扩容，从600G扩容至900G */
        VALID,
        /** 参数无效，应当立即终止请求，例如原磁盘容量为600，新参数为-1 */
        INVALID,
        /** 不做任何操作，例如磁盘容量为600，新参数也是600 */
        NONE
    }

    /**
     * 重启类型
     */
    public enum RebootType {
        /** kafka核心服务重启 */
        BROKER,
        /** ECS重启 */
        ECS,
        /** 不用重启 */
        NONE
    }
}
