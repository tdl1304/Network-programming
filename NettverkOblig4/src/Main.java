public class Main {
    public static void main(String[] args) throws InterruptedException {
        Workers worker_threads = new Workers(4);
        Workers event_loop = new Workers(1);
        worker_threads.start();
        event_loop.start();

        worker_threads.post(()->{
            System.out.println("Hello from task A: "+Thread.currentThread().getName());
        });
        worker_threads.post(()->{
            System.out.println("Hello from task B: "+Thread.currentThread().getName());
        });

        event_loop.post(()->{
            System.out.println("Hello from task C: "+Thread.currentThread().getName());
        });

        event_loop.post(()->{
            System.out.println("Hello from task D: "+Thread.currentThread().getName());
        });

        worker_threads.post_timeout(()->{
            System.out.println("Worker_thread timeout task just ran");
        }, 6000);

        event_loop.post_timeout(()->{
            System.out.println("Eventloop timeout task just ran");
        }, 2000);

        worker_threads.join();
        event_loop.join();

        worker_threads.stop();
        event_loop.stop();
    }
}
