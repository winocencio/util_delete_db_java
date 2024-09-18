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

    private static int executionSpliter;

    private static int batchSize;

    public static void set(String urlOut, String userOut, String passwordOut, String tableOut, int logPerLineOut,int executionSpliterOut,int batchSizeOut) {
        url = urlOut;
        user = userOut;
        password = passwordOut;
        table = tableOut;
        logPerLine = logPerLineOut;
        executionSpliter = executionSpliterOut;
        batchSize = batchSizeOut;
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
    public static int getExecutionSpliter() {
        return executionSpliter;
    }

    public static int getBatchSize() {
        return batchSize;
    }

    public static void setPropertiesDomainByProperties(String propertiesName) throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(propertiesName);
        Properties props = new Properties();
        props.load(is);

        PropertiesDomain.set(
            props.getProperty("db.url"),
            props.getProperty("db.user"),
            props.getProperty("db.password"),
            props.getProperty("db.table"),
            Integer.valueOf(props.getProperty("db.log_per_line")),
            Integer.valueOf(props.getProperty("db.v2.execution_spliter")),
            Integer.valueOf(props.getProperty("db.v2.batch_size")));

    }
}
