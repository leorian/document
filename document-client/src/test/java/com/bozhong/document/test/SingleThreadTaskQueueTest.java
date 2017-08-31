package com.bozhong.document.test;

import java.util.LinkedList;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 * 单线程任务队列
 */
public class SingleThreadTaskQueueTest {
    private static Thread thread;
    private static LinkedList<Runnable> list = new LinkedList<>();

    static int test = 0;

    public static void main(String[] args) {
        final long time = System.currentTimeMillis();
        for (int i = 0; i < 20; i++) {
            testEvent(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out
                            .println("第"
                                    + (++test)
                                    + ("个任务  耗时:" + (System
                                    .currentTimeMillis() - time)));
                }

            });
        }
    }

    public static void testEvent(Runnable r) {
        synchronized (list) {
            list.add(r);
        }
        if (thread == null) {
            thread = new Thread(run);
            thread.start();
        }
    }

    static Runnable run = new Runnable() {
        @Override
        public void run() {
            synchronized (list) {

                while (!list.isEmpty()) {
                    list.poll().run();
                }
                thread = null;
            }
        }
    };
}
