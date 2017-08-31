package com.bozhong.document.dao.impl;

import com.bozhong.config.common.MongoDBConfig;
import com.bozhong.config.domain.JqPage;
import com.bozhong.document.common.TaskStatusEnum;
import com.bozhong.document.dao.MongoDao;
import com.bozhong.document.entity.TaskEntity;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.descending;


/**
 * Created by xiezg@317hu.com on 2017/4/14 0014.
 */
public class MongoDaoImpl implements MongoDao {
    @Autowired
    private MongoDBConfig mongoDBConfig;

    @Override
    public <T> void insertOne(T t) {
        Gson gson = new Gson();
        Document document = gson.fromJson(t.toString(), Document.class);
        MongoCollection<Document> mongoCollection = mongoDBConfig.getCollection(t.getClass());
        mongoCollection.insertOne(document);
    }

    @Override
    public <T> void insertMany(List<T> tlist, Class<T> tClass) {
        Gson gson = new Gson();
        List<Document> documentList = gson.fromJson(gson.toJson(tlist), new TypeToken<List<Document>>() {
        }.getType());
        MongoCollection<Document> mongoCollection = mongoDBConfig.getCollection(tClass);
        mongoCollection.insertMany(documentList);
    }

    @Override
    public <T> T findOneWaitingUpdateToExecuting(Class<T> tClass) {
        Gson gson = new Gson();
        MongoCollection<Document> mongoCollection = mongoDBConfig.getCollection(tClass);
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTaskStatus(TaskStatusEnum.EXECUTING.getName());
        taskEntity.setExecuteTimeStamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        Document document = mongoCollection.findOneAndUpdate(eq("taskStatus", TaskStatusEnum.WAITING.getName()),
                new Document("$set", gson.fromJson(taskEntity.toString(), Document.class)));
        if (document != null) {
            return gson.fromJson(document.toJson(), tClass);
        }

        return null;
    }

    @Override
    public <T> void updateOneByTaskId(String taskId, T t) {
        Gson gson = new Gson();
        Document document = gson.fromJson(t.toString(), Document.class);
        MongoCollection<Document> mongoCollection = mongoDBConfig.getCollection(t.getClass());
        mongoCollection.updateOne(eq("taskId", taskId), new Document("$set", document));
    }

    @Override
    public <T> T findOneByTaskId(String taskId, Class<T> tClass) {
        MongoCollection<Document> mongoCollection = mongoDBConfig.getCollection(tClass);
        Document document = mongoCollection.find(eq("taskId", taskId)).first();
        if (document != null) {
            Gson gson = new Gson();
            return gson.fromJson(document.toJson(), tClass);
        }

        return null;
    }

    @Override
    public <T> T findOneByTaskContent(String taskContent, Class<T> tClass) {
        MongoCollection<Document> mongoCollection = mongoDBConfig.getCollection(tClass);
        Document document = mongoCollection.find(eq("taskContent", taskContent)).first();
        if (document != null) {
            Gson gson = new Gson();
            return gson.fromJson(document.toJson(), tClass);
        }

        return null;
    }

    @Override
    public <T> long findCountByTaskStatus(String taskStatus, Class<T> tClass) {
        MongoCollection<Document> mongoCollection = mongoDBConfig.getCollection(tClass);
        return mongoCollection.count(eq("taskStatus", taskStatus));
    }

    @Override
    public <T> long findCountByTaskStatusAndAppId(String taskStatus, Class<T> tClass, String appId) {
        MongoCollection<Document> mongoCollection = mongoDBConfig.getCollection(tClass);
        return mongoCollection.count(and(eq("taskStatus", taskStatus), eq("appId", appId)));
    }

    @Override
    public <T> long findCount(Class<T> tClass) {
        MongoCollection<Document> mongoCollection = mongoDBConfig.getCollection(tClass);
        return mongoCollection.count();
    }

    @Override
    public <T> long findCountByAppId(Class<T> tClass, String appId) {
        MongoCollection<Document> mongoCollection = mongoDBConfig.getCollection(tClass);
        return mongoCollection.count(eq("appId", appId));
    }

    @Override
    public <T> long findAuthCount(Class<T> tClass, String[] appIds) {
        MongoCollection<Document> mongoCollection = mongoDBConfig.getCollection(tClass);
        return mongoCollection.count(in("appId", appIds));
    }

    @Override
    public <T> long findAuthCountByTaskStatus(Class<T> tClass, String[] appIds, String taskStatus) {
        MongoCollection<Document> mongoCollection = mongoDBConfig.getCollection(tClass);
        return mongoCollection.count(and(in("appId", appIds),
                eq("taskStatus", taskStatus)));
    }

    @Override
    public <T> JqPage<T> getJqPage(JqPage<T> jqPage, Class<T> tClass) {
        MongoCollection<Document> mongoCollection = mongoDBConfig.getCollection(tClass);
        FindIterable<Document> findIterable = mongoCollection.find().sort(descending("createTimeStamp"))
                .skip(jqPage.getFromIndex()).limit(jqPage.getPageSize());
        Iterator<Document> iterator = findIterable.iterator();
        List<T> rows = new ArrayList<>(jqPage.getPageSize());
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        while (iterator.hasNext()) {
            Document document = iterator.next();
            rows.add(gson.fromJson(document.toJson(), tClass));
        }
        jqPage.setRecords((int) mongoCollection.count());
        jqPage.setRows(rows);
        return jqPage;
    }

