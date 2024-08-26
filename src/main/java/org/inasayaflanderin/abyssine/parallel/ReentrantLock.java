package org.inasayaflanderin.abyssine.parallel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.inasayaflanderin.abyssine.diagnostic.SystemCollector;
import org.inasayaflanderin.abyssine.exceptions.AbyssineException;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@ToString
public class ReentrantLock implements Lock, Serializable {
    @Serial
    private static final long serialVersionUID = 685322557851045861L;
    private static int numberOfNull = 0;
    @Getter
    private final String name;
    private final Synchronizer sync;

    public ReentrantLock() {
        this(null, null, false);
    }

    public ReentrantLock(String name) {
        this(name, null, false);

    }

    public ReentrantLock(Integer maximumLock) {
        this(null, maximumLock, false);
    }

    public ReentrantLock(Boolean fair) {
        this(null, null, fair);
    }

    public ReentrantLock(String name, Integer maximumLock) {
        this(name, maximumLock, false);
    }

    public ReentrantLock(String name, Boolean fair) {
        this(name, null, fair);
    }

    public ReentrantLock(Integer maximumLock, Boolean fair) {
        this(null, maximumLock, fair);
    }

    public ReentrantLock(String name, Integer maximumLock, Boolean fair) {
        this.sync = new Synchronizer(name == null ? "ReentrantLock" + (++numberOfNull): name, maximumLock == null ? Integer.MAX_VALUE : maximumLock, fair != null && fair);
        this.name = sync.getName();
    }

    public void lock() {
        sync.acquire(1);
    }

    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    public boolean tryLock(final int numLock) {
        return sync.tryAcquire(numLock);
    }

    public boolean tryLock(long time, TimeUnit tu) throws InterruptedException {
        return sync.tryAcquireNanos(1, tu.toNanos(time));
    }

    public boolean tryLock(final int numLock, long time, TimeUnit tu) throws InterruptedException {
        return sync.tryAcquireNanos(numLock, tu.toNanos(time));
    }

    public void unlock() {
        sync.release(1);
    }

    public void unlock(final int numLock) {
        sync.release(numLock);
    }

    public Condition newCondition() {
        return sync.newCondition();
    }

    public int getHoldCount() {
        return sync.getHoldCount();
    }

    public boolean isHeldByCurrentThread() {
        return sync.isHeldExclusively();
    }

    public void setMaximumLock(int numLock) {
        sync.setMaximumLock(numLock);
    }

    public boolean isFair() {
        return sync.isFair();
    }

    public void setFair(Boolean fair) {
        sync.setFair(fair != null && fair);
    }

    @ToString
    private static class Synchronizer extends AbstractQueuedSynchronizer {
        @Serial
        private static final long serialVersionUID = 8859732343203211526L;
        @Getter
        private final String name;
        private int maximumLock;
        @Getter @Setter private boolean fair;

        Synchronizer(String name, int maximumLock, boolean fair) {
            this.name = SystemCollector.getSystemIdentity(name) + "[" + name + "]";
            this.maximumLock = maximumLock;
            this.fair = fair;
        }

        public boolean tryAcquire(final int acquires) {
            if(acquires <= 0) throw new IllegalArgumentException("Acquire zero or negative number of lock");
            if(acquires > this.maximumLock) throw new AbyssineException("Acquiring more than maximum");

            final var currentThread = Thread.currentThread();
            final var currentState = getState();

            if(currentState == 0) {
                if(!fair || hasQueuedPredecessors())
                    if(compareAndSetState(0, acquires)) {
                        setExclusiveOwnerThread(currentThread);
                        return true;
                    }
            } else if(getExclusiveOwnerThread() == currentThread) {
                var newState = currentState + acquires;

                if(newState < 0) throw new AbyssineException("Acquire overflow");
                if(newState > maximumLock) throw new AbyssineException("Total lock after acquires is larger than maximum lock");

                setState(newState);
                return true;
            }

            return false;
        }

        public boolean tryRelease(final int releases) {
            if(getExclusiveOwnerThread() != Thread.currentThread()) throw new IllegalMonitorStateException();
            if(releases <= 0) throw new IllegalArgumentException("Release zero or negative lock");
            if(releases > getState()) throw new AbyssineException("Release more than current lock");

            var lockLeft = getState() - releases;

            if(lockLeft > getState()) throw new AbyssineException("Releases Overflow");

            if(lockLeft == 0) {
                setExclusiveOwnerThread(null);
                setState(0);
                return true;
            }

            setState(lockLeft);

            return false;
        }

        public Condition newCondition() {
            return new ConditionObject();
        }

        public boolean isHeldExclusively() {
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        public int getHoldCount() {
            return isHeldExclusively() ? getState() : 0;
        }

        public void setMaximumLock(int numLock) {
            if(numLock > getState()) throw new AbyssineException("Cannot change locks capacity at runtime when new locks capacity is less then current hold locks");

            this.maximumLock = numLock;
        }
    }
}