package dag.handlers;

/**
 * 当前针对handler的操作类型
 *
 * @author xijiu
 * @since 2022/11/30 下午7:10
 */
public enum Action {
    /** 集群新建 */
    NEW_CLUSTER,

    /** 集群扩容-横向扩容 */
    HORIZONTAL,

    /** 集群扩容-纵向扩容 */
    VERTICAL
}
