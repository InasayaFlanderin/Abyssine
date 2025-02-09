package org.inasayaflanderin.abyssine.primitives;

import lombok.With;

import java.io.Serial;
import java.io.Serializable;

@With
public record Quin<F, S, T, H, C>(F first, S second, T third, H fourth, C fifth) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1470872958785798357L;
}