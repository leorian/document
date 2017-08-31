package com.bozhong.document.task;

import com.bozhong.document.common.DocumentErrorEnum;
import com.bozhong.document.common.DocumentLogger;
import com.bozhong.document.common.TaskStatusEnum;
import com.bozhong.document.entity.TaskEntity;
import com.bozhong.document.pool.OpenOfficeConnectionPool;
import com.bozhong.document.service.WorkFlowTraceService;
import com.bozhong.document.util.DocumentException;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by xiezhonggui on 2017/5/10.
 */

public abstract class AbstractDocumentWorkQueue {
    private final int nThreads;
    private final OpenOfficeConnectionPool openOfficeConnectionPool;
    private final WorkFlowTraceService workFlowTraceService;

    public AbstractDocumentWorkQueue(int nThreads, OpenOfficeConnectionPool openOfficeConnectionPool,
                                     WorkFlowTraceService workFlowTraceService) {
        this.nThreads = nThreads;
        this.openOfficeConnectionPool = openOfficeConnectionPool;
        this.workFlowTraceService = workFlowTraceService;
        try {
            initErrorExecutingToWaiting();
        } catch (Throwable e) {
            e.printStackTrace();
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
        }

    }

    protected void execute(TaskEntity taskEntity) {
        try {
            if (taskEntity.isReAction()) {
                workFlowTraceService.waitingForReAction(taskEntity);
            } else {
                workFlowTraceService.waiting(taskEntity);
            }
        } catch (Throwable e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
            //数据库操作异常
            throw new DocumentException(DocumentErrorEnum.E10002.getError(), DocumentErrorEnum.E10002.getMsg());
        }
    }

    protected void initErrorExecutingToWaiting() {
        List<String> executingTaskIdList = ZkUtil.getAllExecutingTaskIdRecords();
        if (CollectionUtils.isEmpty(executingTaskIdList)) {
            return;
        }

        for (String executingTaskId : executingTaskIdList) {
            ZkUtil.deleteFinishOrFailureTaskIdRecord(executingTaskId);
            TaskEntity taskEntity = workFlowTraceService.findTaskEntityByTaskId(executingTaskId);
            if (taskEntity == null || !TaskStatusEnum.EXECUTING.getName().equals(taskEntity.getTaskStatus())) {
                //未查询到相关数据
                continue;
            }
            taskEntity.setReAction(true);
            execute(taskEntity);
        }
    }
}
