package com.bozhong.document.task;

import com.artofsolving.jodconverter.*;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;
import com.bozhong.common.util.StringUtil;

import java.io.*;

import com.bozhong.document.common.*;
import com.bozhong.document.entity.TaskEntity;
import com.bozhong.document.pool.OpenOfficeConnectionPool;
import com.bozhong.document.qiniu.QiniuUtil;
import com.bozhong.document.service.WorkFlowTraceService;
import com.bozhong.document.util.DocumentException;
import com.qiniu.storage.model.DefaultPutRet;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.io.IOUtils;

/**
 * Created by xiezg@317hu.com on 2017/4/26 0026.
 */
public class Converter {


    /**
     * 文件格式转换
     *
     * @param connection
     * @param sourceFile
     * @param targetFile
     */
    public static void convert(OpenOfficeConnection connection, File sourceFile, File targetFile) {
        DocumentConverter converter = new StreamOpenOfficeDocumentConverter(connection);
        converter.convert(sourceFile, targetFile);
    }

    /**
     * 文件流格式转换
     *
     * @param connection
     * @param inputStream
     * @param inputDocumentFormat
     * @param outputStream
     * @param outputDocumentFormat
     */
    public static void convert(OpenOfficeConnection connection,
                               InputStream inputStream, DocumentFormat inputDocumentFormat,
                               OutputStream outputStream, DocumentFormat outputDocumentFormat) {
        long start = System.currentTimeMillis();
        DocumentConverter converter = new StreamOpenOfficeDocumentConverter(connection);
        converter.convert(inputStream, inputDocumentFormat, outputStream, outputDocumentFormat);
        long end = System.currentTimeMillis();
        System.out.println("转换时间：" + ((end - start) / 1000) + "s");
    }

    /**
     * 获取文件扩展名
     *
     * @param path
     * @return
     */
    public static String getExtensionName(String path) {
        if (StringUtil.isBlank(path)) {
            return null;
        }

        if (path.endsWith(DocFileTypeEnum.DOC.getExt()) ||
                path.endsWith(DocFileTypeEnum.DOCX.getExt())) {
            return DocFileTypeEnum.DOC.getExt();
        } else if (path.endsWith(DocFileTypeEnum.PPT.getExt()) ||
                path.endsWith(DocFileTypeEnum.PPTX.getExt())) {
            return DocFileTypeEnum.PPT.getExt();
        } else if (path.endsWith(DocFileTypeEnum.XLS.getExt()) ||
                path.endsWith(DocFileTypeEnum.XLSX.getExt())) {
            return DocFileTypeEnum.XLS.getExt();
        } else if (path.endsWith(DocFileTypeEnum.PDF.getExt())) {
            return DocFileTypeEnum.PDF.getExt();
        }

        return null;
    }

