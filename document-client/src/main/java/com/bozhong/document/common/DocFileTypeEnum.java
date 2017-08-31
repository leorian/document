package com.bozhong.document.common;

import com.alibaba.fastjson.JSON;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiezg@317hu.com on 2017/4/27 0027.
 * 文件链接地址的ContentType转换成文件扩展名
 */
public enum DocFileTypeEnum {

    DOC("doc", "application/msword"),
    DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    PPT("ppt", "application/vnd.ms-powerpoint"),
    PPTX("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    XLS("xls", "application/vnd.ms-excel"),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    PDF("pdf", "application/pdf"),;

    public static final Map<String, String> contentTypeExtMap = new HashMap<>();

    static {
        for (DocFileTypeEnum docFileTypeEnum : EnumSet.allOf(DocFileTypeEnum.class)) {
            contentTypeExtMap.put(docFileTypeEnum.getContentType(), docFileTypeEnum.getExt());
        }
    }

    DocFileTypeEnum(String ext, String contentType) {
        this.ext = ext;
        this.contentType = contentType;
    }

    private String ext;
    private String contentType;

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
