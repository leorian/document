package com.bozhong.document.task;

import com.bozhong.common.util.StringUtil;
import com.bozhong.config.common.ConfigSetPropertyHolder;
import com.bozhong.document.common.DocumentLogger;
import com.bozhong.document.entity.TaskEntity;
import com.bozhong.document.pool.OpenOfficeConnectionPool;
import com.bozhong.document.service.WorkFlowTraceService;
import com.bozhong.document.util.DocumentException;
import com.qiniu.storage.model.DefaultPutRet;

import java.util.concurrent.Callable;

/**
 * Created by xiezg@317hu.com on 2017/4/26 0026.
 */
public class DocumentTask implements Callable<Boolean> {

    private final TaskEntity taskEntity;

    private final OpenOfficeConnectionPool openOfficeConnectionPool;

    private final WorkFlowTraceService workFlowTraceService;

    public DocumentTask(TaskEntity taskEntity, OpenOfficeConnectionPool openOfficeConnectionPool,
                        WorkFlowTraceService workFlowTraceService) {
        this.taskEntity = taskEntity;
        this.openOfficeConnectionPool = openOfficeConnectionPool;
        this.workFlowTraceService = workFlowTraceService;
    }

    public void run() {
        try {
            workFlowTraceService.executing(taskEntity);
            DefaultPutRet defaultPutRet = new Converter().convertAndUploadDocLinkFile2PDF(openOfficeConnectionPool,
                    taskEntity, workFlowTraceService);
            taskEntity.setTaskResult(ConfigSetPropertyHolder.getProperty("qiniu_domain") + defaultPutRet.key);
            workFlowTraceService.success(taskEntity);
        } catch (Throwable e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
            if (e instanceof DocumentException) {
                DocumentException documentException = (DocumentException) e;
                taskEntity.setErrorCode(documentException.getErrorCode());
                taskEntity.setErrorMessage(documentException.getErrorMessage());
            } else {
                taskEntity.setErrorCode(e.getClass().getName());
                taskEntity.setErrorMessage(e.getMessage());
            }
            workFlowTraceService.failure(taskEntity);
        } finally {
            try {
                //执行结果异步回调
                if (StringUtil.isNotBlank(taskEntity.getCallBackUrl())) {
                    DocHttpUtil.docConvertCallBack(taskEntity.getCallBackUrl(), taskEntity);
                }
            } catch (Throwable e) {
                DocumentLogger.getSysLogger().error(e.getMessage(), e);
            }
        }
    }

    @Override
    public Boolean call() throws Exception {
        try {
            run();
        } catch (Throwable e) {
            return false;
        }

        return true;
    }
}
