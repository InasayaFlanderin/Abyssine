package org.inasayaflanderin.abyssine.primitives;

public record Pair<F, S>(F getFirst, S getSecond) {
    public Pair<F, S> withSecond(S newValue) {
        return new Pair<>(this.getFirst(), newValue);
    }

    public Pair<F, S> withFirst(F newValue) {
        return new Pair<>(newValue, this.getSecond());
    }
}