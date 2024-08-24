package org.inasayaflanderin.abyssine.config;

import com.sun.management.OperatingSystemMXBean;
import com.sun.management.ThreadMXBean;
import lombok.Getter;
import lombok.extern.java.Log;
import org.inasayaflanderin.abyssine.diagnostic.CPUCollector;
import org.inasayaflanderin.abyssine.diagnostic.SystemCollector;

import javax.management.MBeanServer;
import java.io.Serial;
import java.io.Serializable;
import java.lang.management.ManagementFactory;

@Getter
@Log
public class AbyssineConfigurations implements Serializable {
    @Serial
    private static final long serialVersionUID = -2859598628762108235L;
    private static AbyssineConfigurations configurations;
    private final OperatingSystemMXBean systemRecorder;
    private final ThreadMXBean threadRecorder;
    private final MBeanServer MBServer;

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

    public AbyssineProperties getProperties() {
        return AbyssineProperties.getProperties();
    }

    public SystemCollector getSystemCollector() {
        return SystemCollector.getSystemCollector();
    }

    public CPUCollector getCPUCollector() {
        return CPUCollector.getCpuCollector();
    }
}