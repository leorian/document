package com.bozhong.document.test;

import com.bozhong.document.util.ObjectUtil;
import redis.clients.jedis.Jedis;

import java.io.IOException;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class TestRedisQueue {
    public static byte[] redisKey = "keyHHH".getBytes();

    static {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void init() throws IOException {
        for (int i = 0; i < 1000000; i++) {
            Message message = new Message(i, "这是第" + i + "个内容");
            JedisUtil.lpush(redisKey, ObjectUtil.object2Bytes(message));
        }

    }

    public static void main(String[] args) {

        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        pop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }


    }

    private static void pop() throws Exception {
        byte[] bytes = JedisUtil.rpop(redisKey);
        Message msg = (Message) ObjectUtil.bytes2Object(bytes);
        if (msg != null) {
            System.out.println(Thread.currentThread().getName()+ "-----" + msg.getId() + "----" + msg.getContent());
        }
    }
}