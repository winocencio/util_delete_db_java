package com.winocencio.util.v2;

import com.winocencio.util.v2.domain.PropertiesDomain;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.winocencio.util.v2.domain.PropertiesDomain.setPropertiesDomainByProperties;

public class DeleteRecordsMain {

    private static final long START_TIME = System.currentTimeMillis();

    public static void main(String[] args) throws IOException, SQLException {
        setPropertiesDomainByProperties("db.properties");
        //LOG_PER_LINES = propertiesDomain.getLogPerLine();

        List<String> dataLines = getDataLinesFromCsv("data.csv");

        if(dataLines.isEmpty() || dataLines.size() < 2)
            return;

        String firstLine = dataLines.get(0);
        String[] primaryKeyColumns = firstLine.split(",");
        dataLines.remove(0);

        List<String> tables = List.of(PropertiesDomain.getTable().split(","));

        for (String table : tables) {
            new DeleteRecordsService().executeDeleteInTable(table,primaryKeyColumns,dataLines);
        }
    }



    private static List<String> getDataLinesFromCsv(String csvName) throws IOException {
        BufferedReader bufferedReader = getReader(csvName);
        List<String> dataLines = new ArrayList<>();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            dataLines.add(line);
        }

        return dataLines;
    }


    private static BufferedReader getReader(String csvName) throws FileNotFoundException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("data.csv");
        InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
        return new BufferedReader(streamReader);
    }
}
