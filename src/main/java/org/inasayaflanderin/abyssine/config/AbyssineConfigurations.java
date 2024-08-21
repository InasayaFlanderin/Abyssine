package org.inasayaflanderin.abyssine.config;

import jdk.jfr.Recording;
import lombok.Getter;
import org.inasayaflanderin.abyssine.diagnostic.SystemCollector;

import java.io.Serial;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class AbyssineConfigurations implements Serializable {
    @Serial
    private static final long serialVersionUID = -2859598628762108235L;
    @Getter
    private static AbyssineConfigurations configuration;
    @Getter
    private final Recording systemRecorder;
    @Getter
    private final ThreadMXBean threadRecorder;
    @Getter
    private Properties properties;
    @Getter
    private final SystemCollector systemCollector;

    static {
        configuration = new AbyssineConfigurations();
    }

    private AbyssineConfigurations() {
        systemRecorder = new Recording();
        systemCollector = new SystemCollector();
        threadRecorder = ManagementFactory.getThreadMXBean();
    }
}