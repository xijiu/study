package dag.bean;
import dag.handlers.AbstractHandler;
import dag.handlers.DeployTask;
import dag.handlers.Node;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 二次封装
 *
 * @author xijiu
 * @since 2022/3/29 下午4:25
 */
@Getter
@Setter
@NoArgsConstructor
public class BusHandler {
    /** 任务执行编号，同一个任务该编号递增 */
    private static final ThreadLocal<AtomicInteger> TASK_SEQ = ThreadLocal.withInitial(AtomicInteger::new);

    /** 当前执行器编号 */
    private int seq;
    /** 父编号 */
    private int fSeq;
    private Node node;
    private NumHandler numHandler;
    private AbstractHandler handler;
    private DeployTask deployTask;
    /** 复合节点的收尾节点，仅在DAG图中做一个多线程收敛作用，本身不执行逻辑 */
    private boolean partTail = false;

    private BusHandler(Node node, NumHandler numHandler, BusHandler fatherHandler) {
        this.node = node;
        this.numHandler = numHandler;
        this.handler = AbstractHandler.get(numHandler.getHandler());
        this.seq = TASK_SEQ.get().incrementAndGet();
        this.fSeq = fatherHandler == null ? -1 : fatherHandler.seq;
        this.deployTask = createNewDeployTask(seq, fSeq);
    }

    private DeployTask createNewDeployTask(int seq, int fSeq) {
        DeployTask deployTask = new DeployTask();
        deployTask.setSeq(seq);
        deployTask.setExec(fSeq);
        return deployTask;
    }

    /**
     * 构建集群类型
     */
    public static BusHandler of(NumHandler numHandler, BusHandler fatherHandler) {
        return of(numHandler, fatherHandler, null);
    }

    /**
     * 构建节点类型
     */
    public static BusHandler of(NumHandler numHandler, BusHandler fatherHandler, Node node) {
        return new BusHandler(node, numHandler, fatherHandler);
    }

    public static void reset() {
        TASK_SEQ.get().set(0);
    }
}