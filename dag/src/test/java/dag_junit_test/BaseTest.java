package dag_junit_test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import dag.CoreDeployDriver;
import dag.handlers.AbstractHandler;
import dag.handlers.Config;
import dag.handlers.Context;
import dag.handlers.Handler;
import dag.handlers.Node;
import dag_junit_test.handlers.node.A1;
import dag_junit_test.handlers.node.A10;
import dag_junit_test.handlers.node.A11;
import dag_junit_test.handlers.node.A12;
import dag_junit_test.handlers.node.A2;
import dag_junit_test.handlers.node.A3;
import dag_junit_test.handlers.node.A4;
import dag_junit_test.handlers.node.A5;
import dag_junit_test.handlers.node.A6;
import dag_junit_test.handlers.node.A7;
import dag_junit_test.handlers.node.A8;
import dag_junit_test.handlers.node.A9;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author xijiu
 * @since 2022/4/11 上午11:47
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ScanPackage.class})
@ComponentScan(basePackages = {"dag_junit_test", "dag"},
        excludeFilters = {@ComponentScan.Filter()})
public class BaseTest implements ApplicationContextAware {
    private static ApplicationContext APPLICATION_CONTEXT;

    private static final String INSTANCE_ID = "alikafka_post-cn-zvp2mrxzk00d";

    private static Map<Handler, AbstractHandler> handlerMap = null;

    @Resource
    private CoreDeployDriver coreDeployDriver;
//    @MockBean
//    private GetEcsTypeService getEcsTypeService;
//    @MockBean
//    private StopOrReleaseService stopOrReleaseService;
//    @MockBean
//    protected ClusterInstanceDao clusterInstanceDao;
//    @MockBean
//    protected ClusterService clusterService;
//    @MockBean
//    protected ScheduledLoadDbKeyValueWrapper scheduledLoadDbKeyValueWrapper;
//    @MockBean
//    protected NasService nasService;
//    @MockBean
//    protected OneCloudService oneCloudService;
//    @MockBean
//    protected VersionControlService versionControlService;
//    @MockBean
//    protected AffinityZoneService affinityZoneService;
//    @MockBean
//    protected SpecialZonesService specialZonesService;
//    @MockBean
//    protected StockService stockService;
//    @MockBean
//    protected DingNotify dingNotify;
//    @MockBean
//    protected FileService fileService;
//    @MockBean
//    protected NodesManager nodesManager;

    @Before
    public void before() throws Exception {
        register(
                A1.class, A2.class, A3.class, A4.class,
                A5.class, A6.class, A7.class, A8.class,
                A9.class, A10.class, A11.class, A12.class);
    }

    private void register(Class<? extends AbstractHandler>... clazzArr) throws Exception {
        if (clazzArr != null && clazzArr.length > 0) {
            for (Class<? extends AbstractHandler> clazz : clazzArr) {
                AbstractHandler bean = APPLICATION_CONTEXT.getBean(clazz);
                Field field = AbstractHandler.class.getDeclaredField("HANDLER_MAP");
                field.setAccessible(true);
                handlerMap = (Map<Handler, AbstractHandler>) field.get(null);
                handlerMap.put(bean.handler(), bean);
            }
        }
    }

