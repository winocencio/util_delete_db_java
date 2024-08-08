package com.winocencio.util.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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
            preparedStatement.setInt(i + 1, Integer.valueOf(values[i]));
        }
    }
}
