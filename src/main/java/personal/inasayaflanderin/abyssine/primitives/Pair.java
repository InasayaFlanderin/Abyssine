package personal.inasayaflanderin.abyssine.primitives;

import java.io.Serializable;

import lombok.With;

@With
public record Pair<F, S>(F first, S second) implements Serializable {}
