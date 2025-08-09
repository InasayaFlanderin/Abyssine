package personal.inasayaflanderin.abyssine.primtivies;

import java.io.Serializable;

import lombok.With;

@With
public record Pair<F, S>(F first, S second) implements Serializable {}
