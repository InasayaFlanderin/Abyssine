package org.inasayaflanderin.abyssine.primitives;

public record Quad<F, S, T, H>(F getFirst, S getSecond, T getThird, H getFourth) {
    public Quad<F, S, T, H> withSecond(S newValue) {
        return new Quad<>(this.getFirst(), newValue, this.getThird(), this.getFourth());
    }

    public Quad<F, S, T, H> withFirst(F newValue) {
        return new Quad<>(newValue, this.getSecond(), this.getThird(), this.getFourth());
    }

    public Quad<F, S, T, H> withThird(T newValue) {
        return new Quad<>(this.getFirst(), this.getSecond(), newValue, this.getFourth());
    }

    public Quad<F, S, T, H> withFourth(H newValue) {
        return new Quad<>(this.getFirst(), this.getSecond(), this.getThird(), newValue);
    }
}
