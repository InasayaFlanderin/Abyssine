package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.Getter;

import java.io.Serial;
import java.util.LinkedList;
import java.util.function.BiFunction;

public abstract class LFG implements RandomGenerators {
    @Serial
    private static final long serialVersionUID = 4644751270481145040L;

    protected BiFunction<Double, Double, Double> add = Double::sum;
    protected BiFunction<Double, Double, Double> multiply = (a, b) -> a * b;
    LinkedList<Double> lagQueue;
    @Getter
    private double seed;
    @Getter
    private int firstLagged;
    @Getter
    private int secondLagged;

    protected LFG(int firstLagged, int secondLagged) {
        this(System.currentTimeMillis(), firstLagged, secondLagged);
    }

    protected LFG(double seed, int firstLagged, int secondLagged) {
        if(firstLagged <= 0) throw new IllegalArgumentException("First lagged must be larger than 0");
        if(secondLagged <= 0) throw new IllegalArgumentException("Second lagged must be larger than 0");

        this.seed = seed;
        this.firstLagged = firstLagged;
        this.secondLagged = secondLagged;
        this.lagQueue = new LinkedList<>();
        this.lagQueue.add(0.0);
        this.lagQueue.add(seed);

        for(int i = 0; i < Math.max(firstLagged, secondLagged) - 2; i++) this.lagQueue.add(this.lagQueue.getLast() + this.lagQueue.get(this.lagQueue.size() - 2));
    }

    public abstract double next();

    protected double next(BiFunction<Double, Double, Double> operator) {
        double result = Math.abs(operator.apply(this.lagQueue.get(this.lagQueue.size() - this.firstLagged), this.lagQueue.get(this.lagQueue.size() - this.secondLagged)));

        if(Double.isInfinite(result)) {
            setSeed(System.currentTimeMillis());

            return next(operator);
        }

        this.lagQueue.add(result);

        return result / (result % 10 == 0 ? result : Math.pow(10, Math.ceil(Math.log10(result))));
    }

    public void setSeed(double seed) {
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
