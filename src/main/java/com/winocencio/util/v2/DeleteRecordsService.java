package com.winocencio.util.v2;

import com.winocencio.util.core.Util;
import com.winocencio.util.v2.domain.PropertiesDomain;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.winocencio.util.v2.DeleteRecordsMain.START_TIME;

public class DeleteRecordsService {

    private static final AtomicInteger PROCESSED_LINES_GENERAL = new AtomicInteger(0);
    private static final AtomicInteger PROCESSED_LINES_BY_TABLE = new AtomicInteger(0);

    private static Integer TOTAL_GENERAL;
    private static Integer TOTAL_BY_TABLE;

    public void executeDeleteInAllTables(List<String>  tables,String[] primaryKeyColumns ,List<String> dataLines) throws InterruptedException {
        TOTAL_BY_TABLE = dataLines.size();
        TOTAL_GENERAL = TOTAL_BY_TABLE * tables.size();

        List<List<String>> listOfDataLines = Util.batches(dataLines, (int) Math.ceil((float)dataLines.size() / (float)PropertiesDomain.getExecutionSpliter() )).toList();

        ExecutorService executorService = Executors.newFixedThreadPool(tables.size() * listOfDataLines.size());
        List<Callable<Void>> callableList = new ArrayList<>();
        for (String table : tables) {
            for (List<String> dataLinesSplit : listOfDataLines) {
                callableList.add(this.executeDeleteInTable(null,table,primaryKeyColumns,dataLinesSplit));
            }
        }

        executorService.invokeAll(callableList);
        awaitTerminationAfterShutdown(executorService);
    }

    public void awaitTerminationAfterShutdown(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public Callable<Void> executeDeleteInTable(Connection conn2, String table, String[] primaryKeyColumns , List<String> dataLines) {
        return () -> {
            try (Connection conn = DriverManager.getConnection(PropertiesDomain.getUrl(), PropertiesDomain.getUser(), PropertiesDomain.getPassword())) {
                String sqlDelete = Util.createSqlDelete(table, primaryKeyColumns);

                executeDeleteInDataLineBatch(conn, sqlDelete, dataLines);
                /*for (String dataLine : dataLines) {
                    executeDeleteInDataLine(conn, sqlDelete, dataLine);
                }*/
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            return null;
        };
    }

    private void executeDeleteInDataLineBatch(Connection conn, String sqlDelete, List<String> dataLineList) throws SQLException {

        PreparedStatement preparedStatement = conn.prepareStatement(sqlDelete);
        List<List<String>> dataLinebatch = Util.batches(dataLineList, PropertiesDomain.getBatchSize()).toList();
        int processedLinesByTable = 0;

        for (List<String> dataLineListMinor : dataLinebatch) {
            for (String dataLine : dataLineListMinor) {
                String[] values = dataLine.split(",");
                Util.putValuesInPreparedStatement(preparedStatement, values);
                preparedStatement.addBatch();
                int processedLinesGeneral = PROCESSED_LINES_GENERAL.incrementAndGet();
                processedLinesByTable = PROCESSED_LINES_BY_TABLE.incrementAndGet();
            }
            int[] rowsAffected = preparedStatement.executeBatch();
            logPercentage(processedLinesByTable,TOTAL_GENERAL,Arrays.stream(rowsAffected).boxed().mapToInt(value -> value).sum());
        }
    }

    private void executeDeleteInDataLine(Connection conn, String sqlDelete, String dataLine) throws SQLException {

            String[] values = dataLine.split(",");

            PreparedStatement preparedStatement = conn.prepareStatement(sqlDelete);
            Util.putValuesInPreparedStatement(preparedStatement, values);
            int rowsAffected = preparedStatement.executeUpdate();

            int processedLinesGeneral = PROCESSED_LINES_GENERAL.incrementAndGet();
            int processedLinesByTable = PROCESSED_LINES_BY_TABLE.incrementAndGet();

            logPercentage(processedLinesByTable,TOTAL_GENERAL,rowsAffected);
    }

    private static void logPercentage(Integer processedLinesByTable, Integer totalLines, Integer rowsAffectedCount){
        if (processedLinesByTable % PropertiesDomain.getLogPerLine() == 0) {
            int percentage = (processedLinesByTable * 100) / totalLines;
            System.out.println("Executed " + percentage + "% (" + processedLinesByTable + " of the lines, " + rowsAffectedCount +" rows affected. Time passed: " + ((System.currentTimeMillis() - START_TIME) / 1000) + " seconds - " + Thread.currentThread().getName());
        }
    }
}
