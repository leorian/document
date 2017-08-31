package com.bozhong.document.restful;

import com.alibaba.fastjson.JSON;
import com.bozhong.common.util.ResultMessageBuilder;
import com.bozhong.common.util.StringUtil;
import com.bozhong.config.common.ConfigSetPropertyHolder;
import com.bozhong.document.common.*;
import com.bozhong.document.dto.TaskContentAsyncResDto;
import com.bozhong.document.dto.TaskContentSyncResDto;
import com.bozhong.document.entity.TaskEntity;
import com.bozhong.document.pool.OpenOfficeConnectionPool;
import com.bozhong.document.service.MongoService;
import com.bozhong.document.service.WorkFlowTraceService;
import com.bozhong.document.task.Converter;
import com.bozhong.document.task.DocumentMongoDBWorkQueue;
import com.bozhong.document.task.RedisUtil;
import com.bozhong.document.task.ZkUtil;
import com.bozhong.document.util.DocumentException;
import com.bozhong.myswitch.common.SwitchUtil;
import com.qiniu.storage.model.DefaultPutRet;
import com.sun.jersey.spi.resource.Singleton;
import com.yx.eweb.main.EWebServletContext;
import com.zhicall.core.util.MD5;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.JedisCluster;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by xiezg@317hu.com on 2017/4/27 0027.
 * 操作菜单数据接口
 */
@Controller
@Singleton
@Path("docOperation")
public class DocOperationRest {

    @Autowired
    private DocumentMongoDBWorkQueue documentMongoDBWorkQueue;

    @Autowired
    private WorkFlowTraceService workFlowTraceService;

    @Autowired
    private OpenOfficeConnectionPool openOfficeConnectionPool;

    @Autowired
    private MongoService mongoService;

