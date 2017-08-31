package com.bozhong.document.junit;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiezg@317hu.com on 2017/5/15 0015.
 */
public class StaticTest {
    public static final String hello = "Hello";
    static {
        System.out.println("Hello");
    }
    public static void  main(String args[]) {
        List<String> docFileUrlList = new ArrayList<>();
        docFileUrlList.add(new String("http://okxyat5ou.bkt.clouddn.com/JVM%E4%BD%93%E7%B3%BB%E7%BB%93%E6%9E%84%E4%B8%8EGC%E8%B0%83%E4%BC%98.pptx"));
        docFileUrlList.add(new String("http://okxyat5ou.bkt.clouddn.com/%E5%B9%B3%E5%8F%B0-insist-rpc%E6%8A%80%E6%9C%AF%E6%96%B9%E6%A1%88.docx"));
        System.out.println(JSON.toJSONString(docFileUrlList));
        List<String> he = JSON.parseArray("[\"hellowolrd\",\"heelloworldxxxxxxxxxx\"]", String.class);
        for (int i=0; i<10; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(new StaticTest().toString());
                }
            });
            thread.start();
            //new StaticTest();
        }
    }
}