    protected void register(Handler handler, AbstractHandler abstractHandler) {
        try {
            Field field = AbstractHandler.class.getDeclaredField("HANDLER_MAP");
            field.setAccessible(true);
            handlerMap = (Map<Handler, AbstractHandler>) field.get(null);
            handlerMap.put(handler, abstractHandler);

            Field field2 = CoreDeployDriver.class.getDeclaredField("CONTROLLER_ABSTRACT_HANDLER");
            field2.setAccessible(true);
            field2.set(null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    protected void mockOtherService(Context context) throws Exception {
//        Field field = AbstractHandler.class.getDeclaredField("HANDLER_MAP");
//        field.setAccessible(true);
//        Map<Handler, AbstractHandler> HANDLER_MAP = (Map)field.get(null);
//        HANDLER_MAP.put(Handler.CREATE_USER_SG, new SubCreateAndAuthSgHandler());
//        boolean enableVpcSasl = Tools.isTrue(context.getNewConfig().getEnableVpcSasl());
//        AbstractHandler.ParamState paramState = enableVpcSasl ? AbstractHandler.ParamState.VALID : AbstractHandler.ParamState.NONE;
//        HANDLER_MAP.put(Handler.ENABLE_VPC_SASL, new SubEnableVpcSaslHandler(paramState));
//        HANDLER_MAP.put(Handler.BROKER_CONFIG, new SubBrokerConfigHandler());
//        HANDLER_MAP.put(Handler.REBOOT_END, new SubRebootEndHandler());
//        HANDLER_MAP.put(Handler.PARAMETER_FOR_UPDATE, new SubParameterForUpdateHandler());
//        HANDLER_MAP.put(Handler.PARAMETER_FOR_CREATE, new SubParameterForCreateHandler());
//    }
//
//    public static class SubParameterForCreateHandler extends ParameterForCreateHandler {
//        @Override
//        protected ParamState isParameterWithinScope(Config config) {
//            return ParamState.VALID;
//        }
//    }
//
//    public static class SubParameterForUpdateHandler extends ParameterForUpdateHandler {
//        @Override
//        protected ParamState isParameterWithinScope(Config config) {
//            return ParamState.VALID;
//        }
//    }
//
//    public static class SubRebootEndHandler extends RebootEndHandler {
//        @Override
//        public void beforeBrokerStop(Context context, Node node) {}
//        @Override
//        public void onBrokerStopped(Context context, Node node) {}
//        @Override
//        public void afterBrokerStart(Context context, Node node) {}
//        @Override
//        public void beforeEcsStop(Context context, Node node) {}
//        @Override
//        public void onEcsStopped(Context context, Node node) {}
//        @Override
//        public void afterEcsStart(Context context, Node node) {}
//    }
//
//    public static class SubBrokerConfigHandler extends BrokerConfigHandler {
//        @Override
//        public ParamState preCheck(Context context, Action action) {
//            return ParamState.VALID;
//        }
//    }
//
//    public static class SubCreateAndAuthSgHandler extends CreateUserSgHandler {
//        @Override
//        public ParamState preCheck(Context context, Action action) {
//            return ParamState.VALID;
//        }
//    }
//
//
//    public static class SubEnableVpcSaslHandler extends EnableVpcSaslHandler {
//        private final ParamState paramState;
//        public SubEnableVpcSaslHandler(ParamState paramState) {
//            this.paramState = paramState;
//        }
//        @Override
//        public ParamState preCheck(Context context, Action action) {
//            return paramState;
//        }
//    }

    @SneakyThrows
    protected Context mockContext(int nodeSize) {
        return mockContext(nodeSize, 10000);
    }

    @SneakyThrows
    protected Context mockContext(int nodeSize, int startNodeIndex) {
        Context context = new Context();
        Config currConfig = Config.create();
        currConfig.setInstanceId(INSTANCE_ID);
        currConfig.setDiskCategory("");
        currConfig.setPubNet(0);
        currConfig.setEnableAcl(true);
        currConfig.setIoMaxSpec("alikafka.hw.2xlarge");
        currConfig.setMsgStoreTime(10000);
        currConfig.setMaxMsgSize(10);
        currConfig.setOffsetStoreTime(10000);
        currConfig.setSpecType("normal");
        currConfig.setState("ItemStatus.RUNNING");
        currConfig.setVersion("8.0.10.2-5.0.3");
        context.setRunningNodeList(mockNodeList(startNodeIndex, nodeSize));

        Config newConfig = Config.create();
        BeanUtils.copyProperties(newConfig, currConfig);
        newConfig.setPubNet(20);
        newConfig.setIoMaxSpec("alikafka.hw.4xlarge");

        context.setCurrConfig(currConfig);
        context.setNewConfig(newConfig);
        return context;
    }

    private List<Node> mockNodeList(int nodeSize) {
        return mockNodeList(0, nodeSize);
    }

    private List<Node> mockNodeList(int startNodeIndex, int nodeSize) {
        startNodeIndex = startNodeIndex <= 0 ? 10000 : startNodeIndex;
        List<Node> list = Lists.newArrayList();
        for (int i = 1; i <= nodeSize; i++) {
            Node node = new Node();
            node.setNodeIndex(startNodeIndex + i);
            node.setNodeName("uk_4kafka_alikafka_serverless-cn-0ju2mrdux001_" + (10000 + i));
            list.add(node);
        }
        return list;
    }

    protected boolean isResultMatch(String except, List<Record> records) {
        String[] split = except.split(",");
        List<Record> expectList = Lists.newArrayList();
        for (String single : split) {
            expectList.add(Record.of(single));
        }
        if (expectList.size() != records.size()) {
            return false;
        }
        for (int i = 0; i < expectList.size(); i++) {
            Record record1 = expectList.get(i);
            Record record2 = records.get(i);
            if (!record1.getName().equals(record2.getName()) || record1.getNodeId() != record2.getNodeId()) {
                return false;
            }
        }
        return true;
    }

    protected boolean fixedSeq(List<Record> records, String nodesStr) {
        String[] nodes = nodesStr.split(",");
        int index = 0;
        for (Record record : records) {
            if (record.getName().equals(nodes[index])) {
                index++;
                if (index >= nodes.length) {
                    break;
                }
            }
        }
        return index >= nodes.length;
    }

    protected boolean fixedSeqWithNode(List<Record> records, String nodesStr) {
        String[] nodes = nodesStr.split(",");
        int index = 0;
        for (Record record : records) {
            String[] split = nodes[index].split("-");
            long nodeId = split.length >= 2 ? Long.parseLong(split[1]) : 10001L;
            if (record.getName().equals(split[0]) && record.getNodeId() == nodeId) {
                index++;
                if (index >= nodes.length) {
                    break;
                }
            }
        }
        return index >= nodes.length;
    }

    protected boolean isSameTime(List<Record> records, String nodeStr1, String... nodeStrArr) {
        Set<String> timeCostSet = Sets.newHashSet();
        timeCostSet.add(nodeStr1);
        timeCostSet.addAll(Arrays.asList(nodeStrArr));

        List<Long> timeCostList = Lists.newArrayList();
        for (Record record : records) {
            if (timeCostSet.contains(record.getName() + "-" + record.getNodeId())) {
                timeCostList.add(record.getTime());
            }
        }
        Long max = timeCostList.stream().max(Long::compare).get();
        Long min = timeCostList.stream().min(Long::compare).get();
        return Math.abs(max - min) < 50;
    }

    protected boolean isAfter(List<Record> records, String oldStr, String newStr) {
        String[] oldSplit = oldStr.split(",");
        Set<String> oldSet = Sets.newHashSet();
        oldSet.addAll(Arrays.asList(oldSplit));

        String[] newSplit = newStr.split(",");
        Set<String> newSet = Sets.newHashSet();
        newSet.addAll(Arrays.asList(newSplit));

        List<Long> timeCostListOld = Lists.newArrayList();
        List<Long> timeCostListNew = Lists.newArrayList();
        for (Record record : records) {
            if (oldSet.contains(record.getName() + "-" + record.getNodeId())) {
                timeCostListOld.add(record.getTime());
            }
            if (newSet.contains(record.getName() + "-" + record.getNodeId())) {
                timeCostListNew.add(record.getTime());
            }
        }
        Long max = timeCostListOld.stream().max(Long::compare).get();
        Long min = timeCostListNew.stream().min(Long::compare).get();
        return max < min;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        APPLICATION_CONTEXT = applicationContext;
    }
}
