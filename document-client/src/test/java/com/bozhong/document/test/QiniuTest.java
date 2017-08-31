package com.bozhong.document.test;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

import java.io.IOException;

/**
 * Created by xiezg@317hu.com on 2017/4/27 0027.
 */
public class QiniuTest {
    public static void main(String args[]) throws IOException {
        String string = "http://okxyat5ou.bkt.clouddn.com" +
                "/%E5%B9%B3%E5%8F%B0-%E9%85%8D%E7%BD%AE%E4%B8%AD%E5%BF%83-" +
                "%E6%8A%80%E6%9C%AF%E6%96%B9%E6%A1%882016-12-27V1.0.docx";
        HttpClient httpClient = new HttpClient(new HttpClientParams(),
                new SimpleHttpConnectionManager(true));
        HttpMethod httpMethod = new GetMethod(string);
        httpClient.executeMethod(httpMethod);
        Header header = httpMethod.getResponseHeader("Content-Type");
        System.out.println(header.getValue());
    }
}

/**
 *
 * application/pdf pdf格式
 * application/vnd.openxmlformats-officedocument.wordprocessingml.document docx格式
 *
 */
