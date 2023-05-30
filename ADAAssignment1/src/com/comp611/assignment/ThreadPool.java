package com.comp611.assignment;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

public class ThreadPool {
    private int size;
    private boolean destroyPool;
    private final List<Worker> workerPool;
    private final BlockingQueue<Runnable> taskQueue;

    public ThreadPool(int initialSize) {
        this.workerPool = new LinkedList<>();
        this.taskQueue = new ArrayBlockingQueue<>(initialSize);
        resize(initialSize);
        this.size = initialSize;
    }

    public int getSize() {
        return size;
    }

    public int getAvailable() {
        return (int) workerPool.stream().map(worker -> worker.getState() == Thread.State.WAITING).count();
    }

    private Worker getAvailableWorker() {
        return workerPool.stream().filter(worker -> worker.getState() == Thread.State.WAITING).findFirst().orElse(null);
    }

    private List<Worker> getAvailableWorkers() {
        return workerPool.stream().filter(worker -> worker.getState() == Thread.State.WAITING).collect(Collectors.toList());
    }

    public synchronized void resize(int newSize) throws IllegalArgumentException{
        if (newSize < size && getAvailable() < newSize)
            throw new IllegalArgumentException("Not enough free threads available to resize");

        if (newSize < size) {
            int difference = size - newSize;
            for (int i = 0; i < difference; i++) {
                Worker worker = this.getAvailableWorker();
                worker.interrupt();
                workerPool.remove(worker);
            }
        } else {
            int numNewWorkers = newSize - size;
            for (int i = 0; i < numNewWorkers; i++) {
                Worker worker = new Worker();
                workerPool.add(worker);
                worker.start();
            }
        }

        this.size = newSize;
    }

    public void destroyPool() {
        destroyPool = true;

        // TODO: Fix this up, there should be a better way!
        // TODO: This will lock the main thread if threads are waiting ;(
        while (!taskQueue.isEmpty()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (Worker worker : workerPool) {
            worker.interrupt();
        }

        workerPool.clear();
    }

    public boolean perform(Runnable task) {
        if (destroyPool) return false;

        taskQueue.add(task);
        synchronized (taskQueue) {
            taskQueue.notify();
        }
        return true;
    }

    @SuppressWarnings({"InfiniteLoopStatement", "ConstantConditions"})
    private class Worker extends Thread {
        @Override
        public void run() {
            Runnable task;

            while (true) {
                synchronized (taskQueue) {
                    if (taskQueue.isEmpty()) {
                        try {
                            taskQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    task = taskQueue.poll();
                }

                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
