package com.bozhong.document.task;


import com.alibaba.fastjson.JSON;
import com.bozhong.common.util.CollectionUtil;
import com.bozhong.document.common.DocumentLogger;
import com.bozhong.document.entity.OsEntity;
import com.bozhong.myswitch.common.SwitchUtil;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZKUtil;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xiezg@317hu.com on 2017/5/10 0010.
 */
public class ZkUtil {

    private static final String SEPARATOR = "/";

    private static final String DOC_EXECUTING_ROOT_PATH = SEPARATOR + "docExecutingRootPath";
    private static final String DOC_SERVER_ROOT_PATH = SEPARATOR + "docServerRootPath";
    private static final ZkClient zkClient = new ZkClient(System.getProperty("switch.zkHosts"),
            5000, 5000, new SerializableSerializer());
    private static final Timer timer = new Timer();

    static {
        final OsEntity osEntity = new OsEntity();
        try {
            if (!zkClient.exists(DOC_SERVER_ROOT_PATH)) {
                zkClient.createPersistent(DOC_SERVER_ROOT_PATH);
            }
        } catch (Throwable e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
        }

        try {
            if (!zkClient.exists(DOC_SERVER_ROOT_PATH + SEPARATOR + SwitchUtil.getIp())) {
                zkClient.createEphemeral(DOC_SERVER_ROOT_PATH + SEPARATOR + SwitchUtil.getIp(), osEntity);
                zkClient.subscribeChildChanges(DOC_SERVER_ROOT_PATH, new IZkChildListener() {
                    @Override
                    public void handleChildChange(String s, List<String> list) throws Exception {
                        System.out.println("handleChildChange");
                        try {
                            if (CollectionUtil.isEmpty(list) || !list.contains(SwitchUtil.getIp())) {
                                zkClient.createEphemeral(DOC_SERVER_ROOT_PATH + SEPARATOR + SwitchUtil.getIp(), osEntity);
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                            DocumentLogger.getSysLogger().error(e.getMessage(), e);
                        }

                    }
                });
            }
        } catch (Throwable e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    System.out.println("定时任务");
                    if (!zkClient.exists(DOC_SERVER_ROOT_PATH + SEPARATOR + SwitchUtil.getIp())) {
                        zkClient.createEphemeral(DOC_SERVER_ROOT_PATH + SEPARATOR + SwitchUtil.getIp(), new OsEntity());
                    } else {
                        zkClient.writeData(DOC_SERVER_ROOT_PATH + SEPARATOR + SwitchUtil.getIp(), new OsEntity());
                    }

                    List<String> onlineIpList = zkClient.getChildren(DOC_SERVER_ROOT_PATH);
                    List<String> offlineIpList = zkClient.getChildren(DOC_EXECUTING_ROOT_PATH);
                    if (!CollectionUtils.isEmpty(offlineIpList)) {
                        for (String offlineIp : offlineIpList) {
                            List<String> taskIdList = zkClient.getChildren(DOC_EXECUTING_ROOT_PATH +
                                    SEPARATOR + offlineIp);
                            if (CollectionUtils.isEmpty(taskIdList) && !CollectionUtils.isEmpty(onlineIpList)
                                    && !onlineIpList.contains(offlineIp)) {
                                zkClient.delete(DOC_EXECUTING_ROOT_PATH + SEPARATOR + offlineIp, -1);
                            }
                        }
                    }
                } catch (Throwable e) {
                    DocumentLogger.getSysLogger().error(e.getMessage(), e);
                }
            }

        }, 1000l, 3000l);

        zkClient.subscribeStateChanges(new IZkStateListener() {
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState keeperState) throws Exception {
                System.out.println("handleStateChanged: " + JSON.toJSONString(keeperState));
                if (keeperState.name().equals(Watcher.Event.KeeperState.SyncConnected.name())) {
                    try {
                        if (!zkClient.exists(DOC_SERVER_ROOT_PATH + SEPARATOR + SwitchUtil.getIp())) {
                            zkClient.createEphemeral(DOC_SERVER_ROOT_PATH + SEPARATOR + SwitchUtil.getIp(), osEntity);
                        }
                    } catch (Throwable e) {
                        DocumentLogger.getSysLogger().error(e.getMessage(), e);
                    }

                }
            }

            @Override
            public void handleNewSession() throws Exception {
                System.out.println("handleNewSession");
            }

            @Override
            public void handleSessionEstablishmentError(Throwable throwable) throws Exception {
                System.out.println("handleSessionEstablishmentError");
            }
        });
    }

    /**
     * @return
     */
    public static List<String> getOnlineDocServerList() {
        try {
            return zkClient.getChildren(DOC_SERVER_ROOT_PATH);
        } catch (Throwable e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
        }

        return new ArrayList<>();

    }

    /**
     * @param ip
     * @return
     */
    public static OsEntity getOnlineDocServerInfo(String ip) {
        try {
            return zkClient.readData(DOC_SERVER_ROOT_PATH + SEPARATOR + ip);
        } catch (Throwable e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * zookeeper中创建正在执行任务ID路径
     *
     * @param taskId
     */
    public static void createExecutingTaskIdRecord(String taskId) {
        synchronized (ZKUtil.class) {
            try {
                if (!zkClient.exists(DOC_EXECUTING_ROOT_PATH)) {
                    zkClient.createPersistent(DOC_EXECUTING_ROOT_PATH);
                }
            } catch (Throwable e) {
                DocumentLogger.getSysLogger().error(e.getMessage(), e);
            }

        }

        synchronized (ZKUtil.class) {
            try {
                if (!zkClient.exists(DOC_EXECUTING_ROOT_PATH + SEPARATOR + SwitchUtil.getIp())) {
                    zkClient.createPersistent(DOC_EXECUTING_ROOT_PATH + SEPARATOR + SwitchUtil.getIp());
                }
            } catch (Throwable e) {
                DocumentLogger.getSysLogger().error(e.getMessage(), e);
            }

        }

        try {
            if (!zkClient.exists(DOC_EXECUTING_ROOT_PATH + SEPARATOR + SwitchUtil.getIp() + SEPARATOR + taskId)) {
                zkClient.createPersistent(DOC_EXECUTING_ROOT_PATH + SEPARATOR + SwitchUtil.getIp() + SEPARATOR + taskId);
            }
        } catch (Throwable e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
        }

    }

    /**
     * 删除执行结束的任务ID路径
     *
     * @param taskId
     */
    public static void deleteFinishOrFailureTaskIdRecord(String taskId) {
        try {
            if (zkClient.exists(DOC_EXECUTING_ROOT_PATH + SEPARATOR + SwitchUtil.getIp() + SEPARATOR + taskId)) {
                zkClient.delete(DOC_EXECUTING_ROOT_PATH + SEPARATOR + SwitchUtil.getIp() + SEPARATOR + taskId, -1);
            }
        } catch (Throwable e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
        }
    }

    /**
     * 获取所有未执行任务的任务ID集合
     *
     * @return
     */
    public static List<String> getAllExecutingTaskIdRecords() {
        try {
            return zkClient.getChildren(DOC_EXECUTING_ROOT_PATH + SEPARATOR + SwitchUtil.getIp());
        } catch (Throwable e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
        }

        return new ArrayList<>();
    }

    /**
     * 获取所有的正在执行状态的IP集合
     *
     * @return
     */
    public static List getALLExecutingRecordIPList() {
        try {
            return zkClient.getChildren(DOC_EXECUTING_ROOT_PATH);
        } catch (Throwable e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
        }

        return new ArrayList();
    }

    /**
     * 获取某个ip正在执行状态的任务id集合
     *
     * @param ip
     * @return
     */
    public static List getAllExecutingRecordWithIP(String ip) {
        try {
            return zkClient.getChildren(DOC_EXECUTING_ROOT_PATH + SEPARATOR + ip);
        } catch (Throwable e) {
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
        }

        return new ArrayList();
    }
}
