package dag.handlers;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.sql.Timestamp;
import java.util.Set;

/**
 * @author shannon, rock
 */
@Data
public class Node {
    @JSONField(serialize = false)
    private Long id;
    private String instanceId;
    private String nodeId;
    private String nodeName;
    private String nodeRole;
    private int nodeIndex;
    private String vpcId;
    private String regionId;
    private String vSwitchId;
    private String eNetworkInterfceId;
    private String securityGroupId;
    private String zoneId;
    private String version;
    private String hostname;
    private String username;
    private String password;
    private String cpu;
    private String memory;
    private String sysDiskSize;
    private String sysDiskName;
    private String network;
    private String diskSize;
    private Integer diskSizePerNode;
    private String diskCategory;
    private String diskName;
    private String deleteWithInstance;
    private String imageId;
    private String innerIpAdress;
    private int managerPort;
    private String mappingIpAdress;
    private int mappingIpPort;
    private String mappings;
    private String ecsType;
    private String codeVersion;
    private String instanceChargeType;
    private String period;
    private String periodUnit;
    private String autoRenew;
    private String autoRenewPeriod;
    private String status;
    private int fileReservedTime;
    private int agentStatus;
    private String agentVersion;
    private String domainInfo;

    private String ioMaxSpec;
    /**
     * unit byte quota.producer.default
     */
    private String ioMax;

    /**
     * Will overwrite quota.producer.default if not null
     */
    private String ioMaxRead;

    /**
     * Will overwrite quota.consumer.default if not null
     */
    private String ioMaxWrite;
    /**
     * 鉴权ak，等价于username
     */
    private String ak;
    /**
     * 鉴权sk，等价于password
     */
    private String sk;
    private String deployType;
    private String eipInfo;
    private String eniInfo;
    private String specType;

    private String systemDiskCategory;
    private String ext;

    private String configs;

    private String nasMountTargetDomain;

    private String nasFileSystemId;

    private String performanceLevel;
    private Set<String> filterPrefix;

    private Timestamp modifyTime;

    public Node() {
    }

    public Node(String instanceId) {
        this.instanceId = instanceId;
    }

    public Set<String> getFilterPrefix() {
        return filterPrefix;
    }

    public void setFilterPrefix(Set<String> filterPrefix) {
        this.filterPrefix = filterPrefix;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
