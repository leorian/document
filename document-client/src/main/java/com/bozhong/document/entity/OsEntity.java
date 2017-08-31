package com.bozhong.document.entity;

import com.alibaba.fastjson.JSON;
import com.bozhong.document.common.WebSettingParam;
import com.bozhong.myswitch.common.SwitchUtil;

import java.io.Serializable;

/**
 * Created by xiezg@317hu.com on 2017/5/12 0012.
 */
public class OsEntity implements Serializable {

    /**
     *
     */
    private String number;

    /**
     * IP
     */
    private String ip;

    /**
     * 操作系统名称
     */
    private String osName;

    /**
     * 操作系统架构
     */
    private String osArch;

    /**
     * 操作系统版本
     */
    private String osVersion;

    /**
     * jvm总内存
     */
    private String jvmTotalMemory;

    /**
     * jvm剩余内存
     */
    private String jvmFreeMemory;

    /**
     * jvm最大内存
     */
    private String jvmMaxMemory;

    /**
     * 处理器个数
     */
    private String availableProcessors;

    /**
     * 异步转换是否开启
     */
    private String asyncStatus;

    /**
     * 同步转换是否开启
     */
    private String syncStatus;

    /**
     * 单台机器最大执行能力
     */
    private String maxExecutingStatusCount;

    /**
     * 文档大小最大值限制
     */
    private String maxDocFileSize;

    /**
     * 是否开启系统资源限制，目前只支持同一文档转换短时间内禁止
     */
    private String osResourceProtection;

    /**
     * 是否开启系统资源限制，目前只支持同一文档转换短时间内禁止（时间间隔）
     */
    private String osResourceProtectionTime;

    /**
     * 转换工具
     */
    private String converterTools;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsArch() {
        return osArch;
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getJvmTotalMemory() {
        return jvmTotalMemory;
    }

    public void setJvmTotalMemory(String jvmTotalMemory) {
        this.jvmTotalMemory = jvmTotalMemory;
    }

    public String getJvmFreeMemory() {
        return jvmFreeMemory;
    }

    public void setJvmFreeMemory(String jvmFreeMemory) {
        this.jvmFreeMemory = jvmFreeMemory;
    }

    public String getAvailableProcessors() {
        return availableProcessors;
    }

    public void setAvailableProcessors(String availableProcessors) {
        this.availableProcessors = availableProcessors;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getJvmMaxMemory() {
        return jvmMaxMemory;
    }

    public void setJvmMaxMemory(String jvmMaxMemory) {
        this.jvmMaxMemory = jvmMaxMemory;
    }

    public String getAsyncStatus() {
        return asyncStatus;
    }

    public void setAsyncStatus(String asyncStatus) {
        this.asyncStatus = asyncStatus;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getOsResourceProtection() {
        return osResourceProtection;
    }

    public void setOsResourceProtection(String osResourceProtection) {
        this.osResourceProtection = osResourceProtection;
    }

    public String getMaxExecutingStatusCount() {
        return maxExecutingStatusCount;
    }

    public void setMaxExecutingStatusCount(String maxExecutingStatusCount) {
        this.maxExecutingStatusCount = maxExecutingStatusCount;
    }

    public String getMaxDocFileSize() {
        return maxDocFileSize;
    }

    public void setMaxDocFileSize(String maxDocFileSize) {
        this.maxDocFileSize = maxDocFileSize;
    }

    public String getOsResourceProtectionTime() {
        return osResourceProtectionTime;
    }

    public void setOsResourceProtectionTime(String osResourceProtectionTime) {
        this.osResourceProtectionTime = osResourceProtectionTime;
    }

    public String getConverterTools() {
        return converterTools;
    }

    public void setConverterTools(String converterTools) {
        this.converterTools = converterTools;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public OsEntity() {
        this.setIp(SwitchUtil.getIp());
        this.setOsName(System.getProperty("os.name"));
        this.setOsArch(System.getProperty("os.arch"));
        this.setOsVersion(System.getProperty("os.version"));
        this.setJvmTotalMemory(((double) Runtime.getRuntime().totalMemory() / 1024 / 1024) + "MB");
        this.setJvmFreeMemory(((double) Runtime.getRuntime().freeMemory() / 1024 / 1024) + "MB");
        this.setJvmMaxMemory(((double) Runtime.getRuntime().maxMemory() / 1024 / 1024) + "MB");
        this.setAvailableProcessors(Runtime.getRuntime().availableProcessors() + "");
        this.setAsyncStatus(WebSettingParam.OPEN_ASYNC_CONVERSION ? "开启中" : "关闭中");
        this.setSyncStatus(WebSettingParam.OPEN_SYNC_CONVERSION ? "开启中" : "关闭中");
        this.setMaxExecutingStatusCount(WebSettingParam.MAX_EXECUTING_STATUS_COUNT + "");
        this.setMaxDocFileSize(WebSettingParam.MAX_DOC_FILE_LENGTH + "");
        this.setOsResourceProtectionTime(WebSettingParam.CONVERSION_SAME_REQUEST_EXPIRE_TIME + "");
        this.setOsResourceProtection(WebSettingParam.CONVERSION_SAME_REQUEST_CONTROLLER ? "开启中" : "关闭中");
        this.setConverterTools(WebSettingParam.CONVERTER_TOOLS);//转换工具
    }
}
