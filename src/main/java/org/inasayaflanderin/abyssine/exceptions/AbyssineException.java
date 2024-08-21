package org.inasayaflanderin.abyssine.exceptions;

public class AbyssineException extends RuntimeException {
    public AbyssineException(String message) {
        super("Abyssine: " + message);
    }
}
