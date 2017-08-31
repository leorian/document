package com.bozhong.document.task;

import com.bozhong.document.common.DocumentLogger;
import com.bozhong.document.entity.TaskEntity;
import com.bozhong.document.pool.OpenOfficeConnectionPool;
import com.bozhong.document.service.WorkFlowTraceService;
import com.bozhong.document.util.ObjectUtil;
import com.bozhong.document.util.QueueUtil;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class DocumentRedisWorkerThread extends Thread {

    private final OpenOfficeConnectionPool openOfficeConnectionPool;
    private final WorkFlowTraceService workFlowTraceService;

    public DocumentRedisWorkerThread(OpenOfficeConnectionPool openOfficeConnectionPool,
                                     WorkFlowTraceService workFlowTraceService) {
        this.openOfficeConnectionPool = openOfficeConnectionPool;
        this.workFlowTraceService = workFlowTraceService;
    }

    public void run() {
        while (true) {
            byte[] taskEntityBytes = RedisUtil.rpop(QueueUtil.getDQueueKey());
            if (taskEntityBytes == null) {
                continue;
            }

            TaskEntity taskEntity = null;

            try {
                taskEntity = (TaskEntity) ObjectUtil.bytes2Object(taskEntityBytes);
            } catch (Throwable e) {
                DocumentLogger.getSysLogger().error(e.getMessage(), e);
            }

            System.out.println("taskEntity: " + taskEntity);

            new DocumentTask(taskEntity, openOfficeConnectionPool, workFlowTraceService).run();
        }
    }
}
