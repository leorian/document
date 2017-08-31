package com.bozhong.document.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class OpenOfficeConnectionPoolConfig extends GenericObjectPoolConfig {
    public OpenOfficeConnectionPoolConfig() {
        setMinIdle(5);
        setMaxIdle(20);
        setMaxTotal(100);
    }
}
