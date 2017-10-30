package com.codyy.erpsportal.onlinemeetings.models.entities.coco;

/**
 * 申请的coco相关信息
 * host:port/token
 * Created by poe on 17-10-11.
 */

public class CoCoInfo {

    /**
     * port : 66100
     * serverHost : 测试内容5350
     * sslPort : 85766
     * sslServerHost : 测试内容b89q
     * token : 7+OXIkz4BYNkuVNQFRmiqqZXqeyZe/LJJw67NXInSvs=
     */

    private int port;
    private String serverHost;
    private int sslPort;
    private String sslServerHost;
    private String token;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getSslPort() {
        return sslPort;
    }

    public void setSslPort(int sslPort) {
        this.sslPort = sslPort;
    }

    public String getSslServerHost() {
        return sslServerHost;
    }

    public void setSslServerHost(String sslServerHost) {
        this.sslServerHost = sslServerHost;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
