package org.inasayaflanderin.abyssine.diagnostic;

import lombok.extern.java.Log;
import org.inasayaflanderin.abyssine.config.AbyssineConfigurations;
import org.inasayaflanderin.abyssine.config.AbyssineProperties;
import org.inasayaflanderin.abyssine.primitives.Pair;

@Log
public class SystemCollector {
    private static SystemCollector systemCollector;
    private AbyssineProperties properties;

    public SystemCollector() {
        var systemProperties = System.getProperties();
        properties = AbyssineConfigurations.getConfigurations().getProperties();

        properties.addProperty("specification_version", systemProperties.get("java.specification.version"), AbyssineProperties.PropertiesType.LOCAL);
        properties.addProperty("specification_vm_version", systemProperties.get("java.vm.specification.version"), AbyssineProperties.PropertiesType.LOCAL);
        properties.addProperty("os", systemProperties.get("os.name"), AbyssineProperties.PropertiesType.LOCAL);
        properties.addProperty("os_version", systemProperties.get("os.version"), AbyssineProperties.PropertiesType.LOCAL);
        properties.addProperty("os_arch", systemProperties.get("os.arch"), AbyssineProperties.PropertiesType.LOCAL);
        loadSystem();
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

    public static String getSystemIdentityName(final Object o) {
        return o.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(o));
    }

    public static String getSystemIdentity(final Object o) {
        return o.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(o));
    }
}
