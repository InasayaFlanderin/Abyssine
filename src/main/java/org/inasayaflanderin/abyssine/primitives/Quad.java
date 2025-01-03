package org.inasayaflanderin.abyssine.primitives;

import lombok.With;

import java.io.Serial;
import java.io.Serializable;

@With
public record Quad<F, S, T, H>(F first, S second, T third, H fourth) implements Serializable {
    @Serial
    private static final long serialVersionUID = -5227512353699456705L;
}