    /**
     * 转换文件链接地址并上传七牛
     *
     * @param openOfficeConnectionPool
     * @param taskEntity
     * @return
     */
    public DefaultPutRet convertAndUploadDocLinkFile2PDF(OpenOfficeConnectionPool openOfficeConnectionPool,
                                                         TaskEntity taskEntity, WorkFlowTraceService workFlowTraceService) {
        long start = System.currentTimeMillis();
        HttpClient httpClient = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
        HttpMethod httpMethod = new GetMethod(taskEntity.getTaskContent());
        try {
            httpClient.executeMethod(httpMethod);
        } catch (IOException e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
            taskEntity.setErrorCode(DocumentErrorEnum.E10006.getError());
            taskEntity.setErrorMessage(DocumentErrorEnum.E10006.getMsg());
            httpMethod.releaseConnection();
            //文件链接地址不可达
            throw new DocumentException(DocumentErrorEnum.E10006.getError(), DocumentErrorEnum.E10006.getMsg());
        }

        if (httpMethod.getStatusCode() == 403) {
            taskEntity.setErrorCode(DocumentErrorEnum.E10019.getError());
            taskEntity.setErrorMessage(DocumentErrorEnum.E10019.getMsg());
            httpMethod.releaseConnection();
            //请求频繁，403被禁止
            throw new DocumentException(DocumentErrorEnum.E10019.getError(), DocumentErrorEnum.E10019.getMsg());
        }

        //设置文件格式
        String docExt = DocFileTypeEnum.contentTypeExtMap.get(httpMethod.
                getResponseHeader("Content-Type").getValue());
        if (StringUtil.isBlank(docExt) || docExt.equals(DocFileTypeEnum.PDF.getExt())) {
            taskEntity.setTaskContentExt(httpMethod.
                    getResponseHeader("Content-Type").getValue());
            taskEntity.setErrorCode(DocumentErrorEnum.E10007.getError());
            taskEntity.setErrorMessage(DocumentErrorEnum.E10007.getMsg());
            httpMethod.releaseConnection();
            //文件格式不支持
            throw new DocumentException(DocumentErrorEnum.E10007.getError(), DocumentErrorEnum.E10007.getMsg());
        } else {
            taskEntity.setTaskContentExt(docExt);//设置源文件格式
        }

        //设置源文件大小
        double docLength = 0;
        try {
            docLength = Double.valueOf(httpMethod.getResponseHeader("Content-Length").getValue());
        } catch (Throwable e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
            taskEntity.setErrorCode(DocumentErrorEnum.E10008.getError());
            taskEntity.setErrorMessage(DocumentErrorEnum.E10008.getMsg());
            httpMethod.releaseConnection();
            //文件大小未知
            throw new DocumentException(DocumentErrorEnum.E10008.getError(),
                    DocumentErrorEnum.E10008.getMsg());
        }

        if (docLength / 1024 / 1024 >= 1) {
            taskEntity.setTaskContentLength(String.format("%.2f", (docLength / 1024 / 1024)) + "MB");
        } else {
            taskEntity.setTaskContentLength(String.format("%.2f", (docLength / 1024)) + "KB");
        }

        //待转换文档大小超过限制
        if (docLength / 1024 / 1024 >= WebSettingParam.MAX_DOC_FILE_LENGTH) {
            throw new DocumentException(DocumentErrorEnum.E10026.getError(), DocumentErrorEnum.E10026.getMsg() +
                    WebSettingParam.MAX_DOC_FILE_LENGTH + "M");
        }

        try {
            if (workFlowTraceService != null) {
                workFlowTraceService.executing(taskEntity);
            }
        } catch (Throwable e) {

            DocumentLogger.getSysLogger().error(e.getMessage(), e);
            //数据库操作异常
            taskEntity.setErrorCode(DocumentErrorEnum.E10002.getError());
            taskEntity.setErrorMessage(DocumentErrorEnum.E10002.getMsg());
            httpMethod.releaseConnection();
            throw new DocumentException(DocumentErrorEnum.E10002.getError(), DocumentErrorEnum.E10002.getMsg());
        }


        InputStream inputStream = null;

        try {
            inputStream = httpMethod.getResponseBodyAsStream();
        } catch (IOException e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
            taskEntity.setErrorCode(DocumentErrorEnum.E10009.getError());
            taskEntity.setErrorMessage(DocumentErrorEnum.E10009.getMsg());
            httpMethod.releaseConnection();
            //文件内容获取不到
            throw new DocumentException(DocumentErrorEnum.E10009.getError(), DocumentErrorEnum.E10009.getMsg());
        }


        String parentPath = System.getProperty("openOffice.storage");
        if (!parentPath.endsWith(File.separator)) {
            parentPath += File.separator;
        }

        File inputFile = new File(parentPath + taskEntity.getTaskId() + "." + taskEntity.getTaskContentExt());
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(inputFile);
            IOUtils.copy(inputStream, fileOutputStream);
        } catch (Throwable e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
            taskEntity.setErrorCode(DocumentErrorEnum.E10021.getError());
            taskEntity.setErrorMessage(DocumentErrorEnum.E10021.getMsg());
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            if (inputFile.exists()) {
                try {
                    inputFile.delete();
                } catch (Throwable e2) {
                    e2.printStackTrace();
                }
            }
            httpMethod.releaseConnection();
            throw new DocumentException(DocumentErrorEnum.E10021.getError(), DocumentErrorEnum.E10021.getMsg());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            httpMethod.releaseConnection();
        }

