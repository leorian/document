package com.bozhong.document.restful;

import com.alibaba.fastjson.JSON;
import com.bozhong.document.common.DocumentLogger;
import com.bozhong.document.entity.TaskEntity;
import com.bozhong.document.pool.OpenOfficeConnectionPool;
import com.bozhong.document.task.Converter;
import com.sun.jersey.spi.resource.Singleton;
import com.yx.eweb.main.EWebServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

/**
 * Created by xiezg@317hu.com on 2017/4/26 0026.
 */
@Controller
@Singleton
@Path("testRest")
public class TestRest {

    @Autowired
    private OpenOfficeConnectionPool openOfficeConnectionPool;

    @POST
    @Path("testMethod")
    public String testMethod(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        String testParam = (String) EWebServletContext.getEWebContext().get("testParam");
        String linkFile = "http://okxyat5ou.bkt.clouddn.com/1.ppt";
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTaskContent(linkFile);
        try {
            System.out.println(JSON.toJSONString(new Converter().convertAndUploadDocLinkFile2PDF(openOfficeConnectionPool,
                    taskEntity, null)));
        } catch (Exception e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
        } finally {

        }
        return testParam;
    }
}
