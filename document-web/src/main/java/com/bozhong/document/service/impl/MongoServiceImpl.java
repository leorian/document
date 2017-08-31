package com.bozhong.document.service.impl;

import com.bozhong.common.util.StringUtil;
import com.bozhong.config.domain.JqPage;
import com.bozhong.document.dao.MongoDao;
import com.bozhong.document.service.MongoService;

import java.util.List;
import java.util.Map;

/**
 * Created by xiezg@317hu.com on 2017/4/14 0014.
 */
public class MongoServiceImpl implements MongoService {
    private MongoDao mongoDao;

    @Override
    public <T> void insertOne(T t) {
        mongoDao.insertOne(t);
    }

    @Override
    public <T> void insertMany(List<T> list, Class<T> tClass) {
        mongoDao.insertMany(list, tClass);
    }

    @Override
    public <T> T findOneWaitingUpdateToExecuting(Class<T> tClass) {
        return mongoDao.findOneWaitingUpdateToExecuting(tClass);
    }

    @Override
    public <T> void updateOneByTaskId(String taskId, T t) {
        mongoDao.updateOneByTaskId(taskId, t);
    }

    @Override
    public <T> T findOneByTaskId(String taskId, Class<T> tClass) {
        return mongoDao.findOneByTaskId(taskId, tClass);
    }

    @Override
    public <T> T findOneByTaskContent(String taskContent, Class<T> tClass) {
        return mongoDao.findOneByTaskContent(taskContent, tClass);
    }

    @Override
    public <T> long findCountByTaskStatus(String taskStatus, Class<T> tClass) {
        return mongoDao.findCountByTaskStatus(taskStatus, tClass);
    }

    @Override
    public <T> long findCountByTaskStatusAndAppId(String taskStatus, Class<T> tClass, String appId) {
        if (StringUtil.isBlank(appId)) {
            return mongoDao.findCountByTaskStatus(taskStatus, tClass);
        }

        return mongoDao.findCountByTaskStatusAndAppId(taskStatus, tClass, appId);
    }

    @Override
    public <T> long findCount(Class<T> tClass) {
        return mongoDao.findCount(tClass);
    }

    @Override
    public <T> long findCountByAppId(Class<T> tClass, String appId) {
        if (StringUtil.isBlank(appId)) {
            return mongoDao.findCount(tClass);
        }

        return mongoDao.findCountByAppId(tClass, appId);
    }

    @Override
    public <T> long findAuthCount(Class<T> tClass, String[] appIds) {
        return mongoDao.findAuthCount(tClass, appIds);
    }

    @Override
    public <T> long findAuthCountByTaskStatus(Class<T> tClass, String[] appIds, String taskStatus) {
        if (StringUtil.isBlank(taskStatus)) {
            return mongoDao.findAuthCount(tClass, appIds);
        }
        return mongoDao.findAuthCountByTaskStatus(tClass, appIds, taskStatus);
    }

    @Override
    public <T> JqPage<T> getJqPage(JqPage<T> jqPage, Class<T> tClass) {
        return mongoDao.getJqPage(jqPage, tClass);
    }

    @Override
    public <T> JqPage<T> getJqPageByAppId(JqPage<T> jqPage, Class<T> tClass, String appId) {
        if (StringUtil.isBlank(appId)) {
            return mongoDao.getJqPage(jqPage, tClass);
        }
        return mongoDao.getJqPageByAppId(jqPage, tClass, appId);
    }

    @Override
    public <T> JqPage<T> getJqPage(String taskStatus, JqPage<T> jqPage, Class<T> tClass) {
        return mongoDao.getJqPage(taskStatus, jqPage, tClass);
    }

    @Override
    public <T> JqPage<T> getJqPageByAppId(String taskStatus, JqPage<T> jqPage, Class<T> tClass, String appId) {

        if (StringUtil.isBlank(appId)) {
            return mongoDao.getJqPage(taskStatus, jqPage, tClass);
        }

        return mongoDao.getJqPageByAppId(taskStatus, jqPage, tClass, appId);
    }

    @Override
    public <T> Map<String, Integer> groupByAppId(Class<T> tClass) {
        return mongoDao.groupByAppId(tClass);
    }

    @Override
    public <T> Map<String, Integer> groupByAppId(Class<T> tClass, String taskStatus) {
        if (StringUtil.isBlank(taskStatus)) {
            return mongoDao.groupByAppId(tClass);
        }
        return mongoDao.groupByAppId(tClass, taskStatus);
    }

    @Override
    public <T> Long findTodayCount(Class<T> tClass, String taskStatus, String createTimeStamp) {
        return mongoDao.findTodayCount(tClass, taskStatus, createTimeStamp);
    }

    @Override
    public <T> Long findYesterdayCount(Class<T> tClass, String taskStatus, String startTime, String endTime) {
        return mongoDao.findYesterdayCount(tClass, taskStatus, startTime, endTime);
    }

    public void setMongoDao(MongoDao mongoDao) {
        this.mongoDao = mongoDao;
    }


}