        System.out.println("下载原始文件时间：" + (System.currentTimeMillis() - start) / 1000 + "s");
        taskEntity.setTaskResultExt(DocFileTypeEnum.PDF.getExt());
        File outFile = new File(parentPath + taskEntity.getTaskId() + "." + taskEntity.getTaskResultExt());
        OpenOfficeConnection openOfficeConnection = null;
        try {
            if (OSinfo.isWindows() && !ConverterToolEnum.OPEN_OFFICE.equals(WebSettingParam.CONVERTER_TOOLS)
                    ) {//windows office转换
                long cStart = System.currentTimeMillis();
                if (ConverterToolEnum.WPS.name().equals(WebSettingParam.CONVERTER_TOOLS)) {
                    WpsWord2PDF.wpsConvert2PDF(inputFile.getAbsolutePath(), outFile.getAbsolutePath());
                } else if (ConverterToolEnum.WIN_OFFICE.name().equals(WebSettingParam.CONVERTER_TOOLS)) {
                    OfficeWord2PDF.convert2PDF(inputFile.getAbsolutePath(), outFile.getAbsolutePath());
                }
                System.out.println("office转换时间：" + ((System.currentTimeMillis() - cStart) / 1000) + "s");
            } else { //open office 转换
                openOfficeConnection = openOfficeConnectionPool.borrowObject();
                long cStart = System.currentTimeMillis();
                convert(openOfficeConnection, inputFile, outFile);
                System.out.println("open office 转换时间：" + ((System.currentTimeMillis() - cStart) / 1000) + "s");

            }
        } catch (Throwable e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
            taskEntity.setErrorCode(DocumentErrorEnum.E10010.getError());
            taskEntity.setErrorMessage(DocumentErrorEnum.E10010.getMsg());
            if (inputFile.exists()) {
                inputFile.delete();
            }

            if (outFile.exists()) {
                outFile.delete();
            }

            throw new DocumentException(DocumentErrorEnum.E10010.getError(),
                    DocumentErrorEnum.E10010.getMsg());
        } finally {
            if (openOfficeConnection != null) {
                openOfficeConnectionPool.returnObject(openOfficeConnection);
            }
        }

        try {
            long startTime = System.currentTimeMillis();
            if ((outFile.length() / 1024 / 1024) >= 1) {
                taskEntity.setTaskResultLength(String.format("%.2f", ((double) outFile.length() / 1024 / 1024)) + "MB");
            } else {
                taskEntity.setTaskResultLength(String.format("%.2f", ((double) outFile.length() / 1024)) + "KB");
            }

            DefaultPutRet defaultPutRet = QiniuUtil.upload(outFile, taskEntity.getTaskId() + "." + DocFileTypeEnum.PDF.getExt());
            System.out.println("上传时间：" + ((System.currentTimeMillis() - startTime) / 1000) + "s");
            return defaultPutRet;
        } catch (Throwable e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
            taskEntity.setErrorCode(DocumentErrorEnum.E10011.getError());
            taskEntity.setErrorMessage(DocumentErrorEnum.E10011.getMsg());
            throw new DocumentException(DocumentErrorEnum.E10011.getError(), DocumentErrorEnum.E10011.getMsg());
        } finally {
            try {
                if (inputFile.exists()) {
                    inputFile.delete();
                }

                if (outFile.exists()) {
                    outFile.delete();
                }
            } catch (Throwable e) {
                e.printStackTrace();
                DocumentLogger.getSysLogger().error(e.getMessage(), e);
            }

        }
    }


    /**
     * 毫秒转换成天时分秒毫秒
     *
     * @param ms
     * @return
     */
    public static String formatTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if (day > 0) {
            sb.append(day + "天");
        }
        if (hour > 0) {
            sb.append(hour + "小时");
        }
        if (minute > 0) {
            sb.append(minute + "分");
        }
        if (second > 0) {
            sb.append(second + "秒");
        }
        if (milliSecond > 0) {
            sb.append(milliSecond + "毫秒");
        }
        return sb.toString();
    }
}
