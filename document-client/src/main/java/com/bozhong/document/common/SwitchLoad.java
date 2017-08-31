package com.bozhong.document.common;

import com.bozhong.myswitch.common.SwitchLogger;
import com.bozhong.myswitch.core.SwitchRegister;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by renyueliang on 17/4/12.
 */
public class SwitchLoad {

    private String appId;
    private String zkHosts;
    private Class dynamicClass;

    public void init() throws Throwable {
        if (dynamicClass == null) {
            dynamicClass = WebSettingParam.class;
        }

        SwitchRegister.getSwitchRegister().init(this.appId, dynamicClass, this.zkHosts);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(3000l);
                        List<Field> fields = Arrays.asList(dynamicClass.getDeclaredFields());
                        Collections.sort(fields, new Comparator<Field>() {
                            @Override
                            public int compare(Field o1, Field o2) {
                                return o1.getName().compareTo(o2.getName());
                            }
                        });
                        SwitchLogger.getSysLogger().warn("-------start----------");
                        for (Field field : fields) {
                            //System.out.println(field.getName() + ":" + field.get(dynamicClass));
                            SwitchLogger.getSysLogger().warn(field.getName() + ":" + field.get(dynamicClass));
                        }
                        SwitchLogger.getSysLogger().warn("-------end----------");
                    } catch (Throwable e) {
                        SwitchLogger.getSysLogger().error(e.getMessage(), e);
                    }

                }
            }
        });

        thread.start();
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getZkHosts() {
        return zkHosts;
    }

    public void setZkHosts(String zkHosts) {
        this.zkHosts = zkHosts;
    }

    public Class getDynamicClass() {
        return dynamicClass;
    }

    public void setDynamicClass(Class dynamicClass) {
        this.dynamicClass = dynamicClass;
    }
}
