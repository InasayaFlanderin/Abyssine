package org.inasayaflanderin.abyssine.diagnostic;

import lombok.Getter;
import lombok.extern.java.Log;
import org.inasayaflanderin.abyssine.config.AbyssineConfigurations;
import org.inasayaflanderin.abyssine.config.AbyssineProperties;

import javax.management.MBeanServer;
import javax.management.ObjectName;

@Log
public class SystemCollector {
    private static final String[] OSBased = {"X11", "Mac", "Windows", "Unknown"};
    private static SystemCollector systemCollector;
    private final AbyssineProperties properties;
    private final MBeanServer MBServer;
    @Getter
    private int PID;
    private final int OSType;

    @SuppressWarnings("unchecked")
    public SystemCollector() {
        var sRecorder = AbyssineConfigurations.getConfigurations().getSystemRecorder();
        properties = AbyssineConfigurations.getConfigurations().getProperties();

        properties.addProperty("os", sRecorder.getName(), AbyssineProperties.PropertiesType.LOCAL);
        properties.addProperty("os_version", sRecorder.getVersion(), AbyssineProperties.PropertiesType.LOCAL);
        properties.addProperty("os_arch", sRecorder.getArch(), AbyssineProperties.PropertiesType.LOCAL);
        loadSystem();

        this.MBServer = AbyssineConfigurations.getConfigurations().getMBServer();
        this.PID = -1;

        try {
            ObjectName runTimeMBean = new ObjectName("java.lang:type=Runtime");
            String name = (String) this.MBServer.getAttribute(runTimeMBean, "Name");
            int idx = name.indexOf("@");

            if(idx > 0) {
                this.PID = Integer.parseInt(name.substring(0, idx));
                log.info("Process name: " + name + " ,PID: " + this.PID);
            }
        } catch(Exception e) {
            log.severe("Could not get PID");
            log.severe(e.toString());
        }

        var OSName = (String)  properties.getProperty("os");

        if(OSName.startsWith("Linux") || OSName.startsWith("SunOS") || OSName.startsWith("Solaris") || OSName.startsWith("FreeBSD") || OSName.startsWith("OpenBSD")) this.OSType = 0;
        else if(OSName.startsWith("Mac") || OSName.startsWith("Darwin")) this.OSType = 1;
        else if(OSName.startsWith("Windows") && !OSName.startsWith("Windows CE")) this.OSType = 2;
        else this.OSType = 3;
    }

    public static SystemCollector getSystemCollector() {
        if(systemCollector == null) {
            synchronized(SystemCollector.class) {
                systemCollector = new SystemCollector();
            }
        }

        return systemCollector;
    }

    private void loadSystem() {
        var JVMRt = Runtime.getRuntime();
        properties.addProperty("system_available_processors", JVMRt.availableProcessors(), AbyssineProperties.PropertiesType.LOCAL);
        properties.addProperty("free_memory", JVMRt.freeMemory(), AbyssineProperties.PropertiesType.LOCAL);
        properties.addProperty("total_memory", JVMRt.totalMemory(), AbyssineProperties.PropertiesType.LOCAL);
        properties.addProperty("max_memory", JVMRt.maxMemory(), AbyssineProperties.PropertiesType.LOCAL);
        var usedMemory = JVMRt.totalMemory() - JVMRt.freeMemory();
        properties.addProperty("used_memory", usedMemory, AbyssineProperties.PropertiesType.LOCAL);
        properties.addProperty("available_memory", JVMRt.maxMemory() - usedMemory, AbyssineProperties.PropertiesType.LOCAL);
    }

    public String getOSType() {
        return OSBased[this.OSType];
    }

    public boolean isX11OS() {
        return this.OSType == 0;
    }

    public boolean isMacOS() {
        return this.OSType == 1;
    }

    public boolean isWindowsOS() {
        return this.OSType == 2;
    }

    public boolean isUnknownOS() {
        return this.OSType == 3;
    }

    public static String getSystemIdentityName(final Object o) {
        return o.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(o));
    }

    public static String getSystemIdentity(final Object o) {
        return o.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(o));
    }
}
