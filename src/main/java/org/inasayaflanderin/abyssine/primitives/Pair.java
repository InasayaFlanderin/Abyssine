package org.inasayaflanderin.abyssine.primitives;

import lombok.Value;
import lombok.With;

@Value
@With
public class Pair<F, S> {
    F first;
    S second;
}