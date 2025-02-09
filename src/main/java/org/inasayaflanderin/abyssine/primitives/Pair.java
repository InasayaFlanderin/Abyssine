package org.inasayaflanderin.abyssine.primitives;

import lombok.With;

import java.io.Serial;
import java.io.Serializable;

@With
public record Pair<F, S>(F first, S second) implements Serializable {
    @Serial
    private static final long serialVersionUID = 5315635149774553553L;
}