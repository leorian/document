package com.bozhong.document.qiniu;

import com.bozhong.config.common.ConfigSetPropertyHolder;
import com.bozhong.document.common.DocFileTypeEnum;
import com.bozhong.document.common.DocumentErrorEnum;
import com.bozhong.document.common.DocumentLogger;
import com.bozhong.document.util.DocumentException;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

import java.io.*;

/**
 * Created by xiezg@317hu.com on 2017/4/26 0026.
 */
public class QiniuUtil {
    public static final String ACCESS_KEY = ConfigSetPropertyHolder.getProperty("qiniu_access_key");
    public static final String SECRET_KEY = ConfigSetPropertyHolder.getProperty("qiniu_secret_key");
    public static final String BUCKET = ConfigSetPropertyHolder.getProperty("qiniu_bucket");
    public static final Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);

    /**
     * 上传本地文件
     *
     * @param localFilePath 本地文件路径
     * @return
     */
    public static DefaultPutRet upload(String localFilePath, String key) {
        return upload(new File(localFilePath), key);
    }

    /**
     * 上传本地文件
     *
     * @param localFile 本地文件对象
     * @param key
     * @return
     */
    public static DefaultPutRet upload(File localFile, String key) {
        UploadManager uploadManager = new UploadManager();
        String upToken = auth.uploadToken(BUCKET);
        DefaultPutRet putRet = null;
        try {
            Response response = uploadManager.put((File) localFile, key, upToken);
            putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        } catch (QiniuException ex) {
            DocumentLogger.getSysLogger().error(ex.getMessage());
            //七牛上传失败
            throw new DocumentException(DocumentErrorEnum.E10011.getError(),
                    DocumentErrorEnum.E10011.getMsg());
        }

        return putRet;
    }

    /**
     * 上传文件输出流
     *
     * @param byteArrayOutputStream
     * @param key
     * @return
     */
    public static DefaultPutRet upload(ByteArrayOutputStream byteArrayOutputStream, String key) {
        return upload(byteArrayOutputStream.toByteArray(), key);
    }


    /**
     * 上传字节数组
     *
     * @param uploadBytes
     * @param key
     * @return
     */
    public static DefaultPutRet upload(byte[] uploadBytes, String key) {
        UploadManager uploadManager = new UploadManager();
        String upToken = auth.uploadToken(BUCKET);
        DefaultPutRet putRet = null;
        try {
            Response response = uploadManager.put((byte[]) uploadBytes, key, upToken);
            putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        } catch (QiniuException ex) {
            DocumentLogger.getSysLogger().error(ex.getMessage());
            //七牛上传失败
            throw new DocumentException(DocumentErrorEnum.E10011.getError(),
                    DocumentErrorEnum.E10011.getMsg());
        }

        return putRet;
    }

    /**
     * 获取文件信息
     *
     * @param key
     * @return
     */
    public static FileInfo getFileInfo(String key) {
        BucketManager bucketManager = new BucketManager(auth);
        FileInfo fileInfo = null;
        try {
            fileInfo = bucketManager.stat(BUCKET, key);
        } catch (QiniuException ex) {
            DocumentLogger.getSysLogger().error(ex.getMessage());
            //七牛获取文件信息异常
            throw new DocumentException(DocumentErrorEnum.E10015.getError(),
                    DocumentErrorEnum.E10015.getMsg());
        }

        return fileInfo;
    }


    /**
     * 获取文件链接地址指向的文件具体信息
     *
     * @param linkFile
     * @return
     */
    public String getLinkFileExt(String linkFile) {
        HttpClient httpClient = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
        HttpMethod httpMethod = new GetMethod(linkFile);
        try {
            httpClient.executeMethod(httpMethod);
            return DocFileTypeEnum.contentTypeExtMap.get(httpMethod.getResponseHeader("Content-Type").
                    getValue());
        } catch (IOException e) {
            //网络连接异常
            throw new DocumentException(DocumentErrorEnum.E10016.getError(),
                    DocumentErrorEnum.E10016.getMsg());
        } finally {
            httpMethod.releaseConnection();
        }
    }


}
