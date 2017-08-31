package com.bozhong.document.common;

import org.apache.log4j.Logger;


/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class DocumentLogger {

    public static Logger getLogger() {
        return Logger.getRootLogger();
    }

    public static Logger getSysLogger() {
        return Logger.getLogger("document");
    }
}
