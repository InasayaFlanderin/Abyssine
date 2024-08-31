package org.inasayaflanderin.abyssine.primitives;

public record Quin<F, S, T, H, C>(F getFirst, S getSecond, T getThird, H getFourth, C getFifth) {
    public Quin<F, S, T, H, C> withSecond(S newValue) {
        return new Quin<>(this.getFirst(), newValue, this.getThird(), this.getFourth(), this.getFifth());
    }

    public Quin<F, S, T, H, C> withFirst(F newValue) {
        return new Quin<>(newValue, this.getSecond(), this.getThird(), this.getFourth(), this.getFifth());
    }

    public Quin<F, S, T, H, C> withThird(T newValue) {
        return new Quin<>(this.getFirst(), this.getSecond(), newValue, this.getFourth(), this.getFifth());
    }

    public Quin<F, S, T, H, C> withFourth(H newValue) {
        return new Quin<>(this.getFirst(), this.getSecond(), this.getThird(), newValue, this.getFifth());
    }

    public Quin<F, S, T, H, C> withFifth(C newValue) {
        return new Quin<>(this.getFirst(), this.getSecond(), this.getThird(), this.getFourth(), newValue);
    }
}
