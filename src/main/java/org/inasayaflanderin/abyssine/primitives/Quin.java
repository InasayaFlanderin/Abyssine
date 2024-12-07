package org.inasayaflanderin.abyssine.primitives;

import lombok.Value;
import lombok.With;

import java.io.Serial;
import java.io.Serializable;

@Value
@With
public class Quin<F, S, T, H, C> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1470872958785798357L;

    F first;
    S second;
    T third;
    H fourth;
    C fifth;
}