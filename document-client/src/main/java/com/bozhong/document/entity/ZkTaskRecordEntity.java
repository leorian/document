package com.bozhong.document.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * Created by xiezhonggui on 2017/5/12.
 */
public class ZkTaskRecordEntity implements Serializable {

    /**
     * 序号
     */
    private String number;
    /**
     * IP
     */
    private String ip;

    /**
     * 正在执行任务个数
     */
    private String count;

    /**
     * 正在执行任务列表
     */
    private String executingTaskIds;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getExecutingTaskIds() {
        return executingTaskIds;
    }

    public void setExecutingTaskIds(String executingTaskIds) {
        this.executingTaskIds = executingTaskIds;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
