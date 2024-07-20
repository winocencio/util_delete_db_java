package com.winocencio.util.v1;

import com.winocencio.util.v1.domain.PropertiesDomain;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

public class DeleteRecordsMain {

    private static int LOG_PER_LINES;
    private static int TOTAL_LINES;
    private static int PROCESSED_LINES = 0;
    private static int ROWS_AFFECTED_COUNT;
    private static long START_TIME;

    public static void main(String[] args) throws IOException, SQLException {
        PropertiesDomain propertiesDomain = getPropertiesDomainByProperties("db.properties");
        START_TIME = System.currentTimeMillis();
        LOG_PER_LINES = propertiesDomain.getLogPerLine();

        try (Connection conn = DriverManager.getConnection(propertiesDomain.getUrl(), propertiesDomain.getUser(), propertiesDomain.getPassword())) {

            setTotalRecords();
            BufferedReader reader = getReader();

            String line = reader.readLine();
            String[] primaryKeyColumns = line.split(",");

            String sqlDelete = createSqlDelete(propertiesDomain.getTable(), primaryKeyColumns);
            PreparedStatement preparedStatement = conn.prepareStatement(sqlDelete);

            while ((line = reader.readLine()) != null) {

                String[] values = line.split(",");
                putValuesInPreparedStatement(preparedStatement, values);
                ROWS_AFFECTED_COUNT += preparedStatement.executeUpdate();
                PROCESSED_LINES++;

                logPercentage();
            }

            System.out.println("Process completed.");
            reader.close();
        }

    }

    private static BufferedReader getReader() throws FileNotFoundException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("data.csv");
        InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
        return new BufferedReader(streamReader);
    }

    private static void logPercentage(){
        if (PROCESSED_LINES % LOG_PER_LINES == 0) {
            int percentage = (PROCESSED_LINES * 100) / TOTAL_LINES;
            System.out.println("Executed " + percentage + "% (" + PROCESSED_LINES + " of the lines, " + ROWS_AFFECTED_COUNT +" rows affected. Time passed: " + ((System.currentTimeMillis() - START_TIME) / 1000) + " seconds");
        }
    }

    private static void putValuesInPreparedStatement(PreparedStatement preparedStatement, String[] values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            preparedStatement.setString(i + 1, values[i]);
        }
    }

    private static String createSqlDelete(String tableName, String[] primaryKeyColumns) {
        String sql = "DELETE FROM " + tableName + " WHERE ";
        for (int i = 0; i < primaryKeyColumns.length; i++) {
            String columnName = primaryKeyColumns[i];
            if (i != 0)
                sql += " AND ";
            sql += columnName + " = ?";
        }
        return sql;
    }

    private static PropertiesDomain getPropertiesDomainByProperties(String propertiesName) throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(propertiesName);
        Properties props = new Properties();
        props.load(is);

        return new PropertiesDomain(props.getProperty("db.url"), props.getProperty("db.user"), props.getProperty("db.password"), props.getProperty("db.table"), Integer.valueOf(props.getProperty("db.log_per_line")));
    }

    private static void setTotalRecords() throws FileNotFoundException {
        int totalRecords = 0;
        int blockSize = 1000; // Processar 1000 linhas por bloco

        try (Scanner scanner = new Scanner(getReader())) {
            while (scanner.hasNextLine()) {
                for (int i = 0; i < blockSize; i++) {
                    if (!scanner.hasNextLine()) {
                        break;
                    }
                    scanner.nextLine();
                    totalRecords++;
                }
            }
        }

        TOTAL_LINES = totalRecords -1;
    }

}
