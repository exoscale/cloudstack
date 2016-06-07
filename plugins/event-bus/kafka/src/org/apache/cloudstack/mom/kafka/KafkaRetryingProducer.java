package org.apache.cloudstack.mom.kafka;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.utils.KafkaThread;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.errors.ApiException;
import org.apache.log4j.Logger;


public class KafkaRetryingProducer<K,V> extends Object implements Producer<K,V> {

    private final DroppingBufferQueue<BufferedRecord<K,V>> buffer;
    private final KafkaProducer<K,V> producer;
    private static final String DEFAULT_CAPACITY = "100";
    private static final String DEFAULT_DRAIN_INTERVAL = "10";
    private final AtomicBoolean canSend = new AtomicBoolean(true);
    private final BufferDrainer drainer;
    private static final Logger s_logger = Logger.getLogger(KafkaRetryingProducer.class);

    public KafkaRetryingProducer(Properties props) {

        final int buffer_capacity = Integer.valueOf(props.getProperty("buffer.capacity", DEFAULT_CAPACITY)).intValue();
        final int drain_interval = Integer.valueOf(props.getProperty("buffer.drain.interval", DEFAULT_DRAIN_INTERVAL)).intValue();

        props.remove("buffer.capacity");
        props.remove("buffer.drain.interval");
        buffer = new DroppingBufferQueue<BufferedRecord<K,V>>(buffer_capacity);
        producer = new KafkaProducer<K,V>(props);
        drainer = new BufferDrainer(this, canSend, producer, buffer, drain_interval);
    }

    /**
     * (Re-)Start the draining process in a separate thread.
     */
    public void startRetryThread() {
        s_logger.info("starting drainer thread.");
        DaemonThread t = new DaemonThread("buffer-drainer", drainer);
        t.start();
    }

    /**
     *
     * Produce a record and provide a way to handle the edge case in KafkaProducer where
     * timeouts may occur while fetching metadata.
     *
     * When this happens, call the `onTimeout` method of the `CompletionCallback` argument.
     * the return value from `onTimeout` will be yielded back to the caller.
     *
     * When producing succeeds, call the `onSuccess` method of the `CompleationCallback` argument
     * with the result future as its argument.
     */
    private Future<RecordMetadata> sendSafely(BufferedRecord<K,V> br, CompletionCallback cb) {

        Future<RecordMetadata> f = producer.send(br.getRecord(), br);

        try {
            /*
             * This will throw an ExecutionException containing our Timeout if
             * a timeout was reached.
             */
            f.get(1, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ApiException) {
                return cb.onTimeout();
            }
        } catch (Exception e) {
            /*
             * A generic exception occured, or get took longer than expected.
             * Let's do nothing and just yield the initial future back.
             */
        }
        cb.onSuccess(f);
        return f;
    }

    @Override
    public Future<RecordMetadata> send(ProducerRecord<K,V> record) {
        return send(record, null);
    }

    @Override
    public Future<RecordMetadata> send(ProducerRecord<K,V> record, Callback cb) {
        final BufferedRecord<K,V> br = new BufferedRecord<K,V>(record, cb);
        Future<RecordMetadata> f;

        if (canSend.get()) {
            f = sendSafely(br,
                           new CompletionCallback() {
                               public Future<RecordMetadata> onTimeout(){
                                   s_logger.warn("timeout reached while sending record");
                                   canSend.set(false);
                                   buffer.offer(br);
                                   startRetryThread();
                                   return br.getFuture();
                               }
                               public void onSuccess(Future<RecordMetadata> innerf) {
                               }
                           });
        } else {
            buffer.offer(br);
            f = br.getFuture();
        }
        return f;
    }

    /**
     * Allow the client process to know whether we are sending or not.
     */
    public boolean isSending() {
        return canSend.get();
    }

    /**
     * Peek at the number of records we have buffered.
     */
    public int bufferedRecordCount() {
        return buffer.size();
    }

    @Override
    public void close() {
        producer.close();
    }

    @Override
    public void close(long timeout, TimeUnit unit) {
        producer.close(timeout, unit);
    }

    @Override
    public void flush() {
        producer.flush();
    }

    @Override
    public java.util.Map<MetricName,? extends Metric> metrics () {
        return producer.metrics();
    }

    @Override
    public java.util.List<PartitionInfo> partitionsFor(String topic) {
        return producer.partitionsFor(topic);
    }

    /**
     * This is a poor man's promise. We create a coutdown latch of 1 and let
     * the future wait on it. When a future is delivered to us, we wait on
     * the future instead. This is thus essentially a promise embedded in
     * a future chain.
     *
     * We do this because a producer should return a future when `send()` is
     * called on it. If metadata hasn't been fetched yet, or is expired,
     * send() will block for a configurable amount of time then fail. In that
     * case, we want to this place-holder future to wait for a correct send()
     * operation to provide us with a real future through ``deliverFuture`` and
     * then forward all operations to the delived future.
     */
    private class CountDownFuture<E> implements Future<E> {

        private final CountDownLatch cd = new CountDownLatch(1);
        private boolean cancelled = true;
        private Future<E> ftr = null;

