package com.example.training.jmxManagementFactory;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JmxDemoClient {

    public static void main(String[] args) throws Exception {
        // URL-ul standard pentru conectorul RMI folosit de JMX
        JMXServiceURL serviceUrl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi");
        JMXConnector connector = null;
        try {
            connector = JMXConnectorFactory.connect(serviceUrl, null);
            MBeanServerConnection mbsc = connector.getMBeanServerConnection();

            ObjectName name = new ObjectName("demo:type=Hello");

            // Attribute name = "Message" pentru getter-ul getMessage()
            String message = (String) mbsc.getAttribute(name, "Message");

            System.out.println("Message from HelloMBean: " + message);

        } finally {
            if (connector != null) {
                connector.close();
            }
        }
    }
}
