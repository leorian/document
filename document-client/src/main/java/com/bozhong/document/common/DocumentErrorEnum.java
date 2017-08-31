package com.bozhong.document.common;

import com.alibaba.fastjson.JSON;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public enum DocumentErrorEnum {

    E10001("E10001", "Parameter Not Defined", "参数未定义！"),
    E10002("E10002", "Database Operation Error", "数据库操作异常！"),
    E10003("E10003", "WorkerThread Execute Error", "工作线程执行异常！"),
    E10004("E10004", "File Convert Error", "文件转换异常！"),
    E10005("E10005", "Not Find Relation Data", "未查询到相关数据！"),
    E10006("E10006", "File Unreachable", "文件链接地址不可达！"),
    E10007("E10007", "File Format UnSupport", "文件格式不支持！"),
    E10008("E10008", "File Size UnKnown", "文件大小未知"),
    E10009("E10009", "File Content Unreachable", "文件内容获取不到！"),
    E10010("E10010", "File Convert Error", "文件转换失败！"),
    E10011("E10011", "QiNiu Upload Error", "七牛上传失败"),
    E10012("E10012", "File Not Exist", "文件不存在！"),
    E10013("E10013", "Pdf Format Not Need Convert", "pdf格式文件无需转换！"),
    E10014("E10014", "Count Search Error", "统计查询异常！"),
    E10015("E10015", "QiNiu Get File Info Error", "七牛获取文件信息异常！"),
    E10016("E10016", "Net Work Error", "网络连接异常！"),
    E10017("E10017", "Open Office Service Error", "文件转换服务异常！"),
    E10018("E10018", "Login Failure Public Private Key Expire", "用户密码公钥私钥加密策略过期，请重新进入登录页面！"),
    E10019("E10019", "Same Http Request Frequency Too Many, 403 Forbidden", "同一请求频繁，被禁止！"),
    E10020("E10020", "Waiting Queue Operation Error", "等待队列操作异常！"),
    E10021("E10021", "Remote File Write Local Error", "远程文件写入本地异常！"),
    E10022("E10022", "File Convert TimeOut", "转换任务执行超时！"),
    E10023("E10023", "Document SYNC Conversion Service Not Open", "文档同步转换服务未开启!"),
    E10024("E10024", "Document Conversion Service System Resource Scarcity, Forbidden Same Document Convert Frequency",
            "文档转换服务系统资源稀缺，禁止频繁转换同一文档！"),
    E10025("E10025", "Current Document Max Executing Conversion Limit, Please Waiting Request!!!", "当前文档同步转换并发数太多，请稍后重试"),
    E10026("E10026", "The Document File Size Exceeds The Limit ", "文档大小超过限制"),
    E20001("E20001", "转化失败，未知错误...", "转化失败，未知错误..."),
    E20002("E20002", "原文件就是PDF文件,无需转化...", "原文件就是PDF文件,无需转化..."),
    E20003("E20003", "转化失败，文件不存在...", "转化失败，文件不存在..."),
    E20004("E20004", "转化失败，请重新尝试...", "转化失败，请重新尝试...");


    private String error;

    private String msg;

    private String cnMsg;

    DocumentErrorEnum(String error, String msg, String cnMsg) {
        this.error = error;
        this.msg = msg;
        this.cnMsg = cnMsg;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCnMsg() {
        return cnMsg;
    }

    public void setCnMsg(String cnMsg) {
        this.cnMsg = cnMsg;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
