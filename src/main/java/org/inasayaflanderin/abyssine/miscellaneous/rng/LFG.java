package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.util.LinkedList;
import java.util.function.BiFunction;

@EqualsAndHashCode
@ToString
public abstract class LFG implements RandomGenerators {
    @Serial
    private static final long serialVersionUID = 4644751270481145040L;

    protected static final BiFunction<Long, Long, Long> add = Long::sum;
    protected static final BiFunction<Long, Long, Long> multiply = (a, b) -> a * b;
    protected static final BiFunction<Long, Long, Long> subtract = (a, b) -> a - b;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    protected LinkedList<Long> lagQueue;
    @Getter
    @EqualsAndHashCode.Exclude
    private long seed;
    @Getter
    private int firstLagged;
    @Getter
    private int secondLagged;

    protected LFG(int firstLagged, int secondLagged) {
        this(System.currentTimeMillis(), firstLagged, secondLagged);
    }

    protected LFG(long seed, int firstLagged, int secondLagged) {
        if(firstLagged <= 0) throw new IllegalArgumentException("First lagged must be larger than 0");
        if(secondLagged <= 0) throw new IllegalArgumentException("Second lagged must be larger than 0");

        this.seed = seed;
        this.firstLagged = firstLagged;
        this.secondLagged = secondLagged;
        this.lagQueue = new LinkedList<>();
        this.lagQueue.add(1L);
        this.lagQueue.add(seed);

        for(int i = 0; i < Math.max(firstLagged, secondLagged) - 2; i++) this.lagQueue.add(this.lagQueue.getLast() + this.lagQueue.get(this.lagQueue.size() - 2));
    }

    public abstract double next();

    protected long calculate(BiFunction<Long, Long, Long> operator) {
        long result = Math.abs(operator.apply(this.lagQueue.get(this.lagQueue.size() - this.firstLagged), this.lagQueue.get(this.lagQueue.size() - this.secondLagged)));

        this.lagQueue.removeFirst();

        if(result == 0) {
            this.lagQueue.add(System.currentTimeMillis());

            return calculate(operator);
        } else {
            this.lagQueue.add(result);
        }

        return result;
    }

    protected double normalize(long result) {
        return (double) Math.abs(result % Long.MAX_VALUE) / Long.MAX_VALUE;
    }

    public void setSeed(long seed) {
        this.lagQueue.clear();
        this.lagQueue.add(seed);
        this.seed = seed;
        this.lagQueue.add(seed);

        for(int i = 0; i < Math.max(firstLagged, secondLagged) - 2; i++) this.lagQueue.add(this.lagQueue.getLast() + this.lagQueue.get(this.lagQueue.size() - 2));
    }

    public void setFirstLagged(int firstLagged) {
        if(firstLagged <= 0) throw new IllegalArgumentException("First lagged must be greater than 0");

        this.firstLagged = firstLagged;

        while(this.lagQueue.size() < firstLagged) this.lagQueue.add(this.lagQueue.getLast() + this.lagQueue.get(this.lagQueue.size() - 2));
        while(this.lagQueue.size() > firstLagged) this.lagQueue.removeFirst();
    }

    public void setSecondLagged(int secondLagged) {
        if(secondLagged <= 0) throw new IllegalArgumentException("Second lagged must be greater than 0");

        this.secondLagged = secondLagged;

        while(this.lagQueue.size() < secondLagged) this.lagQueue.add(this.lagQueue.getLast() + this.lagQueue.get(this.lagQueue.size() - 2));
        while(this.lagQueue.size() > secondLagged) this.lagQueue.removeFirst();
    }
}