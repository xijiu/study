package dag.handlers;

import org.apache.commons.lang.StringUtils;


/**
 * 节点级别的任务
 *
 * @author xijiu
 * @since 2022/3/14 上午9:10
 */
public abstract class AbstractNodeHandler extends AbstractBehaviorHandler {

    public TaskType taskType() {
        return TaskType.NODE_TYPE;
    }

    /**
     * 核心方法，执行业务逻辑，集群任务类型时调用
     *
     * @param context   上下文
     */
    public void exeBusiness(Context context) {
    }

    protected boolean isAttributeExist(Node node, String attributeName) {
        return isAttributeExist(node, attributeName, obj -> obj != null && StringUtils.isNotEmpty(obj.toString()));
    }

    /**
     * 判断node中某个属性是否存在
     *
     * @param node  节点
     * @param attributeName 属性名称
     * @param fieldChecker 属性内容检查器
     * @return  属性内容是否存在
     */
    protected boolean isAttributeExist(Node node, String attributeName, FieldChecker fieldChecker) {
        if (isSpecialAttributeExist(node, attributeName, fieldChecker)) {
            return true;
        }
//        Node nodeDB = clusterInstanceDao.getNodeByNodeIndex(node.getInstanceId(), node.getNodeIndex());
        Node nodeDB = null;
        if (isSpecialAttributeExist(nodeDB, attributeName, fieldChecker)) {
            Object dbVal = ReflectTools.get(nodeDB, attributeName);
            ReflectTools.set(node, attributeName, dbVal);
            return true;
        }
        return false;
    }

    private boolean isSpecialAttributeExist(Node node, String attributeName, FieldChecker fieldChecker) {
        if (node != null) {
            Object fieldVal = ReflectTools.get(node, attributeName);
            if (fieldVal != null && StringUtils.isNotEmpty(fieldVal.toString().trim())) {
                return fieldChecker.check(fieldVal);
            }
        }
        return false;
    }

    protected interface FieldChecker {
        boolean check(Object obj);
    }


}
