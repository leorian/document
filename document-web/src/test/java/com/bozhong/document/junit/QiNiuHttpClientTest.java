package com.bozhong.document.junit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

import java.io.IOException;

/**
 * Created by xiezg@317hu.com on 2017/5/5 0005.
 */
public class QiNiuHttpClientTest {
    public static void main(String args[]) {

        for (int j = 0; j < 20; j++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 1000; i++) {
                        HttpClient httpClient = new HttpClient(new HttpClientParams(),
                                new SimpleHttpConnectionManager(true));
                        String url = "http://okxyat5ou.bkt.clouddn.com/1.ppt";
                        HttpMethod httpMethod = new GetMethod(url);
                        try {
                            httpClient.executeMethod(httpMethod);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println(httpMethod.getStatusCode());
                        System.out.println(httpMethod.getResponseHeader("Content-Type").getValue());
                        httpMethod.releaseConnection();
                    }
                }
            });
            thread.start();

        }
    }
}
