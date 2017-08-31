package com.bozhong.document.task;

import com.bozhong.document.util.QueueUtil;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class DocumentPoolWorkerThread extends Thread {
    public void run() {
        Runnable r;
        while (true) {
            synchronized (QueueUtil.queue) {
                while (QueueUtil.queue.isEmpty()) {
                    try {
                        QueueUtil.queue.wait();
                    } catch (InterruptedException ignored) {
                    }
                }
                r = (Runnable) QueueUtil.queue.removeFirst();
            }
            try {
                r.run();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }
}
