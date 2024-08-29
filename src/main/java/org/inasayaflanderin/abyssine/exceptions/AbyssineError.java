package org.inasayaflanderin.abyssine.exceptions;

import java.io.Serial;

public class AbyssineError extends Error{
    @Serial
    private static final long serialVersionUID = -7647216906700610894L;
    public AbyssineError(String msg) {
        super("Abyssine Error: "  + msg);
    }
}
