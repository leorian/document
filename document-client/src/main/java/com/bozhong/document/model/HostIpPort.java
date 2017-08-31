package com.bozhong.document.model;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class HostIpPort implements Serializable {
    private static final String DEFAULT_HOST_IP = "localhost";
    private static final int DEFAULT_PORT = 8100;
    private String hostIp;
    private int port;

    public HostIpPort() {
        this(DEFAULT_HOST_IP, DEFAULT_PORT);
    }

    public HostIpPort(String hostIp) {
        this(hostIp, DEFAULT_PORT);
    }

    public HostIpPort(int port) {
        this(DEFAULT_HOST_IP, port);
    }

    public HostIpPort(String hostIp, int port) {
        this.hostIp = hostIp;
        this.port = port;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
