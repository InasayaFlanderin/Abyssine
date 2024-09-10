module Abyssine {
    requires lombok;
    requires java.logging;
    requires java.compiler;
    requires jdk.jfr;
    requires java.management;
    requires jdk.management;

    exports org.inasayaflanderin.abyssine;
    exports org.inasayaflanderin.abyssine.math.number;
    exports org.inasayaflanderin.abyssine.memory.buffer;
    exports org.inasayaflanderin.abyssine.memory.cache;
    exports org.inasayaflanderin.abyssine.parallel;
    exports org.inasayaflanderin.abyssine.primitives;
    exports org.inasayaflanderin.abyssine.diagnostic;
}