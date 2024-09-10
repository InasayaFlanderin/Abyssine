package org.inasayaflanderin.abyssine.diagnostic;

import com.sun.management.OperatingSystemMXBean;
import com.sun.management.ThreadMXBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import javax.management.MBeanServer;
import java.io.Serial;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.LinkedList;

@Log
public class AbyssineConfigurations implements Serializable {
    @Serial
    private static final long serialVersionUID = -2859598628762108235L;
    private static SystemCollector systemCollector;
    private static CPUCollector CPUc;
    private static AbyssineConfigurations configurations;
    @Getter
    private final OperatingSystemMXBean systemRecorder;
    @Getter
    private final ThreadMXBean threadRecorder;
    @Getter
    private final MBeanServer MBServer;
    @Getter @Setter
    private static long CPUInterval = 1000L;
    @Getter
    private static LinkedList<String> serverList = new LinkedList<>();

    private AbyssineConfigurations() {
        this.systemRecorder = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.threadRecorder = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        this.MBServer = ManagementFactory.getPlatformMBeanServer();
    }

    public static AbyssineConfigurations getConfigurations() {
        if(configurations == null) {
            synchronized(AbyssineConfigurations.class) {
                configurations = new AbyssineConfigurations();
            }
        }

        return configurations;
    }

    public static SystemCollector getSystemCollector() {
        if(systemCollector == null) {
            synchronized(SystemCollector.class) {
                systemCollector = new SystemCollector();
            }
        }

        return systemCollector;
    }

    public static CPUCollector getCpuCollector() {
        if(CPUc == null) {
            synchronized(CPUCollector.class) {
                CPUc = new CPUCollector();
            }
        }

        return CPUc;
    }
}