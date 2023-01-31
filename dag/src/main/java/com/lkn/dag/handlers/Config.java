package com.lkn.dag.handlers;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 配置信息
 *
 * @author xijiu
 * @since 2022/3/14 上午10:07
 */
@Getter
@Setter
public class Config {

    /** 当前配置描述的版本号 */
    public static final int DESC_VERSION = 1;

    /** 描述版本 */
    private Integer descVersion = DESC_VERSION;
    /** 实例id */
    private String instanceId;
    /** regionId */
    private String regionId;
    /** 实例状态 */
    private String state;
    /** 实例类型，例如normal */
    private String specType;
    /** 规格，例如alikafka.hw.2xlarge */
    private String ioMaxSpec;
    /** 用户的vpcId */
    private String vpcId;
    /** 用户的 switchId */
    private String vSwitchId;
    /** 用户选择的主可用区 */
    private String zoneId;
    /** 是否跨区域部署 */
    private Boolean acrossZone;
    /** 可用区信息，格式为JSON，例如 [["A","B","C"],["A","B","C"]] 固定长度为2，一个主一个副 */
    private String selectedZones;
    /** 是否强制跨可用区 */
    private Boolean forceAcrossZone;
    /** 我们资源账号的VPC */
    private String ourVpcId;
    /** 用户的安全组id，通过console传递过来 */
    private String userSgId;
    /** 内核版本，例如 8.0.10.2-5.0.3 */
    private String version;
    /** 实例磁盘总大小，单位G */
    private Integer diskSize;
    /** 磁盘类型 */
    private String diskCategory;
    /** 公网带宽，单位 M */
    private Integer pubNet;
    /** 消息存储时长，单位小时 */
    private Integer msgStoreTime;
    /** 最大消息大小，单位M */
    private Integer maxMsgSize;
    /** 位点存储时长，单位分钟 */
    private Integer offsetStoreTime;
    /** 是否开启ACL */
    private Boolean enableAcl;
    /** 是否开启VPC内的加密 */
    private Boolean enableVpcSasl;
    /** 是否打开二级存储 */
    private Boolean tieredNas;
    /** 设置二级存储的大小 */
    private Integer tieredNasStoreSpace;
    /** kms key，用于标明集群是否需要加密 */
    private String kmsKeyId;
    /** 暂时由console传递过来，将来考虑将其搬迁至deploy中 */
    private String zkConnect;
    /** 公网实例，需要填写，由用户指定，如果不指定，则deploy自动生成 */
    private String ak;
    /** 公网实例，需要填写，由用户指定，如果不指定，则deploy自动生成 */
    private String sk;
    /** 部署的agent版本 */
    private String agentVersion;

    /** 存储每个节点的配置信息，key是nodeIndex，例如101、102、103等 */
    private Map<Integer, NodeConfig> nodes = Maps.newHashMap();

    @Getter
    @Setter
    public static class NodeConfig {
        /** 节点编号，例如101、102、103等 */
        private int nodeIndex;
        /** cpu核数 */
        private int cpu;
        /** 内存大小，单位G */
        private int memory;
        /** 磁盘大小，单位G */
        private int disk;
    }

    /**
     * 新建config对象
     *
     * @return  新对象
     */
    public static Config create() {
        return new Config();
    }

}
