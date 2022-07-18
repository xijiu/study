package jmx.dynamic_test;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;


import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * 该类是一个Agent类，说明： 先创建了一个MBeanServer，用来做MBean的容器
 * 将Hello这个类注入到MBeanServer中，注入需要创建一个ObjectName类
 * 创建一个AdaptorServer，这个类将决定MBean的管理界面，这里用最普通的Html型界面。 AdaptorServer其实也是一个MBean。
 * jmx.hello:name=Hello的名字是有一定规则的，格式为：“域名:name=MBean名称”，
 * 域名和MBean名称都可以任意取。 打开网页：http://localhost:8082/
 * @author chenyw
 *
 */
public class Server {
    public static final int PORT = 9093;

    public static void main(String[] args) throws Exception {
    	// 创建一个MBeanServer，用来做MBean的容器
        MBeanServer server = MBeanServerFactory.createMBeanServer();
        ObjectName helloName = new ObjectName("chengang:name=HelloDynamic");
        HelloDynamic hello = new HelloDynamic();
        // 将Hello这个类注入到MBeanServer中，注入需要创建一个ObjectName类
        server.registerMBean(hello, helloName);


        LocateRegistry.createRegistry(PORT);
        //构造JMXServiceURL
        JMXServiceURL jmxServiceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + PORT + "/jmxrmi");
        //创建JMXConnectorServer
        JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(jmxServiceURL, null, server);
        //启动
        cs.start();
        System.out.println("start.....");
    }

}