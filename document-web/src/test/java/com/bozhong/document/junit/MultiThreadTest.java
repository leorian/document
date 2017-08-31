package com.bozhong.document.junit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xiezhonggui on 2017/5/14.
 */
public class MultiThreadTest {
    public static void main(String args[]) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
    }
}