    @Override
    public <T> JqPage<T> getJqPageByAppId(JqPage<T> jqPage, Class<T> tClass, String appId) {
        MongoCollection<Document> mongoCollection = mongoDBConfig.getCollection(tClass);
        FindIterable<Document> findIterable = mongoCollection.find(eq("appId", appId)).
                sort(descending("createTimeStamp")).skip(jqPage.getFromIndex()).limit(jqPage.getPageSize());
        Iterator<Document> iterator = findIterable.iterator();
        List<T> rows = new ArrayList<>(jqPage.getPageSize());
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        while (iterator.hasNext()) {
            Document document = iterator.next();
            rows.add(gson.fromJson(document.toJson(), tClass));
        }
        jqPage.setRecords((int) mongoCollection.count(eq("appId", appId)));
        jqPage.setRows(rows);
        return jqPage;
    }

    @Override
    public <T> JqPage<T> getJqPage(String taskStatus, JqPage<T> jqPage, Class<T> tClass) {
        MongoCollection<Document> mongoCollection = mongoDBConfig.getCollection(tClass);
        FindIterable<Document> findIterable = mongoCollection.find(eq("taskStatus", taskStatus)).
                sort(descending("createTimeStamp"))
                .skip(jqPage.getFromIndex()).limit(jqPage.getPageSize());
        Iterator<Document> iterator = findIterable.iterator();
        List<T> rows = new ArrayList<>(jqPage.getPageSize());
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        while (iterator.hasNext()) {
            Document document = iterator.next();
            rows.add(gson.fromJson(document.toJson(), tClass));
        }
        jqPage.setRecords((int) mongoCollection.count(eq("taskStatus", taskStatus)));
        jqPage.setRows(rows);
        return jqPage;
    }

    @Override
    public <T> JqPage<T> getJqPageByAppId(String taskStatus, JqPage<T> jqPage, Class<T> tClass, String appId) {
        MongoCollection<Document> mongoCollection = mongoDBConfig.getCollection(tClass);
        FindIterable<Document> findIterable = mongoCollection.find(and(eq("taskStatus", taskStatus),
                eq("appId", appId))).
                sort(descending("createTimeStamp"))
                .skip(jqPage.getFromIndex()).limit(jqPage.getPageSize());
        Iterator<Document> iterator = findIterable.iterator();
        List<T> rows = new ArrayList<>(jqPage.getPageSize());
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        while (iterator.hasNext()) {
            Document document = iterator.next();
            rows.add(gson.fromJson(document.toJson(), tClass));
        }
        jqPage.setRecords((int) mongoCollection.count(and(eq("taskStatus", taskStatus),
                eq("appId", appId))));
        jqPage.setRows(rows);
        return jqPage;
    }

    @Override
    public <T> Map<String, Integer> groupByAppId(Class<T> tClass) {
        MongoCollection<Document> mongoCollection = mongoDBConfig.getCollection(tClass);
        AggregateIterable<Document> aggregateIterable = mongoCollection.aggregate(Arrays.asList(new Document("$project",
                        new Document("taskId", 1).append("_id", 0).append("appId", 1)),
                new Document("$unwind", "$appId"),
                new Document("$group", new Document("_id", "$appId").append("count", new Document("$sum", 1)))));
        MongoCursor<Document> mongoCursor = aggregateIterable.iterator();
        Map<String, Integer> appCountMap = new HashMap<>();
        while (mongoCursor.hasNext()) {
            Document document = mongoCursor.next();
            appCountMap.put(document.getString("_id"), document.getInteger("count"));
        }

        return appCountMap;
    }

    @Override
    public <T> Map<String, Integer> groupByAppId(Class<T> tClass, String taskStatus) {
        MongoCollection<Document> mongoCollection = mongoDBConfig.getCollection(tClass);
        AggregateIterable<Document> aggregateIterable = mongoCollection.aggregate(Arrays.asList(new Document("$project",
                        new Document("taskId", 1).append("_id", 0).append("appId", 1).append("taskStatus", 1)),
                new Document("$match", new Document("taskStatus", taskStatus))
                , new Document("$unwind", "$appId"),
                new Document("$group", new Document("_id", "$appId").append("count", new Document("$sum", 1)))));
        MongoCursor<Document> mongoCursor = aggregateIterable.iterator();
        Map<String, Integer> appCountMap = new HashMap<>();
        while (mongoCursor.hasNext()) {
            Document document = mongoCursor.next();
            appCountMap.put(document.getString("_id"), document.getInteger("count"));
        }

        return appCountMap;
    }

    @Override
    public <T> Long findTodayCount(Class<T> tClass, String taskStatus, String createTimeStamp) {
        MongoCollection<Document> mongoCollection = mongoDBConfig.getCollection(tClass);
        return mongoCollection.count(and(eq("taskStatus", taskStatus),
                gte("createTimeStamp", createTimeStamp)));
    }

    @Override
    public <T> Long findYesterdayCount(Class<T> tClass, String taskStatus, String startTime, String endTime) {
        MongoCollection<Document> mongoCollection = mongoDBConfig.getCollection(tClass);
        return mongoCollection.count(and(eq("taskStatus", taskStatus),
                lte("createTimeStamp", endTime), gte("createTimeStamp", startTime)));
    }
}
