package org.inasayaflanderin.abyssine.diagnostic;

import lombok.extern.java.Log;

@Log
public class SystemCollector {
    public SystemCollector() {

    }

    public String getSystemIdentityName(final Object o) {
        return o.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(o));
    }

    public String getSystemIdentity(final Object o) {
        return o.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(o));
    }
}
