package com.bozhong.document.pool;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public abstract class OpenOfficeConnectionFactory
        extends BasePooledObjectFactory<OpenOfficeConnection> {

    public abstract OpenOfficeConnection createOpenOfficeConnection() throws Exception;

    public abstract PooledObject<OpenOfficeConnection> wrapOpenOfficeConnection(OpenOfficeConnection var1);

    @Override
    public OpenOfficeConnection create() throws Exception {
        return createOpenOfficeConnection();
    }

    @Override
    public PooledObject<OpenOfficeConnection> wrap(OpenOfficeConnection o) {
        return wrapOpenOfficeConnection(o);
    }
}
