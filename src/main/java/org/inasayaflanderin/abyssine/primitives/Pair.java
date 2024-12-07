package org.inasayaflanderin.abyssine.primitives;

import lombok.Value;
import lombok.With;

import java.io.Serial;
import java.io.Serializable;

@Value
@With
public class Pair<F, S> implements Serializable {
    @Serial
    private static final long serialVersionUID = 5315635149774553553L;

    F first;
    S second;
}