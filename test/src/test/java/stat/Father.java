package stat;

/**
 * @author xijiu
 * @since 2022/10/11 下午3:26
 */
public class Father {
    static {
        System.out.println("123");
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            System.out.println(stackTraceElement);
        }
        System.out.println("father static");
    }

    public final void aaaa() {
        System.out.println("i am Father");
    }

    String abc = "{\"action\":\"UpgradeInstance\",\"allConfigJsonMap\":{\"enable.vpc_sasl_ssl\":\"false\",\"kafka.log.retention.hours\":\"72\",\"kafka.offsets.retention.minutes\":\"10080\",\"enable.tiered\":\"true\",\"cloud.maxTieredStoreSpace\":\"107374182400000\",\"enable.acl\":\"false\",\"kafka.ssl.bit\":\"4096\",\"enable.compact\":\"true\",\"kafka.message.max.bytes\":\"1048576\"},\"allConfigsMapJsonStr\":\"{\\\"enable.vpc_sasl_ssl\\\":\\\"false\\\",\\\"kafka.log.retention.hours\\\":\\\"72\\\",\\\"kafka.offsets.retention.minutes\\\":\\\"10080\\\",\\\"enable.tiered\\\":\\\"true\\\",\\\"cloud.maxTieredStoreSpace\\\":\\\"107374182400000\\\",\\\"enable.acl\\\":\\\"false\\\",\\\"kafka.ssl.bit\\\":\\\"4096\\\",\\\"enable.compact\\\":\\\"true\\\",\\\"kafka.message.max.bytes\\\":\\\"1048576\\\"}\",\"alterConfigJsonMap\":{\"kafka.ssl.bit\":\"4096\"},\"alterConfigsMapJsonStr\":\"{\\\"kafka.ssl.bit\\\":\\\"4096\\\"}\",\"autoEcsTypeConf\":\"{\\\"enable\\\":true}\",\"instanceId\":\"alikafka_serverless-cn-zvp313gz0005\",\"upgradeJsonMap\":{\"diskSize\":\"10000\",\"ioMax\":\"10\",\"ioMaxSpec\":\"alikafka.serverless.xlarge\",\"ioMaxWrite\":\"200\",\"deployType\":\"4\",\"outerIoMax\":\"200\",\"ioMaxRead\":\"200\"},\"upgradeMapJsonStr\":\"{\\\"diskSize\\\":\\\"10000\\\",\\\"ioMax\\\":\\\"10\\\",\\\"ioMaxSpec\\\":\\\"alikafka.serverless.xlarge\\\",\\\"ioMaxWrite\\\":\\\"200\\\",\\\"deployType\\\":\\\"4\\\",\\\"outerIoMax\\\":\\\"200\\\",\\\"ioMaxRead\\\":\\\"200\\\"}\"}";
}
