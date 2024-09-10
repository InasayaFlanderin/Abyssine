package org.inasayaflanderin.abyssine.diagnostic;

import com.sun.management.OperatingSystemMXBean;
import com.sun.management.ThreadMXBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.util.concurrent.atomic.AtomicLong;

@Log
public class CPUCollector implements Runnable {
    private final OperatingSystemMXBean sRecorder;
    private static ThreadMXBean tRecorder;
    private long interval;
    private final AtomicLong totalUserCPUTime = new AtomicLong(0);
    private final AtomicLong userCPULoad = new AtomicLong(0);
    private final AtomicLong totalJVMCPUTime = new AtomicLong(0);
    private final AtomicLong JVMCPULoad = new AtomicLong(0);
    private final AtomicLong systemCPULoad = new AtomicLong(0);
    @Getter @Setter
    private boolean stopped = false;

    static {
        final Thread CPUCollectorThread = Thread.ofVirtual().name("CPU Collector Thread").unstarted(AbyssineConfigurations.getCpuCollector());
        if(AbyssineConfigurations.getCpuCollector().isThreadCPUTimeSupported()) {
            try {
                tRecorder.setThreadCpuTimeEnabled(true);
                log.info("Thread CPU time enabled");
            } catch(Exception e) {
                log.severe(e.toString());
            }
        }
        CPUCollectorThread.setDaemon(true);
        CPUCollectorThread.start();
    }

    CPUCollector() {
        this.sRecorder = AbyssineConfigurations.getConfigurations().getSystemRecorder();
        tRecorder = AbyssineConfigurations.getConfigurations().getThreadRecorder();
    }

    public boolean isThreadCPUTimeSupported() {
        return tRecorder.isThreadCpuTimeSupported();
    }

    public boolean isCPUTimeEnabled() {
        return tRecorder.isThreadCpuTimeEnabled();
    }

    public void run() {
        try {
            this.interval = AbyssineConfigurations.getCPUInterval();
            var timeSleep = 0L;
            Thread.sleep(interval);
            while(!stopped) {
                timeSleep = System.currentTimeMillis() - timeSleep;
                var oldCPUTime = totalUserCPUTime.get();
                var threadIds = tRecorder.getAllThreadIds();
                var recordTime = 0L;

                for(var id : threadIds) {
                    var threadCPUTime = tRecorder.getThreadCpuTime(id);
                    if(threadCPUTime >= 0) recordTime += threadCPUTime;
                }

                var cpuTime = recordTime / 1000000;
                totalUserCPUTime.set(cpuTime);
                userCPULoad.set(Double.doubleToLongBits((double) (cpuTime - oldCPUTime) / (double) (timeSleep * AbyssineConfigurations.getSystemCollector().getSystemAvailableProcessors())));
                totalJVMCPUTime.set(sRecorder.getProcessCpuTime());
                JVMCPULoad.set(Double.doubleToLongBits(sRecorder.getProcessCpuLoad()));
                systemCPULoad.set(Double.doubleToLongBits(sRecorder.getCpuLoad()));
            }
        } catch(Exception e) {
            log.severe(e.toString());
        }
    }

    public long getThreadCPUTime(int threadId) {
        return tRecorder.getThreadCpuTime(threadId);
    }

    public long getTotalUserCPUTime() {
        return totalUserCPUTime.get();
    }

    public long getTotalJVMCPUTime() {
        return totalJVMCPUTime.get();
    }

    public double getUserCPULoad() {
        var result = Double.doubleToLongBits(userCPULoad.get());
        if(result > 1) return 1;
        if(result < 0) return 0;

        return result;
    }

    public double getJVMCPULoad() {
        var result = Double.doubleToLongBits(JVMCPULoad.get());
        if(result > 1) return 1;
        if(result < 0) return 0;

        return result;
    }

    public double getSystemCPULoad() {
        var result = Double.doubleToLongBits(systemCPULoad.get());
        if(result > 1) return 1;
        if(result < 0) return 0;

        return result;
    }
}
