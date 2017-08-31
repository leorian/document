package com.bozhong.document.task;

import com.bozhong.document.common.DocumentErrorEnum;
import com.bozhong.document.common.DocumentLogger;
import com.bozhong.document.common.WebSettingParam;
import com.bozhong.document.entity.TaskEntity;
import com.bozhong.document.pool.OpenOfficeConnectionPool;
import com.bozhong.document.service.WorkFlowTraceService;

import java.util.Calendar;
import java.util.concurrent.*;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class DocumentMongoDBWorkerThread extends Thread {

    private final OpenOfficeConnectionPool openOfficeConnectionPool;
    private final WorkFlowTraceService workFlowTraceService;

    public DocumentMongoDBWorkerThread(OpenOfficeConnectionPool openOfficeConnectionPool,
                                       WorkFlowTraceService workFlowTraceService) {
        this.openOfficeConnectionPool = openOfficeConnectionPool;
        this.workFlowTraceService = workFlowTraceService;
    }

    public void run() {
        while (true) {

            try {
                Thread.sleep(1000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //是否开启异步文档转换服务
            if (!WebSettingParam.OPEN_ASYNC_CONVERSION) {
                System.out.println(WebSettingParam.OPEN_ASYNC_CONVERSION);
                continue;
            }

            TaskEntity taskEntity = workFlowTraceService.findWaitingToExecuting();
            if (taskEntity == null) {
                continue;
            }

            System.out.println("taskEntity: " + taskEntity);
            taskEntity.setExecuteTimeStamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            workFlowTraceService.executing(taskEntity);
            FutureTask<Boolean> futureTask = new FutureTask<Boolean>(new DocumentTask(taskEntity,
                    openOfficeConnectionPool, workFlowTraceService));
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(futureTask);
            try {
//                futureTask.get(Integer.parseInt(System.getProperty("openOffice.timeout")),
//                        TimeUnit.MILLISECONDS);
                futureTask.get();
            } catch (Throwable e) {
                DocumentLogger.getSysLogger().error(e.getMessage(), e);
                try {
                    futureTask.cancel(true);
                    executorService.shutdownNow();
                    taskEntity.setErrorCode(DocumentErrorEnum.E10022.getError());
                    taskEntity.setErrorMessage(DocumentErrorEnum.E10022.getMsg());
                    workFlowTraceService.failure(taskEntity);
                    Command.exeCmd(System.getProperty("openOffice.starter"));
                } catch (Throwable e1) {
                    DocumentLogger.getSysLogger().error(e1.getMessage(), e);
                }
            }

            executorService.shutdownNow();

            System.out.println("线程运行结束");
        }
    }
}
