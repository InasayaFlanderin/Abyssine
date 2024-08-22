package org.inasayaflanderin.abyssine.config;

import jdk.jfr.Recording;
import lombok.Getter;
import lombok.extern.java.Log;
import org.inasayaflanderin.abyssine.diagnostic.SystemCollector;

import java.io.Serial;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

@Log
public class AbyssineConfigurations implements Serializable {
    @Serial
    private static final long serialVersionUID = -2859598628762108235L;
    private static AbyssineConfigurations configurations;
    @Getter
    private final Recording systemRecorder;
    @Getter
    private final ThreadMXBean threadRecorder;

    private AbyssineConfigurations() {
        systemRecorder = new Recording();
        threadRecorder = ManagementFactory.getThreadMXBean();
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
}