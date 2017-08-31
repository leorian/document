package com.bozhong.document.junit;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.bozhong.document.task.Converter;
import org.apache.commons.pool.impl.GenericObjectPool;

import java.io.File;
import java.net.ConnectException;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class WordProcess {

    public static void main(String args[]) {
        GenericObjectPool.Config config = new GenericObjectPool.Config();
        config.maxActive = 4;
        config.maxWait = 30000;
        final ConnectionPoolFactory poolFactory = new ConnectionPoolFactory(config, "127.0.0.1", 8100);

        for (int i = 1; i < 14; i++) {
            final int num = i;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    OpenOfficeConnection connection = null;
                    try {
                        connection = poolFactory.getConnection();
                        System.out.println(connection.toString());
                        Converter.convert(connection, new File("D:\\office\\" + num + ".doc"), new File("D:\\office\\" + num + ".pdf"));
                    } catch (ConnectException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (connection != null) {
                            poolFactory.releaseConnection(connection);
                        }
                    }
                }
            });
            thread.start();

        }
    }




}
