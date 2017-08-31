package com.bozhong.document.pool;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.bozhong.document.model.HostIpPort;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.List;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class OpenOfficeConnectionPool extends GenericObjectPool<OpenOfficeConnection> {

    public OpenOfficeConnectionPool() {
        super(new SocketOpenOfficeConnectionFactory(), new OpenOfficeConnectionPoolConfig());
    }

    public OpenOfficeConnectionPool(OpenOfficeConnectionFactory factory) {
        super(factory, new OpenOfficeConnectionPoolConfig());
    }

    public OpenOfficeConnectionPool(OpenOfficeConnectionFactory factory, OpenOfficeConnectionPoolConfig config) {
        super(factory, config);
    }

    public OpenOfficeConnectionPool(OpenOfficeConnectionPoolConfig config) {
        super(new SocketOpenOfficeConnectionFactory(), config);
    }

    public OpenOfficeConnectionPool(List<HostIpPort> hostIpPortList) {
        super(new SocketOpenOfficeConnectionFactory(hostIpPortList), new OpenOfficeConnectionPoolConfig());
    }

    public void init() {
        OpenOfficeConnection openOfficeConnection = null;
        try {
            openOfficeConnection = this.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (openOfficeConnection != null) {
                this.returnObject(openOfficeConnection);
            }
        }
    }
}
