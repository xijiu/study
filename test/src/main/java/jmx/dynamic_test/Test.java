package jmx.dynamic_test;

import com.sun.tools.attach.VirtualMachine;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.util.List;

/**
 *
 * @author xijiu
 * @since 2022/3/21 下午4:12
 */
public class Test {
    final static String pid               = "85657";
    final static String CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";

    public static void main(String[] args) throws Exception {
        VirtualMachine vm = VirtualMachine.attach(pid);
        JMXConnector connector = null;
        String connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);

        String agent = vm.getSystemProperties().getProperty("java.home") + File.separator + "lib" + File.separator
                + "management-agent.jar";
        vm.loadAgent(agent);

        connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
        System.out.println("connectorAddress content is " + connectorAddress);
        JMXServiceURL url = new JMXServiceURL(connectorAddress);
        connector = JMXConnectorFactory.connect(url);
        MBeanServerConnection serverConnection = connector.getMBeanServerConnection();


        AttributeList attributeList = serverConnection.getAttributes(new ObjectName("jmxBean:name=hello"),
                new String[] {"Name"});


        System.out.println(123);
        System.out.println("attributeList is " + attributeList);
        List<Attribute> attributes = attributeList.asList();
        System.out.println("attributes size is " + attributes.size());

    }
}
