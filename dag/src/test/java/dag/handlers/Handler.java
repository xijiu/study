package dag.handlers;



/**
 * 各类处理器的枚举汇总
 *
 * @author xijiu
 * @since 2022/3/14 上午9:17
 */
public enum Handler {

    /* 基础操作---------------- begin */
    /** 集群新建 */
    CLUSTER_CREATE(TaskType.CLUSTER_TYPE),
    /** 集群变更 */
    CLUSTER_UPDATE(TaskType.CLUSTER_TYPE),
    /* 基础操作---------------- end */


    /* broker相关操作---------------- begin */
    /** broker服务启动 */
    BROKER_START(TaskType.NODE_TYPE),
    /** broker服务关闭 */
    BROKER_STOP(TaskType.NODE_TYPE),
    /** broker服务的配置信息，在服务启动前，需要将配置信息拷贝过去 */
    BROKER_CONFIG(TaskType.NODE_TYPE),
    /** 检查服务是否正常启动 */
    BROKER_CHECK(TaskType.NODE_TYPE),
    /* broker相关操作---------------- end */


    /* 集群扩容---------------- begin */
    /** 集群扩容 */
    EXPAND_CLUSTER(TaskType.CLUSTER_TYPE),
    /** 集群扩容-重开节点 */
    EXPAND_REOPEN(TaskType.CLUSTER_TYPE),
    /** 节点打开后的后续动作 */
    REOPEN_AFTER_START(TaskType.NODE_TYPE),
    /** 集群扩容-新建节点 */
    EXPAND_CREATE_NODE(TaskType.CLUSTER_TYPE),
    /* 集群扩容---------------- end */


    /* NAS--------------------- begin */
    /** NAS对外暴露的handler */
    NAS(TaskType.CLUSTER_TYPE),
    /** 二级存储创建 */
    NAS_CREATE(TaskType.CLUSTER_TYPE),
    /** 二级存储挂载 */
    NAS_MOUNT(TaskType.NODE_TYPE),
    /* NAS--------------------- end */


    /* 公网相关---------------------------------------begin */
    /** 公网相关操作 */
    EIP(TaskType.NODE_TYPE),
    /** 公网相关配置 */
    EIP_CONFIG(TaskType.NODE_TYPE),
    /** 开启公网 */
    EIP_ENABLE(TaskType.NODE_TYPE),
    /** 关闭公网 */
    EIP_DISABLE(TaskType.NODE_TYPE),
    /** 修改公网 */
    EIP_MODIFY(TaskType.NODE_TYPE),
    /* 公网相关---------------------------------------end */


    /** 磁盘相关操作 */
    DISK(TaskType.NODE_TYPE),
    /** 磁盘扩容 */
    DISK_EXPAND(TaskType.NODE_TYPE),
    /** 磁盘缩容 */
    DISK_SHRINK(TaskType.NODE_TYPE),


    /* ACL相关---------------------------------------begin */
    /** ACL相关操作 */
    ACL(TaskType.NODE_TYPE),
    /** 为ACL授权安全组 */
    ACL_SG_AUTH(TaskType.NODE_TYPE),
    /** 新建或扩容时ACL对应的handler */
    ACL_FOR_CREATE(TaskType.NODE_TYPE),
    /** 实例升配时ACL对应的handler */
    ACL_FOR_UPDATE(TaskType.NODE_TYPE),
    /** 是否开启VPC的SASL配置 */
    ENABLE_VPC_SASL(TaskType.NODE_TYPE),
    /* ACL相关---------------------------------------end */


    /** 应用版本 */
    CORE_VERSION(TaskType.NODE_TYPE),
    TOPIC_NUM(TaskType.NODE_TYPE),
    REBOOT_ECS(TaskType.NODE_TYPE),
    AGENT_RESTART(TaskType.NODE_TYPE),
    REELECT_CONTROLLER(TaskType.CLUSTER_TYPE),


    /* ECS相关---------------------------------------begin */
    /** 创建ECS */
    CREATE_ECS(TaskType.NODE_TYPE),
    /** 执行创建ECS的操作 */
    DO_CREATE_ECS(TaskType.NODE_TYPE),
    /** 释放ecs连接池 */
    RELEASE_ECS_POOL(TaskType.CLUSTER_TYPE),
    /** 启动ECS */
    START_ECS(TaskType.NODE_TYPE),
    /** 修改ECS名称，使其跳过监控 */
    MODIFY_ECS_NAME(TaskType.NODE_TYPE),
    /** 还原ECS名称 */
    REVERT_ECS_NAME(TaskType.NODE_TYPE),
    /** 变更ECS类型 */
    CHANGE_ECS_TYPE(TaskType.NODE_TYPE),
    /* ECS相关---------------------------------------end */


