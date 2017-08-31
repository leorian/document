package com.bozhong.document.entity;


import java.io.Serializable;

/**
 * Created by renyueliang on 16/12/29.
 */
public class AppDO implements Serializable {

    private String appId;
    private String appName;
    private boolean mainSelectType = false;//应用管理选中状态
    private boolean viewSelectType = false;//应用预览选中状态

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


    public boolean isMainSelectType() {
        return mainSelectType;
    }

    public void setMainSelectType(boolean mainSelectType) {
        this.mainSelectType = mainSelectType;
    }

    public boolean isViewSelectType() {
        return viewSelectType;
    }

    public void setViewSelectType(boolean viewSelectType) {
        this.viewSelectType = viewSelectType;
    }
}
