package org.inasayaflanderin.abyssine.primitives;

public record Triple<F, S, T>(F getFirst, S getSecond, T getThird) {
    public Triple<F, S, T> withSecond(S newValue) {
        return new Triple<>(this.getFirst(), newValue, this.getThird());
    }

    public Triple<F, S, T> withFirst(F newValue) {
        return new Triple<>(newValue, this.getSecond(), this.getThird());
    }

    public Triple<F, S, T> withThird(T newValue) {
        return new Triple<>(this.getFirst(), this.getSecond(), newValue);
    }
}
