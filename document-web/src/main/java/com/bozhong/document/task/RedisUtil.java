package com.bozhong.document.task;


import com.bozhong.myredis.MyRedisClusterForHessian;
import org.springframework.beans.BeansException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by xiezhonggui on 2017/4/30.
 */
public class RedisUtil implements MyRedisClusterForHessianAware {

    @Override
    public void setMyRedisClusterForHessian(MyRedisClusterForHessian myRedisClusterForHessian) throws BeansException {
        RedisUtil.myRedisClusterForHessian = myRedisClusterForHessian;
    }

    public static MyRedisClusterForHessian myRedisClusterForHessian;

    /**
     * 存储REDIS队列 顺序存储
     *
     * @param key   字节类型
     * @param value 字节类型
     */
    public static void lpush(byte[] key, byte[] value) {
        try {
            myRedisClusterForHessian.getJedisCluster().lpush(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    /**
     * 存储REDIS队列 反序存储
     *
     * @param key   字节类型
     * @param value 字节类型
     */
    public static void rpush(byte[] key, byte[] value) {
        try {
            myRedisClusterForHessian.getJedisCluster().rpush(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * 移除列表的最后一个元素，并将该元素添加到另一个列表并返回,就可以实现任务队列
     *
     * @param srckey 原队列的key
     * @param dstkey 目标队列的key
     */
    public static byte[] rpoplpush(byte[] srckey, byte[] dstkey) {
        byte[] value = null;
        try {
            value = myRedisClusterForHessian.getJedisCluster().rpoplpush(srckey, dstkey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return value;
    }

    /**
     * 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
     *
     * @param srckey
     * @param dstkey
     * @param timout
     * @return
     */
    public static byte[] brpoplpush(byte[] srckey, byte[] dstkey, int timout) {
        byte[] value = null;
        try {
            value = myRedisClusterForHessian.getJedisCluster().brpoplpush(srckey, dstkey, timout);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return value;
    }

    /**
     * 设置实现任务队列的键和过期时间
     *
     * @param key
     * @param timeout
     */
    public static List<byte[]> brpop(byte[] key, int timeout) {
        List<byte[]> result = null;
        try {
            result = myRedisClusterForHessian.getJedisCluster().brpop(timeout, key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return result;
    }

    /**
     * 移除队列中的最后一个元素并显示最后一个元素
     *
     * @param key
     * @return
     */
    public static byte[] rpop(byte[] key) {
        byte[] bytes = null;
        try {
            bytes = myRedisClusterForHessian.getJedisCluster().rpop(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return bytes;
    }

    /**
     * set集合添加元素
     *
     * @param key
     * @param value
     * @return
     */
    public static long sadd(byte[] key, byte[] value) {
        long count = 0;
        try {
            count = myRedisClusterForHessian.getJedisCluster().sadd(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return count;
    }

    /**
     * 获取集合成员
     *
     * @param key
     * @return
     */
    public static Set<byte[]> smembers(byte[] key) {
        Set<byte[]> set = new HashSet<>();
        try {
            set = myRedisClusterForHessian.getJedisCluster().smembers(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        return set;
    }

    /**
     * set集合删除元素
     *
     * @param key
     * @param value
     * @return
     */
    public static long srem(byte[] key, byte[] value) {
        long count = 0;
        try {
            count = myRedisClusterForHessian.getJedisCluster().srem(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return count;
    }

}
