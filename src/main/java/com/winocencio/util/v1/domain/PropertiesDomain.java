package com.winocencio.util.v1.domain;

public class PropertiesDomain {

    private String url;
    private String user;
    private String password;
    private String table;
    private int logPerLine;

    public PropertiesDomain(String url, String user, String password, String table,int logPerLine) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.table = table;
        this.logPerLine = logPerLine;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getTable() {
        return table;
    }

    public int getLogPerLine() {
        return logPerLine;
    }
}
