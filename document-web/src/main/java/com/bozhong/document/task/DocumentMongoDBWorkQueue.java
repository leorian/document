package com.bozhong.document.task;

import com.bozhong.document.entity.TaskEntity;
import com.bozhong.document.pool.OpenOfficeConnectionPool;
import com.bozhong.document.service.WorkFlowTraceService;


/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class DocumentMongoDBWorkQueue extends AbstractDocumentWorkQueue {
    private final DocumentMongoDBWorkerThread[] threads;

    public DocumentMongoDBWorkQueue(int nThreads, OpenOfficeConnectionPool openOfficeConnectionPool,
                                    WorkFlowTraceService workFlowTraceService) {
        super(nThreads, openOfficeConnectionPool, workFlowTraceService);
        threads = new DocumentMongoDBWorkerThread[nThreads];
        for (int i = 0; i < nThreads; i++) {
            threads[i] = new DocumentMongoDBWorkerThread(openOfficeConnectionPool, workFlowTraceService);
            threads[i].start();
        }
    }


    public void execute(TaskEntity taskEntity) {
        super.execute(taskEntity);
    }
}

