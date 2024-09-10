package org.inasayaflanderin.abyssine.diagnostic;

import com.sun.management.OperatingSystemMXBean;
import lombok.Getter;
import lombok.extern.java.Log;

import javax.management.MBeanServer;
import javax.management.ObjectName;

@Log
public class SystemCollector {
    private static final String[] OSBased = {"X11", "Mac", "Windows", "Unknown"};
    private final OperatingSystemMXBean sRecorder;
    private final MBeanServer MBServer;
    private final Runtime JVMRt;
    @Getter
    private int PID;
    private final int OSType;

    SystemCollector() {
        this.sRecorder = AbyssineConfigurations.getConfigurations().getSystemRecorder();
        this.MBServer = AbyssineConfigurations.getConfigurations().getMBServer();
        this.JVMRt = Runtime.getRuntime();
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

        var OSName = getOSName();

        if(OSName.startsWith("Linux") || OSName.startsWith("SunOS") || OSName.startsWith("Solaris") || OSName.startsWith("FreeBSD") || OSName.startsWith("OpenBSD")) this.OSType = 0;
        else if(OSName.startsWith("Mac") || OSName.startsWith("Darwin")) this.OSType = 1;
        else if(OSName.startsWith("Windows") && !OSName.startsWith("Windows CE")) this.OSType = 2;
        else this.OSType = 3;
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

    public String getOSName() {
        return this.sRecorder.getName();
    }

    public String getOSVersion() {
        return this.sRecorder.getVersion();
    }

    public String getOSArch() {
        return this.sRecorder.getArch();
    }

    public int getSystemAvailableProcessors() {
        return this.JVMRt.availableProcessors();
    }

    public long getFreeMemory() {
        return this.JVMRt.freeMemory();
    }

    public long getTotalMemory() {
        return this.JVMRt.totalMemory();
    }

    public long getMaxMemory() {
        return this.JVMRt.maxMemory();
    }

    public long getUsedMemory() {
        return getTotalMemory() - getFreeMemory();
    }

    public long getAvailableMemory() {
        return getMaxMemory() - getUsedMemory();
    }
}
