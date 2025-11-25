package com.example.training.jmxManagementFactory;

import java.lang.management.ManagementFactory;
import javax.management.ObjectName;

/**Run with
 * -Dcom.sun.management.jmxremote \
 *  -Dcom.sun.management.jmxremote.port=9999 \
 *  -Dcom.sun.management.jmxremote.authenticate=false \
 *  -Dcom.sun.management.jmxremote.ssl=false
 */
public class JmxDemoServer {

  public static void main(String[] args) throws Exception {
    ObjectName name = new ObjectName("demo:type=Hello");

    //The bean can be seen with JConsole or other JMX tool
    Hello mbean = new Hello();

    ManagementFactory.getPlatformMBeanServer().registerMBean(mbean, name);

    System.out.println("HelloMBean registered. Attach with JConsole and inspect.");
    Thread.sleep(Long.MAX_VALUE);
  }
}
