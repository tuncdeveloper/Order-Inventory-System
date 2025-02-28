package service;

import model.ConfirmProcess;

import java.time.Instant;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ConcurrentConfirmManager {

    private static ConcurrentConfirmManager concurrentConfirmManager;

    private PriorityBlockingQueue<Runnable> queue;

    private final ThreadPoolExecutor executor;

    private final OrderService orderService;

    public ConcurrentConfirmManager() {
        queue = new PriorityBlockingQueue<>();
        executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, queue);
        orderService = OrderService.getInstance();
    }

    public static ConcurrentConfirmManager getInstance() {
        if (concurrentConfirmManager == null) {
            synchronized (ConcurrentConfirmManager.class) {
                if (concurrentConfirmManager == null) {
                    concurrentConfirmManager = new ConcurrentConfirmManager();
                }
            }
        }
        return concurrentConfirmManager;
    }

    // admin confirm all butonuna bastıgında oncelıkle onay bekleyen butun orderlar ıcın confirmprocess nesnesi olusturulacak daha sonra bu metot calıstırılacak
    // her bir confirm process active olmalı
    public synchronized void confirmAll() {
        PriorityBlockingQueue<Runnable> newQueue = new PriorityBlockingQueue<>();
        long confirmingTime = Instant.now().toEpochMilli();
        while (!queue.isEmpty()) {
            ConfirmProcess thread = (ConfirmProcess) queue.poll();
            thread.setConfirmingTime(confirmingTime);

            if (thread.getCustomer().getType().equals("Premium")) {
                Double priority = (double) (15+ (((thread.getConfirmingTime() - thread.getTimestamp()) / 1000F) * 0.5F));
                thread.setPriority(priority);
                //thread.getCustomer().getPriorityScore();
            }else {
                Double priority = (double) (10 + (((thread.getConfirmingTime() - thread.getTimestamp()) / 1000F) * 0.5F));
                thread.setPriority(priority);
            }

            newQueue.add(thread);
        }
        queue = newQueue;
        runThreads();
    }

    private void runThreads() {
        Thread queueProcessor = new Thread(() -> {
            while (!queue.isEmpty()) {
                try {
                    ConfirmProcess task = (ConfirmProcess) queue.poll();
                    if (task != null && task.isIsActive()) {
                        executor.execute(task);
                    }
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        queueProcessor.setDaemon(true);
        queueProcessor.start();
    }

    public PriorityBlockingQueue<Runnable> getQueue() {
        return queue;
    }

}
