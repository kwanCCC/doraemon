package doraemon.algorithms.ds;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicReference;

public class RingBuffer<E> {
    private Object[] RING;
    @Getter
    private int      size;
    // to & Operation to next cursor
    private int      mask;
    // max wait lock
    private int      maxWait;

    private AtomicReference<Integer> WriteCur;
    private AtomicReference<Integer> ReadCur;

    public RingBuffer(int maxsize, int maxwait) {
        RING = new Object[maxsize];
        size = maxsize;
        mask = maxsize - 1;
        WriteCur = new AtomicReference<Integer>(0);
        ReadCur = new AtomicReference<Integer>(0);
        maxWait = maxsize;
    }

    /**
     * @return
     */
    public boolean isFull() {
        return ring_mod(WriteCur.get()) == ReadCur.get();
    }

    /**
     * @return
     */
    public boolean isEmpty() {
        return WriteCur.get().equals(ReadCur.get());
    }

    /**
     * @return RING SIZE
     */
    public int curSize() {
        if (WriteCur.get() > ReadCur.get()) {
            return WriteCur.get() - ReadCur.get();
        } else if (WriteCur.get() < ReadCur.get()) {
            return WriteCur.get() + size - ReadCur.get();
        } else {
            return 0;
        }
    }

    /**
     * @param e
     * @param strategy {@link Strategy}
     */
    public void add(E e, Strategy strategy) {
        for (; ; ) {
            Integer current_W = WriteCur.get();
            // judge status of RING
            if (isFull()) {
                if (strategy.equals(Strategy.BLOCKING)) {
                    block();
                } else if (strategy.equals(Strategy.YIELD)) {
                    // log or do something else?
                    return;
                }
            } else {
                // mv write cursor to the next
                int ring_mod = ring_mod(current_W);
                if (WriteCur.compareAndSet(current_W, ring_mod)) {
                    RING[current_W] = e;
                    return;
                }
            }
        }
    }

    /**
     * @param strategy {@link Strategy}
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public E get(Strategy strategy) {
        for (; ; ) {
            Integer current_C = ReadCur.get();
            if (isEmpty()) {
                if (strategy.equals(Strategy.YIELD)) {
                    return null;
                } else if (strategy.equals(Strategy.BLOCKING)) {
                    block();
                    return get(Strategy.YIELD);
                }
            } else {
                Object o = RING[current_C];
                int ring_mod = ring_mod(current_C);
                if (ReadCur.compareAndSet(current_C, ring_mod)) {
                    while (o == null) {
                        Thread.yield();
                        o = RING[current_C];
                    }
                    RING[current_C] = null;
                    return (E) o;
                }
            }
        }
    }

    public void block() {
        try {
            Thread.sleep(maxWait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int ring_mod(int i) {
        return (i + 1) & mask;
    }

}
