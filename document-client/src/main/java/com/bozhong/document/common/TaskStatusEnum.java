package com.bozhong.document.common;

import com.alibaba.fastjson.JSON;

/**
 * Created by xiezg@317hu.com on 2017/4/26 0026.
 */
public enum TaskStatusEnum {

    WAITING("WAITING", "就绪"),
    EXECUTING("EXECUTING", "正在执行"),
    FAILURE("FAILURE", "执行失败"),
    SUCCESS("SUCCESS", "执行成功");

    TaskStatusEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
