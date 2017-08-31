package com.bozhong.document.dto;

import com.alibaba.fastjson.JSON;

/**
 * Created by xiezg@317hu.com on 2017/5/15 0015.
 */
public class TaskContentAsyncResDto {


    private String taskId;//任务ID
    private String appId;//应用ID
    private String taskContent;//源文件在线链接地址
    private String callBackUrl;//回调地址
    private String taskStatus;//任务状态

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
