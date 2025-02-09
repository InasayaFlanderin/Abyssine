package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Square extends MSWS {
    @Serial
    private static final long serialVersionUID = -7483589787447159440L;

    private int state;

    public Square(int state) {
        super();

        if(state <= 0) throw new IllegalArgumentException("State must be larger than 0");

        this.state = state;
    }

    public Square(long seed, int state) {
        super(seed);

        if(state <= 0) throw new IllegalArgumentException("State must be larger than 0");

        this.state = state;
    }

    public double next() {
        for (int i = 0; i < this.state - 1; i++) super.next();

        return super.next();
    }

    public void setState(int state) {
        if(state <= 0) throw new IllegalArgumentException("State must be larger than 0");

        this.state = state;
    }
}