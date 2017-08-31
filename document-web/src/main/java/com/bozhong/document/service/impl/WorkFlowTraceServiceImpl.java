package com.bozhong.document.service.impl;

import com.bozhong.common.util.StringUtil;
import com.bozhong.document.common.DocumentLogger;
import com.bozhong.document.common.TaskStatusEnum;
import com.bozhong.document.entity.TaskEntity;
import com.bozhong.document.service.MongoService;
import com.bozhong.document.service.WorkFlowTraceService;
import com.bozhong.document.task.ZkUtil;

import java.util.Calendar;

/**
 * Created by xiezg@317hu.com on 2017/4/26 0026.
 */
public class WorkFlowTraceServiceImpl implements WorkFlowTraceService {

    private MongoService mongoService;

    @Override
    public void waiting(TaskEntity taskEntity) {
        taskEntity.setTaskStatus(TaskStatusEnum.WAITING.getName());
        taskEntity.setCreateTimeStamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        mongoService.insertOne(taskEntity);
    }

    @Override
    public TaskEntity findWaitingToExecuting() {
        return mongoService.findOneWaitingUpdateToExecuting(TaskEntity.class);
    }

    @Override
    public void waitingForReAction(TaskEntity taskEntity) {
        taskEntity.setCreateTimeStamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        mongoService.updateOneByTaskId(taskEntity.getTaskId(), taskEntity);
        taskEntity.setTaskStatus(TaskStatusEnum.WAITING.getName());
        mongoService.updateOneByTaskId(taskEntity.getTaskId(), taskEntity);
    }

    @Override
    public void executing(TaskEntity taskEntity) {
        taskEntity.setTaskStatus(TaskStatusEnum.EXECUTING.getName());
        if (StringUtil.isBlank(taskEntity.getExecuteTimeStamp()) || taskEntity.isReAction()) {
            taskEntity.setExecuteTimeStamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        }

        mongoService.updateOneByTaskId(taskEntity.getTaskId(), taskEntity);

        try {
            ZkUtil.createExecutingTaskIdRecord(taskEntity.getTaskId());
        } catch (Throwable e) {
            e.printStackTrace();
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
        }
    }

    @Override
    public void executingForReAction(TaskEntity taskEntity) {
        taskEntity.setTaskStatus(TaskStatusEnum.EXECUTING.getName());
        taskEntity.setExecuteTimeStamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        mongoService.updateOneByTaskId(taskEntity.getTaskId(), taskEntity);
    }

    @Override
    public void failure(TaskEntity taskEntity) {
        taskEntity.setTaskStatus(TaskStatusEnum.FAILURE.getName());
        taskEntity.setFinishTimeStamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        mongoService.updateOneByTaskId(taskEntity.getTaskId(), taskEntity);

        try {
            ZkUtil.deleteFinishOrFailureTaskIdRecord(taskEntity.getTaskId());
        } catch (Throwable e) {
            e.printStackTrace();
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
        }
    }

    @Override
    public void success(TaskEntity taskEntity) {
        taskEntity.setTaskStatus(TaskStatusEnum.SUCCESS.getName());
        taskEntity.setFinishTimeStamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        mongoService.updateOneByTaskId(taskEntity.getTaskId(), taskEntity);

        try {
            ZkUtil.deleteFinishOrFailureTaskIdRecord(taskEntity.getTaskId());
        } catch (Throwable e) {
            e.printStackTrace();
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
        }
    }

    @Override
    public TaskEntity findTaskEntityByTaskId(String taskId) {
        return mongoService.findOneByTaskId(taskId, TaskEntity.class);
    }

    public void setMongoService(MongoService mongoService) {
        this.mongoService = mongoService;
    }
}