        @Override
        public boolean cancel(boolean mayInterrupt) {
            if (ftr == null) {
                cancelled = true;
                cd.countDown();
                return true;
            }
            return ftr.cancel(mayInterrupt);
        }

        @Override
        public E get() throws InterruptedException, ExecutionException {
            if (ftr == null) {
                cd.await();
            }

            if (ftr != null) {
                return ftr.get();
            }
            return null;
        }

        @Override
        public E get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {

            long start_stamp = System.currentTimeMillis();
            long interval = unit.toMillis(timeout);

            if (ftr == null) {
                cd.await(interval, TimeUnit.MILLISECONDS);
            }

            long remaining = interval - (System.currentTimeMillis() - start_stamp);

            if (ftr != null) {
                return ftr.get(remaining, TimeUnit.MILLISECONDS);
            }
            return null;
        }

        @Override
        public boolean isCancelled() {
            if (cancelled) {
                return true;
            }
            if (ftr != null) {
                return ftr.isCancelled();
            }
            return false;
        }

        @Override
        public boolean isDone() {
            if (ftr == null) {
                return false;
            }
            return ftr.isDone();
        }

        /**
         * Deliver a value to our promise. The value is a future itself.
         * Once we have it operations will be forwarded to it.
         */
        public void deliverFuture(Future<E> ftr) {
            if (!cancelled) {
                this.ftr = ftr;
            }
            cd.countDown();
        }
    }

    /**
     * A class to hold on to a record we want to send when
     * the producer is connected.
     */
    private class BufferedRecord<K,V> implements Callback {
        private final ProducerRecord<K,V> record;
        private final Callback cb;
        private final CountDownFuture<RecordMetadata> ftr = new CountDownFuture<RecordMetadata>();

        public BufferedRecord(ProducerRecord<K,V> record, Callback cb) {
            this.record = record;
            this.cb = cb;
        }

        public ProducerRecord<K,V> getRecord() {
            return record;
        }

        public void onCompletion(RecordMetadata metadata, Exception e) {
            if (cb == null) {
                return;
            }

            if ((e == null) || !(e instanceof ApiException)) {
                cb.onCompletion(metadata, e);
            }
        }

        public Future<RecordMetadata> getFuture() {
            return ftr;
        }

        public void setFuture(Future<RecordMetadata> f) {
            ftr.deliverFuture(f);
        }
    }

    /**
     * A Task to drain our buffer once our producer is connected again.
     * This is started every time the producer becomes unavailable to
     * try every configured interval.
     *
     * When the producer becomes available again, the buffer is drained
     * and the task stops.
     */
    private class BufferDrainer implements Runnable {

        private final int interval;
        private final KafkaProducer<K,V> producer;
        private final KafkaRetryingProducer<K,V> parent;
        private final DroppingBufferQueue<BufferedRecord<K,V>> buffer;
        private final AtomicBoolean canSend;

        public BufferDrainer(KafkaRetryingProducer<K,V> parent, AtomicBoolean canSend,
                             KafkaProducer<K,V> producer,
                             DroppingBufferQueue<BufferedRecord<K,V>> buffer, int interval) {
            this.canSend = canSend;
            this.producer = producer;
            this.buffer = buffer;
            this.interval = interval;
            this.parent = parent;
        }

        public boolean tryDrain() {
            BufferedRecord<K,V> br;
            final AtomicBoolean barrier = new AtomicBoolean(true);

            while (((br = buffer.peek()) != null) && barrier.get()) {
                /*
                 * Help the JVM close over our scope.
                 */
                final BufferedRecord<K,V> innerbr = br;

                parent.sendSafely(br,
                                  new CompletionCallback() {
                                      public Future<RecordMetadata> onTimeout() {
                                          s_logger.warn("timeout notice in drainer. stopping poll loop.");
                                          barrier.set(false);
                                          return null;
                                      }
                                      public void onSuccess(Future<RecordMetadata> innerf) {
                                          buffer.poll();
                                          innerbr.setFuture(innerf);

                                      }
                                  });
            }
            return barrier.get();
        }

        @Override
        public void run()  {
            s_logger.info("no connectivity to producer, drainer thread started.");
            while (canSend.get() == false) {
                s_logger.info("ticking on retry thread");

                boolean drainSucceeded = tryDrain();
                if (drainSucceeded) {
                    canSend.set(true);
                    tryDrain();
                    s_logger.info("connectivity to producer restored, stopping drainer thread.");
                    return;
                }
                try {
                    Thread.sleep(interval * 1000);
                } catch (InterruptedException e) {
                    s_logger.info("drainer interrupted during interval sleep, retrying.");
                }
            }
        }
    }

    /**
     * Simple helper to start up a daemon thread.
     */
    private class DaemonThread extends Thread {

        public DaemonThread(final String name, Runnable runnable) {
            super(runnable, name);
            setDaemon(true);
            setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    public void uncaughtException(Thread t, Throwable e) {
                        s_logger.error("Uncaught exception in " + name + ": ", e);
                    }
                });
        }

    }


    private interface CompletionCallback {
        Future<RecordMetadata> onTimeout();
        void onSuccess(Future<RecordMetadata> innerf);
    }
}
