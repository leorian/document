package com.bozhong.document.junit;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.DocumentFormatRegistry;
import com.bozhong.document.pool.OpenOfficeConnectionPool;
import com.bozhong.document.task.Converter;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by xiezg@317hu.com on 2017/4/27 0027.
 */
public class DocxDemo {
    public static void main(String args[]) {
        String string = "http://okxyat5ou.bkt.clouddn.com/%E5%B9%B3%E5%8F%B0-%E9%85%8D%E7%BD%AE%E4%B8%AD%E5%BF%83-%E6%8A%80%E6%9C%AF%E6%96%B9%E6%A1%882016-12-27V1.0.docx";
        HttpClient client = new HttpClient(new HttpClientParams(),
                new SimpleHttpConnectionManager(true));
        HttpMethod method = new GetMethod(string);
        OpenOfficeConnectionPool openOfficeConnectionPool = new OpenOfficeConnectionPool();
        DocumentFormatRegistry factory = new DefaultDocumentFormatRegistry();
        DocumentFormat inputDocumentFormat = factory
                .getFormatByFileExtension(Converter.getExtensionName("docx"));
        DocumentFormat outputDocumentFormat = factory
                .getFormatByFileExtension(Converter.getExtensionName("pdf"));
        try {
            client.executeMethod(method);
            InputStream inputStream = method.getResponseBodyAsStream();
            File file = new File("D:\\office\\hello.pdf");
            Converter.convert(openOfficeConnectionPool.borrowObject(), inputStream, inputDocumentFormat, new FileOutputStream(file), outputDocumentFormat);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            method.releaseConnection();
            System.out.println("执行结束");
        }
    }
}
