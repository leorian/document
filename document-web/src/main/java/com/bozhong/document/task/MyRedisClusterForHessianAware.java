package com.bozhong.document.task;

import com.bozhong.myredis.MyRedisClusterForHessian;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;

/**
 * Created by xiezhonggui on 2017/4/30.
 */
public interface MyRedisClusterForHessianAware extends Aware {

    void setMyRedisClusterForHessian(MyRedisClusterForHessian myRedisClusterForHessian) throws BeansException;
}