    /**
     * 异步转换请求
     * <p>
     * 请求参数：「
     * appId:"应用ID"
     * docFileUrl:"文件链接地址"
     * callBackUrl(非必填项):"回调地址"
     * 」
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("doc2pdfAsync")
    public String doc2pdfAsync(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        String appId = (String) EWebServletContext.getEWebContext().get("appId");
        String docFileUrl = (String) EWebServletContext.getEWebContext().get("docFileUrl");
        String callBackUrl = (String) EWebServletContext.getEWebContext().get("callBackUrl");
        if (StringUtil.isBlank(appId) || StringUtil.isBlank(docFileUrl)) {
            //参数为定义
            throw new DocumentException(DocumentErrorEnum.E10001.getError(), DocumentErrorEnum.E10001.getMsg());
        }

        //同一文档转换请求频繁性控制
        if (WebSettingParam.CONVERSION_SAME_REQUEST_CONTROLLER) {
            sameDocumentConversionRequestController(docFileUrl);
        }

        //todo appId合法性验证未加，后期优化

        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTaskId(UUID.randomUUID().toString());
        taskEntity.setAppId(appId);//设置应用Id
        taskEntity.setTaskContent(docFileUrl);
        taskEntity.setCallBackUrl(callBackUrl);


        try {
            documentMongoDBWorkQueue.execute(taskEntity);
        } catch (Throwable e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
            //工作线程执行异常
            throw new DocumentException(DocumentErrorEnum.E10003.getError(), DocumentErrorEnum.E10003.getMsg());
        }

        //返回值
        TaskContentAsyncResDto resDto = new TaskContentAsyncResDto();
        BeanUtils.copyProperties(taskEntity, resDto);
        return ResultMessageBuilder.build(resDto).toJSONString();
    }

    /**
     * 异步转换请求(正在执行中的任务重新执行)管理端调用
     * <p>
     * 请求参数：「
     * taskId:"任务Id"
     * 」
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("doc2pdfReAsync")
    public String doc2pdfReAsync(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        String taskId = (String) EWebServletContext.getEWebContext().get("taskId");
        if (StringUtil.isBlank(taskId)) {
            //参数为定义
            throw new DocumentException(DocumentErrorEnum.E10001.getError(), DocumentErrorEnum.E10001.getMsg());
        }

        TaskEntity taskEntity = mongoService.findOneByTaskId(taskId, TaskEntity.class);
        if (taskEntity == null || (!TaskStatusEnum.EXECUTING.getName().equals(taskEntity.getTaskStatus()) &&
                !TaskStatusEnum.FAILURE.getName().equals(taskEntity.getTaskStatus()))) {
            //未查询到相关数据
            throw new DocumentException(DocumentErrorEnum.E10005.getError(), DocumentErrorEnum.E10005.getMsg());
        }

        try {
            taskEntity.setReAction(true);//重新执行
            documentMongoDBWorkQueue.execute(taskEntity);
        } catch (Throwable e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
            //工作线程执行异常
            throw new DocumentException(DocumentErrorEnum.E10003.getError(), DocumentErrorEnum.E10003.getMsg());
        }

        return ResultMessageBuilder.build(taskEntity).toJSONString();
    }

    /**
     * 异步转换请求批量接口
     * 请求参数：「
     * appId:应用ID
     * docFileUrlList:文件在线链接地址，JSON字符串数组字符串
     * callBackUrl（非必填项）: 回调地址
     * 」
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("doc2pdfBatchAsync")
    public String doc2pdfBatchAsync(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        String appId = (String) EWebServletContext.getEWebContext().get("appId");
        String docFileUrlList = (String) EWebServletContext.getEWebContext().get("docFileUrlList");
        String callBackUrl = (String) EWebServletContext.getEWebContext().get("callBackUrl");
        if (StringUtil.isBlank(appId) || StringUtil.isBlank(docFileUrlList)) {
            //参数未定义
            throw new DocumentException(DocumentErrorEnum.E10001.getError(), DocumentErrorEnum.E10001.getMsg());
        }

        //同一文档转换请求频繁性控制
        if (WebSettingParam.CONVERSION_SAME_REQUEST_CONTROLLER) {
            sameDocumentConversionRequestController(docFileUrlList);
        }

        List<String> docFileUrlArray = null;
        try {
            docFileUrlArray = JSON.parseArray(docFileUrlList, String.class);
        } catch (Throwable e) {
            //参数未定义
            throw new DocumentException(DocumentErrorEnum.E10001.getError(), DocumentErrorEnum.E10001.getMsg());
        }

        if (CollectionUtils.isEmpty(docFileUrlArray)) {
            //参数未定义
            throw new DocumentException(DocumentErrorEnum.E10001.getError(), DocumentErrorEnum.E10001.getMsg());
        }

        List<TaskEntity> taskEntities = new ArrayList<TaskEntity>(docFileUrlArray.size());
        for (String docFileUrl : docFileUrlArray) {
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setTaskId(UUID.randomUUID().toString());
            taskEntity.setAppId(appId);//设置应用ID
            taskEntity.setTaskContent(docFileUrl);
            taskEntity.setCallBackUrl(callBackUrl);
            taskEntities.add(taskEntity);
        }

        List<TaskContentAsyncResDto> resDtoList = new ArrayList<TaskContentAsyncResDto>(docFileUrlArray.size());
        for (TaskEntity taskEntity : taskEntities) {
            try {
                documentMongoDBWorkQueue.execute(taskEntity);
                TaskContentAsyncResDto resDto = new TaskContentAsyncResDto();
                BeanUtils.copyProperties(taskEntity, resDto);
                resDtoList.add(resDto);
            } catch (Throwable e) {
                DocumentLogger.getSysLogger().error(e.getMessage(), e);
                e.printStackTrace();
                //工作线程执行异常
                throw new DocumentException(DocumentErrorEnum.E10003.getError(), DocumentErrorEnum.E10003.getMsg());
            }
        }

        return ResultMessageBuilder.build(resDtoList).toJSONString();
    }

    /**
     * 同步转换请求
     * 请求参数：「
     * appId:"应用ID"
     * docFileUrl:"文件链接地址"
     * 」
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("doc2pdfSync")
    public String doc2pdfSync(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        String appId = (String) EWebServletContext.getEWebContext().get("appId");
        String docFileUrl = (String) EWebServletContext.getEWebContext().get("docFileUrl");
        if (StringUtil.isBlank(appId) || StringUtil.isBlank(docFileUrl)) {
            //参数为定义
            throw new DocumentException(DocumentErrorEnum.E10001.getError(), DocumentErrorEnum.E10001.getMsg());
        }

        //判断是否开启同步转换了
        if (!WebSettingParam.OPEN_SYNC_CONVERSION) {
            throw new DocumentException(DocumentErrorEnum.E10023.getError(), DocumentErrorEnum.E10023.getMsg());
        }

        //文档转换执行总数控制
        List list = ZkUtil.getAllExecutingRecordWithIP(SwitchUtil.getIp());
        if (!CollectionUtils.isEmpty(list) && list.size() > WebSettingParam.MAX_EXECUTING_STATUS_COUNT) {
            throw new DocumentException(DocumentErrorEnum.E10025.getError(), DocumentErrorEnum.E10025.getMsg());
        }

        //同一文档转换请求频繁性控制
        if (WebSettingParam.CONVERSION_SAME_REQUEST_CONTROLLER) {
            sameDocumentConversionRequestController(docFileUrl);
        }

        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTaskId(UUID.randomUUID().toString());
        taskEntity.setAppId(appId);//设置应用ID
        taskEntity.setTaskContent(docFileUrl);

        try {
            workFlowTraceService.waiting(taskEntity);
        } catch (Throwable e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
            //数据库操作异常
            throw new DocumentException(DocumentErrorEnum.E10002.getError(), DocumentErrorEnum.E10002.getMsg());
        }

        try {
            //是否开启了文档转换服务
//            if (!WebSettingParam.OPEN_CONVERSION) {
//                throw new DocumentException(DocumentErrorEnum.E10023.getError(), DocumentErrorEnum.E10023.getMsg());
//            }

            workFlowTraceService.executing(taskEntity);
            DefaultPutRet defaultPutRet = new Converter().convertAndUploadDocLinkFile2PDF(openOfficeConnectionPool,
                    taskEntity, workFlowTraceService);
            taskEntity.setTaskResult(ConfigSetPropertyHolder.getProperty("qiniu_domain") + defaultPutRet.key);
            workFlowTraceService.success(taskEntity);
        } catch (Throwable e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
            if (e instanceof DocumentException) {
                DocumentException exception = (DocumentException) e;
                taskEntity.setErrorCode(exception.getErrorCode());
                taskEntity.setErrorMessage(exception.getErrorMessage());
            } else {
                taskEntity.setErrorCode(e.getClass().getName());
                taskEntity.setErrorMessage(e.getLocalizedMessage());
            }

            workFlowTraceService.failure(taskEntity);

            if (e instanceof DocumentException) {
                throw new DocumentException(taskEntity.getErrorCode(), taskEntity.getErrorMessage());
            }

            //文件转换异常
            throw new DocumentException(DocumentErrorEnum.E10004.getError(), DocumentErrorEnum.E10004.getMsg());
        } finally {

        }

        TaskContentSyncResDto resDto = new TaskContentSyncResDto();
        BeanUtils.copyProperties(taskEntity, resDto);
        return ResultMessageBuilder.build(resDto).toJSONString();
    }

    /**
     * 异步转换请求执行结果状态查询接口
     * 请求参数：{
     * taskId:"任务ID"，
     * docFileUrl:"带转换文件链接地址"
     * }
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("doc2pdfTask")
    public String doc2pdfTask(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        String taskId = (String) EWebServletContext.getEWebContext().get("taskId");
        String docFileUrl = (String) EWebServletContext.getEWebContext().get("docFileUrl");
        if (StringUtil.isBlank(taskId) && StringUtil.isBlank(docFileUrl)) {
            //参数为定义
            throw new DocumentException(DocumentErrorEnum.E10001.getError(), DocumentErrorEnum.E10001.getMsg());
        }
        if (StringUtil.isNotBlank(taskId)) {
            try {
                TaskEntity taskEntity = mongoService.findOneByTaskId(taskId, TaskEntity.class);
                if (taskEntity != null) {
                    return ResultMessageBuilder.build(taskEntity).toJSONString();
                }
            } catch (Throwable e) {
                DocumentLogger.getSysLogger().error(e.getMessage(), e);
                //数据库操作异常
                throw new DocumentException(DocumentErrorEnum.E10002.getError(), DocumentErrorEnum.E10002.getMsg());
            }
        }

        if (StringUtil.isNotBlank(docFileUrl)) {
            try {
                TaskEntity taskEntity = mongoService.findOneByTaskContent(docFileUrl, TaskEntity.class);
                if (taskEntity != null) {
                    return ResultMessageBuilder.build(taskEntity).toJSONString();
                }
            } catch (Throwable e) {
                DocumentLogger.getSysLogger().error(e.getMessage(), e);
                //数据库操作异常
                throw new DocumentException(DocumentErrorEnum.E10002.getError(), DocumentErrorEnum.E10002.getMsg());
            }
        }

        //未查询到相关数据
        throw new DocumentException(DocumentErrorEnum.E10005.getError(), DocumentErrorEnum.E10005.getMsg());
    }

    private void sameDocumentConversionRequestController(String docFileUrl) {
        String docFileURLMD5 = DocumentConstants.DOC_FILE_URL_PREFIX + MD5.sign(docFileUrl);
        JedisCluster jedisCluster = RedisUtil.myRedisClusterForHessian.getJedisCluster();
        synchronized (DocOperationRest.class) {
            if (jedisCluster.exists(docFileURLMD5)) {
                throw new DocumentException(DocumentErrorEnum.E10024.getError(), DocumentErrorEnum.E10024.getMsg());
            } else {
                jedisCluster.setex(docFileURLMD5, WebSettingParam.CONVERSION_SAME_REQUEST_EXPIRE_TIME,
                        String.valueOf(System.currentTimeMillis()));
            }
        }
    }
}
