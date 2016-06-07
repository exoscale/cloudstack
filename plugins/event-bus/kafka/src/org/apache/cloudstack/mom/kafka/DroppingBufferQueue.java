/**
 * A Dropping Buffer Queue for holding a cache of messages
 * waiting for a connection to come back up. An initial
 * array is allocated at startup and will be used to store
 * payloads waiting to be sent.
 *
 * This should be sized according to memory constraints.
 * The iterator should only be used for debugging purposes
 * since it does not guarantee providing a consistent view.
 *
 * To drain the queue please use the following idiom:
 *
 * while ((elem = queue.poll()) != null) {
 *   handle(elem)
 * }
 */
package org.apache.cloudstack.mom.kafka;

import java.util.AbstractQueue;
import java.util.Iterator;

public class DroppingBufferQueue<E> extends AbstractQueue<E> {

    private static final int DEFAULT_CAPACITY = 100;
    private final int capacity;
    private final E[] buffer;
    private volatile int head = 0;
    private volatile int tail = 0;
    private volatile int len = 0;

    public DroppingBufferQueue() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Our constructor. This contains unchecked code to
     * be able to create an array of `E` (our templated type).
     */
    @SuppressWarnings("unchecked")
    public DroppingBufferQueue(int capacity) {
        buffer = (E[]) new Object[capacity];
        this.capacity = capacity;
    }

    /**
     * This version of offer always succeeds by evicting the
     * oldest member of the collection when at full capacity.
     */
    @Override
    public synchronized boolean offer(E e) {

        /*
         * Prevent null pointers from being inserted.
         */
        if (e == null) {
            throw(new NullPointerException("null elements cannot be inserted."));
        }
        buffer[tail] = e;

        if (len == capacity) {
            head = head + 1;
        } else {
            len = len + 1;
        }

        tail = tail + 1;

        if (tail == capacity) {
            tail = 0;
        }

        if (head == capacity) {
            head = 0;
        }

        return true;
    }


    @Override
    public E peek() {
        if (len == 0) {
            return null;
        }
        return buffer[head];
    }

    @Override
    public synchronized E poll() {
        E elem;

        if (len == 0) {
            return null;
        }

        elem = buffer[head];
        buffer[head] = null;
        head = head + 1;
        if (head == capacity) {
            head = 0;
        }
        len = len - 1;
        return elem;
    }

    @Override
    public int size() {
        return len;
    }

    @Override
    public Iterator<E> iterator() {
        return new DroppingBufferQueueIterator<E>(this);
    }

    /*
     * If available, yield the corresponding position in the
     * backing array of the provided index.
     * This is used by iterators.
     */
    public int getIndexPosition(int index) {

        int pos;

        if (len == 0 || index < 0 || index >= len) {
            return -1;
        }

        pos = head + index;
        if (pos < capacity) {
            return pos;
        }
        return pos - capacity;
    }

    /*
     * Retrieve the provided position in the backing array.
     * This is used by iterators.
     */
    public E getPos(int pos) {

        return buffer[pos];
    }

    /**
     * An iterator for debugging purposes.
     * A consistent view of the collection cannot be maintained through this
     * iterator.
     */
    private class DroppingBufferQueueIterator<E> implements Iterator<E> {
        private final DroppingBufferQueue<E> q;
        private volatile int pos = 0;

        public DroppingBufferQueueIterator(DroppingBufferQueue<E> q) {
            this.q = q;
        }

        @Override
        public boolean hasNext() {
            return (q.getIndexPosition(pos) != -1);
        }

        @Override
        public E next() {
            E elem;

            int qpos = q.getIndexPosition(pos);
            if (qpos == -1) {
                return null;
            }

            elem = q.getPos(qpos);
            pos = pos + 1;
            return elem;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
