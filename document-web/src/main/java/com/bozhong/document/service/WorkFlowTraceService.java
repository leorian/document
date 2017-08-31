package com.bozhong.document.service;

import com.bozhong.document.entity.TaskEntity;

/**
 * Created by xiezg@317hu.com on 2017/4/26 0026.
 */
public interface WorkFlowTraceService {
    /**
     * 任务状态设置（就绪）
     *
     * @param taskEntity
     */
    void waiting(TaskEntity taskEntity);

    /**
     * @return
     */
    TaskEntity findWaitingToExecuting();

    /**
     * 任务状态重新执行时设置（就绪）
     *
     * @param taskEntity
     */
    void waitingForReAction(TaskEntity taskEntity);

    /**
     * 任务状态设置(正在执行)
     *
     * @param taskEntity
     */
    void executing(TaskEntity taskEntity);

    /**
     * 任务状态重新执行时设置（正在执行）
     *
     * @param taskEntity
     */
    void executingForReAction(TaskEntity taskEntity);

    /**
     * 任务状态设置（执行失败）
     *
     * @param taskEntity
     */
    void failure(TaskEntity taskEntity);

    /**
     * 任务状态设置(执行成功)
     *
     * @param taskEntity
     */
    void success(TaskEntity taskEntity);

    /**
     * @param taskId
     * @return
     */
    TaskEntity findTaskEntityByTaskId(String taskId);
}
