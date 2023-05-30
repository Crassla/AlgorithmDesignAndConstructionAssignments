package com.comp611.assignment;

public interface TaskObserver<F> {
    void update(F progress);
}
