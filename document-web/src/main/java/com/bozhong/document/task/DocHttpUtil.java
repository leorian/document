package com.bozhong.document.task;

import com.alibaba.fastjson.JSON;
import com.bozhong.config.common.ConfigSetPropertyHolder;
import com.bozhong.document.common.DocumentLogger;
import com.bozhong.document.entity.AppDO;
import com.bozhong.document.entity.TaskEntity;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;


/**
 * Created by xiezhonggui on 2017/5/4.
 */
public class DocHttpUtil {

    /**
     * 文档异步转换成功之后回调接口
     *
     * @param callBackUrl
     * @param taskEntity
     */
    public static void docConvertCallBack(String callBackUrl, TaskEntity taskEntity) {
        HttpClient httpClient = new HttpClient(new HttpClientParams(),
                new SimpleHttpConnectionManager(true));
        PostMethod postMethod = new PostMethod(callBackUrl);
        Class tClass = taskEntity.getClass();
        Field[] fields = tClass.getDeclaredFields();
        NameValuePair[] nameValuePairs = new NameValuePair[fields.length];
        int i = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                nameValuePairs[i++] = new NameValuePair(field.getName(), String.valueOf(field.get(taskEntity)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        postMethod.setRequestBody(nameValuePairs);

        try {
            httpClient.executeMethod(postMethod);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String response = new String(IOUtils.toByteArray(postMethod.getResponseBodyAsStream()), "utf-8");
            //String response = new String(postMethod.getResponseBodyAsString().getBytes("ISO8859-1"));
            DocumentLogger.getSysLogger().warn("回调结果" + response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        postMethod.releaseConnection();
    }

    /**
     * 从配置中心获取用户的应用ID
     *
     * @param uId
     * @return
     */
    public static List<AppDO> getAppDOList(String uId) throws IOException {
        HttpClient client = new HttpClient(new HttpClientParams(),
                new SimpleHttpConnectionManager(true));
        PostMethod method = new PostMethod(ConfigSetPropertyHolder.CONFIG_CENTER_URL
                + "/config/configSet/loadAppDOList");
        NameValuePair appKeyNVP = new NameValuePair("appKey", ConfigSetPropertyHolder.CONFIG_CENTER_ACCESS_KEY);
        NameValuePair uIdNVP = new NameValuePair("uId", uId);
        method.setRequestBody(new NameValuePair[]{appKeyNVP, uIdNVP});
        try {
            client.executeMethod(method);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String response = new String(IOUtils.toByteArray(method.getResponseBodyAsStream()), "utf-8");
//        String response = new String(method.getResponseBodyAsString().
//                getBytes("ISO8859-1"), "utf-8");

        return JSON.parseArray(response, AppDO.class);
    }
}
