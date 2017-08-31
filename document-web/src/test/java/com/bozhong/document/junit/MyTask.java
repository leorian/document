package com.bozhong.document.junit;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.bozhong.document.pool.OpenOfficeConnectionPool;
import com.bozhong.document.task.Converter;

import java.io.File;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class MyTask implements Runnable {
    private int i;
    private OpenOfficeConnectionPool openOfficeConnectionPool;

    public MyTask(int i, OpenOfficeConnectionPool openOfficeConnectionPool) {
        this.i = i;
        this.openOfficeConnectionPool = openOfficeConnectionPool;
    }

    public void run() {
        String name = Thread.currentThread().getName();
        OpenOfficeConnection connection = null;
        try {
            connection = openOfficeConnectionPool.borrowObject();
            System.out.println(connection.toString());
            Converter.convert(connection, new File("D:\\office\\" + i + ".doc"),
                    new File("D:\\office\\" + i + ".pdf"));
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                openOfficeConnectionPool.returnObject(connection);
            }
        }

        System.out.println(name + " executed OK");
    }
}