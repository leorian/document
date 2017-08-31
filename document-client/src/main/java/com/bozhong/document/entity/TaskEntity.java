package com.bozhong.document.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * Created by xiezg@317hu.com on 2017/4/26 0026.
 */
public class TaskEntity implements Serializable {

    /**
     * 任务ID（主键，唯一性）
     */
    private String taskId;

    /**
     * 任务内容（原始文件链接地址）
     */
    private String taskContent;

    /**
     * 任务内容（原始文件链接地址）大小
     */
    private String taskContentLength;

    /**
     * 任务内容（原始文件链接地址）类型
     */
    private String taskContentExt;

    /**
     * 任务结果（转换后文件链接地址）
     */
    private String taskResult;

    /**
     * 任务结果（转换后文件链接地址）大小
     */
    private String taskResultLength;

    /**
     * 任务结果（转换后文件链接地址）类型
     */
    private String taskResultExt;

    /**
     * 任务状态（Waiting未执行，正在执行Executing，执行失败Failure，执行成功Success）
     */
    private String taskStatus;

    /**
     * 任务创建时间
     */
    private String createTimeStamp;

    /**
     * 执行开始时间
     */
    private String executeTimeStamp;

    /**
     * 执行结束时间
     */
    private String finishTimeStamp;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 具体错误信息
     */
    private String errorMessage;

    /**
     * 转换时长
     */
    private String convertTime;

    /**
     * 是否重新执行
     */
    private boolean isReAction = false;

    /**
     * 异步回调地址
     */
    private String callBackUrl;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 应用名称
     */
    private String appName;


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public String getTaskResult() {
        return taskResult;
    }

    public void setTaskResult(String taskResult) {
        this.taskResult = taskResult;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getCreateTimeStamp() {
        return createTimeStamp;
    }

    public void setCreateTimeStamp(String createTimeStamp) {
        this.createTimeStamp = createTimeStamp;
    }

    public String getExecuteTimeStamp() {
        return executeTimeStamp;
    }

    public void setExecuteTimeStamp(String executeTimeStamp) {
        this.executeTimeStamp = executeTimeStamp;
    }

    public String getFinishTimeStamp() {
        return finishTimeStamp;
    }

    public void setFinishTimeStamp(String finishTimeStamp) {
        this.finishTimeStamp = finishTimeStamp;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getTaskContentLength() {
        return taskContentLength;
    }

    public void setTaskContentLength(String taskContentLength) {
        this.taskContentLength = taskContentLength;
    }

    public String getTaskContentExt() {
        return taskContentExt;
    }

    public void setTaskContentExt(String taskContentExt) {
        this.taskContentExt = taskContentExt;
    }

    public String getTaskResultLength() {
        return taskResultLength;
    }

    public void setTaskResultLength(String taskResultLength) {
        this.taskResultLength = taskResultLength;
    }

    public String getTaskResultExt() {
        return taskResultExt;
    }

    public void setTaskResultExt(String taskResultExt) {
        this.taskResultExt = taskResultExt;
    }

    public String getConvertTime() {
        return convertTime;
    }

    public void setConvertTime(String convertTime) {
        this.convertTime = convertTime;
    }

    public boolean isReAction() {
        return isReAction;
    }

    public void setReAction(boolean reAction) {
        isReAction = reAction;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
