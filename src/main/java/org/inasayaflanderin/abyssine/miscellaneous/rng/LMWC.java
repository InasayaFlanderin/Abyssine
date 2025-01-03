package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.util.LinkedList;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LMWC extends LCG {
    @Serial
    private static final long serialVersionUID = -2418898204426034523L;

    @EqualsAndHashCode.Exclude
    protected LinkedList<Long> lagQueue;
    @Getter
    private int lag;

    public LMWC(long multiplier, long increment, long modulus, int lag) {
        super(multiplier, increment, modulus);

        initialLag(lag);
    }

    public LMWC(long seed, long multiplier, long increment, long modulus, int lag) {
        super(seed, multiplier, increment, modulus);

        initialLag(lag);
    }

    public double next() {
        setSeed(lagQueue.removeFirst());

        var result = super.next() * getModulus();
        this.lagQueue.add((long) result);

        setIncrement((long) result);

        return result / getModulus();
    }

    public void setLag(int lag) {
        if(lag <= 0) throw new IllegalArgumentException("Lag must be greater than 0");

        this.lag = lag;

        while(this.lagQueue.size() < this.lag) this.lagQueue.add((long) super.next() * this.getModulus());
        while(this.lagQueue.size() > this.lag) this.lagQueue.removeFirst();
    }

    private void initialLag(int lag) {
        if(lag <= 0) throw new IllegalArgumentException("Lag must be greater than 0");

        this.lag = lag;
        this.lagQueue = new LinkedList<>();

        for (int i = 0; i < lag; i++) this.lagQueue.add((long) (super.next() * super.getModulus()));
    }

    public void setSeed(long seed) {
        super.setSeed(seed);

        this.lagQueue.clear();
        for (int i = 0; i < lag; i++) this.lagQueue.add((long) super.next() * this.getModulus());
    }
}