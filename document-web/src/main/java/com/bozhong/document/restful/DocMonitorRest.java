package com.bozhong.document.restful;

import com.alibaba.fastjson.JSON;
import com.bozhong.common.util.CollectionUtil;
import com.bozhong.common.util.ResultMessageBuilder;
import com.bozhong.common.util.StringUtil;
import com.bozhong.config.domain.JqPage;
import com.bozhong.document.common.DocumentErrorEnum;
import com.bozhong.document.common.DocumentLogger;
import com.bozhong.document.common.TaskStatusEnum;
import com.bozhong.document.entity.*;
import com.bozhong.document.service.MongoService;
import com.bozhong.document.task.Converter;
import com.bozhong.document.task.ZkUtil;
import com.bozhong.document.util.DocumentException;
import com.google.gson.Gson;
import com.sun.jersey.spi.resource.Singleton;
import com.yx.eweb.main.EWebRequestDTO;
import com.yx.eweb.main.EWebServletContext;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import java.util.*;

/**
 * Created by xiezg@317hu.com on 2017/4/28 0028.
 * 监控菜单数据接口
 */
@Controller
@Singleton
@Path("docMonitor")
public class DocMonitorRest {

    @Autowired
    private MongoService mongoService;

    /**
     * 在线的文档转换服务器
     *
     * @return
     */
    @POST
    @Path("onlineDocumentServer")
    public String onlineDocumentServer(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        try {
            List<String> onlineDocumentServerList = ZkUtil.getOnlineDocServerList();
            if (!CollectionUtils.isEmpty(onlineDocumentServerList)) {
                List<OsEntity> osEntityList = new ArrayList<>();
                int index = 0;
                for (String ip : onlineDocumentServerList) {
                    OsEntity osEntity = ZkUtil.getOnlineDocServerInfo(ip);
                    if (osEntity == null) {
                        continue;
                    }

                    osEntity.setNumber((index++) + "");
                    osEntityList.add(osEntity);
                }
                return ResultMessageBuilder.build(osEntityList).toJSONString();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return ResultMessageBuilder.build(false, "", "").toJSONString();
    }

    /**
     * 机器正在执行任务列表
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("executingIpZkTaskIds")
    public String executingIpZkTaskIds(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {

        try {
            List<String> ipList = ZkUtil.getALLExecutingRecordIPList();
            if (!CollectionUtil.isEmpty(ipList)) {
                List<ZkTaskRecordEntity> zkTaskRecordEntityList = new ArrayList<>();
                int index = 0;
                for (String ip : ipList) {
                    ZkTaskRecordEntity zkTaskRecordEntity = new ZkTaskRecordEntity();
                    zkTaskRecordEntity.setNumber((index++) + "");
                    zkTaskRecordEntity.setIp(ip);
                    List<String> taskIds = ZkUtil.getAllExecutingRecordWithIP(ip);
                    if (CollectionUtil.isEmpty(taskIds)) {
                        zkTaskRecordEntity.setCount(0 + "");
                        zkTaskRecordEntity.setExecutingTaskIds("");
                    } else {
                        zkTaskRecordEntity.setCount(taskIds.size() + "");
                        zkTaskRecordEntity.setExecutingTaskIds(StringUtil.join(taskIds.iterator(), "<br/>"));
                    }
                    zkTaskRecordEntityList.add(zkTaskRecordEntity);
                }

                return ResultMessageBuilder.build(zkTaskRecordEntityList).toJSONString();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return ResultMessageBuilder.build(false, "", "").toJSONString();

    }

    /**
     * 获取文档转换任务详情
     * 请求参数：{
     * taskId:"任务ID"
     * }
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("findTaskByTaskId")
    public String findTaskByTaskId(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        Map<String, Object> param = ((EWebRequestDTO) EWebServletContext.getEWebContext().getParam()).getRequestParam();
        String taskId = (String) param.get("taskId");
        TaskEntity taskEntity = mongoService.findOneByTaskId(taskId, TaskEntity.class);
        taskEntityConvertTimeBuilder(taskEntity);
        return ResultMessageBuilder.build(taskEntity).toJSONString();
    }

    /**
     * 统计各个应用请求饼图占比数
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("appRequestCountPieData")
    public String appRequestCountPieData(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        Map<String, Object> param = ((EWebRequestDTO) EWebServletContext.getEWebContext().getParam()).getRequestParam();
        String taskStatus = (String) param.get("taskStatus");
        Map<String, Integer> appIdCountMap = mongoService.groupByAppId(TaskEntity.class, taskStatus);
        Map<String, Integer> appNameCountMap = new HashMap<>();
        List<AppDO> appDOList = (List<AppDO>) EWebServletContext.getRequest().getAttribute("appDOList");
        if (!CollectionUtils.isEmpty(appDOList)) {
            for (AppDO appDO : appDOList) {
                appNameCountMap.put(appDO.getAppName(), appIdCountMap.get(appDO.getAppId()) == null ? 0 :
                        appIdCountMap.get(appDO.getAppId()));
            }
        }

        return ResultMessageBuilder.build(appNameCountMap).toJSONString();
    }

    /**
     * 图表展示统计
     * <p>
     * 请求参数：{
     * appId:"应用ID（非必填项）"
     * }
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("taskStatusCount")
    public String taskStatusCount(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        Map<String, Object> param = ((EWebRequestDTO) EWebServletContext.getEWebContext().getParam()).getRequestParam();
        String appId = (String) param.get("appId");
        CountEntity countEntity = new CountEntity();
        try {
            countEntity.setAllCount(mongoService.findCountByAppId(TaskEntity.class, appId));
            countEntity.setWaitingCount(mongoService.findCountByTaskStatusAndAppId(TaskStatusEnum.WAITING.getName(),
                    TaskEntity.class, appId));
            countEntity.setExecutingCount(mongoService.findCountByTaskStatusAndAppId(TaskStatusEnum.EXECUTING.getName(),
                    TaskEntity.class, appId));
            countEntity.setSuccessCount(mongoService.findCountByTaskStatusAndAppId(TaskStatusEnum.SUCCESS.getName(),
                    TaskEntity.class, appId));
            countEntity.setFailureCount(mongoService.findCountByTaskStatusAndAppId(TaskStatusEnum.FAILURE.getName(),
                    TaskEntity.class, appId));
        } catch (Throwable e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
            //统计查询异常
            throw new DocumentException(DocumentErrorEnum.E10014.getError(),
                    DocumentErrorEnum.E10014.getMsg());
        }

        return ResultMessageBuilder.build(countEntity).toJSONString();
    }

    /**
     * 授权应用饼图图表展示统计
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("authTaskStatusCount")
    public String authTaskStatusCount(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        List<AppDO> appDOList = (List<AppDO>) EWebServletContext.getRequest().getAttribute("appDOList");
        CountEntity countEntity = new CountEntity();
        if (!CollectionUtils.isEmpty(appDOList)) {
            String[] appIds = new String[appDOList.size()];
            int i = 0;
            for (AppDO appDO : appDOList) {
                appIds[i++] = appDO.getAppId();
            }

            try {
                countEntity.setAllCount(mongoService.findAuthCount(TaskEntity.class, appIds));
                countEntity.setWaitingCount(mongoService.findAuthCountByTaskStatus(TaskEntity.class, appIds,
                        TaskStatusEnum.WAITING.getName()));
                countEntity.setExecutingCount(mongoService.findAuthCountByTaskStatus(TaskEntity.class, appIds,
                        TaskStatusEnum.EXECUTING.getName()));
                countEntity.setSuccessCount(mongoService.findAuthCountByTaskStatus(TaskEntity.class, appIds,
                        TaskStatusEnum.SUCCESS.getName()));
                countEntity.setFailureCount(mongoService.findAuthCountByTaskStatus(TaskEntity.class, appIds,
                        TaskStatusEnum.FAILURE.getName()));
            } catch (Throwable e) {
                DocumentLogger.getSysLogger().error(e.getMessage(), e);
                //统计查询异常
                throw new DocumentException(DocumentErrorEnum.E10014.getError(),
                        DocumentErrorEnum.E10014.getMsg());
            }
        }

        return ResultMessageBuilder.build(countEntity).toJSONString();
    }

    /**
     * 列表展示统计
     * 请求参数：{
     * appId : "应用ID"
     * }
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("taskStatusList")
    public String taskStatusList(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        Map<String, Object> param = ((EWebRequestDTO) EWebServletContext.getEWebContext().getParam()).getRequestParam();
        String callBack = param.get("callback").toString();
        Integer page = Integer.valueOf(param.get("page").toString());
        Integer rows = Integer.valueOf(param.get("rows").toString());
        String taskStatus = (String) param.get("taskStatus");
        String appId = (String) param.get("appId");
        Gson gson = new Gson();
        JqPage<TaskEntity> jqPage = new JqPage<TaskEntity>();
        jqPage.setPage(page);
        jqPage.setPageSize(rows);
        if (StringUtil.isNotBlank(taskStatus)) {
            jqPage = mongoService.getJqPageByAppId(taskStatus, jqPage, TaskEntity.class, appId);
        } else {
            jqPage = mongoService.getJqPageByAppId(jqPage, TaskEntity.class, appId);
        }
        List<TaskEntity> taskEntities = jqPage.getRows();
        if (!CollectionUtils.isEmpty(taskEntities)) {
            for (TaskEntity taskEntity : taskEntities) {
                taskEntityConvertTimeBuilder(taskEntity);
            }
        }

        return callBack + "(" + gson.toJson(jqPage) + ")";
    }

    /**
     * 模拟异步调用
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("simulateAsyncRequest")
    public String simulateAsyncRequest(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        String appId = (String) EWebServletContext.getEWebContext().get("appId");
        String docFileUrl = (String) EWebServletContext.getEWebContext().get("docFileUrl");
        String callNumber = (String) EWebServletContext.getEWebContext().get("callNumber");
        String callBackUrl = (String) EWebServletContext.getEWebContext().get("callBackUrl");
        final String requestUrl = "http://" + EWebServletContext.getRequest().getServerName()
                + ":" + EWebServletContext.getRequest().getServerPort()
                + EWebServletContext.getRequest().getContextPath()
                + "/document/docOperation/doc2pdfAsync";
        System.out.println(requestUrl);
        simulateRequest(requestUrl, appId, docFileUrl, callNumber, callBackUrl);
        return ResultMessageBuilder.build().toJSONString();
    }

    /**
     * 模拟同步调用
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("simulateSyncRequest")
    public String simulateSyncRequest(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        String appId = (String) EWebServletContext.getEWebContext().get("appId");
        String docFileUrl = (String) EWebServletContext.getEWebContext().get("docFileUrl");
        String callNumber = (String) EWebServletContext.getEWebContext().get("callNumber");
        final String requestUrl = "http://" + EWebServletContext.getRequest().getServerName()
                + ":" + EWebServletContext.getRequest().getServerPort()
                + EWebServletContext.getRequest().getContextPath()
                + "/document/docOperation/doc2pdfSync";
        System.out.println(requestUrl);
        simulateRequest(requestUrl, appId, docFileUrl, callNumber, null);
        return ResultMessageBuilder.build().toJSONString();
    }

    /**
     * 模拟批量异步调用
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("simulateBatchAsyncRequest")
    public String simulateBatchAsyncRequest(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        String appId = (String) EWebServletContext.getEWebContext().get("appId");
        String docFileUrlList = (String) EWebServletContext.getEWebContext().get("docFileUrlList");
        String callBackUrl = (String) EWebServletContext.getEWebContext().get("callBackUrl");
        final String requestUrl = "http://" + EWebServletContext.getRequest().getServerName()
                + ":" + EWebServletContext.getRequest().getServerPort()
                + EWebServletContext.getRequest().getContextPath()
                + "/document/docOperation/doc2pdfBatchAsync";
        System.out.println(requestUrl);
        simulateBatchRequest(requestUrl, appId, docFileUrlList, callBackUrl);
        return ResultMessageBuilder.build().toJSONString();
    }

    /**
     * 今日任务统计
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("todayTaskCount")
    public String todayTaskCount(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Long waitingCount = mongoService.findTodayCount(TaskEntity.class, TaskStatusEnum.WAITING.getName()
                , String.valueOf(calendar.getTimeInMillis()));
        Long executingCount = mongoService.findTodayCount(TaskEntity.class, TaskStatusEnum.EXECUTING.getName()
                , String.valueOf(calendar.getTimeInMillis()));
        Long successCount = mongoService.findTodayCount(TaskEntity.class, TaskStatusEnum.SUCCESS.getName()
                , String.valueOf(calendar.getTimeInMillis()));
        Long failureCount = mongoService.findTodayCount(TaskEntity.class, TaskStatusEnum.FAILURE.getName()
                , String.valueOf(calendar.getTimeInMillis()));
        Map<String, Long> map = new HashMap<>();
        map.put("waitingCount", waitingCount != null ? waitingCount : 0l);
        map.put("executingCount", executingCount != null ? executingCount : 0l);
        map.put("successCount", successCount != null ? successCount : 01);
        map.put("failureCount", failureCount != null ? failureCount : 0l);

        return ResultMessageBuilder.build(map).toJSONString();
    }

    /**
     * 今日任务统计
     *
     * @param request
     * @param uriInfo
     * @param httpHeaders
     * @return
     */
    @POST
    @Path("yesterdayTaskCount")
    public String yesterdayTaskCount(@Context Request request, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Long waitingCount = mongoService.findYesterdayCount(TaskEntity.class, TaskStatusEnum.WAITING.getName()
                , String.valueOf(calendar.getTimeInMillis() - 24 * 60 * 60 * 1000), String.valueOf(calendar.getTimeInMillis()));
        Long executingCount = mongoService.findYesterdayCount(TaskEntity.class, TaskStatusEnum.EXECUTING.getName()
                , String.valueOf(calendar.getTimeInMillis() - 24 * 60 * 60 * 1000), String.valueOf(calendar.getTimeInMillis()));
        Long successCount = mongoService.findYesterdayCount(TaskEntity.class, TaskStatusEnum.SUCCESS.getName()
                , String.valueOf(calendar.getTimeInMillis() - 24 * 60 * 60 * 1000), String.valueOf(calendar.getTimeInMillis()));
        Long failureCount = mongoService.findYesterdayCount(TaskEntity.class, TaskStatusEnum.FAILURE.getName()
                , String.valueOf(calendar.getTimeInMillis() - 24 * 60 * 60 * 1000), String.valueOf(calendar.getTimeInMillis()));
        Map<String, Long> map = new HashMap<>();
        map.put("waitingCount", waitingCount != null ? waitingCount : 0l);
        map.put("executingCount", executingCount != null ? executingCount : 0l);
        map.put("successCount", successCount != null ? successCount : 01);
        map.put("failureCount", failureCount != null ? failureCount : 0l);

        return ResultMessageBuilder.build(map).toJSONString();
    }

    /**
     * 模拟批量异步文档转换请求
     *
     * @param requestUrl
     * @param docFileUrlList
     */
    private void simulateBatchRequest(final String requestUrl, String appId, String docFileUrlList, String callBackUrl) {
        if (StringUtil.isBlank(appId) || StringUtil.isBlank(docFileUrlList)) {
            //参数未定义
            throw new DocumentException(DocumentErrorEnum.E10001.getError(),
                    DocumentErrorEnum.E10001.getMsg());
        }

        final String cookies = EWebServletContext.getRequest().getHeader("Cookie");
        final String appIdStr = appId;
        final String docFileUrlStr = docFileUrlList;
        final String callBackUrlStr = callBackUrl;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpClient = new HttpClient(new HttpClientParams(),
                        new SimpleHttpConnectionManager(true));
                PostMethod httpMethod = new PostMethod(requestUrl);
                NameValuePair appIdParam = new NameValuePair("appId", appIdStr);
                NameValuePair docFileUrlParam = new NameValuePair("docFileUrlList", docFileUrlStr);
                NameValuePair callBackUrlParam = new NameValuePair("callBackUrl", callBackUrlStr);
                httpMethod.setRequestBody(new NameValuePair[]{appIdParam, docFileUrlParam, callBackUrlParam});
                httpMethod.setRequestHeader("Cookie", cookies);
                try {
                    httpClient.executeMethod(httpMethod);
                    String response = new String(IOUtils.toByteArray(httpMethod.getResponseBodyAsStream()), "utf-8");
                    DocumentLogger.getSysLogger().warn(response);
//                    DocumentLogger.getSysLogger().warn(new String(httpMethod.getResponseBodyAsString().
//                            getBytes("ISO8859-1")));
                } catch (Throwable e) {
                    DocumentLogger.getSysLogger().error(e.getMessage(), e);
                }
                httpMethod.releaseConnection();
            }
        });

