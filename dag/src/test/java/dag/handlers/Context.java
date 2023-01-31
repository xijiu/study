package dag.handlers;

import com.google.common.collect.Lists;
import dag.bean.BusHandler;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/**
 * 存放整个执行过程中的上下文信息
 *
 * @author xijiu
 * @since 2022/3/14 上午9:12
 */
@Getter
@Setter
public class Context {

    /** 实例id */
    private String instanceId;

    /** regionId */
    private String regionId;

    /** 目标用户id */
    private Long ownerId;

    /** 要执行的配置 */
    private Config newConfig;

    /** 当前配置 */
    private Config currConfig;

    /** 部署类型 */
    private String deployType;

    /** 部署任务的ID */
    private String taskId;

    /** 是否为任务恢复操作 */
    private boolean taskRecover = false;

    /** 域名前缀 */
    private String domainPrefix;

    /** 节点列表，数据库中已经存在的节点列表 */
    private List<Node> runningNodeList = Lists.newArrayList();

    /** 将要进行节点扩容的列表 */
    private List<Node> expandNodeList = Lists.newArrayList();

    /** 主执行器，例如{@link Handler#CLUSTER_UPDATE} 等 */
    private BusHandler majorBusHandler;

    /** 主执行器，例如{@link Handler#CLUSTER_UPDATE} 等 */
    private Handler majorHandler;

    /** 存储全部执行器列表，存储DAG详情 */
    private LinkedList<BusHandler> wholeHandlers = new LinkedList<>();

}
