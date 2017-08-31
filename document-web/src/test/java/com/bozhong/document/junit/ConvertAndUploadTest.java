package com.bozhong.document.junit;

import com.alibaba.fastjson.JSON;
import com.bozhong.document.entity.TaskEntity;
import com.bozhong.document.model.HostIpPort;
import com.bozhong.document.pool.OpenOfficeConnectionPool;
import com.bozhong.document.pool.SocketOpenOfficeConnectionFactory;
import com.bozhong.document.task.Converter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiezg@317hu.com on 2017/4/27 0027.
 */
public class ConvertAndUploadTest extends DocumentBaseTest {

    @Test
    public void testConvertANDUpload() {
        List<HostIpPort> hostIpPorts = new ArrayList<>();
        //hostIpPorts.add(new HostIpPort("172.16.110.2", 8100));
        hostIpPorts.add(new HostIpPort("127.0.0.1", 8100));
        OpenOfficeConnectionPool openOfficeConnectionPool = new OpenOfficeConnectionPool
                (new SocketOpenOfficeConnectionFactory(hostIpPorts));
        String linkFile = "http://okxyat5ou.bkt.clouddn.com/1.ppt";
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTaskContent(linkFile);
        try {
            long start = System.currentTimeMillis();
            System.out.println("连接对象池话费时间：" + ((System.currentTimeMillis() - start) / 1000) + "s");
            long startTime = System.currentTimeMillis();
            System.out.println(JSON.toJSONString(new Converter().convertAndUploadDocLinkFile2PDF(openOfficeConnectionPool,
                    taskEntity, null)));
            System.out.println("转换AND上传成功" + ((System.currentTimeMillis() - startTime) / 1000) + "s");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        for (int i = 0; i < 10; i++) {
            long begin = System.currentTimeMillis();
            try {
                openOfficeConnectionPool.returnObject(openOfficeConnectionPool.borrowObject());
            } catch (Exception e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("获取连接池对象花费时间：" + ((end - begin) / 1000) + "s");
        }
    }
}
