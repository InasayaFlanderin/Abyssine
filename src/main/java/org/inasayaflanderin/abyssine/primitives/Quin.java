package org.inasayaflanderin.abyssine.primitives;

import lombok.Value;
import lombok.With;

@Value
@With
public class Quin<F, S, T, H, C> {
    F first;
    S second;
    T third;
    H fourth;
    C fifth;
}