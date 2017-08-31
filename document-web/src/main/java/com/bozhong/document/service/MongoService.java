package com.bozhong.document.service;

import com.bozhong.config.domain.JqPage;

import java.util.List;
import java.util.Map;

/**
 * Created by xiezg@317hu.com on 2017/4/14 0014.
 */
public interface MongoService {

    /**
     * @param t
     * @param <T>
     */
    <T> void insertOne(T t);

    /**
     * @param tList
     * @param tClass
     * @param <T>
     */
    <T> void insertMany(List<T> tList, Class<T> tClass);

    /**
     * @param tClass
     * @param <T>
     */
    <T> T findOneWaitingUpdateToExecuting(Class<T> tClass);

    /**
     * @param taskId
     * @param t
     * @param <T>
     */
    <T> void updateOneByTaskId(String taskId, T t);

    /**
     * @param taskId
     * @param tClass
     * @param <T>
     * @return
     */
    <T> T findOneByTaskId(String taskId, Class<T> tClass);

    /**
     * @param taskContent
     * @param tClass
     * @param <T>
     * @return
     */
    <T> T findOneByTaskContent(String taskContent, Class<T> tClass);


    /**
     * @param taskStatus
     * @param tClass
     * @param <T>
     * @return
     */
    <T> long findCountByTaskStatus(String taskStatus, Class<T> tClass);

    /**
     * @param taskStatus
     * @param tClass
     * @param appId
     * @param <T>
     * @return
     */
    <T> long findCountByTaskStatusAndAppId(String taskStatus, Class<T> tClass, String appId);

    /**
     * @param tClass
     * @param <T>
     * @return
     */
    <T> long findCount(Class<T> tClass);

    /**
     * @param tClass
     * @param appId
     * @param <T>
     * @return
     */
    <T> long findCountByAppId(Class<T> tClass, String appId);

    /**
     * @param tClass
     * @param appIds
     * @param <T>
     * @return
     */
    <T> long findAuthCount(Class<T> tClass, String[] appIds);

    /**
     * @param tClass
     * @param appIds
     * @param taskStatus
     * @param <T>
     * @return
     */
    <T> long findAuthCountByTaskStatus(Class<T> tClass, String[] appIds, String taskStatus);

    /**
     * @param jqPage
     * @param tClass
     * @param <T>
     * @return
     */
    <T> JqPage<T> getJqPage(JqPage<T> jqPage, Class<T> tClass);

    /**
     * @param jqPage
     * @param tClass
     * @param <T>
     * @return
     */
    <T> JqPage<T> getJqPageByAppId(JqPage<T> jqPage, Class<T> tClass, String appId);


    /**
     * @param taskStatus
     * @param jqPage
     * @param tClass
     * @param <T>
     * @return
     */
    <T> JqPage<T> getJqPage(String taskStatus, JqPage<T> jqPage, Class<T> tClass);

    /**
     * @param taskStatus
     * @param jqPage
     * @param tClass
     * @param appId
     * @param <T>
     * @return
     */
    <T> JqPage<T> getJqPageByAppId(String taskStatus, JqPage<T> jqPage, Class<T> tClass, String appId);

    /**
     * @param tClass
     * @param <T>
     * @return
     */
    <T> Map<String, Integer> groupByAppId(Class<T> tClass);

    /**
     * @param tClass
     * @param <T>
     */
    <T> Map<String, Integer> groupByAppId(Class<T> tClass, String taskStatus);

    /**
     * @param tClass
     * @param taskStatus
     * @param createTimeStamp
     * @param <T>
     * @return
     */
    <T> Long findTodayCount(Class<T> tClass, String taskStatus, String createTimeStamp);

    /**
     * @param tClass
     * @param taskStatus
     * @param startTime
     * @param endTime
     * @param <T>
     * @return
     */
    <T> Long findYesterdayCount(Class<T> tClass, String taskStatus, String startTime, String endTime);
}
