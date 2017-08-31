package com.bozhong.document.task;

import com.bozhong.document.common.DocumentErrorEnum;
import com.bozhong.document.entity.TaskEntity;
import com.bozhong.document.pool.OpenOfficeConnectionPool;
import com.bozhong.document.service.WorkFlowTraceService;
import com.bozhong.document.util.DocumentException;
import com.bozhong.document.util.ObjectUtil;
import com.bozhong.document.util.QueueUtil;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class DocumentRedisWorkQueue extends AbstractDocumentWorkQueue {


    private final DocumentRedisWorkerThread[] threads;

    public DocumentRedisWorkQueue(int nThreads, OpenOfficeConnectionPool openOfficeConnectionPool,
                                  WorkFlowTraceService workFlowTraceService) {
        super(nThreads, openOfficeConnectionPool, workFlowTraceService);
        threads = new DocumentRedisWorkerThread[nThreads];
        for (int i = 0; i < nThreads; i++) {
            threads[i] = new DocumentRedisWorkerThread(openOfficeConnectionPool, workFlowTraceService);
            threads[i].start();
        }
    }

    public void execute(TaskEntity taskEntity) {
        super.execute(taskEntity);
        try {
            RedisUtil.lpush(QueueUtil.getDQueueKey(), ObjectUtil.object2Bytes(taskEntity));
        } catch (Throwable e) {
            //数据库操作异常
            throw new DocumentException(DocumentErrorEnum.E10020.getError(), DocumentErrorEnum.E10020.getMsg());
        }
    }
}

