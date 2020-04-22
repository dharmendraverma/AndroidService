package com.example.application;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServiceWorker {

    private String mServiceTag;
    private final Queue<Task> mWorkerQueue;
    private int mSize = Integer.MAX_VALUE;
    Handler mMainThreadHandler;


    ServiceWorker(String service) {
        mServiceTag = service;
        mWorkerQueue = new LinkedList<>();
        mMainThreadHandler = new Handler(Looper.getMainLooper());
        Thread startBackGroundTask = new Thread(new BackGroundTaskRun(mMainThreadHandler, mWorkerQueue, mSize));
        startBackGroundTask.start();
    }

    ServiceWorker(String service, int size) {
        mServiceTag = service;
        this.mSize = size;
        mWorkerQueue = new LinkedBlockingQueue<>(mSize);
        mMainThreadHandler = new Handler(Looper.getMainLooper());
        Thread startBackGroundTask = new Thread(new BackGroundTaskRun(mMainThreadHandler, mWorkerQueue, mSize));
        startBackGroundTask.start();
    }


    public void addTask(Task task) throws InterruptedException {
        if (task == null) throw new NullPointerException();
        while (mWorkerQueue.size() == mSize) {
            synchronized (mWorkerQueue) {
                mWorkerQueue.wait();
            }
        }
        synchronized (mWorkerQueue) {
            Log.i(mServiceTag, "Task Added In the Queue");
            mWorkerQueue.add(task);
            mWorkerQueue.notifyAll();
        }
    }

    class BackGroundTaskRun implements Runnable {

        private final Queue<Task> mSharedQueue;
        Object mFinalResult;
        Handler mMainThreadHandler;
        Task mTaskCallBack;

        public BackGroundTaskRun(Handler handler, Queue sharedQueue, int size) {
            this.mSharedQueue = sharedQueue;
            this.mMainThreadHandler = handler;
        }

        @Override
        public void run() {
            while (true) {
                while (mSharedQueue.isEmpty()) {
                    synchronized (mSharedQueue) {
                        Log.i(mServiceTag, "Queue is empty " + Thread.currentThread().getName()
                                + " is waiting , size: " + mSharedQueue.size());
                        try {
                            mSharedQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                synchronized (mSharedQueue) {
                    mSharedQueue.notifyAll();
                    mTaskCallBack = mSharedQueue.poll();
                    if (mTaskCallBack != null) {
                        mFinalResult = mTaskCallBack.onExecuteTask();
                    }
                    mMainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(mServiceTag, "Call To Main Thread");
                            mTaskCallBack.onTaskComplete(mFinalResult);
                        }
                    });
                }
            }
        }
    }
}
