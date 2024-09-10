package org.inasayaflanderin.abyssine.parallel.work;

import java.io.Serializable;

public interface Tasks<R> extends Serializable {
    R getResult();
    void setResult(R result);
    void addError(Throwable t);
    void setID(String s);
    String getID();
}