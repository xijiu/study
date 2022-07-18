package file_test;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by jingdong.hjd<jingdong.hjd@alibaba-inc.com> on 2021/06/22.
 */
public class ServerlessUpdateDataDo {
    @Getter
    @Setter
    public static class TopicPartitionInfo {
        String topic;
        Map<String, String> partitionMap;
    }
    // instanceId
    String instanceId;
    // 集群规模
    int fromNodeSize;
    int toNodeSize;
    // 执行类型
    ServerlessOperatorEnum operator = ServerlessOperatorEnum.NONE;
    //
    private Long lastUpdateTimestamp = 0L;
    private String lastUpdateTime;
    // 需要sleep到时间点
    private Long sleepToTimestamp = 0L;
    private Map<String, TopicPartitionInfo> partitionInfoMap;
    //

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public int getToNodeSize() {
        return toNodeSize;
    }

    public void setToNodeSize(int toNodeSize) {
        this.toNodeSize = toNodeSize;
    }

    public int getFromNodeSize() {
        return fromNodeSize;
    }

    public void setFromNodeSize(int fromNodeSize) {
        this.fromNodeSize = fromNodeSize;
    }

    public ServerlessOperatorEnum getOperator() {
        return operator;
    }

    public void setOperator(ServerlessOperatorEnum operator) {
        this.operator = operator;
    }

    public Long getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(Long lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
        lastUpdateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.valueOf(lastUpdateTimestamp)));
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Long getSleepToTimestamp() {
        return sleepToTimestamp;
    }

    public void setSleepToTimestamp(Long sleepToTimestamp) {
        this.sleepToTimestamp = sleepToTimestamp;
    }

    public Map<String, TopicPartitionInfo> getPartitionInfoMap() {
        return partitionInfoMap;
    }

    public void setPartitionInfoMap(Map<String, TopicPartitionInfo> partitionInfoMap) {
        this.partitionInfoMap = partitionInfoMap;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
