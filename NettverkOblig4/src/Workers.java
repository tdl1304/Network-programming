import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Workers {

    private final Lock lock = new ReentrantLock();
    private final Condition cv = lock.newCondition();
    private final ExecutorService executor;
    private final List<Runnable> tasks = new ArrayList<>();
    private boolean stopsignal = false;


    public Workers(int poolsize) {
        executor = Executors.newFixedThreadPool(poolsize);
    }

    public void post(Runnable task) {
        lock.lock();
        tasks.add(task);
        cv.signal();
        lock.unlock();

    }

    public void start() {
        Thread runner = new Thread(()->{
            while (true) {
                lock.lock();
                try {
                    cv.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (!tasks.isEmpty()) {
                    executor.submit(tasks.remove(0));
                }
                if(stopsignal) {
                    break;
                }
                lock.unlock();
            }
        });
        runner.start();
    }

    /**
     * Set timeout for task
     *
     * @param task  Runnable
     * @param delay millis to wait
     */
    public void post_timeout(Runnable task, long delay) {
        lock.lock();
        tasks.add(() -> {
            try {
                Thread.sleep(delay);
                task.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        cv.signal();
        lock.unlock();

    }

    public void stop() {
        lock.lock();
        executor.shutdownNow();
        stopsignal = true;
        cv.signal();
        System.out.println("shutdown finished");
        lock.unlock();
    }

    public void join() {
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }
}
