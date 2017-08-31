package com.bozhong.document.junit;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.bozhong.document.pool.OpenOfficeConnectionPool;
import com.bozhong.document.pool.OpenOfficeConnectionPoolConfig;
import com.bozhong.document.task.Converter;

import java.io.File;
import java.net.ConnectException;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class WordProcess2 {

    public static void main(String args[]) {
        OpenOfficeConnectionPoolConfig openOfficeConnectionPoolConfig = new OpenOfficeConnectionPoolConfig();
        openOfficeConnectionPoolConfig.setMaxTotal(4);
        openOfficeConnectionPoolConfig.setMaxIdle(2);
        openOfficeConnectionPoolConfig.setMinIdle(1);
        final OpenOfficeConnectionPool openOfficeConnectionPool = new OpenOfficeConnectionPool(openOfficeConnectionPoolConfig);

        for (int i = 1; i < 14; i++) {
            final int num = i;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    OpenOfficeConnection connection = null;
                    try {
                        connection = openOfficeConnectionPool.borrowObject();
                        System.out.println(connection.toString());
                        Converter.convert(connection, new File("D:\\office\\" + num + ".doc"),
                                new File("D:\\office\\" + num + ".pdf"));
                    } catch (ConnectException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (connection != null) {
                            openOfficeConnectionPool.returnObject(connection);
                        }
                    }
                }
            });
            thread.start();
        }
    }
}
