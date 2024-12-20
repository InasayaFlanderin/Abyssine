package org.inasayaflanderin.abyssine.primitives;

import lombok.With;

import java.io.Serial;
import java.io.Serializable;

@With
public record Triple<F, S, T>(F first, S second, T third) implements Serializable {
    @Serial
    private static final long serialVersionUID = 4963276767505130172L;

}