package stat;

import java.util.Map;

/**
 * 用户向JMX暴露
 *
 * @author xijiu
 * @since 2022/3/22 下午2:38
 */
public interface NetworkFlowStatService {

    /**
     * 获取网络流量
     *
     * @return  key：时间戳，例如：1647931140000
     *          val：长度为2的数组，inBytes 及 outBytes
     */
    Map<Long, Long[]> getNetworkFlow();
}
