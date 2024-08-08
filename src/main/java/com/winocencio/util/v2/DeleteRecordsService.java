package com.winocencio.util.v2;

import com.winocencio.util.core.Util;
import com.winocencio.util.v2.domain.PropertiesDomain;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeleteRecordsService {

    public void executeDeleteInTable(String table,String[] primaryKeyColumns ,List<String> dataLines) throws SQLException {

        try (Connection conn = DriverManager.getConnection(PropertiesDomain.getUrl(), PropertiesDomain.getUser(), PropertiesDomain.getPassword())) {
            String sqlDelete = Util.createSqlDelete(PropertiesDomain.getTable(), primaryKeyColumns);

            ExecutorService executorService = Executors.newFixedThreadPool(5);

            List<Callable<Void>> callableList = new ArrayList<>();
            for (String dataLine : dataLines) {
                callableList.add(executeDeleteInDataLine(conn, sqlDelete, dataLine));
            }

            executorService.invokeAll(callableList);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private Callable<Void> executeDeleteInDataLine(Connection conn, String sqlDelete, String dataLine){
        return () -> {
            String[] values = dataLine.split(",");

            PreparedStatement preparedStatement = conn.prepareStatement(sqlDelete);
            Util.putValuesInPreparedStatement(preparedStatement, values);
            int i = preparedStatement.executeUpdate();
            System.out.println(i + "lines deleted");
            return null;
        };
    }
}
