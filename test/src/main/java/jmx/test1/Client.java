package jmx.test1;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.List;

/**
 * @author xijiu
 * @since 2022/3/18 下午2:59
 */
public class Client {
    public static final int PORT = ServerAgent.PORT;


    public static void main(String[] args) throws Exception {
        JMXServiceURL jmxServiceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + PORT + "/jmxrmi");
        JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL);

        MBeanServerConnection serverConnection = jmxConnector.getMBeanServerConnection();

        AttributeList attributeList = serverConnection.getAttributes(new ObjectName("jmxBean:name=hello"),
                new String[] {"NetworkFlow"});


        System.out.println(123);
        System.out.println("attributeList is " + attributeList);
        List<Attribute> attributes = attributeList.asList();
        System.out.println("attributes size is " + attributes.size());
        Attribute attribute = attributes.get(0);
        System.out.println("attribute.getName() is : " + attribute.getName());
        Object value = attribute.getValue();
        System.out.println(value.getClass().getName());
    }
}
