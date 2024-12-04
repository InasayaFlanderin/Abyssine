package org.inasayaflanderin.abyssine.primitives;

import lombok.Value;
import lombok.With;

@Value
@With
public class Triple<F, S, T> {
    F first;
    S second;
    T third;
}