    /* ENI相关---------------------------------------begin */
    /** 打通用户VPC安全组 */
    ENI_SG(TaskType.CLUSTER_TYPE),
    /** 创建且授权VPC的安全组 */
    CREATE_USER_SG(TaskType.CLUSTER_TYPE),
    /** 创建我们VPC的SG */
    CREATE_OUR_SG(TaskType.CLUSTER_TYPE),
    /** ECS的与安全组绑定 */
    ATTACH_ESC_SG(TaskType.NODE_TYPE),
    /* ENI相关---------------------------------------end */


    /* preDeploy相关---------------------------------------begin */
    /** 部署前准备 */
    PRE_DEPLOY(TaskType.NODE_TYPE, "PRE_DEPLOY"),
    PRE_DEPLOY_SCRIPT(TaskType.NODE_TYPE),
    PRE_DEPLOY_INIT_SERVICE(TaskType.NODE_TYPE),
    PRE_DEPLOY_EXPAND(TaskType.NODE_TYPE),
    PRE_DEPLOY_WAIT_DISK(TaskType.NODE_TYPE),
    PRE_DEPLOY_SCP_KERNEL(TaskType.NODE_TYPE),
    /* preDeploy相关---------------------------------------end */


    /** 创建安全组 */
    CREATE_SECURITY_GROUP(TaskType.CLUSTER_TYPE),

    /** 授权打通端口 */
    VPC_PORT_MAPPING(TaskType.NODE_TYPE),
    /** 修改hostname */
    MODIFY_HOSTNAME(TaskType.NODE_TYPE),
    /** vpc相关配置 */
    CONFIG_VPC(TaskType.CLUSTER_TYPE),
    /** 开启域名 */
    ENABLE_DOMAIN(TaskType.NODE_TYPE),
    /** 部署服务 */
    DEPLOY_SERVICE(TaskType.CLUSTER_TYPE),
    /** 部署代理 */
    DEPLOY_AGENT(TaskType.CLUSTER_TYPE),

    /* node相关---------------------------------------begin */
    /** do nothing */
    NODE_NONE(TaskType.CLUSTER_TYPE),
    /** 节点标记为正常运行状态 */
    NODE_RUNNING_MAKER(TaskType.NODE_TYPE),
    /** 节点基础信息初始化 */
    NODE_INIT(TaskType.CLUSTER_TYPE),
    /** 为node填充可用区 */
    NODE_ZONE_FILL(TaskType.CLUSTER_TYPE),
    /** 为node填充ECS_TYPE相关数据 */
    NODE_ECS_TYPE_FILL(TaskType.CLUSTER_TYPE),
    /* node相关---------------------------------------end */

    /** rmq配置项的替换动作 */
    RMQ_CONF_REPLACE(TaskType.NODE_TYPE),

    /** 将kafka-tools拷贝至节点 */
    SCP_KAFKA_TOOLS_TO_NODE(TaskType.NODE_TYPE, "SCP_KAFKA_TOOLS"),

    /** 规整的处理器 */
    MSG_COMPACT(TaskType.NODE_TYPE),

    /** 上下文初始化 */
    CONTEXT_INIT(TaskType.CLUSTER_TYPE),

    /* 填充上下文相关---------------------------------------begin */
    /** 填充上下文父类 */
    CONTEXT_FILLER(TaskType.CLUSTER_TYPE),
    /** 实例更新时填充上下文的逻辑 */
    CONTEXT_UPDATE_FILLER(TaskType.CLUSTER_TYPE),
    /* node相关---------------------------------------end */

    /* 服务、ECS重启相关---------------------------------------begin */
    /** （服务、机器）重启标识 */
    REBOOT_BEGIN(TaskType.NODE_TYPE),
    /** （服务、机器）重启标识 */
    REBOOT_END(TaskType.NODE_TYPE),
    /* 服务、ECS重启相关---------------------------------------end */


    /* 参数配置相关---------------------------------------begin */
    /** 参数配置，例如消息保留时长、消息最大长度等,一级handler，对外暴露 */
    PARAMETER(TaskType.NODE_TYPE),
    /** 新建或者横向扩容时处理逻辑 */
    PARAMETER_FOR_CREATE(TaskType.NODE_TYPE),
    /** 纵向升级 */
    PARAMETER_FOR_UPDATE(TaskType.NODE_TYPE),
    /* 参数配置相关---------------------------------------end */


    ;

    public final String alias;

    public final TaskType taskType;

    Handler(TaskType taskType) {
        this.alias = this.toString();
        this.taskType = taskType;
    }

    Handler(TaskType taskType, String alias) {
        this.taskType = taskType;
        this.alias = alias;
    }

    public static Handler of(String name) {
        Handler[] values = values();
        for (Handler handler : values) {
            if (handler.toString().equalsIgnoreCase(name)) {
                return handler;
            }
        }
        throw new RuntimeException("can not find handler: " + name);
    }

}
