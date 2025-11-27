package com.example.training.bean;// MonitoredApp.java
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class MonitoredApp {
    public static void main(String[] args) throws Exception {
        // Register custom MBean: app:type=Work,name=Load
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName on = new ObjectName("app:type=Work,name=Load");
        Work work = new Work();
        mbs.registerMBean(work, on);

        // Print PID for the attach client (format: "<pid>@<host>")
        String runtime = ManagementFactory.getRuntimeMXBean().getName();
        String pid = runtime.substring(0, runtime.indexOf('@'));
        System.out.println("MonitoredApp PID = " + pid);
        System.out.println("Start jconsole and connect to this PID, or run MonitorClient " + pid + " and look on MBean tab");

        // Keep producing some load so we have something to see in jconsole
        while (true) {
            work.doWork(1000);      // busy ~200 ms
            Thread.sleep(300);     // rest ~300 ms
        }
    }
}
