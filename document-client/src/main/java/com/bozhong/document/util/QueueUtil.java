package com.bozhong.document.util;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class QueueUtil {

    public static final LinkedList queue = new LinkedList();

    public static byte[] dQueue;

    public static byte[] getDQueueKey() {

        if (dQueue == null) {
            try {
                dQueue = ObjectUtil.object2Bytes("dQueue");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return dQueue;
    }


}
