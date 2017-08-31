package com.bozhong.document.pool;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.bozhong.document.common.DocumentErrorEnum;
import com.bozhong.document.common.DocumentLogger;
import com.bozhong.document.model.HostIpPort;
import com.bozhong.document.util.DocumentException;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class SocketOpenOfficeConnectionFactory extends OpenOfficeConnectionFactory {

    private List<HostIpPort> hostIpPorts;

    private String hostIpPortsString;

    private int size = 0;

    private int errorCount = 0;

    public SocketOpenOfficeConnectionFactory() {
        hostIpPorts = Arrays.asList(new HostIpPort());
    }

    public SocketOpenOfficeConnectionFactory(List<HostIpPort> hostIpPorts) {
        this.hostIpPorts = hostIpPorts;
    }

    public void setHostIpPorts(List<HostIpPort> hostIpPorts) {
        this.hostIpPorts = hostIpPorts;
    }

    public void setHostIpPortsString(String hostIpPortsString) {
        this.hostIpPortsString = hostIpPortsString;
        try {
            String[] hostIpPortArray = this.hostIpPortsString.split(",");
            List<HostIpPort> list = new ArrayList<>();
            for (String hostIpPort : hostIpPortArray) {
                String[] ipPort = hostIpPort.split(":");
                list.add(new HostIpPort(ipPort[0], Integer.valueOf(ipPort[1]).intValue()));
            }

            this.hostIpPorts = list;
        } catch (Throwable e) {
            DocumentLogger.getSysLogger().error(e.getMessage());
        }
    }

    @Override
    public OpenOfficeConnection createOpenOfficeConnection() throws Exception {
        if (CollectionUtils.isEmpty(hostIpPorts)) {
            //参数未定义
            throw new DocumentException(DocumentErrorEnum.E10001.getError(),
                    DocumentErrorEnum.E10001.getMsg());
        }

        HostIpPort hostIpPort = hostIpPorts.get(size);

        if (++size >= hostIpPorts.size()) {
            size = 0;
        }

        OpenOfficeConnection openOfficeConnection;

        try {
            openOfficeConnection = new SocketOpenOfficeConnection(hostIpPort.getHostIp(),
                    hostIpPort.getPort());
            openOfficeConnection.connect();
            errorCount = 0;
        } catch (Throwable e) {
            if (errorCount == hostIpPorts.size()) {
                System.exit(-1);
            }

            e.printStackTrace();
            errorCount++;
            openOfficeConnection = createOpenOfficeConnection();
        }

        if (openOfficeConnection == null) {
            //文件转换服务异常
            throw new DocumentException(DocumentErrorEnum.E10017.getError(),
                    DocumentErrorEnum.E10017.getMsg());
        }

        return openOfficeConnection;
    }


    @Override
    public PooledObject<OpenOfficeConnection> wrapOpenOfficeConnection(OpenOfficeConnection openOfficeConnection) {
        return new DefaultPooledObject<>(openOfficeConnection);
    }
}
