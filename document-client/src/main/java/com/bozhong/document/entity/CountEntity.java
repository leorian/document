package com.bozhong.document.entity;

import com.alibaba.fastjson.JSON;

/**
 * Created by xiezg@317hu.com on 2017/4/28 0028.
 */
public class CountEntity {
    private long allCount;
    private long waitingCount;
    private long executingCount;
    private long successCount;
    private long failureCount;

    public long getAllCount() {
        return allCount;
    }

    public void setAllCount(long allCount) {
        this.allCount = allCount;
    }

    public long getWaitingCount() {
        return waitingCount;
    }

    public void setWaitingCount(long waitingCount) {
        this.waitingCount = waitingCount;
    }

    public long getExecutingCount() {
        return executingCount;
    }

    public void setExecutingCount(long executingCount) {
        this.executingCount = executingCount;
    }

    public long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(long successCount) {
        this.successCount = successCount;
    }

    public long getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(long failureCount) {
        this.failureCount = failureCount;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
