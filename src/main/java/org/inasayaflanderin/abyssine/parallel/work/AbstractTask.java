package org.inasayaflanderin.abyssine.parallel.work;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.io.Serial;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

@Log
public abstract class AbstractTask<R> implements Tasks<R> {
    @Serial
    private static final long serialVersionUID = 6716215965762270636L;
    @Getter
    @Setter
    private R result;
    @Setter
    private Consumer<Throwable> errorHandler = throwable -> log.severe(throwable.toString());
    private List<Throwable> errors = new LinkedList<>();
    @Getter
    @Setter
    private String ID;

    public abstract void run();

    public void addError(Throwable t) {
        errors.add(t);
    }
}