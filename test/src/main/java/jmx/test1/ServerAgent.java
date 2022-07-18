package jmx.test1;

import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
 
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
 
public class ServerAgent {

    public static final int PORT = 9093;

    public static void main(String[] args) throws Exception {
        //create mbean server
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();         
        //create object name
        ObjectName objectName = new ObjectName("jmxBean:name=hello");
         
        //create mbean and register mbean 
        server.registerMBean(new Hello(), objectName);

        //这句话非常重要，不能缺少！注册一个端口，绑定url后，客户端就可以使用rmi通过url方式来连接JMXConnectorServer
        Registry registry = LocateRegistry.createRegistry(PORT);

        //构造JMXServiceURL
        JMXServiceURL jmxServiceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + PORT + "/jmxrmi");
        //创建JMXConnectorServer
        JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(jmxServiceURL, null, server);
        //启动
        cs.start();
    }
}

