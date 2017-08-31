package com.bozhong.document.junit;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

import java.net.InetSocketAddress;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class ConnectionPoolFactory {

    private GenericObjectPool pool;

    public ConnectionPoolFactory(Config config, String ip, int port) {
        ConnectionFactory factory = new ConnectionFactory(ip, port);
        pool = new GenericObjectPool(factory, config);
    }

    public OpenOfficeConnection getConnection() throws Exception {
        return (OpenOfficeConnection) pool.borrowObject();
    }

    public void releaseConnection(OpenOfficeConnection connection) {
        try {
            pool.returnObject(connection);
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    class ConnectionFactory extends BasePoolableObjectFactory {

        private InetSocketAddress address;

        public ConnectionFactory(String ip, int port) {
            address = new InetSocketAddress(ip, port);
        }

        @Override
        public Object makeObject() throws Exception {
            OpenOfficeConnection connection = new SocketOpenOfficeConnection(address.getHostString(), address.getPort());
            connection.connect();
            return connection;
        }

        public void destroyObject(Object obj) throws Exception {
            if (obj instanceof OpenOfficeConnection) {
                ((OpenOfficeConnection) obj).disconnect();
            }
        }

        public boolean validateObject(Object obj) {
            if (obj instanceof OpenOfficeConnection) {
                OpenOfficeConnection connection = ((OpenOfficeConnection) obj);
                if (!connection.isConnected()) {
                    return false;
                }
                return true;
            }
            return false;
        }
    }

}  