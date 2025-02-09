package org.inasayaflanderin.abyssine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogTest {
    public static void main(String[] args) {
        log.error("Hello, World!");
        log.info("Hello, World!");
        log.debug("Hello, World!");
        log.warn("Hello, World!");
        log.trace("Hello, World!");
    }
}