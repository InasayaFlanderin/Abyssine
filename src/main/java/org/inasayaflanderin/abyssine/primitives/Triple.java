package org.inasayaflanderin.abyssine.primitives;

import lombok.Value;
import lombok.With;

import java.io.Serial;
import java.io.Serializable;

@Value
@With
public class Triple<F, S, T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 4963276767505130172L;

    F first;
    S second;
    T third;
}