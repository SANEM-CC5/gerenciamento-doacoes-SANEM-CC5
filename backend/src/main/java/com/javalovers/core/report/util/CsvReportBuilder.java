package com.javalovers.core.report.util;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class CsvReportBuilder {

    private final StringBuilder builder;

    public CsvReportBuilder() {
        this.builder = new StringBuilder();
        // UTF-8 BOM para garantir compatibilidade com o Excel no Windows
        this.builder.append('\uFEFF');
    }

    public CsvReportBuilder addHeader(List<String> headers) {
        return addRow(headers);
    }

    public CsvReportBuilder addRow(List<String> values) {
        for (int i = 0; i < values.size(); i++) {
            builder.append(escape(values.get(i)));
            if (i < values.size() - 1) {
                builder.append(';');
            }
        }
        builder.append("\r\n");
        return this;
    }

    public CsvReportBuilder addRows(List<List<String>> rows) {
        for (List<String> row : rows) {
            addRow(row);
        }
        return this;
    }

    public CsvReportBuilder addEmptyLine() {
        builder.append("\r\n");
        return this;
    }

    public CsvReportBuilder addComment(String text) {
        builder.append("# ").append(text).append("\r\n");
        return this;
    }

    public byte[] build() {
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static String escape(String value) {
        if (value == null) return "";
        String val = value.trim();
        if (val.contains(";") || val.contains("\"") || val.contains("\n") || val.contains("\r")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }
}
