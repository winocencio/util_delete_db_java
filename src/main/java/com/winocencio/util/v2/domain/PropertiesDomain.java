package com.winocencio.util.v2.domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesDomain {


    private static String url;
    private static String user;
    private static String password;
    private static String table;
    private static int logPerLine;

    public static void set(String urlOut, String userOut, String passwordOut, String tableOut, int logPerLineOut) {
        url = urlOut;
        user = userOut;
        password = passwordOut;
        table = tableOut;
        logPerLine = logPerLineOut;
    }

    public static String getUrl() {
        return url;
    }

    public static String getUser() {
        return user;
    }

    public static String getPassword() {
        return password;
    }

    public static String getTable() {
        return table;
    }

    public static int getLogPerLine() {
        return logPerLine;
    }

    public static void setPropertiesDomainByProperties(String propertiesName) throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(propertiesName);
        Properties props = new Properties();
        props.load(is);

        PropertiesDomain.set(props.getProperty("db.url"), props.getProperty("db.user"), props.getProperty("db.password"), props.getProperty("db.table"), Integer.valueOf(props.getProperty("db.log_per_line")));
    }
}
