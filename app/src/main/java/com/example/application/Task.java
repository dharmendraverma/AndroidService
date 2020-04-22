package com.example.application;

public interface Task<T> {
    T onExecuteTask();
    void onTaskComplete(T task);
}
