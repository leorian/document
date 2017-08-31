package com.bozhong.document.junit;

import com.alibaba.fastjson.JSON;
import com.bozhong.document.common.TaskStatusEnum;
import com.bozhong.document.entity.TaskEntity;
import com.bozhong.document.service.MongoService;
import com.bozhong.document.task.ZkUtil;
import com.bozhong.myredis.MyRedisClusterForHessian;
import com.yx.eweb.handler.SpringApplicationContextHolder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by xiezhonggui on 2017/4/25.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-basic.xml")
public class DocumentBaseTest {

    @Autowired
    private MyRedisClusterForHessian myRedisClusterForHessian;

    @Autowired
    private MongoService mongoService;

    @Test
    public void testRedisOpt() {
        myRedisClusterForHessian.putForStr("hello", "world");
        System.out.println(myRedisClusterForHessian.getForStr("hello"));
    }

    @Test
    public void testSpringApplicationContextHolder() {
        MyRedisClusterForHessian myRedisClusterForHessian = SpringApplicationContextHolder.getSpringBean("myRedisClusterForHessian");
        myRedisClusterForHessian.putForStr("xiezg", "罗田");
        System.out.println(myRedisClusterForHessian.getForStr("xiezg"));
    }

    @Test
    public void testRedisUtil() {
    }

    @Test
    public void testTaskEntityInsert() {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTaskId(UUID.randomUUID().toString());
        taskEntity.setCreateTimeStamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        mongoService.insertOne(taskEntity);
    }

    @Test
    public void testTaskEntityUpdate() {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setExecuteTimeStamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        mongoService.updateOneByTaskId("78b35d3d-77a3-4bee-b634-221a4888a391", taskEntity);
    }

    @Test
    public void testGroupBy() {
        Map<String, Integer> appCountMap1 = mongoService.groupByAppId(TaskEntity.class);
        Map<String, Integer> appCountMap = mongoService.groupByAppId(TaskEntity.class, TaskStatusEnum.SUCCESS.getName());
        System.out.println(JSON.toJSONString(appCountMap1));
        System.out.println(JSON.toJSONString(appCountMap));
    }

    @Test
    public void testDelete() {
        List<String> taskIdList = ZkUtil.getAllExecutingTaskIdRecords();
        if (!CollectionUtils.isEmpty(taskIdList)) {
            for (String taskId : taskIdList) {
                ZkUtil.deleteFinishOrFailureTaskIdRecord(taskId);
            }
        }
    }
}
