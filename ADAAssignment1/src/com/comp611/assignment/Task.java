package com.comp611.assignment;

import java.util.ArrayList;
import java.util.List;

public abstract class Task<E,F> implements Runnable {

    
    private E param;
    private final int id;
    private final List<TaskObserver<F>> taskObservers;

    public Task(E param) {
        this.param = param;
        this.id = UniqueIdentifier.getInstance().get();
        this.taskObservers = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void addListener(TaskObserver<F> o) {
        this.taskObservers.add(o);
    }

    public void removeListener(TaskObserver<F> o) {
        this.taskObservers.remove(o);
    }

    protected void notifyAll(F progress) {
        for (TaskObserver<F> observer : taskObservers) {
            observer.update(progress);
        }
    }
}