        thread.start();
    }


    /**
     * 模拟同步或者异步文档转换请求
     *
     * @param appId
     * @param requestUrl
     * @param docFileUrl
     * @param callNumber
     */
    private void simulateRequest(final String requestUrl, String appId, String docFileUrl, String callNumber, String callBackUrl) {

        if (StringUtil.isBlank(appId) || StringUtil.isBlank(docFileUrl)) {
            //参数为定义
            throw new DocumentException(DocumentErrorEnum.E10001.getError(),
                    DocumentErrorEnum.E10001.getMsg());
        }

        try {
            Integer.valueOf(callNumber);
        } catch (Throwable e) {
            //参数未定义
            throw new DocumentException(DocumentErrorEnum.E10001.getError(),
                    DocumentErrorEnum.E10001.getMsg());
        }

        final String cookies = EWebServletContext.getRequest().getHeader("Cookie");
        final String docFileUrlStr = docFileUrl;
        final String appIdStr = appId;
        final int callNumberInt = Integer.valueOf(callNumber);
        final String callBackUrlStr = callBackUrl == null ? "" : callBackUrl;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpClient = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
                PostMethod httpMethod = new PostMethod(requestUrl);
                NameValuePair appIdParam = new NameValuePair("appId", appIdStr);
                NameValuePair docFileUrlParam = new NameValuePair("docFileUrl", docFileUrlStr);
                NameValuePair callBackUrlParam = new NameValuePair("callBackUrl", callBackUrlStr);
                httpMethod.setRequestBody(new NameValuePair[]{appIdParam, docFileUrlParam, callBackUrlParam});
                httpMethod.setRequestHeader("Cookie", cookies);
                for (int i = 0; i < callNumberInt; i++) {
                    try {
                        httpClient.executeMethod(httpMethod);
//                        String response = new String(httpMethod.getResponseBodyAsString().
//                                getBytes("ISO8859-1"));
                        String response = new String(IOUtils.toByteArray(httpMethod.getResponseBodyAsStream()), "utf-8");
                        System.out.println(response);
                        DocumentLogger.getSysLogger().warn(response);
                    } catch (Throwable e) {
                        DocumentLogger.getSysLogger().error(e.getMessage(), e);
                    }
                }
                httpMethod.releaseConnection();
            }
        });

        thread.start();
    }

    /**
     * 转换时长设定
     *
     * @param taskEntity
     */
    private void taskEntityConvertTimeBuilder(TaskEntity taskEntity) {
        if (TaskStatusEnum.SUCCESS.getName().equals(taskEntity.getTaskStatus())
                || TaskStatusEnum.FAILURE.getName().equals(taskEntity.getTaskStatus())
                ) {
            try {
                long executing = Long.valueOf(taskEntity.getExecuteTimeStamp());
                long finish = Long.valueOf(taskEntity.getFinishTimeStamp());
                taskEntity.setConvertTime(Converter.formatTime(finish - executing));
            } catch (Throwable e) {

            }
        } else if (TaskStatusEnum.EXECUTING.getName().equals(taskEntity.getTaskStatus())) {
            try {
                long executing = Long.valueOf(taskEntity.getExecuteTimeStamp());
                long finish = Long.valueOf(new Date().getTime());
                taskEntity.setConvertTime(Converter.formatTime(finish - executing));
            } catch (Throwable e) {

            }
        } else if (TaskStatusEnum.WAITING.getName().equals(taskEntity.getTaskStatus())) {
            try {
                long executing = Long.valueOf(taskEntity.getCreateTimeStamp());
                long finish = Long.valueOf(new Date().getTime());
                taskEntity.setConvertTime(Converter.formatTime(finish - executing));
            } catch (Throwable e) {

            }
        }
    }
}
