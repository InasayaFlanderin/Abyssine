package org.inasayaflanderin.abyssine.primitives;

import lombok.Value;
import lombok.With;

import java.io.Serial;
import java.io.Serializable;

@Value
@With
public class Quad<F, S, T, H> implements Serializable {
    @Serial
    private static final long serialVersionUID = -5227512353699456705L;

    F first;
    S second;
    T third;
    H fourth;
}