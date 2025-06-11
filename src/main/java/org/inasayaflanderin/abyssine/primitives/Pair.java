package org.inasayaflanderin.abyssine.primitives;

import lombok.With;

import java.io.Serializable;

/**
 * @param first first element of the pair
 * @param second second element of the pair
 * @param <F> first element type
 * @param <S> second element type
 */
@With
public record Pair<F, S>(F first, S second) implements Serializable {
}