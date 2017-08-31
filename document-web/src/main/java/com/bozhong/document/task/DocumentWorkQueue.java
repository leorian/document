package com.bozhong.document.task;

import com.bozhong.document.util.QueueUtil;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class DocumentWorkQueue {
    private final int nThreads;
    private final DocumentPoolWorkerThread[] threads;

    public DocumentWorkQueue(int nThreads) {
        this.nThreads = nThreads;
        threads = new DocumentPoolWorkerThread[nThreads];

        for (int i = 0; i < nThreads; i++) {
            threads[i] = new DocumentPoolWorkerThread();
            threads[i].start();
        }
    }

    public void execute(Runnable r) {
        synchronized (QueueUtil.queue) {
            QueueUtil.queue.addLast(r);
            QueueUtil.queue.notify();
        }
    }
}

