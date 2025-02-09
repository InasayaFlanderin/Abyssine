package org.inasayaflanderin.abyssine.exception;

public class ImpossibleException extends RuntimeException {
    public ImpossibleException() {
        super("""
                This should never happen. If it do, please report it to me.
                Contact info : inasayaflanderin on discord
                """);
    }
}