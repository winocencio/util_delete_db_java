package com.winocencio.util.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Util {

    public static String createSqlDelete(String tableName, String[] primaryKeyColumns) {
        String sql = "DELETE FROM " + tableName + " WHERE ";
        for (int i = 0; i < primaryKeyColumns.length; i++) {
            String columnName = primaryKeyColumns[i];
            if (i != 0)
                sql += " AND ";
            sql += columnName + " = ?";
        }
        return sql;
    }

    public static void putValuesInPreparedStatement(PreparedStatement preparedStatement, String[] values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            preparedStatement.setString(i + 1, values[i]);
        }
    }

    public static <T> Stream<List<T>> batches(List<T> source, int length) {
        if (length <= 0)
            throw new IllegalArgumentException("length = " + length);
        int size = source.size();
        if (size == 0)
            return Stream.empty();
        int fullChunks = (size - 1) / length;
        return IntStream.range(0, fullChunks + 1).mapToObj(n -> source.subList(n * length, n == fullChunks ? size : (n + 1) * length));
    }
}
