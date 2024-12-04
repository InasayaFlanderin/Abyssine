package org.inasayaflanderin.abyssine.primitives;

import lombok.Value;
import lombok.With;

@Value
@With
public class Quad<F, S, T, H> {
    F first;
    S second;
    T third;
    H fourth;
}