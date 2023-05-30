package com.comp611.assignment;

public class UniqueIdentifier {

    private static volatile UniqueIdentifier instance;
    private int currentIdentifier;

    private UniqueIdentifier() {}

    public synchronized int get() {
        return ++currentIdentifier;
    }

    public static UniqueIdentifier getInstance() {
        if (instance == null) {
            synchronized (UniqueIdentifier.class) {
                if (instance == null) instance = new UniqueIdentifier();
            }
        }

        return instance;
    }
}
