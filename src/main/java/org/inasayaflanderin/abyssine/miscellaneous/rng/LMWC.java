package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.Getter;

import java.io.Serial;
import java.util.LinkedList;

public class LMWC extends LCG {
    @Serial
    private static final long serialVersionUID = -2418898204426034523L;

    protected LinkedList<Double> lagQueue;
    @Getter
    private int lag;

    public LMWC(double multiplier, double increment, double modulus, int lag) {
        super(multiplier, increment, modulus);

        initialLag(lag);
    }

    public LMWC(double seed, double multiplier, double increment, double modulus, int lag) {
        super(seed, multiplier, increment, modulus);

        initialLag(lag);
    }

    public double next() {
        setSeed(lagQueue.remove());

        var result = super.next();
        this.lagQueue.add(result);

        setIncrement(result);

        return result;
    }

    public void setLag(int lag) {
        if(lag <= 0) throw new IllegalArgumentException("Lag must be greater than 0");

        this.lag = lag;

        while(this.lagQueue.size() < lag) this.lagQueue.add(super.next());
        while(this.lagQueue.size() > lag) this.lagQueue.removeFirst();
    }

    private void initialLag(int lag) {
        if(lag <= 0) throw new IllegalArgumentException("Lag must be greater than 0");

        this.lag = lag;
        this.lagQueue = new LinkedList<>();

        for (int i = 0; i < lag; i++) this.lagQueue.add(super.next());
    }
}
